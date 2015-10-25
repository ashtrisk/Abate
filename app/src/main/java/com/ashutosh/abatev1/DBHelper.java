package com.ashutosh.abatev1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vostro-Daily on 10/18/2015.
 * Description : Helps in creating, opening, upgrading and closing the SQLite database using SQLiteOpenHelper
 */
public class DBHelper extends SQLiteOpenHelper {

    private final static String Database_Name = "UserPost.db";       // Database (Database file) name in the file system
    private final static int Database_Version = 1;                   // should be incremented every time the schema changes

    public DBHelper(Context ctx) {
        super(ctx, Database_Name, null, Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String TEXT_TYPE = " TEXT";           // note : space before text is deliberately added
        final String SQL_CREATE_ENTRIES = "Create Table "+ UserPostContract.Post.TABLE_NAME +" (" +
                UserPostContract.Post._ID + " INTEGER PRIMARY KEY, " + UserPostContract.Post.COLUMN_NAME_POST_CONTENT + TEXT_TYPE + " ," +
                UserPostContract.Post.COLUMN_NAME_POST_IMAGE_URI + TEXT_TYPE + ");";

        db.execSQL(SQL_CREATE_ENTRIES);                 // creates a new table "post" with the specified parameters
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        final String SQL_DELETE_ENTERIES = "DROP TABLE IF EXIST "+ UserPostContract.Post.TABLE_NAME ;

        db.execSQL(SQL_DELETE_ENTERIES);                // drops table i.e. deletes all data present in the table

        onCreate(db);
    }
}
