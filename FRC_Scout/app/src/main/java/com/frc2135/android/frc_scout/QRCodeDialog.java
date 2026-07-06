package com.frc2135.android.frc_scout;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.frc2135.android.frc_scout.databinding.QrCodeDialogBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
public class QRCodeDialog extends DialogFragment
{
    private static final String TAG = "QRCodeDialog";
    private static final String ARG_LABEL = "match_label";
    private static final String ARG_STATS = "stats";

    private QrCodeDialogBinding m_binding;

    /**
     * Creates a new instance of QRCodeDialog for the given match data.
     *
     * @param matchData the match data to encode into the QR code
     * @return a new QRCodeDialog instance
     */
    public static QRCodeDialog newInstance(MatchData matchData)
    {
        QRCodeDialog fragment = new QRCodeDialog();
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

        m_binding = QrCodeDialogBinding.inflate(getLayoutInflater());

        Bundle args = requireArguments();
        String label = args.getString(ARG_LABEL, "Match Data QR");
        String stats = args.getString(ARG_STATS, "");

        generateQRCode(stats);
        m_binding.qrDataPreview.setText(stats);

        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(label)
                .setView(m_binding.getRoot())
                .setPositiveButton(android.R.string.ok, (d, w) -> dismiss())
                .create();
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
                m_binding.matchDataQr.setImageBitmap(bitmap);
            }
        }
        catch (WriterException e)
        {
            Log.e(TAG, "QR code generation failed", e);
        }
    }

    /**
     * Formats a date into a standardized string for labeling (yyyy-MM-dd'T'HH:mm:ss).
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
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        m_binding = null;
    }
}
