package edu.moravian.csci299.mocalendar;

/**
 * The types of events (and assignments). This is gives each type an icon and a name so that it can
 * be displayed with the events for easy identification.
 *
 * NOTE: If you want to, you can add additional types here. Each one requires a name and a drawable
 * id so that it can be selected and have an icon displayed.
 */
public enum EventType {
    GENERIC("Event", R.drawable.event),
    ASSIGNMENT("Assignment", R.drawable.assignment),
    CLASS("Class", R.drawable.school),
    LAB("Lab", R.drawable.science),
    EXAM("Exam", R.drawable.quiz),
    ESSAY("Essay", R.drawable.essay),
    PROGRAMMING("Programming Assignment", R.drawable.code),
    READING("Reading Assignment", R.drawable.book),
    CLUB("Club", R.drawable.groups),
    OFFICE_HOURS("Office Hours", R.drawable.meeting_room),
    ATHLETIC_PRACTICE("Athletic Practice", R.drawable.sports_soccer),
    MUSIC_PRACTICE("Music Practice", R.drawable.music_note),
    COMPETITION("Competition", R.drawable.trophy),
    PRESENTATION("Presentation", R.drawable.present),
    HOLIDAY("Holiday", R.drawable.holiday);

    public final String simpleName;
    public final int iconResourceId;
    EventType(String name, int iconResId) {
        this.simpleName = name;
        this.iconResourceId = iconResId;
    }
}
