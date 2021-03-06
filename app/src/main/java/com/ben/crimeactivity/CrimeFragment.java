package com.ben.crimeactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;


import java.util.Date;
import java.util.UUID;

/**
 * Created by Ben_Big on 10/26/16.
 */

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE="DialogDate";
    private static final String DIALOG_TIME="DialogTime";

    private static final int REQUEST_DATE=0;
    private static final int REQUEST_TIME=1;



    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mDeleteButton;
    private CheckBox mSolvedCheckBox;



    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args=new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);

        CrimeFragment fragment=new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UUID crimeId=(UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime=CrimeLab.get(getActivity()).getCrime(crimeId);
    }


    private String removeTime(Date date){
        String[] dateComponents=mCrime.getDate().toString().split(" ");
        String dateWithoutTime="";
        for (int i=0;i<dateComponents.length;i++){
            if (i==0) dateWithoutTime+=dateComponents[i];
            else if (i!=3 && i!=4) dateWithoutTime=dateWithoutTime+" "+dateComponents[i];
        }
        return dateWithoutTime;
    }

    private String removeDate(Date date){
        String[] dateComponents=mCrime.getDate().toString().split(" ");
        String onlyTime="";
        for (int i=0;i<dateComponents.length;i++){
            if (i==3) onlyTime+=dateComponents[i];
            else if (i==4) onlyTime=onlyTime+" "+dateComponents[i];
        }
        return onlyTime;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField=(EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start, int before, int count) {
                mCrime.setTitle(s.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });





        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mDateButton.setText(removeTime(mCrime.getDate()));
        mDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DatePickerFragment dialog=DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                FragmentManager manager=getFragmentManager();
                dialog.show(manager,DIALOG_DATE);
            }
        });

        mTimeButton= (Button) v.findViewById(R.id.crime_time);
        mTimeButton.setText(removeDate(mCrime.getDate()));
        mTimeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TimePickerFragment dialog=TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_TIME);
                FragmentManager manager=getFragmentManager();
                dialog.show(manager,DIALOG_TIME);
            }
        });


        mSolvedCheckBox=(CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    mCrime.setSolved(isChecked);
                }
        });


        mDeleteButton=(Button) v.findViewById(R.id.delete_crime);
        mDeleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent data=new Intent();
                data.putExtra(CrimePagerActivity.EXTRA_CRIME_DEL,true);
                data.putExtra(CrimePagerActivity.EXTRA_CRIME_DEL_ID,mCrime.getId());
                getActivity().setResult(Activity.RESULT_OK,data);
                getActivity().finish();
            }
        });


        return v;
    }


    public void returnResult(){
        getActivity().setResult(Activity.RESULT_OK,null);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode!=Activity.RESULT_OK){
            return;
        }

        if (requestCode==REQUEST_DATE){
            Date date=(Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mDateButton.setText(removeTime(mCrime.getDate()));
        }

        if (requestCode==REQUEST_TIME){
            Date date=(Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            mTimeButton.setText(removeDate(mCrime.getDate()));

        }


    }


}
