package edu.moravian.csci299.mocalendar;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EventRepository {
    private final CalendarDao calendarDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private EventRepository(Context context) {
        AppDatabase database = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                "event_database").build();
        calendarDao = database.calendarDao();
    }

    // The public methods that simply call the DAO methods
    public LiveData<List<Event>> getAllEvents() { return calendarDao.getAllEvents(); }
    public LiveData<Event> getEventById(UUID id) { return calendarDao.getEventById(id); }
    public LiveData<List<Event>> getEventsBetween(Date start, Date end) { 
        return calendarDao.getEventsBetween(start, end); 
    }
    public LiveData<List<Event>> getEventsOnDay(Date date) { return calendarDao.getEventsOnDay(date); }

    // Insert, update, and remove methods
    public void addEvent(Event event) {
        executor.execute(() -> {
            calendarDao.addEvent(event);
        });
    }
    public void updateEvent(Event event) {
        executor.execute(() -> {
            calendarDao.updateEvent(event);
        });
    }
    public void removeEvent(Event event) {
        executor.execute(() -> {
            calendarDao.removeEvent(event);
        });
    }

    // The single instance of the repository
    private static EventRepository INSTANCE;
    public static EventRepository get() {
        if (INSTANCE == null) { throw new IllegalStateException("EventRepository must be initialized"); }
        return INSTANCE;
    }
    public static void initialize(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new EventRepository(context);
        }
    }
}
