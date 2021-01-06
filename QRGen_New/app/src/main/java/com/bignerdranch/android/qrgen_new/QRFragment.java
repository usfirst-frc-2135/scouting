package com.bignerdranch.android.qrgen_new;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.io.FileNotFoundException;
import java.util.Scanner;

import zxing.Contents;
import zxing.QRCodeEncoder;



public class QRFragment extends Fragment {
    private static String TAG = "QRFragment";
    private ImageView mImageView;
    private Button mBacktoMenuButton;

    private String message;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.qr_fragment, parent, false);
        //FragmentManager fm = getActivity().getSupportFragmentManager();

        mBacktoMenuButton = (Button)v.findViewById(R.id.back_to_menu_button);
        mBacktoMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MatchListActivity.class);
                startActivityForResult(i, 0);
                Log.d("ScoutingActivity", "Sent intent");
            }
        });

        message = "https://script.google.com/macros/s/AKfycbwWsew9waAQc0dCZKoD4ebgGYlN-OgbpTqicsROJ4E/dev?csv="+getActivity().getIntent().getStringExtra("stats");

        ImageView mImageView = (ImageView)v.findViewById(R.id.matchdata_qr);

        int qrCodeDimension = 1000;

        Log.d(TAG, message);

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(message, null,
                Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimension);

        Log.i(TAG, message);

        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            mImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.d("QRFragment", e+"");
        }


        return v;
    }
}
