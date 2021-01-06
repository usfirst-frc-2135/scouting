package com.bignerdranch.android.qrgen_new;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.Calendar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Date;

public class SignInFragment extends Fragment {

    private TextView mSignInInstructions;
    private TextView mErrorMessage1;
    private TextView mScoutingDate;
    private EditText mScouterNameField;
    private ImageButton mDatePickerButton;
    private Button mSignInButton;

    private String scout_name;
    private Date scout_date;

    private static Scouter mScout;


    private static final int REQUEST_DATETIME = 0;
    public static final String EXTRA_DATE = "com.bignerranch.android.qrgen.date";
    public static final String TDTAG = "date/time";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.sign_in, parent, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();

        mSignInInstructions = (TextView)v.findViewById(R.id.sign_in_text);

        mScouterNameField = (EditText)v.findViewById(R.id.scouter_name);
        mScouterNameField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                scout_name = c.toString();
            }

        });

        mDatePickerButton = (ImageButton)v.findViewById(R.id.date_button);
        mDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance();
                dialog.setTargetFragment(SignInFragment.this, REQUEST_DATETIME);
                /*Bundle bundle = new Bundle();
                bundle.putSerializable(EXTRA_CRIME_ID1, mCrime.getId());
                dialog.setArguments(bundle);*/
                dialog.show(fm, TDTAG);
            }

            });

        mScoutingDate = (TextView)v.findViewById(R.id.scouting_date);
        mScoutingDate.setText(formattedDate(Calendar.getInstance().getTime()).toString());


        mErrorMessage1 = (TextView)v.findViewById(R.id.error_message1);
        mErrorMessage1.setVisibility(View.INVISIBLE);

        mSignInButton = (Button)v.findViewById(R.id.log_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mScouterNameField.getText().toString().trim().equals("")){
                    mErrorMessage1.setText("***Please fill in the required fields");
                    mErrorMessage1.setTextColor(Color.RED);
                    mErrorMessage1.setVisibility(View.VISIBLE);
                }
                else{
                    mScout = Scouter.get();
                    mScout.setName(mScouterNameField.getText().toString());
                    mScout.setDate(mScoutingDate.getText().toString());

                    Intent i = new Intent(getActivity(), MatchListActivity.class);
                    startActivityForResult(i, 0);
                    Log.d("SignInFragment", "Sent intent");

                }

            }
        });


        return v;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Date d = new Date(data.getStringExtra(EXTRA_DATE));
        mScoutingDate.setText(formattedDate(d).toString());
    }


    public String formattedDate(Date d){
        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd hh:mm:ss z yyyy");
        Date date = null;
        try{
            date=dt.parse(d.toString());
        }catch(Exception e){
            Log.d("SignInFragment", e.getMessage());
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("E, dd MMM yyyy");
        return (dt1.format(date));
    }
}
