package com.frc2135.android.frc_scout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

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

public class SetTeamIndexDlg extends DialogFragment {
    private static final String TAG = "SetTeamIndexDlg";

    private Scouter   m_Scouter;
    private EditText  m_teamIndexField;
    private TextView  m_teamIndexErrMsg;
    private Context   m_appContext; 

    public Dialog onCreateDialog(Bundle SavedInstanceState){
        setCancelable(true);
        Log.d(TAG, "onCreateDialog called");
        m_appContext = getContext();

        View v = getActivity().getLayoutInflater().inflate(R.layout.set_team_index_dlg,null);
        m_teamIndexField = v.findViewById(R.id.set_team_index_field);
        m_teamIndexField.setHint("Enter team index number (1-6 or 'None')");
        m_teamIndexErrMsg = v.findViewById(R.id.team_index_err);
        m_teamIndexErrMsg.setVisibility(View.INVISIBLE);
        m_teamIndexErrMsg.setTextColor(Color.RED);

        // Get the existing index from Scouter, if any.
        m_Scouter = Scouter.get(getContext());
        if(m_Scouter != null) {
            String indexStr = m_Scouter.getTeamIndexStr();
            Log.d(TAG,"From Scouter: teamFieldIndex = "+indexStr);
            if(m_Scouter.isValidTeamIndexStr(indexStr))
                m_teamIndexField.setText(m_Scouter.getTeamIndexStr());
            else m_teamIndexField.setText("None");
        }
        else m_teamIndexField.setText("None");

        m_teamIndexField.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence c, int start, int before, int count){
            String indexStr = m_teamIndexField.getText().toString().trim();
 //HOLD           if(!m_Scouter.isValidTeamIndexStr(indexStr))  {
 //HOLD               m_teamIndexField.setText("None");
 //HOLD               Log.d(TAG,"teamFieldIndex value "+indexStr+" is not valid, so set to None!");
 //HOLD           }
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after){
            }
            public void afterTextChanged(Editable c){
                // Validate the teamIndex entry.
                m_teamIndexErrMsg.setVisibility(View.INVISIBLE); // reset invalid teamIndex msg
                if(m_Scouter != null){
                    if(!m_Scouter.isValidTeamIndexStr(m_teamIndexField.getText().toString().trim())) {
                        m_teamIndexErrMsg.setVisibility(View.VISIBLE); // show invalid teamIndex msg
                        m_teamIndexField.setTextColor(Color.RED);
                    }
                    else m_teamIndexField.setTextColor(Color.BLACK);
                }
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
               if(checkValidData()){
                   if(m_Scouter != null) {
                        m_Scouter.setTeamIndexStr(m_teamIndexField.getText().toString());
                        Log.d(TAG,"===> setting team index to "+m_teamIndexField.getText().toString());
                    }
                    try {
                        sendResult(Activity.RESULT_OK);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).create();

        dialog.setTitle("Set Team Index");
        dialog.show();
        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setBackgroundColor(Color.parseColor("#3F51B5"));
//HOLD        Button button2 = dialog.getButton(AlertDialog.BUTTON_CANCEL);
//HOLD        button2.setBackgroundColor(Color.parseColor("#3F51B5"));
        return dialog;
    }

    public static SetTeamIndexDlg newInstance(){
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();
        SetTeamIndexDlg dialog = new SetTeamIndexDlg();
        dialog.setArguments(args);
        return dialog;
    }

    private boolean checkValidData(){
        m_teamIndexErrMsg.setVisibility(View.INVISIBLE);

        // Validate team index
        if(!m_Scouter.isValidTeamIndexStr(m_teamIndexField.getText().toString().trim())) {
            m_teamIndexErrMsg.setVisibility(View.VISIBLE);
            Log.d(TAG,"+++>> checkValidData(): ERROR: teamIndex is not valid: "+m_teamIndexField.getText().toString().trim());
            return false;
        }
        return true;
    }

    private void sendResult(int resultCode) throws JSONException, IOException {
        Log.d(TAG, "sendResult() called");
        Intent i = new Intent(getActivity(), MatchListActivity.class);
    }
}

