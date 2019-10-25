package com.example.notebook.DBclasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.notebook.POJO.Note;
import com.example.notebook.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import static com.example.notebook.OtherStuff.Constants.LOG_TAG;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "notesdb.db";
    private static String DB_PATH;

    private final Context mContext;

    private NewNoteTask newNoteTask;
    private UpdateNoteTask updateNoteTask;
    private DeleteNoteTask deleteNoteTask;
    private GetNoteTask getNoteTask;
    private GetAllNotesTask getAllNotesTask;
    private GetNotesOnDateTask getNotesOnDateTask;
    private GetDatesTask getDatesTask;

    public DBHelper (Context context){
        super(context, DB_NAME, null, 1);

        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";

        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        Log.d(LOG_TAG, "--- OnCreate Database ---");
//        db.execSQL("create table notes (" +
//                "id integer primary key autoincrement," +
//                "note text," +
//                "date date Not Null)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void updateDataBase(){
        File dbFile = new File(DB_PATH + DB_NAME);
        if (dbFile.exists())
            dbFile.delete();

        // Копирование БД, если её не существует на устройстве
        copyDataBase();
    }

    public boolean checkDataBase(){
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase(){
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile(DB_NAME);
            } catch (IOException mIOException) {
                throw new Error("Error copying dataBase");
            }
        }
    }

    private void copyDBFile(String DBFileName) throws IOException{
        InputStream mInput = mContext.getResources().openRawResource(R.raw.notesdb);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DBFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    //doesn't use id from note
    public void newNote(Note note){
        Log.d(LOG_TAG, "on NewNote");
        newNoteTask = new NewNoteTask(getWritableDatabase());
        newNoteTask.execute(note);
    }

    public void updateNote(Note note){
        updateNoteTask = new UpdateNoteTask(getWritableDatabase());
        updateNoteTask.execute(note);
    }

    public void deleteNote(int id){
        deleteNoteTask = new DeleteNoteTask(getWritableDatabase());
        deleteNoteTask.execute(id);
    }

    public Note getNote(int id){
        getNoteTask = new GetNoteTask(getReadableDatabase());
        getNoteTask.execute(id);
        try {
            return getNoteTask.get();
        } catch(InterruptedException e){
            Log.d(LOG_TAG, e.getMessage());
        } catch (ExecutionException e){
            Log.d(LOG_TAG, e.getMessage());
        }
        return null;
    }

    public ArrayList<Note> getAllNotes(){
        getAllNotesTask = new GetAllNotesTask(getReadableDatabase());
        getAllNotesTask.execute();
        try {
            return getAllNotesTask.get();
        } catch(InterruptedException e){
            Log.d(LOG_TAG, e.getMessage());
        } catch (ExecutionException e){
            Log.d(LOG_TAG, e.getMessage());
        }
        return null;
    }

    public ArrayList<Note> getNotesOnDate(String date){
        getNotesOnDateTask = new GetNotesOnDateTask(getReadableDatabase());
        getNotesOnDateTask.execute(date);
        try {
            return getNotesOnDateTask.get();
        } catch(InterruptedException e){
            Log.d(LOG_TAG, e.getMessage());
        } catch (ExecutionException e){
            Log.d(LOG_TAG, e.getMessage());
        }
        return null;
    }

    public ArrayList<Calendar> getDates(){
        getDatesTask = new GetDatesTask(getReadableDatabase());
        getDatesTask.execute();
        try {
            return getDatesTask.get();
        } catch(InterruptedException e){
            Log.d(LOG_TAG, e.getMessage());
        } catch (ExecutionException e){
            Log.d(LOG_TAG, e.getMessage());
        }
        return null;
    }

    private static class NewNoteTask extends AsyncTask<Note, Void, Void>{
        SQLiteDatabase db;

        NewNoteTask(SQLiteDatabase db){
            this.db = db;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            Log.d(LOG_TAG, "on newNoteTask");
            ContentValues cv = new ContentValues();
            cv.put("note", notes[0].getText());
            cv.put("date", notes[0].getDate());
            cv.put("time", notes[0].getTime());
            db.insert("notes", null, cv);
//            db.rawQuery("INSERT INTO notes (note, date) VALUES (\"" + text + "\",\"" + date + "\")", null);
//            db.close();
            return null;
        }
    }

    private static class UpdateNoteTask extends AsyncTask<Note, Void, Void>{
        SQLiteDatabase db;

        UpdateNoteTask(SQLiteDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            ContentValues cv = new ContentValues();
            cv.put("id", notes[0].getId());
            cv.put("note", notes[0].getText());
            cv.put("date", notes[0].getDate());
            cv.put("time", notes[0].getTime());
            db.replace("notes", null, cv);
//            db.rawQuery("UPDATE notes SET note = \"" + text + "\", date = \"" + date + "\" WHERE ID = "+ id, null);
//            db.close();
            return null;
        }
    }

    private static class DeleteNoteTask extends AsyncTask<Integer, Void, Void>{
        SQLiteDatabase db;

        DeleteNoteTask(SQLiteDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            String[] args = {String.valueOf(integers[0])};
            db.delete("notes","ID = ?",args);
//            db.delete("notes","ID = ?",new String[]{String.valueOf(id)});
//            db.rawQuery("DELETE FROM notes WHERE id = " + id, null);
//            db.close();
            return null;
        }
    }

    private static class GetNoteTask extends AsyncTask<Integer, Void, Note>{
        SQLiteDatabase db;

        GetNoteTask(SQLiteDatabase db) {
            this.db = db;
        }

        @Override
        protected Note doInBackground(Integer... integers) {
            Note note = new Note();
            Cursor c = db.rawQuery("SELECT * FROM notes WHERE id = " + integers[0].toString(), null);
            if (c != null && c.moveToFirst()) {
                note.setId(c.getInt(0));
                note.setText(c.getString(1));
                note.setDate(c.getString(2));
                note.setTime(c.getString(3));
                c.close();
            }
//            db.close();
            return note;
        }
    }

    private static class GetAllNotesTask extends AsyncTask<Void, Void, ArrayList<Note>>{
        SQLiteDatabase db;

        GetAllNotesTask(SQLiteDatabase db) {
            this.db = db;
        }

        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {
            ArrayList<Note> notes = new ArrayList<>();

            Cursor c = db.query("notes", null, null, null, null, null, "date DESC, time DESC");
//            Cursor c = db.rawQuery("SELECT * FROM notes", null);
            if (c != null && c.moveToFirst()) {
                do {
                    notes.add(new Note(c.getInt(0), c.getString(1), c.getString(2), c.getString(3)));
                } while (c.moveToNext());
                c.close();
            }
//            db.close();
            return notes;
        }
    }

    private static class GetNotesOnDateTask extends AsyncTask<String, Void, ArrayList<Note>>{
        SQLiteDatabase db;

        GetNotesOnDateTask(SQLiteDatabase db) {
            this.db = db;
        }

        @Override
        protected ArrayList<Note> doInBackground(String... strings) {
            ArrayList<Note> notes = new ArrayList<>();

            Cursor c = db.rawQuery("SELECT * FROM notes WHERE date = \"" + strings[0] + "\" ORDER BY time DESC", null);
            if (c != null && c.moveToFirst()){
                do {
                    notes.add(new Note(c.getInt(0), c.getString(1), c.getString(2), c.getString(3)));
                } while (c.moveToNext());
                c.close();
            }
//            db.close();
            return notes;
        }
    }

    private static class GetDatesTask extends AsyncTask<Void, Void, ArrayList<Calendar>>{
        SQLiteDatabase db;

        GetDatesTask(SQLiteDatabase db) {
            this.db = db;
        }

        @Override
        protected ArrayList<Calendar> doInBackground(Void... voids) {
            ArrayList<String> dates = new ArrayList<>();

            Cursor c = db.rawQuery("SELECT date FROM notes GROUP BY date ORDER BY date", null);
            if (c != null && c.moveToFirst()) {
                do {
                    dates.add(c.getString(0));
                } while (c.moveToNext());
                c.close();
            }
//            db.close();

            ArrayList<Calendar> datesForCalendar = new ArrayList<>();
            for (int i = 0; i < dates.size(); i++){
                String spliter = "-";
                String[] subStr;
                subStr = dates.get(i).split(spliter);
                Log.d(LOG_TAG, subStr[0] + " " + subStr[1] + " " + subStr[2]);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(subStr[0]),
                        Integer.parseInt(subStr[1]) - 1,
                        Integer.parseInt(subStr[2]));
                datesForCalendar.add(calendar);
            }
            return datesForCalendar;
        }
    }
}
