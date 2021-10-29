package com.comp90018.proj2.ui.photo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.io.File;
import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {

    private final String TAG = "PhotoActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate: ");

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (albumItemsAdView != null) {
//            if (albumItemsAdView.getParent() != null) {
//                ((FrameLayout) (albumItemsAdView.getParent())).removeAllViews();
//            }
//        }
//        if (photosAdView != null) {
//            if (photosAdView.getParent() != null) {
//                ((FrameLayout) (photosAdView.getParent())).removeAllViews();
//            }
//        }
//        if (RESULT_OK == resultCode) {
//            //Camera or album callbacks
//            if (requestCode == 101) {
//                //Return a collection of objects
//                ArrayList<Photo> resultPhotos =
//                        data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
//                selectedPhotoList.clear();
//                selectedPhotoList.addAll(resultPhotos);
//                String data123 = selectedPhotoList.get(0).path;
//                String data456 = selectedPhotoList.get(0).uri.toString();
//
//                Log.i(TAG, data123);
//                Log.i(TAG, data456);
//
//                setResult(RESULT_OK, new Intent().putExtra("asd", "asdf"));
//                finish();
////                Intent intent = new Intent(PhotoActivity.this, SendPostActivity.class);
////                intent.putExtra("content", data123);
////                intent.putExtra("content2", data456);
////                startActivity(intent);
//            }
//        }
//    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");

    }
}
