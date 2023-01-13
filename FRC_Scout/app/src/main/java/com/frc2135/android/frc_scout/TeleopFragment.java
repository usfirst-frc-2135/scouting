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
    private static final int MAX_POINTS = 30;     // max for valid high or low points total

    private TextView m_teleopconesBottomRowValue;
    private TextView m_teleopconesMiddleRowValue;
    private TextView m_teleopconesTopRowValue;

    private TextView m_teleopcubesBottomRowValue;
    private TextView m_teleopcubesMiddleRowValue;
    private TextView m_teleopcubesTopRowValue;

    private Button m_teleopconeBottomDecrButton;
    private Button m_teleopconeBottomIncrButton;

    private Button m_teleopconeMiddleDecrButton;
    private Button m_teleopconeMiddleIncrButton;

    private Button m_teleopconeTopDecrButton;
    private Button m_teleopconeTopIncrButton;

    private Button m_teleopcubeBottomDecrButton;
    private Button m_teleopcubeBottomIncrButton;

    private Button m_teleopcubeMiddleDecrButton;
    private Button m_teleopcubeMiddleIncrButton;

    private Button m_teleopcubeTopDecrButton;
    private Button m_teleopcubeTopIncrButton;

    private CheckBox m_pickupCubeCheckbox;
    private CheckBox m_pickupUprightCheckbox;
    private CheckBox m_pickupTippedCheckbox;
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
        m_actionBar.setTitle("Teleoperated          Scouting Team "+teamNumber+"         Match "+m_matchData.getMatchNumber());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.teleop_fragment, parent, false);

        //Sets up TextView that displays cones bottom row points, setting 0 as the default
        m_teleopconesBottomRowValue = v.findViewById(R.id.teleopcone_bottom_text);
        m_teleopconesBottomRowValue.setText(0 + "");
        m_teleopconesBottomRowValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Sets up TextView that displays cones middle row points, setting 0 as the default
        m_teleopconesMiddleRowValue = v.findViewById(R.id.teleopcone_middle_text);
        m_teleopconesMiddleRowValue.setText(0+ "");
        m_teleopconesMiddleRowValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Sets up TextView that displays cones top row points, setting 0 as the default
        m_teleopconesTopRowValue = v.findViewById(R.id.teleopcone_top_text);
        m_teleopconesTopRowValue.setText(0+ "");
        m_teleopconesTopRowValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Sets up TextView that displays cubes bottom row points, setting 0 as the default
        m_teleopcubesBottomRowValue = v.findViewById(R.id.teleopcube_bottom_text);
        m_teleopcubesBottomRowValue.setText(0 + "");
        m_teleopcubesBottomRowValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Sets up TextView that displays cubes middle row points, setting 0 as the default
        m_teleopcubesMiddleRowValue = v.findViewById(R.id.teleopcube_middle_text);
        m_teleopcubesMiddleRowValue.setText(0+ "");
        m_teleopcubesMiddleRowValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Sets up TextView that displays cubes top row points, setting 0 as the default
        m_teleopcubesTopRowValue = v.findViewById(R.id.teleopcube_top_text);
        m_teleopcubesTopRowValue.setText(0+ "");
        m_teleopcubesTopRowValue.setTextColor(getResources().getColor(R.color.textPrimary));




        //Connects the decrement button for cones bottom row points and sets up a listener that detects when the button is clicked
        m_teleopconeBottomDecrButton = v.findViewById(R.id.teleopcone_bottom_decr);
        m_teleopconeBottomDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_teleopconesBottomRowValue,false);
            }
        });

        //Connects the increment button for cones bottom row points and sets up a listener that detects when the button is clicked
        m_teleopconeBottomIncrButton = v.findViewById(R.id.teleopcone_bottom_incr);
        m_teleopconeBottomIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_teleopconesBottomRowValue,true);
            }
        });

        //Connects the decrement button for cones middle row points and sets up a listener that detects when the button is clicked
        m_teleopconeMiddleDecrButton = v.findViewById(R.id.teleopcone_middle_decr);
        m_teleopconeMiddleDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_teleopconesMiddleRowValue,false);
            }
        });

        //Connects the increment button for cones middle row points and sets up a listener that detects when the button is clicked
        m_teleopconeMiddleIncrButton = v.findViewById(R.id.teleopcone_middle_incr);
        m_teleopconeMiddleIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_teleopconesMiddleRowValue,true);
            }
        });

        //Connects the decrement button for cones top row points and sets up a listener that detects when the button is clicked
        m_teleopconeTopDecrButton = v.findViewById(R.id.teleopcone_top_decr);
        m_teleopconeTopDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_teleopconesTopRowValue,false);
            }
        });

        //Connects the increment button for cones top row points and sets up a listener that detects when the button is clicked
        m_teleopconeTopIncrButton = v.findViewById(R.id.teleopcone_top_incr);
        m_teleopconeTopIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_teleopconesTopRowValue,false);
            }
        });






        //Connects the decrement button for cubes bottom row points and sets up a listener that detects when the button is clicked
        m_teleopcubeBottomDecrButton = v.findViewById(R.id.teleopcube_bottom_decr);
        m_teleopcubeBottomDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_teleopcubesBottomRowValue,false);
            }
        });

        //Connects the increment button for cubes bottom row points and sets up a listener that detects when the button is clicked
        m_teleopcubeBottomIncrButton = v.findViewById(R.id.teleopcube_bottom_incr);
        m_teleopcubeBottomIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_teleopcubesBottomRowValue,true);
            }
        });

        //Connects the decrement button for cubes middle row points and sets up a listener that detects when the button is clicked
        m_teleopcubeMiddleDecrButton = v.findViewById(R.id.teleopcube_middle_decr);
        m_teleopcubeMiddleDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_teleopcubesMiddleRowValue,false);
            }
        });

        //Connects the increment button for cubes middle row points and sets up a listener that detects when the button is clicked
        m_teleopcubeMiddleIncrButton = v.findViewById(R.id.teleopcube_middle_incr);
        m_teleopcubeMiddleIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_teleopcubesMiddleRowValue,true);
            }
        });

        //Connects the decrement button for cubes top row points and sets up a listener that detects when the button is clicked
        m_teleopcubeTopDecrButton = v.findViewById(R.id.teleopcube_top_decr);
        m_teleopcubeTopDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_teleopcubesTopRowValue,false);
            }
        });

        //Connects the increment button for cubes top row points and sets up a listener that detects when the button is clicked
        m_teleopcubeTopIncrButton = v.findViewById(R.id.teleopcube_top_incr);
        m_teleopcubeTopIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_teleopcubesTopRowValue,true);
            }
        });



        m_pickupCubeCheckbox = v.findViewById(R.id.pickup_cube_checkbox);
        m_pickupCubeCheckbox.setChecked(m_matchData.getPickedUpCube());

        m_pickupUprightCheckbox = v.findViewById(R.id.pickup_upright_checkbox);
        m_pickupUprightCheckbox.setChecked(m_matchData.getPickedUpUpright());

        m_pickupTippedCheckbox = v.findViewById(R.id.pickup_tipped_checkbox);
        m_pickupTippedCheckbox.setChecked(m_matchData.getPickedUpTipped());

        m_teleopconesBottomRowValue.setText(m_matchData.getTeleopConesBottomRow()+"");
        m_teleopconesMiddleRowValue.setText(m_matchData.getTeleopConesMiddleRow()+"");
        m_teleopconesTopRowValue.setText(m_matchData.getTeleopConesTopRow()+"");
        m_teleopcubesBottomRowValue.setText(m_matchData.getTeleopCubesBottomRow()+"");
        m_teleopcubesMiddleRowValue.setText(m_matchData.getTeleopCubesMiddleRow()+"");
        m_teleopcubesTopRowValue.setText(m_matchData.getTeleopCubesTopRow()+"");
        if(!isValidPoints(m_teleopconesBottomRowValue)) {
            m_teleopconesBottomRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_teleopconesMiddleRowValue)) {
            m_teleopconesMiddleRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_teleopconesTopRowValue)) {
            m_teleopconesTopRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_teleopcubesBottomRowValue)) {
            m_teleopcubesBottomRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_teleopcubesMiddleRowValue)) {
            m_teleopcubesMiddleRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_teleopcubesTopRowValue)) {
            m_teleopcubesTopRowValue.setTextColor(Color.RED);
        }
        return v;
    }

    public void updateTeleopData(){
        m_matchData.setTeleopConesBottomRow(Integer.parseInt(m_teleopconesBottomRowValue.getText().toString()));
        m_matchData.setTeleopConesMiddleRow(Integer.parseInt(m_teleopconesMiddleRowValue.getText().toString()));
        m_matchData.setTeleopConesTopRow(Integer.parseInt(m_teleopconesTopRowValue.getText().toString()));
        m_matchData.setTeleopCubesBottomRow(Integer.parseInt(m_teleopcubesBottomRowValue.getText().toString()));
        m_matchData.setTeleopCubesMiddleRow(Integer.parseInt(m_teleopcubesMiddleRowValue.getText().toString()));
        m_matchData.setTeleopCubesTopRow(Integer.parseInt(m_teleopcubesTopRowValue.getText().toString()));

        m_matchData.setPickedUpCube(m_pickupCubeCheckbox.isChecked());
        m_matchData.setPickedUpUpright(m_pickupUprightCheckbox.isChecked());
        m_matchData.setPickedUpTipped(m_pickupTippedCheckbox.isChecked());
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
