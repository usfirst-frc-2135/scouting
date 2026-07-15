package com.frc2135.android.frc_scout;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
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

import java.util.Locale;
import java.util.Objects;

/**
 * Dialog for loading team aliases data for a specific event from the team's scouting website.
 * This dialog handles fetching, clearing, and saving team-specific alias mapping data.
 */
public class LoadTeamAliasesDialog extends DialogFragment
{
    private static final String TAG = "LoadTeamAliasesDialog";

    private LoadEventDialogBinding m_binding;

    /**
     * Creates a new instance of {@link LoadTeamAliasesDialog}.
     *
     * @return a new LoadTeamAliasesDialog instance
     */
    public static LoadTeamAliasesDialog newInstance()
    {
        return new LoadTeamAliasesDialog();
    }

    /**
     * Constructs the {@link AlertDialog} instance, initializes View Binding, and sets up
     * the event code input field and listeners for loading or clearing team aliases.
     *
     * @param savedInstanceState if the dialog is being re-initialized from a previous saved state
     * @return the constructed {@link Dialog}
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateDialog called");

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
                .setTitle(R.string.load_team_aliases_title)
                .setView(m_binding.getRoot())
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton(R.string.clear_team_aliases, (d, w) -> {
                    Log.i(TAG, "Clear Team Aliases called");
                    String eventCode = Objects.requireNonNull(m_binding.loadEventCodeInput.getText()).toString().trim();
                    if (!eventCode.isEmpty())
                    {
                        TeamAliases teamAliases = TeamAliases.getInstance(requireContext(), eventCode, false);
                        if (teamAliases.deleteTeamAliasesFile(eventCode) > 0)
                        {
                            displayToastMessages(requireContext(), TAG, "Cleared Team Aliases for " + eventCode, false, null);
                        }
                    }
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
                    Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
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
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(v -> handleOkClick(dialog));
        });

        m_binding.loadEventCodeInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
            {
                Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
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
     * and initiates the team aliases data download if the code is valid.
     *
     * @param dialog the active {@link AlertDialog} instance
     */
    private void handleOkClick(AlertDialog dialog)
    {
        String eventCode = Objects.requireNonNull(m_binding.loadEventCodeInput.getText()).toString().trim().toLowerCase(Locale.US);

        Settings settings = Settings.getInstance(requireContext());
        if (!settings.isValidEventCode(eventCode))
        {
            m_binding.loadEventCodeLayout.setError("Invalid event code (e.g., 2026casac)");
            return;
        }

        m_binding.loadEventCodeLayout.setError(null);
        downloadTeamAliases(dialog, eventCode);
    }

    /**
     * Downloads team alias mapping data from the team website for the specified event code.
     * Updates the UI state to show loading during the request.
     * On success, saves the data locally and dismisses the dialog.
     *
     * @param dialog    the dialog instance to update or dismiss upon completion
     * @param eventCode the FRC event code (e.g., "2026casac")
     */
    private void downloadTeamAliases(AlertDialog dialog, String eventCode)
    {
        Log.i(TAG, "Starting aliases download for: " + eventCode);

        // Disable button to prevent multiple requests
        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setEnabled(false);
        okButton.setText(R.string.loading);
        m_binding.loadEventCodeInput.setEnabled(false);

        String urlStr = Constants.TEAM_WEBSITE_JSON_URL + eventCode + Constants.TEAM_ALIASES_FILENAME_SUFFIX;
        Log.i(TAG, "URL: " + urlStr);

        Context context = requireContext().getApplicationContext();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlStr, null,
                response -> {
                    Log.i(TAG, "Successfully received team aliases file for:" + eventCode);
                    if (response.length() == 0)
                    {
                        displayToastMessages(requireContext(), TAG, "No team team aliases in file for " + eventCode, false, null);
                        resetUiState(okButton);
                        return;
                    }

                    if (saveTeamAliases(context, eventCode, response))
                    {
                        displayToastMessages(requireContext(), TAG, "Successfully downloaded " + response.length() + " team aliases for " + eventCode, false, null);
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
                    displayToastMessages(requireContext(), TAG, "Failed to download team aliases for '" + eventCode + "'. Check connection or event code.", false, null);
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
            m_binding.loadEventCodeInput.setEnabled(true);
        }
    }

    /**
     * Saves the downloaded team alias mappings to internal storage.
     *
     * @param context   the application context
     * @param eventCode the FRC event code
     * @param response  the JSON array of alias mappings received from the API
     * @return true if successful, false otherwise
     */
    private boolean saveTeamAliases(Context context, String eventCode, org.json.JSONArray response)
    {
        TeamAliases teamAliases = TeamAliases.getInstance(context, eventCode, true);
        teamAliases.deleteTeamAliasesFile(eventCode);
        return teamAliases.writeTeamAliasesFile(eventCode, response, false);
    }

    /**
     * Log and optionally display an error message for an exception.
     *
     * @param context the context to show the Toast in
     * @param tag     the log tag
     * @param msg     the error message
     * @param bSilent if true, the Toast is suppressed
     * @param e       the exception that occurred
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
