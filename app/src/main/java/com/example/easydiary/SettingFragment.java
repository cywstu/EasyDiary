package com.example.easydiary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

//language
//https://www.youtube.com/watch?v=zILw5eV9QBQ
//firebase
//data : https://www.youtube.com/watch?v=r-g2R_COMqo
//image : https://www.youtube.com/watch?v=lPfQN-Sfnjw
public class SettingFragment extends Fragment {

    private String DBName = "testDB";
    private ListView listSetting;

    private DatabaseReference dbref;
    private StorageReference storage;
    private long maxId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        //language related
        loadLocale();
        listSetting = view.findViewById(R.id.list_setting);

        //adapter
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.list_setting));

        //firebase stuff
        maxId = 0;
        storage = FirebaseStorage.getInstance().getReference().child(DBName);
        dbref = FirebaseDatabase.getInstance().getReference().child(DBName);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot curDS : dataSnapshot.getChildren()){
                        Log.d("Firebase Data - ID", curDS.getValue().toString());
                        String title = (String)curDS.child("Title").getValue();
                        String desc = (String)curDS.child("Desc").getValue();
                        String date = (String)curDS.child("Date").getValue();
                        double lat = Double.parseDouble(curDS.child("Lat").getValue().toString());
                        double lng = Double.parseDouble(curDS.child("Lng").getValue().toString());
                        Log.d("Firebase Data - Content",title + " | " + desc + " | " + date + " | " + lat + " | " + lng);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        //list click action
        listSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(id == 0){
                    showLanguageDialog();
                }
                if (id == 1) {
                    backup();
                }
            }
        });
        listSetting.setAdapter(mAdapter);

        return view;
    }

    private void showLanguageDialog(){
        SharedPreferences prefs = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
        String curLanguage = prefs.getString("language","en");

        final String[] listItems = {"English", "中文"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        if(curLanguage == "en") {
            mBuilder.setTitle("Choose Language");
        }else if(curLanguage == "zh"){
            mBuilder.setTitle("請選擇語言");
        }
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                if(i == 0){
                    setLocale("en");
                    //getActivity().recreate();
                    Toast.makeText(getActivity(),"please restart the app!", Toast.LENGTH_SHORT).show();
                }else{
                    setLocale("zh");
                    //getActivity().recreate();
                    Toast.makeText(getActivity(), "請重新載入應用程式", Toast.LENGTH_SHORT).show();
                }

                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    //==========================================================================================
    //language stuff
    //==========================================================================================

    private void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getContext().getResources().updateConfiguration(config,getContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString("language",lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("language","en");
        setLocale(lang);
    }

    //==========================================================================================
    //firebase
    //==========================================================================================

    private void backup(){
        String createDT = "20201225010203325";
        byte[] ba = {1,2,3};
        DatabaseReference child = dbref.child(createDT);
        child.child("Title").setValue("backup");
        child.child("Desc").setValue("backup desc");
        child.child("Date").setValue("20201225");
        child.child("Lat").setValue(12.5);
        child.child("Lng").setValue(30);
        storage.child(createDT).putBytes(ba);

        Log.d("original byte",ba.toString());
        storage.child(createDT).getBytes(1024*1024*10).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d("returned byte", bytes.toString());
            }
        });
        //backupPush();
        //backupPull();
    }

    private void backupPush(){
        DiaryDB db = new DiaryDB(getActivity());
        Cursor bCursor = db.getAllBackup();
        while(bCursor.moveToNext()){
            String createDT = bCursor.getString(0);
            String type = bCursor.getString(1);
            if(type.equals("update") || type.equals("create")){
                Cursor dCursor = db.getDiary(createDT);
                dCursor.moveToNext();
                //prepare diary data
                String title = dCursor.getString(1);
                String desc = dCursor.getString(2);
                String date = dCursor.getString(3);
                byte[] image = dCursor.getBlob(4);
                double lat = dCursor.getDouble(5);
                double lng = dCursor.getDouble(6);

                DatabaseReference child = dbref.child(createDT);
                child.child("Title").setValue(dCursor.getString(1));
                child.child("Desc").setValue(dCursor.getString(2));
                child.child("Date").setValue(dCursor.getString(3));
                child.child("Image").setValue(dCursor.getBlob(4));
                child.child("Lat").setValue(dCursor.getDouble(5));
                child.child("Lng").setValue(dCursor.getDouble(6));
                child.child("CreateDT").setValue(dCursor.getString(7));
            }
        }

        Toast.makeText(getActivity(), "backup completed", Toast.LENGTH_SHORT).show();
    }

    private void backupPull(){
        dbref.getKey();
    }
}
