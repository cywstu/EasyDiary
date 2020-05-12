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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecentFragment extends Fragment {

    private TextView lblTitle;
    private TextView lblDesc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);

        lblTitle = view.findViewById(R.id.lblTitle);
        lblDesc = view.findViewById(R.id.lblDesc);

        DiaryDB db = new DiaryDB(getActivity());
        //db.addDiary("test","randomDesc",null);

        //list out all diaries
        Cursor cursor = db.getDiaries();
        Toast.makeText(getActivity(),"count: " + cursor.getCount(), Toast.LENGTH_SHORT).show();
        showDiaries(cursor);
        //cursor.close();

        return view;
    }


    public void showDiaries(Cursor cursor){

        while(cursor.moveToNext()){
            lblTitle.setText(cursor.getString(1));
            lblDesc.setText(cursor.getString(2));
        }
        //image part
        //byte[] imageBytes = cursor.getBlob(2);
        //Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

    }

}
