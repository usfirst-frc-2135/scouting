package com.bignerdranch.android.qrgen_new;

import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Calendar;
import java.util.Date;

public class PreMatchFragment extends Fragment {

    private AutoCompleteTextView mCompetitionField;
    private AutoCompleteTextView mScouterNameField;
    private EditText mTeamNumberField;
    private EditText mMatchNumberField;



    private MatchData mMatchData;
    private ActionBar t;


    private static final int REQUEST_DATETIME = 0;
    public static final String TAG = "PreMatch Fragment";
    public static final String EXTRA_DATE = "com.bignerranch.android.qrgen.date";
    public static final String TDTAG = "date/time";

    private String scout_name;
    private Date scout_date;

    private static Scouter mScout;


    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        mMatchData = ((ScoutingActivity)getActivity()).getCurrentMatch();

        t =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        t.setTitle("Pre-Match: ");

        mMatchData = ((ScoutingActivity)getActivity()).getCurrentMatch();
        Log.d(TAG, mMatchData.getMatchID());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.prematch_fragment, parent, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();

        mCompetitionField = (AutoCompleteTextView) v.findViewById(R.id.competition_name);
        mCompetitionField.setHint("Competition");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, Scouter.get(getContext()).getPastComps());
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

        mScouterNameField =(AutoCompleteTextView)v.findViewById(R.id.scouter_name);
        mScouterNameField.setHint("Name");
        mScouterNameField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){

            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
            }

        });
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, Scouter.get(getContext()).getPastScouts());
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

        mTeamNumberField = (EditText)v.findViewById(R.id.team_number_field);
        mTeamNumberField.setText(mMatchData.getTeamNumber()+"");
        mTeamNumberField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){

            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                t.setSubtitle(c + "  " + mMatchNumberField.getText().toString());

            }

        });


        mMatchNumberField = (EditText)v.findViewById(R.id.match_number_field);
        mMatchNumberField.setText(mMatchData.getMatchNumber());
        mMatchNumberField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){

            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                t.setSubtitle( mTeamNumberField.getText().toString()+ " " + c);
            }

        });

        return v;
    }

    public void updatePreMatchData(){
        mScout = Scouter.get(getContext());
        mScout.addPastComp(mCompetitionField.getText().toString());
        mScout.addPastScouter(mScouterNameField.getText().toString());
        mScout.saveData(getContext());
        mMatchData.setName(mScouterNameField.getText().toString());
        mMatchData.setCompetition(mCompetitionField.getText().toString());
        mMatchData.setMatchNumber(mMatchNumberField.getText().toString().trim());
        mMatchData.setTeamNumber(mTeamNumberField.getText().toString());
    }

    public String formattedDate(Date d){
        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd hh:mm:ss z yyyy");
        Date date = null;
        try{
            date=dt.parse(d.toString());
        }catch(Exception e){
            Log.d("SignInFragment", e.getMessage());
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("E, dd MMM yyyy");
        return (dt1.format(date));
    }

}



