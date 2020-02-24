package com.example.notebook.screens.toDo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notebook.POJO.Hashtag;
import com.example.notebook.POJO.Note;
import com.example.notebook.R;
import com.example.notebook.screens.MinorActivity;
import com.example.notebook.screens.note.NoteActivity;
import com.example.notebook.adapters.NotesAdapter;
import com.example.notebook.adapters.TestTagsListAdapter;
import com.example.notebook.storage.DBHelper;

import java.util.ArrayList;

import static com.example.notebook.storage.Constants.LOG_TAG;
import static com.example.notebook.storage.Constants.REQUEST_CODE_NEWNOTE;
import static com.example.notebook.storage.Constants.REQUEST_CODE_UPDATENOTE;
import static com.example.notebook.storage.Constants.RESULT_DELETE;

public class SpecificNotesActivity extends MinorActivity {
    private RecyclerView specific_list_rv;
    private DBHelper dbHelper;
    private TestTagsListAdapter adapter;
    private Hashtag receivedHashtag;
    private Intent note_intent;
    private NotesAdapter notesAdapter;
    private TextView textView;
    private LinearLayout.LayoutParams lParams;
    private ArrayList<Hashtag> oldHashtags;

    public static void startActivity(Context context){
        Intent intent = new Intent(context, SpecificNotesActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_notes);
        initToolbar();

        receivedHashtag = (Hashtag) getIntent().getExtras().getParcelable("hashtag");
        dbHelper = new DBHelper(this);

        oldHashtags = new ArrayList<>();
        note_intent = new Intent(this, NoteActivity.class);

        initHashtagsRV();
        refreshNotes();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(LOG_TAG, "onActivityResult mainActivity");
//        Log.d(LOG_TAG, "Result code: " + resultCode);
//        Log.d(LOG_TAG, "Request code: " + requestCode);
        if (resultCode == RESULT_OK){
            Bundle arguments = data.getExtras();
            Note note = (Note) arguments.getSerializable("Note");
            ArrayList<Hashtag> hashtags = arguments.getParcelableArrayList("Hashtags");
            Log.d(LOG_TAG, "Text: " + note.getText());
            switch (requestCode){
                case REQUEST_CODE_NEWNOTE:{
                    if (!note.getText().equals("")) {
                        dbHelper.newNote(note);
                        try {
//                            Log.d(LOG_TAG, "On hashtags write");
                            dbHelper.writeHashtags(hashtags);
                        } catch (NullPointerException e){
                            Log.d(LOG_TAG, e.getMessage());
                        }
                    }
                    break;
                }
                case REQUEST_CODE_UPDATENOTE:{
                    if (note.getText().equals("")) {
                        dbHelper.deleteNote(note.getId());
                    } else {
                        dbHelper.updateNote(note);
                        try {
                            //TODO: когда редактируешь хэштег, старая версия остается в базе
                            dbHelper.deleteHashtags(getRemovedHashtags(oldHashtags, hashtags));
//                            Log.d(LOG_TAG, "On hashtags write");
                            dbHelper.writeHashtags(hashtags);
                        } catch (NullPointerException e){
                            Log.d(LOG_TAG, e.getMessage());
                        }
                    }
                    break;
                }
            }
            refreshNotes();
        }
        if (resultCode == RESULT_DELETE){
            int id = data.getIntExtra("ID", -1);
            if (id != -1){
                dbHelper.deleteNote(id);
            }
            refreshNotes();
        }
    }

    private void initHashtagsRV(){
        specific_list_rv = findViewById(R.id.specific_list_rv);
        specific_list_rv.setLayoutManager(new LinearLayoutManager(this));
        NotesAdapter.OnNoteClickListener onNoteClickListener = new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(Note note) {
                Log.d(LOG_TAG,"Note id: " + note.getId());
                note_intent.putExtra("Note", note);
                oldHashtags = dbHelper.getSpecificHashtags(note.getId());
                startActivityForResult(note_intent, REQUEST_CODE_UPDATENOTE);
            }
        };
        notesAdapter = new NotesAdapter(onNoteClickListener, this);
        specific_list_rv.setAdapter(notesAdapter);
    }

    private void refreshNotes(){
        notesAdapter.clearItems();
        ArrayList<Note> notes = dbHelper.getNotesOnHashtag(receivedHashtag);
        if (notes.size() == 0) finish();
        notesAdapter.setItems(notes);
    }

    private ArrayList<Hashtag> getRemovedHashtags(ArrayList<Hashtag> oldList, ArrayList<Hashtag> newList){
        ArrayList<Hashtag> hashtagsToRemove = new ArrayList<>();
        for (int i = 0; i < oldList.size(); i++){
            boolean flag = true;
            for (int k = 0; k < newList.size(); k++){
                if (newList.get(k).equals(oldList.get(i))){
                    flag = false;
                    break;
                }
            }
            if (flag) hashtagsToRemove.add(oldHashtags.get(i));
        }
        return hashtagsToRemove;
    }
}
