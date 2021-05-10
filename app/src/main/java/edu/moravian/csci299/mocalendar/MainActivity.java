package edu.moravian.csci299.mocalendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import java.util.Date;
/**
 * The main (and only) activity for the application that hosts all of the fragments.
 *
 * It starts out with a calendar and list fragment (if vertical then they are above/below each
 * other, if horizontal then they are left/right of each other). When a day is clicked in the
 * calendar, the list shows all events for that day.
 *
 * When an event is being edited/viewed (because it was clicked in the list or a new event is being
 * added) then the fragments are replaced with an event fragment which shows the details for a
 * specific event and allows editing.
 *
 * NOTE: This Activity is the bare-bones, empty, Activity. Work will be definitely needed in
 * onCreate() along with implementing some callbacks.
 * 
 * Authors: Mark Morykan and Jonah Beers
 */
public class MainActivity extends AppCompatActivity implements CalendarFragment.Callbacks,
        ListFragment.Callbacks {

    /** The Tags associated with the fragments */
    private static final String CALENDAR_TAG = "calendar";
    private static final String LIST_TAG = "list";

    private ListFragment listFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        Fragment currFragment = fm.findFragmentById(R.id.fragment_container);

        if (currFragment == null) {
            CalendarFragment calendarFragment = CalendarFragment.newInstance();
            listFragment = ListFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.fragment_container, calendarFragment, CALENDAR_TAG)
                    .add(R.id.fragment_container, listFragment, LIST_TAG).commit();
        } else {
            // Every rotation, need to find the list fragment 
            listFragment = (ListFragment) fm.findFragmentByTag(LIST_TAG);
        }
    }

    /**
     * Let the list fragment know what the selected date is
     * @param date the day clicked
     */
    @Override
    public void onDayChanged(Date date) {
        listFragment.setDay(date);
    }

    /**
     * Replace the fragments with an event fragment to create an event
     * @param event The event clicked on
     */
    @Override
    public void onEventClicked(Event event) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, EventFragment.newInstance(event))
            .addToBackStack(null)
            .commit();
    }
}
