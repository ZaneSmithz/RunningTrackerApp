package com.example.runnertracker.Model;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// Dao provides functions/queries to retrieve/insert/delete select data from database
@Dao
public interface TrackerDao {

    @Insert
    void insert(Tracker tracker);

    @Update
    void update(Tracker tracker);

    @Delete
    void delete(Tracker tracker);

    @Query("SELECT * FROM tracker_table ORDER BY km DESC LIMIT 1")
    LiveData<List<Tracker>> getMaxDistance();

    @Query("SELECT * FROM tracker_table ORDER BY id DESC")
    LiveData<List<Tracker>> getAllTrackers();


}
