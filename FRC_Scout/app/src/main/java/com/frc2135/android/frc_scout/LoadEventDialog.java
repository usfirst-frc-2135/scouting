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

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoadEventDialog extends DialogFragment
{
    private static final String TAG = "LoadEventDialog";

    private CompetitionDataSerializer m_compSerializer;
    private EditText m_eventCodeField;
    private String m_eventCode = "myEventCode";
    private Context m_appContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle SavedInstanceState)
    {
        Log.d(TAG, "onCreateDialog called");
        setCancelable(true);

        View v = requireActivity().getLayoutInflater().inflate(R.layout.load_event_data_dialog, null);
        m_eventCodeField = v.findViewById(R.id.event_code_field);
        m_eventCodeField.setHint("Enter event code for matches list");

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).setPositiveButton(android.R.string.ok, (dialog1, which) -> {
            try
            {
                sendResult();
            }
            catch (JSONException | IOException e)
            {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }).create();

        dialog.setTitle("Enter event code for matches list");

        dialog.show();
        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setBackgroundColor(Color.parseColor("#3F51B5"));

        return dialog;
    }

    public static LoadEventDialog newInstance()
    {
        Log.i(TAG, "newInstance() called");
        Bundle args = new Bundle();

        LoadEventDialog dialog = new LoadEventDialog();
        dialog.setArguments(args);

        return dialog;
    }

    private void sendResult()
            throws JSONException, IOException
    {
        Log.i(TAG, "sendResult() called");
        Intent i = new Intent(getActivity(), MatchListActivity.class);

        // Get the eventCode's list of matches (which has the list of 6 team numbers for each match)
        // from thebluealliance.com site and save it as a JSON file named <eventCode>_matches.json.
        // The file is saved to the device.
        Log.d(TAG, "Load data clicked");
        String eventCode = m_eventCodeField.getText().toString();
        Log.d(TAG, "LoadEventDialog: eventCode = '" + eventCode + "'");
        if (!eventCode.isEmpty() && eventCode.length() > 4)
        {
            // Save the eventCode and competition name in CurrentCompetition object.
            CurrentCompetition.get(getContext()).setCompName(eventCode.substring(4));
            CurrentCompetition.get(getContext()).setEventCode(eventCode.trim());

            m_appContext = getContext();
            m_eventCode = eventCode;

            if (getActivity() != null)
            {
                m_compSerializer = new CompetitionDataSerializer(getActivity());

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getActivity());

                // Looking for the event matches data at this URL:
                String urlStr = "https://www.thebluealliance.com/api/v3/event/" + m_eventCodeField.getText().toString().trim() + "/matches";
                Log.d(TAG, "LoadEventData URL = " + urlStr);

                // Load the data found at the URL into a JsonArrayRequest object.
                // Going to save the event matches JSONArray data to the device.
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlStr, null,
                        response -> {
                            try
                            {
                                // Look through existing files on device.
                                // File will be saved to this path on the device.
                                String dataFileDir = m_appContext.getFilesDir().getPath();
                                Log.d(TAG, "Data files path = " + dataFileDir);
                                File file = new File(dataFileDir);
                                File[] fileList = file.listFiles();
                                if (fileList != null)
                                {
                                    String eventFileName = m_eventCodeField.getText().toString().trim() + "matches.json";
                                    // Remove event matches data file if it exists already.
                                    for (File f1 : fileList)
                                    {
                                        if (f1.getName().equals(eventFileName))
                                        {
                                            Log.d(TAG, "DELETING existing competition file on device: " + f1.getName());
                                            boolean deleted = f1.delete();
                                            if (!deleted)
                                            {
                                                Log.d(TAG, "DELETING existing competition file: failed");
                                            }
                                            break;
                                        }
                                    }

                                    // Save comp data to matches JSON file
                                    m_compSerializer.saveEventData(response);
                                    String tMsg = " Successfully downloaded matches list for event: " + m_eventCode + " to device";
                                    Toast.makeText(m_appContext, tMsg, Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "SUCCESSFULLY downloaded matches.json file: " + dataFileDir + "/" + eventFileName);
                                    // Set up the CompetitionInfo with this event data.
                                    CompetitionInfo.get(m_appContext, m_eventCode, true);
                                }
                            }
                            catch (JSONException | IOException e)
                            {
                                Log.e(TAG, Log.getStackTraceString(e));
                            }
                        },
                        error -> {
                            Log.d(TAG, "LoadEventData::sendResult() failed!");
                            String toastMsg = " FAILED to download competition match data for event: '" + m_eventCode + "'. \n Check wifi connections or eventCode string.";
                            Toast.makeText(m_appContext, toastMsg, Toast.LENGTH_LONG).show();
                        })
                {
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        // These params are used to access the URL data (I think).
                        Map<String, String> params = new HashMap<>();
                        params.put("X-TBA-Auth-Key", "E7akoVihRO2ZbNHtW2nRrjuNTcZaOxWtfeYWwh4XILMsKsqLnH2ZQrKAnbevlWGn");
                        return params;
                    }
                };

                queue.add(jsonArrayRequest);

                // Write out the current_competition.json file.
                m_compSerializer.saveCurrentCompetition(CurrentCompetition.get(getContext()).toJSON());
                startActivity(i);
            }
        }
        else
        {
            Log.d(TAG, "LoadEventDialog: no event code entered!");
        }
    }
}
