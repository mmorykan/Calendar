package edu.moravian.csci299.mocalendar;

import androidx.room.TypeConverter;

import java.util.Date;
import java.util.UUID;

/**
 * Converts a Date to/from a Long, a UUID to/from a String,
 * and an EventType to/from a String.
 */
public class EventTypeConverter {

    @TypeConverter
    public Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public Date toDate(Long ms) {
        return ms == null ? null : new Date(ms);
    }

    @TypeConverter
    public String fromUUID(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }

    @TypeConverter
    public UUID toUUID(String uuid) {
        return uuid == null ? null : UUID.fromString(uuid);
    }

    @TypeConverter
    public String fromEventType(EventType eventType) {
        return eventType == null ? null : eventType.name();
    }

    @TypeConverter
    public EventType toEventType(String eventType) {
        return eventType == null ? null : EventType.valueOf(eventType);
    }

}
