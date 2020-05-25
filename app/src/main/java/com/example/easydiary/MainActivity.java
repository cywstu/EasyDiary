package com.example.easydiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //nav bar at the bottom
    private BottomNavigationView bottomNav;
    final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setting language stuff
        loadLocale();
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        //
        setContentView(R.layout.activity_main);

        //navigation bar problem
        //solution from : stackoverflow.com/questions/41352934/force-showing-icon-and-title-in-bottomnavigationview-support-android/47407412
        bottomNav = findViewById(R.id.navBar);
        disableShiftMode(bottomNav);

        //navListener: change fragment when click on nav items
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        //start with "recent" fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecentFragment()).commit();

        //request permissions
        if (!checkPermissions()) {
            requestPermission();
        }

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

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            menuView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            menuView.buildMenuView();
    }

    //==========================================================================================================
    //---permission
    //==========================================================================================================
    //checking for permission
    private boolean checkPermissions() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int access_fine_location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED &&
                access_fine_location == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    //==========================================================================================================
    //--- language
    //==========================================================================================================
    private void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString("language",lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("language","en");
        setLocale(lang);
    }
}
