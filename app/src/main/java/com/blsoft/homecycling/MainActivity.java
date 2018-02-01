package com.blsoft.homecycling;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blsoft.homecycling.dicts.DictFragment;
import com.blsoft.homecycling.fragments.HomeFragment;
import com.blsoft.homecycling.global.ConstStrings;

public class MainActivity extends AppCompatActivity {

    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        }
        if (!isFinishing()) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.layout_fragment_area, newFragment, ConstStrings.CURRENT_FRAGMENT);
            transaction.addToBackStack(null);

            transaction.commitAllowingStateLoss();
        }
    }
}
