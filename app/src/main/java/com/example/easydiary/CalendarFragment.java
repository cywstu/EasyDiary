package com.example.easydiary;

import androidx.annotation.Nullable;
import android.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//*****************************************CAUTION*************************************************
//have used custom calender, named mCalender
//found from : stackoverflow.com/questions/47450529/how-to-highlight-multiple-dates-in-a-android-calendarview-programmatically
// --> github.com/SpongeBobSun/mCalendarView?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=2420
//***************************************************************************************************

public class CalendarFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }
}
