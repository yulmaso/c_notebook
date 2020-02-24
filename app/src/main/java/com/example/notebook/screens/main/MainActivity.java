package com.example.notebook.screens.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.notebook.app.App;
import com.example.notebook.screens.main.dagger.MainActivityModule;
import com.example.notebook.screens.toDo.HashtagsActivity;
import com.example.notebook.screens.toDo.MapActivity;
import com.example.notebook.screens.toDo.SettingsActivity;
import com.example.notebook.adapters.NotesAdapter;
import com.example.notebook.POJO.Note;
import com.example.notebook.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.notebook.storage.Constants.*;

public class MainActivity extends AppCompatActivity implements MainContract.View, NavigationView.OnNavigationItemSelectedListener{

    public static boolean allNotesScreen;

    @Inject
    MainContract.Presenter presenter;

    private NotesAdapter notesAdapter;

    private ArrayList<Hashtag> oldHashtags;

    @BindView(R.id.list_rv)
    RecyclerView list_rv;

    @BindView(R.id.calendar)
    CalendarView calendar;

    @BindView(R.id.newNoteButton)
    FloatingActionButton fab;

    @BindView(R.id.list_layout)
    LinearLayout list_layout;

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private TextView textView;
    private LinearLayout.LayoutParams lParams;

    private int tv_id = 1;

    public static void startActivity(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        App.getApp(this).getComponentsHolder()
                .getActivityComponent(getClass(), new MainActivityModule())
                .inject(this);

        presenter.attachView(this);

        presenter.viewIsReady();

        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.refreshNotes(allNotesScreen);
        presenter.refreshCalendar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    private void init(){
        setSupportActionBar(toolbar);
        allNotesScreen = false;

        initCalendarView();
        initRecyclerView();
        initActionBar();
        initTextView();
        initToolbarButton();
        initNavView();

        presenter.refreshNotes(allNotesScreen);
        presenter.refreshCalendar();
    }

    /*@Override
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
    }*/

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
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
            presenter.refreshCalendar();
        }
        if (resultCode == RESULT_DELETE){
            int id = data.getIntExtra("ID", -1);
            if (id != -1){
                dbHelper.deleteNote(id);
            }
            refreshNotes();
            presenter.refreshCalendar();
        }
    }*/

    @Override
    public Context getContext() {
        return this;
    }

    private void initCalendarView(){
        calendar.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                presenter.onCalendarDayClick(eventDay);
            }
        });
    }

    @Override
    public void setCalendarDate(Calendar date) {
        try {
            calendar.setDate(date);
        } catch (OutOfDateRangeException e){
            Log.d(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public void setCalendarEvents(List<EventDay> events) {
        calendar.setEvents(events);
    }

    @Override
    public String getCalendarSelectedDate() {
        return dateFormat.format(calendar.getFirstSelectedDate().getTime());
    }

    private void initRecyclerView(){
        list_rv.setLayoutManager(new LinearLayoutManager(this));
        NotesAdapter.OnNoteClickListener lambda = (note) ->
                presenter.onNoteListClick(note);
        notesAdapter = new NotesAdapter(lambda, this);
        list_rv.setAdapter(notesAdapter);
    }

    private void initActionBar(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onActionBarClick(allNotesScreen, view);
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

    @Override
    public void showTextView() {
        if (list_layout.findViewById(tv_id) == null) {
            notesAdapter.clearItems();
            list_layout.addView(textView, lParams);
            textView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation_fall_down));
        }
    }

    @Override
    public void removeTextView(ArrayList<Note> notes) {
        list_layout.removeView(findViewById(tv_id));
        notesAdapter.setItems(notes);
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
                presenter.refreshNotes(allNotesScreen);
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
                presenter.refreshNotes(allNotesScreen);
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                break;
            case R.id.nav_hashtags:
                HashtagsActivity.startActivity(this);
                break;
            case R.id.nav_map:
                MapActivity.startActivity(this);
                break;
            case R.id.nav_settings:
                SettingsActivity.startActivity(this);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
