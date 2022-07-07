package com.frc2135.android.frc_scout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class PreMatchActivity extends AppCompatActivity {
    private AutoCompleteTextView m_teamIndexField;
    private AutoCompleteTextView m_competitionField;
    private AutoCompleteTextView m_scoutNameField;
    private AutoCompleteTextView m_teamNumberField;
    private EditText             m_matchNumberField;
    private Button               m_startScoutingButton;
    private TextView             m_missingFieldErrMsg;
    private TextView             m_teamIndexErrMsg;
    private Button               m_prematchCancelButton;
    private MatchData            m_matchData;
    private ActionBar            m_actionBar;
    private CompetitionInfo      m_compInfo;;

    public static final String TAG = "PreMatchActivity";
    public static final String EXTRA_DATE = "com.frc2135.android.frc_scout.date";

    private static Scouter m_Scouter;


    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        m_Scouter = Scouter.get(getApplicationContext());

        String matchId = getIntent().getStringExtra("match_ID");
        m_matchData = MatchHistory.get(getApplicationContext()).getMatch(matchId);
        m_compInfo = CompetitionInfo.get(getApplicationContext(),m_matchData.getCompetition().trim());

        m_actionBar = getSupportActionBar();
        m_actionBar.setTitle("Pre-Match");

        setContentView(R.layout.prematch_activity);

        m_teamIndexErrMsg = findViewById(R.id.team_index_err);
        m_teamIndexErrMsg.setVisibility(View.INVISIBLE);
        m_teamIndexErrMsg.setTextColor(Color.RED);

        m_missingFieldErrMsg = findViewById(R.id.error_message_pm);
        m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
        m_missingFieldErrMsg.setTextColor(Color.RED);

        m_teamIndexField = findViewById(R.id.team_index_field);
        m_teamIndexField.setHint("Team Number index to use");
        if(m_Scouter != null) {
            String indexStr = m_Scouter.getTeamIndexStr();
            Log.d(TAG,"From Scouter: teamFieldIndex = "+indexStr);
            if(m_Scouter.isValidTeamIndexStr(indexStr)) 
                m_teamIndexField.setText(m_Scouter.getTeamIndexStr());
            else m_teamIndexField.setText("None");
        }
        else m_teamIndexField.setText("None");
        m_teamIndexField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){
 //HOLD           String indexStr = m_teamIndexField.getText().toString().trim();
 //HOLD           if(!m_Scouter.isValidTeamIndexStr(indexStr))  {
 //HOLD               m_teamIndexField.setText("None");
 //HOLD               Log.d(TAG,"teamFieldIndex value "+indexStr+" is not valid, so set to None!");
 //HOLD           }
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                 // Validate the teamIndex entry.
                 m_teamIndexErrMsg.setVisibility(View.INVISIBLE); // reset invalid teamIndex msg
                 if(!m_Scouter.isValidTeamIndexStr(m_teamIndexField.getText().toString().trim())) {
                     m_teamIndexErrMsg.setVisibility(View.VISIBLE); // show invalid teamIndex msg
                     m_teamIndexField.setTextColor(Color.RED);
                 }
                 else m_teamIndexField.setTextColor(getResources().getColor(R.color.textPrimary));
                 m_missingFieldErrMsg.setVisibility(View.INVISIBLE);  // reset err msg to be invisible
            }

        });

        // Set up the dropdown list for the TeamIndex field.
        String[] teamIndexList = new String[7];
        teamIndexList[0]="None";
        teamIndexList[1]="1";
        teamIndexList[2]="2";
        teamIndexList[3]="3";
        teamIndexList[4]="4";
        teamIndexList[5]="5";
        teamIndexList[6]="6";
        ArrayAdapter<String> teamIndexAdapter = new ArrayAdapter<String> (PreMatchActivity.this, android.R.layout.select_dialog_item,teamIndexList );
        m_teamIndexField.setAdapter(teamIndexAdapter);
        m_teamIndexField.setThreshold(0);
        m_teamIndexField.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange (View v, boolean hasFocus){
                if(hasFocus){
                    m_teamIndexField.showDropDown();
                }
            }
        });

        m_competitionField = findViewById(R.id.comp_name);
        m_competitionField.setHint("Competition");
        m_competitionField.setText(m_matchData.getCompetition());
        m_competitionField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){

            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
            }

        });

        m_scoutNameField = findViewById(R.id.scouter_name);
        m_scoutNameField.setHint("Scout Name");
        if(m_matchData != null && !m_matchData.getName().isEmpty())
            m_scoutNameField.setText(m_matchData.getName());
        else if(m_Scouter != null) {
            String mostRecentScoutName = m_Scouter.getMostRecentScoutName();
            if(!mostRecentScoutName.isEmpty()){
              m_scoutNameField.setText(mostRecentScoutName);
            }
        }
        m_scoutNameField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
            }

        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String> (PreMatchActivity.this, android.R.layout.select_dialog_item, Scouter.get(getApplicationContext()).getPastScouts());
        m_scoutNameField.setAdapter(adapter2);
        m_scoutNameField.setThreshold(0);
        m_scoutNameField.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange (View v, boolean hasFocus){
                if(hasFocus){
                    m_scoutNameField.showDropDown();
                }
            }
        });

        m_matchNumberField = (EditText)findViewById(R.id.match_number_field);
        if(!m_matchData.getMatchNumber().isEmpty())
            m_matchNumberField.setText(m_matchData.getMatchNumber());
        else if(m_Scouter != null && !m_Scouter.getMostRecentMatchNumber().isEmpty())
            m_matchNumberField.setText(m_Scouter.getNextExpectedMatchNumber());
        else m_matchNumberField.setText("qm1");

        m_teamNumberField = findViewById(R.id.team_number_field);
        m_teamNumberField.setText(m_matchData.getTeamNumber());

        // If there is a match number and a team index, load that team number from event teams list.
        String matchNumStr = m_matchNumberField.getText().toString().trim();
        String teamIndexStr = m_teamIndexField.getText().toString().trim();
      
        if(!matchNumStr.isEmpty() && !teamIndexStr.isEmpty() && m_Scouter.isValidTeamIndexNum(teamIndexStr)  && m_compInfo != null && m_compInfo.isEventDataLoaded()) {
            try {
                String[] teams = m_compInfo.getTeams(m_matchNumberField.getText().toString().trim());
                int teamIndx = Integer.parseInt(teamIndexStr);
                String teamNumStr = teams[teamIndx];
                Log.d(TAG,"Preloading team number using index "+teamIndexStr+": "+teamNumStr);
                m_teamNumberField.setText(teamNumStr);
            } catch (JSONException jsonException) {
                Log.e(TAG, "For preload: couldn't get teams from m_compInfo");
                jsonException.printStackTrace();
            }
        }

        m_teamNumberField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
                if (parent != null && parent.getItemAtPosition(position) != null) {
                    m_matchData.setTeamNumber(parent.getItemAtPosition(position).toString());
                    // TODO - can we remove keyboard here???
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
                m_teamNumberField.setSelection(0);
            }
        });

        m_teamNumberField.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange (View v, boolean hasFocus){
                if(hasFocus){
                    Log.d(TAG, "m_teamNumberField clicked");
                    String matchNumStr = m_matchNumberField.getText().toString().trim();
                    if(!matchNumStr.isEmpty() && m_compInfo != null && m_compInfo.isEventDataLoaded()) {
                        boolean bTeamsLoadedSuccessfully = false;
                        try {
                            String[] teams = m_compInfo.getTeams(m_matchNumberField.getText().toString().trim());
                            if(teams[0] != "")
                                bTeamsLoadedSuccessfully = true;
                            ArrayAdapter<String> adapter3 = new ArrayAdapter<String> (PreMatchActivity.this, android.R.layout.select_dialog_item, teams);
                            m_teamNumberField.setAdapter(adapter3);
                        } catch (JSONException jsonException) {
                            Log.e(TAG, "finding teams failed");
                            jsonException.printStackTrace();
                        }catch(NullPointerException nullPointerException){
                            nullPointerException.printStackTrace();
                        }
                        if(bTeamsLoadedSuccessfully)
                        {
                            m_teamNumberField.setDropDownHeight(620);
                            m_teamNumberField.showDropDown();
                        }
                    }
                }
            }
        });

        m_startScoutingButton = findViewById(R.id.start_scouting_button);
        m_startScoutingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkValidData()){
                    updatePreMatchData();
                    Intent intent1 = new Intent(PreMatchActivity.this, ScoutingActivity.class);
                    intent1.putExtra("match_ID", m_matchData.getMatchID());
                    startActivityForResult(intent1, 0);
                }
            }
        });

        m_prematchCancelButton = findViewById(R.id.prematch_cancel_button);
        m_prematchCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MatchHistory.get(getApplicationContext()).deleteMatch(m_matchData);
                Intent intent2 = new Intent(PreMatchActivity.this, MatchListActivity.class);
                startActivityForResult(intent2, 0);
            }
        });
    }

    public void updatePreMatchData(){
        m_Scouter = Scouter.get(getApplicationContext());
        m_Scouter.addPastScouter(m_scoutNameField.getText().toString());
        m_Scouter.setMostRecentScoutName(m_scoutNameField.getText().toString());
        m_Scouter.setMostRecentMatchNumber(m_matchNumberField.getText().toString());
        m_matchData.setName(m_scoutNameField.getText().toString());
        m_matchData.setCompetition(m_competitionField.getText().toString());
        m_matchData.setMatchNumber(m_matchNumberField.getText().toString().trim());
        m_matchData.setTeamNumber(m_teamNumberField.getText().toString());
        m_Scouter.setTeamIndexStr(m_teamIndexField.getText().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        //if(requestCode == 0){
            //finish();
        //}
    }

    private boolean checkValidData(){
        m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
        m_teamIndexErrMsg.setVisibility(View.INVISIBLE); 

        // Validate team index 
        if(!m_Scouter.isValidTeamIndexStr(m_teamIndexField.getText().toString().trim())) {
            m_teamIndexErrMsg.setVisibility(View.VISIBLE); 
            Log.d(TAG,"+++>> checkValidData(): ERROR: teamIndex is not valid: "+m_teamIndexField.getText().toString().trim());
            return false;
        }

        // Make sure there are entries for the various fields on this page.
        if(m_competitionField.getText().toString().trim().equals("")||m_scoutNameField.getText().toString().trim().equals("")|| m_teamNumberField.getText().toString().trim().equals("")||m_matchNumberField.getText().equals("")){
            m_missingFieldErrMsg.setVisibility(View.VISIBLE);
            return false;
        }
        m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}



