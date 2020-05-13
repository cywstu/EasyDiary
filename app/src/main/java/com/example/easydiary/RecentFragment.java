package com.example.easydiary;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class RecentFragment extends Fragment {

    private ListView diaryList;
    private TextView lblMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);

        diaryList = view.findViewById(R.id.diaryList);
        lblMessage = view.findViewById(R.id.lblMessage);
        listDiaries();

        return view;
    }


    public void listDiaries(){

        DiaryDB db = new DiaryDB(getActivity());
        Cursor cursor = db.getDiaries();

        ArrayList<Diary> Diaries = new ArrayList<>();
        while(cursor.moveToNext()){
            int id = cursor.getInt(1);
            String title = cursor.getString(2);
            String desc = cursor.getString(3);
            String date = cursor.getString(4);
            byte[] image = cursor.getBlob(5);

            Diaries.add(new Diary(id,title,desc,date,image));
        }

        if(Diaries.size() == 0){
            lblMessage.setText("nothing yet!");
            //lblMessage.setText(getResources().getText(R.string.app_name));
        }else {
            diaryList.setAdapter(new DiaryListAdapter(getActivity(), R.layout.diary_list_layout, Diaries));
            diaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    
                }
            });
        }

    }

}
