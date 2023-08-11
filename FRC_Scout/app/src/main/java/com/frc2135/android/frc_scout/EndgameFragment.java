package com.frc2135.android.frc_scout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class EndgameFragment extends Fragment
{

    private RadioGroup m_endGameRadioGroup;
    private RadioButton m_radio_endGameParked;
    private RadioButton m_radio_endGameDocked;
    private RadioButton m_radio_endGameEngaged;
    private CheckBox m_diedCheckbox;
    private EditText m_commentText;
    private MatchData m_matchData;

    public static final String QRTAG = "qr";
    private static final String TAG = "EndgameFragment";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ScoutingActivity activity = (ScoutingActivity) getActivity();
        if(activity != null) {
            m_matchData = activity.getCurrentMatch();
            if(m_matchData != null) {
                String teamNumber = m_matchData.stripTeamNamePrefix(m_matchData.getTeamNumber());
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if( actionBar != null) {
                    actionBar.setTitle("Endgame               Scouting Team " + teamNumber + "          Match " + m_matchData.getMatchNumber());
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        //Creates a view using the specific fragment layout.
        View v = inflater.inflate(R.layout.endgame_fragment, parent, false);

        //Connects the checkbox for if the robot dies and sets up a listener to detect when the checked status is changed
        m_diedCheckbox = v.findViewById(R.id.died_checkbox_true);
        m_diedCheckbox.setChecked(m_matchData.getDied());// Default is unchecked
        m_diedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                updateEndgameData();
            }
        });

        m_endGameRadioGroup = v.findViewById(R.id.endgame_charge_level);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        RadioButton radio_endGameNone = v.findViewById(R.id.level_end_game_none);//Sets up radio button that corresponds to 0
        radio_endGameNone.setChecked(true);
        m_radio_endGameParked = v.findViewById(R.id.level_end_game_parked);//Sets up radio button that corresponds to 1
        m_radio_endGameParked.setChecked(false);
        m_radio_endGameDocked = v.findViewById(R.id.level_end_game_docked);//Sets up radio button that corresponds to 2
        m_radio_endGameDocked.setChecked(false);
        m_radio_endGameEngaged = v.findViewById(R.id.level_end_game_engaged);//Sets up radio button that corresponds to 3
        m_radio_endGameEngaged.setChecked(false);

        int x = m_matchData.getEndgameChargeLevel();
        if (x == 0)
            radio_endGameNone.setChecked(true);
        else if (x == 1)
            m_radio_endGameParked.setChecked(true);
        else if (x == 2)
            m_radio_endGameDocked.setChecked(true);
        else if (x == 3)
            m_radio_endGameEngaged.setChecked(true);

        m_endGameRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {

                //Changes m_matchData's climb variable according to which radio button is selected
                m_matchData.setEndgameChargeLevel(getCurrentEndgameChargeLevel());
            }
        });

        //Sets up an EditText that allows users to input any additional comments
        m_commentText = v.findViewById(R.id.comments);
        m_commentText.setHint("Enter comments here");
        m_commentText.setText(m_matchData.getComment());

        ImageButton qrButton = v.findViewById(R.id.gen_QR);
        qrButton.setOnClickListener(new View.OnClickListener()
        {
            //Setting an onClickListener makes it so that our button actually senses for when it is clicked, and when it is clicked, it will proceed with onClick()

            @Override
            public void onClick(View view)
            {
                //Uses intents to start the QR code dialog
                updateEndgameData();
                Log.d(TAG, "Clicked on QR Code");
                FragmentActivity fActivity = getActivity();
                if(fActivity != null) {
                    FragmentManager fm = fActivity.getSupportFragmentManager();
                    QRFragment dialog = QRFragment.newInstance(m_matchData);
                    dialog.show(fm, QRTAG);
                }
            }
        });

        Button mNextButton = v.findViewById(R.id.nav_to_menu_button);
        mNextButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                // Save the latest Scouter and MatchData JSON files.
                Log.d(TAG, "EndgameFragment DONE onClick() saving latest match and Scouter files\n");
                if(!MatchHistory.get(getActivity()).saveScouterData())
                {
                    Log.d(TAG, "ERROR - unable to save Scouter Data!");
                    // TODO - issue a toast msg here??
                }
                if(!MatchHistory.get(getActivity()).saveMatchData(m_matchData))
                {
                    Log.d(TAG, "ERROR - unable to save Match Data!");
                    // TODO - issue a toast msg here??
                }

                // Go back to MatchListActivity page
                Intent i = new Intent(getActivity(), MatchListActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                requireActivity().finish();
            }
        });

        return v;
    }

    public int getCurrentEndgameChargeLevel()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_endGameRadioGroup.getCheckedRadioButtonId() == m_radio_endGameParked.getId())
        {
            rtn = 1;
        }
        else if (m_endGameRadioGroup.getCheckedRadioButtonId() == m_radio_endGameDocked.getId())
        {
            rtn = 2;
        }
        else if (m_endGameRadioGroup.getCheckedRadioButtonId() == m_radio_endGameEngaged.getId())
        {
            rtn = 3;
        }
        return rtn;
    }

    public void updateEndgameData()
    {
        m_matchData.setEndgameChargeLevel(getCurrentEndgameChargeLevel());
        m_matchData.setDied(m_diedCheckbox.isChecked());
        m_matchData.setComment(m_commentText.getText().toString());
    }
}
