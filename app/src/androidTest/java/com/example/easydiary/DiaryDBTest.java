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
    public void addDiaryTest(){
        byte[] testBytes = {123};
        Diary diary = new Diary(0, "test diary", "test description", "20190204", testBytes, 12.5, 30);
        int count = db.getDiaries().getCount();

        int id = db.addDiary(diary.getTitle(), diary.getDesc(), diary.getDate(), diary.getImage(), diary.getLat(), diary.getLng());
        int newCount = db.getDiaries().getCount();

        assertEquals(count + 1, newCount);
        db.removeDiary(id);
    }

    @Test
    public void updateDiaryTest(){
        byte[] testBytes = {123};
        Diary diary = new Diary(1, "old", "test description", "20190204", testBytes, 12.5, 30);
        Diary newDiary = new Diary(1, "updated", "test description", "20190205", testBytes, 12.5, 30);

        DiaryDB db = new DiaryDB(InstrumentationRegistry.getInstrumentation().getTargetContext());
        int id = db.addDiary(diary.getTitle(), diary.getDesc(), diary.getDate(), diary.getImage(), diary.getLat(), diary.getLng());
        db.updateDiary(id, newDiary.getTitle(), newDiary.getDesc(), newDiary.getDate(), newDiary.getImage());

        Cursor cursor = db.getDiary(id);
        String newTitle = "";
        if(cursor.moveToNext()) {
            newTitle = cursor.getString(1);
        }
        assertEquals("updated", newTitle);
        db.removeDiary(id);
    }

    @Test
    public void removeDiaryTest(){
        byte[] testBytes = {123};
        DiaryDB db = new DiaryDB(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Diary diary = new Diary(2, "old", "test description", "20190202", testBytes, 12.5, 30);

        int id = db.addDiary(diary.getTitle(), diary.getDesc(), diary.getDate(), diary.getImage(), diary.getLat(), diary.getLng());
        int count = db.getDiaries().getCount();
        db.removeDiary(id);

        int newCount = db.getDiaries().getCount();
        assertEquals(count - 1, newCount);
    }

    @Test
    public void selectFromDateTest(){
        byte[] testBytes = {123};
        Diary diary = new Diary(3, "dateDiary", "test description", "20190303", testBytes, 12.5, 30);

        DiaryDB db = new DiaryDB(InstrumentationRegistry.getInstrumentation().getTargetContext());
        int id = db.addDiary(diary.getTitle(), diary.getDesc(), diary.getDate(), diary.getImage(), diary.getLat(), diary.getLng());

        Cursor cursor = db.getDiaries(2019,3,3);
        String diaryTitle = "";
        if(cursor.moveToNext()) {
            diaryTitle = cursor.getString(1);
        }
        assertEquals("dateDiary", diaryTitle);
        db.removeDiary(id);
    }

    //============================================================================================
    //BACK UP TEST
    //============================================================================================

    @Test
    public void addBackupTest(){
        byte[] testBytes = {123};
        Diary diary = new Diary(4, "backuptest", "backup", "20190404", testBytes, 12.5, 30);

        DiaryDB db = new DiaryDB(InstrumentationRegistry.getInstrumentation().getTargetContext());
        int id = db.addDiary(diary.getTitle(), diary.getDesc(), diary.getDate(), diary.getImage(), diary.getLat(), diary.getLng());

        String createDT = db.getDiaryCreateDT(id);
        boolean backupExists = db.existsInBackup(createDT);

        assertEquals(true, backupExists);
        db.removeDiary(id);
    }

    @Test
    public void updateBackupTest(){
        byte[] testBytes = {123};
        Diary diary = new Diary(5, "backuptest", "backup", "20190505", testBytes, 12.5, 30);

        DiaryDB db = new DiaryDB(InstrumentationRegistry.getInstrumentation().getTargetContext());
        int id = db.addDiary(diary.getTitle(), diary.getDesc(), diary.getDate(), diary.getImage(), diary.getLat(), diary.getLng());

        Diary newDiary = new Diary(5, "updateBackup", "updated", "20190505", testBytes, 12.5, 30);
        db.updateDiary(id, diary.getTitle(), diary.getDesc(), diary.getDate(), diary.getImage());

        String createDT = db.getDiaryCreateDT(id);
        Cursor cursor = db.getBackup(createDT);
        String type = "";
        if(cursor.moveToNext()) {
            type = cursor.getString(1);
        }

        assertEquals("update", type);
        db.removeDiary(id);
    }
}
