package ca.mdietr.achieved.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import ca.mdietr.achieved.model.Goal;
import ca.mdietr.achieved.model.Reminder;

/**
 * Created by Matt on 2015-09-01.
 * Database access object
 */
public class DatabaseAccessObject {

    private static DatabaseAccessObject mInstance;

    private Context mContext;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");// new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Database fields
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    /** Block direct instantiation to ensure singleton */
    private DatabaseAccessObject(Context context) {
        mContext = context;
    }

    public static DatabaseAccessObject getInstance(Context context) {
        if (mInstance == null)
            mInstance = new DatabaseAccessObject(context.getApplicationContext());

        return mInstance;
    }

    public void open() {
        try{
            if(dbHelper == null)
                dbHelper = DatabaseHelper.getInstance(mContext.getApplicationContext());
            database = dbHelper.getWritableDatabase();
        }
        catch (SQLException e){
            // TODO
            int i = 0;
        }
    }

    public void close() {
        try{
            if(dbHelper == null)
                dbHelper = DatabaseHelper.getInstance(mContext.getApplicationContext());
            dbHelper.close();
        }
        catch (SQLException e){
            // TODO
        }
    }

    /**
     * Inserts a new goal with the specified text and date into the database.
     * Returns a new Goal object.
     */
    public Goal createGoal (String text, Date date, String reward) {
        if (database == null || !database.isOpen())
            open();
        if (database == null || !database.isOpen()){
            // TODO - Error Opening Database
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.GoalSchema.COLUMN_NAME_TEXT, text);
        values.put(DatabaseContract.GoalSchema.COLUMN_NAME_DATE, dateToString(date));
        values.put(DatabaseContract.GoalSchema.COLUMN_NAME_REWARD, reward);
        values.put(DatabaseContract.GoalSchema.COLUMN_NAME_ACHIEVED, 0);

        long insertId = database.insert(DatabaseContract.GoalSchema.TABLE_NAME, null, values);
        Cursor cursor = database.query(DatabaseContract.GoalSchema.TABLE_NAME,
                DatabaseContract.GoalSchema.ALL_COLUMNS,
                DatabaseContract.GoalSchema.COLUMN_NAME_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();

        Goal newGoal = cursorToGoal(cursor);
        cursor.close();
        return newGoal;
    }

    /**
     * Retrieves the goal for the specified date.
     * Returns a Goal object.
     */
    public Goal getGoal (Date goalDate) {
        if (database == null || !database.isOpen())
            open();
        if (database == null || !database.isOpen()){
            // TODO - Error Opening Database
        }

        Cursor cursor = database.query(DatabaseContract.GoalSchema.TABLE_NAME,
                DatabaseContract.GoalSchema.ALL_COLUMNS,
                DatabaseContract.GoalSchema.COLUMN_NAME_DATE + "= \"" + dateToString(goalDate)+"\"",
                null, null, null, null);
        cursor.moveToLast(); // TODO - handle multiple goals per date

        Goal newGoal = cursorToGoal(cursor);
        cursor.close();
        return newGoal;
    }

    /**
     * Updates the goal spec*/
    public long updateGoal (Goal goal) {
        if (database == null || !database.isOpen())
            open();
        if (database == null || !database.isOpen()){
            // TODO - Error Opening Database
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.GoalSchema.COLUMN_NAME_TEXT, goal.getText());
        values.put(DatabaseContract.GoalSchema.COLUMN_NAME_DATE, dateToString(goal.getDate()));
        values.put(DatabaseContract.GoalSchema.COLUMN_NAME_REWARD, goal.getReward());
        values.put(DatabaseContract.GoalSchema.COLUMN_NAME_ACHIEVED, goal.isAchieved());

        long updateId = database.update(DatabaseContract.GoalSchema.TABLE_NAME, values, DatabaseContract.GoalSchema.COLUMN_NAME_ID + " = ?", new String[] {String.valueOf(goal.getId())});

        return updateId;
    }

    /**
     * Inserts a new reminder with the specified date/time, enabled status, and goal Id into the database.
     * Returns a new Reminder object.
     */
    public Reminder createReminder (Date date, boolean isEnabled, long goalId) {
        if (database == null || !database.isOpen())
            open();
        if (database == null || !database.isOpen()){
            // TODO - Error Opening Database
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ReminderSchema.COLUMN_NAME_DATETIME, dateTimeToString(date));
        values.put(DatabaseContract.ReminderSchema.COLUMN_NAME_ENABLED, isEnabled);
        values.put(DatabaseContract.ReminderSchema.COLUMN_NAME_GOAL_ID, goalId);

        long insertId = database.insert(DatabaseContract.ReminderSchema.TABLE_NAME, null, values);
        Cursor cursor = database.query(DatabaseContract.ReminderSchema.TABLE_NAME,
                DatabaseContract.ReminderSchema.ALL_COLUMNS,
                DatabaseContract.ReminderSchema.COLUMN_NAME_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();

        Reminder newReminder = cursorToReminder(cursor);
        cursor.close();
        return newReminder;
    }

    /**
     * Retrieves the Reminder for the specified goal.
     * Returns a Reminder object.
     */
    public Reminder getReminder (Goal goal) {
        if (goal == null)
            return null;
        if (database == null || !database.isOpen())
            open();
        if (database == null || !database.isOpen()){
            // TODO - Error Opening Database
        }

        Cursor cursor = database.query(DatabaseContract.ReminderSchema.TABLE_NAME,
                DatabaseContract.ReminderSchema.ALL_COLUMNS,
                DatabaseContract.ReminderSchema.COLUMN_NAME_GOAL_ID + " = " + goal.getId(),
                null, null, null, null);
        cursor.moveToLast();

        Reminder reminder = cursorToReminder(cursor);
        cursor.close();
        return reminder;
    }

    /**
     * Below are some helper functions (data type conversions)
     */

    private Goal cursorToGoal(Cursor cursor) {
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

    private Reminder cursorToReminder(Cursor cursor) {
        if (cursor.getCount() < 1)
            return null;
        Reminder reminder = new Reminder();
        reminder.setId(cursor.getLong(0));
        reminder.setDateTime(stringToDateTime(cursor.getString(1)));
        reminder.setEnabled(intToBool(cursor.getInt(2)));
        reminder.setGoalId(cursor.getLong(3));
        return reminder;
    }

    private String dateToString(Date date) {
        return DATE_FORMAT.format(date);
    }

    private String dateTimeToString(Date dateTime) {
        return DATETIME_FORMAT.format(dateTime);
    }

    private Date stringToDate (String dateString) {
        Date d = new Date();

        try {
            d = DATE_FORMAT.parse(dateString);
        }
        catch (Exception e){
            // TODO
        }
        return d;
    }

    private Date stringToDateTime (String dateTimeString) {
        Date d = new Date();

        try {
            d = DATETIME_FORMAT.parse(dateTimeString);
        }
        catch (Exception e){
            // TODO
        }
        return d;
    }

    private int boolToInt (boolean b) {
        return b ? 1 : 0;
    }

    private boolean intToBool (int i) {
        return i != 0;
    }
}
