package com.frc2135.android.frc_scout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.frc2135.android.frc_scout.databinding.ActivityScoutingTabbedBinding;

public class ScoutingActivity extends AppCompatActivity
{
    private static final String TAG = "ScoutingActivity";

    private MatchData m_matchData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Connects this Java class to the XML file activity_main, linking the UI to the controller layer.
        ActivityScoutingTabbedBinding binding = ActivityScoutingTabbedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializes FragmentManager so that we can host a fragment within our activity.
        final FragmentManager fm = getSupportFragmentManager();
        // Designates that chosen fragment will be housed within fragmentContainer, a frame layout in the activity's XML.
        if (fm.findFragmentById(R.id.fragmentContainer) == null)
        {
            fm.beginTransaction().add(R.id.fragmentContainer, createInitialFragment()).commit();
        }

        binding.navView.setOnItemSelectedListener(item -> {
            updateCurrentFragmentData();

            Fragment fragment;
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_teleop)
            {
                fragment = new TeleopFragment();
            }
            else if (itemId == R.id.navigation_auton)
            {
                fragment = new AutonFragment();
            }
            else if (itemId == R.id.navigation_endgame)
            {
                fragment = new EndgameFragment();
            }
            else
            {
                return false;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        });

        String matchId = getIntent().getStringExtra("match_ID");
        Log.d(TAG, "MatchId = " + matchId);
        m_matchData = MatchHistory.get(getApplicationContext()).getMatch(matchId);
    }

    //This method returns an instance of the class MatchFragment, so that whichever XML file is linked to Match Fragment will be placed in fragmentContainer
    private void updateCurrentFragmentData()
    {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof AutonFragment)
        {
            ((AutonFragment) f).updateAutonData();
        }
        else if (f instanceof TeleopFragment)
        {
            ((TeleopFragment) f).updateTeleopData();
        }
        else if (f instanceof EndgameFragment)
        {
            ((EndgameFragment) f).updateEndgameData();
        }
    }

    protected Fragment createInitialFragment()
    {
        return new AutonFragment();
    }

    protected MatchData getCurrentMatch()
    {
        return m_matchData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
