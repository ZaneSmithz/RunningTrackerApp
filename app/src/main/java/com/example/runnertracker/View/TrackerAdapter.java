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
    TrackerAdapter connects to appropriate recyclerView which will render DB tracker information into a render-able list
    we inflate recyclerView with appropriate information based on function passed to ViewModel.

 */
public class TrackerAdapter extends RecyclerView.Adapter<TrackerAdapter.TrackerHolder> {
    private List<Tracker> trackers = new ArrayList<>();
    private OnItemClickListener mListener;

    @NonNull
    @Override
    public TrackerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tracker_item, parent, false);
        return new TrackerHolder(itemView);
    }

    /*
    Loops through all trackers in db, and calls TrackerHolder which will store
    each row of data into list
     */
    @Override
    public void onBindViewHolder(@NonNull TrackerHolder holder, int position) {
        Tracker currentTracker = trackers.get(position);
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

    /*
    Initializes data to be rendered, e.g. is given initial db data
    when onChange is called in MainActivity, trackers will be updated
     */
    public void setTrackers(List<Tracker> trackers) {
        this.trackers = trackers;
        notifyDataSetChanged();
    }

    public Tracker getTrackerAt(int position) {
        return trackers.get(position);
    }

    /*
    TrackerHolder inserts single tracker data into xml List
     */
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (mListener != null && position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(trackers.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Tracker tracker);
    }

    public void setOnItemClickListener(OnItemClickListener listener)  {
        this.mListener = listener;

    }
}
