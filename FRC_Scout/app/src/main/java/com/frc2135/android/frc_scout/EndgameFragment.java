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

    private RadioGroup m_endGameRadioGroupStage;
    private RadioButton m_radio_endGameParked;
    private RadioButton m_radio_endGameOnstage;
    private RadioGroup m_endGameRadioGroupHarmony;
    private RadioButton m_radio_endGame1;
    private RadioButton m_radio_endGame2;
    private CheckBox m_spotlitCheckbox;
    private CheckBox m_trapCheckbox;
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
        if (activity != null)
        {
            m_matchData = activity.getCurrentMatch();
            if (m_matchData != null)
            {
                String teamNumber = m_matchData.stripTeamNamePrefix(m_matchData.getTeamNumber());
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if (actionBar != null)
                {
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

        m_spotlitCheckbox = v.findViewById(R.id.spotlit_checkbox_true);
        m_spotlitCheckbox.setChecked(m_matchData.getEndgameSpotLit());// Default is unchecked
        m_spotlitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                updateEndgameData();
            }
        });

        m_trapCheckbox = v.findViewById(R.id.trap_checkbox_true);
        m_trapCheckbox.setChecked(m_matchData.getEndgameTrap());// Default is unchecked
        m_trapCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                updateEndgameData();
            }
        });

        m_endGameRadioGroupStage = v.findViewById(R.id.endgame_stage);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        RadioButton radio_endGameNone = v.findViewById(R.id.level_end_game_none);//Sets up radio button that corresponds to 0
        radio_endGameNone.setChecked(true);
        m_radio_endGameParked = v.findViewById(R.id.level_end_game_parked);//Sets up radio button that corresponds to 1
        m_radio_endGameParked.setChecked(false);
        m_radio_endGameOnstage = v.findViewById(R.id.level_end_game_onstage);//Sets up radio button that corresponds to 2
        m_radio_endGameOnstage.setChecked(false);

        //Harmony groups set disabled by default
        m_endGameRadioGroupHarmony = v.findViewById(R.id.endgame_harmony);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        RadioButton radio_endGame0 = v.findViewById(R.id.end_game_harmony_default);//Sets up radio button that corresponds to 0
        radio_endGame0.setChecked(true);
        radio_endGame0.setEnabled(false);
        m_radio_endGame1 = v.findViewById(R.id.level_end_game_harmony_1);//Sets up radio button that corresponds to 1
        m_radio_endGame1.setChecked(false);
        m_radio_endGame1.setEnabled(false);
        m_radio_endGame2 = v.findViewById(R.id.level_end_game_harmony_2);//Sets up radio button that corresponds to 2
        m_radio_endGame2.setChecked(false);
        m_radio_endGame2.setEnabled(false);

        int y = m_matchData.getEndgameHarmony();
        if (y == 0)
            radio_endGame0.setChecked(true);
        else if (y == 1)
            m_radio_endGame1.setChecked(true);
        else if (y == 2)
            m_radio_endGame2.setChecked(true);

        int x = m_matchData.getEndgameStage();
        if (x == 0)
        {
            radio_endGameNone.setChecked(true);
            radio_endGame0.setEnabled(false);
            m_radio_endGame1.setEnabled(false);
            m_radio_endGame2.setEnabled(false);
            radio_endGame0.setChecked(true);
        }
        else if (x == 1)
        {
            m_radio_endGameParked.setChecked(true);
            radio_endGame0.setEnabled(false);
            m_radio_endGame1.setEnabled(false);
            m_radio_endGame2.setEnabled(false);
            radio_endGame0.setChecked(true);
        }
        else if (x == 2)
        {
            m_radio_endGameOnstage.setChecked(true);
            radio_endGame0.setEnabled(true);
            m_radio_endGame1.setEnabled(true);
            m_radio_endGame2.setEnabled(true);
        }

        m_endGameRadioGroupStage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {

                //Changes m_matchData's climb variable according to which radio button is selected
                m_matchData.setEndgameStage(getCurrentEndgameStageLevel());
                if (getCurrentEndgameStageLevel() == 0) //none
                {
                    radio_endGame0.setChecked(true);
                    radio_endGame0.setEnabled(false);
                    m_radio_endGame1.setEnabled(false);
                    m_radio_endGame2.setEnabled(false);
                }
                else if (getCurrentEndgameStageLevel() == 1) //parked
                {
                    radio_endGame0.setChecked(true);
                    radio_endGame0.setEnabled(false);
                    m_radio_endGame1.setEnabled(false);
                    m_radio_endGame2.setEnabled(false);
                }
                else if (getCurrentEndgameStageLevel() == 2) //onstage
                {
                    radio_endGame0.setEnabled(true);
                    m_radio_endGame1.setEnabled(true);
                    m_radio_endGame2.setEnabled(true);
                }
            }
        });

        m_endGameRadioGroupHarmony.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {

                //Changes m_matchData's climb variable according to which radio button is selected
                m_matchData.setEndgameHarmony(getCurrentEndgameHarmony());
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
                if (fActivity != null)
                {
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
                if (!MatchHistory.get(getActivity()).saveScouterData())
                {
                    Log.d(TAG, "ERROR - unable to save Scouter Data!");
                    // TODO - issue a toast msg here??
                }
                if (!MatchHistory.get(getActivity()).saveMatchData(m_matchData))
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

    public int getCurrentEndgameStageLevel()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_endGameRadioGroupStage.getCheckedRadioButtonId() == m_radio_endGameParked.getId())
        {
            rtn = 1;
        }
        else if (m_endGameRadioGroupStage.getCheckedRadioButtonId() == m_radio_endGameOnstage.getId())
        {
            rtn = 2;
        }
        return rtn;
    }

    public int getCurrentEndgameHarmony()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_endGameRadioGroupHarmony.getCheckedRadioButtonId() == m_radio_endGame1.getId())
        {
            rtn = 1;
        }
        else if (m_endGameRadioGroupHarmony.getCheckedRadioButtonId() == m_radio_endGame2.getId())
        {
            rtn = 2;
        }
        return rtn;
    }

    public void updateEndgameData()
    {
        m_matchData.setEndgameSpotLit(m_spotlitCheckbox.isChecked());
        m_matchData.setEndgameTrap(m_trapCheckbox.isChecked());
        m_matchData.setEndgameHarmony(getCurrentEndgameHarmony());
        m_matchData.setEndgameStage(getCurrentEndgameStageLevel());
        m_matchData.setDied(m_diedCheckbox.isChecked());
        m_matchData.setComment(m_commentText.getText().toString());
    }
}
