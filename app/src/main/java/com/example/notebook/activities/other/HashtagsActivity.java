package com.example.notebook.activities.other;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notebook.R;
import com.example.notebook.adapters.TestTagsListAdapter;
import com.example.notebook.db.DBHelper;

public class HashtagsActivity extends SecondaryActivity {
    private RecyclerView hashtags_rv;
    private DBHelper dbHelper;
    private TestTagsListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        initToolbar();

        dbHelper = new DBHelper(this);

        hashtags_rv = findViewById(R.id.hashtags_rv);
        initHashtagsRV();
    }

    private void initHashtagsRV(){
        hashtags_rv = findViewById(R.id.hashtags_rv);
        hashtags_rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TestTagsListAdapter();
        hashtags_rv.setAdapter(adapter);
        adapter.setItems(dbHelper.getAllTags());
    }
}
