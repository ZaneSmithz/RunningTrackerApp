package com.example.runnertracker.View;

import static com.example.runnertracker.View.TrackerInspector.TRACKER_EMPTY_STRING;
import static com.example.runnertracker.View.TrackerInspector.TRACKER_FEELING_BAD;
import static com.example.runnertracker.View.TrackerInspector.TRACKER_FEELING_GOOD;
import static com.example.runnertracker.View.TrackerInspector.TRACKER_WEATHER_RAINY;
import static com.example.runnertracker.View.TrackerInspector.TRACKER_WEATHER_SUNNY;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runnertracker.Model.Tracker;
import com.example.runnertracker.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
    Performs same functionality as TrackerAdapter
    See TrackerAdapter for explanation
 */
public class TrackerDistanceAdapter extends RecyclerView.Adapter<TrackerDistanceAdapter.TrackerHolder> {

    private List<Tracker> trackers = new ArrayList<>();

    @NonNull
    @Override
    public TrackerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.furthest_distance, parent, false);
        return new TrackerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackerHolder holder, int position) {
        Tracker currentTracker = trackers.get(0);
        holder.textViewTime.setText(secondsFormat(currentTracker.getTime()));
        holder.textViewKm.setText(distanceFormat(currentTracker.getKm()));
        holder.textViewDate.setText(dateFormat(currentTracker.getDate()));

        switch (currentTracker.getFeeling()) {
            case TRACKER_FEELING_GOOD:
                holder.imageViewThumbsDown.setVisibility(View.INVISIBLE);
                holder.imageViewThumbsUp.setVisibility(View.VISIBLE);
                break;
            case TRACKER_FEELING_BAD:
                holder.imageViewThumbsUp.setVisibility(View.INVISIBLE);
                holder.imageViewThumbsDown.setVisibility(View.VISIBLE);
                break;
            case TRACKER_EMPTY_STRING:
                holder.imageViewThumbsDown.setVisibility(View.INVISIBLE);
                holder.imageViewThumbsUp.setVisibility(View.INVISIBLE);
                break;
            }
        switch (currentTracker.getWeather()) {
            case TRACKER_WEATHER_SUNNY:
                holder.imageViewRain.setVisibility(View.INVISIBLE);
                holder.imageViewSunny.setVisibility(View.VISIBLE);
                break;
            case TRACKER_WEATHER_RAINY:
                holder.imageViewSunny.setVisibility(View.INVISIBLE);
                holder.imageViewRain.setVisibility(View.VISIBLE);
                break;
            case TRACKER_EMPTY_STRING:
                holder.imageViewSunny.setVisibility(View.INVISIBLE);
                holder.imageViewRain.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private String secondsFormat(double seconds) {
        Date date = new Date((long)(seconds*1000));
        return new SimpleDateFormat("mm:ss").format(date);
    }

    private  String distanceFormat(double km) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(km);
    }

    private String dateFormat(Date date) {
        return new SimpleDateFormat("dd/MM/yy").format(date);
    }

    @Override
    public int getItemCount() {
        return trackers.size();
    }

    public void setTrackers(List<Tracker> trackers) {
        this.trackers = trackers;
        notifyDataSetChanged();
    }

    class TrackerHolder extends RecyclerView.ViewHolder {
        private TextView textViewTime;
        private TextView textViewKm;
        private TextView textViewDate;
        private ImageView imageViewThumbsUp;
        private ImageView imageViewThumbsDown;
        private ImageView imageViewSunny;
        private ImageView imageViewRain;


        public TrackerHolder(@NonNull View itemView) {
            super(itemView);
            textViewTime = itemView.findViewById(R.id.text_view_time);
            textViewKm = itemView.findViewById(R.id.text_view_km);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            imageViewThumbsUp = itemView.findViewById(R.id.image_view_thumbs_up);
            imageViewThumbsDown = itemView.findViewById(R.id.image_view_thumbs_down);
            imageViewSunny = itemView.findViewById(R.id.image_view_sunny);
            imageViewRain = itemView.findViewById(R.id.image_view_rain);
        }
    }
}

