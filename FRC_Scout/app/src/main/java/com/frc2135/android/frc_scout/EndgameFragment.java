package com.frc2135.android.frc_scout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

public class EndgameFragment extends Fragment {

    private RadioGroup m_radioGroup;
    private RadioButton m_radioButton0;
    private RadioButton m_radioButton1;
    private RadioButton m_radioButton2;
    private RadioButton m_radioButton3;
    private RadioButton m_radioButton4;
    private CheckBox m_diedCheckbox;
    private EditText m_commentText;
    private MatchData m_matchData;
    private ActionBar m_actionBar;

    private static final int REQUEST_QR = 2;
    public static final String QRTAG = "qr";
    private static final String TAG = "EndgameFragment";


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        m_matchData = ((ScoutingActivity)getActivity()).getCurrentMatch();

        m_actionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        String teamNumber = m_matchData.stripTeamNamePrefix(m_matchData.getTeamNumber());
        m_actionBar.setTitle("Endgame                           - scouting Team "+teamNumber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.endgame_fragment, parent, false);


        //Connects the checkbox for if the robot dies and sets up a listener to detect when the checked status is changed
        m_diedCheckbox = (CheckBox)v.findViewById(R.id.died_checkbox_true);
        m_diedCheckbox.setChecked(m_matchData.getDied());// Default is unchecked
        m_diedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateEndgameData();
            }
        });

        m_radioGroup = (RadioGroup)v.findViewById(R.id.climb_level);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        m_radioButton0 = (RadioButton)v.findViewById(R.id.level_zero);//Sets up radio button that corresponds to 0
        m_radioButton0.setChecked(true);
        m_radioButton1 = (RadioButton)v.findViewById(R.id.level_one);//Sets up radio button that corresponds to 1
        m_radioButton2 = (RadioButton)v.findViewById(R.id.level_two);//Sets up radio button that corresponds to 2
        m_radioButton3 = (RadioButton)v.findViewById(R.id.level_three);//Sets up radio button that corresponds to 3
        m_radioButton4 = (RadioButton)v.findViewById(R.id.level_four);//Sets up radio button that corresponds to 4

        int x = m_matchData.getClimb();
        if(x==0) 
            m_radioButton0.setChecked(true);
        else if(x==1) 
            m_radioButton1.setChecked(true);
        else if(x==2) 
            m_radioButton2.setChecked(true);
        else if(x==3) 
            m_radioButton3.setChecked(true);
        else if(x==4) 
            m_radioButton4.setChecked(true);

        m_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //Changes m_matchData's climb variable according to which radio button is selected
                m_matchData.setClimb(getCurrentClimbLevel());
            }
        });

        //Sets up an EditText that allows users to input any additional comments
        m_commentText = (EditText)v.findViewById(R.id.comments);
        m_commentText.setHint("Enter comments here");
        m_commentText.setText(m_matchData.getComment());
//REMOVE->
//        m_commentText.addTextChangedListener(new TextWatcher(){
//            public void onTextChanged(CharSequence c, int start, int before, int count){
//            }
//            public void beforeTextChanged(CharSequence c, int start, int count, int after){
//            }
//            public void afterTextChanged(Editable c){
//                m_matchData.setComment(m_commentText.getText().toString());
//            }
//        });
//<-REMOVE

        ImageButton mQRButton = (ImageButton)v.findViewById(R.id.gen_QR);
        mQRButton.setOnClickListener(new View.OnClickListener() {
            //Setting an onClickListener makes it so that our button actually senses for when it is clicked, and when it is clicked, it will proceed with onClick()

            @Override
            public void onClick(View view) {
                //Uses intents to start the QR code dialog
                updateEndgameData();
                Snackbar.make(view, "Generating QR code", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Log.d(TAG, "Clicked on QR Code");
                FragmentManager fm = getActivity().getSupportFragmentManager();
                QRFragment dialog = QRFragment.newInstance(m_matchData);
                dialog.setTargetFragment(EndgameFragment.this, REQUEST_QR);
                dialog.show(fm, QRTAG);
            }
        });

        Button mNextButton  = (Button)v.findViewById(R.id.nav_to_menu_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Save the latest Scouter and MatchData JSON files.
                Log.d(TAG,"EndgameFragment DONE onClick() saving latest match and Scouter files");
                MatchHistory.get(getActivity()).saveScouterData();
                MatchHistory.get(getActivity()).saveMatchData(m_matchData);

                // Go back to MatchListActivity page
                Intent i = new Intent(getActivity(), MatchListActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(i, 0);
                getActivity().finish();
                Log.d(TAG, "Sent intent");
            }
        });

        return v;
    }
    public int getCurrentClimbLevel() {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_radioGroup.getCheckedRadioButtonId() == m_radioButton1.getId()) {
            rtn = 1;
        }
        else if (m_radioGroup.getCheckedRadioButtonId() == m_radioButton2.getId()) {
            rtn = 2;
        }
        else if (m_radioGroup.getCheckedRadioButtonId() == m_radioButton3.getId()) {
            rtn = 3;
        }
        else if (m_radioGroup.getCheckedRadioButtonId() == m_radioButton4.getId()) {
            rtn = 4;
        }
        return rtn;
    }
    public void updateEndgameData(){
        m_matchData.setClimb(getCurrentClimbLevel());
        m_matchData.setDied(m_diedCheckbox.isChecked());
        m_matchData.setComment(m_commentText.getText().toString());
    }
}
