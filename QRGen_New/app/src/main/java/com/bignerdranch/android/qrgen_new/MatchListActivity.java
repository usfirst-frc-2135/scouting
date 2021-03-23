package com.bignerdranch.android.qrgen_new;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MatchListActivity extends AppCompatActivity {
    private static final String TAG =  "MatchListActivity";


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "Activity created.");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_list_activity);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if(fragment == null){
            fragment = createMatchListFragment();
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }

        Fragment fragment2 = fm.findFragmentById(R.id.fragmentContainer1);
        if(fragment2 == null){
            fragment2 = createFilterFragment();
            fm.beginTransaction().add(R.id.fragmentContainer1, fragment2).commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected Fragment createMatchListFragment(){
        setContentView(R.layout.match_list_activity);
        return new MatchListFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected Fragment createFilterFragment(){
        setContentView(R.layout.match_list_activity);
        return new FilterFragment();
    }


}
