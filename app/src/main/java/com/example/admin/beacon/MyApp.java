package com.example.admin.beacon;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by Admin on 1/27/2016.
 */
public class MyApp extends Application {


    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApp.context;
    }
}