package com.example.playground1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "myDB.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table item ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "description text,"
                + "ownerId integer,"
                + "takenById integer,"
                + "pictureUri text"
                + ");");
        db.execSQL("create table user ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "address text,"
                + "phone text,"
                + "email text,"
                + "password text"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
