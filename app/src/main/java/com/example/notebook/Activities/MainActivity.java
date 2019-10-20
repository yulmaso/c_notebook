package com.example.notebook.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.notebook.DBclasses.DBHelper;
import com.example.notebook.Adapters.NotesAdapter;
import com.example.notebook.POJO.Note;
import com.example.notebook.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.notebook.OtherStuff.Constants.*;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private NotesAdapter notesAdapter;
    private Intent intent;
    private RecyclerView list_rv;
    private CalendarView calendar;
    private FloatingActionButton fab;
    private LinearLayout layout_content_main;

    private TextView textView;
    private LinearLayout.LayoutParams lParams;
    private int tv_id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        dbHelper = new DBHelper(this);
        if (!dbHelper.checkDataBase()) dbHelper.updateDataBase();

        intent = new Intent(this, NoteActivity.class);
        layout_content_main = findViewById(R.id.layout_content_main);

        initCalendarView();
        initRecyclerView();
        initActionBar();
        initTextView();

        refreshNotes();
        refreshCalendar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Animation animation = AnimationUtils.makeOutAnimation(this, true);
            calendar.startAnimation(animation);
            LinearLayout layout = findViewById(R.id.layout_content_main);
            layout.removeView(calendar);
            return true;
        }
        if (id == R.id.action_refresh) {
            refreshNotes();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult mainActivity");
        Log.d(LOG_TAG, "Result code: " + resultCode);
        Log.d(LOG_TAG, "Request code: " + requestCode);
        if (resultCode == RESULT_OK){
            Bundle arguments = data.getExtras();
            Note note = (Note) arguments.getSerializable("Note");
            Log.d(LOG_TAG, "Text: " + note.getText());
            switch (requestCode){
                case REQUEST_CODE_NEWNOTE:{
                    if (!note.getText().equals("")) {
                        dbHelper.newNote(note);
                    }
                    break;
                }
                case REQUEST_CODE_UPDATENOTE:{
                    if (note.getText().equals("")) {
                        dbHelper.deleteNote(note.getId());
                    } else {
                        dbHelper.updateNote(note);
                    }
                    break;
                }
            }
            refreshNotes();
            refreshCalendar();
        }
    }

    private void initCalendarView(){
        calendar = findViewById(R.id.calendar);
        calendar.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar date = eventDay.getCalendar();
                try {
                    calendar.setDate(date);
                } catch (OutOfDateRangeException e){
                    Log.d(LOG_TAG, e.getMessage());
                }
                refreshNotes();
            }
        });
    }

    private void initRecyclerView(){
        list_rv = findViewById(R.id.list_rv);
        list_rv.setLayoutManager(new LinearLayoutManager(this));
        NotesAdapter.OnNoteClickListener onNoteClickListener = new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(Note note) {
                intent.putExtra("Note", note);
                startActivityForResult(intent, REQUEST_CODE_UPDATENOTE);
            }
        };
        notesAdapter = new NotesAdapter(onNoteClickListener);
        list_rv.setAdapter(notesAdapter);
    }

    private void initActionBar(){
        fab = findViewById(R.id.newNoteButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note note = new Note("", dateFormat.format(calendar.getFirstSelectedDate().getTime()), "");
                intent.putExtra("Note", note);
                startActivityForResult(intent, REQUEST_CODE_NEWNOTE);
            }
        });
    }

    //inits text view that's shown instead of recycler view when there are no items to show
    private void initTextView(){
        textView = new TextView(this);
        textView.setText(R.string.text_view_instead_of_list);
        textView.setId(tv_id);
        lParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParams.gravity = Gravity.CENTER;
    }

    private void refreshNotes(){
        notesAdapter.clearItems();
        ArrayList<Note> notes = dbHelper.getNotesOnDate(dateFormat.format(calendar.getFirstSelectedDate().getTime()));
        if (notes != null && notes.size() == 0){
            if (layout_content_main.findViewById(tv_id) == null) layout_content_main.addView(textView, lParams);
        } else {
            layout_content_main.removeView(findViewById(tv_id));
            notesAdapter.setItems(notes);
        }
    }

    private void refreshCalendar(){
        List<Calendar> dates = dbHelper.getDates();
        List<EventDay> events = new ArrayList<>();
        for (Calendar date : dates) {
            Log.d(LOG_TAG, date.getTime().toString());
            events.add(new EventDay(date, R.drawable.ic_note_black_24dp));
        }
        calendar.setEvents(events);
    }
}
