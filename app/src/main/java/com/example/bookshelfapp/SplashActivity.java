package com.example.bookshelfapp;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import com.example.bookshelfapp.utils.DataStoreManager;

public class SplashActivity extends AppCompatActivity {

    private DataStoreManager dataStoreManager;
    private Intent nextActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        dataStoreManager=DataStoreManager.getInstance(this);

        dataStoreManager.getBoolean("keep_logged_in")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(keepLoggedIn -> {
                    if (!keepLoggedIn) {
                        nextActivity = new Intent(SplashActivity.this, LoginRegisterActivity.class);
                    } else {
                        //nextActivity = new Intent(SplashActivity.this, MainActivity.class);
                        nextActivity = new Intent(SplashActivity.this, MainActivityTemp.class);
                    }
                }, throwable -> {
                    Log.e("SplashActivity", "Error getting keep_logged_in preference", throwable);
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(nextActivity);
                finish();
            }
        }, 1500);
    }
}