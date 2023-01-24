package com.example.runnertracker.Model;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Date;

/*
    Intialize database and prepopulate with determined figures.
 */
@Database(entities = Tracker.class, exportSchema = false, version = 1)
public abstract class TrackerDatabase extends RoomDatabase {

    private static TrackerDatabase instance;
    public abstract TrackerDao trackerDao();

    public static synchronized TrackerDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), TrackerDatabase.class,
                    "tracker_table")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new populateDBAsyncTask(instance).execute();
        }
    };

    private static class populateDBAsyncTask extends AsyncTask<Void, Void, Void> {
        private TrackerDao trackerDao;

        private populateDBAsyncTask(TrackerDatabase db) {
            trackerDao = db.trackerDao();

        }
        @Override
        protected Void doInBackground(Void... voids) {
            Date date = new Date(2020, 02, 12);

            trackerDao.insert(new Tracker(94.23, 5.55, "Rainy", "Good", date));
            trackerDao.insert(new Tracker(81.23, 7.32, "Rainy", "Good", date));
            trackerDao.insert(new Tracker(45.31, 2.4, "Sunny", "Bad", new Date(2021, 12, 1)));
            return null;
        }
    }
}
