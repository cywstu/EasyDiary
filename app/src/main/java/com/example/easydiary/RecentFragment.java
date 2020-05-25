package com.example.easydiary;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//https://www.youtube.com/watch?v=_4i5Jk5RnFA
public class RecentFragment extends Fragment {

    private ListView diaryList;
    private TextView lblMessage;

    private String mode;
    private int year;
    private int month;
    private int day;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);

        diaryList = view.findViewById(R.id.diaryList);
        lblMessage = view.findViewById(R.id.lblMessage);

        mode = "create";
        Bundle bundle = getArguments();
        if(bundle != null){
            mode = bundle.getString("mode");
            year = bundle.getInt("diaryYear");
            month = bundle.getInt("diaryMonth");
            day = bundle.getInt("diaryDay");
        }
        listDiaries();

        return view;
    }


    public void listDiaries(){

        DiaryDB db = new DiaryDB(getActivity());
        Cursor cursor;
        if(mode.equals("create")) {
            cursor = db.getDiaries();
        }else{
            cursor = db.getDiaries(year, month, day);
        }

        final ArrayList<Diary> diaries = new ArrayList<>();
        while(cursor.moveToNext()){
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String desc = cursor.getString(2);
            String date = cursor.getString(3);
            byte[] image = cursor.getBlob(4);
            double lat = cursor.getDouble(5);
            double lng = cursor.getDouble(6);

            diaries.add(new Diary(id,title,desc,date,image,lat,lng));
        }

        if(diaries.size() == 0){
            lblMessage.setText(getResources().getText(R.string.recent_list_no_diary));
        }else{
            diaryList.setAdapter(new DiaryListAdapter(getActivity(), R.layout.diary_list_layout, diaries));
            diaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Diary currentDiary = diaries.get(position);

                    Bundle bundle = new Bundle();
                    bundle.putString("mode", "update");
                    bundle.putInt("diaryID", currentDiary.getId());
                    bundle.putString("diaryTitle", currentDiary.getTitle());
                    bundle.putString("diaryDesc", currentDiary.getDesc());
                    bundle.putString("diaryDate", currentDiary.getDate());
                    bundle.putByteArray("diaryImage", currentDiary.getImage());
                    bundle.putDouble("diaryLat", currentDiary.getLat());
                    bundle.putDouble("diaryLng", currentDiary.getLng());

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    CreateFragment createFragment = new CreateFragment();
                    createFragment.setArguments(bundle);

                    fragmentTransaction.replace(R.id.fragment_container, createFragment);
                    fragmentTransaction.commit();
                }
            });
        }

    }
}
