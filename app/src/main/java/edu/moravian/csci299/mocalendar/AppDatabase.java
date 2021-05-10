package edu.moravian.csci299.mocalendar;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Our connection to the database of events for our app.
 */
@Database(entities = {Event.class}, version = 1)
@TypeConverters(EventTypeConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CalendarDao calendarDao();
}

