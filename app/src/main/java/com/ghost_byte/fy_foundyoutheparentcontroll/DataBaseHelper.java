package com.ghost_byte.fy_foundyoutheparentcontroll;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Location.DB";
    private static final int DATABASE_VERSION = 1;

    // Columns
    public static final String TABLE_NAME = "users";
    public static final String COL_NAME = "name";
    public static final String UNIQUE_CODE = "uniqueCode";

    // SQL code to create table
    private static final String SQL_CODE_LINE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    UNIQUE_CODE + " TEXT PRIMARY KEY , " +
                    COL_NAME + " TEXT)";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CODE_LINE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert data
    public long insertUser(String name, String uniqueCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(UNIQUE_CODE, uniqueCode);

        return db.insert(TABLE_NAME, null, values);
    }

    //get data
    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    //delete data
    public boolean deleteData(String uniqueCode){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, UNIQUE_CODE + "=?", new String[]{uniqueCode});
        db.close();

        return result > 0 ;
    }

}
