package ca.mdietr.achieved.database;

import android.provider.BaseColumns;

/**
 * Created by Matt on 2015-09-11.
 * This contract class is the Database Schema
 */
public final class DatabaseContract {

    public static final String DATABASE_NAME = "goals.db";
    public static final int DATABASE_VERSION = 1;

    /* An array list of all the SQL create table statements */
    public static final String[] SQL_CREATE_TABLE_ARRAY = {
        GoalSchema.CREATE_TABLE,
        ReminderSchema.CREATE_TABLE
    };

    /* An array list of all the SQL drop table statements */
    public static final String[] SQL_DROP_TABLE_ARRAY = {
            GoalSchema.DELETE_TABLE,
            ReminderSchema.DELETE_TABLE
    };

    // Private constructor prevents instantiation
    private DatabaseContract(){}

    /* Inner class that defines the table contents */
    public static final class GoalSchema implements BaseColumns {
        public static final String TABLE_NAME = "goal";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_REWARD = "reward";
        public static final String COLUMN_NAME_ACHIEVED = "achieved";

        public static final String CREATE_TABLE = "create table "
                + TABLE_NAME+ " ("
                + COLUMN_NAME_ID + " integer primary key autoincrement, "
                + COLUMN_NAME_TEXT + " text not null, "
                + COLUMN_NAME_DATE + " text not null, "
                + COLUMN_NAME_REWARD + " text, "
                + COLUMN_NAME_ACHIEVED + " integer default 0);";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String[] ALL_COLUMNS = {
                COLUMN_NAME_ID,
                COLUMN_NAME_TEXT,
                COLUMN_NAME_DATE,
                COLUMN_NAME_REWARD,
                COLUMN_NAME_ACHIEVED
        };

        // Private constructor prevents instantiation
        private GoalSchema() {}
    }

    /* Inner class that defines the table contents */
    public static final class ReminderSchema implements BaseColumns {
        public static final String TABLE_NAME = "reminder";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_DATETIME= "dateTime";
        public static final String COLUMN_NAME_ENABLED = "enabled";
        public static final String COLUMN_NAME_GOAL_ID = "goalId";

        public static final String CREATE_TABLE = "create table "
                + TABLE_NAME + " ("
                + COLUMN_NAME_ID + " integer primary key autoincrement, "
                + COLUMN_NAME_DATETIME + " text not null, "
                + COLUMN_NAME_ENABLED + " integer default 1, "
                + COLUMN_NAME_GOAL_ID + " integer, "
                + "foreign key(" + COLUMN_NAME_GOAL_ID + ") references " + GoalSchema.TABLE_NAME + "("
                + GoalSchema.COLUMN_NAME_ID + "));";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String[] ALL_COLUMNS = {
                COLUMN_NAME_ID,
                COLUMN_NAME_DATETIME,
                COLUMN_NAME_ENABLED,
                COLUMN_NAME_GOAL_ID
        };

        // Private constructor prevents instantiation
        private ReminderSchema() {}
    }
}
