package com.example.notebook.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.notebook.POJO.Geotag;
import com.example.notebook.POJO.Note;
import com.example.notebook.POJO.Hashtag;
import com.example.notebook.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.notebook.storage.Constants.LOG_TAG;

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
    private WriteHashtagsTask writeHashtagsTask;
    private GetAllHashtagsTask getAllHashtagsTask;
    private GetSpecificHashtagsTask getSpecificHashtagsTask;
    private DeleteHashtagsTask deleteHashtagsTask;
    private NewGeotagTask newGeotagTask;
    private UpdateGeotagTask updateGeotagTask;
    private GetAllGeotagsTask getAllGeotagsTask;
    private GetNotesOnHashtag getNotesOnHashtag;
    private GetNextNoteIDTask getNextNoteIDTask;

    public DBHelper (Context context){
        super(context, DB_NAME, null, 2);

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
        newNoteTask = new NewNoteTask(getWritableDatabase(), this);
        newNoteTask.execute(note);
    }

    public void updateNote(Note note){
        updateNoteTask = new UpdateNoteTask(getWritableDatabase(), this);
        updateNoteTask.execute(note);
    }

    public void deleteNote(int id){
        deleteNoteTask = new DeleteNoteTask(getWritableDatabase(), this);
        deleteNoteTask.execute(id);
    }

    public void writeHashtags(List<Hashtag> tags){
        writeHashtagsTask = new WriteHashtagsTask(getWritableDatabase(), this);
        writeHashtagsTask.execute(tags);
    }

    public ArrayList<Hashtag> getAllTags(){
        getAllHashtagsTask = new GetAllHashtagsTask(getReadableDatabase(), this);
        getAllHashtagsTask.execute();
        try {
            return getAllHashtagsTask.get();
        } catch(InterruptedException e){
            Log.d(LOG_TAG, e.getMessage());
        } catch (ExecutionException e){
            Log.d(LOG_TAG, e.getMessage());
        }
        return null;
    }

    public ArrayList<Note> getNotesOnHashtag(Hashtag hashtag){
        getNotesOnHashtag = new GetNotesOnHashtag(getReadableDatabase(), this);
        getNotesOnHashtag.execute(hashtag);
        try {
            return getNotesOnHashtag.get();
        } catch(InterruptedException e){
            Log.d(LOG_TAG, e.getMessage());
        } catch (ExecutionException e){
            Log.d(LOG_TAG, e.getMessage());
        }
        return null;
    }

    public Integer getNextNoteID(){
        getNextNoteIDTask = new GetNextNoteIDTask(getReadableDatabase(), this);
        getNextNoteIDTask.execute();
        try {
            return getNextNoteIDTask.get();
        } catch(InterruptedException e){
            Log.d(LOG_TAG, e.getMessage());
        } catch (ExecutionException e){
            Log.d(LOG_TAG, e.getMessage());
        }
        return null;
    }

    public ArrayList<Hashtag> getSpecificHashtags(int note_id){
        getSpecificHashtagsTask = new GetSpecificHashtagsTask(getReadableDatabase(), this);
        getSpecificHashtagsTask.execute(note_id);
        try {
            return getSpecificHashtagsTask.get();
        } catch(InterruptedException e){
            Log.d(LOG_TAG, e.getMessage());
        } catch (ExecutionException e){
            Log.d(LOG_TAG, e.getMessage());
        }
        return null;
    }

    public void deleteHashtags(ArrayList<Hashtag> tags){
        deleteHashtagsTask = new DeleteHashtagsTask(getWritableDatabase(), this);
        deleteHashtagsTask.execute(tags);
    }

    public Note getNote(int id){
        getNoteTask = new GetNoteTask(getReadableDatabase(), this);
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
        getAllNotesTask = new GetAllNotesTask(getReadableDatabase(), this);
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
        getNotesOnDateTask = new GetNotesOnDateTask(getReadableDatabase(), this);
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
        getDatesTask = new GetDatesTask(getReadableDatabase(), this);
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

    public void newGeotag(Geotag geotag){
        newGeotagTask = new NewGeotagTask(getWritableDatabase(), this);
        newGeotagTask.execute(geotag);
    }

    public void updateGeotag(Geotag geotag){
        updateGeotagTask = new UpdateGeotagTask(getWritableDatabase(), this);
        updateGeotagTask.execute(geotag);
    }

    public ArrayList<Geotag> getAllGeotags(){
        getAllGeotagsTask = new GetAllGeotagsTask(getReadableDatabase(), this);
        getAllGeotagsTask.execute();
        try {
            return getAllGeotagsTask.get();
        } catch(InterruptedException e){
            Log.d(LOG_TAG, e.getMessage());
        } catch (ExecutionException e){
            Log.d(LOG_TAG, e.getMessage());
        }
        return null;
    }

    private static class GetNextNoteIDTask extends AsyncTask<Void, Void, Integer>{
        SQLiteDatabase db;
        WeakReference<DBHelper> weakReference;

        GetNextNoteIDTask(SQLiteDatabase db, DBHelper dbHelper){
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int result = 1;
            Cursor c = db.rawQuery("SELECT id FROM notes ORDER BY id DESC", null);
            if (c != null && c.moveToFirst()) {
                result = c.getInt(0) + 1;
            }
            return result;
        }
    }

    private static class NewNoteTask extends AsyncTask<Note, Void, Void>{
        SQLiteDatabase db;
        WeakReference<DBHelper> weakReference;

        NewNoteTask(SQLiteDatabase db, DBHelper dbHelper){
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
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
        WeakReference<DBHelper> weakReference;

        UpdateNoteTask(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
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
        WeakReference<DBHelper> weakReference;

        DeleteNoteTask(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
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
        WeakReference<DBHelper> weakReference;

        GetNoteTask(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
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
        WeakReference<DBHelper> weakReference;

        GetAllNotesTask(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
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
        WeakReference<DBHelper> weakReference;

        GetNotesOnDateTask(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
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

    private static class GetNotesOnHashtag extends AsyncTask<Hashtag, Void, ArrayList<Note>>{
        SQLiteDatabase db;
        WeakReference<DBHelper> weakReference;

        GetNotesOnHashtag(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
        }
        @Override
        protected ArrayList<Note> doInBackground(Hashtag... hashtags) {
            ArrayList<Note> notes = new ArrayList<>();
            ArrayList<Hashtag> htags = new ArrayList<>();

            Cursor c = db.rawQuery("SELECT * FROM hashtags WHERE hashtag = \""+ hashtags[0].getHashtag() +"\"", null);
            if (c != null && c.moveToFirst()){
                do {
                    htags.add(new Hashtag(c.getInt(0), c.getString(1)));
                } while (c.moveToNext());
                c.close();
            }
            for (int i = 0; i < htags.size(); i++) {
                c = db.rawQuery("SELECT * FROM notes WHERE id = \"" + htags.get(i).getId_note() + "\" ORDER BY time DESC", null);
                if (c != null && c.moveToFirst()) {
                    do {
                        Log.d(LOG_TAG, "ON GETNOTESONHASHTAGTASK");
                        notes.add(new Note(c.getInt(0), c.getString(1), c.getString(2), c.getString(3)));
                    } while (c.moveToNext());
                    c.close();
                }
            }
            return notes;
        }
    }

    private static class GetDatesTask extends AsyncTask<Void, Void, ArrayList<Calendar>>{
        SQLiteDatabase db;
        WeakReference<DBHelper> weakReference;

        GetDatesTask(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
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
//                Log.d(LOG_TAG, subStr[0] + " " + subStr[1] + " " + subStr[2]);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(subStr[0]),
                        Integer.parseInt(subStr[1]) - 1,
                        Integer.parseInt(subStr[2]));
                datesForCalendar.add(calendar);
            }
            return datesForCalendar;
        }
    }

    private static class WriteHashtagsTask extends AsyncTask<List<Hashtag>, Void, Void>{
        SQLiteDatabase db;
        WeakReference<DBHelper> weakReference;

        WriteHashtagsTask(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
        }

        @Override
        protected Void doInBackground(List<Hashtag>... lists) {
            for (int i = 0; i < lists[0].size(); i++){
                ContentValues cv = new ContentValues();
                cv.put("hashtag", lists[0].get(i).getHashtag());
                cv.put("id_note", lists[0].get(i).getId_note());
//                Log.d(LOG_TAG, lists[0].get(i).getHashtag());
//                Log.d(LOG_TAG, "WRITE HT TASK: " + lists[0].get(i).getHashtag());
                try {
                    db.insert("hashtags", null, cv);
                } catch (Exception e){
                    Log.d(LOG_TAG, e.getMessage());
                }
            }
            return null;
        }
    }

    private static class GetAllHashtagsTask extends AsyncTask<Void, Void, ArrayList<Hashtag>>{
        SQLiteDatabase db;
        WeakReference<DBHelper> weakReference;

        public GetAllHashtagsTask(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
        }

        @Override
        protected ArrayList<Hashtag> doInBackground(Void... voids) {
            ArrayList<Hashtag> hashtags = new ArrayList<>();
            Log.d(LOG_TAG, "on GetAllHashtagsTask");

            Cursor c = db.rawQuery("SELECT hashtag FROM hashtags GROUP BY hashtag ORDER BY hashtag", null);
            if (c != null && c.moveToFirst()){
                do {
                    Log.d(LOG_TAG, "Reading hashtag: " + c.getString(0));
                    Hashtag hashtag = new Hashtag(c.getString(0));
                    hashtags.add(hashtag);
                } while (c.moveToNext());
                c.close();
            }

            return hashtags;
        }
    }

    private static class GetSpecificHashtagsTask extends AsyncTask<Integer, Void, ArrayList<Hashtag>>{
        SQLiteDatabase db;
        WeakReference<DBHelper> weakReference;

        public GetSpecificHashtagsTask(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
        }

        @Override
        protected ArrayList<Hashtag> doInBackground(Integer... integers) {
            ArrayList<Hashtag> hashtags = new ArrayList<>();

            Cursor c = db.rawQuery("SELECT * FROM hashtags WHERE id_note = " + integers[0].toString(), null);
            if (c != null && c.moveToFirst()){
                do {
                    //TODO: берет только со второго хэштега
                    Log.d(LOG_TAG, "onSpecificHashtagsTask, hashtag = " + c.getString(1));
                    Hashtag hashtag = new Hashtag(integers[0], c.getString(1));
                    hashtags.add(hashtag);
                } while (c.moveToNext());
                c.close();
            }
            return hashtags;
        }
    }

    private static class DeleteHashtagsTask extends AsyncTask<ArrayList<Hashtag>, Void, Void>{
        SQLiteDatabase db;
        WeakReference<DBHelper> weakReference;

        public DeleteHashtagsTask(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
        }

        @Override
        protected Void doInBackground(ArrayList<Hashtag>... arrayLists) {
            for (int i = 0; i < arrayLists[0].size(); i++){
                Log.d(LOG_TAG, "DELETING HASHTAG = " + arrayLists[0].get(i).getHashtag() + ", WITH ID_NOTE = " + arrayLists[0].get(i).getId_note());
                String[] args = {arrayLists[0].get(i).getHashtag(), String.valueOf(arrayLists[0].get(i).getId_note())};
                db.delete("hashtags","hashtag = ? AND id_note = ?",args);
            }
            return null;
        }
    }

    private static class NewGeotagTask extends AsyncTask<Geotag, Void, Void>{
        SQLiteDatabase db;
        WeakReference<DBHelper> weakReference;

        public NewGeotagTask(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
        }

        @Override
        protected Void doInBackground(Geotag... geotags) {
            ContentValues cv = new ContentValues();
            cv.put("id_note", geotags[0].getId_note());
            cv.put("date", geotags[0].getDate());
            cv.put("time", geotags[0].getTime());
            cv.put("geolong", geotags[0].getGeolong());
            cv.put("geolat", geotags[0].getGeolat());

            Log.d(LOG_TAG, "writing geotag for id_note = " + geotags[0].getId_note());
            try {
                db.insert("geotags", null, cv);
            } catch (Exception e){
                Log.d(LOG_TAG, e.getMessage());
            }
            return null;
        }
    }

    private static class UpdateGeotagTask extends AsyncTask<Geotag, Void, Void>{
        SQLiteDatabase db;
        WeakReference<DBHelper> weakReference;

        public UpdateGeotagTask(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
        }

        @Override
        protected Void doInBackground(Geotag... geotags) {
            ContentValues cv = new ContentValues();
            cv.put("id_note", geotags[0].getId_note());
            cv.put("date", geotags[0].getDate());
            cv.put("time", geotags[0].getTime());
            cv.put("geolong", geotags[0].getGeolong());
            cv.put("geolat", geotags[0].getGeolat());
            db.replace("geotags", null, cv);
            return null;
        }
    }

    private static class GetAllGeotagsTask extends AsyncTask<Void, Void, ArrayList<Geotag>>{
        SQLiteDatabase db;
        WeakReference<DBHelper> weakReference;

        public GetAllGeotagsTask(SQLiteDatabase db, DBHelper dbHelper) {
            this.db = db;
            weakReference = new WeakReference<>(dbHelper);
        }

        @Override
        protected ArrayList<Geotag> doInBackground(Void... voids) {
            ArrayList<Geotag> geotags = new ArrayList<>();

            Cursor c = db.query("geotags", null, null, null, null, null, "date DESC, time DESC");
            if (c != null && c.moveToFirst()) {
                do {
                    geotags.add(new Geotag(c.getInt(0), c.getInt(1), c.getString(2),
                            c.getString(3), c.getFloat(4), c.getFloat(4)));
                } while (c.moveToNext());
                c.close();
            }
            return geotags;
        }
    }
}
