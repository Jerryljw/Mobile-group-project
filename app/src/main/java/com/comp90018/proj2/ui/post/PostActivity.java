package com.comp90018.proj2.ui.post;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.comp90018.proj2.MainActivity;
import com.comp90018.proj2.R;
import com.comp90018.proj2.utils.GlideApp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Activity for Post
 */
public class PostActivity extends AppCompatActivity {

    // UI components
    PostItem postItem;
    ImageView imgPost,imgUserPost, currentUserHeadIcon;
    GeoPoint postLocation;
    TextView txtPostDesc, txtPostUsername,txtPostTitle,txtPostSpecie;
    EditText editTextComment;
    Button btnAddComment, locatePostButton;
    String PostKey;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<CommentItem> listComment;

    // Constants
    private static final String PREFIX = "gs://mobiletest-e36f3.appspot.com/";
    static String COMMENT_KEY = "Comments" ;
    static String POST_KEY = "Post_Temp";
    static String COMMENT_CONTENT = "CommentContent";
    static String COMMENT_TIME = "CommentTime";
    static String COMMENT_USERHEADICON = "CommentUserHeadicon";
    static String COMMENT_USERID = "CommentUserId";
    static String COMMENT_USERNAME = "CommentUsername";
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Initialize values
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        // Initialize firebase instances
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        // Initialize views
        imgPost =findViewById(R.id.post_img);
        imgUserPost = findViewById(R.id.post_userhead);

        currentUserHeadIcon = findViewById(R.id.post_currentuser_img);

        txtPostTitle = findViewById(R.id.post_title_view);
        txtPostDesc = findViewById(R.id.post_textview);
        txtPostUsername = findViewById(R.id.post_username);
        txtPostSpecie = findViewById(R.id.speciesTextview);

        editTextComment = findViewById(R.id.comment_edit_multiline_text);
        btnAddComment =findViewById(R.id.comment_button);
        locatePostButton = findViewById(R.id.locate_post_button);

        // post id transfer
        postItem = new PostItem();

        PostKey = bundle.getString("postId"); //bundle.get;
        Log.e("PostKey!",PostKey);


        // Add click listener for adding new comment
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextComment.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please input comment", Toast.LENGTH_SHORT).show();
                } else {
                    btnAddComment.setVisibility(View.INVISIBLE);
//                DatabaseReference commentReference = firebaseFirestore.collection(POST_KEY).document(PostKey).collection(COMMENT_KEY);

                    // Get the comment content
                    String comment_content = editTextComment.getText().toString();

                    // Get the user information
                    String uid = firebaseUser.getUid();
                    String uname;
                    if ("".equals(firebaseUser.getDisplayName())) {
                        if(firebaseUser.getUid().length()>6){
                            uname = "user-" + firebaseUser.getUid().substring(0,5)+"...";
                        }else {
                            uname = "user-unknown";
                        }
                    } else {
                        uname = firebaseUser.getDisplayName();
                    }

                    // Create the comment document dto
                    String uImg = Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString(); //firebaseUser.getPhotoUrl().toString();

                    CommentItem comment = new CommentItem(comment_content, uid, uImg, uname);
                    Map<String, Object> data = new HashMap<>();
                    data.put("CommentContent", comment.getContent());
                    data.put("CommentTime", comment.getTimestamp());
                    data.put("CommentUserHeadicon", comment.getUimg());
                    data.put("CommentUserId", comment.getUid());
                    data.put("CommentUsername", comment.getUname());

//                    Log.d("TAG", "onClick: add data" + comment.getUid());

                    // Store the comment to firebase
                    firebaseFirestore.collection(POST_KEY).document(PostKey).collection(COMMENT_KEY)
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
//                                    Log.d("TAG", "onSuccess: onClick: add data ");
                                    Toast.makeText(getApplicationContext(), "comment added", Toast.LENGTH_SHORT).show();
                                    editTextComment.setText("");
                                    btnAddComment.setVisibility(View.VISIBLE);
                                    hideKeyboard(view);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
//                                    Log.d("TAG", "onfil: onClick: add data ");
                                    Toast.makeText(getApplicationContext(), "comment added failure", Toast.LENGTH_SHORT).show();

                                }
                            });
                    iniRvComment();
                }
            }
        });


        // TODO: 2021/10/29 : add locate button listener
        // Add click listener for locating post on the map
        locatePostButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent1 = new Intent(PostActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("fromLocationToMap", 1);
//                        Log.d("TAGaaaaa", "onClick: " + postLocation);
                        bundle.putDouble("latitude", postLocation.getLatitude());
                        bundle.putDouble("longitude", postLocation.getLongitude());
                        intent1.putExtras(bundle);
                        startActivity(intent1);

                    }
                }
        );

        //bind data to all views
        DocumentReference postReference = firebaseFirestore.collection(POST_KEY).document(PostKey);
        postReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Map<String, Object> dataMap = documentSnapshot.getData();
                    //load title
                    txtPostTitle.setText((String) dataMap.get("PostTitle"));
                    //load image
