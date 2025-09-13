package com.frc2135.android.frc_scout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** @noinspection ALL*/
public class LoadAliasesDialog extends DialogFragment
{
    private static final String TAG = "LoadAliasesDialog";

    private AliasesSerializer m_aliasesSerializer;
    private EditText m_eventCodeField;
    private String m_eventCode = "myEventCode";
    private Context m_appContext;

    @NonNull
    public Dialog onCreateDialog(Bundle SavedInstanceState)
    {
        setCancelable(true);

        Log.i(TAG, "onCreateDialog called");

        View v = requireActivity().getLayoutInflater().inflate(R.layout.load_event_data_dialog, null);
        m_eventCodeField = v.findViewById(R.id.event_code_field);
        m_eventCodeField.setHint("Enter event code");

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                try
                {
                    sendResult();
                } catch (JSONException | IOException e)
                {
                    e.printStackTrace();
                }
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

    private void sendResult() throws JSONException, IOException
    {
        Log.i(TAG, "sendResult() called");
        Intent i = new Intent(getActivity(), MatchListActivity.class);

        // Get the list of team number/aliases for this eventCode from the team scouting website and save it 
        // on this kindle device as a JSON file named <eventCode>_aliases.json.
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
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlStr, null, new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        // Going to save the aliases JSONArray data to the device.
                        Log.i(TAG, "going to save the aliases JSONArray data to the device");
                        try
                        {
                            // Look thru existing files on device.
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
                                            Log.i(TAG, "DELETING existing aliases file: failed");
                                        break;
                                    }
                                }

                                // Save comp data to matches JSON file 
                                m_aliasesSerializer.saveAliasesData(aliasFileBaseName,response);
                                Log.i(TAG, "SUCCESSFULLY downloaded aliases json file: " + dataFileDir + "/" + aliasFileBaseName);
                                String tMsg = "Successfully downloaded aliases file for event: " + m_eventCode;
                                Toast toast1 = Toast.makeText(m_appContext, tMsg, Toast.LENGTH_LONG);
//REMOVE                                toast1.setGravity(Gravity.CENTER, 0, 0);
                                toast1.show();
                            }
                        } 
                        catch (JSONException | IOException e)
                        {
                            e.printStackTrace();
                            Log.i(TAG, "--> IOException: "+e);
                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        // Failed to load the data from the URL.
                        Log.i(TAG, "LoadAliasesDialog::sendResult() failed!");
                        Log.i(TAG,"---> error = "+error);
                        String toastMsg = "FAILED to download aliases file for event: '" + m_eventCode + "'. \n Check wifi connections or eventCode string.";
                        Toast toast2 = Toast.makeText(m_appContext, toastMsg, Toast.LENGTH_LONG);
//REMOVE                        View view2 = toast2.getView();
//REMOVE                        view2.setBackgroundColor(Color.RED);
//REMOVE                        toast2.setGravity(Gravity.CENTER, 0, 0);
                        toast2.show();
                    }
                })
                {
                    /*@Override
                    public Map<String, String> getHeaders()
                    {
                        // These params are used to access the URL data (I think).
                        Map<String, String> params = new HashMap<>();
                        //noinspection SpellCheckingInspection
                        params.put("X-TBA-Auth-Key", "E7akoVihRO2ZbNHtW2nRrjuNTcZaOxWtfeYWwh4XILMsKsqLnH2ZQrKAnbevlWGn");
                        return params;
                    } */
                };

                //jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsonArrayRequest);

                startActivity(i);
            }
        }
        else
            Log.i(TAG, "LoadAliasesDialog: no event code entered!");
    }
}

