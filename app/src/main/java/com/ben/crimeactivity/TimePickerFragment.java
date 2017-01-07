package com.ben.crimeactivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Ben_Big on 1/5/17.
 */

public class TimePickerFragment extends DialogFragment {
    private static final String ARG_DATE="date";
    private TimePicker mTimePicker;



    public static TimePickerFragment newInstance(Date date){
        Bundle args=new Bundle();
        args.putSerializable(ARG_DATE,date);

        TimePickerFragment fragment=new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Date date=(Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);
        int hour=calendar.get(Calendar.HOUR);
        int minute=calendar.get(Calendar.MINUTE);


        View v= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time,null);

        mTimePicker=(TimePicker) v.findViewById(R.id.dialog_time_time_picker);
        if (Build.VERSION.SDK_INT<23) {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }
        else{
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        }


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                        int hour;
                        int minute;
                        if (Build.VERSION.SDK_INT<23) {
                            hour=mTimePicker.getCurrentHour();
                            minute=mTimePicker.getCurrentMinute();
                        }
                        else{
                            hour=mTimePicker.getHour();
                            minute=mTimePicker.getMinute();
                        }
                        Date date=new GregorianCalendar(year,month,day,hour,minute).getTime();

                    }
                })
                .create();
    }

}
