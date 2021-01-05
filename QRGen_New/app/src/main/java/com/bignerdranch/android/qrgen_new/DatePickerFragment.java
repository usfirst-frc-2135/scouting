package com.bignerdranch.android.qrgen_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import java.util.GregorianCalendar;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = "com.bignerranch.android.qrgen.date";
    private static final String TAG = "*****DatePickerFragment";
    private Date mDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //mDate = (Date)getArguments().getSerializable(EXTRA_DATE);
        Calendar c = Calendar.getInstance();
        //c.setTime(mDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);

        DatePicker dp = (DatePicker)v.findViewById(R.id.dialog_date_datePicker);
        Log.d(TAG, year +  " " + month + " "+ day );
        dp.init(year, month, day, new DatePicker.OnDateChangedListener(){
            public void onDateChanged(DatePicker view, int year, int month, int day){
                mDate = new GregorianCalendar(year, month, day).getTime();
            }
        });
        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.date_picker_title).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                sendResult(Activity.RESULT_OK);
            }
        }).create();
    }

    public static DatePickerFragment newInstance(){
        /*Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);*/

        DatePickerFragment fragment = new DatePickerFragment();
        //fragment.setArguments(args);

        return fragment;
    }

    private void sendResult(int resultCode){
        if(getTargetFragment()== null){
            return;
        }
        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate.toString());

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);

    }


}
