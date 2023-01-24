package com.example.runnertracker.Model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.runnertracker.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;

/*
    Service created to ensure location can be tracked in the background and main ui thread not interrupted

    Stores location data in ArrayList every second, subtracts distance from last -> first location
    to retrieve final distance travelled

    Sends data to RunnerTracker activity when service destroyed.

    DEBUG NOTE:

    According to this post, my foreground service will not end when calling onDestroy on both the
    activity and service when my service is called by onTaskRemoved.
    There is a method I could use which I have commented out within my onTaskRemoved method, however
    it makes a call to the OS and does not perform cleanup afterwards. I didn't think this was
    good practice nor necessary for this coursework.
    Originally I thought it was an issue with unbinding but after debugging it seems to be an issue with the method
    startForeground().

    https://stackoverflow.com/questions/58346054/app-keeps-running-when-foreground-service-is-stopped-last
 */
public class GPSService extends Service {

   public static final String EXTRA_DISTANCE = "com.example.runnertracker.Model.GPSService.EXTRA_DISTANCE";
   public static final String EXTRA_TIME = "com.example.runnertracker.Model.GPSService.EXTRA_TIME";

    private final String CHANNEL_ID = "100";
    private final int NOTIFICATION_ID = 001;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private ArrayList<Location> mLocationsVisited;
    private long startTime = 0;
    private long endTime = 0;
    private NotificationManager notificationManager;
    private IBinder mBinder = new MyBinder();

    private double finalDuration;
    private double finalDistance;
    private Notification notification;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        initializeNotificationManager();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        InitializeJourneyArray();
        getLocation();
        notificationBuilder();
        startForeground(NOTIFICATION_ID, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        }
        notificationManager.cancel(NOTIFICATION_ID);
        fusedLocationClient.removeLocationUpdates(locationCallback);
        mLocationsVisited.clear();
    }

    // ArrayList initialized
    private void InitializeJourneyArray() {
        mLocationsVisited = new ArrayList<>();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public class MyBinder extends Binder {

        public GPSService getService() {
            return GPSService.this;
        }
    }

    private void getLocation() {
        startTravelTime();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = new
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    mLocationsVisited.add(location);
                    Log.d("comp3018", "location +" + location);
                }
            }
        };
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        } catch(SecurityException e) {
            Toast.makeText(this,"This application is lacking location permissions, please enable", Toast.LENGTH_SHORT).show();
        }
    }

    // performing final calculations on distance + time.
    public void saveTravel() {
        finalDuration = totalDuration();
        finalDistance = lengthOfTravel();
        startTime = SystemClock.elapsedRealtime();
        endTime = 0;
    }

    private double lengthOfTravel() {
        if(mLocationsVisited.size() <= 1) {
            return 0;
        }
        int finalLocationIndex = mLocationsVisited.size() - 1;
        return mLocationsVisited.get(0).distanceTo(mLocationsVisited.get(finalLocationIndex))/1000;
        // m conversion to km
    }

    private void startTravelTime() {
        startTime = SystemClock.elapsedRealtime();
        endTime = 0;
    }

    private double totalDuration() {
        if (startTime == 0) {
            return 0.0;
        }

        long currentTime = SystemClock.elapsedRealtime();
        if (endTime != 0) {
            currentTime = endTime;
        }
        long elaspedTime = currentTime - startTime;
        return elaspedTime / 1000.0;

    }

    private void notificationBuilder() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_wb_sunny)
                .setContentTitle("Runner Tracker")
                .setContentText("Keep Running!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notification = mBuilder.build();
    }

    private void initializeNotificationManager() {
        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Runner Tracker";
            String description = "Notifcation channel for running app!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public double getFinalDuration() {
        return finalDuration;
    }

    public double getFinalDistance() {
        return finalDistance;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("comp3018", "ON TASK REMOVED!!");
        stopSelf();
        // android.os.Process.killProcess(android.os.Process.myPid()); -> calls OS
        // could've placed conditional, then called this within an if statement onDestroy
        // but not sure if this is necessary...
    }
}
