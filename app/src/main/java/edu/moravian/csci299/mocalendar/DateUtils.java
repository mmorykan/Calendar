package edu.moravian.csci299.mocalendar;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * This class provides a ton a static utility functions for working with Date
 * objects. All of these are completed and working, but you will likely need to
 * use most of them so read them over.
 *
 * In Java, Date objects actually contain information about the year, month,
 * day, hour, minute, and second. This makes talking about them very confusing
 * since saying "Date" could refer to an object that is just a date (year,
 * month, and day), an object that is just a time (hours, minutes, seconds), or
 * both. In the documentation for this function, capitalized Date refers to the
 * Java class and not specific date or time. Lowercase date refers to just a
 * date (year, month, and day). The phrase date/time refers to having/using
 * both the date and time of a Date object.
 */
public class DateUtils {
    /**
     * Formatter to convert Date objects to textual dates like "Thursday April 1, 2021". Includes
     * the weekday. Ignores any time.
     */
    public static final SimpleDateFormat fullDateFormat = new SimpleDateFormat("EEEE MMMM d, yyyy", Locale.US);

    /**
     * Formatter to convert Date objects to textual dates like "April 1, 2021". Does not include the
     * weekday. Ignores any time.
     */
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);

    /**
     * Formatter for converting Date objects to textual times like "3:42 pm". Ignores any date.
     */
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);

    /**
     * Returns the given date/time or the current date/time if the given Date is null.
     * @param date the date/time to return (or null to return the current date/time)
     * @return a date/time, either the one given or the current date/time
     */
    @NonNull
    public static Date useDateOrNow(Date date) {
        return date == null ? new Date() : date;
    }

    /**
     * Gets a Date object for the given year, month, and day. The time is at
     * the start of that day (i.e. midnight).
     * @param year the year (e.g. 2021)
     * @param month the month (1-12)
     * @param dayOfMonth the day of that month (1-31)
     * @return a Date object for midnight at the start of that day
     */
    public static Date getDate(int year, int month, int dayOfMonth) {
        return new GregorianCalendar(year, month, dayOfMonth).getTime();
    }

    /**
     * Gets the year, month, and day from a Date object. The time is ignored.
     * @param date the Date object to extract information from
     * @return an array of the year, month, and day from that Date
     */
    public static int[] getYearMonthDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return new int[]{cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)};
    }

    /**
     * Gets a Date object for the given hour and minute. The time is put on an
     * arbitrary day.
     * @param hour the hour (0-23)
     * @param minute the minute (0-59)
     * @return a Date object for the given time on an arbitrary day
     */
    public static Date getTime(int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return cal.getTime();
    }

    /**
     * Gets the hour and minute from a Date object. The date is ignored.
     * @param date the Date object to extract information from
     * @return an array of the hour and minute from that Date
     */
    public static int[] getHourMinute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return new int[]{cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE)};
    }

    /**
     * Returns a new Date that has the date of the first argument and the time
     * of the second argument. The time on the date is ignored and the date on
     * the time is ignored.
     * @param date the Date to use for the date portion
     * @param time the Date to use for the time portion
     * @return a Date that is the combination of the arguments given
     */
    @NonNull
    public static Date combineDateAndTime(Date date, Date time) {
        Calendar calDate = Calendar.getInstance(), calTime = Calendar.getInstance();
        calDate.setTime(date);
        calTime.setTime(time);
        return new GregorianCalendar(
                calDate.get(Calendar.YEAR),
                calDate.get(Calendar.MONTH),
                calDate.get(Calendar.DAY_OF_MONTH),
                calTime.get(Calendar.HOUR_OF_DAY),
                calTime.get(Calendar.MINUTE),
                calTime.get(Calendar.SECOND)
                ).getTime();
    }

    /**
     * Gets the new end Date based on how the start time changed, keeping the
     * end time the same amount of time after the start that it was originally.
     *
     * @param origStart the original start Date
     * @param newStart the new start Date
     * @param origEnd the original end Date
     * @return the new end Date to keep the amount of time between start and
     *         end the same
     */
    public static Date getNewEndTime(Date origStart, Date newStart, Date origEnd) {
        return new Date(newStart.getTime() + origEnd.getTime() - origStart.getTime());
    }

    /**
     * Fixes the end time so that it comes after the given start time and at
     * most 24 hours after it. If the end time comes before the start time then
     * it is moved to the next day but at the same time. If it after the start
     * time but by more than 24 hours it keeps the same time but its date is
     * changed to the same day as the start day.
     *
     * @param start the reference Date to determine how to move the end Date
     * @param end the Date to adjust to make sure comes after start by at most 24 hours
     * @return the adjusted end Date (or possibly the same end Date if no adjustment needed)
     */
    public static Date fixEndTime(Date start, Date end) {
        end = combineDateAndTime(start, end);
        if (end.before(start)) {
            // move the end date to the day after start but at the same time
            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
            return cal.getTime();
        } else {
            return end;
        }
    }

    /**
     * Get the textual representation of a date including the day of the week like
     * "Thursday April 1, 2021".
     * @param date the Date to get the information from
     * @return the String like "Tuesday April 1, 2021"
     */
    public static String toFullDateString(Date date) {
        return fullDateFormat.format(date.getTime());
    }

    /**
     * Get the textual representation of a date NOT including the day of the week like
     * "April 1, 2021".
     * @param date the Date to get the information from
     * @return the String like "April 1, 2021"
     */
    public static String toDateString(Date date) {
        return dateFormat.format(date.getTime());
    }

    /**
     * Get the textual representation of the time like "3:42 pm".
     * @param date the Date to get the information from
     * @return the String like "3:42 pm"
     */
    public static String toTimeString(Date date) {
        return timeFormat.format(date.getTime());
    }
}
