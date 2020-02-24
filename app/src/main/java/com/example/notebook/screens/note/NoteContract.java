package com.example.notebook.screens.note;

import android.os.Bundle;

import com.example.notebook.POJO.Hashtag;
import com.example.notebook.POJO.Note;
import com.example.notebook.base.mvp.MvpPresenter;
import com.example.notebook.base.mvp.MvpView;

import java.util.ArrayList;

public interface NoteContract {

    interface View extends MvpView {
        String getText();
        void setText(String text);
        ArrayList<Hashtag> getHashtags();
        void setSelection(int length);

        void initNoteEditText();
        void initHashtagsHolderLayout(ArrayList<Hashtag> hashtags);

        void showKeyboard();

        void finishView();
    }

    interface Presenter extends MvpPresenter<View>{
        void handleReceivedExtras(Bundle arguments);

        void onDeleteButtonClick();
        void onBackButtonClick(String text);
        void onEditHashtag();

        Note getReceivedNote();
    }
}
