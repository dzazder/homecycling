package com.blsoft.homecycling.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blsoft.homecycling.R;
import com.blsoft.homecycling.dicts.TrainingMode;
import com.blsoft.homecycling.dicts.TrainingState;
import com.blsoft.homecycling.entitites.TrainingPeek;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc;
import com.dsi.ant.plugins.antplus.pcc.defines.BatteryStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusBikeSpdCadCommonPcc;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc;
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment implements View.OnClickListener {

    AntPlusBikeSpeedDistancePcc bsdPcc = null;
    PccReleaseHandle<AntPlusBikeSpeedDistancePcc> bsdReleaseHandle = null;
    AntPlusBikeCadencePcc bcPcc = null;
    PccReleaseHandle<AntPlusBikeCadencePcc> bcReleaseHandle = null;

    private TrainingMode trainingMode = TrainingMode.FREE;
    private TrainingState trainingState = TrainingState.NOT_STARTED;
    private long startTime = 0;
    private long trainingTime = 0;
    private long millis;

    private ArrayList<TrainingPeek> training = new ArrayList<>();

    Button btnStart;
    Button btnPause;
    Button btnEnd;

    TextView txtTime;
    TextView txtDistance;
    TextView txtPower;
    TextView txtSpeed;
    TextView txtAvgSpeed;
    TextView txtAvgPower;

    TextView txtAntTime;
    TextView tv_status;

    final Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            millis = System.currentTimeMillis() - startTime + trainingTime;
            int seconds = (int) (millis / 1000);
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

        tv_status = view.findViewById(R.id.tv_status);
        txtAntTime = view.findViewById(R.id.txtAntTime);

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
        resetPcc();
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

    private void resetPcc() {
        //Release the old access if it exists
        if (bsdReleaseHandle != null) {
            bsdReleaseHandle.close();
        }
        if (bcReleaseHandle != null) {
            bcReleaseHandle.close();
        }

        // starts the plugins UI search
        bsdReleaseHandle = AntPlusBikeSpeedDistancePcc.requestAccess(getActivity(), getActivity(),
                mResultReceiver, mDeviceStateChangeReceiver);

    }

    AntPluginPcc.IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc> mResultReceiver = new AntPluginPcc.IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc>() {
        // Handle the result, connecting to events on success or reporting
        // failure to user.
        @Override
        public void onResultReceived(AntPlusBikeSpeedDistancePcc result,
                                     RequestAccessResult resultCode, DeviceState initialDeviceState) {
            switch (resultCode) {
                case SUCCESS:
                    bsdPcc = result;
                    tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                    subscribeToEvents();
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    Toast.makeText(getActivity(), "Channel Not Available",
                            Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case ADAPTER_NOT_DETECTED:
                    Toast
                            .makeText(getActivity(),
                                    "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.",
                                    Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case BAD_PARAMS:
                    // Note: Since we compose all the params ourself, we should
                    // never see this result
                    Toast.makeText(getActivity(), "Bad request parameters.", Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case OTHER_FAILURE:
                    Toast.makeText(getActivity(),
                            "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case DEPENDENCY_NOT_INSTALLED:
                    tv_status.setText("Error. Do Menu->Reset.");
                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder(getActivity());
                    adlgBldr.setTitle("Missing Dependency");
                    adlgBldr
                            .setMessage("The required service\n\""
                                    + AntPlusBikeSpeedDistancePcc.getMissingDependencyName()
                                    + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
                    adlgBldr.setCancelable(true);
                    adlgBldr.setPositiveButton("Go to Store", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent startStore = null;
                            startStore = new Intent(Intent.ACTION_VIEW, Uri
                                    .parse("market://details?id="
                                            + AntPlusBikeSpeedDistancePcc
                                            .getMissingDependencyPackageName()));
                            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            //Activity_BikeSpeedDistanceSampler.this.startActivity(startStore);
                        }
                    });
                    adlgBldr.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    final AlertDialog waitDialog = adlgBldr.create();
                    waitDialog.show();
                    break;
                case USER_CANCELLED:
                    tv_status.setText("Cancelled. Do Menu->Reset.");
                    break;
                case UNRECOGNIZED:
                    Toast.makeText(getActivity(),
                            "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                            Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                default:
                    Toast.makeText(getActivity(),
                            "Unrecognized result: " + resultCode, Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
            }
        }

        /**
         * Subscribe to all the heart rate events, connecting them to display
         * their data.
         */
        private void subscribeToEvents()
        {
            // 2.095m circumference = an average 700cx23mm road tire
            bsdPcc.subscribeCalculatedSpeedEvent(new AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver(new BigDecimal(2.095))
            {
                @Override
                public void onNewCalculatedSpeed(final long estTimestamp,
                                                 final EnumSet<EventFlag> eventFlags, final BigDecimal calculatedSpeed)
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            txtAntTime.setText(String.valueOf(estTimestamp));

                            txtSpeed.setText(String.format("%.2f km/h", calculatedSpeed));
                        }
                    });
                }
            });

            bsdPcc
                    .subscribeCalculatedAccumulatedDistanceEvent(new AntPlusBikeSpeedDistancePcc.CalculatedAccumulatedDistanceReceiver(
                            new BigDecimal(2.095)) // 2.095m circumference = an average
                            // 700cx23mm road tire
                    {

                        @Override
                        public void onNewCalculatedAccumulatedDistance(final long estTimestamp,
                                                                       final EnumSet<EventFlag> eventFlags,
                                                                       final BigDecimal calculatedAccumulatedDistance)
                        {
                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    txtAntTime.setText(String.valueOf(estTimestamp));
                                    txtDistance.setText(String.format("%f m", calculatedAccumulatedDistance));
                                }
                            });
                        }
                    });

            bsdPcc.subscribeRawSpeedAndDistanceDataEvent(new AntPlusBikeSpeedDistancePcc.IRawSpeedAndDistanceDataReceiver()
            {
                @Override
                public void onNewRawSpeedAndDistanceData(final long estTimestamp,
                                                         final EnumSet<EventFlag> eventFlags,
                                                         final BigDecimal timestampOfLastEvent, final long cumulativeRevolutions)
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            txtAntTime.setText(String.valueOf(estTimestamp));

                            //tv_timestampOfLastEvent.setText(String.valueOf(timestampOfLastEvent));

                            //tv_cumulativeRevolutions.setText(String.valueOf(cumulativeRevolutions));
                        }
                    });
                }
            });

            if (bsdPcc.isSpeedAndCadenceCombinedSensor())
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //tv_isSpdAndCadCombo.setText("Yes");
                        //tv_cumulativeOperatingTime.setText("N/A");
                        //tv_manufacturerID.setText("N/A");
                        //tv_serialNumber.setText("N/A");
                        //tv_hardwareVersion.setText("N/A");
                        //tv_softwareVersion.setText("N/A");
                        //tv_modelNumber.setText("N/A");

                        //tv_calculatedCadence.setText("...");

                        bcReleaseHandle = AntPlusBikeCadencePcc.requestAccess(
                                getActivity(),
                                bsdPcc.getAntDeviceNumber(), 0, true,
                                new AntPluginPcc.IPluginAccessResultReceiver<AntPlusBikeCadencePcc>()
                                {
                                    // Handle the result, connecting to events
                                    // on success or reporting failure to user.
                                    @Override
                                    public void onResultReceived(AntPlusBikeCadencePcc result,
                                                                 RequestAccessResult resultCode,
                                                                 DeviceState initialDeviceStateCode)
                                    {
                                        switch (resultCode)
                                        {
                                            case SUCCESS:
                                                bcPcc = result;
                                                bcPcc
                                                        .subscribeCalculatedCadenceEvent(new AntPlusBikeCadencePcc.ICalculatedCadenceReceiver()
                                                        {
                                                            @Override
                                                            public void onNewCalculatedCadence(
                                                                    long estTimestamp,
                                                                    EnumSet<EventFlag> eventFlags,
                                                                    final BigDecimal calculatedCadence)
                                                            {
                                                                getActivity().runOnUiThread(new Runnable()
                                                                {
                                                                    @Override
                                                                    public void run()
                                                                    {
                                                                        //tv_calculatedCadence.setText(String
                                                                        //        .valueOf(calculatedCadence));
                                                                    }
                                                                });
                                                            }
                                                        });
                                                break;
                                            case CHANNEL_NOT_AVAILABLE:
                                                //tv_calculatedCadence
                                                //        .setText("CHANNEL NOT AVAILABLE");
                                                break;
                                            case BAD_PARAMS:
                                                //tv_calculatedCadence.setText("BAD_PARAMS");
                                                break;
                                            case OTHER_FAILURE:
                                                //tv_calculatedCadence.setText("OTHER FAILURE");
                                                break;
                                            case DEPENDENCY_NOT_INSTALLED:
                                                //tv_calculatedCadence
                                                //        .setText("DEPENDENCY NOT INSTALLED");
                                                break;
                                            default:
                                                //tv_calculatedCadence.setText("UNRECOGNIZED ERROR: "
                                                //        + resultCode);
                                                break;
                                        }
                                    }
                                },
                                // Receives state changes and shows it on the
                                // status display line
                                new AntPluginPcc.IDeviceStateChangeReceiver()
                                {
                                    @Override
                                    public void onDeviceStateChange(final DeviceState newDeviceState)
                                    {
                                        getActivity().runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                if (newDeviceState != DeviceState.TRACKING)
                                                    //tv_calculatedCadence.setText(newDeviceState
                                                    //        .toString());
                                                if (newDeviceState == DeviceState.DEAD)
                                                    bcPcc = null;
                                            }
                                        });

                                    }
                                });
                    }
                });
            }
            else
            {
                // Subscribe to the events available in the pure cadence profile
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //tv_isSpdAndCadCombo.setText("No");
                        //tv_calculatedCadence.setText("N/A");
                    }
                });

                bsdPcc.subscribeCumulativeOperatingTimeEvent(new AntPlusLegacyCommonPcc.ICumulativeOperatingTimeReceiver()
                {
                    @Override
                    public void onNewCumulativeOperatingTime(final long estTimestamp,
                                                             final EnumSet<EventFlag> eventFlags, final long cumulativeOperatingTime)
                    {
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                txtAntTime.setText(String.valueOf(estTimestamp));

                                //tv_cumulativeOperatingTime.setText(String
                                 //       .valueOf(cumulativeOperatingTime));
                            }
                        });
                    }
                });

                bsdPcc.subscribeManufacturerAndSerialEvent(new AntPlusLegacyCommonPcc.IManufacturerAndSerialReceiver()
                {
                    @Override
                    public void onNewManufacturerAndSerial(final long estTimestamp,
                                                           final EnumSet<EventFlag> eventFlags, final int manufacturerID,
                                                           final int serialNumber)
                    {
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                txtAntTime.setText(String.valueOf(estTimestamp));

                                //tv_manufacturerID.setText(String.valueOf(manufacturerID));
                                //tv_serialNumber.setText(String.valueOf(serialNumber));
                            }
                        });
                    }
                });

                bsdPcc.subscribeVersionAndModelEvent(new AntPlusLegacyCommonPcc.IVersionAndModelReceiver()
                {
                    @Override
                    public void onNewVersionAndModel(final long estTimestamp,
                                                     final EnumSet<EventFlag> eventFlags, final int hardwareVersion,
                                                     final int softwareVersion, final int modelNumber)
                    {
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                txtAntTime.setText(String.valueOf(estTimestamp));

                                //tv_hardwareVersion.setText(String.valueOf(hardwareVersion));
                                //tv_softwareVersion.setText(String.valueOf(softwareVersion));
                                //tv_modelNumber.setText(String.valueOf(modelNumber));
                            }
                        });
                    }
                });

                bsdPcc.subscribeBatteryStatusEvent(new AntPlusBikeSpdCadCommonPcc.IBatteryStatusReceiver()
                {
                    @Override
                    public void onNewBatteryStatus(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                                   final BigDecimal batteryVoltage, final BatteryStatus batteryStatus)
                    {
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                txtAntTime.setText(String.valueOf(estTimestamp));

                                //textView_BatteryVoltage.setText(batteryVoltage.intValue() != -1 ? String.valueOf(batteryVoltage) + "V" : "Invalid");
                                //textView_BatteryStatus.setText(batteryStatus.toString());
                            }
                        });
                    }
                });

                bsdPcc.subscribeMotionAndSpeedDataEvent(new AntPlusBikeSpeedDistancePcc.IMotionAndSpeedDataReceiver()
                {
                    @Override
                    public void onNewMotionAndSpeedData(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                                        final boolean isStopped)
                    {
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                txtAntTime.setText(String.valueOf(estTimestamp));

                                //textView_IsStopped.setText(String.valueOf(isStopped));
                            }
                        });
                    }
                });
            }
        }
    };
    AntPluginPcc.IDeviceStateChangeReceiver mDeviceStateChangeReceiver = new AntPluginPcc.IDeviceStateChangeReceiver() {
        @Override
        public void onDeviceStateChange(final DeviceState newDeviceState) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_status.setText(bsdPcc.getDeviceName() + ": " + newDeviceState);
                    if (newDeviceState == DeviceState.DEAD)
                        bsdPcc = null;
                }
            });
        }
    };


}
