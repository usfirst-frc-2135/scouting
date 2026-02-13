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
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/** @noinspection ALL*/
public class EndgameFragment extends Fragment
{

    private RadioGroup m_diedGroup;
    private RadioButton m_diedNone;
    private RadioButton m_diedMost;
    private RadioButton m_diedMin;
    private RadioButton m_diedThirty;
    private RadioButton m_diedTt;
    private RadioButton m_noShow;

    private RadioGroup m_startGroup;
    private RadioButton m_startNone;
    private RadioButton m_startBefore;
    private RadioButton m_startBell;
    private RadioButton m_startTen;
    private RadioButton m_startLess;

    private RadioGroup m_endgameClimbButtonGroup;
    private RadioButton m_endgameLeft;
    private RadioButton m_endgameFront;
    private RadioButton m_endgameRight;
    private RadioButton m_endgameBack;
    private RadioButton m_endgameNA;

    private RadioGroup m_endgameClimbLevelGroup;
    private RadioButton m_levelNa;
    private RadioButton m_levelThree;
    private RadioButton m_levelTwo;
    private RadioButton m_levelOne;



    private EditText m_commentText;
    private MatchData m_matchData;

    public static final String QRTAG = "qr";
    private static final String TAG = "EndgameFragment";

    private void setupWarnings(View view)
    {
        // Determine if the warning msgs should be shown or hidden, and thus if the QR and Done 
        // buttons should be disabled or not.
        String warnmsg = "";
        String warnmsg2 = "";
        boolean bWarnCoral = false;
        boolean bWarnAlgae = false;
        boolean bDisableButtons = false;
        TextView tmsg1 = view.findViewById(R.id.warn_msg1);
        TextView tmsg2 = view.findViewById(R.id.warn_msg2);

        // Check if the acquired number is less than the scored number for coral and algae.
       //FIX int numCoralAcqd = m_matchData.getCoralAcquired();
      //FIX  int numAlgaeAcqd = m_matchData.getAlgaeAcquired();
       /* int numCoralScored = m_matchData.getTeleopCoralL1() + m_matchData.getTeleopCoralL2() + m_matchData.getTeleopCoralL3() + m_matchData.getTeleopCoralL4();
        int numAlgaeScored = m_matchData.getTeleopAlgaeNet() + m_matchData.getTeleopAlgaeProcessor();
        if(numCoralAcqd < numCoralScored) {
            bWarnCoral = true;
            Log.d(TAG, "-->  Number of coral acquired ("+numCoralAcqd+") is less than numCoralScored("+numCoralScored+")!");
        }
        if(numAlgaeAcqd < numAlgaeScored) {
            bWarnAlgae = true;
            Log.d(TAG, "-->  Number of algae acquired ("+numAlgaeAcqd+") is less than numAlgaeScored("+numAlgaeScored+")!");
        }*/
        if(bWarnAlgae && bWarnCoral) 
            warnmsg = "In Teleop, adjust the acquired coral and algae numbers (they are less than the numbers scored).";
        else if(bWarnAlgae)
            warnmsg = "In Teleop, adjust the acquired algae number (it is less than the number scored).";
        else if(bWarnCoral)
            warnmsg = "In Teleop, adjust the acquired coral number (it is less than the number scored).";

        // Check if auton coral was scored but no reefzone was checked.
        // Make the appropriate warnings visible and red. 
        if(warnmsg != "") 
        {
            tmsg1.setText(warnmsg);
            tmsg1.setVisibility(View.VISIBLE);
            tmsg1.setTextColor(Color.RED);
            bDisableButtons = true;
        }
        else  tmsg1.setVisibility(View.INVISIBLE);
        if(warnmsg2 != "")
        {
            tmsg2.setText(warnmsg2);
            tmsg2.setVisibility(View.VISIBLE);
            tmsg2.setTextColor(Color.RED);
            bDisableButtons = true;
        }
        else  tmsg2.setVisibility(View.INVISIBLE);

        // Enable or disable the QR and DONE buttons.
        ImageButton qrButton = view.findViewById(R.id.gen_QR);
        ImageButton qrButtonDisabled = view.findViewById(R.id.gen_QR_disabled);
        qrButtonDisabled.setVisibility(view.INVISIBLE);
        Button doneButton = view.findViewById(R.id.nav_to_menu_button);
        Button doneButtonDisabled = view.findViewById(R.id.nav_to_menu_button_disabled);
        doneButtonDisabled.setVisibility(view.INVISIBLE);
        if(bDisableButtons)
        {
            Log.d(TAG, "--> ! Disabling DONE and QR buttons");
            qrButton.setEnabled(false);
            doneButton.setEnabled(false);
            qrButtonDisabled.setVisibility(view.VISIBLE);
            doneButtonDisabled.setVisibility(view.VISIBLE);

        } else {
            Log.d(TAG, "--> ! Enabling DONE and QR buttons");
            qrButton.setEnabled(true);
            doneButton.setEnabled(true);
            qrButtonDisabled.setVisibility(view.INVISIBLE);
            doneButtonDisabled.setVisibility(view.INVISIBLE);
        }
    }

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
                String teamNumber = m_matchData.getTeamNumber();
                String teamAlias = m_matchData.getTeamAlias();
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if (actionBar != null)
                {
                    if(!teamAlias.equals(""))
                        actionBar.setTitle("Endgame               Scouting Team " + teamAlias + "          Match " + m_matchData.getMatchNumber());
                    else actionBar.setTitle("Endgame               Scouting Team " + teamNumber + "          Match " + m_matchData.getMatchNumber());
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        //Creates a view using the specific fragment layout.
        View v = inflater.inflate(R.layout.endgame_fragment, parent, false);



        /*
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
        */

        m_diedGroup = v.findViewById(R.id.died_group);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        m_diedNone = v.findViewById(R.id.died_none);//Sets up radio button that corresponds to 1
        m_diedMost = v.findViewById(R.id.died_most);//Sets up radio button that corresponds to 2
        m_diedMin = v.findViewById(R.id.died_min);//Sets up radio button that corresponds to 3
        m_diedThirty = v.findViewById(R.id.died_thirty);//Sets up radio button that corresponds to 4
        m_diedTt = v.findViewById(R.id.died_tt);//Sets up radio button that corresponds to 4
        m_noShow = v.findViewById(R.id.no_show);//Sets up radio button that corresponds to 4
        m_diedNone.setChecked(false);
        m_diedMost.setChecked(false);
        m_diedMin.setChecked(false);
        m_diedThirty.setChecked(false);
        m_diedTt.setChecked(false);
        m_noShow.setChecked(false);

        int defValueClimb = m_matchData.getTimeDied();
        if (defValueClimb == 0)
            m_diedNone.setChecked(true);
        else if (defValueClimb == 1)
            m_diedMost.setChecked(true);
        else if(defValueClimb == 2)
            m_diedMin.setChecked(true);
        else if(defValueClimb == 3)
            m_diedThirty.setChecked(true);
        else if(defValueClimb == 4)
            m_diedTt.setChecked(true);
        else if(defValueClimb == 5)
            m_noShow.setChecked(true);

        m_startGroup = v.findViewById(R.id.start_text);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        m_startNone = v.findViewById(R.id.start_none);//Sets up radio button that corresponds to 0
        m_startBefore = v.findViewById(R.id.start_before);//Sets up radio button that corresponds to 1
        m_startBell = v.findViewById(R.id.start_bell);//Sets up radio button that corresponds to 2
        m_startTen = v.findViewById(R.id.start_ten);//Sets up radio button that corresponds to 3
        m_startLess = v.findViewById(R.id.start_less);//Sets up radio button that corresponds to 4
        m_startNone.setChecked(false);
        m_startBefore.setChecked(false);
        m_startBell.setChecked(false);
        m_startTen.setChecked(false);
        m_startLess.setChecked(false);


        int defValueStartClimb = m_matchData.getStartClimb();
        if (defValueStartClimb == 0)
            m_startNone.setChecked(true);
        else if(defValueStartClimb == 1)
            m_startBefore.setChecked(true);
        else if(defValueStartClimb == 2)
            m_startBell.setChecked(true);
        else if(defValueStartClimb == 3)
            m_startTen.setChecked(true);
        else if(defValueStartClimb == 4)
            m_startLess.setChecked(true);

        m_endgameClimbButtonGroup = v.findViewById(R.id.endgame_climb_buttons);
        m_endgameLeft = v.findViewById(R.id.endgame_climb_left);
        m_endgameFront = v.findViewById(R.id.endgame_climb_front);
        m_endgameRight = v.findViewById(R.id.endgame_climb_right);
        m_endgameBack = v.findViewById(R.id.endgame_climb_back);
        m_endgameNA = v.findViewById(R.id.endgame_climb_na);
        m_endgameLeft.setChecked(false);
        m_endgameFront.setChecked(false);
        m_endgameRight.setChecked(false);
        m_endgameBack.setChecked(false);
        m_endgameNA.setChecked(false);


        int CaccValue = m_matchData.getEndgameClimb();
        if (CaccValue == 0)
            m_endgameNA.setChecked(true);
        else if(CaccValue == 1)
            m_endgameBack.setChecked(true);
        else if(CaccValue == 2)
            m_endgameLeft.setChecked(true);
        else if(CaccValue == 3)
            m_endgameFront.setChecked(true);
        else if(CaccValue == 4)
            m_endgameRight.setChecked(true);


        m_endgameClimbLevelGroup = v.findViewById(R.id.endgame_level_climb_group);
        m_levelNa= v.findViewById(R.id.endgame_level_na);
        m_levelThree = v.findViewById(R.id.endgame_level_three);
        m_levelTwo = v.findViewById(R.id.endgame_level_two);
        m_levelOne = v.findViewById(R.id.endgame_level_one);
        m_levelThree.setChecked(false);
        m_levelTwo.setChecked(false);
        m_levelOne.setChecked(false);
        m_levelNa.setChecked(false);


        int BaccValue = m_matchData.getEndgameClimbLevel();
        if (BaccValue == 0)
            m_levelNa.setChecked(true);
        else if(BaccValue == 1)
            m_levelOne.setChecked(true);
        else if(BaccValue == 2)
            m_levelTwo.setChecked(true);
        else if(BaccValue == 3)
            m_levelThree.setChecked(true);


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

        setupWarnings(v);
        return v;
    }

