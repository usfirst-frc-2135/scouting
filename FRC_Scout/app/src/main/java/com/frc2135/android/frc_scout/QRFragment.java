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

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import zxing.Contents;
import zxing.QRCodeEncoder;

public class QRFragment extends DialogFragment
{
    private static final String TAG = "QRFragment";

    /** @noinspection Convert2Lambda*/
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String stats = "";
        View v = requireActivity().getLayoutInflater().inflate(R.layout.qr_fragment, null);
        Bundle args = getArguments();
        if (args != null)
            stats = args.getString("stats");

        ImageView imageView = v.findViewById(R.id.match_data_qr);

        int qrCodeDimension = 750;

        Log.d(TAG, "stats: " + stats);

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(stats, null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimension);

        try
        {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e)
        {
            Log.d(TAG, "qrCodeEncoder Error: " + e + "");
        }

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).setTitle(stats).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                //sendResult(Activity.RESULT_OK);
            }
        }).create();

        dialog.show();
        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setBackgroundColor(Color.parseColor("#3F51B5"));
        return dialog;
    }

    public static QRFragment newInstance(MatchData matchData)
    {

        QRFragment fragment = new QRFragment();

        Bundle bundle = new Bundle();
        bundle.putString("match label", matchData.getEventCode() + "-" + matchData.getMatchNumber() + "-" + matchData.getTeamNumber() + "-" + formattedDate(matchData.getTimestamp()));
        bundle.putString("stats", matchData.encodeToTSV());
        fragment.setArguments(bundle);

        return fragment;
    }

    public static String formattedDate(Date d)
    {
        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd hh:mm:ss z yyyy", Locale.US);
        Date date = null;
        try
        {
            date = dt.parse(d.toString());
        } catch (Exception e)
        {
            Log.d("SignInFragment", Objects.requireNonNull(e.getMessage()));
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-M-dd hh:mm:ss", Locale.US);

        if (date == null)
        {
            return null;
        }
        else
        {
            return (dt1.format(date).substring(0, 9) + "T" + dt1.format(date).substring(10));
        }
    }
}
