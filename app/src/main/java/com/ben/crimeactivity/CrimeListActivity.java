package com.ben.crimeactivity;

import android.support.v4.app.Fragment;

/**
 * Created by Ben_Big on 12/16/16.
 */

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }

}
