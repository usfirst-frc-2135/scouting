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

    /**
     * The base URL for The Blue Alliance API v3 event matches endpoint.
     */
    public static final String TBA_EVENT_MATCHES_URL = "https://www.thebluealliance.com/api/v3/event/";

    /**
     * The authentication key for The Blue Alliance API.
     */
    private static final String TBA_AUTH_KEY = "MetfyxQxRpk0do2GygII8alQnV0qaQ8kF9KUIYDrFTMmQr2pPC8Cl4FGdoKlUaAu";

    private LoadEventDialogBinding m_binding;

    /**
     * Creates a new instance of LoadEventDialog.
     *
     * @return a new LoadEventDialog instance
     */
    public static LoadTBAMatchesDialog newInstance()
    {
        return new LoadTBAMatchesDialog();
    }

    /**
     * Initializes the dialog, sets up the layout, and configures button listeners.
     *
     * @param savedInstanceState if the dialog is being re-initialized after previously being shut down
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
            String currentCode = settings.getEventCode();
            if (!Objects.equals(currentCode, "EVTX") && !currentCode.isEmpty())
            {
                m_binding.eventCodeField.setText(currentCode);
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
                .setTitle("Load TBA Matches")
                .setView(m_binding.getRoot())
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton("Clear", (d, w) -> {
                    String eventCode = Objects.requireNonNull(m_binding.eventCodeField.getText()).toString().trim();
                    if (!eventCode.isEmpty())
                    {
                        TBAMatches tbaMatches = TBAMatches.getInstance(requireContext(), eventCode, false);
                        if (tbaMatches.deleteTBAMatches(eventCode) > 0)
                        {
                            Toast.makeText(requireContext(), "Cleared data for " + eventCode, Toast.LENGTH_SHORT).show();
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
     * Handles the OK button click, validates the event code, and initiates the data download.
     *
     * @param dialog the current {@link AlertDialog} instance
     */
    private void handleOkClick(AlertDialog dialog)
    {
        Log.d(TAG, "handleOkClick called");
        String eventCode = Objects.requireNonNull(m_binding.eventCodeField.getText()).toString().trim().toLowerCase(Locale.US);

        if (eventCode.isEmpty() || eventCode.length() < 7)
        {
            m_binding.eventCodeLayout.setError("Event code must be at least 7 characters (e.g., 2026casac)");
            return;
        }

        if (!eventCode.matches("\\d{4}[a-z0-9]+"))
        {
            m_binding.eventCodeLayout.setError("Invalid event code format (e.g., 2026casac)");
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
        Log.d(TAG, "downloadTBAMatches for: " + eventCode);

        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setEnabled(false);
        okButton.setText(R.string.loading);
        m_binding.eventCodeField.setEnabled(false);

        String urlStr = TBA_EVENT_MATCHES_URL + eventCode + "/matches";
        Log.d(TAG, "URL: " + urlStr);

        Context context = requireContext().getApplicationContext();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlStr, null,
                response -> {
                    Log.d(TAG, "Successfully received event data for: " + eventCode);
                    if (response.length() == 0)
                    {
                        Log.w(TAG, "Received empty match list for: " + eventCode);
                        Toast.makeText(context, "No matches found for " + eventCode, Toast.LENGTH_LONG).show();
                        resetUiState(okButton);
                        return;
                    }

                    try
                    {
                        saveTBAMatches(context, eventCode, response);
                        Toast.makeText(context, "Successfully downloaded " + response.length() + " matches for " + eventCode, Toast.LENGTH_LONG).show();
                        if (isAdded())
                        {
                            dismiss();
                        }
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG, "Error saving event data: " + e.getMessage(), e);
                        Toast.makeText(context, "Error saving event matches locally", Toast.LENGTH_SHORT).show();
                        resetUiState(okButton);
                    }
                },
                error -> {
                    Log.e(TAG, "Download failed: " + error.toString());
                    StringBuilder msg = new StringBuilder("Failed to download matches. ");
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
                params.put("X-TBA-Auth-Key", TBA_AUTH_KEY);
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
        tbaMatches.deleteTBAMatches(eventCode);
        tbaMatches.saveTBAMatches(eventCode, response);

        // Update current event code settings
        Settings settings = Settings.getInstance(context);
        settings.setEventCode(eventCode);
        ScoutedMatches.getInstance(context).saveScoutNames();

        // The singleton was updated by TBAMatches.getInstance() call above with bForceReload=true
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
