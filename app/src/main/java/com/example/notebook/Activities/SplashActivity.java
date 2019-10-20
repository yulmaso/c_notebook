package com.example.notebook.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //cant make it work because for some reason it doesnt want to use @drawable/splash_background.xml

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
