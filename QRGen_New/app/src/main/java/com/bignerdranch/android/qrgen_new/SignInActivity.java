package com.bignerdranch.android.qrgen_new;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Connects this Java class to the XML file activity_main, linking the UI to the controller layer
        setContentView(R.layout.activity_main);

        //Initializes FragmentManager so that we can host a fragment within our activity
        final FragmentManager fm = getSupportFragmentManager();
        final Fragment[] fragment = {fm.findFragmentById(R.id.fragmentContainer)};
        fragment[0] = createFragment();

        //Designates that chosen fragment will be housed within fragmentContainer, a frame layout in the activity's XML
        fm.beginTransaction().add(R.id.fragmentContainer, fragment[0]).commit();

        //Set up the send button which generates a QR code upon being pressed


    }

    //This method returns an instance of the class MatchFragment, so that whichever XML file is linked to Match Fragment will be placed in fragmentContainer
    protected Fragment createFragment() {
        setContentView(R.layout.activity_main);
        return new com.bignerdranch.android.qrgen_new.SignInFragment();
    }
}
