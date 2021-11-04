package com.comp90018.proj2;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    /** Firebase **/
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final String TAG = "ExampleInstrumentedTest";


    @Before
    public void login() {
        mAuth.signInWithEmailAndPassword("ccccc@gmail.com", "cw980629lm"
        );
    }


    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.comp90018.proj2", appContext.getPackageName());
    }

    @Test
    public void fireStore_send() throws InterruptedException {

        File f = new File("/Users/chenyinglyu/Downloads/14960411577ac1b8.jpg");
        Log.i(TAG, "Image Path:" + f.getName());
        CountDownLatch latch = new CountDownLatch(1);

        Map<String, Object> dto = new HashMap<>();
        dto.put("PostImage", "PostImage");
        dto.put("PostLocation", "PostLocation");
        dto.put("PostSpecies", "Cat");
        dto.put("PostTime", LocalDateTime.now());
        dto.put("PostType", "Animal");
        dto.put("UserId", mAuth.getUid());
        Log.d(TAG, "Doc dto " + dto);

        Log.i(TAG, "################ Test Start ################");

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Post Send");
                db.collection("Test")
                        .add(dto)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                            @Override
                            public void onSuccess(@NonNull DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            };
        }).start();

        latch.await(20000, TimeUnit.MILLISECONDS);
        Log.i(TAG, "################ Test Finished ################");
    }
}