package ca.mdietr.achieved.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import ca.mdietr.achieved.model.Goal;
import ca.mdietr.achieved.model.Reminder;

/**
 * Created by Matt on 2015-09-01.
 * Database access object
 */
public class DatabaseAccessObject {

    private static DatabaseAccessObject mInstance;

    private Context mContext;

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
        values.put(DatabaseContract.GoalSchema.COLUMN_NAME_DATE, DataTypeConversion.dateToString(date));
        values.put(DatabaseContract.GoalSchema.COLUMN_NAME_REWARD, reward);
        values.put(DatabaseContract.GoalSchema.COLUMN_NAME_ACHIEVED, 0);

        long insertId = database.insert(DatabaseContract.GoalSchema.TABLE_NAME, null, values);
        Cursor cursor = database.query(DatabaseContract.GoalSchema.TABLE_NAME,
                DatabaseContract.GoalSchema.ALL_COLUMNS,
                DatabaseContract.GoalSchema.COLUMN_NAME_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();

        Goal newGoal = DataTypeConversion.cursorToGoal(cursor);
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
                DatabaseContract.GoalSchema.COLUMN_NAME_DATE + "= \"" + DataTypeConversion.dateToString(goalDate)+"\"",
                null, null, null, null);
        cursor.moveToLast(); // TODO - handle multiple goals per date

        Goal newGoal = DataTypeConversion.cursorToGoal(cursor);
        cursor.close();
        return newGoal;
    }

    /**
     * Updates the goal
     */
    public long updateGoal (Goal goal) {
        if (database == null || !database.isOpen())
            open();
        if (database == null || !database.isOpen()){
            // TODO - Error Opening Database
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.GoalSchema.COLUMN_NAME_TEXT, goal.getText());
        values.put(DatabaseContract.GoalSchema.COLUMN_NAME_DATE, DataTypeConversion.dateToString(goal.getDate()));
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
        values.put(DatabaseContract.ReminderSchema.COLUMN_NAME_DATETIME, DataTypeConversion.dateTimeToString(date));
        values.put(DatabaseContract.ReminderSchema.COLUMN_NAME_ENABLED, isEnabled);
        values.put(DatabaseContract.ReminderSchema.COLUMN_NAME_GOAL_ID, goalId);

        long insertId = database.insert(DatabaseContract.ReminderSchema.TABLE_NAME, null, values);
        Cursor cursor = database.query(DatabaseContract.ReminderSchema.TABLE_NAME,
                DatabaseContract.ReminderSchema.ALL_COLUMNS,
                DatabaseContract.ReminderSchema.COLUMN_NAME_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();

        Reminder newReminder = DataTypeConversion.cursorToReminder(cursor);
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
                DatabaseContract.ReminderSchema.COLUMN_NAME_GOAL_ID + " = ?",
                new String[] {String.valueOf(goal.getId())},
                null, null, null, null);
        cursor.moveToLast();

        Reminder reminder = DataTypeConversion.cursorToReminder(cursor);
        cursor.close();
        return reminder;
    }

    /**
     * Updates the reminder
     */
    public long updateReminder (Reminder reminder) {
        if (database == null || !database.isOpen())
            open();
        if (database == null || !database.isOpen()){
            // TODO - Error Opening Database
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ReminderSchema.COLUMN_NAME_DATETIME, DataTypeConversion.dateTimeToString(reminder.getDateTime()));
        values.put(DatabaseContract.ReminderSchema.COLUMN_NAME_ENABLED, reminder.isEnabled());
        values.put(DatabaseContract.ReminderSchema.COLUMN_NAME_GOAL_ID, reminder.getGoalId());

        long updateId = database.update(DatabaseContract.ReminderSchema.TABLE_NAME, values, DatabaseContract.ReminderSchema.COLUMN_NAME_ID + " = ?", new String[] {String.valueOf(reminder.getId())});

        return updateId;
    }

    /**
     * Get Achievements Cursor
     */
    public Cursor getAchievementsCursor () {
        if (database == null || !database.isOpen())
            open();
        if (database == null || !database.isOpen()){
            // TODO - Error Opening Database
        }

        Cursor cursor = database.query(DatabaseContract.GoalSchema.TABLE_NAME,
                DatabaseContract.GoalSchema.ALL_COLUMNS,
                DatabaseContract.GoalSchema.COLUMN_NAME_ACHIEVED + " = 1",
                null, null, null, DatabaseContract.GoalSchema.COLUMN_NAME_DATE + " DESC", null);
        cursor.moveToFirst();

        return cursor;
    }
}
