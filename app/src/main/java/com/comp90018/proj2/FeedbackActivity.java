package com.comp90018.proj2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Button button = findViewById(R.id.buttonShare);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(FeedbackActivity.this, "Thank you for your advice!", Toast.LENGTH_SHORT);
                toast.show();
                android.os.SystemClock.sleep(2000);
                Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}