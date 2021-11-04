package com.comp90018.proj2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.comp90018.proj2.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * The activity for splash screen
 *
 */
public class SplashActivity extends Activity {

    private static final int sleepTime = 2000;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Override the onCreate method
     */
    @Override
    protected void onCreate(Bundle arg0) {
        final View view = View.inflate(this, R.layout.activity_splash, null);
        setContentView(view);
        super.onCreate(arg0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            public void run() {
                long start = System.currentTimeMillis();
                long costTime = System.currentTimeMillis() - start;

                // Wait for sleet time
                if (sleepTime - costTime > 0) {
                    try {
                        Thread.sleep(sleepTime - costTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Check the status of user login, and move to the Main / Login activity
                if (mAuth.getCurrentUser() == null) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
            }
        }).start();
    }
}