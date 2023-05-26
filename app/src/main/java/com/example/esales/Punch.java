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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
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
    String url = "http://esales.zeetsoftserve.com/api/v1/punch_arrived";
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
        ActivityCompat.requestPermissions(Punch.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        int user_id = preferences.getInt("id", 0);
        int startId = preferences.getInt("start_id", 0);
        int lastVisitId = preferences.getInt("last_visit_id", 0);
        String name = preferences.getString("name", "");
        String access_token = preferences.getString("access_token", "");

        //String executiveLocation = preferences.getString("executive_location", "");
        //String executiveDistanceTravelled = preferences.getString("executive_distance_travelled", "");
       /* int user_id = preferences.getInt("user_id", 0);
        int startId = preferences.getInt("start_id", 0);
        int id = preferences.getInt("id", 0);
        String name = preferences.getString("name", "");
        String access_token = preferences.getString("access_token", "");*/

        if (preferences.contains("access_token")) {
            Intent intent = new Intent(Punch.this, Exit.class);
            startActivity(intent);
            finish();
            return;
        }

        username.setText("Hi" + " " + name);
        queue = Volley.newRequestQueue(this);
        String message = "You have reached a <b>New Destination</b> If you want the information of your visit to be added to your sales adventure for the day please <b>PUNCH</b> in using the button below.";
        destination.setText(Html.fromHtml(message));
        punch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.complete_meeting, null);

                EditText executiveLocationEditText = popupView.findViewById(R.id.location);
                EditText executiveDistanceTravelledEditText = popupView.findViewById(R.id.dis_edit);

                int wid = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 370, getResources().getDisplayMetrics());
                int high = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
                boolean focus = true;
                PopupWindow popupWindow = new PopupWindow(popupView, wid, high, focus);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                Button popupButton = popupView.findViewById(R.id.submit);

                popupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String executiveLocation = executiveLocationEditText.getText().toString();
                        String distance = executiveDistanceTravelledEditText.getText().toString();

                        /*if (executiveLocation.isEmpty()) {
                            executiveLocationEditText.setError("location name is required");
                            executiveLocationEditText.requestFocus();
                        } else if (distance.isEmpty()) {
                            executiveDistanceTravelledEditText.setError("enter distance");
                            executiveDistanceTravelledEditText.requestFocus();*/

                            Intent intent = new Intent(Punch.this, Exit.class);
                            startActivity(intent);

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
                                            Intent intent1 = new Intent(Punch.this, Exit.class);
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

                                    params.put("user_id", String.valueOf(user_id));
                                    params.put("start_id", String.valueOf(startId));
                                    params.put("last_visit_id", String.valueOf(lastVisitId));
                                    params.put("arrived_time", arrived_time);
                                    params.put("destination_latitude", destination_latitude);
                                    params.put("destination_longitude", destination_longitude);
                                    params.put("executive_location", executiveLocation);
                                    params.put("executive_distance_travelled", String.valueOf(distance));
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
                                destination_latitude = String.valueOf(Double.parseDouble(String.valueOf(lat)));
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
        });

    }


}