package com.example.notebook.screens.main.dagger;

import com.example.notebook.base.dagger.ActivityComponent;
import com.example.notebook.base.dagger.ActivityComponentBuilder;
import com.example.notebook.screens.main.MainActivity;

import dagger.Subcomponent;

@MainActivityScope
@Subcomponent(modules = MainActivityModule.class)
public interface MainActivityComponent extends ActivityComponent<MainActivity> {
    @Subcomponent.Builder
    interface Builder extends ActivityComponentBuilder<MainActivityComponent, MainActivityModule>{

    }
}
