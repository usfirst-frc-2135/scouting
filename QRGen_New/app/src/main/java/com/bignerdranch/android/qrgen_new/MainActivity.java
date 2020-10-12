package com.bignerdranch.android.qrgen_new;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton; //Imports button necessary for creating QR code generate button
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;//Imports use of fragments
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;



public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";// This is the tag that will be used for all Log statements generated from this activity
    private static MatchData mMatchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMatchData = new MatchData();

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
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            //Setting an onClickListener makes it so that our button actually senses for when it is clicked, and when it is clicked, it will proceed with onClick()

            @Override
            public void onClick(View view) {
                //Uses intents to start the QQ code activity --> changes screens
                Snackbar.make(view, "Generating QR code", Snackbar.LENGTH_LONG).setAction("Action", null).show();
               Intent i = new Intent(MainActivity.this, QRActivity.class);
               i.putExtra("stats", mMatchData.toString());
               startActivityForResult(i, 0);
               Log.d("MainActivity", "Sent intent");

            }
        });

    }

    //This method returns an instance of the class MatchFragment, so that whichever XML file is linked to Match Fragment will be placed in fragmentContainer
    protected Fragment createFragment(){
        setContentView(R.layout.activity_main);
        return new com.bignerdranch.android.qrgen_new.MatchFragment();
    }

    protected static MatchData getMatchData(){
        return mMatchData;
    }


    @Override
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
    }

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
