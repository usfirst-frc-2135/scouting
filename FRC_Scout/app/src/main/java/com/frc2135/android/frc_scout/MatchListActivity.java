package com.frc2135.android.frc_scout;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.frc2135.android.frc_scout.databinding.MatchListActivityBinding;

/**
 * Main activity for displaying and managing the list of scouted matches.
 * This activity hosts the {@link MatchListFragment}.
 */
public class MatchListActivity extends AppCompatActivity
{
    private static final String TAG = "MatchListActivity";
    private MatchListActivityBinding m_binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
        // Apply theme preference before super.onCreate to ensure the correct theme is applied early
        Preferences.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);

        // Use View Binding for layout inflation
        m_binding = MatchListActivityBinding.inflate(getLayoutInflater());
        setContentView(m_binding.getRoot());

        setSupportActionBar(m_binding.matchListActivityToolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(R.string.title_match_list);
        }

        updateToolbarTeamIndex();

        // Initializes FragmentManager to host the match list fragment
        FragmentManager fm = getSupportFragmentManager();

        // Designates that chosen fragment will be housed within match_list_activity_fragment_container
        if (fm.findFragmentById(R.id.match_list_activity_fragment_container) == null)
        {
            fm.beginTransaction()
                    .add(R.id.match_list_activity_fragment_container, createMatchListFragment())
                    .commit();
        }

        getSupportFragmentManager().setFragmentResultListener("team_index_changed", this, (requestKey, result) -> {
            Log.d(TAG, "Team index changed, updating toolbar");
            updateToolbarTeamIndex();
        });
    }

    /**
     * Updates the toolbar text with the current team index from settings.
     */
    public void updateToolbarTeamIndex()
    {
        Log.d(TAG, "updateToolbarTeamIndex()");
        String indexStr = Settings.getInstance(this).getTeamIndexStr();
        m_binding.matchListActivityToolbarTeamIndex.setText(String.format(getString(R.string.team_index_label), indexStr));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
        updateToolbarTeamIndex();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        m_binding = null;
    }

    /**
     * Creates the fragment to be displayed in this activity.
     *
     * @return a new instance of {@link MatchListFragment}
     */
    protected Fragment createMatchListFragment()
    {
        return new MatchListFragment();
    }
}
