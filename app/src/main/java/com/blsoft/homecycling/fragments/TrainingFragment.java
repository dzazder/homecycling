package com.blsoft.homecycling.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blsoft.homecycling.R;
import com.blsoft.homecycling.dicts.TrainingMode;
import com.blsoft.homecycling.dicts.TrainingState;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment implements View.OnClickListener {

    private TrainingMode trainingMode = TrainingMode.FREE;
    private TrainingState trainingState = TrainingState.NOT_STARTED;
    private long startTime = 0;
    private long trainingTime = 0;
    private long millis;

    Button btnStart;
    Button btnPause;
    Button btnEnd;

    TextView txtTime;
    TextView txtDistance;
    TextView txtPower;
    TextView txtSpeed;
    TextView txtAvgSpeed;
    TextView txtAvgPower;

    final Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            millis = System.currentTimeMillis() - startTime + trainingTime;
            int seconds = (int)(millis/1000);
            int hours = seconds / 60 / 60;
            int minutes = seconds / 60;
            seconds = seconds % 60;


            txtTime.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    public TrainingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);

        btnStart = view.findViewById(R.id.btnStart);
        btnPause = view.findViewById(R.id.btnPause);
        btnEnd = view.findViewById(R.id.btnEnd);

        btnStart.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnEnd.setOnClickListener(this);

        txtTime = view.findViewById(R.id.txtTime);
        txtDistance = view.findViewById(R.id.txtDistance);
        txtPower = view.findViewById(R.id.txtPower);
        txtSpeed = view.findViewById(R.id.txtSpeed);
        txtAvgSpeed = view.findViewById(R.id.txtAvgSpeed);
        txtAvgPower = view.findViewById(R.id.txtAvgPower);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                startTraining();
                break;
            case R.id.btnPause:
                pauseTraining();
                break;
            case R.id.btnEnd:
                endTraining();
                break;
        }
    }

    protected void startTraining() {
        trainingState = TrainingState.IN_PROGRESS;
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    protected void pauseTraining() {
        trainingTime = millis;
        trainingState = TrainingState.PAUSED;
        timerHandler.removeCallbacks(timerRunnable);
    }

    protected void endTraining() {
        trainingState = TrainingState.FINISHED;
        timerHandler.removeCallbacks(timerRunnable);
    }

    public void setTrainingMode(TrainingMode trainingMode) {
        this.trainingMode = trainingMode;
    }
}
