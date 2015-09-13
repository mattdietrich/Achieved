package ca.mdietr.achieved.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import ca.mdietr.achieved.model.Goal;

/**
 * Created by Matt on 2015-09-01.
 * Database access object
 */
public class DatabaseAccessObject {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public void open() {
        try{
            database = dbHelper.getWritableDatabase();
        }
        catch (SQLException e){
            // TODO
        }
    }

    public void close() {
        dbHelper.close();
    }


    /**
     * Inserts a new goal with the specified text and date into the database.
     * Returns a new Goal object.
     */
    public Goal createGoal (String goalText, Date goalDate) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Goal.COLUMN_NAME_TEXT, goalText);
        values.put(DatabaseContract.Goal.COLUMN_NAME_DATE, dateToString(goalDate));
        values.put(DatabaseContract.Goal.COLUMN_NAME_ACHIEVED, 0);

        long insertId = database.insert(DatabaseContract.Goal.TABLE_NAME, null, values);
        Cursor cursor = database.query(DatabaseContract.Goal.TABLE_NAME,
                DatabaseContract.Goal.ALL_COLUMNS,
                DatabaseContract.Goal.COLUMN_NAME_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();

        Goal newGoal = cursorToGoal(cursor);
        cursor.close();
        return newGoal;
    }

    private Goal cursorToGoal(Cursor cursor) {
        Goal goal = new Goal();
        goal.setId(cursor.getLong(0));
        goal.setText(cursor.getString(1));
        goal.setDate(stringToDate(cursor.getString(2)));
        goal.setAchieved(intToBool(cursor.getInt(3)));
        return goal;
    }

    private String dateToString(Date date) {
        return DATE_FORMAT.format(date);
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

    private int boolToInt (boolean b) {
        return b ? 1 : 0;
    }

    private boolean intToBool (int i) {
        return i != 0;
    }
}
