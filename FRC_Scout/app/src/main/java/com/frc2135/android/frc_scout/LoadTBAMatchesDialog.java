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

import org.json.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Dialog for loading match data for a specific event from The Blue Alliance (TBA) API.
 * This dialog handles event code validation, data download via Volley, and local persistence.
 */
public class LoadTBAMatchesDialog extends DialogFragment
{
    private static final String TAG = "LoadEventDialog";

    private LoadEventDialogBinding m_binding;

    /**
     * Creates a new instance of LoadTBAMatchesDialog.
     *
     * @return a new LoadTBAMatchesDialog instance
     */
    public static LoadTBAMatchesDialog newInstance()
    {
        return new LoadTBAMatchesDialog();
    }

    /**
     * Constructs the {@link AlertDialog} instance, initializes View Binding, and sets up
     * the event code input field with validation listeners.
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

        // Pre-fill with current event code if available
        Settings settings = Settings.getInstance(requireContext());
        if (settings != null)
        {
            String currentEventCode = settings.getEventCode();
            if (!currentEventCode.isEmpty() && !Objects.equals(currentEventCode, "EVTX"))
            {
                m_binding.eventCodeField.setText(currentEventCode);
            }
        }

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
                .setTitle(R.string.load_tba_matches_title)
                .setView(m_binding.getRoot())
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton(R.string.clear_tba_matches, (d, w) -> {
                    Log.d(TAG, "Clear TBA Matches called");
                    String eventCode = Objects.requireNonNull(m_binding.eventCodeField.getText()).toString().trim();
                    if (!eventCode.isEmpty())
                    {
                        TBAMatches tbaMatches = TBAMatches.getInstance(requireContext(), eventCode, false);
                        if (tbaMatches.deleteTBAMatchesFile(eventCode) > 0)
                        {
                            Toast.makeText(requireContext(), "Cleared TBA Matches for " + eventCode, Toast.LENGTH_SHORT).show();
                        }
                    }
                    m_binding.eventCodeField.setText("");
                    m_binding.eventCodeLayout.setError(null);
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
     * and initiates the match data download if the code is valid.
     *
     * @param dialog the active {@link AlertDialog} instance
     */
    private void handleOkClick(AlertDialog dialog)
    {
        Log.d(TAG, "handleOkClick called");
        String eventCode = Objects.requireNonNull(m_binding.eventCodeField.getText()).toString().trim().toLowerCase(Locale.US);

        Settings settings = Settings.getInstance(requireContext());
        if (!settings.isValidEventCode(eventCode))
        {
            m_binding.eventCodeLayout.setError("Invalid event code (e.g., 2026casac)");
            return;
        }

        m_binding.eventCodeLayout.setError(null);
        downloadTBAMatches(dialog, eventCode);
    }

    /**
     * Downloads event match data from The Blue Alliance API for the specified event code.
     * Updates the UI state to show loading during the request.
     * On success, saves the data locally and dismisses the dialog.
     *
     * @param dialog    the dialog instance to update or dismiss upon completion
     * @param eventCode the TBA event code (e.g., "2026casac")
     */
    private void downloadTBAMatches(AlertDialog dialog, String eventCode)
    {
        Log.d(TAG, "Starting TBA matches download for: " + eventCode);

        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setEnabled(false);
        okButton.setText(R.string.loading);
        m_binding.eventCodeField.setEnabled(false);

        String urlStr = Constants.TBA_EVENT_MATCHES_URL + eventCode + "/matches";
        Log.d(TAG, "URL: " + urlStr);

        Context context = requireContext().getApplicationContext();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlStr, null,
                response -> {
                    Log.d(TAG, "Successfully received TBA matches for: " + eventCode);
                    if (response.length() == 0)
                    {
                        Log.w(TAG, "Received empty TBA matches list for: " + eventCode);
                        Toast.makeText(context, "No TBA matches found for " + eventCode, Toast.LENGTH_LONG).show();
                        resetUiState(okButton);
                        return;
                    }

                    try
                    {
                        saveTBAMatches(context, eventCode, response);
                        Toast.makeText(context, "Successfully downloaded " + response.length() + " TBA matches for " + eventCode, Toast.LENGTH_LONG).show();
                        if (isAdded())
                        {
                            dismiss();
                        }
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG, "Error saving TBA matches: " + e.getMessage(), e);
                        Toast.makeText(context, "Error saving TBA matches locally", Toast.LENGTH_SHORT).show();
                        resetUiState(okButton);
                    }
                },
                error -> {
                    Log.e(TAG, "Download TBA matches failed: " + error.toString());
                    StringBuilder msg = new StringBuilder("Failed to download TBA matches. ");
                    if (error.networkResponse != null)
                    {
                        int statusCode = error.networkResponse.statusCode;
                        switch (statusCode)
                        {
                            case 401 -> msg.append("Invalid TBA API Key.");
                            case 404 -> msg.append("Event code '").append(eventCode).append("' not found.");
                            default -> msg.append("Server error (").append(statusCode).append(").");
                        }
                    }
                    else
                    {
                        msg.append("Check your internet connection.");
                    }
                    Toast.makeText(context, msg.toString(), Toast.LENGTH_LONG).show();
                    resetUiState(okButton);
                })
        {
            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String> params = new HashMap<>();
                params.put("X-TBA-Auth-Key", Constants.TBA_AUTH_KEY);
                return params;
            }
        };

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
     * Saves the downloaded event match data to internal storage and updates application state.
     *
     * @param context   the application context
     * @param eventCode the TBA event code
     * @param response  the JSON array of matches received from the API
     * @throws IOException if saving to disk fails
     */
    private void saveTBAMatches(Context context, String eventCode, JSONArray response)
            throws IOException
    {
        TBAMatches tbaMatches = TBAMatches.getInstance(context, eventCode, true);
        tbaMatches.deleteTBAMatchesFile(eventCode);
        tbaMatches.writeTBAMatchesFile(eventCode, response);

        // Update current event code settings!
        Settings settings = Settings.getInstance(context);
        settings.setEventCode(eventCode);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    /**
     * Cleans up the view binding when the dialog view is destroyed.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        m_binding = null;
    }
}
