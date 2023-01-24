package com.example.runnertracker.View;

import static com.example.runnertracker.Model.GPSService.EXTRA_DISTANCE;
import static com.example.runnertracker.Model.GPSService.EXTRA_TIME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.example.runnertracker.Model.GPSService;
import com.example.runnertracker.R;
import com.example.runnertracker.ViewModel.GPSViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/*
    Used to call GPS service, provides user a means to view if they're movement is being tracked
    and then stop said tracking.
    
    Service is bound with communication via a method call which will save the duration and distance of the run.

     I used sharedPreferences over save/restore instance since restore instance never initialized.
 */
public class RunnerTracker extends AppCompatActivity {

    private String mDistance;
    private String mDuration;
    FrameLayout mFrameLayoutStart;
    FrameLayout mFrameLayoutStop;
    FloatingActionButton mStartButton;
    FloatingActionButton mStopButton;
    Intent mIntent = null;
    private boolean mStopFrame = false;
    public static final String PREF_FRAME = "PREF_FRAME";
    public static final String PREF_FRAME_STOP_LAYOUT = "PREF_FRAME_STOP_LAYOUT";
    private GPSService mGpsService;
    private GPSViewModel mGpsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;
        if(savedInstanceState != null) {
            Log.d("comp3018", "we in saved instance oncreate");
            mStopFrame = savedInstanceState.getBoolean("TEST", false);
        }
        setContentView(R.layout.activity_runner_tracker);

        mFrameLayoutStart = findViewById(R.id.frame_layout_start);
        mFrameLayoutStop = findViewById(R.id.frame_layout_stop);
        mStartButton = findViewById(R.id.floating_button_start);
        mStopButton = findViewById(R.id.floating_button_stop);

        toggleStartStopVisibility();
        buttonListener();

        mGpsViewModel = ViewModelProviders.of(this).get(GPSViewModel.class);
        assignBinder();
    }

    private void assignBinder() {
        mGpsViewModel.getBinder().observe(this, new Observer<GPSService.MyBinder>() {
            @Override
            public void onChanged(GPSService.MyBinder myBinder) {
                if (myBinder != null) {
                    mGpsService = myBinder.getService();
                } else {
                    mGpsService = null;
                }
            }
        });
    }

    private void toggleUpdates() {
        if(mGpsService != null) {
            mGpsService.saveTravel();
            mDistance = String.valueOf(mGpsService.getFinalDistance());
            mDuration =  String.valueOf(mGpsService.getFinalDuration());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences previewSizePref = getSharedPreferences(PREF_FRAME, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = previewSizePref.edit();
        prefEditor.putBoolean(PREF_FRAME_STOP_LAYOUT, isStopFrame());
        prefEditor.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences previewSizePref = getSharedPreferences(PREF_FRAME, MODE_PRIVATE);
        if (previewSizePref.contains(PREF_FRAME_STOP_LAYOUT)) {
           toggleStartStopVisibility();
        }
        toggleStartStopVisibility();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mGpsViewModel.getBinder() != null) {
            unbindService(mGpsViewModel.getServiceConnection());
        }
        if(mIntent != null) {
            stopService(mIntent);
        }
    }

    private void startService() {
        mIntent = new Intent(RunnerTracker.this, GPSService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(mIntent);
        }
        bindService();
    }

    private void bindService() {
        mIntent = new Intent(RunnerTracker.this, GPSService.class);
        bindService(mIntent, mGpsViewModel.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    public void buttonListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.floating_button_start:
                        startService();
                        setStopFrame(true);
                        toggleStartStopVisibility();
                        break;
                    case R.id.floating_button_stop:
                        toggleUpdates();
                        stopService(mIntent);
                        setStopFrame(false);
                        sendGPSData();
                        break;
                }
            }
        };

        findViewById(R.id.floating_button_start).setOnClickListener(listener);
        findViewById(R.id.floating_button_stop).setOnClickListener(listener);
    }

    private void toggleStartStopVisibility() {
        if(isStopFrame()) {
            mFrameLayoutStart.setVisibility(View.GONE);
            mStartButton.setEnabled(false);
            mFrameLayoutStop.setVisibility(View.VISIBLE);
            mStopButton.setEnabled(true);
        }
        else {
            mFrameLayoutStop.setVisibility(View.GONE);
            mStopButton.setEnabled(false);
            mFrameLayoutStart.setVisibility(View.VISIBLE);
            mStartButton.setEnabled(true);
        }
    }

    public boolean isStopFrame() {
        return mStopFrame;
    }

    public void setStopFrame(boolean mStopFrame) {
        this.mStopFrame = mStopFrame;
    }

    private void sendGPSData() {
        Intent data = new Intent();
        data.putExtra(EXTRA_DISTANCE, mDistance);
        data.putExtra(EXTRA_TIME, mDuration);
        setResult(RESULT_OK, data);
        finish();
    }
}