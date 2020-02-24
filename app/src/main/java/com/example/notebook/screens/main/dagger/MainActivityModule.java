package com.example.notebook.screens.main.dagger;

import com.example.notebook.base.dagger.ActivityModule;
import com.example.notebook.storage.DBHelper;
import com.example.notebook.screens.main.MainContract;
import com.example.notebook.screens.main.MainActivityPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule implements ActivityModule {

    @MainActivityScope
    @Provides
    MainContract.Presenter provideMainActivityPresenter(DBHelper dbHelper){
        return new MainActivityPresenter(dbHelper);
    }
}
