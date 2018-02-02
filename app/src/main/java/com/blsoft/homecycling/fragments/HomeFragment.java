package com.blsoft.homecycling.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.blsoft.homecycling.MainActivity;
import com.blsoft.homecycling.R;
import com.blsoft.homecycling.dicts.DictFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment  {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        final Button btnTraining = view.findViewById(R.id.btnTraining);
        final Button btnHistory = view.findViewById(R.id.btnHistory);
        final Button btnSettings = view.findViewById(R.id.btnSettings);
        final Button btnSynchronization = view.findViewById(R.id.btnSynchronization);

        setOnClickListener(btnTraining, DictFragment.TRAINING);
        setOnClickListener(btnHistory, DictFragment.HISTORY);
        setOnClickListener(btnSettings, DictFragment.SETTINGS);
        setOnClickListener(btnSynchronization, DictFragment.SYNCHRONIZATION);

        return view;
    }

    private void setOnClickListener(Button button, final DictFragment fragment) {
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).switchFragment(fragment);
            }
        });
    }
}
