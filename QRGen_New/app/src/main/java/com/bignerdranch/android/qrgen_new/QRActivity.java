package com.bignerdranch.android.qrgen_new;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Date;

public class QRActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setContentView(R.layout.qr_activity); //Sets the view to qr_activity(an XML file)

        final FragmentManager fm = getSupportFragmentManager();
        final Fragment[] fragment = {fm.findFragmentById(R.id.fragmentContainer)};

        //Sets QRFragment as the fragment that will be housed within fragment_container
        fragment[0] = createFragment();
        fm.beginTransaction().add(R.id.fragmentContainer, fragment[0]).commit();

    }

    //Returns an itnstance of QRFragment so that it can be used by FragmentManager in the onCreate method
    protected Fragment createFragment() {
        setContentView(R.layout.activity_main);
        return new com.bignerdranch.android.qrgen_new.QRFragment();
    }




}
