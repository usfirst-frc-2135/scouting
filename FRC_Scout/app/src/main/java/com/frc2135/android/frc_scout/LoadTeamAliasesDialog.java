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
import java.util.Objects;

/**
 * Dialog for loading team aliases data for a specific event from the team's scouting website.
 */
public class LoadTeamAliasesDialog extends DialogFragment
{
    private static final String TAG = "LoadTeamAliasesDialog";
    private LoadEventDialogBinding m_binding;

    /**
     * Creates a new instance of LoadAliasesDialog.
     *
     * @return a new LoadAliasesDialog instance
     */
    public static LoadTeamAliasesDialog newInstance()
    {
        return new LoadTeamAliasesDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateDialog called");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        m_binding = LoadEventDialogBinding.inflate(inflater);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Load Team Aliases")
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
        String eventCode = Objects.requireNonNull(m_binding.eventCodeField.getText()).toString().trim();
        if (eventCode.isEmpty() || eventCode.length() < 7)
        {
            m_binding.eventCodeField.setError("Event code must be at least 7 characters (e.g., 2026casac)");
            return;
        }

        downloadAliases(eventCode, dialog);
    }

    /**
     * Downloads team alias mapping data from the team website for the specified event code.
     * Updates the UI state to show loading during the request.
     * On success, saves the data locally and dismisses the dialog.
     *
     * @param eventCode the FRC event code (e.g., "2026casac")
     * @param dialog    the dialog instance to update or dismiss upon completion
     */
    private void downloadAliases(String eventCode, AlertDialog dialog)
    {
        Log.i(TAG, "Starting aliases download for: " + eventCode);

        // Disable button to prevent multiple requests
        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setEnabled(false);
        okButton.setText(R.string.loading);

        String urlStr = "https://www.frc2135.org/json/" + eventCode + "_teamAliases.json";
        Log.i(TAG, "URL: " + urlStr);

        Context context = requireContext().getApplicationContext();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlStr, null,
                response -> {
                    Log.i(TAG, "Successfully received aliases data");
                    try
                    {
                        saveAliases(eventCode, response, context);
                        Toast.makeText(context, "Successfully downloaded aliases for " + eventCode, Toast.LENGTH_LONG).show();
                        if (isAdded())
                        {
                            dismiss();
                        }
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG, "Error saving aliases: " + e.getMessage());
                        Toast.makeText(context, "Error saving aliases data", Toast.LENGTH_SHORT).show();
                        okButton.setEnabled(true);
                        okButton.setText(android.R.string.ok);
                    }
                },
                error -> {
                    Log.e(TAG, "Download failed: " + error.toString());
                    String msg = "Failed to download aliases for '" + eventCode + "'. Check connection or event code.";
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    okButton.setEnabled(true);
                    okButton.setText(android.R.string.ok);
                });

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    /**
     * Saves the downloaded team alias mappings to internal storage and updates application state.
     *
     * @param eventCode the FRC event code
     * @param response  the JSON array of alias mappings received from the API
     * @param context   the application context
     * @throws IOException if saving to disk fails
     */
    private void saveAliases(String eventCode, org.json.JSONArray response, Context context)
            throws IOException
    {
        TeamAliases teamAliases = TeamAliases.getInstance(context, eventCode, true);
        teamAliases.deleteTeamAliases(eventCode);
        teamAliases.saveTeamAliases(eventCode, response);
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
