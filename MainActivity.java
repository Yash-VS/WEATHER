package com.gtappdevelopers.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    int PERMISSION_CODE = 1;
    private TextView cityNameTV, cityTemperatureTV, conditionTV;
    private ImageView iconIV;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private RecyclerView weatherRV;
    private ImageView backIV;
    private WeatherRVAdapter weatherRVAdapter;
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextInputEditText searchBarEdt;
    private ImageView searchIV;
    private String cityName;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_main);
        weatherRVModalArrayList = new ArrayList<>();
        searchBarEdt = findViewById(R.id.idEdtCity);
        searchIV = findViewById(R.id.idIVSearch);
        weatherRV = findViewById(R.id.idRvTemperatures);
        backIV = findViewById(R.id.idIVBack);
        loadingPB = findViewById(R.id.idPBLoading);
        homeRL = findViewById(R.id.idRLHome);
        weatherRVAdapter = new WeatherRVAdapter(weatherRVModalArrayList, this);
        weatherRV.setAdapter(weatherRVAdapter);
        cityTemperatureTV = findViewById(R.id.idTVTemperature);
        cityNameTV = findViewById(R.id.idTVCityName);
        conditionTV = findViewById(R.id.idTVCondition);
        iconIV = findViewById(R.id.idIVIcon);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        //Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        Location location = getLocation();
//        cityName = getLocationName(location.getLatitude(), location.getLongitude());
//          Log.e("TAG","CHECK_______" + location.toString());
//
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = searchBarEdt.getText().toString();
                if (data.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your city", Toast.LENGTH_SHORT).show();
                } else {
                    cityNameTV.setText(data);
                    getWeatherInfo(data);
                }
            }
        });
//        getWeatherInfo(getLocationName(location.getLatitude(), location.getLongitude()));
    }

//    public Location getLocation() {
//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager != null) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
//
//            }
//            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (lastKnownLocationGPS != null) {
//                return lastKnownLocationGPS;
//            } else {
//                Location loc =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//                System.out.println("1::"+loc); //getting null over here
//                System.out.println("2::"+loc.getLatitude());
//                return loc;
//            }
//        } else {
//            return null;
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void getWeatherInfo(String cityName) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=8be38dd8be4a473683c110530210711&q="+cityName+"&days=1&aqi=no&alerts=no";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        cityNameTV.setText(cityName);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", "RESPONSE IS " + response);
                homeRL.setVisibility(View.VISIBLE);
                loadingPB.setVisibility(View.GONE);
                weatherRVModalArrayList.clear();
                try {
                    String temp = response.getJSONObject("current").getString("temp_c");
                    cityTemperatureTV.setText(temp + "°c");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionImg = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    conditionImg = conditionImg.substring(2);
                    Picasso.get().load("http://".concat(conditionImg)).into(iconIV);
                    conditionTV.setText(condition);
                    if (isDay == 1) {
                        //https://www.poynter.org/wp-content/uploads/2019/07/shutterstock_264132746.jpg
                        //https://images.unsplash.com/photo-1566228015668-4c45dbc4e2f5?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80
                        //morning
                        Picasso.get().load("https://www.poynter.org/wp-content/uploads/2019/07/shutterstock_264132746.jpg").into(backIV);
                    } else {
                        Picasso.get().load("https://images.unsplash.com/photo-1505673542670-a5e3ff5b14a3?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxleHBsb3JlLWZlZWR8Mnx8fGVufDB8fHx8&w=1000&q=80").into(backIV);
                    }
                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");
                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRVModalArrayList.add(new WeatherRVModal(time, temper, img, wind));
                    }
                    weatherRVAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter a valid city name..", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    public String getLocationName(double lattitude, double longitude) {
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(lattitude, longitude,
                    10);
            for (Address adrs : addresses) {
                if (adrs != null) {

                    String city = adrs.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                        System.out.println("city ::  " + cityName);
                    } else {
                        Log.e("TAG", "FAIL TO GET CITY");
                    }
                    // // you should also try with addresses.get(0).toSring();

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;

    }
}