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

    private RadioGroup m_climbGroup;
    private RadioButton m_climbParked;
    private RadioButton m_climbFell;
    private RadioButton m_climbShallow;
    private RadioButton m_climbDeep;

    private CheckBox m_diedCheckbox;
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
        int numCoralAcqd = m_matchData.getCoralAcquired();
        int numAlgaeAcqd = m_matchData.getAlgaeAcquired();
        int numCoralScored = m_matchData.getTeleopCoralL1() + m_matchData.getTeleopCoralL2() + m_matchData.getTeleopCoralL3() + m_matchData.getTeleopCoralL4();
        int numAlgaeScored = m_matchData.getTeleopAlgaeNet() + m_matchData.getTeleopAlgaeProcessor();
        if(numCoralAcqd < numCoralScored) {
            bWarnCoral = true;
            Log.d(TAG, "-->  Number of coral acquired ("+numCoralAcqd+") is less than numCoralScored("+numCoralScored+")!");
        }
        if(numAlgaeAcqd < numAlgaeScored) {
            bWarnAlgae = true;
            Log.d(TAG, "-->  Number of algae acquired ("+numAlgaeAcqd+") is less than numAlgaeScored("+numAlgaeScored+")!");
        }
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

        int defValueClimb = m_matchData.getCageClimb();
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

    public int getCurrentCageClimb()
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

    public void updateEndgameData()
    {
        m_matchData.setCageClimb(getCurrentCageClimb());
        m_matchData.setDied(m_diedCheckbox.isChecked());
        m_matchData.setComment(m_commentText.getText().toString());

    }
}
