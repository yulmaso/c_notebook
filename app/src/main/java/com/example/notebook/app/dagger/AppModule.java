package com.example.notebook.app.dagger;

import android.content.Context;

import com.example.notebook.storage.DBHelper;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @AppScope
    @Provides
    Context provideContext(){
        return context;
    }

    @AppScope
    @Provides
    DBHelper provideDBHelper(Context context){
        DBHelper dbHelper = new DBHelper(context);
        if (!dbHelper.checkDataBase()) dbHelper.updateDataBase();
        return dbHelper;
    }
}
