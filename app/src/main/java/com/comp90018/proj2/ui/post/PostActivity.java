package com.comp90018.proj2.ui.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.comp90018.proj2.R;
import com.comp90018.proj2.MainActivity;
import com.comp90018.proj2.ui.finder.CardItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {
    PostItem postItem;
    ImageView imgPost,imgUserPost,imgCurrentUser;
    TextView txtPostDesc,txtPostDateName,txtPostTitle;
    EditText editTextComment;
    Button btnAddComment;
    String PostKey;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<CommentItem> listComment;
    static String COMMENT_KEY = "Comments" ;
    static String POST_KEY = "Post";
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
        //initial values
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int postId = bundle.getInt("postId");
        this.firebaseFirestore=FirebaseFirestore.getInstance();
        //initial views

        RvComment =  findViewById(R.id.comments_recycle_view);

        RvComment.setLayoutManager(new LinearLayoutManager(this));
        // post id传递
        Log.d("post", "posteeeee"+String.valueOf(postId));

        postItem = new PostItem();
        PostKey = "cYmutQb9s00wcn1dd6AS";






        // initial Recycle view comments
        iniRvComment();
    }

    private void iniRvComment() {


        Log.d("TAG", "onSuccess11: " + "hellsoosos");
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
                    Log.d("TAG", "onSuccess11: "+ comment_content);
                    CommentItem comment = new CommentItem(comment_content,comment_userid,comment_headicon,comment_username,comment_timestamp);
                    listComment.add(comment);
                }
                Log.d("TAG", "onSuccess11: " + listComment);
                commentAdapter = new CommentAdapter(getApplicationContext(),listComment);
                RvComment.setAdapter(commentAdapter);


            }

        });


    }

    public void back_onclick(View view)
    {
        Intent intent=new Intent();
        intent.setClass(PostActivity.this, MainActivity.class);
        PostActivity.this.startActivity(intent);
    }
}