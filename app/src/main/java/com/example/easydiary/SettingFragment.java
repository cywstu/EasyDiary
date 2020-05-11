package com.example.easydiary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//firebase:
//www.youtube.com/watch?v=lnidtzL71ZA
//firebase for images;
//www.youtube.com/watch?v=h62bcMwahTU
//www.youtube.com/watch?v=Cofhptx6RRA
//www.youtube.com/watch?v=Zy2DKo0v-OY

public class SettingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }
}
