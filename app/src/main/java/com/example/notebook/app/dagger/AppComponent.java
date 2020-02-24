package com.example.notebook.app.dagger;

import com.example.notebook.app.ComponentsHolder;
import com.example.notebook.storage.DBHelper;

import dagger.Component;

@AppScope
@Component(modules = {AppModule.class, AppSubComponentsModule.class})
public interface AppComponent {
    void injectComponentsHolder(ComponentsHolder componentsHolder);
    DBHelper getDBHelper();
}
