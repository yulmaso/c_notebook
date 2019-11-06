package com.example.notebook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.notebook.POJO.Hashtag;
import com.example.notebook.activities.other.HashtagsActivity;
import com.example.notebook.activities.other.MapActivity;
import com.example.notebook.activities.other.SettingsActivity;
import com.example.notebook.db.DBHelper;
import com.example.notebook.adapters.NotesAdapter;
import com.example.notebook.POJO.Note;
import com.example.notebook.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.example.notebook.util.Constants.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static boolean allNotesScreen;

    private DBHelper dbHelper;
    private NotesAdapter notesAdapter;
    private Intent note_intent;

    private ArrayList<Hashtag> oldHashtags;

    private RecyclerView list_rv;
    private CalendarView calendar;
    private FloatingActionButton fab;
    private LinearLayout list_layout;
    private TextView textView;
    private LinearLayout.LayoutParams lParams;
    private Spinner spinner;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    private int tv_id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(this);
        if (!dbHelper.checkDataBase()) dbHelper.updateDataBase();

        note_intent = new Intent(this, NoteActivity.class);
        list_layout = findViewById(R.id.list_layout);

        allNotesScreen = false;

        initCalendarView();
        initRecyclerView();
        initActionBar();
        initTextView();
        initToolbarButton();
        initNavView();

        refreshNotes();
        refreshCalendar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(this, "There are no settings in this app yet :(", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                break;
            case R.id.nav_hashtags:
                intent = new Intent(this, HashtagsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_map:
                intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            ArrayList<Hashtag> hashtags = arguments.getParcelableArrayList("Hashtags");
            Log.d(LOG_TAG, "Text: " + note.getText());
            switch (requestCode){
                case REQUEST_CODE_NEWNOTE:{
                    if (!note.getText().equals("")) {
                        dbHelper.newNote(note);
                        try {
                            Log.d(LOG_TAG, "On hashtags write");
                            dbHelper.writeTags(hashtags);
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
                            Log.d(LOG_TAG, "On hashtags write");
                            dbHelper.writeTags(hashtags);
                        } catch (NullPointerException e){
                            Log.d(LOG_TAG, e.getMessage());
                        }
                    }
                    break;
                }
            }
            refreshNotes();
            refreshCalendar();
        }
        if (resultCode == RESULT_DELETE){
            int id = data.getIntExtra("ID", -1);
            if (id != -1){
                dbHelper.deleteNote(id);
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
                oldHashtags = dbHelper.getSpecificHashtags(note.getId());
                note_intent.putExtra("Note", note);
                startActivityForResult(note_intent, REQUEST_CODE_UPDATENOTE);
            }
        };
        notesAdapter = new NotesAdapter(onNoteClickListener, this);
        list_rv.setAdapter(notesAdapter);
    }

    private void initActionBar(){
        fab = findViewById(R.id.newNoteButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date;
                if (allNotesScreen){
                    date = dateFormat.format(Calendar.getInstance().getTime());
                } else {
                    date = dateFormat.format(calendar.getFirstSelectedDate().getTime());
                }
                Note note = new Note("", date, "");
                note_intent.putExtra("Note", note);
                startActivityForResult(note_intent, REQUEST_CODE_NEWNOTE);
            }
        });
    }

    /**inits text view that's shown instead of recycler view when there are no items to show*/
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
        ArrayList<Note> notes;
        if (allNotesScreen) {
           notes = dbHelper.getAllNotes();
        } else {
           notes = dbHelper.getNotesOnDate(dateFormat.format(calendar.getFirstSelectedDate().getTime()));
        }
        if (notes != null && notes.size() == 0){
            if (list_layout.findViewById(tv_id) == null){
                list_layout.addView(textView, lParams);
                textView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation_fall_down));
            }
        } else {
            list_layout.removeView(findViewById(tv_id));
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

    /**initialises button on the toolbar, that changes the view of the activity between
     'calendar view' and 'all notes', and animations*/
    private void initToolbarButton(){
        HashMap<Boolean, String> stateNames = new HashMap<Boolean, String>();
        stateNames.put(true, "All notes");
        stateNames.put(false, "Calendar");

        Button button = findViewById(R.id.main_appearance_switch_btn);
        ImageView pic = findViewById(R.id.pic);
        button.setText(stateNames.get(allNotesScreen));

        Animation animationAppear = AnimationUtils.loadAnimation(this, R.anim.animation_appear);
        Animation animationDisappear = AnimationUtils.loadAnimation(this, R.anim.animation_disappear);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.animation_rotate);

        animationAppear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                notesAdapter.clearItems();
                calendar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                refreshNotes();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        animationDisappear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                notesAdapter.clearItems();
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                calendar.setVisibility(View.GONE);
                refreshNotes();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allNotesScreen){
                    allNotesScreen = false;
                    button.setText(stateNames.get(allNotesScreen));
                    pic.startAnimation(rotate);
                    calendar.startAnimation(animationAppear);
                } else {
                    allNotesScreen = true;
                    button.setText(stateNames.get(allNotesScreen));
                    pic.startAnimation(rotate);
                    calendar.startAnimation(animationDisappear);
                }
            }
        });
    }

    private void initNavView(){
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
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
