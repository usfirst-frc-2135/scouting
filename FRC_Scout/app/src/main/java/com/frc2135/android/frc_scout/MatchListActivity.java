package com.frc2135.android.frc_scout;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.frc2135.android.frc_scout.databinding.MatchListActivityBinding;

/**
 * Main activity for displaying and managing the list of scouted matches.
 * This activity hosts the {@link MatchListFragment} and provides a top-level toolbar with the current team index.
 */
public class MatchListActivity extends AppCompatActivity
{
    private static final String TAG = "MatchListActivity";
    private MatchListActivityBinding m_binding;
    private Settings m_settings;

    /**
     * Initializes the activity, sets up the action bar, and loads the {@link MatchListFragment}.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreate");
        // Apply theme preference before super.onCreate to ensure the correct theme is applied early
        Preferences.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);

        m_settings = Settings.getInstance(this);

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
            Log.i(TAG, "Team index changed, updating toolbar");
            updateToolbarTeamIndex();
        });
    }

    /**
     * Updates the toolbar text with the current team index from application settings.
     */
    public void updateToolbarTeamIndex()
    {
        Log.d(TAG, "updateToolbarTeamIndex");
        String indexStr = (m_settings != null) ? m_settings.getTeamIndexStr() : "unknown";
        m_binding.matchListActivityToolbarTeamIndex.setText(String.format(getString(R.string.team_index_label), indexStr));
    }

    /**
     * Called when the activity is becoming visible to the user.
     * Ensures the toolbar team index is up to date.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume");
        updateToolbarTeamIndex();
    }

    /**
     * Perform any final cleanup before an activity is destroyed.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
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
