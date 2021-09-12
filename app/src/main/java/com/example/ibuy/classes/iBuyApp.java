package com.example.ibuy.classes;

import android.app.Application;

import static net.ticherhaz.tarikhmasa.TarikhMasa.AndroidThreeTenBP;

public class iBuyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTenBP(this);
    }
}
