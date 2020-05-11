package com.example.easydiary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

//firebase:
//www.youtube.com/watch?v=lnidtzL71ZA
//firebase for images;
//www.youtube.com/watch?v=h62bcMwahTU
//www.youtube.com/watch?v=Cofhptx6RRA
//www.youtube.com/watch?v=Zy2DKo0v-OY

public class SettingFragment extends PreferenceFragment {

    FrameLayout framePreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
