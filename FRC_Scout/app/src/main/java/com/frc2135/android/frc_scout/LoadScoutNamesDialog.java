package com.frc2135.android.frc_scout;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.frc2135.android.frc_scout.databinding.LoadEventDialogBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

/**
 * Dialog for loading scout names for a specific event from the team's scouting website.
 */
public class LoadScoutNamesDialog extends DialogFragment
{
    private static final String TAG = "LoadScoutNamesDialog";
    private static final String SCOUT_NAMES_URL = "https://www.frc2135.org/json/";
    private static final String SCOUT_NAMES_SUFFIX = "_scoutNames.json";
    private LoadEventDialogBinding m_binding;

    /**
     * Creates a new instance of LoadScoutsDialog.
     *
     * @return a new LoadScoutsDialog instance
     */
    public static LoadScoutNamesDialog newInstance()
    {
        return new LoadScoutNamesDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateDialog called");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        m_binding = LoadEventDialogBinding.inflate(inflater);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.load_scout_names_title)
                .setView(m_binding.getRoot())
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton("Clear", (d, w) -> {
                    m_binding.eventCodeField.setText("");
                    m_binding.eventCodeField.setError(null);
                })
                .create();

        dialog.setOnShowListener(d -> {
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(v -> handleOkClick(dialog));
        });

        return dialog;
    }

    private void handleOkClick(AlertDialog dialog)
    {
        String eventCode = Objects.requireNonNull(m_binding.eventCodeField.getText()).toString().trim().toLowerCase(Locale.US);

        Settings settings = Settings.getInstance(requireContext());
        if (!settings.isValidEventCode(eventCode))
        {
            m_binding.eventCodeField.setError("Invalid event code (e.g., 2026casac)");
            return;
        }

        downloadScoutNames(eventCode, dialog);
    }

    /**
     * Downloads scout name data from the team website for the specified event code.
     * Updates the UI state to show loading during the request.
     * On success, saves the data locally and dismisses the dialog.
     *
     * @param eventCode the FRC event code (e.g., "2026casac")
     * @param dialog    the dialog instance to update or dismiss upon completion
     */
    private void downloadScoutNames(String eventCode, AlertDialog dialog)
    {
        Log.i(TAG, "Starting scouts download for: " + eventCode);

        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setEnabled(false);
        okButton.setText(R.string.loading);

        String urlStr = SCOUT_NAMES_URL + eventCode + SCOUT_NAMES_SUFFIX;
        Log.i(TAG, "URL: " + urlStr);

        Context context = requireContext().getApplicationContext();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlStr, null,
                response -> {
                    Log.i(TAG, "Successfully received scout names data");
                    if (response.length() == 0)
                    {
                        Log.w(TAG, "Received empty scout names for: " + eventCode);
                        Toast.makeText(context, "No scout names found for " + eventCode, Toast.LENGTH_LONG).show();
                        resetUiState(okButton);
                        return;
                    }

                    try
                    {
                        Settings settings = Settings.getInstance(context);
                        settings.saveEventScoutNames(context, eventCode, response);
                        Toast.makeText(context, "Successfully downloaded scouts for " + eventCode, Toast.LENGTH_LONG).show();
                        if (isAdded())
                        {
                            dismiss();
                        }
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG, "Error saving scouts: " + e.getMessage());
                        Toast.makeText(context, "Error saving scout names", Toast.LENGTH_SHORT).show();
                        okButton.setEnabled(true);
                        okButton.setText(android.R.string.ok);
                        resetUiState(okButton);
                    }
                },
                error -> {
                    Log.e(TAG, "Download failed: " + error.toString());
                    String msg = "Failed to download scouts for '" + eventCode + "'. Check connection or event code.";
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    okButton.setEnabled(true);
                    okButton.setText(android.R.string.ok);
                });

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    /**
     * Resets the UI components to their interactive state after a download attempt.
     *
     * @param okButton the dialog's OK button
     */
    private void resetUiState(Button okButton)
    {
        if (okButton != null)
        {
            okButton.setEnabled(true);
            okButton.setText(android.R.string.ok);
        }
        if (m_binding != null)
        {
            m_binding.eventCodeField.setEnabled(true);
        }
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
