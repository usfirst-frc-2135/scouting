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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class PreMatchActivity extends AppCompatActivity {
    private AutoCompleteTextView mCompetitionField;
    private AutoCompleteTextView mScouterNameField;
    private AutoCompleteTextView mTeamNumberField;
    private AutoCompleteTextView mMatchNumberField;
    private Button mStartScoutingButton;
    private TextView mErrorMessagepm;

    private MatchData mMatchData;
    private ActionBar t;

    public static final String TAG = "PreMatch Fragment";
    public static final String EXTRA_DATE = "com.frc2135.android.frc_scout.date";

    private static Scouter mScout;


    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        mScout = Scouter.get(getApplicationContext());

        String matchId = getIntent().getStringExtra("match_ID");
        mMatchData = MatchHistory.get(getApplicationContext()).getMatch(matchId);
        final Event finalEvent = new Event(this, mMatchData.getCompetition().trim());

        t =  getSupportActionBar();
        t.setTitle("Pre-Match: ");

        setContentView(R.layout.prematch_activity);

        mCompetitionField = findViewById(R.id.comp_name);
        mCompetitionField.setHint("Competition");
        mCompetitionField.setText(mMatchData.getCompetition());
        mCompetitionField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){

            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                mErrorMessagepm.setVisibility(View.INVISIBLE);
            }

        });

        mScouterNameField = findViewById(R.id.scouter_name);
        mScouterNameField.setHint("Scout Name");
        if(mMatchData != null && !mMatchData.getName().isEmpty())
            mScouterNameField.setText(mMatchData.getName());
        else if(mScout != null) {
            String mostRecentScoutName = mScout.getMostRecentScoutName();
            if(!mostRecentScoutName.isEmpty()){
              mScouterNameField.setText(mostRecentScoutName);
            }
        }
        mScouterNameField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                mErrorMessagepm.setVisibility(View.INVISIBLE);
            }

        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>
                (PreMatchActivity.this, android.R.layout.select_dialog_item, Scouter.get(getApplicationContext()).getPastScouts());
        mScouterNameField.setAdapter(adapter2);
        mScouterNameField.setThreshold(0);
        mScouterNameField.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange (View v, boolean hasFocus){
                if(hasFocus){
                    mScouterNameField.showDropDown();
                }
            }
        });

        mMatchNumberField = (AutoCompleteTextView)findViewById(R.id.match_number_field);
        if(!mMatchData.getMatchNumber().isEmpty())
            mMatchNumberField.setText(mMatchData.getMatchNumber());
        else if(mScout != null && !mScout.getMostRecentMatchNumber().isEmpty())
            mMatchNumberField.setText(mScout.getNextExpectedMatchNumber());

        mMatchNumberField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMatchData.setMatchNumber(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMatchNumberField.setSelection(0);
            }
        });

        ArrayAdapter<String> adapter4 = null;
        if(finalEvent != null && mMatchData != null && mMatchData.getCompetition() != "compX" && !mMatchData.getCompetition().isEmpty() ){
                try {
                    String[] matchArray = finalEvent.getEventMatches(mMatchData.getCompetition());
                    if(matchArray != null && matchArray.length > 0)
                        adapter4 = new ArrayAdapter<String>(PreMatchActivity.this, android.R.layout.select_dialog_item, matchArray);
                } catch (JSONException | IOException jsonException) {
                    jsonException.printStackTrace();
                }

        } 
        mMatchNumberField.setAdapter(adapter4);
        mMatchNumberField.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange (View v, boolean hasFocus){
                if(hasFocus){
                    mMatchNumberField.showDropDown();
                }
            }
        });

        mTeamNumberField = findViewById(R.id.team_number_field);
        mTeamNumberField.setText(mMatchData.getTeamNumber()+"");
        mTeamNumberField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mErrorMessagepm.setVisibility(View.INVISIBLE);
                mMatchData.setTeamNumber(parent.getItemAtPosition(position).toString());
                // TODO - can we remove keyboard here???
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mErrorMessagepm.setVisibility(View.INVISIBLE);
                mTeamNumberField.setSelection(0);
            }
        });


        mTeamNumberField.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange (View v, boolean hasFocus){
                if(hasFocus){
                    Log.d(TAG, "mTeamNumberField clicked");
                    try {
                        ArrayAdapter<String> adapter3 = null;
                        String[] teams = finalEvent.getTeams(mMatchNumberField.getText().toString().trim());
                        if(teams == null){
                            teams = new String[1];
                            teams[0] = "Error loading teams";
                        }
                        adapter3 = new ArrayAdapter<String>
                                (PreMatchActivity.this, android.R.layout.select_dialog_item, teams);
                        mTeamNumberField.setAdapter(adapter3);
                    } catch (JSONException jsonException) {
                        Log.e("Event", "finding teams failed");
                        jsonException.printStackTrace();
                    }catch(NullPointerException nullPointerException){
                        nullPointerException.printStackTrace();
                    }
                    mTeamNumberField.setDropDownHeight(620);
                    mTeamNumberField.showDropDown();

                }
            }
        });




        mStartScoutingButton = findViewById(R.id.start_scouting_button);
        mStartScoutingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkValidData()){
                    updatePreMatchData();
                    Intent i = new Intent(PreMatchActivity.this, ScoutingActivity.class);
                    i.putExtra("match_ID", mMatchData.getMatchID());
                    startActivityForResult(i, 0);
                }


            }
        });

        mErrorMessagepm = findViewById(R.id.error_message_pm);
        mErrorMessagepm.setVisibility(View.INVISIBLE);
        mErrorMessagepm.setTextColor(Color.RED);




    }

    public void updatePreMatchData(){
        mScout = Scouter.get(getApplicationContext());
        mScout.addPastScouter(mScouterNameField.getText().toString());
        mScout.setMostRecentScoutName(mScouterNameField.getText().toString());
        mScout.setMostRecentMatchNumber(mMatchNumberField.getText().toString());
        mScout.saveData(getApplicationContext());
        mMatchData.setName(mScouterNameField.getText().toString());
        mMatchData.setCompetition(mCompetitionField.getText().toString());
        mMatchData.setMatchNumber(mMatchNumberField.getText().toString().trim());
        mMatchData.setTeamNumber(mTeamNumberField.getText().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        //if(requestCode == 0){
            //finish();
        //}
    }

    private boolean checkValidData(){
        mErrorMessagepm.setVisibility(View.INVISIBLE);
        if(mCompetitionField.getText().toString().trim().equals("")||mScouterNameField.getText().toString().trim().equals("")|| mTeamNumberField.getText().toString().trim().equals("")||mMatchNumberField.getText().equals("")){
            Log.d(TAG,"+++>> checkValidData(): ERROR");
            mErrorMessagepm.setVisibility(View.VISIBLE);
            return false;
        }
        mErrorMessagepm.setVisibility(View.INVISIBLE);
        return true;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}



