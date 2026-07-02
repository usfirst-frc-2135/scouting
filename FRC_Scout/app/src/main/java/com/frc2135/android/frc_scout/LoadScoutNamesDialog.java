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

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;

/**
 * Dialog for loading scout names for a specific event from the team's scouting website.
 */
public class LoadScoutNamesDialog extends DialogFragment
{
    private static final String TAG = "LoadScoutsDialog";
    private LoadEventDialogBinding binding;

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
        Log.i(TAG, "onCreateDialog called");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = LoadEventDialogBinding.inflate(inflater);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Load Scout Names")
                .setView(binding.getRoot())
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton("Clear", (d, w) -> {
                    binding.eventCodeField.setText("");
                    binding.eventCodeField.setError(null);
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
        String eventCode = Objects.requireNonNull(binding.eventCodeField.getText()).toString().trim();
        if (eventCode.isEmpty() || eventCode.length() < 7)
        {
            binding.eventCodeField.setError("Event code must be at least 7 characters (e.g., 2026casac)");
            return;
        }

        downloadScouts(eventCode, dialog);
    }

    private void downloadScouts(String eventCode, AlertDialog dialog)
    {
        Log.i(TAG, "Starting scouts download for: " + eventCode);

        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setEnabled(false);
        okButton.setText(R.string.loading);

        String urlStr = "https://www.frc2135.org/json/" + eventCode + "_scoutNames.json";
        Log.i(TAG, "URL: " + urlStr);

        Context context = requireContext().getApplicationContext();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlStr, null,
                response -> {
                    Log.i(TAG, "Successfully received scouts data");
                    try
                    {
                        processScouts(response, context);
                        Toast.makeText(context, "Successfully downloaded scouts for " + eventCode, Toast.LENGTH_LONG).show();
                        if (isAdded())
                        {
                            dismiss();
                        }
                    }
                    catch (JSONException | IOException e)
                    {
                        Log.e(TAG, "Error processing scouts: " + e.getMessage());
                        Toast.makeText(context, "Error saving scout names", Toast.LENGTH_SHORT).show();
                        okButton.setEnabled(true);
                        okButton.setText(android.R.string.ok);
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

    private void processScouts(JSONArray response, Context context)
            throws JSONException, IOException
    {
        Settings settings = Settings.get(context);
        for (int i = 0; i < response.length(); i++)
        {
            String name = response.getString(i);
            settings.addPastScoutNames(name);
        }
        MatchListData.get(context).saveScoutNames();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}
