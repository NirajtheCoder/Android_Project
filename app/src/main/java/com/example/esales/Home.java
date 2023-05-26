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
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
        ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button start_button = findViewById(R.id.start_button);
        queue = Volley.newRequestQueue(this);
        que = Volley.newRequestQueue(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        String message = preferences.getString("message", "");
        String name = preferences.getString("name", "");
        String access_token = preferences.getString("access_token", "");
        int id = preferences.getInt("id", 0);
        //int user_id = preferences.getInt("id", 0);

        //int lastVisitId = preferences.getInt("last_visit_id", 0);
        //String executive_location = preferences.getString("executive_location", "");
        //int executive_distance_travelled = preferences.getInt("executive_distance_travelled",0);

        text_view.setText(message);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String date = dateFormat.format(calendar.getTime());
        String time = timeFormat.format(calendar.getTime());

       //Toast.makeText(this, String.valueOf(user_id), Toast.LENGTH_SHORT).show();

        if (preferences.contains("access_token")) {
            Intent intent = new Intent(Home.this, Punch.class);
            startActivity(intent);
            finish();
            return;
        }

        String messagess = "Today is <b>\"" + date + "\"</b> and the Time is <b>\"" + time + "\"</b>. Please click on the start button below to start your Sales Adventure.";
        date_time.setText(Html.fromHtml(messagess));
        date_time.setMovementMethod(LinkMovementMethod.getInstance());
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("access_token");
                editor.apply();
                // Start the MainActivity
                Intent intent = new Intent(Home.this, MainActivity.class);
                startActivity(intent);
                finish();

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
                                    //Integer user_id = dataObject.getInt("user_id");
                                    Integer startId = dataObject.getInt("start_id");
                                    Integer start_time = dataObject.getInt("start_time");

                                   /* MyPreferences preferences = new MyPreferences(Home.this);
                                    preferences.setPreferenceInt("id", id);*/
                                    editor.putInt("id", id);
                                    editor.putInt("start_id", startId);
                                    editor.putString("name", name);
                                    editor.putString("access_token", access_token);
                                   // editor.putInt("user_id", user_id);
                                    editor.putInt("start_time", start_time);
                                    editor.putString("latitude", latitude);
                                    editor.putString("longitude", longitude);

                                    Intent intent1 = new Intent(Home.this, Punch.class);
                                    startActivity(intent1);
                                    finish();

                                } else {
                                    // handle false status
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(Home.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                            new Response.ErrorListener() {
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
                StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("status");
                            if (status) {
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.apply();
                                finish();
                                Intent intent1 = new Intent(Home.this, MainActivity.class);
                                startActivity(intent1);
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
   /* @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString(KEY_MY_TEXT,.getText().toString();
    }*/
}
