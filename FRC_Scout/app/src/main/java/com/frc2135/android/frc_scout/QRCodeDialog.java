/*
 * Copyright (c) 2025 FRC 2135 Presentation Invasion
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
 * Dialog fragment that generates and displays a QR code for a scouted match.
 * The QR code encodes the match data in a Tab-Separated Values (TSV) format for easy data transfer to a master computer.
 */
public class QRCodeDialog extends DialogFragment
{
    private static final String TAG = "QRCodeDialog";
    private static final String ARG_LABEL = "match_label";
    private static final String ARG_STATS = "stats";

    private QrCodeDialogBinding m_binding;

    /**
     * Creates a new instance of {@link QRCodeDialog} for the given match data.
     * Encodes the match statistics and a human-readable label into the fragment's arguments.
     *
     * @param matchData the match data to encode into the QR code
     * @return a new QRCodeDialog instance
     */
    public static QRCodeDialog newInstance(MatchData matchData)
    {
        QRCodeDialog dialog = new QRCodeDialog();
        Bundle bundle = new Bundle();

        String label = String.format("%s-%s-%s-%s",
                matchData.getEventCode(),
                matchData.getMatchNumber(),
                matchData.getTeamNumber(),
                formattedDate(matchData.getTimestamp()));

        bundle.putString(ARG_LABEL, label);
        bundle.putString(ARG_STATS, matchData.encodeToTSV());
        dialog.setArguments(bundle);
        return dialog;
    }

    /**
     * Constructs the {@link androidx.appcompat.app.AlertDialog} instance, initializes view binding,
     * and triggers the QR code generation process based on the match data provided in the arguments.
     *
     * @param savedInstanceState if the dialog is being re-initialized from a previous saved state
     * @return the constructed {@link Dialog} instance
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreateDialog called");

        m_binding = QrCodeDialogBinding.inflate(getLayoutInflater());

        Bundle args = requireArguments();
        String label = args.getString(ARG_LABEL, "Match Data QR");
        String stats = args.getString(ARG_STATS, "");

        generateQRCode(stats);
        m_binding.qrDialogDataPreview.setText(stats);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(label)
                .setView(m_binding.getRoot())
                .setPositiveButton(android.R.string.ok, (d, w) -> dismiss())
                .create();
    }

    /**
     * Generates a QR code bitmap from the provided text and displays it in the dialog's ImageView.
     *
     * @param text the raw data string to encode into the QR code
     */
    private void generateQRCode(String text)
    {
        if (text == null || text.isEmpty())
        {
            Log.e(TAG, "Empty text provided for QR code generation");
            return;
        }

        Log.i(TAG, "Generating QR code for text length: " + text.length());

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(text, null, Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(), Constants.QR_CODE_DIMENSION);

        try
        {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            if (bitmap != null)
            {
                m_binding.qrDialogImage.setImageBitmap(bitmap);
            }
        }
        catch (WriterException e)
        {
            Log.e(TAG, "QR code generation failed", e);
        }
    }

    /**
     * Formats a {@link Date} object into a standardized string for labeling (yyyy-MM-dd'T'HH:mm:ss).
     *
     * @param date the date to format
     * @return the formatted date string, or "unknown_date" if null
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

    /**
     * Called when the dialog is visible to the user and actively running.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    /**
     * Cleans up the view binding reference when the fragment view is being destroyed.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");
        m_binding = null;
    }
}
