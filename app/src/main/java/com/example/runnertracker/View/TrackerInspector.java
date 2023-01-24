package com.example.runnertracker.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.runnertracker.R;

import java.util.Objects;

public class TrackerInspector extends AppCompatActivity {

    RadioGroup mRadioGroupWeather;
    RadioGroup mRadioGroupFeeling;
    RadioButton mRadioButtonWeather;
    RadioButton mRadioButtonFeeling;
    private String mWeatherText;
    private String mFeelingText;

    public static final String EXTRA_FEELING = "com.example.runnertracker.EXTRA_FEELING";
    public static final String EXTRA_WEATHER = "com.example.runnertracker.EXTRA_WEATHER";
    public static final String TRACKER_WEATHER_SUNNY = "Sunny";
    public static final String TRACKER_WEATHER_RAINY = "Rainy";
    public static final String TRACKER_FEELING_GOOD = "Good";
    public static final String TRACKER_FEELING_BAD = "Bad";
    public static final String TRACKER_EMPTY_STRING = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_inspector);
        mWeatherText = TRACKER_WEATHER_SUNNY;
        mFeelingText = TRACKER_FEELING_GOOD;
        mRadioGroupWeather = findViewById(R.id.radioGroup_Weather);
        mRadioGroupFeeling = findViewById(R.id.radioGroup_Feeling);
    }

    public void checkFeelingButton(View v) {
        int radioIdFeeling = mRadioGroupFeeling.getCheckedRadioButtonId();
        mRadioButtonFeeling = findViewById(radioIdFeeling);
        mFeelingText = (String) mRadioButtonFeeling.getText();
        Log.d("comp3018", "Selected Radio Button: " + mFeelingText);
    }

    public void checkWeatherButton(View v) {
        int radioIdWeather = mRadioGroupWeather.getCheckedRadioButtonId();
        mRadioButtonWeather = findViewById(radioIdWeather);
        mWeatherText = (String) mRadioButtonWeather.getText();
        Log.d("comp3018", "Selected Radio Button: " + mWeatherText);
    }

    public void ButtonSubmit(View v) {
        if(!Objects.equals(mFeelingText, TRACKER_EMPTY_STRING) || !Objects.equals(mWeatherText, TRACKER_EMPTY_STRING)) {
            Intent intent = new Intent(TrackerInspector.this, MainActivity.class);
            intent.putExtra(EXTRA_WEATHER, mWeatherText);
            intent.putExtra(EXTRA_FEELING, mFeelingText);
            Log.d("comp3018", "Before Intent gets sent " + mWeatherText);
            Log.d("comp3018", "Before Intent gets sent " + mFeelingText);
            setResult(RESULT_OK, intent);
            finish();
        }
        else {
            Toast.makeText(this, "Ensure you fill out both fields", Toast.LENGTH_SHORT).show();
        }


    }
}