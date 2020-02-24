package com.example.notebook.screens.toDo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notebook.POJO.Hashtag;
import com.example.notebook.R;
import com.example.notebook.adapters.TestTagsListAdapter;
import com.example.notebook.storage.DBHelper;
import com.example.notebook.screens.MinorActivity;

import static com.example.notebook.storage.Constants.LOG_TAG;

public class HashtagsActivity extends MinorActivity {
    private RecyclerView hashtags_rv;
    private DBHelper dbHelper;
    private TestTagsListAdapter adapter;
    private Intent specificNotesIntent;

    public static void startActivity(Context context){
        Intent intent = new Intent(context, HashtagsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        initToolbar();

        dbHelper = new DBHelper(this);

        specificNotesIntent = new Intent(this, SpecificNotesActivity.class);

        hashtags_rv = findViewById(R.id.hashtags_rv);
//        Toast.makeText(this, "CAUTION! DOESN'T WORK CORRECTLY", Toast.LENGTH_SHORT).show();
        initHashtagsRV();
    }

    private void initHashtagsRV(){
        hashtags_rv = findViewById(R.id.hashtags_rv);
        hashtags_rv.setLayoutManager(new LinearLayoutManager(this));
        TestTagsListAdapter.OnSmthingClickListener clickListener = new TestTagsListAdapter.OnSmthingClickListener(){
            @Override
            public void onHashtagClick(Hashtag hashtag) {
                int count = dbHelper.getNotesOnHashtag(hashtag).size();
                Log.d(LOG_TAG, "NUMBER OF NOTES = " + count);
                specificNotesIntent.putExtra("hashtag", hashtag);
                startActivity(specificNotesIntent);
            }
        };
        adapter = new TestTagsListAdapter(clickListener);
        hashtags_rv.setAdapter(adapter);
        adapter.setItems(dbHelper.getAllTags());
    }
}
