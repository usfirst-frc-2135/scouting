package com.frc2135.android.frc_scout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ScoutingActivity extends AppCompatActivity
{
    private static final String TAG = "ScoutingActivity";

    private MatchData m_matchData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Connects this Java class to the XML file activity_main, linking the UI to the controller layer.
        setContentView(R.layout.activity_scouting_tabbed);

        // Initializes FragmentManager so that we can host a fragment within our activity.
        final FragmentManager fm = getSupportFragmentManager();
        final Fragment[] fragment = { fm.findFragmentById(R.id.fragmentContainer) };
        fragment[0] = createFragment();

        // Designates that chosen fragment will be housed within fragmentContainer, a frame layout in the activity's XML.
        fm.beginTransaction().add(R.id.fragmentContainer, fragment[0]).commit();
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_auton, R.id.navigation_teleop, R.id.navigation_endgame).build();

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.navigation_teleop:
                        Fragment f = (ScoutingActivity.this).getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                        if (f instanceof AutonFragment)
                        {
                            ((AutonFragment) f).updateAutonData();
                        }
                        if (f instanceof TeleopFragment)
                        {
                            ((TeleopFragment) f).updateTeleopData();
                        }
                        if (f instanceof EndgameFragment)
                        {
                            ((EndgameFragment) f).updateEndgameData();
                        }

                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                        TeleopFragment fragment1 = new TeleopFragment();
                        fragmentTransaction.replace(R.id.fragmentContainer, fragment1);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;

                    case R.id.navigation_auton:
                        Fragment f1 = (ScoutingActivity.this).getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                        if (f1 instanceof AutonFragment)
                        {
                            ((AutonFragment) f1).updateAutonData();
                        }
                        if (f1 instanceof TeleopFragment)
                        {
                            ((TeleopFragment) f1).updateTeleopData();
                        }
                        if (f1 instanceof EndgameFragment)
                        {
                            ((EndgameFragment) f1).updateEndgameData();
                        }
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        AutonFragment fragment2 = new AutonFragment();
                        fragmentTransaction.replace(R.id.fragmentContainer, fragment2);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;

                    case R.id.navigation_endgame:
                        Fragment f3 = (ScoutingActivity.this).getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                        if (f3 instanceof AutonFragment)
                        {
                            ((AutonFragment) f3).updateAutonData();
                        }
                        if (f3 instanceof TeleopFragment)
                        {
                            ((TeleopFragment) f3).updateTeleopData();
                        }
                        if (f3 instanceof EndgameFragment)
                        {
                            ((EndgameFragment) f3).updateEndgameData();
                        }
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        EndgameFragment fragment4 = new EndgameFragment();
                        fragmentTransaction.replace(R.id.fragmentContainer, fragment4);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                }
                return true;
            }
        });

        String matchId = getIntent().getStringExtra("match_ID");
        Log.d(TAG, "MatchId = " + matchId);
        m_matchData = MatchHistory.get(getApplicationContext()).getMatch(matchId);
    }

    //This method returns an instance of the class MatchFragment, so that whichever XML file is linked to Match Fragment will be placed in fragmentContainer
    protected Fragment createFragment()
    {
        setContentView(R.layout.activity_scouting_tabbed);
        return new com.frc2135.android.frc_scout.AutonFragment();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
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
