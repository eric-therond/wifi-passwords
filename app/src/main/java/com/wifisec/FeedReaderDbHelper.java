package com.wifisec;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES1 =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME_WIFI + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_TITLE + TEXT_TYPE + "," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_BSSID + TEXT_TYPE + "," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_LASTSCAN + TEXT_TYPE + "," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_SECURITY + INTEGER_TYPE + "," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_ROBUSTNESS + INTEGER_TYPE + "," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_PASSWORD + TEXT_TYPE + "," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_COORDINATES_X + REAL_TYPE + "," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_COORDINATES_Y + REAL_TYPE + "," +
                    " UNIQUE (" + FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_BSSID + ") ON CONFLICT REPLACE )";

    private static final String SQL_CREATE_ENTRIES2 =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME_PASSWORDS + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORDS_TITLE + TEXT_TYPE + ", UNIQUE (" + FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORDS_TITLE + ") ON CONFLICT REPLACE )";

    private static final String SQL_DELETE_ENTRIES1 =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME_WIFI;

    private static final String SQL_DELETE_ENTRIES2 =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME_PASSWORDS;


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 89;
    public static final String DATABASE_NAME = "FeedReader.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES1);
        db.execSQL(SQL_CREATE_ENTRIES2);

        ContentValues values1 = new ContentValues();
        values1.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORDS_TITLE, "admin");
        long newRowId1 = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME_PASSWORDS, "null", values1);
        ContentValues values2 = new ContentValues();
        values2.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORDS_TITLE, "012345678");
        long newRowId2 = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME_PASSWORDS, "null", values2);
        ContentValues values3 = new ContentValues();
        values3.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORDS_TITLE, "12345678");
        long newRowId3 = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME_PASSWORDS, "null", values3);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES1);
        db.execSQL(SQL_DELETE_ENTRIES2);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
