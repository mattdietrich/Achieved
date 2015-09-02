package ca.mattdietrich.achieved.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Matt on 2015-09-01.
 */
public class GoalSQLiteOpenHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "goals.db"
    private static final int DATABASE_VERSION = 1;

    // Goal Table
    private static final String GOAL_TABLE_NAME = "goal";
    private static final String GOAL_COLUMN_ID = "_id";
    private static final String GOAL_COLUMN_TEXT = "text";
    private static final String GOAL_COLUMN_DATE= "date";
    private static final String GOAL_COLUMN_ACHIEVED = "achieved";

    // Reminder Table
    private static final String REMINDER_TABLE_NAME = "reminder";
    private static final String REMINDER_COLUMN_ID = "_id";
    private static final String REMINDER_COLUMN_DATETIME = "dateTime";
    private static final String REMINDER_COLUMN_ENABLED = "enabled";
    private static final String REMINDER_COLUMN_GOAL_ID = "achieved";

    // Create Goal Table String
    private static final String GOAL_TABLE_CREATE = "create table "
            + GOAL_TABLE_NAME + " ("
            + GOAL_COLUMN_ID + " integer primary key autoincrement, "
            + GOAL_COLUMN_TEXT + " text not null, "
            + GOAL_COLUMN_DATE + " text not null, "
            + GOAL_COLUMN_ACHIEVED + " integer default 0);";

    // Create Reminder Table String
    private static final String REMINDER_TABLE_CREATE = "create table "
            + REMINDER_TABLE_NAME + " ("
            + REMINDER_COLUMN_ID + " integer primary key autoincrement, "
            + REMINDER_COLUMN_DATETIME + " text not null, "
            + REMINDER_COLUMN_ENABLED + " integer default 1"
            + REMINDER_COLUMN_GOAL_ID + " integer, "
            + "foreign key(" + REMINDER_COLUMN_GOAL_ID + ") references " + GOAL_TABLE_NAME + "("
            + GOAL_COLUMN_ID + "));";

    GoalSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GOAL_TABLE_CREATE);
        db.execSQL(REMINDER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(GoalSQLiteOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + GOAL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + REMINDER_TABLE_NAME);
        onCreate(db);
    }
}
