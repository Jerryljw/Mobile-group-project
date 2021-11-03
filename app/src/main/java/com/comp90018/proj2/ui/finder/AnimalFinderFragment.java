package com.comp90018.proj2.ui.finder;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.proj2.R;
import com.comp90018.proj2.data.model.CardItem;
import com.comp90018.proj2.ui.post.PostActivity;
import com.comp90018.proj2.utils.GeoPointUtils;
import com.comp90018.proj2.utils.GlideApp;
import com.comp90018.proj2.utils.LocationCommunication;
import com.comp90018.proj2.utils.PostLocSort;
import com.comp90018.proj2.utils.PostTimeSort;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class AnimalFinderFragment extends Fragment {

    private static final String PREFIX = "gs://mobiletest-e36f3.appspot.com/";

    // store items
    private ArrayList<CardItem> cardItemArrayList = new ArrayList<>();
    private View view;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;

    // Upper Tab
    private Spinner spinner;
    private String sp_item;

    private static final String TAG = "Extract";

    // read data from firebase
    private FirebaseFirestore firestore_db = FirebaseFirestore.getInstance();

    private CollectionReference firestore_reference = firestore_db.
            collection("Post_Temp");

    FirebaseStorage storage = FirebaseStorage.getInstance();

    private GeoPoint current;

    // get the user current location
    LocationCommunication mLocationCallback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mLocationCallback = (LocationCommunication) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DataCommunication");
        }
        current = mLocationCallback.getLocation();
        // Log.e("Animal Current onAttach", String.valueOf(current));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_finder_animal,container,false);

        // load layout and show the data from firebase
        loadCardItemFromFirebase();

        // sorting list button
        spinner = (Spinner) view.findViewById(R.id.animal_sp);
        sp_item = (String) spinner.getSelectedItem();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                sp_item = (String) spinner.getSelectedItem();
                if (sp_item.equalsIgnoreCase("Latest Post Time")) {
                    PostTimeSort postTimeSort = new PostTimeSort();
                    Collections.sort(cardItemArrayList, postTimeSort);
                } else {
                    Log.e("Animal","Nearest");
                    PostLocSort postLocSort = new PostLocSort(current);
                    Collections.sort(cardItemArrayList, postLocSort);
                }
                initRecycleView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void initRecycleView(){
        recyclerView=(RecyclerView) view.findViewById(R.id.home_item);
        homeAdapter = new HomeAdapter(getActivity(),cardItemArrayList,current);

        // set format of view, 2 cols
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(homeAdapter);

        // create click listener on adapter for passing postid to PostActivity
        homeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, CardItem cardItem) {

                // jump to post activity
                Intent intent = new Intent(getActivity(), PostActivity.class);
                Bundle bundle = new Bundle();
                Log.e(TAG, String.valueOf(cardItem.getPostId()));
                bundle.putString("postId",cardItem.getPostId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    /**
     * Load Data from firebase, go through all documents in the collections
     * @return a list contains all the data
     */
    private ArrayList<CardItem> loadCardItemFromFirebase() {
        firestore_reference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                firestore_reference.document(document.getId())
                                        .get()
                                        .addOnSuccessListener(
                                                new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists()) {

                                                            // get data
                                                            String postSpecies = documentSnapshot
                                                                    .getString("PostSpecies");
                                                            Timestamp postTime = documentSnapshot
                                                                    .getTimestamp("PostTime");
                                                            String postType = documentSnapshot
                                                                    .getString("PostType");
                                                            String postUserName = documentSnapshot
                                                                    .getString("UserDisplayName");
                                                            String postUserHeadIcon = documentSnapshot
                                                                    .getString("UserPhotoUri");
                                                            int postFlag = Math.toIntExact(documentSnapshot
                                                                    .getLong("PostFlag"));
                                                            GeoPoint postGeoPoint = documentSnapshot
                                                                    .getGeoPoint("PostLocation");
                                                            String postImg = documentSnapshot
                                                                    .getString("PostImage");
                                                            String postTitle = documentSnapshot
                                                                    .getString("PostTitle");
                                                            String postId = document.getId();

                                                            // set data
                                                            CardItem cardItem = new CardItem();
                                                            cardItem.setImg(storage.getReferenceFromUrl(PREFIX+postImg));
                                                            cardItem.setHeadIcon(storage.getReferenceFromUrl(postUserHeadIcon));                                                            cardItem.setTitles(postTitle);
                                                            cardItem.setUsernames(postUserName);
                                                            cardItem.setPoint(postGeoPoint);
                                                            cardItem.setPostId(postId);
                                                            cardItem.setPostTime(postTime);
                                                            cardItem.setPostFlag(postFlag);
                                                            cardItem.setPostSpecies(postSpecies);
                                                            cardItem.setPostType(postType);
                                                            if(postType.equalsIgnoreCase("Animal")){
                                                            cardItemArrayList.add(cardItem);}
                                                            homeAdapter = new HomeAdapter(getActivity(),
                                                                    cardItemArrayList,current);
                                                            recyclerView.setAdapter(homeAdapter);

                                                            // create click listener on adapter
                                                            homeAdapter.setOnItemClickListener(
                                                                    new HomeAdapter.OnItemClickListener() {
                                                                        @Override
                                                                        public void OnItemClick(View view,
                                                                                                CardItem cardItem) {

                                                                            // jump to post activity
                                                                            // TODO: need to pass carditem to PostActivity
                                                                            Intent intent = new Intent(getActivity(), PostActivity.class);
                                                                            Bundle bundle = new Bundle();
                                                                            Log.e("click", String.valueOf(cardItem.getPostId()));
                                                                            bundle.putString("postId",cardItem.getPostId());
                                                                            intent.putExtras(bundle);
                                                                            startActivity(intent);
                                                                        }
                                                                    });

                                                        } else {
                                                            Toast.makeText(getActivity(),
                                                                    "Document does not exist",
                                                                    Toast.LENGTH_SHORT)
                                                                    .show();
                                                        }
                                                    }
                                                });
                                // Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return cardItemArrayList;
    }

    // inner adapter class
    static class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        private Context context;
        private GeoPoint current;
        private ArrayList<CardItem> cardItemArrayList;
        private DecimalFormat df = new DecimalFormat("0.0");

        public HomeAdapter(Context context, ArrayList<CardItem> cardItemArrayList, GeoPoint current) {
            this.context = context;
            this.cardItemArrayList = cardItemArrayList;
            this.current = current;
        }

        @NonNull
        @Override
        // loading layout, and return MyViewHolder object
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Create view object
            View view = LayoutInflater.from(context).inflate(R.layout.carditem ,parent,false);

            // Create MyViewHolder object
            MyViewHolder myViewHolder=new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        // get data and showing
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CardItem cardData = cardItemArrayList.get(position);
            //holder.img.setImageResource(cardData.getImg());
            holder.title.setText(cardData.getTitles());
            holder.username.setText(cardData.getUsernames());
            holder.distance.setText(df.format(GeoPointUtils.calDistance(current, cardData.getPoint())) + " km");
            GlideApp
                    .with(context)
                    .load(cardData.getImg())
                    .centerCrop()
                    .into(holder.img);
            GlideApp
                    .with(context)
                    .load(cardData.getHeadIcon())
                    .centerCrop()
                    .into(holder.head);
            Log.e("Loading", String.valueOf(cardData.getImg()));


            Log.e("Before", cardData.getTitles()+String.valueOf(cardData.getPostFlag()));
            // 0 means unsolved
            if (cardData.getPostFlag()==0){

                Log.e("After 0", String.valueOf(cardData.getPostFlag()));
                holder.postFlag.setImageResource(R.drawable.ic_finder_question);
            }else{

                Log.e("After 1", String.valueOf(cardData.getPostFlag()));
                holder.postFlag.setImageResource(R.drawable.ic_finder_bingo);
            }
        }



        @Override
        public int getItemCount() {
            // get the number of all card items
            return cardItemArrayList.size();
        }


        // inner class
        class MyViewHolder extends RecyclerView.ViewHolder{

            ImageView img,head,postFlag;
            TextView title,username,distance;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                img=itemView.findViewById(R.id.home_item_img);
                title=itemView.findViewById(R.id.home_item_title);
                head=itemView.findViewById(R.id.home_item_user_img);
                username=itemView.findViewById(R.id.home_item_username);
                distance=itemView.findViewById(R.id.home_item_location);
                postFlag=itemView.findViewById(R.id.home_item_post_flag);

                // click listener
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null){
                            onItemClickListener.OnItemClick(v,cardItemArrayList.get(getLayoutPosition()));
                        }
                    }
                });
            }
        }

        // need to be overwrited in adapter, enable to click item
        public interface OnItemClickListener{
            void OnItemClick(View view, CardItem cardItem);
        }

        private OnItemClickListener onItemClickListener;
        public void setOnItemClickListener(OnItemClickListener onItemClickListener){
            this.onItemClickListener = onItemClickListener;
        }
    }
}
