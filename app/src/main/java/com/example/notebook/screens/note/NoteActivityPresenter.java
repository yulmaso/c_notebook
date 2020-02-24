package com.example.notebook.screens.note;

import android.os.Bundle;
import android.util.Log;

import com.example.notebook.POJO.Hashtag;
import com.example.notebook.POJO.Note;
import com.example.notebook.base.mvp.BasePresenter;
import com.example.notebook.storage.DBHelper;

import java.util.ArrayList;
import java.util.Date;

import static com.example.notebook.storage.Constants.LOG_TAG;
import static com.example.notebook.storage.Constants.timeToWriteFormat;

public class NoteActivityPresenter extends BasePresenter<NoteContract.View> implements NoteContract.Presenter {

    DBHelper dbHelper;

    private ArrayList<Hashtag> receivedHashtags;
    private Note receivedNote;

    private boolean newNote;

    public NoteActivityPresenter(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void viewIsReady() {}

    @Override
    public void onDeleteButtonClick() {
        dbHelper.deleteNote(receivedNote.getId());
        getView().finishView();
    }

    @Override
    public void onBackButtonClick(String text) {
        if (text.equals(receivedNote.getText())){
            getView().finishView();
        } else {
            receivedNote.setText(text);
            //TODO: NOTE TIME IS MESSED UP! if you create a note not on today, why then it takes time from now??
            receivedNote.setTime(timeToWriteFormat.format(new Date()));

            if (newNote){
                dbHelper.newNote(receivedNote);
                dbHelper.writeHashtags(getView().getHashtags());
            } else {
                dbHelper.updateNote(receivedNote);
                updateHashtags(receivedHashtags, getView().getHashtags());
            }

            getView().finishView();
        }
    }

    @Override
    public void onEditHashtag() {
        getView().initHashtagsHolderLayout(getView().getHashtags());
    }

    @Override
    public void handleReceivedExtras(Bundle arguments) {
        if (arguments != null) {
            receivedNote = (Note) arguments.getSerializable("Note");
            getView().initNoteEditText();
            try {
                if (receivedNote.getText().equals("")){
                    newNote = true;
                } else {
                    newNote = false;
                    getView().setText(receivedNote.getText());
                    getView().setSelection(receivedNote.getText().length());
                }

                receivedHashtags = getView().getHashtags();
                if (receivedHashtags.size() != 0) getView().initHashtagsHolderLayout(receivedHashtags);
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, e.getMessage());
            }
        } else {
            getView().finishView();
        }
    }

    @Override
    public Note getReceivedNote() {
        return receivedNote;
    }

    private void updateHashtags(ArrayList<Hashtag> oldHashtags,
                                              ArrayList<Hashtag> newHashtags){
        for (Hashtag newhashtag : newHashtags)
            for (Hashtag oldHashtag : oldHashtags){
                if (oldHashtag.equals(newhashtag)) {
                    oldHashtags.remove(oldHashtag);
                    newHashtags.remove(newhashtag);
                    break;
                }
            }


        dbHelper.writeHashtags(newHashtags);
        dbHelper.deleteHashtags(oldHashtags);
    }
}
