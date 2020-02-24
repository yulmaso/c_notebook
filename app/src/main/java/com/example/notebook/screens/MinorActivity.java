package com.example.notebook.screens;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.notebook.R;

public abstract class MinorActivity extends AppCompatActivity {
    private ImageButton back_button;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initToolbar(){
        toolbar = findViewById(R.id.toolbar_others);
        setSupportActionBar(toolbar);

        back_button = findViewById(R.id.toolbar_back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
