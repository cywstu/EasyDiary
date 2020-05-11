package com.example.easydiary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//audio record&play from:
//  www.youtube.com/watch?v=-eFoX4K59qM
//camera from:
//  www.youtube.com/watch?v=ondCeqlAwEI
//  www.youtube.com/watch?v=u5PDdg1G4Q4
//save image:
//  stackoverflow.com/questions/649154/save-bitmap-to-location
//google map
//  www.youtube.com/watch?v=eiexkzCI8m8

public class CreateFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create, container, false);
    }
}
