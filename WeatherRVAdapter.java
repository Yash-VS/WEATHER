package com.gtappdevelopers.weatherapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private Context context;

    public WeatherRVAdapter(ArrayList<WeatherRVModal> weatherRVModalArrayList, Context context) {
        this.weatherRVModalArrayList = weatherRVModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
        WeatherRVModal modal = weatherRVModalArrayList.get(position);

        holder.temperatureTV.setText(modal.getTemperature() + "°c");
        holder.windTV.setText(modal.getWindSpeed() + " Km/h");
        String img = modal.getIcon();
        img = img.substring(2);
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(modal.getTime());
            holder.timeTV.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("TAG", "IMG URL IS " + img);
        Picasso.get().load("http://".concat(img)).into(holder.iconIV);
    }

    @Override
    public int getItemCount() {
        return weatherRVModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView timeTV, temperatureTV, windTV;
        private ImageView iconIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTV = itemView.findViewById(R.id.idTVTime);
            temperatureTV = itemView.findViewById(R.id.idTVTemperature);
            windTV = itemView.findViewById(R.id.idTVWindSpeed);
            iconIV = itemView.findViewById(R.id.idIVCondition);
        }
    }
}
