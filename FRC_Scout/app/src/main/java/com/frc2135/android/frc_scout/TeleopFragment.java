package com.frc2135.android.frc_scout;
import android.util.Log;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.widget.RadioButton;
import android.widget.RadioGroup;


/** @noinspection SpellCheckingInspection*/
public class TeleopFragment extends Fragment
{
    private static final String TAG = "TeleopFragment";
    private static final int MAX_POINTS_ALGAE = 7;     // max for valid high or low points total
    private static final int MAX_POINTS_CORAL= 11;     // max for valid high or low points total

    private CheckBox m_pickUpCoral;
    private CheckBox m_pickUpAlgae;
    private CheckBox m_knockAlgaeOff;
    private CheckBox m_algaeFromReef;
    private CheckBox m_holdBothElements;
    private TextView m_coralAcquire;
    private TextView m_algaeAcquire;

    private RadioGroup m_defenseButtonGroup;
    private RadioButton m_defenseNone;
    private RadioButton m_defenseLow;
    private RadioButton m_defenseMedium;
    private RadioButton m_defenseHigh;

    private RadioGroup m_foulPin;
    private RadioButton m_zeroPin;
    private RadioButton m_onePin;
    private RadioButton m_twoPin;

    private RadioGroup m_foulAnchor;
    private RadioButton m_zeroAnchor;
    private RadioButton m_oneAnchor;
    private RadioButton m_twoAnchor;

    private RadioGroup m_foulCage;
    private RadioButton m_zeroCage;
    private RadioButton m_oneCage;
    private RadioButton m_twoCage;

    private RadioGroup m_teleopFoulBarge;
    private RadioButton m_teleopZeroBarge;
    private RadioButton m_teleopOneBarge;
    private RadioButton m_teleopTwoBarge;

    private RadioGroup m_teleopFoulReef;
    private RadioButton m_teleopZeroReef;
    private RadioButton m_teleopOneReef;
    private RadioButton m_teleopTwoReef;

    private MatchData m_matchData;

    // Check if pointsTextView field is greater than the MAX_POINTS.
    private boolean isGreaterThanMax(TextView field,boolean bIsCoral)
    {
        boolean rtn = false;
        int num = Integer.parseInt(field.getText().toString());
        if (bIsCoral == true) {
            if (num > MAX_POINTS_CORAL)  // for coral number
                rtn = true;
        } else  // for algae number
        {
            if (num > MAX_POINTS_ALGAE)
                rtn = true;
        }
        return rtn;
    }



