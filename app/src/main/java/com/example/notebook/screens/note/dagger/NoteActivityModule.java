package com.example.notebook.screens.note.dagger;

import com.example.notebook.base.dagger.ActivityModule;
import com.example.notebook.storage.DBHelper;
import com.example.notebook.screens.main.dagger.MainActivityScope;
import com.example.notebook.screens.note.NoteActivityPresenter;
import com.example.notebook.screens.note.NoteContract;

import dagger.Module;
import dagger.Provides;

@Module
public class NoteActivityModule implements ActivityModule {

    @NoteActivityScope
    @Provides
    NoteContract.Presenter provideNoteActivityPresenter(DBHelper dbHelper){
        return new NoteActivityPresenter(dbHelper);
    }
}
