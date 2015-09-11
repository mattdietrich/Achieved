package ca.mdietr.achieved.database;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import ca.mdietr.achieved.model.Goal;

/**
 * Created by Matt on 2015-09-01.
 * Database access object
 */
public class GoalsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private GoalSQLiteOpenHelper dbHelper;

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


    public Goal
}
