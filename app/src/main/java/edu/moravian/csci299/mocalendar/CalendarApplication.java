package edu.moravian.csci299.mocalendar;

import android.app.Application;

public class CalendarApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the repository with this application as the context
        EventRepository.initialize(this);
    }
}
