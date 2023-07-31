package com.frc2135.android.frc_scout;

import android.graphics.Color;
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

public class TeleopFragment extends Fragment
{
    private static final String TAG = "TeleopFragment";
    private static final int MAX_POINTS = 30;     // max for valid high or low points total

    private TextView m_teleopConesBottomRowValue;
    private TextView m_teleopConesMiddleRowValue;
    private TextView m_teleopConesTopRowValue;

    private TextView m_teleopCubesBottomRowValue;
    private TextView m_teleopCubesMiddleRowValue;
    private TextView m_teleopCubesTopRowValue;

    private Button m_teleopConeBottomDecrButton;
    private Button m_teleopConeBottomIncrButton;

    private Button m_teleopConeMiddleDecrButton;
    private Button m_teleopConeMiddleIncrButton;

    private Button m_teleopConeTopDecrButton;
    private Button m_teleopConeTopIncrButton;

    private Button m_teleopCubeBottomDecrButton;
    private Button m_teleopCubeBottomIncrButton;

    private Button m_teleopCubeMiddleDecrButton;
    private Button m_teleopCubeMiddleIncrButton;

    private Button m_teleopCubeTopDecrButton;
    private Button m_teleopCubeTopIncrButton;

    private CheckBox m_pickupCubeCheckbox;
    private CheckBox m_pickupUprightCheckbox;
    private CheckBox m_pickupTippedCheckbox;
    private MatchData m_matchData;
    private ActionBar m_actionBar;

    // Check if pointsTextView field has a valid number, equal or less than MAX_POINTS.
    private boolean isValidPoints(TextView field)
    {
        boolean rtn = false;
        int num = Integer.parseInt(field.getText().toString());
        if (num <= MAX_POINTS)
            rtn = true;
        return rtn;
    }

    // Sets the new result integer value for the given Button, either decrementing or incrementing it.
    // If the decrement case falls below zero, returns 0. Sets textView to RED if out of valid range.
    public void updatePointsInt(TextView pointsTextView, boolean bIncr)
    {
        int result = Integer.parseInt(pointsTextView.getText().toString()); // get current value as int
        if (bIncr)
            result += 1;
        else
            result -= 1;
        if (result < 0)
            result = 0;
        pointsTextView.setText(result + "");
        if (!isValidPoints(pointsTextView))
        {
            pointsTextView.setTextColor(Color.RED);
        }
        else
            pointsTextView.setTextColor(getResources().getColor(R.color.specialTextPrimary));
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        m_matchData = ((ScoutingActivity) getActivity()).getCurrentMatch();
        m_actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        String teamNumber = m_matchData.stripTeamNamePrefix(m_matchData.getTeamNumber());
        m_actionBar.setTitle("Teleoperated          Scouting Team " + teamNumber + "         Match " + m_matchData.getMatchNumber());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.teleop_fragment, parent, false);

        //Sets up TextView that displays cones bottom row points, setting 0 as the default
        m_teleopConesBottomRowValue = v.findViewById(R.id.teleopcone_bottom_text);
        m_teleopConesBottomRowValue.setText(0 + "");
        m_teleopConesBottomRowValue.setTextColor(getResources().getColor(R.color.specialTextPrimary));

        //Sets up TextView that displays cones middle row points, setting 0 as the default
        m_teleopConesMiddleRowValue = v.findViewById(R.id.teleopcone_middle_text);
        m_teleopConesMiddleRowValue.setText(0 + "");
        m_teleopConesMiddleRowValue.setTextColor(getResources().getColor(R.color.specialTextPrimary));

        //Sets up TextView that displays cones top row points, setting 0 as the default
        m_teleopConesTopRowValue = v.findViewById(R.id.teleopcone_top_text);
        m_teleopConesTopRowValue.setText(0 + "");
        m_teleopConesTopRowValue.setTextColor(getResources().getColor(R.color.specialTextPrimary));

