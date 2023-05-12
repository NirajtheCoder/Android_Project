package com.example.esales;

import static android.location.LocationManager.GPS_PROVIDER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity {


    String url = "http://esales.zeetsoftserve.com/api/v1/logout";
    String link = "http://esales.zeetsoftserve.com/api/v1/start_day";
    private LocationManager locationManager;
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private RequestQueue queue;
    private String latitude;
    private String longitude;
    private RequestQueue que;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        ImageView logout_img = findViewById(R.id.logout_img);
        TextView text_view = findViewById(R.id.text_view);
        TextView date_time = findViewById(R.id.date_time);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(Home.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button start_button = findViewById(R.id.start_button);
        queue = Volley.newRequestQueue(this);
        que = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        String messages = intent.getStringExtra("message");
        String access_token = intent.getStringExtra("access_token");
        String name = intent.getStringExtra("name");
        //String messages = intent.getStringExtra("messages");
        //String id = getIntent().getStringExtra("id");
        int id = intent.getIntExtra("id", 0);

        text_view.setText(messages);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String date = dateFormat.format(calendar.getTime());
        String time = timeFormat.format(calendar.getTime());

        String messagess = "Today is <b>\"" + date + "\"</b> and the Time is <b>\"" + time + "\"</b>. Please click on the start button below to start your Sales Adventure.";


        date_time.setText(Html.fromHtml(messagess));
        date_time.setMovementMethod(LinkMovementMethod.getInstance());

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Date now = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String start_time = format.format(now);

                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    //check gps enable or not
                    if (!locationManager.isProviderEnabled(GPS_PROVIDER)) {
                        onGps();

                    } else {
                        getLocation();
                    }


                    StringRequest stringRequest1 = new StringRequest(Request.Method.POST, link, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean status = jsonObject.getBoolean("status");

                                if (status) {
                                    JSONObject dataObject = jsonObject.getJSONObject("data");
                                    Integer id = dataObject.getInt("id");
                                    Integer user_id = dataObject.getInt("user_id");
                                    Integer start_id = dataObject.getInt("start_id");
                                    Integer start_time = dataObject.getInt("start_time");


                                    Intent intent1 = new Intent(Home.this, Punch.class);

                                    intent1.putExtra("id", id);
                                    intent1.putExtra("user_id", user_id);
                                    intent1.putExtra("start_time", start_time);
                                    intent1.putExtra("start_id",start_id);
                                    intent1.putExtra("latitude", latitude);
                                    intent1.putExtra("longitude", longitude);
                                    intent1.putExtra("name", name);
                                    intent1.putExtra("access_token", access_token);
                                    startActivity(intent1);
                                } else {
                                    // handle false status
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(Home.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error.networkResponse != null && error.networkResponse.statusCode == 101) {
                                Intent intent = new Intent(Home.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.d("Error.Response", error.getMessage());
                                Toast.makeText(Home.this, "Error.Response " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("user_id", String.valueOf(id));
                            params.put("start_time", start_time);
                            params.put("latitude", latitude);
                            params.put("longitude", longitude);
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
                    que.add(stringRequest1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            private void getLocation() {
                if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
                } else {
                    Location LocationGps = locationManager.getLastKnownLocation(GPS_PROVIDER);
                    Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if (LocationGps != null) {
                        double lat = LocationGps.getLatitude();
                        double log = LocationGps.getLongitude();
                        //longitude = String.valueOf(location.getLongitude());
                        latitude = String.valueOf(Double.parseDouble(String.valueOf(lat)));
                        longitude = String.valueOf(Double.parseDouble(String.valueOf(log)));
                    } else if (locationNetwork != null) {
                        double lat = locationNetwork.getLatitude();
                        double log = locationNetwork.getLongitude();

                        latitude = String.valueOf(lat);
                        longitude = String.valueOf(log);
                    } else if (LocationPassive != null) {
                        double lat = LocationPassive.getLatitude();
                        double log = LocationPassive.getLongitude();
                        //longitude = String.valueOf(location.getLongitude());
                        latitude = String.valueOf(Double.parseDouble(String.valueOf(lat)));
                        longitude = String.valueOf(Double.parseDouble(String.valueOf(log)));
                    }
                }
            }

            private void onGps() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
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

        logout_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getSharedPreferences("Authorization", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("status");

                            if (status) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("access_token");
                                editor.apply();

                                Intent intent = new Intent(Home.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(Home.this, message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                        Toast.makeText(Home.this, "Error.Response " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + access_token);
                        return headers;
                    }
                };
                queue.add(postRequest);
            }
        });
    }

}
