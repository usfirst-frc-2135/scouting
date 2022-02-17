package com.frc2135.android.frc_scout;

import android.icu.text.SimpleDateFormat;
import android.graphics.Color;
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
    private static final int MAX_POINTS = 20;     // max for valid high or low points total

    private TextView  m_lowPointsValue;
    private TextView  m_highPointsValue;

    private Button    m_lowDecrButton;
    private Button    m_lowIncrButton;
    private Button    m_highDecrButton;
    private Button    m_highIncrButton;

    private MatchData m_matchData;
    private ActionBar m_actionBar;

    // Check if pointsTextView field has a valid number, equal or less than MAX_POINTS.
    private boolean isValidPoints(TextView field) {
        boolean rtn = false;
        int num = Integer.parseInt(field.getText().toString());
        if(num <= MAX_POINTS)
            rtn = true;
        return rtn;
    }

    // Sets the new result integer value for the given Button, either decrementing or incrementing it.
    // If the decrement case falls below zero, returns 0. Sets textView to RED if out of valid range.
    public void updatePointsInt(TextView pointsTextView,boolean bIncr){
        int result = Integer.parseInt(pointsTextView.getText().toString()); // get current value as int
        if(bIncr)
            result += 1;
        else result -= 1;
        if(result < 0)
            result = 0;
        pointsTextView.setText(result + "");
        if(!isValidPoints(pointsTextView)) {
            pointsTextView.setTextColor(Color.RED);
        }
        else pointsTextView.setTextColor(getResources().getColor(R.color.textPrimary));
    }
 
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        m_matchData = ((ScoutingActivity)getActivity()).getCurrentMatch();
        m_actionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        String teamNumber = m_matchData.stripTeamNamePrefix(m_matchData.getTeamNumber());
        m_actionBar.setTitle("Teleoperated                      - scouting Team "+teamNumber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.teleop_fragment, parent, false);

        //Sets up TextView that displays low points, setting 0 as the default
        m_lowPointsValue = v.findViewById(R.id.lowpoints);
        m_lowPointsValue.setText(0 + "");
        m_lowPointsValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Sets up TextView that displays high points, setting 0 as the default
        m_highPointsValue = v.findViewById(R.id.highpoints);
        m_highPointsValue.setText(0+ "");
        m_highPointsValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Connects the decrement button for low points and sets up a listener that detects when the button is clicked
        m_lowDecrButton = v.findViewById(R.id.lowpointsdec);
        m_lowDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decreases displayed point value by 1; sets to 0 if result goes negative.
                updatePointsInt(m_lowPointsValue,false);
            }
        });

        //Connects the increment button for low points and sets up a listener that detects when the button is clicked
        m_lowIncrButton = v.findViewById(R.id.lowpointsinc);
        m_lowIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Increases displayed point value by 1
                updatePointsInt(m_lowPointsValue,true);
            }
        });

        //Connects the decrement button for high points and sets up a listener that detects when the button is clicked
        m_highDecrButton = v.findViewById(R.id.highpointsdec);
        m_highDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decreases displayed point value by 1; sets to 0 if negative result
                updatePointsInt(m_highPointsValue,false);
            }
        });

        //Connects the increment button for high points and sets up a listener that detects when the button is clicked
        m_highIncrButton = v.findViewById(R.id.highpointsinc);
        m_highIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_highPointsValue,true);
            }
        });

        m_highPointsValue.setText(m_matchData.getTelopHighPoints()+"");
        m_lowPointsValue.setText(m_matchData.getTeleopLowPoints()+"");
        if(!isValidPoints(m_lowPointsValue)) {
            m_lowPointsValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_highPointsValue)) {
            m_highPointsValue.setTextColor(Color.RED);
        }

        return v;
    }

    public void updateTeleopData(){
        m_matchData.setTeleopLowPoints(Integer.parseInt(m_lowPointsValue.getText().toString()));
        m_matchData.setTeleopHighPoints(Integer.parseInt(m_highPointsValue.getText().toString()));
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
