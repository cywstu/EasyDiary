package com.example.easydiary;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;

//*****************************************CAUTION*************************************************
//have used custom calender, named mCalender
//found from : stackoverflow.com/questions/47450529/how-to-highlight-multiple-dates-in-a-android-calendarview-programmatically
// --> github.com/SpongeBobSun/mCalendarView?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=2420
//***************************************************************************************************
public class CalendarFragment extends Fragment {

    private MCalendarView calendarView;
    private ArrayList allDates;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        allDates = new ArrayList<String>();
        calendarView = (MCalendarView) view.findViewById(R.id.calendar);
        calendarView.setMarkedStyle(MarkStyle.DOT, Color.GREEN);
        prepareCalendar();

        calendarView.setOnDateClickListener(new OnDateClickListener(){
            @Override
            public void onDateClick(View view, DateData date){
                //fix date number < 10 without 0
                String strTargetDate = ""+date.getYear();
                if(date.getMonth() < 10){ strTargetDate += "0" + date.getMonth(); } else { strTargetDate += date.getMonth(); }
                if(date.getDay() < 10){ strTargetDate += "0" + date.getDay(); } else {strTargetDate += date.getDay(); }
                if(allDates.contains(strTargetDate)){
                    Bundle bundle = new Bundle();
                    bundle.putString("mode", "calendar");
                    bundle.putInt("diaryYear", date.getYear());
                    bundle.putInt("diaryMonth", date.getMonth());
                    bundle.putInt("diaryDay", date.getDay());

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    RecentFragment recentFragment = new RecentFragment();
                    recentFragment.setArguments(bundle);

                    fragmentTransaction.replace(R.id.fragment_container, recentFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        return view;
    }

    public void highlightDate(int y, int m, int d){ calendarView.markDate(y, m, d); }
    public void prepareCalendar(){
        DiaryDB db = new DiaryDB(getActivity());
        Cursor cursor = db.getDiaries();
        int count = 0;
        while(cursor.moveToNext()){
            String strDate = cursor.getString(3);
            //Toast.makeText(getActivity(), year + " | " + month + " | " + day, Toast.LENGTH_SHORT).show();

            if(!allDates.contains(strDate)) {
                allDates.add(strDate);
                int[] intDate = toIntArray(strDate);
                highlightDate(intDate[0], intDate[1], intDate[2]);
            }
        }
    }
    public int[] toIntArray(String strDate){
        int year = Integer.parseInt(strDate.substring(0,4));
        int month = Integer.parseInt(strDate.substring(4,6));
        int day = Integer.parseInt(strDate.substring(6,8));
        int[] intDate = {year,month,day};
        return intDate;
    }
}
