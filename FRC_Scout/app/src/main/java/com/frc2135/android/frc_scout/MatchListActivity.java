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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "MatchListActivity created.");
        super.onCreate(savedInstanceState);

        // Apply theme preference before setting content view
        Preferences.get(this).applyTheme();

        // Use View Binding for layout inflation
        MatchListActivityBinding binding = MatchListActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializes FragmentManager to host the match list fragment
        FragmentManager fm = getSupportFragmentManager();

        // Designates that chosen fragment will be housed within fragmentContainer
        if (fm.findFragmentById(R.id.fragmentContainer) == null)
        {
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, createMatchListFragment())
                    .commit();
        }
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
