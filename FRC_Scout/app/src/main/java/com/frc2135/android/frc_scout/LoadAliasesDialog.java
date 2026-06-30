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

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Dialog for loading team aliases data for a specific event from the team's scouting website.
 */
public class LoadAliasesDialog extends DialogFragment
{
    private static final String TAG = "LoadAliasesDialog";
    private LoadEventDialogBinding binding;

    /**
     * Creates a new instance of LoadAliasesDialog.
     *
     * @return a new LoadAliasesDialog instance
     */
    public static LoadAliasesDialog newInstance()
    {
        return new LoadAliasesDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreateDialog called");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = LoadEventDialogBinding.inflate(inflater);

        binding.eventCodeField.setHint("Enter event code for aliases");

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Load Team Aliases")
                .setView(binding.getRoot())
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
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
        if (eventCode.isEmpty() || eventCode.length() <= 4)
        {
            binding.eventCodeField.setError("Event code must be longer than 4 characters");
            return;
        }

        downloadAliases(eventCode, dialog);
    }

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

    private void saveAliases(String eventCode, org.json.JSONArray response, Context context)
            throws IOException
    {
        AliasesSerializer serializer = new AliasesSerializer(context);
        String filename = eventCode.toLowerCase() + "_aliases.json";

        File dataDir = context.getFilesDir();
        File existingFile = new File(dataDir, filename);
        if (existingFile.exists())
        {
            Log.i(TAG, "Deleting existing aliases file: " + filename);
            if (!existingFile.delete())
            {
                Log.w(TAG, "Failed to delete existing file: " + filename);
            }
        }

        serializer.saveAliasesInfo(filename, response);

        // Update the singleton if it's already loaded the wrong event code
        AliasesInfo.get(context, eventCode, true);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        binding = null;
    }
}
