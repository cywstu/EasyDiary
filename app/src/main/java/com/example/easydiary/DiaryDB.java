package com.example.easydiary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

public class DiaryDB extends SQLiteOpenHelper {

    private final String DiaryTable = "TestDiary";

    public DiaryDB(Context context){
        super(context, "testDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DiaryTable + " (" +
                "ID INTEGER NOT NULL," +
                "Title VARCHAR NOT NULL," +
                "Content VARCHAR NOT NULL," +
                "Date VARCHAR NOT NULL," +
                "Image BLOB NOT NULL," +
                "PRIMARY KEY (ID))");

    }

    public void test(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DiaryTable);
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public void addDiary(String title, String desc, String date, byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO " + DiaryTable + " VALUES (?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(2, title);
        statement.bindString(3, desc);
        statement.bindString(4, date);
        statement.bindBlob(5, image);

        statement.executeInsert();
    }

    public Cursor getDiaries(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        String queryString = "SELECT * FROM " + DiaryTable;
        Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);

        if (cursor.getCount() == 0){
            Log.d("db", "0 result");
        }else{
            Log.d("db", "have result");
        }

        //cursor.close();
        sqLiteDatabase.close();

        return cursor;
    }
}
