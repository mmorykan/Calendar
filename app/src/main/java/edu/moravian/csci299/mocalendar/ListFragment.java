package edu.moravian.csci299.mocalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A fragment that displays a list of events. The list is a RecyclerView. When an event on the list
 * is clicked, a callback method is called to inform the hosting activity. When an item on the list
 * is swiped, it causes the event to be deleted (see https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e).
 * This is the fragment that also controls the menu of options in the app bar.
 *
 * Above the list is a text box that states the date being displayed on the list.
 *
 * NOTE: Finish CalendarFragment first then work on this one. Also, look at how a few things
 * related to dates are dealt with in the CalendarFragment and use similar ideas here.
 */
public class ListFragment extends Fragment {
    // fragment initialization parameters
    private static final String ARG_DATE = "date";

    private Date date;
    private RecyclerView list;
    private List<Event> events = Collections.emptyList();
    private Callbacks callbacks;
    private TextView currentDate;


    /**
     * The callbacks interface to tell MainActivity to show an EventFragment
     */
    public interface Callbacks {
        /**
         * When an event is clicked in the recycler view or in menu
         * @param event The event clicked
         */
        void onEventClicked(Event event);
    }

    /**
     * Use this factory method to create a new instance of this fragment that
     * lists events for today.
     * @return a new instance of fragment ListFragment
     */
    public static ListFragment newInstance() {
        return newInstance(new Date());
    }

    /**
     * Use this factory method to create a new instance of this fragment that
     * lists events for the given day.
     * @param date the date to show the event list for
     * @return a new instance of fragment ListFragment
     */
    public static ListFragment newInstance(Date date) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Set the day for the events being listed.
     * @param date the new day for the list to show events for
     */
    public void setDay(Date date) {
        this.date = date;
        Objects.requireNonNull(getArguments()).putSerializable(ARG_DATE, date);
        onDateChange();
    }

    /**
     * Upon creation need to enable the options menu and update the view for the initial date.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.date = DateUtils.useDateOrNow((Date) Objects.requireNonNull(getArguments()).getSerializable(ARG_DATE));
        onDateChange();
        setHasOptionsMenu(true);
    }

    /**
     * Create the view for this layout. Also sets up the adapter for the RecyclerView, its swipe-to-
     * delete helper, and gets the date text view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View base = inflater.inflate(R.layout.fragment_list, container, false);

        list = base.findViewById(R.id.list_view);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        EventAdapter adapter = new EventAdapter();
        list.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(list);

        currentDate = base.findViewById(R.id.date);

        // return the base view
        return base;
    }

    /**
     * When the date is changed for this fragment we need to grab a new list of events and update
     * the UI.
     */
    private void onDateChange() {
        int[] day = DateUtils.getYearMonthDay(this.date);
        LiveData<List<Event>> liveDataEvents = EventRepository.get().getEventsOnDay(DateUtils.getDate(day[0], day[1], day[2]));
        liveDataEvents.observe(this, (events) -> {
            this.events = events;
            Objects.requireNonNull(list.getAdapter()).notifyDataSetChanged();
            currentDate.setText(DateUtils.toFullDateString(this.date));
        });
    }

    /**
     * Set the callbacks
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
     * Create the menu in top bar of list fragment
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.event_menu, menu);
    }

    /**
     * When an assignment or event is clicked in the menu bar.
     * If event, set end time one hour after start time
     * @param item Either the assignment or the event buttons
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.event || id == R.id.assignment) {
            Event event = new Event();
            event.name = "New Event";
            event.startTime = DateUtils.combineDateAndTime(this.date, event.startTime);
            if (id == R.id.event) {
                int[] start = DateUtils.getHourMinute(event.startTime);
                event.endTime = DateUtils.combineDateAndTime(event.startTime,
                        DateUtils.getTime(start[0] + 13, start[1]));
                event.type = EventType.GENERIC;
            } else {
                event.type = EventType.ASSIGNMENT;
            }
            EventRepository.get().addEvent(event);
            callbacks.onEventClicked(event);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * The view holder of a single event in the RecyclerView list.
     */
    private class EventHolder extends RecyclerView.ViewHolder {
        // currently bound event
        Event event;

        // the text views in the view we are holding
        TextView eventName, eventDescription, startTime, endTime;
        ImageView eventIcon;

        /**
         * Create a new event holder for the given view.
         * @param itemView the view to have within this holder
         */
        public EventHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDescription = itemView.findViewById(R.id.description);
            startTime = itemView.findViewById(R.id.start_time);
            endTime = itemView.findViewById(R.id.date);
            eventIcon = itemView.findViewById(R.id.eventTypeIcon);
            itemView.setOnClickListener(v -> callbacks.onEventClicked(event));
        }
    }

    /**
     * The adapter for the RecyclerView list to show information from the list of events.
     */
    private class EventAdapter extends RecyclerView.Adapter<EventHolder> {
        /**
         * Create and return the event holder for an item in the RecyclerView list.
         */
        @NonNull
        @Override
        public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            return new EventHolder(view);
        }

        /**
         * Bind the given event holder to an item in the list.
         */
        @Override
        public void onBindViewHolder(@NonNull EventHolder holder, int position) {
            Event event = events.get(position);
            holder.event = event;
            holder.eventName.setText(event.name);
            holder.eventDescription.setText(event.description);
            holder.startTime.setText(DateUtils.toTimeString(event.startTime));
            if (event.endTime != null) {
                holder.endTime.setText(DateUtils.toTimeString(event.endTime));
            }
            holder.eventIcon.setImageResource(event.type.iconResourceId);
        }

        /**
         * @return the number of events in the list
         */
        @Override
        public int getItemCount() { return events == null ? 0 : events.size(); }

        /**
         * Delete an event from the database and remove it from the list of events
         * @param position The position of the event to remove
         */
        public void deleteEvent(int position) {
            EventRepository.get().removeEvent(events.get(position));
            events.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * The class for swipe to delete functionality
     */
    private class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        /** Swipe to delete functionality */
        private final EventAdapter adapter;
        private final Drawable icon;
        private final ColorDrawable background;

        /**
         * Set up the icon, color and adapter for swipe to delete
         * @param adapter The custom EventAdapter for all events
         */
        public SwipeToDeleteCallback(EventAdapter adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.adapter = adapter;
            icon = ContextCompat.getDrawable(list.getContext(), R.drawable.delete);
            background = new ColorDrawable(Color.RED);
        }

        /**
         * Needed to implement but don't need to use
         */
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        /**
         * When an event is swiped
         * @param viewHolder The Holder for an event
         * @param direction The direction swiped
         */
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            adapter.deleteEvent(position);
        }

        /**
         * Used for drawing the trash can icon when swiping an event
         */
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX,
                    dY, actionState, isCurrentlyActive);
            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;

            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (dX > 0) { // Swiping to the right
                int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                int iconRight = itemView.getLeft() + iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                        itemView.getBottom());
            } else if (dX < 0) { // Swiping to the left
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
            }

            background.draw(c);
            icon.draw(c);
        }
    }
}
