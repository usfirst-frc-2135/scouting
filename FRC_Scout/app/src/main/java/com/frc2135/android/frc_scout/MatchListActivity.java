package com.frc2135.android.frc_scout;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MatchListActivity extends AppCompatActivity
{
    private static final String TAG = "MatchListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        Log.i(TAG, "MatchListActivity created.");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.match_list_activity);

        // Initializes FragmentManager so that we can host a fragment within our activity.
        final FragmentManager fm = getSupportFragmentManager();

        // Designates that chosen fragment will be housed within fragmentContainer, a frame layout in the activity's XML.
        if (fm.findFragmentById(R.id.fragmentContainer) == null)
        {
            fm.beginTransaction().add(R.id.fragmentContainer, createMatchListFragment()).commit();
        }

    }

    protected Fragment createMatchListFragment()
    {
        setContentView(R.layout.match_list_activity);
        return new MatchListFragment();
    }
    
}
