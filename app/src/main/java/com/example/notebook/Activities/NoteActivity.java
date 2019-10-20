package com.example.notebook.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notebook.Fragments.DeleteDialog;
import com.example.notebook.POJO.Note;
import com.example.notebook.R;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.Date;

import static com.example.notebook.OtherStuff.Constants.LOG_TAG;
import static com.example.notebook.OtherStuff.Constants.timeToWriteFormat;

public class NoteActivity extends AppCompatActivity {

    private EditText note_et;
    private ImageButton delete_button, back_button;
    private TextView note_date_tv, note_time_tv;

    private Note receivedNote;
    private Intent intent;

    private DeleteDialog deleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        note_et = findViewById(R.id.note_et);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            receivedNote = (Note) arguments.getSerializable("Note");
            try {
                note_et.setText(receivedNote.getText());
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, e.getMessage());
            }
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }

        intent = new Intent();

        initElements();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (note_et.getText().toString().equals(receivedNote.getText())){
                setResult(RESULT_CANCELED);
            } else {
                receivedNote.setText(note_et.getText().toString());
                //TODO: TIME IS MESSED UP! if you create a note not on today, why then it takes time from now??
                receivedNote.setTime(timeToWriteFormat.format(new Date()));
                intent.putExtra("Note", receivedNote);
                setResult(RESULT_OK, intent);
            }
            Log.d(LOG_TAG, "onPause (finish) noteActivity");
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initElements(){
        delete_button = findViewById(R.id.delete_button);
        back_button = findViewById(R.id.back_button);
        note_date_tv = findViewById(R.id.note_date_tv);
        note_time_tv = findViewById(R.id.note_time_tv);

        deleteDialog = new DeleteDialog();

        if (receivedNote.getText().equals("")){ //if it's new note
            Log.d(LOG_TAG, "in this method......");
            UIUtil.showKeyboard(this, note_et);
        } else { //if it's existing note
//            ((ScrollView)findViewById(R.id.scroll_et)).setFocusableInTouchMode(true);
        }

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
}
