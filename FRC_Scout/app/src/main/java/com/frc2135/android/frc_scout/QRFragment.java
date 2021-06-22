package com.frc2135.android.frc_scout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.util.Date;

import zxing.Contents;
import zxing.QRCodeEncoder;



public class QRFragment extends DialogFragment {
    private static String TAG = "QRFragment";
    private ImageView mImageView;
    private Button mBacktoMenuButton;
    private String heading;
    private String message;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = getActivity().getLayoutInflater().inflate(R.layout.qr_fragment, null);

        heading =  getArguments().getString("dialog heading");
        message = getArguments().getString("stats");


        ImageView mImageView = (ImageView)v.findViewById(R.id.matchdata_qr);

        int qrCodeDimension = 1000;

        Log.d(TAG, message);

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(message, null,
                Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimension);

        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            mImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.d("QRFragment", e+"");
        }

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).setTitle(heading).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                //sendResult(Activity.RESULT_OK);
            }
        }).create();

        dialog.show();
        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setBackgroundColor(Color.parseColor("#3F51B5"));

        return dialog;
    }


    public static QRFragment newInstance(MatchData mMatchData){

        QRFragment fragment = new QRFragment();

        Bundle bundle = new Bundle();
        bundle.putString("dialog heading", mMatchData.getCompetition()+"-"+mMatchData.getMatchNumber()+"-"+mMatchData.getTeamNumber()+"-" + formattedDate(mMatchData.getTimestamp()));
        bundle.putString("stats", mMatchData.encodeToTSV());
        fragment.setArguments(bundle);

        return fragment;
    }

    public static String formattedDate(Date d){
        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd hh:mm:ss z yyyy");
        Date date = null;
        try{
            date=dt.parse(d.toString());
        }catch(Exception e){
            Log.d("SignInFragment", e.getMessage());
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("[yyyy/M/dd hh:mm:ss]");

        if(date == null) {
            return null;
        }
        else { return (dt1.format(date)); }
    }


}
