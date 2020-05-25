package com.example.easydiary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

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
                "Desc VARCHAR NOT NULL," +
                "Date VARCHAR NOT NULL," +
                "Image BLOB NOT NULL," +
                "PRIMARY KEY (ID))");

    }

    public void reinstall(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DiaryTable);
        onCreate(db);
    }

    public int addDiary(String title, String desc, String date, byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO " + DiaryTable + " VALUES (?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(2, title);
        statement.bindString(3, desc);
        statement.bindString(4, date);
        statement.bindBlob(5, image);

        int insertedId = (int)statement.executeInsert();
        return insertedId;
    }

    public void updateDiary(int id, String title, String desc, String date, byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE " + DiaryTable + " SET Title = ?, Desc = ?, Date = ?, Image = ? WHERE ID = ?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, title);
        statement.bindString(2, desc);
        statement.bindString(3, date);
        statement.bindBlob(4, image);
        statement.bindLong(5, id);

        statement.executeUpdateDelete();
    }

    public void removeDiary(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM " + DiaryTable + " WHERE ID = ?;";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindLong(1, id);

        statement.executeUpdateDelete();
    }

    public Cursor getDiary(int id){
        SQLiteDatabase database = getReadableDatabase();

        String sql = "SELECT * FROM " + DiaryTable + " WHERE ID=" + id;
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() == 0){
            Log.d("db", "0 result");
        }else{
            Log.d("db", "have result");
        }

        //cursor.close();
        //sqLiteDatabase.close();

        return cursor;
    }

    public Cursor getDiaries(){
        SQLiteDatabase database = getReadableDatabase();

        String queryString = "SELECT * FROM " + DiaryTable;
        Cursor cursor = database.rawQuery(queryString, null);

        if (cursor.getCount() == 0){
            Log.d("db", "0 result");
        }else{
            Log.d("db", "have result");
        }

        //cursor.close();
        //database.close();

        return cursor;
    }

    public Cursor getDiaries(int year, int month, int day){
        //fix without 0 problem
        String strDate = "" + year;
        if(month < 10){ strDate += "0" + month; } else { strDate += month; }
        if(day < 10){ strDate += "0" + day; } else {strDate += day; }

        SQLiteDatabase database = getReadableDatabase();

        String queryString = "SELECT * FROM " + DiaryTable + " WHERE DATE = '" + strDate + "'";
        Cursor cursor = database.rawQuery(queryString, null);

        if (cursor.getCount() == 0){
            Log.d("db", "0 result");
        }else{
            Log.d("db", "have result");
        }

        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
