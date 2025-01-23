package com.frc2135.android.frc_scout;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

/** @noinspection ALL*/
public class EndgameFragment extends Fragment
{

    private RadioGroup m_climbGroup;
    private RadioButton m_climbParked;
    private RadioButton m_climbFell;
    private RadioButton m_climbShallow;
    private RadioButton m_climbDeep;

    private RadioGroup m_startGroup;
    private RadioButton m_startNone;
    private RadioButton m_startBell;
    private RadioButton m_startTen;
    private RadioButton m_startFive;

    private RadioGroup m_foulRobot;
    private RadioButton m_zeroContact;
    private RadioButton m_oneContact;
    private RadioButton m_twoContact;
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

        m_climbGroup = v.findViewById(R.id.climb_group);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        RadioButton radio_climbNone = v.findViewById(R.id.climb_none);//Sets up radio button that corresponds to 0
        m_climbParked = v.findViewById(R.id.climb_parked);//Sets up radio button that corresponds to 1
        m_climbFell = v.findViewById(R.id.climb_fell);//Sets up radio button that corresponds to 2
        m_climbShallow = v.findViewById(R.id.climb_shallow);//Sets up radio button that corresponds to 3
        m_climbDeep = v.findViewById(R.id.climb_deep);//Sets up radio button that corresponds to 4
        radio_climbNone.setChecked(false);
        m_climbParked.setChecked(false);
        m_climbFell.setChecked(false);
        m_climbShallow.setChecked(false);
        m_climbDeep.setChecked(false);

        int defValueClimb = m_matchData.getEndgameBarge();
        if (defValueClimb == 0)
            radio_climbNone.setChecked(true);
        else if (defValueClimb == 1)
            m_climbParked.setChecked(true);
        else if(defValueClimb == 2)
            m_climbFell.setChecked(true);
        else if(defValueClimb == 3)
            m_climbShallow.setChecked(true);
        else if(defValueClimb == 4)
        m_climbDeep.setChecked(true);

        m_startGroup = v.findViewById(R.id.start_text);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        m_startNone = v.findViewById(R.id.start_none);//Sets up radio button that corresponds to 0
        m_startBell = v.findViewById(R.id.start_bell);//Sets up radio button that corresponds to 1
        m_startTen = v.findViewById(R.id.start_ten);//Sets up radio button that corresponds to 2
        m_startFive = v.findViewById(R.id.start_five);//Sets up radio button that corresponds to 3
        m_startTen.setChecked(false);
        m_startBell.setChecked(false);
        m_startNone.setChecked(false);
        m_startFive.setChecked(false);


        int defValueStartClimb = m_matchData.getEndgameStartClimbing();
        if (defValueStartClimb == 0)
            m_startNone.setChecked(true);
        else if(defValueStartClimb == 1)
            m_startBell.setChecked(true);
        else if(defValueStartClimb == 2)
            m_startTen.setChecked(true);
        else if(defValueStartClimb == 3)
            m_startFive.setChecked(true);


        m_foulRobot = v.findViewById(R.id.foul_robot);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        m_zeroContact = v.findViewById(R.id.zero_contact);//Sets up radio button that corresponds to 0
        m_oneContact = v.findViewById(R.id.one_contact);//Sets up radio button that corresponds to 1
        m_twoContact = v.findViewById(R.id.two_contact);//Sets up radio button that corresponds to 2
        m_zeroContact.setChecked(false);
        m_oneContact.setChecked(false);
        m_twoContact.setChecked(false);

        int defValueFoul = m_matchData.getCurrentFoulNumber();
        if (defValueFoul == 0)
            m_zeroContact.setChecked(true);
        else if(defValueFoul == 1)
            m_oneContact.setChecked(true);
        else if(defValueFoul == 2)
            m_twoContact.setChecked(true);



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
                updateEndgameData();  // save current endgame values in matchdata

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

    public int getCurrentEndgameBargeLevel()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_climbGroup.getCheckedRadioButtonId() == m_climbParked.getId())
        {
            rtn = 1;
        }
        else if (m_climbGroup.getCheckedRadioButtonId() == m_climbFell.getId())
        {
            rtn = 2;
        }
        else if (m_climbGroup.getCheckedRadioButtonId() == m_climbShallow.getId())
        {
            rtn = 3;
        }
        else if (m_climbGroup.getCheckedRadioButtonId() == m_climbDeep.getId())
        {
            rtn = 4;
        }
        return rtn;
    }

    public int getCurrentStartClimbing()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_startGroup.getCheckedRadioButtonId() == m_startNone.getId())
        {
            rtn = 0;
        }
        if (m_startGroup.getCheckedRadioButtonId() == m_startBell.getId())
        {
            rtn = 1;
        }
        else if (m_startGroup.getCheckedRadioButtonId() == m_startTen.getId())
        {
            rtn = 2;
        }
        else if (m_startGroup.getCheckedRadioButtonId() == m_startFive.getId())
        {
            rtn = 3;
        }
        return rtn;
    }


    public int getCurrentFoulNumber()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_foulRobot.getCheckedRadioButtonId() == m_zeroContact.getId())
        {
            rtn = 0;
        }
        if (m_foulRobot.getCheckedRadioButtonId() == m_oneContact.getId())
        {
            rtn = 1;
        }
        else if (m_foulRobot.getCheckedRadioButtonId() == m_twoContact.getId())
        {
            rtn = 2;
        }
        return rtn;
    }



    public void updateEndgameData()
    {
        m_matchData.setEndgameStartClimbing(getCurrentStartClimbing());
        m_matchData.setEndgameBarge(getCurrentEndgameBargeLevel());
        m_matchData.setFoulNumber(getCurrentFoulNumber());
        m_matchData.setDied(m_diedCheckbox.isChecked());
        m_matchData.setComment(m_commentText.getText().toString());

    }
}