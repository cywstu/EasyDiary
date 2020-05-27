package com.example.easydiary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.Calendar;

public class DiaryDB extends SQLiteOpenHelper {

    private final String DiaryTable = "TestDiary";
    private final String BackupTable = "TestBackup";

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
                "Lat DOUBLE NOT NULL," +
                "Lng DOUBLE NOT NULL," +
                "CreateDT VARCHAR NOT NULL," +
                "PRIMARY KEY (ID))");

        db.execSQL("CREATE TABLE " + BackupTable + " (" +
                "CreateDT VARCHAR NOT NULL," +
                "Type VARCHAR NOT NULL)");

    }

    public void reinstall(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DiaryTable);
        db.execSQL("DROP TABLE IF EXISTS " + BackupTable);
        onCreate(db);
    }

    public int addDiary(String title, String desc, String date, byte[] image, double lat, double lng){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO " + DiaryTable + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(2, title);
        statement.bindString(3, desc);
        statement.bindString(4, date);
        statement.bindBlob(5, image);
        statement.bindDouble(6, lat);
        statement.bindDouble(7, lng);
        Calendar c = Calendar.getInstance();
        String createDT = "" + c.get(Calendar.YEAR) + c.get(Calendar.MONTH) + c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) + c.get(Calendar.SECOND) + c.get(Calendar.MILLISECOND);
        statement.bindString(8, createDT);

        int insertedId = (int)statement.executeInsert();

        //backup part
        backupLog(createDT, "add");
        return insertedId;
    }

    public int addDiary(String title, String desc, String date, byte[] image, double lat, double lng, String createDT){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO " + DiaryTable + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(2, title);
        statement.bindString(3, desc);
        statement.bindString(4, date);
        statement.bindBlob(5, image);
        statement.bindDouble(6, lat);
        statement.bindDouble(7, lng);
        statement.bindString(8, createDT);

        int insertedId = (int)statement.executeInsert();

        //backup part
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

        statement.execute();

        //backup part
        String createDT = getDiaryCreateDT(id);
        backupLog(createDT,"update");
    }

    public void removeDiary(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM " + DiaryTable + " WHERE ID = ?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindLong(1, id);

        statement.executeUpdateDelete();

        //backup part
        String createDT = getDiaryCreateDT(id);
        backupLog(createDT,"delete");
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

    public Cursor getDiary(String createDT){
        SQLiteDatabase database = getReadableDatabase();

        String sql = "SELECT * FROM " + DiaryTable + " WHERE CreateDT = '" + createDT + "'";
        Cursor cursor = database.rawQuery(sql, null);
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
        Log.d("get diary date", strDate);
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

    public Cursor getRecentDiaries(){
        SQLiteDatabase database = getReadableDatabase();

        String queryString = "SELECT * FROM " + DiaryTable + " ORDER BY Date DESC LIMIT 10";
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

    //===========================================================================================
    //back up part
    //===========================================================================================
    public void backupLog(String createDT, String type){
        if(!existsInBackup(createDT)){
            addBackup(createDT,type);
        }else{
            updateBackup(createDT,type);
        }
    }

    public boolean existsInBackup(String createDT){
        boolean exists = false;
        SQLiteDatabase database = getReadableDatabase();
        String queryString = "SELECT * FROM " + BackupTable + " WHERE CreateDT = '" + createDT + "'";
        Cursor cursor = database.rawQuery(queryString, null);

        //not exists in backup table
        if (cursor.getCount() == 0){
            exists = false;
        }else{//yes
            exists = true;
        }
        return exists;
    }

    public void addBackup(String createDT, String type){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO " + BackupTable + " VALUES (?,?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, createDT);
        statement.bindString(2, type);

        statement.executeInsert();
    }

    public void updateBackup(String createDT, String type){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE " + BackupTable + " SET Type = ? WHERE CreateDT = ?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, type);
        statement.bindString(2, createDT);

        statement.executeUpdateDelete();
    }

    //for DiaryTable's reference
    public String getDiaryCreateDT(int id){
        String createDT = "";
        SQLiteDatabase database = getReadableDatabase();
        String queryString = "SELECT CreateDT FROM " + DiaryTable + " WHERE ID = " + id + "";
        Cursor cursor = database.rawQuery(queryString, null);
        if (cursor.getCount() != 0){
            cursor.moveToNext();
            createDT = cursor.getString(0);
        }
        return createDT;
    }

    public Cursor getAllBackup(){
        SQLiteDatabase database = getReadableDatabase();
        String queryString = "SELECT * FROM " + BackupTable;
        Cursor cursor = database.rawQuery(queryString, null);
        return cursor;
    }

    public Cursor getBackup(String createDT){
        SQLiteDatabase database = getReadableDatabase();
        String queryString = "SELECT * FROM " + BackupTable + " WHERE CreateDT = '" + createDT + "'";
        Cursor cursor = database.rawQuery(queryString, null);
        return cursor;
    }
}
