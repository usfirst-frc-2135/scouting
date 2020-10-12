package com.bignerdranch.android.qrgen_new;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

import static com.bignerdranch.android.qrgen_new.MainActivity.getMatchData;


public class MatchFragment extends Fragment {


    private TextView mLowPoints;
    private TextView mHighPoints;


    private Button mLowPortPointsDec;
    private Button mLowPortPointsInc;
    private Button mHighPortPointsDec;
    private Button mHighPortPointsInc;
    private Button mClearFormButton;

    private CheckBox mCheckBox;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton0;
    private RadioButton mRadioButton1;
    private RadioButton mRadioButton2;
    private RadioButton mRadioButton3;
    private RadioButton mRadioButton4;
    private RadioButton mRadioButton5;
    private EditText mEditText;

    private static final String TAG = "MatchFragment";




    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.match_data_fragment, parent, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();

        //Sets up TextView that displays low points, setting 0 as the default
        mLowPoints = (TextView)v.findViewById(R.id.lowportpoints);
        mLowPoints.setText(0 + "");

        //Sets up TextView that displays high points, setting 0 as the default
        mHighPoints = (TextView)v.findViewById(R.id.highportpoints);
        mHighPoints.setText(0+ "");

        //Connects the "Clear Form" button and sets up a listener that detects when the button is clicked
        mClearFormButton = (Button)v.findViewById(R.id.clear_form);
        mClearFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sets all mMatchData data to defaults and resets the UI
                getMatchData().clearStats();
                mLowPoints.setText(getMatchData().getLowPoints() + "");
                mHighPoints.setText(getMatchData().getHighPoints() + "");
                mCheckBox.setChecked(false);
                mEditText.setText("");
                mEditText.setHint("Enter any additional comments here");
                mRadioGroup.clearCheck();
            }
        });

        //Connects the decrement button for low points and sets up a listener that detects when the button is clicked
        mLowPortPointsDec = (Button)v.findViewById(R.id.lowportpointsdec);
        mLowPortPointsDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updates mMatchData to reflect the current number of points
                //Decreases displayed point value by 1
                getMatchData().setLowPoints(getMatchData().getLowPoints()-1);
                mLowPoints.setText(getMatchData().getLowPoints() + "");
                Log.d(TAG ,getMatchData().getLowPoints() + "");
            }
        });

        //Connects the increment button for low points and sets up a listener that detects when the button is clicked
        mLowPortPointsInc = (Button)v.findViewById(R.id.lowportpointsinc);
        mLowPortPointsInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updates mMatchData to reflect the current number of points
                //Increases displayed point value by 1
                getMatchData().setLowPoints(getMatchData().getLowPoints()+1);
                mLowPoints.setText(getMatchData().getLowPoints() + "");
            }
        });

        //Connects the decrement button for high points and sets up a listener that detects when the button is clicked
        mHighPortPointsDec = (Button)v.findViewById(R.id.highportpointsdec);
        mHighPortPointsDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updates mMatchData to reflect the current number of points
                //Decreases displayed point value by 1
                getMatchData().setHighPoints(getMatchData().getHighPoints()-1);
                mHighPoints.setText(getMatchData().getHighPoints() + "");
            }
        });

        //Connects the increment button for high points and sets up a listener that detects when the button is clicked
        mHighPortPointsInc = (Button)v.findViewById(R.id.highportpointsinc);
        mHighPortPointsInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updates mMatchData to reflect the current number of points
                //Increases displayed point value by 1
                getMatchData().setHighPoints(getMatchData().getHighPoints()+1);
                mHighPoints.setText(getMatchData().getHighPoints() + "");
            }
        });

        //Connects the checkbox for passing the initiation line and sets up a listener to detect when the checked status is changed
        mCheckBox = (CheckBox)v.findViewById(R.id.auto_line_checkbox);
        mCheckBox.setChecked(false);// Default is unchecked
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                //When the checked status changes, mMatchData is changed to reflect the status of whether the initiation line was passed
                getMatchData().setPassedInitLine(isChecked);
            }
        });

        mRadioGroup = (RadioGroup)v.findViewById(R.id.defense_scale);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        mRadioButton0 = (RadioButton)v.findViewById(R.id.level_zero);//Sets up radio button that corresponds to 0
        mRadioButton0.setActivated(true);
        mRadioButton1 = (RadioButton)v.findViewById(R.id.level_one);//Sets up radio button that corresponds to 1
        mRadioButton2 = (RadioButton)v.findViewById(R.id.level_two);//Sets up radio button that corresponds to 2
        mRadioButton3 = (RadioButton)v.findViewById(R.id.level_three);//Sets up radio button that corresponds to 3
        mRadioButton4 = (RadioButton)v.findViewById(R.id.level_four);//Sets up radio button that corresponds to 4
        mRadioButton5 = (RadioButton)v.findViewById(R.id.level_five);//Sets up radio button that corresponds to 5
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
                                                   @Override
                                                   public void onCheckedChanged(RadioGroup group, int checkedId) {

                                                       //Changes mMatchData's defense variable according to which radio button is selected
                                                       if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton0.getId()) {
                                                           getMatchData().setDefense(0);
                                                       }
                                                       if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton1.getId()) {
                                                           getMatchData().setDefense(1);
                                                       }
                                                       if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton2.getId()) {
                                                           getMatchData().setDefense(2);
                                                       }
                                                       if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton3.getId()) {
                                                           getMatchData().setDefense(3);
                                                       }
                                                       if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton4.getId()) {
                                                           getMatchData().setDefense(4);
                                                       }
                                                       if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton5.getId()) {
                                                           getMatchData().setDefense(5);
                                                       }
                                                   }
                                               });


        //Sets up an EditText that allows users to input any additional comments
        mEditText = (EditText)v.findViewById(R.id.comments);
        mEditText.setHint("Enter any additional comments here");
        mEditText.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){
                //Sets the extComments field in mMatchData to whatever the text is changed to when it is changed
                getMatchData().setExtComments(c);
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
            }

        });


        return v;
    }


}
