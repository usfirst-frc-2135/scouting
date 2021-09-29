package com.frc2135.android.frc_scout;

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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

public class EndgameFragment extends Fragment {

    private CheckBox mClimbCheckbox;
    private CheckBox mBalancedCheckbox;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton0;
    private RadioButton mRadioButton1;
    private RadioButton mRadioButton2;
    private RadioButton mRadioButton3;
    private RadioButton mRadioButton4;
    private RadioButton mRadioButton5;
    private CheckBox mDiedCheckbox;
    private EditText mEditText;

    private MatchData mMatchData;

    private static final int REQUEST_QR = 2;
    public static final String QRTAG = "qr";
    private static final String TAG = "Endgame Fragment";

    private ActionBar t;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mMatchData = ((ScoutingActivity)getActivity()).getCurrentMatch();

        t =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        t.setTitle("Endgame");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.endgame_fragment, parent, false);

        //Connects the checkbox for successful climb and sets up a listener to detect when the checked status is changed
        mClimbCheckbox = (CheckBox)v.findViewById(R.id.climb_checkbox_success);
        Log.d(TAG, "Setting up Climb checkbox to "+mMatchData.getClimb());
        mClimbCheckbox.setChecked(mMatchData.getClimb());// Default is unchecked
        mClimbCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateEndgameData();
            }
        });

        //Connects the checkbox for Balanced and sets up a listener to detect when the checked status is changed
        mBalancedCheckbox = (CheckBox)v.findViewById(R.id.climb_checkbox_balance);
        Log.d(TAG, "Setting up Balanced checkbox to "+mMatchData.getBalanced());
        mBalancedCheckbox.setChecked(mMatchData.getBalanced());
        mBalancedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateEndgameData();
            }
        });

        //Connects the checkbox for if the robot dies and sets up a listener to detect when the checked status is changed
        mDiedCheckbox = (CheckBox)v.findViewById(R.id.died_checkbox_true);
        Log.d(TAG, "Setting up Died checkbox to "+mMatchData.getDied());
        mDiedCheckbox.setChecked(mMatchData.getDied());// Default is unchecked
        mDiedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateEndgameData();
            }
        });

        mRadioGroup = (RadioGroup)v.findViewById(R.id.defense_scale);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        mRadioButton0 = (RadioButton)v.findViewById(R.id.level_zero);//Sets up radio button that corresponds to 0
        mRadioButton0.setChecked(true);
        mRadioButton1 = (RadioButton)v.findViewById(R.id.level_one);//Sets up radio button that corresponds to 1
        mRadioButton2 = (RadioButton)v.findViewById(R.id.level_two);//Sets up radio button that corresponds to 2
        mRadioButton3 = (RadioButton)v.findViewById(R.id.level_three);//Sets up radio button that corresponds to 3
        mRadioButton4 = (RadioButton)v.findViewById(R.id.level_four);//Sets up radio button that corresponds to 4
        mRadioButton5 = (RadioButton)v.findViewById(R.id.level_five);//Sets up radio button that corresponds to 5

        int x = mMatchData.getDefense();
        Log.d(TAG,"Setting up defense level to "+x);
        if(x==0) 
            mRadioButton0.setChecked(true);
        else if(x==1) 
            mRadioButton1.setChecked(true);
        else if(x==2) 
            mRadioButton2.setChecked(true);
        else if(x==3) 
            mRadioButton3.setChecked(true);
        else if(x==4) 
            mRadioButton4.setChecked(true);
        else if(x==5) 
            mRadioButton5.setChecked(true);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //Changes mMatchData's defense variable according to which radio button is selected
                mMatchData.setDefense(getCurrentDefenseLevelChecked());
            }
        });

        //Sets up an EditText that allows users to input any additional comments
        mEditText = (EditText)v.findViewById(R.id.comments);
        mEditText.setHint("Enter any additional comments here");
        mEditText.setText(mMatchData.getExtComments());
        mEditText.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
            }
        });

        ImageButton mQRButton = (ImageButton)v.findViewById(R.id.gen_QR);
        mQRButton.setOnClickListener(new View.OnClickListener() {
            //Setting an onClickListener makes it so that our button actually senses for when it is clicked, and when it is clicked, it will proceed with onClick()

            @Override
            public void onClick(View view) {
                //Uses intents to start the QR code dialog
                Snackbar.make(view, "Generating QR code", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Log.d("ScoutingActivity", "Sent intent");
                FragmentManager fm = getActivity().getSupportFragmentManager();
                QRFragment dialog = QRFragment.newInstance(mMatchData);
                dialog.setTargetFragment(EndgameFragment.this, REQUEST_QR);
                dialog.show(fm, QRTAG);
            }
        });

        Button mNextButton  = (Button)v.findViewById(R.id.nav_to_menu_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            //Setting an onClickListener makes it so that our button actually senses for when it is clicked, and when it is clicked, it will proceed with onClick()

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MatchListActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(i, 0);
                getActivity().finish();
                Log.d("TeleopFragment", "Sent intent");
            }
        });

        return v;
    }
    public int getCurrentDefenseLevelChecked() {
        // Returns the integer defense level that is current checked in the radio buttons
        int rtn = 0;
        if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton1.getId()) {
            rtn = 1;
        }
        else if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton2.getId()) {
            rtn = 2;
        }
        else if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton3.getId()) {
            rtn = 3;
        }
        else if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton4.getId()) {
            rtn = 4;
        }
        else if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton5.getId()) {
            rtn = 5;
        }
        return rtn;
    }
    public void updateEndgameData(){
        mMatchData.setClimb(mClimbCheckbox.isChecked());
        mMatchData.setDied(mDiedCheckbox.isChecked());
        mMatchData.setBalanced(mBalancedCheckbox.isChecked());
        mMatchData.setExtComments(mEditText.getText());
        mMatchData.setDefense(getCurrentDefenseLevelChecked());
    }
}
