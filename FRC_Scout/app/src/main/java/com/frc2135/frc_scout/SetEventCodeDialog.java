/*
 * Copyright (c) 2020-26 FRC 2135 Presentation Invasion
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.frc2135.frc_scout;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.frc2135.frc_scout.databinding.LoadEventDialogBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;
import java.util.Objects;

/**
 * Dialog for setting the current event code in application settings.
 * Pre-fills with the current event code and allows resetting to the default event code.
 */
public class SetEventCodeDialog extends DialogFragment
{
    private static final String TAG = "SetEventCodeDialog";
    private LoadEventDialogBinding m_binding;

    /**
     * Creates a new instance of {@link SetEventCodeDialog}.
     *
     * @return a new SetEventCodeDialog instance
     */
    public static SetEventCodeDialog newInstance()
    {
        return new SetEventCodeDialog();
    }

    /**
     * Constructs the {@link AlertDialog} instance, initializes View Binding, and sets up
     * the event code input field and listeners.
     *
     * @param savedInstanceState if the dialog is being re-initialized from a previous saved state
     * @return the constructed {@link Dialog}
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreateDialog called");

        LayoutInflater inflater = getLayoutInflater();
        m_binding = LoadEventDialogBinding.inflate(inflater);

        // Pre-fill with current event code if available
        Settings settings = Settings.getInstance(requireContext());
        if (settings != null)
        {
            String currentEventCode = settings.getEventCode();
            if (currentEventCode != null && !currentEventCode.isEmpty())
            {
                m_binding.loadEventCodeInput.setText(currentEventCode);
            }
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.set_event_code_title)
                .setView(m_binding.getRoot())
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton(R.string.default_event_code, (d, w) -> {
                    Log.i(TAG, "Load default event code called");
                    m_binding.loadEventCodeInput.setText(Constants.DEFAULT_EVENT_CODE);
                    m_binding.loadEventCodeLayout.setError(null);
                    if (settings != null)
                    {
                        settings.setEventCode(Constants.DEFAULT_EVENT_CODE);
                        getParentFragmentManager().setFragmentResult("event_code_changed", new Bundle());
                    }
                })
                .create();

        m_binding.loadEventCodeInput.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                m_binding.loadEventCodeLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        dialog.setOnShowListener(d -> {
            MaterialButton okButton = (MaterialButton) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(v -> {
                String eventCode = Objects.requireNonNull(m_binding.loadEventCodeInput.getText()).toString().trim().toLowerCase(Locale.US);
                if (!ScoutUtils.isValidEventCode(TAG, eventCode))
                {
                    m_binding.loadEventCodeLayout.setError("Invalid event code (e.g., 2026casac)");
                    return;
                }
                m_binding.loadEventCodeLayout.setError(null);
                if (settings != null)
                {
                    Log.i(TAG, "Saving event code to settings: " + eventCode);
                    settings.setEventCode(eventCode);
                    getParentFragmentManager().setFragmentResult("event_code_changed", new Bundle());
                }
                dialog.dismiss();
            });
        });

        m_binding.loadEventCodeInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
            {
                MaterialButton okButton = (MaterialButton) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (okButton != null)
                {
                    okButton.setFocusableInTouchMode(true);
                    okButton.requestFocus();
                }
                return true;
            }
            return false;
        });

        return dialog;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");
        m_binding = null;
    }
}
