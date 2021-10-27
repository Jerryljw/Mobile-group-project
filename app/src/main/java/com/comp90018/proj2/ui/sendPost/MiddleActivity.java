package com.comp90018.proj2.ui.sendPost;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.comp90018.proj2.R;
import com.comp90018.proj2.ui.photo.GlideEngine;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.util.ArrayList;

public class MiddleActivity extends AppCompatActivity {

    /**
     * photos
     */
    private ArrayList<Photo> selectedPhotoList = new ArrayList<>();

    /**
     * Adview for picture list and album item list
     */
    private RelativeLayout photosAdView, albumItemsAdView;

    /**
     * Adview is loaded over or not
     */
    private boolean photosAdLoaded = false, albumItemsAdLoaded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle);
        Button button = (Button) findViewById(R.id.easy_photo_open);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyPhotos.createAlbum(MiddleActivity.this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.comp90018.proj2.ui.photo.fileprovider")
                        .start(101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (albumItemsAdView != null) {
            if (albumItemsAdView.getParent() != null) {
                ((FrameLayout) (albumItemsAdView.getParent())).removeAllViews();
            }
        }
        if (photosAdView != null) {
            if (photosAdView.getParent() != null) {
                ((FrameLayout) (photosAdView.getParent())).removeAllViews();
            }
        }
        if (RESULT_OK == resultCode) {
            //Camera or album callbacks
            if (requestCode == 101) {
                //Return a collection of objects
                ArrayList<Photo> resultPhotos =
                        data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                selectedPhotoList.clear();
                selectedPhotoList.addAll(resultPhotos);
                String data123 = selectedPhotoList.get(0).path;
                String data456 = selectedPhotoList.get(0).uri.toString();
                Intent intent = new Intent(MiddleActivity.this, SendPostActivity.class);
                intent.putExtra("content", data123);
                intent.putExtra("content2", data456);
                startActivity(intent);
                return;
            }
        }
    }
}