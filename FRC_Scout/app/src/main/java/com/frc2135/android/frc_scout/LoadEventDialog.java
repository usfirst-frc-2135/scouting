package com.frc2135.android.frc_scout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.frc2135.android.frc_scout.databinding.LoadEventDataDialogBinding;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Dialog for loading match data for a specific event from The Blue Alliance (TBA) API.
 */
public class LoadEventDialog extends DialogFragment
{
    private static final String TAG = "LoadEventDialog";
    private static final String TBA_AUTH_KEY = "E7akoVihRO2ZbNHtW2nRrjuNTcZaOxWtfeYWwh4XILMsKsqLnH2ZQrKAnbevlWGn";

    private LoadEventDataDialogBinding binding;

    /**
     * Creates a new instance of LoadEventDialog.
     *
     * @return a new LoadEventDialog instance
     */
    public static LoadEventDialog newInstance()
    {
        return new LoadEventDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateDialog called");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = LoadEventDataDialogBinding.inflate(inflater);

        binding.eventCodeField.setHint("Enter event code for matches");

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle("Load Event Matches")
                .setView(binding.getRoot())
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setBackgroundColor(Color.parseColor("#3F51B5"));
            okButton.setTextColor(Color.WHITE);
            okButton.setOnClickListener(v -> handleOkClick(dialog));
        });

        return dialog;
    }

    private void handleOkClick(AlertDialog dialog)
    {
        String eventCode = binding.eventCodeField.getText().toString().trim();
        if (eventCode.isEmpty() || eventCode.length() <= 4)
        {
            binding.eventCodeField.setError("Event code must be longer than 4 characters");
            return;
        }

        downloadEventData(eventCode, dialog);
    }

    private void downloadEventData(String eventCode, AlertDialog dialog)
    {
        Log.d(TAG, "Starting event data download for: " + eventCode);

        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setEnabled(false);
        okButton.setText(R.string.loading); // Fixed: used string literal to avoid missing resource error

        String urlStr = "https://www.thebluealliance.com/api/v3/event/" + eventCode + "/matches";
        Log.d(TAG, "URL: " + urlStr);

        Context context = requireContext().getApplicationContext();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlStr, null,
                response -> {
                    Log.d(TAG, "Successfully received event data");
                    try
                    {
                        saveEventData(eventCode, response, context);
                        Toast.makeText(context, "Successfully downloaded matches for " + eventCode, Toast.LENGTH_LONG).show();
                        if (isAdded())
                        {
                            dismiss();
                        }
                    }
                    catch (JSONException | IOException e)
                    {
                        Log.e(TAG, "Error saving event data: " + e.getMessage(), e);
                        Toast.makeText(context, "Error saving competition data", Toast.LENGTH_SHORT).show();
                        okButton.setEnabled(true);
                        okButton.setText(android.R.string.ok);
                    }
                },
                error -> {
                    Log.e(TAG, "Download failed: " + error.toString());
                    String msg = "Failed to download matches for '" + eventCode + "'. Check connection or event code.";
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    okButton.setEnabled(true);
                    okButton.setText(android.R.string.ok);
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

    private void saveEventData(String eventCode, org.json.JSONArray response, Context context)
            throws JSONException, IOException
    {
        CompetitionDataSerializer serializer = new CompetitionDataSerializer(context);

        // Update current competition settings
        CurrentCompetition currentComp = CurrentCompetition.get(context);
        currentComp.setEventCode(eventCode);
        if (eventCode.length() > 4)
        {
            currentComp.setCompName(eventCode.substring(4));
        }
        serializer.saveCurrentCompetition(currentComp.toJSON());

        // Cleanup existing matches file if it exists
        String eventFileName = eventCode.toLowerCase() + "matches.json";
        File dataDir = context.getFilesDir();
        File existingFile = new File(dataDir, eventFileName);
        if (existingFile.exists())
        {
            Log.d(TAG, "Deleting existing competition file: " + eventFileName);
            if (!existingFile.delete())
            {
                Log.w(TAG, "Failed to delete existing competition file");
            }
        }

        // Save new event data
        serializer.saveEventData(response);

        // Update the singleton
        CompetitionInfo.get(context, eventCode, true);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        binding = null;
    }
}