//                    Log.d("TAG", "onSuccess: " + dataMap.get("PostImage"));
                    StorageReference gsReference = firebaseStorage
                            .getReferenceFromUrl(PREFIX+(String)  dataMap.get("PostImage"));


                    postLocation = documentSnapshot.getGeoPoint("PostLocation");

                    //load description
                    txtPostDesc.setText((String) dataMap.get("PostMessage"));

                     if((dataMap.get("UserDisplayName").equals(""))){
                        txtPostUsername.setText("User-"+dataMap.get("UserId"));
                    }
                    else{
                        txtPostUsername.setText((String)dataMap.get("UserDisplayName"));
                    }
                    //loading species by ai model
                    String post_species = "";
                    if(dataMap.containsKey("PostSpecies")) {
                        post_species = (String) dataMap.get("PostSpecies");
                    }

                    if(post_species.equals("")){
                        txtPostSpecie.setText("");
                    }
                    else {
                        txtPostSpecie.setText("Possible species:"+post_species);
                    }


                    GlideApp.with(getApplication())
                            .load(String.valueOf(firebaseUser.getPhotoUrl()))
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_card_portrait)
                                    .fitCenter())
                            .into(currentUserHeadIcon);

                    GlideApp.with(getApplication())
                            .load(gsReference)
                            .centerCrop()
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_card_image)
                                    .fitCenter())
                            .into(imgPost);

                    gsReference = firebaseStorage
                            .getReferenceFromUrl((String)  dataMap.get("UserPhotoUri"));
                    GlideApp.with(getApplication())
                            .load(gsReference)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_card_portrait)
                                    .fitCenter())
                            .into(imgUserPost);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });


        RvComment =  findViewById(R.id.comments_recycle_view);
        RvComment.setLayoutManager(new LinearLayoutManager(this));
        // initial Recycle view comments
        iniRvComment();

    }

    /**
     * Method for initializing existing comments of the post
     */
    private void iniRvComment() {

//        Log.d("TAG", "onSuccess11: " + "hellsoosos");
        Task<QuerySnapshot> commentRef = firebaseFirestore.collection(POST_KEY).document(PostKey).collection(COMMENT_KEY).get();
        commentRef.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot documentSnapshots) {
                listComment = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot:documentSnapshots) {
//                    Map<String,Object> commentSnapshot = documentSnapshot.getData();
                    String comment_content = documentSnapshot.getString(COMMENT_CONTENT);
                    String comment_userid = documentSnapshot.getString(COMMENT_USERID);
                    Timestamp comment_timestamp = documentSnapshot.getTimestamp(COMMENT_TIME);
                    String comment_headicon = documentSnapshot.getString(COMMENT_USERHEADICON);
                    String comment_username = documentSnapshot.getString(COMMENT_USERNAME);
//                    Log.d("TAG", "onSuccess11: "+ comment_content);
                    CommentItem comment = new CommentItem(comment_content,comment_userid,comment_headicon,comment_username,comment_timestamp);
                    listComment.add(comment);
                }
                if(listComment.toArray().length>0){
                    firebaseFirestore.collection(POST_KEY).document(PostKey)
                            .update("PostFlag",1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Modify PostFlag", "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Modify PostFlag", "Error updating document", e);
                                }
                            });
                }
                else {
                    firebaseFirestore.collection(POST_KEY).document(PostKey)
                            .update("PostFlag",0)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Modify PostFlag", "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Modify PostFlag", "Error updating document", e);
                                }
                            });
                }
//                Log.d("TAG", "onSuccess11: " + listComment);
                Collections.sort(listComment,new CommentsItemComparator());
                commentAdapter = new CommentAdapter(getApplicationContext(),listComment);
                RvComment.setAdapter(commentAdapter);
            }

        });


    }
    class CommentsItemComparator implements Comparator<CommentItem>{
        public int compare(CommentItem c1, CommentItem c2){
            if (c1.getTimestamp().compareTo(c2.getTimestamp())==0)
                return 0;
            else if (c1.getTimestamp().compareTo(c2.getTimestamp()) < 0)
                return 1;
            else
                return -1;
        }
    }


    public void back_onclick(View view)
    {
        Intent intent=new Intent();
        intent.setClass(PostActivity.this, MainActivity.class);
        PostActivity.this.startActivity(intent);
    }

    public void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager!=null){
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }


}