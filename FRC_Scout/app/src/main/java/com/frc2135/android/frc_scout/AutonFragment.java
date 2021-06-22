package com.frc2135.android.frc_scout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


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

    private MatchData mMatchData;

    private ActionBar t;


    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        mMatchData = ((ScoutingActivity)getActivity()).getCurrentMatch();
        Log.d(TAG, mMatchData.getMatchID());

        t =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        t.setTitle("Autonomous");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.auton_fragment, parent, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();

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
        mCheckBox.setChecked(mMatchData.getPassedInitLine());



        mHighPoints.setText(mMatchData.getAutonHighPoints()+"");
        mLowPoints.setText(mMatchData.getAutonLowPoints()+"");

        return v;
    }

    public void updateAutonData(){
        mMatchData.setAutonLowPoints(Integer.parseInt(mLowPoints.getText().toString()));
        mMatchData.setAutonOuterPoints(Integer.parseInt(mHighPoints.getText().toString()));
        mMatchData.setPassedInitLine(mCheckBox.isChecked());
    }
}
