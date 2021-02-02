package com.bignerdranch.android.qrgen_new;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;//Imports use of fragments
import androidx.fragment.app.FragmentManager;
import android.util.Log;


public class ScoutingActivity extends AppCompatActivity {
    private static final String TAG = "ScoutingActivity";// This is the tag that will be used for all Log statements generated from this activity




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Connects this Java class to the XML file activity_main, linking the UI to the controller layer
        setContentView(R.layout.scouting_activity);

        //Initializes FragmentManager so that we can host a fragment within our activity
        final FragmentManager fm = getSupportFragmentManager();
        final Fragment[] fragment = {fm.findFragmentById(R.id.fragmentContainer)};
        fragment[0] = createFragment();

        //Designates that chosen fragment will be housed within fragmentContainer, a frame layout in the activity's XML
        fm.beginTransaction().add(R.id.fragmentContainer, fragment[0]).commit();

        //Set up the send button which generates a QR code upon being pressed


    }

    //This method returns an instance of the class MatchFragment, so that whichever XML file is linked to Match Fragment will be placed in fragmentContainer
    protected Fragment createFragment(){
        setContentView(R.layout.scouting_activity);
        return new com.bignerdranch.android.qrgen_new.AutonFragment();
    }






    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop() called");

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
