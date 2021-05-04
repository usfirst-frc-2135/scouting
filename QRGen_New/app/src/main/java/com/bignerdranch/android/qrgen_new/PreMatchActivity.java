package com.bignerdranch.android.qrgen_new;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.util.Date;

public class PreMatchActivity extends AppCompatActivity {

    private AutoCompleteTextView mCompetitionField;
    private AutoCompleteTextView mScouterNameField;
    private EditText mTeamNumberField;
    private EditText mMatchNumberField;
    private Button mStartScoutingButton;
    private TextView mErrorMessagepm;
    private Spinner mMatchTypesSpinner;

    private MatchData mMatchData;
    private ActionBar t;

    public static final String TAG = "PreMatch Fragment";
    public static final String EXTRA_DATE = "com.bignerranch.android.qrgen.date";

    private static Scouter mScout;


    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        String matchId = getIntent().getStringExtra("match_ID");
        mMatchData = MatchHistory.get(getApplicationContext()).getMatch(matchId);

        t =  getSupportActionBar();
        t.setTitle("Pre-Match: ");

        setContentView(R.layout.prematch_activity);

        mCompetitionField = (AutoCompleteTextView) findViewById(R.id.competition_name);
        mCompetitionField.setHint("Competition");
        mCompetitionField.setText(mMatchData.getCompetition());
        mCompetitionField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){

            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                checkValidData();
                mMatchData.setCompetition(mCompetitionField.getText().toString());

            }

        });
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (PreMatchActivity.this, android.R.layout.select_dialog_item, Scouter.get(getApplicationContext()).getPastComps());
        mCompetitionField.setAdapter(adapter1);
        mCompetitionField.setThreshold(0);
        mCompetitionField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange (View v, boolean hasFocus){
                if(hasFocus){
                    mCompetitionField.showDropDown();
                }
            }
        });

        mScouterNameField =(AutoCompleteTextView)findViewById(R.id.scouter_name);
        mScouterNameField.setHint("Name");
        mScouterNameField.setText(mMatchData.getName());
        mScouterNameField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){

            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                //updatePreMatchData();
                checkValidData();
                mMatchData.setName(mScouterNameField.getText().toString());
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

        mTeamNumberField = (EditText)findViewById(R.id.team_number_field);
        mTeamNumberField.setText(mMatchData.getTeamNumber()+"");
        mTeamNumberField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){

            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                t.setSubtitle(c + "  " + mTeamNumberField.getText().toString());
                checkValidData();

            }

        });


        mMatchNumberField = (EditText)findViewById(R.id.match_number_field);
        if(!formattedMN().equals("")){
            mMatchNumberField.setText(formattedMN());
        }
        mMatchNumberField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){

            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                t.setSubtitle( mMatchNumberField.getText().toString()+ " " + c);
                checkValidData();
            }

        });

        mStartScoutingButton = (Button) findViewById(R.id.start_scouting_button);
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

        mErrorMessagepm = (TextView) findViewById(R.id.error_message_pm);
        mErrorMessagepm.setVisibility(View.INVISIBLE);
        mErrorMessagepm.setTextColor(Color.RED);

        mMatchTypesSpinner = (Spinner) findViewById(R.id.match_types);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.match_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMatchTypesSpinner.setAdapter(adapter);


    }

    public void updatePreMatchData(){
        mScout = Scouter.get(getApplicationContext());
        mScout.addPastComp(mCompetitionField.getText().toString());
        mScout.addPastScouter(mScouterNameField.getText().toString());
        mScout.saveData(getApplicationContext());
        mMatchData.setName(mScouterNameField.getText().toString());
        mMatchData.setCompetition(mCompetitionField.getText().toString());
        mMatchData.setMatchNumber(mMatchTypesSpinner.getSelectedItem().toString()+ mMatchNumberField.getText().toString().trim());
        mMatchData.setTeamNumber(mTeamNumberField.getText().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        if(requestCode == 0){
            finish();
        }
    }

    private boolean checkValidData(){
        if(mCompetitionField.getText().toString().trim().equals("")||mScouterNameField.getText().toString().trim().equals("")|| mTeamNumberField.getText().toString().trim().equals("")||mMatchNumberField.getText().toString().trim().equals("")){
            mErrorMessagepm.setVisibility(View.VISIBLE);
            return false;
        }
        mErrorMessagepm.setVisibility(View.INVISIBLE);
        return true;
    }

    public String formattedMN(){
        String full = mMatchData.getMatchNumber();
        String formatted = "";
        if(full.length()<2){
            return "";
        }
        else{
            if(full.substring(1,2).equals("F")){
                formatted = full.substring(2);
            }
            else{
                formatted = full.substring(1);
            }
        }

        return formatted;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}



