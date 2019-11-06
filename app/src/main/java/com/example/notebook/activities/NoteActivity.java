package com.example.notebook.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notebook.POJO.Hashtag;
import com.example.notebook.adapters.HashtaBarAdapter;
import com.example.notebook.fragments.DeleteDialog;
import com.example.notebook.POJO.Note;
import com.example.notebook.R;
import com.example.notebook.util.NoteEditText;

import java.util.ArrayList;
import java.util.Date;

import static com.example.notebook.util.Constants.LOG_TAG;
import static com.example.notebook.util.Constants.RESULT_DELETE;
import static com.example.notebook.util.Constants.timeToWriteFormat;

public class NoteActivity extends AppCompatActivity {

    private NoteEditText note_et;
    private ImageButton delete_button, back_button;
    private TextView note_date_tv, note_time_tv;
    private View hashtags_holder_layout_separator;
    private ScrollView scroll_et;

    private ArrayList<Hashtag> recievedHashtags;
    private Note receivedNote;

    private Intent intent;

    private DeleteDialog deleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        intent = new Intent();

        handleReceivedExtras();

        initElementsOfScreen();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (note_et.getText().toString().equals(receivedNote.getText())){
                setResult(RESULT_CANCELED);
                finish();
            } else {
                setResultSaveNote();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setResultSaveNote(){
        receivedNote.setText(note_et.getText().toString());
        //TODO: TIME IS MESSED UP! if you create a note not on today, why then it takes time from now??
        receivedNote.setTime(timeToWriteFormat.format(new Date()));
        intent.putExtra("Note", receivedNote);
        intent.putParcelableArrayListExtra("Hashtags", note_et.getHashtags());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void setResultDeleteNote(){
        intent.putExtra("ID", receivedNote.getId());
        setResult(RESULT_DELETE, intent);
        finish();
    }

    private void handleReceivedExtras(){
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            receivedNote = (Note) arguments.getSerializable("Note");
            initNoteEditText();
            try {
                note_et.setText(receivedNote.getText());
                note_et.setSelection(note_et.getText().length());

                recievedHashtags = note_et.getHashtags();
                if (recievedHashtags.size() != 0) initHashtagsHolderLayout(recievedHashtags);
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, e.getMessage());
            }
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void initNoteEditText(){
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lParams.setMargins(10, 10, 10, 10);
        NoteEditText.HashtagListener hashtagListener = new NoteEditText.HashtagListener() {
            @Override
            public void onEditHashtag() {
                initHashtagsHolderLayout(note_et.getHashtags());
            }
        };
        note_et = new NoteEditText(this, receivedNote.getId(), hashtagListener);
        note_et.setBackgroundResource(R.drawable.background_transparent);
        scroll_et = findViewById(R.id.scroll_et);
        scroll_et.addView(note_et, lParams);
    }

    private void initHashtagsHolderLayout(ArrayList<Hashtag> hashtags){
        hashtags_holder_layout_separator = findViewById(R.id.hashtag_holder_layout_separator);

        RecyclerView hashtag_bar_rv = findViewById(R.id.hashtag_bar_rv);
        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        hashtag_bar_rv.setLayoutManager(llm);
        HashtaBarAdapter adapter = new HashtaBarAdapter(this);
        hashtag_bar_rv.setAdapter(adapter);
        adapter.setItems(hashtags);

        hashtag_bar_rv.setVisibility(View.VISIBLE);
        hashtags_holder_layout_separator.setVisibility(View.VISIBLE);
    }

    private void initElementsOfScreen(){
        delete_button = findViewById(R.id.delete_button);
        back_button = findViewById(R.id.note_back_button);
        note_date_tv = findViewById(R.id.note_date_tv);
        note_time_tv = findViewById(R.id.note_time_tv);

        deleteDialog = new DeleteDialog(this);

        //if it's new note
        if (receivedNote.getText().equals("")){ showKeyboard(this); }

        note_date_tv.setText(receivedNote.getDate());
        String time = receivedNote.getTime();
        if (!time.equals("")) time = time.substring(0, time.length() - 3);
        note_time_tv.setText(time);

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.show(getSupportFragmentManager(), "delete_dialog");
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
            }
        });
    }

    private void showKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
