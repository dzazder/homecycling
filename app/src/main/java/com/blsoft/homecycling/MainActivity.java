package com.blsoft.homecycling;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blsoft.homecycling.db.DBManager;
import com.blsoft.homecycling.dicts.DictFragment;
import com.blsoft.homecycling.fragments.HistoryFragment;
import com.blsoft.homecycling.fragments.HomeFragment;
import com.blsoft.homecycling.fragments.TrainingFragment;
import com.blsoft.homecycling.global.ConstStrings;

public class MainActivity extends AppCompatActivity {

    public static FragmentManager fragmentManager;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbManager = new DBManager(this);

        if (savedInstanceState == null) {
            switchFragment(DictFragment.HOME);
        }

        fragmentManager = getFragmentManager();
    }

    /**
     * It runs selected fragment
     * @param fragment Id of fragment to run, ids are saved in MainActivity fields
     */
    public void switchFragment(DictFragment fragment) {
        Fragment newFragment = null;
        switch (fragment) {
            case HOME:
                newFragment = new HomeFragment();
                break;
            case TRAINING:
                newFragment = new TrainingFragment();
                break;
            case HISTORY:
                newFragment = new HistoryFragment();
                break;
        }
        if (!isFinishing()) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.layout_fragment_area, newFragment, ConstStrings.CURRENT_FRAGMENT);
            transaction.addToBackStack(null);

            transaction.commitAllowingStateLoss();
        }
    }

    public DBManager getDBManager() {
        return dbManager;
    }
}
