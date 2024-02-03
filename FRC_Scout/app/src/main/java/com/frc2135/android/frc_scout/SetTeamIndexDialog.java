package com.frc2135.android.frc_scout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;


public class SetTeamIndexDialog extends DialogFragment
{
    private static final String TAG = "SetTeamIndexDlg";

    private Scouter m_Scouter;
    private EditText m_teamIndexField;
    private TextView m_teamIndexErrMsg;

    /** @noinspection Convert2Lambda*/
    @NonNull
    public Dialog onCreateDialog(Bundle SavedInstanceState)
    {
        setCancelable(true);
        Log.d(TAG, "onCreateDialog called");

        View v = requireActivity().getLayoutInflater().inflate(R.layout.set_team_index_dlg, null);
        m_teamIndexField = v.findViewById(R.id.set_team_index_field);
        m_teamIndexField.setHint("Enter team index number (1-6 or 'None')");
        m_teamIndexErrMsg = v.findViewById(R.id.team_index_err);
        m_teamIndexErrMsg.setVisibility(View.INVISIBLE);
        m_teamIndexErrMsg.setTextColor(Color.RED);

        // Get the existing index from Scouter, if any.
        m_Scouter = Scouter.get(getContext());
        String str1 = "None";
        if (m_Scouter != null)
        {
            String indexStr = m_Scouter.getTeamIndexStr();
            Log.d(TAG, "From Scouter: teamFieldIndex = " + indexStr);
            if (m_Scouter.isValidTeamIndexStr(indexStr))
                m_teamIndexField.setText(m_Scouter.getTeamIndexStr());
            else
                m_teamIndexField.setText(str1);
        }
        else
            m_teamIndexField.setText(str1);

        m_teamIndexField.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence c, int start, int before, int count)
            {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after)
            {
            }

            public void afterTextChanged(Editable c)
            {
                // Validate the teamIndex entry.
                m_teamIndexErrMsg.setVisibility(View.INVISIBLE); // reset invalid teamIndex msg
                if (m_Scouter != null)
                {
                    if (!m_Scouter.isValidTeamIndexStr(m_teamIndexField.getText().toString().trim()))
                    {
                        m_teamIndexErrMsg.setVisibility(View.VISIBLE); // show invalid teamIndex msg
                        m_teamIndexField.setTextColor(Color.RED);
                    }
                    else
                        m_teamIndexField.setTextColor(Color.BLACK);
                }
            }
        });

        // OK and Cancel button action handling: 
        AlertDialog.Builder abd = new AlertDialog.Builder(getActivity());
        abd.setView(v);
        abd.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dismiss();
            }
        });

        abd.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                // If the data is valid, save to Scouter and end
                if (checkValidData())
                {
                    if (m_Scouter != null)
                    {
                        m_Scouter.setTeamIndexStr(m_teamIndexField.getText().toString());
                        Log.d(TAG, "onOK: setting team index to " + m_teamIndexField.getText().toString());
                    }
                }
            }
        });

        AlertDialog alertDialog = abd.create();
        alertDialog.setTitle("Set Team Index");
        alertDialog.show();
        Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setBackgroundColor(Color.parseColor("#3F51B5"));
        Button cancelButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        cancelButton.setBackgroundColor(Color.parseColor("#3F51B5"));
        return alertDialog;
    }

    public static SetTeamIndexDialog newInstance()
    {
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();
        SetTeamIndexDialog dialog = new SetTeamIndexDialog();
        dialog.setArguments(args);
        return dialog;
    }

    private boolean checkValidData()
    {
        m_teamIndexErrMsg.setVisibility(View.INVISIBLE);

        // Validate team index
        if (!m_Scouter.isValidTeamIndexStr(m_teamIndexField.getText().toString().trim()))
        {
            m_teamIndexErrMsg.setVisibility(View.VISIBLE);
            Log.d(TAG, ">> checkValidData(): ERROR: teamIndex is not valid: " + m_teamIndexField.getText().toString().trim());
            return false;
        }
        return true;
    }
}

