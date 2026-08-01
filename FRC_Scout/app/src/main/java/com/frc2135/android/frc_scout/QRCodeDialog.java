/*
 * Copyright (c) 2020-26 FRC 2135 Presentation Invasion
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
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.frc2135.android.frc_scout.databinding.QrCodeDialogBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
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
    private static final String ARG_CAN_SAVE = "can_save";

    private QrCodeDialogBinding m_binding;

    /**
     * Creates a new instance of {@link QRCodeDialog} for the given match data.
     * Encodes the match statistics and a human-readable label into the fragment's arguments.
     *
     * @param matchData the match data to encode into the QR code
     * @param canSave   if true, the dialog will show a "DONE" button to save the match and exit
     * @return a new QRCodeDialog instance
     */
    public static QRCodeDialog newInstance(MatchData matchData, boolean canSave)
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
        bundle.putBoolean(ARG_CAN_SAVE, canSave);
        //        bundle.putString(ARG_STATS, matchData.encodeToJSON());
        dialog.setArguments(bundle);
        return dialog;
    }

    /**
     * Creates a new instance of {@link QRCodeDialog} in read-only mode (no save capability).
     *
     * @param matchData the match data to encode into the QR code
     * @return a new QRCodeDialog instance
     */
    public static QRCodeDialog newInstance(MatchData matchData)
    {
        return newInstance(matchData, false);
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
        boolean canSave = args.getBoolean(ARG_CAN_SAVE, false);

        generateQRCode(stats);
        m_binding.qrDialogDataPreview.setText(stats);

        m_binding.qrDialogBackButton.setOnClickListener(v -> dismiss());

        if (canSave)
        {
            m_binding.qrDialogDoneButton.setVisibility(View.VISIBLE);
            m_binding.qrDialogDoneButton.setOnClickListener(v -> {
                Log.i(TAG, "Done button clicked");
                MatchData matchData = ((ScoutingActivity) requireActivity()).getCurrentMatch();
                Settings settings = Settings.getInstance(requireContext());

                settings.setMostRecentMatchNumber(matchData.getMatchNumber());
                settings.addPastScoutNames(matchData.getScoutName());
                settings.setMostRecentScoutName(matchData.getScoutName());

                Log.i(TAG, "Saving latest match and scout names");
                if (!settings.saveSettingsSilent())
                {
                    Log.e(TAG, "Failed to save settings!");
                }

                ScoutedMatches scoutedMatches = ScoutedMatches.getInstance(requireContext());
                if (!scoutedMatches.saveMatchDataFile(matchData))
                {
                    Log.e(TAG, "Failed to save Match Data!");
                    Snackbar.make(m_binding.getRoot(), "Error: Failed to save match data!", Snackbar.LENGTH_SHORT).show();
                }

                Intent i = new Intent(requireContext(), MatchListActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                requireActivity().finish();
            });
        }
        else
        {
            m_binding.qrDialogDoneButton.setVisibility(View.GONE);
            m_binding.qrDialogBackButton.setText(android.R.string.ok);
        }

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(label)
                .setView(m_binding.getRoot())
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
     * Called when the dialog is visible to the user. Forces the screen brightness to 100%
     * to ensure optimal contrast for the scanning camera.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume");

        // Force brightness to 100% for scanning reliability
        if (getDialog() != null)
        {
            Window window = getDialog().getWindow();
            if (window != null)
            {
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                window.setAttributes(layoutParams);
            }
        }
    }

    /**
     * Cleans up the view binding reference when the fragment view is being destroyed.
     */
    @Override
    public void onDestroyView()
    {
        if (m_binding != null)
        {
            m_binding.qrDialogImage.setImageBitmap(null);
        }
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");
        m_binding = null;
    }
}
