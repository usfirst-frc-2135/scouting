package com.frc2135.android.frc_scout;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.frc2135.android.frc_scout.databinding.LoadEventDialogBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Dialog for loading match data for a specific event from The Blue Alliance (TBA) API.
 * This dialog handles fetching, saving, and clearing event-specific match information.
 */
public class LoadTBAMatchesDialog extends DialogFragment
{
    private static final String TAG = "LoadTBAMatchesDialog";
    private LoadEventDialogBinding m_binding;

    /**
     * Creates a new instance of {@link LoadTBAMatchesDialog}.
     *
     * @return a new LoadTBAMatchesDialog instance
     */
    public static LoadTBAMatchesDialog newInstance()
    {
        return new LoadTBAMatchesDialog();
    }

    /**
     * Constructs the {@link androidx.appcompat.app.AlertDialog} instance, initializes View Binding, and sets up
     * the event code input field with validation listeners.
     *
     * @param savedInstanceState if the dialog is being re-initialized from a previous saved state
     * @return the constructed {@link Dialog} instance
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreateDialog called");

        LayoutInflater inflater = getLayoutInflater();
        m_binding = LoadEventDialogBinding.inflate(inflater);

        // Pre-fill with current event code if available
        Settings settings = Settings.getInstance(requireContext());
        if (settings != null)
        {
            String currentEventCode = settings.getEventCode();
            if (!currentEventCode.isEmpty() && !Objects.equals(currentEventCode, "EVTX"))
            {
                m_binding.loadEventCodeInput.setText(currentEventCode);
            }
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.load_tba_matches_title)
                .setView(m_binding.getRoot())
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton(R.string.clear_tba_matches, (d, w) -> {
                    Log.i(TAG, "Clear TBA Matches called");
                    clearTBAMatches();
                    m_binding.loadEventCodeInput.setText("");
                    m_binding.loadEventCodeLayout.setError(null);
                })
                .create();

        m_binding.loadEventCodeInput.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                m_binding.loadEventCodeLayout.setError(null);
                String eventCode = s.toString().trim().toLowerCase(Locale.US);
                if (Settings.getInstance(requireContext()).isValidEventCode(eventCode))
                {
                    MaterialButton okButton = (MaterialButton) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    if (okButton != null)
                    {
                        okButton.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        dialog.setOnShowListener(d -> {
            MaterialButton okButton = (MaterialButton) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(v -> handleOkClick(dialog));
        });

        m_binding.loadEventCodeInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
            {
                MaterialButton okButton = (MaterialButton) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (okButton != null)
                {
                    okButton.setFocusableInTouchMode(true);
                    okButton.requestFocus();
                }
                return true;
            }
            return false;
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
        String eventCode = Objects.requireNonNull(m_binding.loadEventCodeInput.getText()).toString().trim().toLowerCase(Locale.US);

        Settings settings = Settings.getInstance(requireContext());
        if (!settings.isValidEventCode(eventCode))
        {
            m_binding.loadEventCodeLayout.setError("Invalid event code (e.g., 2026casac)");
            return;
        }

        m_binding.loadEventCodeLayout.setError(null);
        downloadTBAMatches(dialog, eventCode);
    }

    /**
     * Resets the UI components to their interactive state after a download attempt.
     *
     * @param okButton the dialog's OK button
     */
    private void resetUiState(MaterialButton okButton)
    {
        if (okButton != null)
        {
            okButton.setEnabled(true);
        }
        if (m_binding != null)
        {
            m_binding.loadEventCodeInput.setEnabled(true);
            m_binding.loadEventProgressIndicator.setVisibility(View.GONE);
        }
    }

    /**
     * Downloads event match data from The Blue Alliance API for the specified event code.
     * Updates the UI state to show loading during the request.
     * On success, saves the data locally and dismisses the dialog.
     *
     * @param dialog    the active {@link AlertDialog} instance to update or dismiss
     * @param eventCode the TBA event code (e.g., "2026casac")
     */
    private void downloadTBAMatches(AlertDialog dialog, String eventCode)
    {
        Log.d(TAG, "Starting TBA matches download for: " + eventCode);

        // Disable button to prevent multiple requests
        MaterialButton okButton = (MaterialButton) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setEnabled(false);
        m_binding.loadEventCodeInput.setEnabled(false);
        m_binding.loadEventProgressIndicator.setVisibility(View.VISIBLE);

        String urlStr = Constants.TBA_EVENT_MATCHES_URL + eventCode + "/matches";
        Log.i(TAG, "TBA Matches URL: " + urlStr);

        Context context = requireContext().getApplicationContext();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlStr, null,
                response -> {
                    Log.i(TAG, "Successfully received TBA match file for: " + eventCode);
                    if (response.length() == 0)
                    {
                        displayToastMessages(requireContext(), TAG, "No TBA matches in file for " + eventCode, false, null);
                        resetUiState(okButton);
                        return;
                    }

                    if (saveTBAMatches(context, eventCode, response))
                    {
                        displayToastMessages(requireContext(), TAG, "Successfully downloaded " + response.length() + " TBA matches for " + eventCode, false, null);
                        if (isAdded())
                        {
                            dismiss();
                        }
                    }
                    else
                    {
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
                    displayToastMessages(requireContext(), TAG, msg.toString(), false, null);
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
     * Saves the downloaded event match data to internal storage and updates application state.
     *
     * @param context   the application context
     * @param eventCode the TBA event code
     * @param response  the JSON array of matches received from the API
     * @return true if successful, false otherwise
     */
    private boolean saveTBAMatches(Context context, String eventCode, JSONArray response)
    {
        TBAMatches tbaMatches = TBAMatches.getInstance(context, eventCode, true);
        if (tbaMatches.writeTBAMatchesFile(eventCode, response, true))
        {
            // Update current event code settings!
            Settings.getInstance(context).setEventCode(eventCode);
            return true;
        }
        return false;
    }

    /**
     * Clears official match data for the event code currently entered in the input field.
     */
    private void clearTBAMatches()
    {
        String eventCode = Objects.requireNonNull(m_binding.loadEventCodeInput.getText()).toString().trim();
        if (!eventCode.isEmpty())
        {
            TBAMatches tbaMatches = TBAMatches.getInstance(requireContext(), eventCode, false);
            if (tbaMatches.deleteTBAMatchesFile(eventCode) > 0)
            {
                displayToastMessages(requireContext(), TAG, "Cleared TBA Matches for " + eventCode, false, null);
            }
        }
    }

    /**
     * Logs and optionally displays an informative or error message via Toast.
     *
     * @param context the context in which to display the message
     * @param tag     the log tag
     * @param msg     the message text
     * @param bSilent if true, the Toast is suppressed
     * @param e       the exception associated with the error, if any
     */
    @SuppressWarnings("SameParameterValue")
    protected void displayToastMessages(Context context, String tag, String msg, boolean bSilent, Exception e)
    {
        int length;
        if (e == null)
        {
            length = Toast.LENGTH_SHORT;
            Log.i(tag, msg);
        }
        else
        {
            length = Toast.LENGTH_LONG;
            Log.e(tag, msg, e);
        }

        if (!bSilent && context != null)
        {
            Toast.makeText(context, msg, length).show();
        }
    }

    /**
     * Called when the fragment is visible to the user and actively running.
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
