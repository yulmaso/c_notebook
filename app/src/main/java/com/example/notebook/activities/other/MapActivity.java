package com.example.notebook.activities.other;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.notebook.R;

public class MapActivity extends SecondaryActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initToolbar();
    }
}
