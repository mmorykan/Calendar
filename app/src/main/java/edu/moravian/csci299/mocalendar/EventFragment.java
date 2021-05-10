package edu.moravian.csci299.mocalendar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.UUID;

/**
 * The fragment for a single event. It allows editing all of the details of the event, either with
 * text edit boxes (for the name and description) or popup windows (for the date, start time,
 * time and type). The event is not updated in the database until the user leaves this fragment.
 */
public class EventFragment extends Fragment implements TextWatcher, DatePickerFragment.Callbacks,
        EventTypePickerFragment.Callbacks, TimePickerFragment.Callbacks {

    // fragment initialization parameters
    private static final String ARG_EVENT_ID = "event_id";

    // dialog fragment tags
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_EVENT_TYPE = "DialogEventType";

    // dialog fragment codes
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_EVENT_TYPE = 2;

    // argument once loaded from database
    private Event event;
    private TextView eventDate, eventStartTime, eventEndTime, till;
    private EditText eventName, eventDescription;
    private ImageView eventIcon;

    /**
     * Use this factory method to create a new instance of this fragment that
     * show the details for the given event.
     * @param event the event to show information about
     * @return a new instance of fragment EventFragment
     */
    public static EventFragment newInstance(Event event) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT_ID, event.id);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Upon creation load the data. Once the data is loaded, update the UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load the event and update the UI
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_EVENT_ID)) {
            UUID id = (UUID)arguments.getSerializable(ARG_EVENT_ID);
            EventRepository.get().getEventById(id).observe(this, event -> {
                this.event = event;
                updateUI();
            });
        }
    }

    /**
     * Create the view from the layout, save references to all of the important
     * views within in, then hook up the listeners.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View base = inflater.inflate(R.layout.fragment_event, container, false);

        // Find all the relevant event views
        eventName = base.findViewById(R.id.event_name);
        eventDescription = base.findViewById(R.id.description);
        eventDate = base.findViewById(R.id.event_date);
        eventStartTime = base.findViewById(R.id.start_time);
        eventEndTime = base.findViewById(R.id.date);
        eventIcon = base.findViewById(R.id.eventTypeIcon);
        till = base.findViewById(R.id.till);

        // Text listeners for the name and description of an event
        eventName.addTextChangedListener(this);
        eventDescription.addTextChangedListener(this);

        // Listener for choosing a date for an event
        eventDate.setOnClickListener(v -> {
            DatePickerFragment fragment = DatePickerFragment.newInstance(event.startTime);
            fragment.setTargetFragment(this, REQUEST_DATE);
            fragment.show(requireFragmentManager(), DIALOG_DATE);
        });

        // Listeners for choosing start and end times
        eventStartTime.setOnClickListener(v -> onClickTimePicker(true, event.startTime));
        eventEndTime.setOnClickListener(v -> onClickTimePicker(false, event.endTime));

        // Listener for choosing the type of event
        eventIcon.setOnClickListener(v -> {
            EventTypePickerFragment fragment = EventTypePickerFragment.newInstance(event.type);
            fragment.setTargetFragment(this, REQUEST_EVENT_TYPE);
            fragment.show(requireFragmentManager(), DIALOG_EVENT_TYPE);
        });

        // Return the base view
        return base;
    }

    /**
     * Time picker handler for when the time views are clicked
     * @param isStartTime Start or end time
     * @param time The time chosen
     */
    private void onClickTimePicker(boolean isStartTime, Date time) {
        TimePickerFragment fragment = TimePickerFragment.newInstance(isStartTime, time);
        fragment.setTargetFragment(this, REQUEST_TIME);
        fragment.show(requireFragmentManager(), DIALOG_TIME);
    }

    /** 
     * Updates the UI to match the event type.
     */
    private void updateUI() {
        eventName.setText(event.name);
        eventDescription.setText(event.description);
        eventDate.setText(DateUtils.toFullDateString(event.startTime));
        eventStartTime.setText(DateUtils.toTimeString(event.startTime));
        if (event.endTime != null) { // event has an end time
            eventEndTime.setText(DateUtils.toTimeString(event.endTime));
            till.setText(R.string.till);
        }
        eventIcon.setImageResource(event.type.iconResourceId);
    }

    /**
     * When an EditText updates we update the corresponding Event field. Need to register this
     * object with the EditText objects with addTextChangedListener(this).
     * @param s the editable object that just updated, equal to some EditText.getText() object
     */
    @Override
    public void afterTextChanged(Editable s) {
        if (event == null) return;  // Used for when rotating the phone while editing the EditTexts
        String str = s.toString();
        if (str.equals(eventName.getText().toString())) {
            event.name = eventName.getText().toString();
        } else if (str.equals(eventDescription.getText().toString())) {
            event.description = eventDescription.getText().toString();
        }
    }

    /** Required to be implemented but not needed. */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    /** Required to be implemented but not needed. */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    /**
     * Save the edits to the database when the fragment is stopped.
     */
    @Override
    public void onStop() {
        super.onStop();
        EventRepository.get().updateEvent(event);
    }

    /**
     * Make sure the event start time is the day of the date chosen and the time of the start time.
     * Make sure the event end time is not more than 24 hours after the start time
     * @param date the date that was picked
     */
    @Override
    public void onDateSelected(Date date) {
        event.startTime = DateUtils.combineDateAndTime(date, event.startTime);
        event.endTime = DateUtils.fixEndTime(event.startTime, event.endTime);
        updateUI();
    }

    /**
     * When the type of the event is selected
     * @param type the event type that was picked
     */
    @Override
    public void onTypeSelected(EventType type) {
        event.type = type;
        updateUI();
    }

    /** 
     * Sets the event's start or end time depending on the parameters provided. 
     * If this is a new starting time, end time is updated so the time between 
     start time and end time stays the same.
     * @param startTime true if time is a starting time, else time is end time
     * @param date new date to set the start or end time to
     */
    @Override
    public void onTimeSelected(boolean startTime, Date date) {
        if (startTime) { // the time selected is the starting time
            Date newStart = DateUtils.combineDateAndTime(event.startTime, date);
            // Update end time 
            event.endTime = DateUtils.getNewEndTime(event.startTime, newStart, event.endTime);
            event.startTime = newStart;
        } else { // the time selcted is the end time
            // Make sure end time is not before start time
            event.endTime = DateUtils.fixEndTime(event.startTime, date); 
        }
        updateUI();
    }
}
