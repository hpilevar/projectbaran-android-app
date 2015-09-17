package ir.pathseeker.baran;

/**
 * Created by farid on 4/12/15.
 */

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ReportTable {

    // Database table
    public static final String TABLE_REPORT = "report";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LONG= "lon";
    public static final String COLUMN_title = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMAGE= "image";
    public static final String COLUMN_STATE= "state";


    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_REPORT
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_LAT + " text not null, "
            + COLUMN_LONG + " text not null,"
            + COLUMN_title + " text not null,"
            + COLUMN_IMAGE + " text not null,"
            + COLUMN_STATE + " text not null,"
            + COLUMN_DESCRIPTION
            + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ReportTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORT);
        onCreate(database);
    }
}