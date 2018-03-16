package com.blsoft.homecycling.fragments;


import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blsoft.homecycling.MainActivity;
import com.blsoft.homecycling.R;
import com.blsoft.homecycling.db.DBManager;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    TextView tvHistory;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        tvHistory = view.findViewById(R.id.tv_training);

        DBManager dbManager = ((MainActivity)getActivity()).getDBManager();
        Cursor k = dbManager.getTrainings();
        while (k.moveToNext()) {
            int id = k.getInt(0);
            String dateStart = k.getString(1);

            tvHistory.setText(tvHistory.getText() + "\n" + id + ": " + dateStart);
        }

        return view;
    }

}
