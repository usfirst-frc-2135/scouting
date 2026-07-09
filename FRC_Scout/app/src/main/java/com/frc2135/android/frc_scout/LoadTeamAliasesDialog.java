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
                .setTitle(R.string.load_team_aliases_title)
                .setView(m_binding.getRoot())
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton(R.string.clear_team_aliases, (d, w) -> {
                    Log.d(TAG, "Clear Team Aliases called");
                    String eventCode = Objects.requireNonNull(m_binding.eventCodeField.getText()).toString().trim();
                    if (!eventCode.isEmpty())
                    {
                        TeamAliases teamAliases = TeamAliases.getInstance(requireContext(), eventCode, false);
                        if (teamAliases.deleteTeamAliasesFile(eventCode) > 0)
                        {
                            Toast.makeText(requireContext(), "Cleared Team Aliases for " + eventCode, Toast.LENGTH_SHORT).show();
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

        m_binding.eventCodeField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE)
            {
                handleOkClick(dialog);
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
        String eventCode = Objects.requireNonNull(m_binding.eventCodeField.getText()).toString().trim().toLowerCase(Locale.US);

        Settings settings = Settings.getInstance(requireContext());
        if (!settings.isValidEventCode(eventCode))
        {
            m_binding.eventCodeField.setError("Invalid event code (e.g., 2026casac)");
            return;
        }

        m_binding.eventCodeLayout.setError(null);
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
        m_binding.eventCodeField.setEnabled(false);

        String urlStr = Constants.TEAM_WEBSITE_JSON_URL + eventCode + Constants.TEAM_ALIASES_FILENAME_SUFFIX;
        Log.i(TAG, "URL: " + urlStr);

        Context context = requireContext().getApplicationContext();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlStr, null,
                response -> {
                    Log.i(TAG, "Successfully received team aliases data for:" + eventCode);
                    if (response.length() == 0)
                    {
                        Log.w(TAG, "Received empty team aliases for: " + eventCode);
                        Toast.makeText(context, "No team team aliases found for " + eventCode, Toast.LENGTH_LONG).show();
                        resetUiState(okButton);
                        return;
                    }

                    if (saveTeamAliases(context, eventCode, response))
                    {
                        Toast.makeText(context, "Successfully downloaded team aliases for " + eventCode, Toast.LENGTH_LONG).show();
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
                    Log.e(TAG, "Download team aliases failed: " + error.toString());
                    String msg = "Failed to download team aliases for '" + eventCode + "'. Check connection or event code.";
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
