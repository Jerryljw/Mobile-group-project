package com.comp90018.proj2.ui.map;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.comp90018.proj2.R;
import com.comp90018.proj2.databinding.CarditemBinding;
import com.comp90018.proj2.ui.finder.CardItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class PostInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private CarditemBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final static String TAG = "PostInfoWindow";
    private CardItem cardItem = new CardItem();

    public PostInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        String postId = (String) marker.getTag();
        Log.d(TAG, "getInfoContents: post id: " + postId);
        getPost(postId);
        return null;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.carditem, null);
        binding =  CarditemBinding.bind(view);
        ImageView itemImg = binding.homeItemImg;
        ImageView itemHead = binding.homeItemHead;
        TextView location = binding.homeItemLocation;
        TextView title = binding.homeItemTitle;
        TextView username = binding.homeItemUsername;

//        itemImg.setImageURI(Uri.parse(cardItem.getImgUrl()));
        Log.d(TAG, "getInfoContents: " + cardItem);
        itemHead.setImageResource(R.drawable.ic_card_portrait);
        location.setText(cardItem.getDistance());
        title.setText("");
        username.setText(cardItem.getUsernames());

        return view;
    }

    private void getPost(String postId) {
        DocumentReference post = db.collection("Post").document(postId);
        Log.d(TAG, "getPost: " +  " coming here");
        post.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> data = document.getData();

                        Log.d(TAG, "onComplete: " + data);
                        cardItem.setDistance("0 km");
                        cardItem.setUsernames((String) data.get("UserId"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
