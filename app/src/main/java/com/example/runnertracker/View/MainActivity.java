package com.example.runnertracker.View;

import static com.example.runnertracker.Model.GPSService.EXTRA_DISTANCE;
import static com.example.runnertracker.Model.GPSService.EXTRA_TIME;
import static com.example.runnertracker.View.TrackerInspector.EXTRA_FEELING;
import static com.example.runnertracker.View.TrackerInspector.EXTRA_WEATHER;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runnertracker.R;
import com.example.runnertracker.Model.Tracker;
import com.example.runnertracker.ViewModel.TrackerViewModel;

import java.util.Date;
import java.util.List;

/*
    MainActivity renders all runs using RecyclerView and appropriate adapters
    Receives tracker information from RunnerTracker activity, then inserts this information to the database
    Only instance of trackerViewModel is initialized here.

    APP FEATURES:
     - Stores Run Duration and Distance
     - Stores furthest distance and time
     - Swipe to delete times
     - Click to update with annotations
     - GPS location runs in background

     Room database used to store GPS and user annotation data.
     MVVM architecture implemented.

     if GPS service does not get destroyed onTaskRemoved -> see comments on GPSService.
 */

public class MainActivity extends AppCompatActivity {
    private TrackerViewModel trackerViewModel;

    public static final int EDIT_TRACKER_REQUEST = 5;
    public static final int ADD_TRACKER_REQUEST = 6;

    private int mViewTrackerId = -1;
    private double mViewTrackerKm = -1;
    private double mViewTrackerTime = -1;
    private Date mViewTrackerDate = null;
    private TrackerAdapter adapter;
    private TrackerDistanceAdapter distanceAdapter;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewFurthestDistance;



    /*
        - RecyclerViews set up + initialized to viewModel + correct adapter
        - ItemTouchHelper determines if RecyclerView list was swiped left/right -> deletes selected tracker
        - adapter assigned onClick listener to determine if list item was tapped -> sends intent to TrackerInspector

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iconLauncherFix();

        recyclerView = findViewById(R.id.recycler_view);
        initializeRecyclerView(recyclerView);

        adapter = new TrackerAdapter();
        recyclerView.setAdapter(adapter);

        recyclerViewFurthestDistance = findViewById(R.id.recycler_view_furthest_distance);
        initializeRecyclerView(recyclerViewFurthestDistance);

        distanceAdapter = new TrackerDistanceAdapter();
        recyclerViewFurthestDistance.setAdapter(distanceAdapter);

        setObservers();
        swipeToDelete();
        clickToUpdate();
    }

    private void initializeRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    private void setObservers() {
        trackerViewModel = ViewModelProviders.of(this).get(TrackerViewModel.class);

        trackerViewModel.getAllTrackers().observe(this, new Observer<List<Tracker>>() {
            @Override
            public void onChanged(List<Tracker> trackers) {
                adapter.setTrackers(trackers);
            }
        });

        trackerViewModel.getMaxDistance().observe(this, new Observer<List<Tracker>>() {
            @Override
            public void onChanged(List<Tracker> trackers) {
                distanceAdapter.setTrackers(trackers);
            }
        });
    }

    public void launchAddNewTracker(View view) {
        Intent intent = new Intent(MainActivity.this, RunnerTracker.class);
        startActivityForResult(intent, ADD_TRACKER_REQUEST);
    }

    private void swipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                trackerViewModel.delete(adapter.getTrackerAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this,"Tracker Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void clickToUpdate() {
        adapter.setOnItemClickListener(new TrackerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Tracker tracker) {
                mViewTrackerId = tracker.getId();
                mViewTrackerKm = tracker.getKm();
                mViewTrackerTime = tracker.getTime();
                mViewTrackerDate = tracker.getDate();
                Intent intent = new Intent(MainActivity.this, TrackerInspector.class);
                startActivityForResult(intent, EDIT_TRACKER_REQUEST);

            }
        });
    }

    private void iconLauncherFix(){
        if (!isTaskRoot()   // Fixes bug when application does not resume from current activity on click of icon launcher
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
    }

    /*
        Returning data from RunnerTracker which gives tracker information
        from recent request to service.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_TRACKER_REQUEST && resultCode == RESULT_OK) {
            String duration = data.getStringExtra(EXTRA_TIME);
            String distance = data.getStringExtra(EXTRA_DISTANCE);

            // passed as a double for schema
            double actualDuration = Double.parseDouble(duration);
            double actualDistance = Double.parseDouble(distance);
            Date date = new Date();

            // new tracker inserted into database
            Tracker tracker = new Tracker(actualDuration, actualDistance, "", "", date);
            trackerViewModel.insert(tracker);

            Toast.makeText(this, "Run Saved!", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == EDIT_TRACKER_REQUEST && resultCode == RESULT_OK) {
            if(mViewTrackerId == -1 || mViewTrackerTime == -1 || mViewTrackerKm == -1 || mViewTrackerDate == null) {
                Toast.makeText(this, "Tracker can not be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String feeling = data.getStringExtra(EXTRA_FEELING);
            String weather = data.getStringExtra(EXTRA_WEATHER);
            Tracker tracker = new Tracker(mViewTrackerTime, mViewTrackerKm, weather, feeling, mViewTrackerDate);

            tracker.setId(mViewTrackerId);
            trackerViewModel.update(tracker);

            setTrackerMemberVariablesNull();
            Toast.makeText(this, "Run Updated!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTrackerMemberVariablesNull() {
        mViewTrackerDate = null;
        mViewTrackerId = -1;
        mViewTrackerTime = -1;
        mViewTrackerKm = -1;
    }
}