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
    private MatchListActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
        // Apply theme preference before super.onCreate to ensure the correct theme is applied early
        Preferences.get(this).applyTheme();
        super.onCreate(savedInstanceState);

        // Use View Binding for layout inflation
        binding = MatchListActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(R.string.title_match_list);
        }

        updateToolbarTeamIndex();

        // Initializes FragmentManager to host the match list fragment
        FragmentManager fm = getSupportFragmentManager();

        // Designates that chosen fragment will be housed within fragmentContainer
        if (fm.findFragmentById(R.id.fragmentContainer) == null)
        {
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, createMatchListFragment())
                    .commit();
        }

        getSupportFragmentManager().setFragmentResultListener("team_index_changed", this, (requestKey, result) -> {
            Log.d(TAG, "Team index changed, updating toolbar");
            updateToolbarTeamIndex();
        });
    }

    public void updateToolbarTeamIndex()
    {
        String indexStr = Settings.get(this).getTeamIndexStr();
        binding.toolbarTeamIndex.setText(String.format("Team Index %s", indexStr));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateToolbarTeamIndex();
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
