package com.example.notebook.screens.main;

import android.content.Context;
import android.view.View;

import com.applandeo.materialcalendarview.EventDay;
import com.example.notebook.POJO.Note;
import com.example.notebook.adapters.NotesAdapter;
import com.example.notebook.base.mvp.MvpPresenter;
import com.example.notebook.base.mvp.MvpView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public interface MainContract {

    interface View extends MvpView{
        Context getContext();

        void setCalendarDate(Calendar date);
        void setCalendarEvents(List<EventDay> events);
        String getCalendarSelectedDate();

        void showTextView();
        void removeTextView(ArrayList<Note> notes);

    }

    interface Presenter extends MvpPresenter<View>{
        void refreshNotes(boolean allNotesScreen);
        void refreshCalendar();

        void onCalendarDayClick(EventDay eventDay);
        void onNoteListClick(Note note);
        void onActionBarClick(boolean allNotesScreen, android.view.View view);
    }
}
