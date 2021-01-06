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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.UUID;


public class TeleopFragment extends Fragment {
    private static final String TAG = "TeleopFragment";

    private TextView mLowPoints;
    private TextView mHighPoints;


    private Button mLowPortPointsDec;
    private Button mLowPortPointsInc;
    private Button mHighPortPointsDec;
    private Button mHighPortPointsInc;

    private CheckBox mCheckBox;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton0;
    private RadioButton mRadioButton1;
    private RadioButton mRadioButton2;
    private RadioButton mRadioButton3;
    private RadioButton mRadioButton4;
    private RadioButton mRadioButton5;
    private EditText mEditText;

    private MatchData mMatchData;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.teleop_fragment, parent, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();


        UUID mMatchId = (UUID)getArguments().getSerializable("match ID");
        Log.d(TAG, mMatchId.toString());
        mMatchData = MatchHistory.get(getContext()).getMatch(mMatchId);



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
        mCheckBox = (CheckBox)v.findViewById(R.id.climb_checkbox);
        mCheckBox.setChecked(false);// Default is unchecked


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
                    mMatchData.setDefense(0);
                }
                if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton1.getId()) {
                    mMatchData.setDefense(1);
                }
                if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton2.getId()) {
                    mMatchData.setDefense(2);
                }
                if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton3.getId()) {
                    mMatchData.setDefense(3);
                }
                if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton4.getId()) {
                    mMatchData.setDefense(4);
                }
                if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton5.getId()) {
                    mMatchData.setDefense(5);
                }
            }
        });


        //Sets up an EditText that allows users to input any additional comments
        mEditText = (EditText)v.findViewById(R.id.comments);
        mEditText.setHint("Enter any additional comments here");
        mEditText.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
            }

        });

        FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            //Setting an onClickListener makes it so that our button actually senses for when it is clicked, and when it is clicked, it will proceed with onClick()

            @Override
            public void onClick(View view) {

                mMatchData.setExtComments(mEditText.getText());
                mMatchData.setTeleopLowPoints(Integer.parseInt(mLowPoints.getText().toString()));
                mMatchData.setTeleopOuterPoints(Integer.parseInt(mHighPoints.getText().toString()));
                mMatchData.setPassedInitLine(mCheckBox.isChecked());



                //Uses intents to start the QQ code activity --> changes screens
                Snackbar.make(view, "Generating QR code", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent i = new Intent(getActivity(), QRActivity.class);
                i.putExtra("stats", mMatchData.encodeToURL());
                startActivityForResult(i, 0);
                Log.d("ScoutingActivity", "Sent intent");

            }
        });



        return v;
    }

}
