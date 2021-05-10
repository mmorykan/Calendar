package edu.moravian.csci299.mocalendar;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import java.util.Date;
import java.util.Objects;

/**
 * A fragment that displays a calendar. When a day within the calendar is clicked, a callback method
 * is called to inform the hosting activity. This calendar auto-updates its arguments with the
 * last highlighted day so that when it is rotated the same day is still highlighted.
 *
 * NOTE: this is the easiest of the core fragments to complete
 */
public class CalendarFragment extends Fragment implements CalendarView.OnDateChangeListener {

    /**
     * The callbacks that can be called by this fragment on the hosting Activity.
     */
    public interface Callbacks {
        /**
         * Called whenever a day is changed on the calendar.
         * @param date the day clicked
         */
        void onDayChanged(Date date);
    }

    // fragment initialization parameters
    private static final String ARG_DATE = "date";

    // the hosting activity callbacks
    private Callbacks callbacks;

    /**
     * Use this factory method to create a new instance of this fragment that
     * highlights today initially.
     * @return a new instance of fragment CalendarFragment.
     */
    public static CalendarFragment newInstance() {
        return newInstance(new Date());
    }

    /**
     * Use this factory method to create a new instance of this fragment that
     * highlights the given day initially.
     * @param date the date to highlight initially.
     * @return a new instance of fragment CalendarFragment.
     */
    public static CalendarFragment newInstance(Date date) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create the view of this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The date to initially highlight
        Date date = DateUtils.useDateOrNow((Date) Objects.requireNonNull(getArguments()).getSerializable(ARG_DATE));

        // Inflate the layout for this fragment
        View base = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Setup the calendar
        CalendarView calendarView = base.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(this);
        calendarView.setDate(date.getTime());

        // Return the base view
        return base;
    }

    /**
     * Set the Callbacks
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    /**
     * Destroy the callbacks
     */
    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    /**
     * Executed when a day is chosen in the calendar
     * @param view The calendar view
     * @param year The year chosen
     * @param month The month chosen
     * @param dayOfMonth The day chosen
     */
    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        Date date = DateUtils.getDate(year, month, dayOfMonth);
        Objects.requireNonNull(getArguments()).putSerializable(ARG_DATE, date);
        callbacks.onDayChanged(date);
    }
}
