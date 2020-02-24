package com.example.notebook.screens.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notebook.POJO.Hashtag;
import com.example.notebook.adapters.HashtaBarAdapter;
import com.example.notebook.app.App;
import com.example.notebook.screens.main.dagger.MainActivityModule;
import com.example.notebook.screens.note.dagger.NoteActivityModule;
import com.example.notebook.utils.DeleteDialogFragment;
import com.example.notebook.POJO.Note;
import com.example.notebook.R;
import com.example.notebook.utils.NoteEditText;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.notebook.storage.Constants.LOG_TAG;
import static com.example.notebook.storage.Constants.RESULT_DELETE;
import static com.example.notebook.storage.Constants.timeToWriteFormat;

public class NoteActivity extends AppCompatActivity implements NoteContract.View {

    @Inject
    NoteContract.Presenter presenter;

    private NoteEditText note_et;

    @BindView(R.id.note_date_tv)
    TextView note_date_tv;

    @BindView(R.id.note_time_tv)
    TextView note_time_tv;

    @BindView(R.id.hashtag_holder_layout_separator)
    View hashtags_holder_layout_separator;

    @BindView(R.id.scroll_et)
    ScrollView scroll_et;

    @BindView(R.id.hashtag_bar_rv)
    RecyclerView hashtag_bar_rv;

    private DeleteDialogFragment deleteDialogFragment;

    public static void startActivity(Context context, Note note){
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra("Note", note);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.bind(this);

        App.getApp(this).getComponentsHolder()
                .getActivityComponent(getClass(), new NoteActivityModule())
                .inject(this);

        presenter.attachView(this);
        presenter.viewIsReady();

        presenter.handleReceivedExtras(getIntent().getExtras());

        initElementsOfScreen();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            presenter.onBackButtonClick(getText());
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.delete_button)
    void onDeleteButtonClick(){
        deleteDialogFragment.show(getSupportFragmentManager(), "delete_dialog");
    }

    @OnClick(R.id.note_back_button)
    void onNoteBackButtonClick(){
        onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
    }

    @Override
    public String getText() {
        return note_et.getText().toString();
    }

    @Override
    public void setText(String text) {
        note_et.setText(text);
    }

    @Override
    public ArrayList<Hashtag> getHashtags() {
        return note_et.getHashtags();
    }

    @Override
    public void setSelection(int length) {
        note_et.setSelection(length);
    }

    public void deleteNote(){
        presenter.onDeleteButtonClick();
    }

    private void initElementsOfScreen(){
        deleteDialogFragment = new DeleteDialogFragment(this);

        //if it's new note
        if (presenter.getReceivedNote().getText().equals("")){ showKeyboard(); }

        note_date_tv.setText(presenter.getReceivedNote().getDate());

        String time = presenter.getReceivedNote().getTime();
        if (!time.equals("")) time = time.substring(0, time.length() - 3);
        note_time_tv.setText(time);
    }

    @Override
    public void initNoteEditText() {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lParams.setMargins(10, 10, 10, 10);
        NoteEditText.HashtagListener hashtagListener = new NoteEditText.HashtagListener() {
            @Override
            public void onEditHashtag() {
                presenter.onEditHashtag();
            }
        };
        note_et = new NoteEditText(this, presenter.getReceivedNote().getId(), hashtagListener);
        note_et.setBackgroundResource(R.drawable.background_transparent);
        scroll_et.addView(note_et, lParams);
    }

    @Override
    public void initHashtagsHolderLayout(ArrayList<Hashtag> hashtags) {
        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        hashtag_bar_rv.setLayoutManager(llm);
        HashtaBarAdapter adapter = new HashtaBarAdapter(this);
        hashtag_bar_rv.setAdapter(adapter);
        adapter.setItems(hashtags);

        hashtag_bar_rv.setVisibility(View.VISIBLE);
        hashtags_holder_layout_separator.setVisibility(View.VISIBLE);
    }

    @Override
    public void showKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void finishView() {
        finish();
    }
}
