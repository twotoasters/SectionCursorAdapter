package com.twotoasters.sectioncursoradaptersample;

import com.activeandroid.app.Application;

public class SampleApp extends Application {

    private static SampleApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static SampleApp getInstance() {
        return instance;
    }
}
