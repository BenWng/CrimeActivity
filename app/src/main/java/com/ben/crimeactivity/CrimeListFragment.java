package com.ben.crimeactivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ben_Big on 12/16/16.
 */

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private static final int REQUEST_CRIME=1;
    private UUID mCrimeId;
    private boolean newCrimeAdded=false;
    private static final String SAVED_SUBTITLE_VISIBLE="subtitle";
    private boolean mIsDeleted=false;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_crime_list,container,false);

        mCrimeRecyclerView=(RecyclerView) view.findViewById(R.id.crime_recycler_view);

        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState!=null){
            mSubtitleVisible=savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateSubtitle();
        updateUI();
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }


    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();


        if(mAdapter==null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else if (mIsDeleted){
            mAdapter.notifyDataSetChanged();
            mIsDeleted=false;
        }
        else if (mCrimeId==null){
            int numCrimes=crimes.size();
            if (newCrimeAdded && numCrimes>=1) {
                mAdapter.notifyItemInserted(numCrimes-1);
                newCrimeAdded=false;
            }
        }
        else {
            mAdapter.updateItemChanged(mCrimeId);
            mCrimeId=null;
        }
    }



    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CheckBox mSolvedBoxView;
        public TextView mTitleTextView;
        public TextView mDateTextView;

        private Crime mCrime;

        public CrimeHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView=(TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView=(TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedBoxView=(CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
        }

        public void bind(Crime crime){
            mCrime=crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedBoxView.setChecked(mCrime.isSolved());

            mSolvedBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    mCrime.setSolved(isChecked);
                }
            });
        }

        @Override
        public void onClick(View v){
            mCrimeId=mCrime.getId();
            Intent intent=CrimePagerActivity.createIntent(getActivity(),mCrimeId);
            startActivityForResult(intent,REQUEST_CRIME);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode!= Activity.RESULT_OK){
            return;
        }
        if (requestCode==REQUEST_CRIME){
            if (data==null){
                return;
            }
            mIsDeleted=data.getBooleanExtra(CrimePagerActivity.EXTRA_CRIME_DEL,false);
            if (mIsDeleted){
                mCrimeId=(UUID) data.getSerializableExtra(CrimePagerActivity.EXTRA_CRIME_DEL_ID);
                CrimeLab.get(getActivity()).deleteCrime(mCrimeId);
            }
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes=crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent,int viewType){
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            View view=layoutInflater.inflate(R.layout.list_item_crime,parent,false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position){
            Crime crime=mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount(){
            return mCrimes.size();
        }


        private void updateItemChanged(UUID id){
            int position=0;
            for (int i=0;i<mCrimes.size();i++){
                if (mCrimes.get(i).getId().equals(id)){
                    position=i;
                    break;
                }
            }
            notifyItemChanged(position);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);


        MenuItem subtitleItem=menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                newCrimeAdded=true;
                Crime crime=new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent=CrimePagerActivity.createIntent(getActivity(),crime.getId());
                startActivityForResult(intent,REQUEST_CRIME);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible=!mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        CrimeLab crimeLab=CrimeLab.get(getActivity());
        int crimeCount=crimeLab.getCrimes().size();
        String subtitle=getString(R.string.subtitle_format,crimeCount);
        //String subtitle=""+crimeCount+" crimes";

        if(!mSubtitleVisible){
            subtitle=null;
        }

        AppCompatActivity activity=(AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);

    }




}
