package com.bignerdranch.android.qrgen_new;

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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import static com.bignerdranch.android.qrgen_new.ScoutingActivity.mMatchData;

public class AutonFragment extends Fragment {
    private static final String TAG = "AutonFragment";

    private TextView mLowPoints;
    private TextView mHighPoints;

    private Button mLowPortPointsDec;
    private Button mLowPortPointsInc;
    private Button mHighPortPointsDec;
    private Button mHighPortPointsInc;

    private EditText mTeamNumberField;
    private EditText mMatchNumberField;

    private CheckBox mCheckBox;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.auton_fragment, parent, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();


        mMatchData = new MatchData();

        mTeamNumberField = (EditText)v.findViewById(R.id.team_number_field);
        mTeamNumberField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                mMatchData.setTeamNumber(c.toString());
            }

        });

        mMatchNumberField = (EditText)v.findViewById(R.id.match_number_field);




        //Sets up TextView that displays low points, setting 0 as the default
        mLowPoints = (TextView)v.findViewById(R.id.lowportpoints);
        mLowPoints.setText(0 + "");

        //Sets up TextView that displays high points, setting 0 as the default
        mHighPoints = (TextView)v.findViewById(R.id.highportpoints);
        mHighPoints.setText(0+ "");


        //Connects the decrement button for low points and sets up a listener that detects when the button is clicked
        mLowPortPointsDec = (Button)v.findViewById(R.id.lowportpointsdec);
        mLowPortPointsDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1
                mLowPoints.setText((Integer.parseInt(mLowPoints.getText().toString())- 1) + "");
            }
        });

        //Connects the increment button for low points and sets up a listener that detects when the button is clicked
        mLowPortPointsInc = (Button)v.findViewById(R.id.lowportpointsinc);
        mLowPortPointsInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                mLowPoints.setText((Integer.parseInt(mLowPoints.getText().toString())+ 1) + "");
            }
        });

        //Connects the decrement button for high points and sets up a listener that detects when the button is clicked
        mHighPortPointsDec = (Button)v.findViewById(R.id.highportpointsdec);
        mHighPortPointsDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1
                mHighPoints.setText((Integer.parseInt(mHighPoints.getText().toString())- 1) + "");
            }
        });

        //Connects the increment button for high points and sets up a listener that detects when the button is clicked
        mHighPortPointsInc = (Button)v.findViewById(R.id.highportpointsinc);
        mHighPortPointsInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                mHighPoints.setText((Integer.parseInt(mHighPoints.getText().toString())+ 1) + "");
            }
        });

        //Connects the checkbox for passing the initiation line and sets up a listener to detect when the checked status is changed
        mCheckBox = (CheckBox)v.findViewById(R.id.auto_line_checkbox);
        mCheckBox.setChecked(false);// Default is unchecked





        FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            //Setting an onClickListener makes it so that our button actually senses for when it is clicked, and when it is clicked, it will proceed with onClick()

            @Override
            public void onClick(View view) {
                mMatchData.setTeamNumber(mTeamNumberField.getText().toString());
                int x = Integer.parseInt(String.valueOf(mMatchNumberField.getText()));
                mMatchData.setMatchNumber(x);
                mMatchData.setAutonLowPoints(Integer.parseInt(mLowPoints.getText().toString()));
                mMatchData.setAutonOuterPoints(Integer.parseInt(mHighPoints.getText().toString()));
                mMatchData.setPassedInitLine(mCheckBox.isChecked());
                //MatchHistory.get(getContext()).addMatch(mMatchData);

                Log.d(TAG, mMatchData.getId()+"");



                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

                TeleopFragment fragment = new TeleopFragment();
                fragmentTransaction.replace(R.id.fragmentContainer, fragment);

                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                //Designates that chosen fragment will be housed within fragmentContainer, a frame layout in the activity's XML


                //Uses intents to start the QQ code activity --> changes screens
                /*Intent i = new Intent(getActivity(), QRActivity.class);
                i.putExtra("stats", mMatchData.encodeToURL());
                startActivityForResult(i, 0);
                Log.d("ScoutingActivity", "Sent intent");*/

            }
        });

        return v;
    }

    protected Fragment createFragment(){
        return new com.bignerdranch.android.qrgen_new.TeleopFragment();
    }
}
