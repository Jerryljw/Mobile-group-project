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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class AnimalFinderFragment extends Fragment {

    // store items
    private ArrayList<CardItem> cardItemArrayList = new ArrayList<>();
    private View view;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;

    private Spinner spinner;
    private String sp_item;
    private static final String TAG = "Extract";

    // read data from firebase
    private FirebaseFirestore firestore_db = FirebaseFirestore.getInstance();
    private CollectionReference firestore_reference = firestore_db.
            collection("Post_Temp");

    // get the user current location
    GeoPoint current = new GeoPoint(34.002, 151.001);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //这个修改为对应子Fragment和父Fragment的布局文件
        view=inflater.inflate(R.layout.fragment_finder_animal,container,false);

        // show data
        initRecycleView();
        loadCardItemFromFirebase();

        // sorting list
        spinner = (Spinner) view.findViewById(R.id.animal_sp);
        sp_item = (String) spinner.getSelectedItem();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp_item = (String) spinner.getSelectedItem();
                if (sp_item.equalsIgnoreCase("Latest Post Time")) {
                    Log.e("Animal","latest");
                    PostTimeSort postTimeSort = new PostTimeSort();
                    Collections.sort(cardItemArrayList, postTimeSort);
                    initRecycleView();
                } else {
                    Log.e("Animal","Nearest");
                    PostLocSort postLocSort = new PostLocSort(current);
                    Collections.sort(cardItemArrayList, postLocSort);
                    initRecycleView();
                }
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
        // Create a recyclerView object，initialize xml
        recyclerView=(RecyclerView) view.findViewById(R.id.home_item);

        // create an adapter to put items
        homeAdapter = new HomeAdapter(getActivity(),cardItemArrayList,current);

        // set format of view，2 cols
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        // add item space to recycleView
        //recyclerView.addItemDecoration(new AnimalFinderFragment.space_item(space));

        //add adapter to recyclerView
        recyclerView.setAdapter(homeAdapter);

        // create click listener on adapter
        homeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, CardItem cardItem) {

                // jump to post activity
                // TODO: need to pass USERID to PostActivity, use !postId!
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
                                                            String postUserid = documentSnapshot
                                                                    .getString("UserId");
                                                            GeoPoint postGeoPoint = documentSnapshot
                                                                    .getGeoPoint("PostLocation");
                                                            String postImg = documentSnapshot
                                                                    .getString("PostImage");
                                                            String postTitle = documentSnapshot
                                                                    .getString("PostTitle");
                                                            String postId = documentSnapshot
                                                                    .getString("PostId");

                                                            // set data
                                                            CardItem cardItem = new CardItem();
                                                            cardItem.setImg(R.drawable.ic_card_image);
                                                            cardItem.setHeadsIcon(R.drawable.ic_card_portrait);
                                                            cardItem.setTitles(postTitle);
                                                            cardItem.setUsernames("test");
                                                            cardItem.setPoint(postGeoPoint);
                                                            cardItem.setPostId(postId);
                                                            cardItem.setPostTime(postTime);

                                                            cardItemArrayList.add(cardItem);

                                                            Log.d("firebase",
                                                                    String.valueOf(cardItemArrayList.size()));

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
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return cardItemArrayList;
    }

    static public double caldistance(GeoPoint current, GeoPoint postPoint)
    {
        double lon1 = Math.toRadians(current.getLongitude());
        double lon2 = Math.toRadians(postPoint.getLongitude());
        double lat1 = Math.toRadians(current.getLatitude());
        double lat2 = Math.toRadians(postPoint.getLatitude());

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }



    // inner adapter class
    static class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        private Context context;
        private GeoPoint current;
        private ArrayList<CardItem> cardItemArrayList;

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
            holder.img.setImageResource(cardData.getImg());
            holder.title.setText(cardData.getTitles());
            holder.head.setImageResource(cardData.getHeadsIcon());
            holder.username.setText(cardData.getUsernames());
            holder.distance.setText(String.valueOf(caldistance(current,cardData.getPoint())));

            // 0 means unsolved
            if (cardData.getPostType()==0){
                holder.postType.setImageResource(R.drawable.ic_finder_question);
            }else{

                holder.postType.setImageResource(R.drawable.ic_finder_bingo);
            }
        }



        @Override
        public int getItemCount() {
            // get the number of all card items
            return cardItemArrayList.size();
        }


        // inner class
        class MyViewHolder extends RecyclerView.ViewHolder{

            ImageView img,head,postType;
            TextView title,username,distance;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                img=itemView.findViewById(R.id.home_item_img);
                title=itemView.findViewById(R.id.home_item_title);
                head=itemView.findViewById(R.id.home_item_head);
                username=itemView.findViewById(R.id.home_item_username);
                distance=itemView.findViewById(R.id.home_item_location);
                postType=itemView.findViewById(R.id.home_item_post_type);

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
