package com.example.easydiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //nav bar at the bottom
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //navigation bar problem
        //solution from : stackoverflow.com/questions/41352934/force-showing-icon-and-title-in-bottomnavigationview-support-android/47407412
        bottomNav = findViewById(R.id.navBar);
        disableShiftMode(bottomNav);

        //navListener: change fragment when click on nav items
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        //start with "recent" fragment
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecentFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_recent:
                            selectedFragment = new RecentFragment();
                            break;
                        case R.id.nav_calender:
                            selectedFragment = new CalendarFragment();
                            break;
                        case R.id.nav_create:
                            selectedFragment = new CreateFragment();
                            break;
                        case R.id.nav_setting:
                            selectedFragment = new SettingFragment();
                            break;
                    }

                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };

    /*
    //for testing buttons
    public void onClick(View view){

        DiaryDB db = new DiaryDB(this);
        //db.addDiary(diary);
        ArrayList resultList = db.getAllDiary();

        Toast.makeText(this, (String)resultList.get(0), Toast.LENGTH_LONG).show();

    }
    */

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            menuView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            menuView.buildMenuView();
    }
}
