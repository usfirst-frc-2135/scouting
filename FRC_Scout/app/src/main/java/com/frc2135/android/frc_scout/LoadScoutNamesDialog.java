package com.frc2135.android.frc_scout;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
 * This dialog handles fetching and saving a list of recognized scout names for autopopulation.
 */
public class LoadScoutNamesDialog extends DialogFragment
{
    private static final String TAG = "LoadScoutNamesDialog";
    private LoadEventDialogBinding m_binding;

    /**
     * Creates a new instance of {@link LoadScoutNamesDialog}.
     *
     * @return a new LoadScoutNamesDialog instance
     */
    public static LoadScoutNamesDialog newInstance()
    {
        return new LoadScoutNamesDialog();
    }

    /**
     * Constructs the {@link AlertDialog} instance, initializes View Binding, and sets up
     * the event code input field and listeners for initiating the scout names download.
     *
     * @param savedInstanceState if the dialog is being re-initialized from a previous saved state
     * @return the constructed {@link Dialog}
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateDialog called");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        m_binding = LoadEventDialogBinding.inflate(inflater);

        m_binding.eventCodeField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                m_binding.eventCodeLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.load_scout_names_title)
                .setView(m_binding.getRoot())
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton("Clear Scout Names", (d, w) -> {
                    Log.d(TAG, "Clear Scout Names called");
                    String eventCode = Objects.requireNonNull(m_binding.eventCodeField.getText()).toString().trim();
                    if (!eventCode.isEmpty())
                    {
                        ScoutNames scoutNames = ScoutNames.getInstance(requireContext(), eventCode, false);
                        if (scoutNames.deleteScoutNamesFile(eventCode) > 0)
                        {
                            Toast.makeText(requireContext(), "Cleared Scout Names for " + eventCode, Toast.LENGTH_SHORT).show();
                        }
                    }
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

    /**
     * Handles the logic when the OK button is clicked. Validates the event code
     * and initiates the scout names data download if the code is valid.
     *
     * @param dialog the active {@link AlertDialog} instance
     */
    private void handleOkClick(AlertDialog dialog)
    {
        String eventCode = Objects.requireNonNull(m_binding.eventCodeField.getText()).toString().trim().toLowerCase(Locale.US);

        Settings settings = Settings.getInstance(requireContext());
        if (!settings.isValidEventCode(eventCode))
        {
            m_binding.eventCodeField.setError("Invalid event code (e.g., 2026casac)");
            return;
        }

        m_binding.eventCodeLayout.setError(null);
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
        m_binding.eventCodeField.setEnabled(false);

        String urlStr = Constants.TEAM_WEBSITE_JSON_URL + eventCode + Constants.SCOUT_NAMES_FILENAME_SUFFIX;
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
                        saveScoutNames(context, eventCode, response);
                        Toast.makeText(context, "Successfully downloaded scout names for " + eventCode, Toast.LENGTH_LONG).show();
                        if (isAdded())
                        {
                            dismiss();
                        }
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG, "Error saving scout names: " + e.getMessage());
                        Toast.makeText(context, "Error saving scout names", Toast.LENGTH_SHORT).show();
                        resetUiState(okButton);
                    }
                },
                error -> {
                    Log.e(TAG, "Download scout names failed: " + error.toString());
                    String msg = "Failed to download scout names for '" + eventCode + "'. Check connection or event code.";
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    resetUiState(okButton);
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

    /**
     * Saves the downloaded scout names to internal storage.
     *
     * @param context   the application context
     * @param eventCode the FRC event code
     * @param response  the JSON array of scout names received from the API
     * @throws IOException if saving to disk fails
     */
    private void saveScoutNames(Context context, String eventCode, org.json.JSONArray response)
            throws IOException
    {
        ScoutNames scoutNames = ScoutNames.getInstance(context, eventCode, true);
        scoutNames.saveEventScoutNames(context, eventCode, response);
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    /**
     * Cleans up the View Binding reference when the fragment view is being destroyed.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        m_binding = null;
    }
}
