package com.frc2135.android.frc_scout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.frc2135.android.frc_scout.databinding.QrFragmentBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import zxing.Contents;
import zxing.QRCodeEncoder;

/**
 * Dialog fragment that generates and displays a QR code for match data.
 */
public class QRFragment extends DialogFragment
{
    private static final String TAG = "QRFragment";
    private static final String ARG_STATS = "stats";
    private static final String ARG_LABEL = "match_label";

    private QrFragmentBinding binding;

    /**
     * Creates a new instance of QRFragment for the given match data.
     *
     * @param matchData the match data to encode into the QR code
     * @return a new QRFragment instance
     */
    public static QRFragment newInstance(MatchData matchData)
    {
        QRFragment fragment = new QRFragment();
        Bundle bundle = new Bundle();

        String label = String.format("%s-%s-%s-%s",
                matchData.getEventCode(),
                matchData.getMatchNumber(),
                matchData.getTeamNumber(),
                formattedDate(matchData.getTimestamp()));

        bundle.putString(ARG_LABEL, label);
        bundle.putString(ARG_STATS, matchData.encodeToTSV());
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateDialog called");

        binding = QrFragmentBinding.inflate(getLayoutInflater());

        Bundle args = requireArguments();
        String stats = args.getString(ARG_STATS, "");
        String label = args.getString(ARG_LABEL, "Match Data QR");

        generateQRCode(stats);

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(label)
                .setMessage(stats)
                .setView(binding.getRoot())
                .setPositiveButton(android.R.string.ok, (d, w) -> dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (okButton != null)
            {
                okButton.setBackgroundColor(Color.parseColor("#3F51B5"));
                okButton.setTextColor(Color.WHITE);
            }
        });

        return dialog;
    }

    private void generateQRCode(String text)
    {
        if (text == null || text.isEmpty())
        {
            Log.w(TAG, "Empty text provided for QR code generation");
            return;
        }

        int qrCodeDimension = 750;
        Log.d(TAG, "Generating QR code for text length: " + text.length());

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(text, null, Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(), qrCodeDimension);

        try
        {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            if (bitmap != null)
            {
                binding.matchDataQr.setImageBitmap(bitmap);
            }
        }
        catch (WriterException e)
        {
            Log.e(TAG, "QR code generation failed", e);
        }
    }

    /**
     * Formats a date into a standardized string for labeling.
     *
     * @param date the date to format
     * @return the formatted date string
     */
    public static String formattedDate(Date date)
    {
        if (date == null)
        {
            return "unknown_date";
        }
        // Using MM for consistent two-digit months and HH for 24-hour time.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        return sdf.format(date);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        binding = null;
    }
}
