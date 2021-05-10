package edu.moravian.csci299.mocalendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.Objects;

/**
 * A fragment that acts as a popup window for picking a date. Any fragment that
 * uses this must implement the Callbacks interface defined here and set the
 * target fragment before showing it.
 *
 * NOTE: this class is complete following what is in chapter 13 of the book.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    /**
     * The callbacks for when a date is selected.
     */
    interface Callbacks {
        /**
         * This function is called when a date is selected and the dialog is
         * confirmed.
         *
         * @param date the date that was picked
         */
        void onDateSelected(Date date);
    }

    /** The name of the argument for the date (a Date object) */
    private static final String ARG_DATE = "date";

    /**
     * Create a new instance of the date picking fragment dialog.
     * @param date the date to initially display in the picker
     * @return a new DatePickerFragment instance
     */
    public static DatePickerFragment newInstance(Date date) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
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
        Date date = DateUtils.useDateOrNow((Date) Objects.requireNonNull(getArguments()).getSerializable(ARG_DATE));
        int[] year_mon_day = DateUtils.getYearMonthDay(date);
        return new DatePickerDialog(requireContext(), this, year_mon_day[0], year_mon_day[1], year_mon_day[2]);
    }

    /**
     * When the DatePickerDialog is confirmed, this method is called which in
     * turn calls the Callbacks.onDateSelected() method.
     * @param view the DatePickerDialog that is calling this method
     * @param year the year picked
     * @param month the month picked
     * @param dayOfMonth the day of that month picked
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        ((Callbacks) Objects.requireNonNull(getTargetFragment())).onDateSelected(DateUtils.getDate(year, month, dayOfMonth));
    }
}

