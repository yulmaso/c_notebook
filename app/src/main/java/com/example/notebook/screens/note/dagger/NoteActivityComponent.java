package com.example.notebook.screens.note.dagger;

import com.example.notebook.base.dagger.ActivityComponent;
import com.example.notebook.base.dagger.ActivityComponentBuilder;
import com.example.notebook.screens.note.NoteActivity;

import dagger.Subcomponent;

@NoteActivityScope
@Subcomponent(modules = {NoteActivityModule.class})
public interface NoteActivityComponent extends ActivityComponent<NoteActivity> {
    @Subcomponent.Builder
    interface Builder extends ActivityComponentBuilder<NoteActivityComponent, NoteActivityModule> {

    }
}
