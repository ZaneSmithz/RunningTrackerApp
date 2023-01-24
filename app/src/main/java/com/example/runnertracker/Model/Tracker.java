package com.example.runnertracker.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

// Entity provides schema for database
@Entity(tableName = "tracker_table")
public class Tracker {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private double time;

    @ColumnInfo
    private double km;

    @ColumnInfo
    private String feeling;


    @ColumnInfo
    private String weather;


    @ColumnInfo
    @TypeConverters({Converters.class})
    private Date date;


    public Tracker(double time, double km, String weather, String feeling, Date date) {
        this.time = time;
        this.km = km;
        this.weather = weather;
        this.feeling = feeling;
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double getTime() {
        return time;
    }

    public double getKm() {
        return km;
    }

    public String getFeeling()
    {
        return feeling;
    }

    public String getWeather() {
        return weather;
    }

    public Date getDate()
    {
        return date;
    }


}
