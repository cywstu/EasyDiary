package com.example.easydiary;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

//language
//https://www.youtube.com/watch?v=zILw5eV9QBQ
public class SettingFragment extends Fragment {

    ListView listSetting;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        loadLocale();
        listSetting = view.findViewById(R.id.list_setting);

        //adapter
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.list_setting));

        //list click action
        listSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(id == 0){
                    showLanguageDialog();
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

                //...
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

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
}