    // Sets the new result integer value for the given Button, either decrementing or incrementing it.
    // If the decrement case falls below zero, returns 0. Sets textView to RED if out of valid range.
    public void updatePointsInt(TextView pointsTextView, boolean bIncr, boolean bIsCoral)
    {
        int result = Integer.parseInt(pointsTextView.getText().toString()); // get current value as int
        if (bIncr)
            result += 1;
        else
            result -= 1;
        if (result < 0)
            result = 0;
        pointsTextView.setText(String.valueOf(result));

        if (isGreaterThanMax(pointsTextView,bIsCoral))
        {
            pointsTextView.setTextColor(Color.RED);
        }
        else
        {
            Context context = getContext();
            if (context != null)
            {
                pointsTextView.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));
            }
        }
    }

    /*
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        m_matchData = ((ScoutingActivity) requireActivity()).getCurrentMatch();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null)
        {
            String teamNumber = m_matchData.stripTeamNamePrefix(m_matchData.getTeamNumber());
            actionBar.setTitle("Teleoperated          Scouting Team " + teamNumber + "         Match " + m_matchData.getMatchNumber());
        }
    }

    /**
     * @noinspection Convert2Lambda
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        //Creates a view using the specific fragment layout.
        View v = inflater.inflate(R.layout.teleop_fragment, parent, false);
        Context context = getContext();
        if (context != null)
        {
            int specialTextPrimaryColor = ContextCompat.getColor(context, R.color.specialTextPrimary);

            // Setup TextViews that displays points, setting 0 as the default.
            // defense buttons
            m_defenseButtonGroup = v.findViewById(R.id.defense_buttons);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
            m_defenseNone = v.findViewById(R.id.defense_none);//Sets up radio button that corresponds to 0
            m_defenseLow = v.findViewById(R.id.defense_low);//Sets up radio button that corresponds to 1
            m_defenseMedium  = v.findViewById(R.id.defense_medium);//Sets up radio button that corresponds to 2
            m_defenseHigh  = v.findViewById(R.id.defense_high);//Sets up radio button that corresponds to 3
            m_defenseNone.setChecked(false);
            m_defenseLow.setChecked(false);
            m_defenseMedium .setChecked(false);
            m_defenseHigh .setChecked(false);

            int defValue = m_matchData.getPlayedDefense();
            if (defValue == 0)
                m_defenseNone.setChecked(true);
            else if(defValue == 1)
              m_defenseLow.setChecked(true);
            else if(defValue == 2)
                m_defenseMedium.setChecked(true);
            else if(defValue == 3)
                m_defenseHigh.setChecked(true);

            //foul pin radio buttons
            m_foulPin = v.findViewById(R.id.foul_pin);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
            m_zeroPin = v.findViewById(R.id.zero_pin);//Sets up radio button that corresponds to 0
            m_onePin  = v.findViewById(R.id.one_pin );//Sets up radio button that corresponds to 1
            m_twoPin  = v.findViewById(R.id.two_pin);//Sets up radio button that corresponds to 2
            m_zeroPin.setChecked(false);
            m_onePin.setChecked(false);
            m_twoPin.setChecked(false);

            int defValuePin = m_matchData.getFoulPin();
            if (defValuePin == 0)
                m_zeroPin.setChecked(true);
            else if(defValuePin == 1)
                m_onePin.setChecked(true);
            else if(defValuePin == 2)
                m_twoPin.setChecked(true);

            //foul touching anchor radio buttons
            m_foulAnchor = v.findViewById(R.id.foul_anchor);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
            m_zeroAnchor = v.findViewById(R.id.zero_anchor);//Sets up radio button that corresponds to 0
            m_oneAnchor  = v.findViewById(R.id.one_anchor);//Sets up radio button that corresponds to 1
            m_twoAnchor  = v.findViewById(R.id.two_anchor);//Sets up radio button that corresponds to 2
            m_zeroAnchor.setChecked(false);
            m_oneAnchor.setChecked(false);
            m_twoAnchor.setChecked(false);

            int defValueAnc = m_matchData.getFoulAnchor();
            if (defValueAnc == 0)
                m_zeroAnchor.setChecked(true);
            else if(defValueAnc == 1)
                m_oneAnchor.setChecked(true);
            else if(defValueAnc == 2)
                m_twoAnchor.setChecked(true);

            //foul touching opponents cage radio buttons
            m_foulCage = v.findViewById(R.id.foul_cage);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
            m_zeroCage = v.findViewById(R.id.zero_cage);//Sets up radio button that corresponds to 0
            m_oneCage  = v.findViewById(R.id.one_cage);//Sets up radio button that corresponds to 1
            m_twoCage  = v.findViewById(R.id.two_cage);//Sets up radio button that corresponds to 2
            m_zeroCage.setChecked(false);
            m_oneCage.setChecked(false);
            m_twoCage.setChecked(false);

            int defValueCage = m_matchData.getFoulCage();
            if (defValueCage == 0)
                m_zeroCage.setChecked(true);
            else if(defValueCage == 1)
                m_oneCage.setChecked(true);
            else if(defValueCage == 2)
                m_twoCage.setChecked(true);


            //foul touching oppent while in barge radio buttons
            m_teleopFoulBarge = v.findViewById(R.id.teleop_foul_barge);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
            m_teleopZeroBarge = v.findViewById(R.id.teleop_zero_barge);//Sets up radio button that corresponds to 0
            m_teleopOneBarge  = v.findViewById(R.id.teleop_one_barge);//Sets up radio button that corresponds to 1
            m_teleopTwoBarge  = v.findViewById(R.id.teleop_two_barge);//Sets up radio button that corresponds to 2
            m_teleopZeroBarge.setChecked(false);
            m_teleopOneBarge.setChecked(false);
            m_teleopTwoBarge.setChecked(false);

            int defValueBarg = m_matchData.getFoulBarge();
            if (defValueBarg == 0)
                m_teleopZeroBarge.setChecked(true);
            else if(defValueBarg == 1)
                m_teleopOneBarge.setChecked(true);
            else if(defValueBarg == 2)
                m_teleopTwoBarge.setChecked(true);


            //foul touching oppent while in reef safe zone radio buttons
            m_teleopFoulReef = v.findViewById(R.id.teleop_foul_reef);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
            m_teleopZeroReef = v.findViewById(R.id.teleop_zero_reef);//Sets up radio button that corresponds to 0
            m_teleopOneReef  = v.findViewById(R.id.teleop_one_reef);//Sets up radio button that corresponds to 1
            m_teleopTwoReef  = v.findViewById(R.id.teleop_two_reef);//Sets up radio button that corresponds to 2
            m_teleopZeroReef.setChecked(false);
            m_teleopOneReef.setChecked(false);
            m_teleopTwoReef.setChecked(false);

            int defValueReef = m_matchData.getFoulReef();
            if (defValueReef == 0)
                m_teleopZeroReef.setChecked(true);
            else if(defValueReef == 1)
                m_teleopOneReef.setChecked(true);
            else if(defValueReef == 2)
                m_teleopTwoReef.setChecked(true);


            // TODO set defense value from matchData

            m_algaeAcquire = v.findViewById(R.id.algae_acquire_text);
            m_algaeAcquire.setText("0");
            m_algaeAcquire.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));
            m_coralAcquire = v.findViewById(R.id.coral_acquire_text);
            m_coralAcquire.setText("0");
            m_coralAcquire.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));


            //Connects the decrement button for amp scoring and sets up a listener that detects when the button is clicked
            Button coralAcquireDecrButton = v.findViewById(R.id.coral_acquire_decr);
            coralAcquireDecrButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // Decreases displayed point value by 1; sets to 0 if result would be negative.
                    updatePointsInt(m_coralAcquire, false, true);
                }
            });


            //Connects the increment button for amp scoring and sets up a listener that detects when the button is clicked
            Button coralAcquireIncrButton = v.findViewById(R.id.coral_acquire_incr);
            coralAcquireIncrButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //Increases displayed point value by 1
                    updatePointsInt(m_coralAcquire, true, true);
                }
            });



        //Connects the increment button for amp misses and sets up a listener that detects when the button is clicked
        Button algaeAcquireIncrButton = v.findViewById(R.id.algae_acquire_incr);
        algaeAcquireIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_algaeAcquire, true, false) ;
            }
        });

        //Connects the decr button for amp misses and sets up a listener that detects when the button is clicked
        Button algaeAcquireDecrButton = v.findViewById(R.id.algae_acquire_decr);
        algaeAcquireDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_algaeAcquire, false, false);
            }
        });

            m_pickUpCoral = v.findViewById(R.id.pickup_coral);
            m_pickUpCoral.setChecked(m_matchData.getPickUpCoral());

            m_pickUpAlgae = v.findViewById(R.id.pickup_algae);
            m_pickUpAlgae.setChecked(m_matchData.getPickUpAlgae());

            m_knockAlgaeOff = v.findViewById(R.id.knock_algae_off);
            m_knockAlgaeOff.setChecked(m_matchData.getKnockOffAlgae());

            m_algaeFromReef = v.findViewById(R.id.algae_from_reef);
            m_algaeFromReef.setChecked(m_matchData.getAlgaeFromReef());

            m_holdBothElements = v.findViewById(R.id.hold_both_elements);
            m_holdBothElements.setChecked(m_matchData.getHoldBothElements());

            m_coralAcquire.setText(String.valueOf(m_matchData.getCoralAcquire()));
            m_algaeAcquire.setText(String.valueOf(m_matchData.getAlgaeAcquire()));

            if (isGreaterThanMax(m_coralAcquire,true))
            {
                m_coralAcquire.setTextColor(Color.RED);
            }
            if (isGreaterThanMax(m_algaeAcquire,false))
            {
                m_algaeAcquire.setTextColor(Color.RED);
            }

        }
        return v;

    }
//used for defense radio buttons
    public int getCurrentDefenseLevel()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_defenseButtonGroup.getCheckedRadioButtonId() == m_defenseNone.getId())
        {
            rtn = 0;
        }
        if (m_defenseButtonGroup.getCheckedRadioButtonId() == m_defenseLow.getId())
        {
            rtn = 1;
        }
        if (m_defenseButtonGroup.getCheckedRadioButtonId() == m_defenseMedium.getId())
        {
            rtn = 2;
        }
        if (m_defenseButtonGroup.getCheckedRadioButtonId() == m_defenseHigh .getId())
        {
            rtn = 3;
        }
        return rtn;
    }

    //used for pin foul radio buttons
    public int getCurrentNumberPin()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_foulPin.getCheckedRadioButtonId() == m_zeroPin.getId())
        {
            rtn = 0;
        }
        if (m_foulPin.getCheckedRadioButtonId() == m_onePin.getId())
        {
            rtn = 1;
        }
        if (m_foulPin.getCheckedRadioButtonId() == m_twoPin.getId())
        {
            rtn = 2;
        }
        return rtn;
    }

    public int getCurrentNumberAnchor()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_foulAnchor.getCheckedRadioButtonId() == m_zeroAnchor.getId())
        {
            rtn = 0;
        }
        if (m_foulAnchor.getCheckedRadioButtonId() == m_oneAnchor.getId())
        {
            rtn = 1;
        }
        if (m_foulAnchor.getCheckedRadioButtonId() == m_twoAnchor.getId())
        {
            rtn = 2;
        }
        return rtn;
    }

    public int getCurrentNumberCage()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_foulCage.getCheckedRadioButtonId() == m_zeroCage.getId())
        {
            rtn = 0;
        }
        if (m_foulCage.getCheckedRadioButtonId() == m_oneCage.getId())
        {
            rtn = 1;
        }
        if (m_foulCage.getCheckedRadioButtonId() == m_twoCage.getId())
        {
            rtn = 2;
        }
        return rtn;
    }

    public int getCurrentTeleopNumberBarge()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_teleopFoulBarge.getCheckedRadioButtonId() == m_teleopZeroBarge.getId())
        {
            rtn = 0;
        }
        if (m_teleopFoulBarge.getCheckedRadioButtonId() == m_teleopOneBarge.getId())
        {
            rtn = 1;
        }
        if (m_teleopFoulBarge.getCheckedRadioButtonId() == m_teleopTwoBarge.getId())
        {
            rtn = 2;
        }
        return rtn;
    }

    public int getCurrentTeleopNumberReef()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_teleopFoulReef.getCheckedRadioButtonId() == m_teleopZeroReef.getId())
        {
            rtn = 0;
        }
        if (m_teleopFoulReef.getCheckedRadioButtonId() == m_teleopOneReef.getId())
        {
            rtn = 1;
        }
        if (m_teleopFoulReef.getCheckedRadioButtonId() == m_teleopTwoReef.getId())
        {
            rtn = 2;
        }
        return rtn;
    }




    public void updateTeleopData()
    {
        m_matchData.setCoralAcquire(Integer.parseInt(m_coralAcquire.getText().toString()));

        m_matchData.setAlgaeAcquire(Integer.parseInt(m_algaeAcquire.getText().toString()));

        m_matchData.setPickUpCoral(m_pickUpCoral.isChecked());

        m_matchData.setPickUpAlgae(m_pickUpAlgae.isChecked());

        m_matchData.setKnockOffAlgae(m_knockAlgaeOff.isChecked());

        m_matchData.setAlgaeFromReef(m_algaeFromReef.isChecked());

        m_matchData.setHoldBothElements(m_holdBothElements.isChecked());

        m_matchData.setPlayedDefense(getCurrentDefenseLevel());

        m_matchData.setFoulPin(getCurrentNumberPin());

        m_matchData.setFoulAnchor(getCurrentNumberAnchor());

        m_matchData.setFoulCage(getCurrentNumberCage());

        m_matchData.setFoulBarge(getCurrentTeleopNumberBarge());

        m_matchData.setFoulReef(getCurrentTeleopNumberReef());


    }

/*REMOVE->
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
            Log.d(TAG, e.getMessage());
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
<-REMOVE*/
}
