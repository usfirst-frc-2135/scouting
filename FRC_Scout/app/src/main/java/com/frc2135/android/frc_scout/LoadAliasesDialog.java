package com.frc2135.android.frc_scout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;

public class LoadAliasesDialog extends DialogFragment
{
    private static final String TAG = "LoadAliasesDialog";

    private AliasesSerializer m_aliasesSerializer;
    private EditText m_eventCodeField;
    private String m_eventCode = "myEventCode";
    private Context m_appContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle SavedInstanceState)
    {
        Log.i(TAG, "onCreateDialog called");
        setCancelable(true);

        View v = requireActivity().getLayoutInflater().inflate(R.layout.load_event_data_dialog, null);
        m_eventCodeField = v.findViewById(R.id.event_code_field);
        m_eventCodeField.setHint("Enter event code");

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).setPositiveButton(android.R.string.ok, (dialog1, which) -> {
            try
            {
                sendResult();
            }
            catch (IOException e)
            {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }).create();

        dialog.setTitle("Enter event code for aliases data");

        dialog.show();
        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setBackgroundColor(Color.parseColor("#3F51B5"));

        return dialog;
    }

    public static LoadAliasesDialog newInstance()
    {
        Log.i(TAG, "newInstance() called");
        Bundle args = new Bundle();

        LoadAliasesDialog dialog = new LoadAliasesDialog();
        dialog.setArguments(args);

        return dialog;
    }

    private void sendResult()
            throws IOException
    {
        Log.i(TAG, "sendResult() called");
        Intent i = new Intent(getActivity(), MatchListActivity.class);

        // Get the list of team number/aliases for this eventCode from the team scouting website and save it 
        // on this Kindle device as a JSON file named <eventCode>_aliases.json.
        Log.i(TAG, "Load alias data clicked");
        String eventCode = m_eventCodeField.getText().toString();
        Log.i(TAG, "LoadAliasesDialog: eventCode = '" + eventCode + "'");
        if (!eventCode.isEmpty() && eventCode.length() > 4)
        {
            Log.i(TAG, "Verified event code length");

            m_appContext = getContext();
            m_eventCode = eventCode;

            if (getActivity() != null)
            {
                m_aliasesSerializer = new AliasesSerializer(getActivity());

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getActivity());

                // Looking for the event aliases data at this URL:
                String urlStr = "https://www.frc2135.org/json/" + m_eventCodeField.getText().toString().trim() + "_teamAliases.json";
                Log.i(TAG, "LoadAliasesDialog URL = " + urlStr);

                // Load the data found at the URL into a JsonArrayRequest object.
                // Going to save the aliases JSONArray data to the device.
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlStr, null,
                        response -> {
                            Log.i(TAG, "going to save the aliases JSONArray data to the device");
                            try
                            {
                                // Look through existing files on device.
                                // File will be saved to this path on the device.
                                String dataFileDir = m_appContext.getFilesDir().getPath();
                                Log.i(TAG, "Data files path = " + dataFileDir);
                                File dataDir = new File(dataFileDir);
                                File[] fileList = dataDir.listFiles();
                                if (fileList != null)
                                {
                                    String aliasFileBaseName = m_eventCodeField.getText().toString().trim() + "_aliases.json";
                                    // Remove event matches data file if it exists already.
                                    for (File f1 : fileList)
                                    {
                                        if (f1.getName().equals(aliasFileBaseName))
                                        {
                                            Log.i(TAG, "DELETING existing aliases file on device: " + f1.getName());
                                            boolean deleted = f1.delete();
                                            if (!deleted)
                                            {
                                                Log.i(TAG, "DELETING existing aliases file: failed");
                                            }
                                            break;
                                        }
                                    }

                                    // Save comp data to matches JSON file
                                    m_aliasesSerializer.saveAliasesInfo(aliasFileBaseName, response);
                                    Log.i(TAG, "SUCCESSFULLY downloaded aliases json file: " + dataFileDir + "/" + aliasFileBaseName);
                                    String tMsg = "Successfully downloaded aliases file for event: " + m_eventCode;
                                    Toast.makeText(m_appContext, tMsg, Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (IOException e)
                            {
                                Log.e(TAG, Log.getStackTraceString(e));
                                Log.i(TAG, "--> IOException: " + e);
                            }
                        },
                        error -> {
                            Log.i(TAG, "LoadAliasesDialog::sendResult() failed!");
                            Log.i(TAG, "---> error = " + error);
                            String toastMsg = "FAILED to download aliases file for event: '" + m_eventCode + "'. \n Check wifi connections or eventCode string.";
                            //jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            Toast.makeText(m_appContext, toastMsg, Toast.LENGTH_LONG).show();
                        });

                queue.add(jsonArrayRequest);

                startActivity(i);
            }
        }
        else
        {
            Log.i(TAG, "LoadAliasesDialog: no event code entered!");
        }
    }
}
