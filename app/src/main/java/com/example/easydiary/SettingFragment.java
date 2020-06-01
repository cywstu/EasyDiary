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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

//language
//https://www.youtube.com/watch?v=zILw5eV9QBQ
//firebase
//data : https://www.youtube.com/watch?v=r-g2R_COMqo
//image : https://www.youtube.com/watch?v=lPfQN-Sfnjw
//https://www.youtube.com/watch?v=7QnhepFaMLM
public class SettingFragment extends Fragment {

    private String DBName = "testDB";
    private ListView listSetting;

    private DatabaseReference dbref;
    private StorageReference storage;
    private ArrayList<Diary> onlineDiaries;
    private byte[] cImage;
    private String cTitle;
    private String cDesc;
    private String cDate;
    private Double cLat;
    private Double cLng;
    private String cCreateDT;
    private boolean isPushing;
    private int pushCount;
    private boolean isPulling;

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
        isPushing = false;
        isPulling =false;
        pushCount = 0;
        onlineDiaries = new ArrayList<Diary>();
        dbref = FirebaseDatabase.getInstance().getReference().child(DBName);
        storage = FirebaseStorage.getInstance().getReference().child(DBName);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && isPushing == false){
                    for(DataSnapshot curDS : dataSnapshot.getChildren()){
                        Log.d("Firebase_Data-ID", "createDT: " + curDS.child("CreateDT").getValue() + " | " + curDS.getValue().toString());
                        boolean alreadyExist = false;
                        for(int i = 0; i < onlineDiaries.size(); i++){
                            if(onlineDiaries.get(i).getCreateDT().equals(cCreateDT)){
                                alreadyExist = true;
                                break;
                            }
                        }
                        if(!alreadyExist){
                            isPulling = true;
                            cCreateDT = curDS.child("CreateDT").getValue().toString();
                            cTitle = curDS.child("Title").getValue().toString();
                            cDesc = curDS.child("Desc").getValue().toString();
                            cDate = curDS.child("Date").getValue().toString();
                            cLat = Double.parseDouble(curDS.child("Lat").getValue().toString());
                            cLng = Double.parseDouble(curDS.child("Lng").getValue().toString());
                            cImage = null;
                            storage.child(cCreateDT).getBytes(1024 * 1024 * 10).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    cImage = bytes;
                                    Diary curDiary = new Diary(cTitle, cDesc, cDate, cImage, cLat, cLng, cCreateDT);
                                    onlineDiaries.add(curDiary);
                                    isPulling = false;
                                }
                            });
                        }
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
        backupPush();
        backupPull();
    }

    private void backupPush(){
        if(isPushing == false) {
            isPushing = true;
            Log.d("Push Start","true");
            DiaryDB db = new DiaryDB(getActivity());
            Cursor bCursor = db.getAllBackup();
            pushCount = bCursor.getCount() * 7;
            if (pushCount == 0) {
                Toast.makeText(getActivity(),"no backup data", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(),"count: " + bCursor.getCount(), Toast.LENGTH_SHORT).show();
                while (bCursor.moveToNext()) {
                    String createDT = bCursor.getString(0);
                    String type = bCursor.getString(1);
                    Toast.makeText(getActivity(), createDT + " | " + type, Toast.LENGTH_SHORT).show();
                    if (type.equals("update") || type.equals("add")) {
                        DatabaseReference childRef = dbref.child(createDT);
                        Cursor dCursor = db.getDiary(createDT);
                        dCursor.moveToNext();
                        childRef.child("Title").setValue(dCursor.getString(1), pushSuccessHandler);
                        childRef.child("Desc").setValue(dCursor.getString(2), pushSuccessHandler);
                        childRef.child("Date").setValue(dCursor.getString(3), pushSuccessHandler);
                        childRef.child("Lat").setValue(dCursor.getString(5), pushSuccessHandler);
                        childRef.child("Lng").setValue(dCursor.getString(6), pushSuccessHandler);
                        childRef.child("CreateDT").setValue(dCursor.getString(7), pushSuccessHandler);
                        storage.child(createDT).putBytes(dCursor.getBlob(4)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                pushCount --;
                                Log.d("push count", "count : " + pushCount);
                                if(pushCount == 0){
                                    isPushing = false;
                                    new DiaryDB(getActivity()).removeAllBackup();
                                }
                            }
                        });
                        Toast.makeText(getActivity(), "backup : " + createDT + " | " + dCursor.getString(1), Toast.LENGTH_SHORT).show();
                        //db.removeBackup(createDT);
                    }
                }
            }

            //isPushing = false;
            Toast.makeText(getActivity(), "backup push completed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(),getResources().getString(R.string.message_sync_working), Toast.LENGTH_SHORT).show();
        }
    }

    private DatabaseReference.CompletionListener pushSuccessHandler = new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
            pushCount --;
            Log.d("push count", "count : " + pushCount);
            if(pushCount == 0){
                isPushing = false;
                new DiaryDB(getActivity()).removeAllBackup();
            }
        }
    };

    private void backupPull(){
        Log.d("Pull start", "true");
        DiaryDB db = new DiaryDB(getActivity());
        Log.d("onlinediary size", ""+onlineDiaries.size());
        if(onlineDiaries.size() == 0){
            Toast.makeText(getActivity(), getResources().getString(R.string.message_sync_noneed), Toast.LENGTH_SHORT).show();
        }else {
            for (int i = 0; i < onlineDiaries.size(); i++) {
                Diary curOnlineDiary = onlineDiaries.get(i);
                Cursor localCursor = db.getDiary(curOnlineDiary.getCreateDT());
                Toast.makeText(getActivity(), "curCreateDT : " + curOnlineDiary.getCreateDT(), Toast.LENGTH_SHORT).show();
                if (localCursor.getCount() == 0) {
                    db.addDiary(curOnlineDiary.getTitle(), curOnlineDiary.getDesc(), curOnlineDiary.getDate(), curOnlineDiary.getImage(), curOnlineDiary.getLat(), curOnlineDiary.getLng(), curOnlineDiary.getCreateDT());
                    Toast.makeText(getActivity(), "added : " + curOnlineDiary.getCreateDT(), Toast.LENGTH_SHORT).show();
                }
                localCursor.close();
            }
            Toast.makeText(getActivity(), getResources().getString(R.string.message_sync_complete), Toast.LENGTH_SHORT).show();
        }
    }
}
