package edu.moravian.csci299.mocalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

/**
 * A fragment that acts as a popup window for picking a type of an event. Any fragment that uses
 * this must implement the Callbacks interface defined here and set the target fragment before
 * showing it.
 *
 * NOTE: This class is done. If you add additional event types they will automatically show up
 * here. You will need to use this class like the other dialog fragments.
 */
public class EventTypePickerFragment extends DialogFragment {
    private static final EventType[] EVENT_TYPES = EventType.values();

    /**
     * The callbacks for when a type is selected.
     */
    interface Callbacks {
        /**
         * This function is called when a type is selected and the dialog is confirmed.
         *
         * @param type the event type that was picked
         */
        void onTypeSelected(EventType type);
    }

    /**
     * The name of the argument for the initial type (a String value)
     */
    private static final String ARG_INITIAL_TYPE = "initial_type";

    /**
     * Create a new instance of the event type picking fragment dialog.
     *
     * @param type the type to initially display in the picker
     * @return a new EventTypePickerFragment instance
     */
    public static EventTypePickerFragment newInstance(EventType type) {
        EventTypePickerFragment fragment = new EventTypePickerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INITIAL_TYPE, type.name());
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create the dialog using an AlertDialog builder and a list adapter.
     * @return the Dialog that will be displayed
     */
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        b.setTitle(R.string.event_type);
        b.setAdapter(new EventTypesListAdapter(), (dialog, which) -> {
            dialog.dismiss();
            ((Callbacks) Objects.requireNonNull(getTargetFragment())).onTypeSelected(EVENT_TYPES[which]);
        });
        return b.create();
    }

    /**
     * Adapt the list of event types to a set of views the can be displayed in an alert box.
     */
    private class EventTypesListAdapter extends BaseAdapter {
        @Override
        public int getCount() { return EVENT_TYPES.length; }

        @Override
        public Object getItem(int position) { return EVENT_TYPES[position]; }

        @Override
        public long getItemId(int position) { return EVENT_TYPES[position].hashCode(); }

        @Override
        public boolean hasStableIds() { return true; }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            // reuse the view or load it new
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.event_type_item, parent, false);
            }

            // set the icon and name
            EventType type = EVENT_TYPES[position];
            ((ImageView)view.findViewById(R.id.eventTypeIcon)).setImageResource(type.iconResourceId);
            ((TextView)view.findViewById(R.id.eventTypeName)).setText(type.simpleName);

            // returns the view
            return view;
        }
    }
}