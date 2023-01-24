package com.example.runnertracker.ViewModel;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.runnertracker.Model.Tracker;
import com.example.runnertracker.Model.TrackerRepository;

import java.util.List;

/*
    Calls repository for access to database functionality.
 */
public class TrackerViewModel extends AndroidViewModel {

    private TrackerRepository trackerRepository;
    private LiveData<List<Tracker>> allTrackers;
    private LiveData<List<Tracker>> mGetMaxDistance;

    public TrackerViewModel(@NonNull Application application) {
        super(application);
        trackerRepository = new TrackerRepository(application);
        allTrackers = trackerRepository.getAllTrackers();
        mGetMaxDistance = trackerRepository.getMaxDistance();
    }

    public void insert(Tracker tracker) {
        trackerRepository.insert(tracker);
    }
    public void update(Tracker tracker) {
        trackerRepository.update(tracker);
    }
    public void delete(Tracker tracker) {trackerRepository.delete(tracker);}
    public LiveData<List<Tracker>> getAllTrackers() {
        return allTrackers;
    }
    public LiveData<List<Tracker>> getMaxDistance() { return mGetMaxDistance; }
}
