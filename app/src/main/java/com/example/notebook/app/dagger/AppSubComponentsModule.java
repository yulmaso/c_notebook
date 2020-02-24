package com.example.notebook.app.dagger;

import com.example.notebook.base.dagger.ActivityComponentBuilder;
import com.example.notebook.screens.main.MainActivity;
import com.example.notebook.screens.main.dagger.MainActivityComponent;
import com.example.notebook.screens.note.NoteActivity;
import com.example.notebook.screens.note.dagger.NoteActivityComponent;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = {MainActivityComponent.class, NoteActivityComponent.class})
public class AppSubComponentsModule {

    @Provides
    @IntoMap
    @ClassKey(MainActivity.class)
    ActivityComponentBuilder provideMainActivityBuilder(MainActivityComponent.Builder builder) {
        return builder;
    }

    @Provides
    @IntoMap
    @ClassKey(NoteActivity.class)
    ActivityComponentBuilder provideNoteActivityBuilder(NoteActivityComponent.Builder builder) {
        return builder;
    }
}
