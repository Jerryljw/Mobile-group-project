package com.comp90018.proj2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity for collecting feedback from end user
 */
public class FeedbackActivity extends AppCompatActivity {

    private final String TAG = "FeedbackActivity";

    // Firebase instances
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();

    /**
     * Override the onCreate method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Button button = findViewById(R.id.buttonShare);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText content = findViewById(R.id.text_content);

                Map<String, Object> dto = new HashMap<>();
                dto.put("UserId", mAuth.getUid());
                dto.put("Content", content.getText().toString());

                db.collection("Advice").add(dto);

                Toast toast = Toast.makeText(FeedbackActivity.this, "Thank you for your advice!", Toast.LENGTH_SHORT);
                toast.show();
                android.os.SystemClock.sleep(2000);
                Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}