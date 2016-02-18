package ca.mattdietrich.achieved.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Matt on 2015-09-01.
 * Responsible for creating and updating the database
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static DatabaseHelper mInstance;

    // Prevent direct instantiation
    private DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (mInstance == null)
            mInstance = new DatabaseHelper(context.getApplicationContext());

        return mInstance;
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Execute each create table query
        for(String create_table : DatabaseContract.SQL_CREATE_TABLE_ARRAY)
            db.execSQL(create_table);
    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        // Execute each drop table query
        for(String drop_table : DatabaseContract.SQL_DROP_TABLE_ARRAY)
            db.execSQL(drop_table);

        onCreate(db);
    }
}