    public int getTimeDied()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_diedGroup.getCheckedRadioButtonId() == m_diedNone.getId())
        {
            rtn = 0;
        }
        else if (m_diedGroup.getCheckedRadioButtonId() == m_diedMost.getId())
        {
            rtn = 1;
        }
        else if (m_diedGroup.getCheckedRadioButtonId() == m_diedMin.getId())
        {
            rtn = 2;
        }
        else if (m_diedGroup.getCheckedRadioButtonId() == m_diedThirty.getId())
        {
            rtn = 3;
        }
        else if (m_diedGroup.getCheckedRadioButtonId() == m_diedTt.getId())
        {
            rtn = 4;
        }
        else if (m_diedGroup.getCheckedRadioButtonId() == m_noShow.getId())
        {
            rtn = 5;
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
        if (m_startGroup.getCheckedRadioButtonId() == m_startBefore.getId())
        {
            rtn = 1;
        }
        if (m_startGroup.getCheckedRadioButtonId() == m_startBell.getId())
        {
            rtn = 2;
        }
        if (m_startGroup.getCheckedRadioButtonId() == m_startTen.getId())
        {
            rtn = 3;
        }
        else if (m_startGroup.getCheckedRadioButtonId() == m_startLess.getId())
        {
            rtn = 4;
        }
        return rtn;
    }

    public int getEndgameClimb()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_endgameClimbButtonGroup.getCheckedRadioButtonId() == m_endgameNA.getId())
        {
            rtn = 0;
        }
        if (m_endgameClimbButtonGroup.getCheckedRadioButtonId() == m_endgameBack.getId())
        {
            rtn = 1;
        }
        if (m_endgameClimbButtonGroup.getCheckedRadioButtonId() == m_endgameLeft.getId())
        {
            rtn = 2;
        }
        if (m_endgameClimbButtonGroup.getCheckedRadioButtonId() == m_endgameFront.getId())
        {
            rtn = 3;
        }
        if (m_endgameClimbButtonGroup.getCheckedRadioButtonId() == m_endgameRight.getId())
        {
            rtn = 4;
        }
        return rtn;
    }

    public int getEndgameClimbLevel()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_endgameClimbLevelGroup.getCheckedRadioButtonId() == m_levelNa.getId())
        {
            rtn = 0;
        }
        if (m_endgameClimbLevelGroup.getCheckedRadioButtonId() == m_levelOne.getId())
        {
            rtn = 1;
        }
        if (m_endgameClimbLevelGroup.getCheckedRadioButtonId() == m_levelTwo.getId())
        {
            rtn = 2;
        }
        if (m_endgameClimbLevelGroup.getCheckedRadioButtonId() == m_levelThree.getId())
        {
            rtn = 3;
        }
        return rtn;
    }
    public void updateEndgameData()
    {
        m_matchData.setStartClimb(getCurrentStartClimbing());
        m_matchData.setTimeDied(getTimeDied());
        m_matchData.setComment(m_commentText.getText().toString());
        m_matchData.setEndgameClimb(getEndgameClimb());
        m_matchData.setEndgameClimbLevel(getEndgameClimbLevel());

    }
}
