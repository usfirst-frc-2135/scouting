package com.frc2135.android.frc_scout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
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
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class LoadEventDialog extends DialogFragment {
    private static final String TAG = "LoadEventDialog";
    private static final String DEVICE_DATA_PATH = "/data/user/0/com.frc2135.android.frc_scout/files";

    private CompetitionDataSerializer m_compSerializer;
    private EditText m_eventCodeField;
    private String m_eventCode = "myEventCode"; 
    private Context m_appContext; 


    public Dialog onCreateDialog(Bundle SavedInstanceState){
        setCancelable(true);

        Log.i(TAG, "onCreateDialog called");

        View v = getActivity().getLayoutInflater().inflate(R.layout.load_event_data_dialog,null);
        m_eventCodeField = v.findViewById(R.id.event_code_field);
        m_eventCodeField.setHint("Enter event code");

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                try {
                    sendResult(Activity.RESULT_OK);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).create();

        dialog.setTitle("Load Competition Data");

        dialog.show();
        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setBackgroundColor(Color.parseColor("#3F51B5"));

        return dialog;
    }

    public static LoadEventDialog newInstance(){
        Log.i(TAG, "newInstance() called");
        Bundle args = new Bundle();

        LoadEventDialog dialog = new LoadEventDialog();
        dialog.setArguments(args);

        return dialog;
    }

    private void sendResult(int resultCode) throws JSONException, IOException {
        Log.i(TAG, "sendResult() called");
        Intent i = new Intent(getActivity(), MatchListActivity.class);

        Log.d(TAG, "Load data clicked");
        String eventCode = m_eventCodeField.getText().toString();
        Log.d(TAG,"LoadEventDialog: eventCode = '"+eventCode+"'");
        if(!eventCode.isEmpty() && eventCode.length() > 4) {
            CurrentCompetition.get(getContext()).setCompName(eventCode.substring(4).toUpperCase());
            CurrentCompetition.get(getContext()).setEventCode(eventCode.trim());
            m_appContext = getContext();
            m_eventCode = eventCode;
            if(getActivity() != null) {
                m_compSerializer = new CompetitionDataSerializer(getActivity(), m_eventCodeField.getText().toString().trim() + "matches.json");

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                String urlStr = "https://www.thebluealliance.com/api/v3/event/" + m_eventCodeField.getText().toString().trim() + "/matches";
                Log.d(TAG, "LoadEventData URL = " + urlStr);
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlStr, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Look thru existing files on device.
                            String dataFileDir = DEVICE_DATA_PATH;
                            File file = new File(dataFileDir);
                            File[] filelist = file.listFiles();
                            if (filelist != null) {
                                String eventFileName = m_eventCodeField.getText().toString().trim() + "matches.json";
                                // Remove comp data file if it exists already.
                                for (File f1 : filelist) {
                                    if (f1.getName().equals(eventFileName)) {
                                        Log.d(TAG,"Deleting existing competition file on device: "+f1.getName());
                                        f1.delete();
                                        break;
                                    }
                                }
                                // Save comp data to file 
                                m_compSerializer.saveEventData(response);
                                Log.d(TAG,"Successfully saved data to file: "+dataFileDir +"/"+eventFileName);
                                Toast toast1 = Toast.makeText(m_appContext, "Successfully loaded competition match data for event: "+m_eventCode, Toast.LENGTH_LONG);
                                toast1.setGravity(Gravity.CENTER,0,0);
                                toast1.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "LoadEventData::sendResult() failed!");
                        String toastMsg = " Failed to load competition match data for event: '"+m_eventCode+"'. \n Check wifi connections or eventCode string.";
                        Toast toast2 = Toast.makeText(m_appContext, toastMsg, Toast.LENGTH_LONG);
                        View view2 = toast2.getView();
                        view2.setBackgroundColor(Color.RED);
                        toast2.setGravity(Gravity.CENTER,0,0);
                        toast2.show();
                    }
                }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("X-TBA-Auth-Key", "E7akoVihRO2ZbNHtW2nRrjuNTcZaOxWtfeYWwh4XILMsKsqLnH2ZQrKAnbevlWGn");
                        return params;
                    }
                };

                queue.add(jsonArrayRequest);

                m_compSerializer.saveCurrentCompetition(CurrentCompetition.get(getContext()).toJSON());

                startActivity(i);
            }
        }
        else Log.d(TAG, "LoadEventDialog: no event code entered!");
    }
}

