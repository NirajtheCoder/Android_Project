package com.example.esales;

import static android.location.LocationManager.GPS_PROVIDER;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Punch extends AppCompatActivity {
    String url= "http://esales.zeetsoftserve.com/api/v1/punch_arrived";
    private LocationManager locationManager;
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private String destination_latitude;
    private String destination_longitude;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch);

        getSupportActionBar().hide();
        TextView username = findViewById(R.id.username);
        TextView destination = findViewById(R.id.destination);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button punch_button = findViewById(R.id.punch_button);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(Punch.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);

        Intent intent1 = getIntent();

       /* final String[] destination_latitude = {intent1.getStringExtra("latitude")};
        final String[] destination_longitude = {intent1.getStringExtra("longitude")};*/
        String id = intent1.getStringExtra("id");
        String start_id = intent1.getStringExtra("start_id");
        String user_id= intent1.getStringExtra("user_id");
        String name = intent1.getStringExtra("name");
        username.setText("Hi"+" " + name);
        String access_token = intent1.getStringExtra("access_token");

        //username.setText(id);
        queue = Volley.newRequestQueue(this);

        String message = "You have reached a <b>New Destination</b> If you want the information of your visit to be added to your sales adventure for the day please <b>PUNCH</b> in using the button below.";
        destination.setText(Html.fromHtml(message));
        punch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                //check gps enable or not
                if (!locationManager.isProviderEnabled(GPS_PROVIDER)) {
                    onGps();

                } else {
                    getLocation();
                }

                Date now = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String arrived_time = format.format(now);

                StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("status");

                            if (status) {
                                JSONObject data = jsonObject.getJSONObject("data");
                              /*  String id = data.getString("id");
                                String start_time = data.getString("start_time");*/
                                Intent intent1 = new Intent(Punch.this, Exit.class);
                               /* intent1.putExtra("start_id", start_id);
                                intent1.putExtra("user_id", user_id);
                                intent1.putExtra("arrived_time", arrived_time);
                                intent1.putExtra("executive_location", executive_location);
                                intent1.putExtra("destination_latitude", destination_latitude);
                                intent1.putExtra("destination_longitude", destination_longitude);
                                intent1.putExtra("executive_distance_travelled",executive_distance_travelled);
                                intent1.putExtra("id",id);*/
                                startActivity(intent1);
                            } else {
                                // handle false status
                                String message = jsonObject.getString("message");
                                Toast.makeText(Punch.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.d("Error.Response", error.getMessage());
                                Toast.makeText(Punch.this, "Error.Response " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("start_id", start_id);
                        params.put("user_id", user_id);
                        params.put("arrived_time", arrived_time);
                        params.put("destination_latitude", destination_latitude);
                        params.put("destination_longitude", destination_longitude);
                        params.put("id",id);
                        return params;
                    }
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " + access_token);
                        //headers.put("Content-Type", "application/x-www-form-urlencoded");
                        return headers;
                    }
                };

                Intent intent = new Intent(Punch.this,Exit.class);
                startActivity(intent);

                queue.add(postRequest);
            }
            private void getLocation() {
                if (ActivityCompat.checkSelfPermission(Punch.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(Punch.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Punch.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
                } else {
                    android.location.Location LocationGps = locationManager.getLastKnownLocation(GPS_PROVIDER);
                    android.location.Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if (LocationGps != null) {
                        double lat = LocationGps.getLatitude();
                        double log = LocationGps.getLongitude();
                        //longitude = String.valueOf(location.getLongitude());
                        destination_latitude= String.valueOf(Double.parseDouble(String.valueOf(lat)));
                        destination_longitude = String.valueOf(Double.parseDouble(String.valueOf(log)));
                    } else if (locationNetwork != null) {
                        double lat = locationNetwork.getLatitude();
                        double log = locationNetwork.getLongitude();

                        destination_latitude = String.valueOf(lat);
                        destination_longitude = String.valueOf(log);
                    } else if (LocationPassive != null) {
                        double lat = LocationPassive.getLatitude();
                        double log = LocationPassive.getLongitude();
                        //longitude = String.valueOf(location.getLongitude());
                        destination_latitude = String.valueOf(Double.parseDouble(String.valueOf(lat)));
                        destination_longitude = String.valueOf(Double.parseDouble(String.valueOf(log)));
                    }
                }
            }


            private void onGps() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Punch.this);
                builder.setMessage("Enable Gps").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        });

    }


}