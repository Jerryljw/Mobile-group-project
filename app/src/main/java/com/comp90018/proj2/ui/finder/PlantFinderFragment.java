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

/**
 * Fragment for plant finer
 */
public class PlantFinderFragment extends Fragment {

    private static final String TAG = "Extract";

    // Prefix for files in Firebase Storage
    private static final String PREFIX = "gs://mobiletest-e36f3.appspot.com/";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    // LocationCommunication to get the user current location
    LocationCommunication mLocationCallback;
    // Store items
    private final ArrayList<CardItem> cardItemArrayList = new ArrayList<>();
    private View view;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    // Upper Tab
    private Spinner spinner;
    private String sp_item;
    // Read data from firebase
    private final FirebaseFirestore firestore_db = FirebaseFirestore.getInstance();
    private final CollectionReference firestore_reference = firestore_db.
            collection("Post_Temp");
    // The current location in GeoPoint
    private GeoPoint current;

    @Override
    public void onAttach(Context context) {
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
        // Log.e("onCreateView", "2");
        view = inflater.inflate(R.layout.fragment_finder_plant, container, false);

        // load layout and show the data from firebase
        loadCardItemFromFirebase();

        // sorting list button
        spinner = view.findViewById(R.id.plant_sp);
        sp_item = (String) spinner.getSelectedItem();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // get the type of sorting
                sp_item = (String) spinner.getSelectedItem();
                if (sp_item.equalsIgnoreCase("Latest Post Time")) {
                    PostTimeSort postTimeSort = new PostTimeSort();
                    Collections.sort(cardItemArrayList, postTimeSort);

                    // refresh the view
                    initRecycleView();
                } else {
                    Log.e(TAG, "Nearest");
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


    /**
     * Initialize the view in nested fragment
     */
    private void initRecycleView() {
        recyclerView = view.findViewById(R.id.home_item);
        homeAdapter = new HomeAdapter(getActivity(), cardItemArrayList, current);

        // set format of view, 2 cols
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(homeAdapter);

        // create click listener on adapter for passing postid to PostActivity
        homeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, CardItem cardItem) {

                // jump to post activity
                Intent intent = new Intent(getActivity(), PostActivity.class);
                Bundle bundle = new Bundle();
                Log.e(TAG, String.valueOf(cardItem.getPostId()));
                bundle.putString("postId", cardItem.getPostId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    /**
     * Load Data from firebase, go through all documents in the collections
     *
     * @return a list contains all the data
     */
    private ArrayList<CardItem> loadCardItemFromFirebase() {
        firestore_reference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // go through all the documents in collection
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
                                                            String postUserHeadIcon = documentSnapshot
                                                                    .getString("UserPhotoUri");
                                                            String postUserName = documentSnapshot
                                                                    .getString("UserDisplayName");
                                                            GeoPoint postGeoPoint = documentSnapshot
                                                                    .getGeoPoint("PostLocation");
                                                            // Log.e("Location", String.valueOf(postGeoPoint));
                                                            String postImg = documentSnapshot
                                                                    .getString("PostImage");
                                                            String postTitle = documentSnapshot
                                                                    .getString("PostTitle");
                                                            String postId = document.getId();
                                                            int postFlag = Math.toIntExact(documentSnapshot
                                                                    .getLong("PostFlag"));


                                                            // set data
                                                            CardItem cardItem = new CardItem();
                                                            cardItem.setImg(storage.getReferenceFromUrl(PREFIX + postImg));
                                                            cardItem.setHeadIcon(storage.getReferenceFromUrl(postUserHeadIcon));
                                                            cardItem.setTitles(postTitle);
                                                            cardItem.setUsernames(postUserName);
                                                            cardItem.setPoint(postGeoPoint);
                                                            cardItem.setPostTime(postTime);
                                                            cardItem.setPostId(postId);
                                                            cardItem.setPostFlag(postFlag);
                                                            cardItem.setPostType(postType);
                                                            cardItem.setPostSpecies(postSpecies);
                                                            Log.e("PostType", postType);
                                                            if (postType.equalsIgnoreCase("Plant")) {
                                                                cardItemArrayList.add(cardItem);
                                                            }

                                                            // refresh view
                                                            homeAdapter = new HomeAdapter(getActivity(),
                                                                    cardItemArrayList, current);
                                                            recyclerView.setAdapter(homeAdapter);

                                                            // create click listener on adapter again
                                                            homeAdapter.setOnItemClickListener(
                                                                    new HomeAdapter.OnItemClickListener() {
                                                                        @Override
                                                                        public void OnItemClick(View view,
                                                                                                CardItem cardItem) {

                                                                            // jump to post activity
                                                                            Intent intent = new Intent(getActivity(), PostActivity.class);
                                                                            Bundle bundle = new Bundle();
                                                                            Log.e("click", String.valueOf(cardItem.getPostId()));
                                                                            bundle.putString("postId", cardItem.getPostId());
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
        private final Context context;
        private final ArrayList<CardItem> cardItemArrayList;

        // for click card item
        private OnItemClickListener onItemClickListener;


        // show current location and format to 2 decimal places
        private final GeoPoint current;
        private final DecimalFormat df = new DecimalFormat("0.0");

        public HomeAdapter(Context context, ArrayList<CardItem> cardItemArrayList, GeoPoint current) {
            this.context = context;
            this.cardItemArrayList = cardItemArrayList;
            this.current = current;
        }

        /**
         * loading layout, and return MyViewHolder object
         *
         * @param parent   parent view
         * @param viewType view type
         * @return a view holder
         */
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Create view object
            View view = LayoutInflater.from(context).inflate(R.layout.carditem, parent, false);

            // Create MyViewHolder object
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CardItem cardData = cardItemArrayList.get(position);
            //holder.img.setImageResource(cardData.getImg());
            holder.title.setText(cardData.getTitles());
            holder.username.setText(cardData.getUsernames());
            holder.distance.setText(df.format(GeoPointUtils.calDistance(current, cardData.getPoint())) + " km");
            // 0 means unsolved
            if (cardData.getPostFlag() == 0) {
                holder.postFlag.setImageResource(R.drawable.ic_finder_question);
            } else {
                holder.postFlag.setImageResource(R.drawable.ic_finder_bingo);
            }

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
        }

        @Override
        public int getItemCount() {
            // get the number of all card items
            return cardItemArrayList.size();
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        // need to be overwritten in adapter, enable to click item
        public interface OnItemClickListener {
            void OnItemClick(View view, CardItem cardItem);
        }

        // inner class
        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView img, head, postFlag;
            TextView title, username, distance;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.home_item_img);
                title = itemView.findViewById(R.id.home_item_title);
                head = itemView.findViewById(R.id.home_item_user_img);
                username = itemView.findViewById(R.id.home_item_username);
                distance = itemView.findViewById(R.id.home_item_location);
                postFlag = itemView.findViewById(R.id.home_item_post_flag);

                // click listener
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.OnItemClick(v, cardItemArrayList.get(getLayoutPosition()));
                        }
                    }
                });
            }
        }
    }


}
