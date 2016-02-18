package ca.mattdietrich.achieved.database;

import android.database.Cursor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.mattdietrich.achieved.model.Goal;
import ca.mattdietrich.achieved.model.Reminder;

/**
 * Contains some helper functions for data type conversions
 * Created by Matt on 2016-02-13.
 */
public final class DataTypeConversion {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat FRIENDLY_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");

    private DataTypeConversion () { }

    public static Goal cursorToGoal(Cursor cursor) {
        if (cursor.getCount() < 1)
            return null;
        Goal goal = new Goal();
        goal.setId(cursor.getLong(0));
        goal.setText(cursor.getString(1));
        goal.setDate(stringToDate(cursor.getString(2)));
        goal.setReward(cursor.getString(3));
        goal.setAchieved(intToBool(cursor.getInt(4)));
        return goal;
    }

    public static Reminder cursorToReminder(Cursor cursor) {
        if (cursor.getCount() < 1)
            return null;
        Reminder reminder = new Reminder();
        reminder.setId(cursor.getLong(0));
        reminder.setDateTime(stringToDateTime(cursor.getString(1)));
        reminder.setEnabled(intToBool(cursor.getInt(2)));
        reminder.setGoalId(cursor.getLong(3));
        return reminder;
    }

    public static String dateToString(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String dateToFriendlyString(Date date) {
        return FRIENDLY_DATE_FORMAT.format(date);
    }

    public static String dateTimeToString(Date dateTime) {
        return DATETIME_FORMAT.format(dateTime);
    }

    public static Date stringToDate (String dateString) {
        Date d = new Date();

        try {
            d = DATE_FORMAT.parse(dateString);
        }
        catch (Exception e){
            // TODO
        }
        return d;
    }

    public static Date stringToDateTime (String dateTimeString) {
        Date d = new Date();

        try {
            d = DATETIME_FORMAT.parse(dateTimeString);
        }
        catch (Exception e){
            // TODO
        }
        return d;
    }

    public static int boolToInt (boolean b) {
        return b ? 1 : 0;
    }

    public static boolean intToBool (int i) {
        return i != 0;
    }
}
