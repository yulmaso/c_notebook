package com.example.notebook.screens.main;

import android.util.Log;
import android.view.View;

import com.applandeo.materialcalendarview.EventDay;
import com.example.notebook.POJO.Note;
import com.example.notebook.R;
import com.example.notebook.base.mvp.BasePresenter;
import com.example.notebook.storage.DBHelper;
import com.example.notebook.screens.note.NoteActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.notebook.storage.Constants.LOG_TAG;
import static com.example.notebook.storage.Constants.dateFormat;

public class MainActivityPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter{

    private DBHelper dbHelper;

    public MainActivityPresenter(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void viewIsReady() {}

    @Override
    public void refreshNotes(boolean allNotesScreen) {
        ArrayList<Note> notes;
        if (allNotesScreen) {
            notes = dbHelper.getAllNotes();
        } else {
            notes = dbHelper.getNotesOnDate(getView().getCalendarSelectedDate());
        }
        if (notes != null && notes.size() == 0){
            getView().showTextView();
        } else {
            getView().removeTextView(notes);
        }
    }

    @Override
    public void refreshCalendar() {
        List<Calendar> dates = dbHelper.getDates();
        List<EventDay> events = new ArrayList<>();
        for (Calendar date : dates) {
            Log.d(LOG_TAG, date.getTime().toString());
            events.add(new EventDay(date, R.drawable.ic_note_black_24dp));
        }
        getView().setCalendarEvents(events);
    }

    @Override
    public void onCalendarDayClick(EventDay eventDay) {
        Calendar date = eventDay.getCalendar();
        getView().setCalendarDate(date);
        refreshNotes(false);
    }

    @Override
    public void onNoteListClick(Note note) {
        Log.d(LOG_TAG,"Note id: " + note.getId());
        NoteActivity.startActivity(getView().getContext(), note);
    }

    @Override
    public void onActionBarClick(boolean allNotesScreen, View view) {
        String date;
        if (allNotesScreen){
            date = dateFormat.format(Calendar.getInstance().getTime());
        } else {
            date = getView().getCalendarSelectedDate();
        }
        Note note = new Note(dbHelper.getNextNoteID(),"", date, "");
        NoteActivity.startActivity(getView().getContext(), note);
    }
}
