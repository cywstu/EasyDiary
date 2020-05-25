package com.example.easydiary;

import android.database.Cursor;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

//coverage
//https://stackoverflow.com/questions/23795595/how-do-i-get-a-jacoco-coverage-report-using-android-gradle-plugin-0-10-0-or-high
//gradlew createDebugCoverageReport
@RunWith(AndroidJUnit4.class)
public class DiaryDBTest {
    private DiaryDB db;

    @Before
    public void dbSetup(){
        db = new DiaryDB(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @Test
    public void addTest(){
        byte[] testBytes = {123};
        Diary diary = new Diary(0, "test diary", "test description", "20190204", testBytes);
        int count = db.getDiaries().getCount();

        int id = db.addDiary(diary.getTitle(), diary.getDesc(), diary.getDate(), diary.getImage());
        int newCount = db.getDiaries().getCount();

        assertEquals(count + 1, newCount);
        db.removeDiary(id);
    }

    @Test
    public void updateTest(){
        byte[] testBytes = {123};
        Diary diary = new Diary(1, "old", "test description", "20190204", testBytes);
        Diary newDiary = new Diary(1, "updated", "test description", "20190205", testBytes);

        DiaryDB db = new DiaryDB(InstrumentationRegistry.getInstrumentation().getTargetContext());
        int id = db.addDiary(diary.getTitle(), diary.getDesc(), diary.getDate(), diary.getImage());
        db.updateDiary(newDiary.getId(), newDiary.getTitle(), newDiary.getDesc(), newDiary.getDate(), newDiary.getImage());

        Cursor cursor = db.getDiary(1);
        String newTitle = "";
        if(cursor.moveToNext()) {
            newTitle = cursor.getString(1);
        }
        assertEquals("updated", newTitle);
        db.removeDiary(id);
    }

    @Test
    public void removeTest(){
        byte[] testBytes = {123};
        DiaryDB db = new DiaryDB(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Diary diary = new Diary(2, "old", "test description", "20190204", testBytes);

        int id = db.addDiary(diary.getTitle(), diary.getDesc(), diary.getDate(), diary.getImage());
        int count = db.getDiaries().getCount();
        db.removeDiary(id);

        int newCount = db.getDiaries().getCount();
        assertEquals(count - 1, newCount);
    }

    @Test
    public void selectFromDateTest(){
        byte[] testBytes = {123};
        Diary diary = new Diary(3, "2000y01m01d", "test description", "20000101", testBytes);

        DiaryDB db = new DiaryDB(InstrumentationRegistry.getInstrumentation().getTargetContext());
        int id = db.addDiary(diary.getTitle(), diary.getDesc(), diary.getDate(), diary.getImage());

        Cursor cursor = db.getDiaries(2000,1,1);
        String diaryTitle = "";
        if(cursor.moveToNext()) {
            diaryTitle = cursor.getString(1);
        }
        assertEquals("2000y01m01d", diaryTitle);
        db.removeDiary(id);
    }
}
