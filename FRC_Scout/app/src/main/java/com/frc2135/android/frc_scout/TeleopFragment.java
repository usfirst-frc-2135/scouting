package com.frc2135.android.frc_scout;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Date;


public class TeleopFragment extends Fragment {
    private static final String TAG = "TeleopFragment";

    private TextView mLowPoints;
    private TextView mHighPoints;

    private Button mLowPortPointsDec;
    private Button mLowPortPointsInc;
    private Button mHighPortPointsDec;
    private Button mHighPortPointsInc;

    private CheckBox mRotationCheckBox;


    private MatchData mMatchData;
    private ActionBar t;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mMatchData = ((ScoutingActivity)getActivity()).getCurrentMatch();
        Log.d(TAG, mMatchData.getMatchID());

        t =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        t.setTitle("Teleoperated");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.teleop_fragment, parent, false);



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

        //Connects the checkbox for rotation control and sets up a listener to detect when the checked status is changed
        mRotationCheckBox = (CheckBox)v.findViewById(R.id.rotation_checkbox);
        mRotationCheckBox.setChecked(mMatchData.getRotationControl());



        mHighPoints.setText(mMatchData.getTelopHighPoints()+"");
        mLowPoints.setText(mMatchData.getTeleopLowPoints()+"");

        return v;
    }

    public void updateTeleopData(){
        mMatchData.setTeleopLowPoints(Integer.parseInt(mLowPoints.getText().toString()));
        mMatchData.setTeleopOuterPoints(Integer.parseInt(mHighPoints.getText().toString()));
        mMatchData.setPassedInitLine(mRotationCheckBox.isChecked());
        MatchHistory.get(getActivity()).saveData();
    }

    public String formattedDate(Date d){
        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd hh:mm:ss z yyyy");
        Date date = null;
        try{
            date=dt.parse(d.toString());
        }catch(Exception e){
            Log.d("SignInFragment", e.getMessage());
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("hh:mm:ss");

        if(date == null) {
            return null;
        }
        else { return (dt1.format(date)); }
    }


}
