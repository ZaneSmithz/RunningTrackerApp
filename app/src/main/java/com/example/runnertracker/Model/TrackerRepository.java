package com.example.runnertracker.Model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/*
    Allow for async methods of CRUD operations.
 */
public class TrackerRepository {

    private TrackerDao trackerDao;
    private LiveData<List<Tracker>> allTrackers;

    public TrackerRepository(Application application) {
        TrackerDatabase trackerDatabase = TrackerDatabase.getInstance(application);
        trackerDao = trackerDatabase.trackerDao();
        allTrackers = trackerDao.getAllTrackers();
    }

    public void insert(Tracker tracker) { new InsertTrackerAsyncTask(trackerDao).execute(tracker); }

    public void update(Tracker tracker) { new UpdateTrackerAsyncTask(trackerDao).execute(tracker); }

    public void delete(Tracker tracker) {
        new DeleteTrackerAsyncTask(trackerDao).execute(tracker);
    }

    public LiveData<List<Tracker>> getMaxDistance() { return trackerDao.getMaxDistance(); }

    public LiveData<List<Tracker>> getAllTrackers() {
        return allTrackers;
    }

    private static class InsertTrackerAsyncTask extends AsyncTask<Tracker, Void, Void> {
        private TrackerDao trackerDao;

        private InsertTrackerAsyncTask(TrackerDao trackerDao) {
            this.trackerDao = trackerDao;
        }

        @Override
        protected Void doInBackground(Tracker... trackers) {
            trackerDao.insert(trackers[0]);
            return null;
        }
    }

    private static class UpdateTrackerAsyncTask extends AsyncTask<Tracker, Void, Void> {
        private TrackerDao trackerDao;

        private UpdateTrackerAsyncTask(TrackerDao trackerDao) {
            this.trackerDao = trackerDao;
        }

        @Override
        protected Void doInBackground(Tracker... trackers) {
            trackerDao.update(trackers[0]);
            return null;
        }
    }

    private static class DeleteTrackerAsyncTask extends AsyncTask<Tracker, Void, Void> {
        private TrackerDao trackerDao;

        private DeleteTrackerAsyncTask(TrackerDao trackerDao) {
            this.trackerDao = trackerDao;
        }

        @Override
        protected Void doInBackground(Tracker... trackers) {
            trackerDao.delete(trackers[0]);
            return null;
        }
    }
}
