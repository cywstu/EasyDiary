package com.example.easydiary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DiaryListAdapter extends ArrayAdapter<Diary> {

    private Context context;
    private int resource;
    private ArrayList<Diary> diaries;

    public DiaryListAdapter(Context context, int resource, ArrayList<Diary> list){
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        diaries = list;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        try {
            if(v == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = layoutInflater.inflate(resource, parent, false);
            }

            TextView lblDate = v.findViewById(R.id.lblDate);
            TextView lblTitle = v.findViewById(R.id.lblTitle);
            TextView lblDesc = v.findViewById(R.id.lblDesc);
            ImageView imgDiary = v.findViewById(R.id.imgDiary);

            String strDate = diaries.get(position).getDate();
            lblDate.setText(strDate.substring(0,4) + "-" + (Integer.parseInt(strDate.substring(4,6))+1) + "-" + strDate.substring(6,8));
            lblTitle.setText(diaries.get(position).getTitle());
            lblDesc.setText(diaries.get(position).getDesc());
            byte[] imageBytes = diaries.get(position).getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imgDiary.setImageBitmap(bitmap);

        }catch(Exception e){
            e.printStackTrace();
        }
        return v;
    }
}
