package com.twotoasters.sectioncursoradaptersample;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;

public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this, false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
