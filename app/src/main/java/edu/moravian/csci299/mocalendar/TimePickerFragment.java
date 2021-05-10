package edu.moravian.csci299.mocalendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.Objects;

/**
 * A fragment that acts as a popup window for picking a time. Any fragment that
 * uses this must implement the Callbacks interface defined here and set the
 * target fragment before showing it.
 *
 * HINTS: use the DatePickerFragment as inspiration for completing this one.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    /** The name of the argument for the start time (a boolean) */
    private static final String ARG_IS_START_TIME = "is_start_time";

    /** The name of the argument for the time (a Date object) */
    private static final String ARG_TIME = "time";

    /**
     * Callbacks interface for telling the EventFragment which time was selected
     */
    public interface Callbacks {
        void onTimeSelected(boolean startTime, Date date);
    }

    /**
     * Create a new instance of the time picking fragment dialog.
     * @param isStartTime whether this is picking a start or end time, this has
     *                    no influence on this picker but is used in when
     *                    calling the callback method
     * @param time the time to initially display in the picker
     * @return a new TimePickerFragment instance
     */
    public static TimePickerFragment newInstance(boolean isStartTime, Date time) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_START_TIME, isStartTime);
        args.putSerializable(ARG_TIME, time);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * When the dialog is created, we need to create the appropriate picker and
     * set its initial information as appropriate.
     * @param savedInstanceState not used
     * @return the Dialog to be shown
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = DateUtils.useDateOrNow((Date) Objects.requireNonNull(getArguments()).getSerializable(ARG_TIME));
        int[] time = DateUtils.getHourMinute(date);
        return new TimePickerDialog(requireContext(), this, time[0], time[1], false);
    }

    /**
     * When the time is set, we call the callbacks method
     * @param view Dialog view
     * @param hourOfDay Hour selected
     * @param minute Minute selected
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        ((Callbacks) Objects.requireNonNull(getTargetFragment()))
                .onTimeSelected((Boolean) Objects.requireNonNull(getArguments()).getSerializable(ARG_IS_START_TIME),
                        DateUtils.getTime(hourOfDay, minute));
    }

}