        //Sets up TextView that displays cubes bottom row points, setting 0 as the default
        m_teleopCubesBottomRowValue = v.findViewById(R.id.teleopcube_bottom_text);
        m_teleopCubesBottomRowValue.setText(0 + "");
        m_teleopCubesBottomRowValue.setTextColor(getResources().getColor(R.color.specialTextPrimary));

        //Sets up TextView that displays cubes middle row points, setting 0 as the default
        m_teleopCubesMiddleRowValue = v.findViewById(R.id.teleopcube_middle_text);
        m_teleopCubesMiddleRowValue.setText(0 + "");
        m_teleopCubesMiddleRowValue.setTextColor(getResources().getColor(R.color.specialTextPrimary));

        //Sets up TextView that displays cubes top row points, setting 0 as the default
        m_teleopCubesTopRowValue = v.findViewById(R.id.teleopcube_top_text);
        m_teleopCubesTopRowValue.setText(0 + "");
        m_teleopCubesTopRowValue.setTextColor(getResources().getColor(R.color.specialTextPrimary));

        //Connects the decrement button for cones bottom row points and sets up a listener that detects when the button is clicked
        m_teleopConeBottomDecrButton = v.findViewById(R.id.teleopcone_bottom_decr);
        m_teleopConeBottomDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_teleopConesBottomRowValue, false);
            }
        });

        //Connects the increment button for cones bottom row points and sets up a listener that detects when the button is clicked
        m_teleopConeBottomIncrButton = v.findViewById(R.id.teleopcone_bottom_incr);
        m_teleopConeBottomIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_teleopConesBottomRowValue, true);
            }
        });

        //Connects the decrement button for cones middle row points and sets up a listener that detects when the button is clicked
        m_teleopConeMiddleDecrButton = v.findViewById(R.id.teleopcone_middle_decr);
        m_teleopConeMiddleDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_teleopConesMiddleRowValue, false);
            }
        });

        //Connects the increment button for cones middle row points and sets up a listener that detects when the button is clicked
        m_teleopConeMiddleIncrButton = v.findViewById(R.id.teleopcone_middle_incr);
        m_teleopConeMiddleIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_teleopConesMiddleRowValue, true);
            }
        });

        //Connects the decrement button for cones top row points and sets up a listener that detects when the button is clicked
        m_teleopConeTopDecrButton = v.findViewById(R.id.teleopcone_top_decr);
        m_teleopConeTopDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_teleopConesTopRowValue, false);
            }
        });

        //Connects the increment button for cones top row points and sets up a listener that detects when the button is clicked
        m_teleopConeTopIncrButton = v.findViewById(R.id.teleopcone_top_incr);
        m_teleopConeTopIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_teleopConesTopRowValue, true);
            }
        });

        //Connects the decrement button for cubes bottom row points and sets up a listener that detects when the button is clicked
        m_teleopCubeBottomDecrButton = v.findViewById(R.id.teleopcube_bottom_decr);
        m_teleopCubeBottomDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_teleopCubesBottomRowValue, false);
            }
        });

        //Connects the increment button for cubes bottom row points and sets up a listener that detects when the button is clicked
        m_teleopCubeBottomIncrButton = v.findViewById(R.id.teleopcube_bottom_incr);
        m_teleopCubeBottomIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_teleopCubesBottomRowValue, true);
            }
        });

        //Connects the decrement button for cubes middle row points and sets up a listener that detects when the button is clicked
        m_teleopCubeMiddleDecrButton = v.findViewById(R.id.teleopcube_middle_decr);
        m_teleopCubeMiddleDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_teleopCubesMiddleRowValue, false);
            }
        });

        //Connects the increment button for cubes middle row points and sets up a listener that detects when the button is clicked
        m_teleopCubeMiddleIncrButton = v.findViewById(R.id.teleopcube_middle_incr);
        m_teleopCubeMiddleIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_teleopCubesMiddleRowValue, true);
            }
        });

        //Connects the decrement button for cubes top row points and sets up a listener that detects when the button is clicked
        m_teleopCubeTopDecrButton = v.findViewById(R.id.teleopcube_top_decr);
        m_teleopCubeTopDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_teleopCubesTopRowValue, false);
            }
        });

        //Connects the increment button for cubes top row points and sets up a listener that detects when the button is clicked
        m_teleopCubeTopIncrButton = v.findViewById(R.id.teleopcube_top_incr);
        m_teleopCubeTopIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_teleopCubesTopRowValue, true);
            }
        });

        m_pickupCubeCheckbox = v.findViewById(R.id.pickup_cube_checkbox);
        m_pickupCubeCheckbox.setChecked(m_matchData.getPickedUpCube());

        m_pickupUprightCheckbox = v.findViewById(R.id.pickup_upright_checkbox);
        m_pickupUprightCheckbox.setChecked(m_matchData.getPickedUpUpright());

        m_pickupTippedCheckbox = v.findViewById(R.id.pickup_tipped_checkbox);
        m_pickupTippedCheckbox.setChecked(m_matchData.getPickedUpTipped());

        m_teleopConesBottomRowValue.setText(m_matchData.getTeleopConesBottomRow() + "");
        m_teleopConesMiddleRowValue.setText(m_matchData.getTeleopConesMiddleRow() + "");
        m_teleopConesTopRowValue.setText(m_matchData.getTeleopConesTopRow() + "");
        m_teleopCubesBottomRowValue.setText(m_matchData.getTeleopCubesBottomRow() + "");
        m_teleopCubesMiddleRowValue.setText(m_matchData.getTeleopCubesMiddleRow() + "");
        m_teleopCubesTopRowValue.setText(m_matchData.getTeleopCubesTopRow() + "");
        if (!isValidPoints(m_teleopConesBottomRowValue))
        {
            m_teleopConesBottomRowValue.setTextColor(Color.RED);
        }
        if (!isValidPoints(m_teleopConesMiddleRowValue))
        {
            m_teleopConesMiddleRowValue.setTextColor(Color.RED);
        }
        if (!isValidPoints(m_teleopConesTopRowValue))
        {
            m_teleopConesTopRowValue.setTextColor(Color.RED);
        }
        if (!isValidPoints(m_teleopCubesBottomRowValue))
        {
            m_teleopCubesBottomRowValue.setTextColor(Color.RED);
        }
        if (!isValidPoints(m_teleopCubesMiddleRowValue))
        {
            m_teleopCubesMiddleRowValue.setTextColor(Color.RED);
        }
        if (!isValidPoints(m_teleopCubesTopRowValue))
        {
            m_teleopCubesTopRowValue.setTextColor(Color.RED);
        }
        return v;
    }

    public void updateTeleopData()
    {
        m_matchData.setTeleopConesBottomRow(Integer.parseInt(m_teleopConesBottomRowValue.getText().toString()));
        m_matchData.setTeleopConesMiddleRow(Integer.parseInt(m_teleopConesMiddleRowValue.getText().toString()));
        m_matchData.setTeleopConesTopRow(Integer.parseInt(m_teleopConesTopRowValue.getText().toString()));
        m_matchData.setTeleopCubesBottomRow(Integer.parseInt(m_teleopCubesBottomRowValue.getText().toString()));
        m_matchData.setTeleopCubesMiddleRow(Integer.parseInt(m_teleopCubesMiddleRowValue.getText().toString()));
        m_matchData.setTeleopCubesTopRow(Integer.parseInt(m_teleopCubesTopRowValue.getText().toString()));

        m_matchData.setPickedUpCube(m_pickupCubeCheckbox.isChecked());
        m_matchData.setPickedUpUpright(m_pickupUprightCheckbox.isChecked());
        m_matchData.setPickedUpTipped(m_pickupTippedCheckbox.isChecked());
    }

    public String formattedDate(Date d)
    {
        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd hh:mm:ss z yyyy");
        Date date = null;
        try
        {
            date = dt.parse(d.toString());
        }
        catch (Exception e)
        {
            Log.d("SignInFragment", e.getMessage());
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("hh:mm:ss");

        if (date == null)
        {
            return null;
        }
        else
        {
            return (dt1.format(date));
        }
    }
}
