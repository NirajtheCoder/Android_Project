package com.example.esales;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String url = "http://esales.zeetsoftserve.com/api/v1/login";
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        EditText usernameEditText = findViewById(R.id.user_name);
        EditText passwordEditText = findViewById(R.id.pass_word);
        Button loginButton = findViewById(R.id.Log_in);
        queue = Volley.newRequestQueue(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        if (preferences.contains("access_token")) {
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            finish();
            return;
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            /*    if (preferences.contains("access_token")) {
                    Intent intent = new Intent(MainActivity.this, Home.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Please login first", Toast.LENGTH_SHORT).show();
                }*/

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.isEmpty()) {
                    usernameEditText.setError("Email is required.");
                    usernameEditText.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    usernameEditText.setError("Enter Valid Email");
                    usernameEditText.requestFocus();
                } else if (password.isEmpty()) {
                    passwordEditText.setError("Password is required.");
                    passwordEditText.requestFocus();
                } else if (password.length() < 6) {
                    passwordEditText.setError("Password must be at least 6 characters long.");
                    passwordEditText.requestFocus();
                } else {

                    StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean status = jsonObject.getBoolean("status");

                                if (status) {
                                    String message = jsonObject.getString("message");
                                    String access_token = jsonObject.getString("access_token");
                                    JSONObject dataObject = jsonObject.getJSONObject("data");

                                    int id = dataObject.getInt("id");
                                    String name = dataObject.getString("name");
                                    String email = dataObject.getString("email");

                                    JSONArray lastVisitArray = jsonObject.getJSONArray("last_visit");
                                    if (lastVisitArray.length() > 0) {

                                        JSONObject lastVisitObject = lastVisitArray.getJSONObject(0);

                                        int lastVisitId = lastVisitObject.getInt("id");
                                        int startId = lastVisitObject.getInt("start_id");
                                        String destinationLatitude = lastVisitObject.getString("destination_latitude");
                                        String destinationLongitude = lastVisitObject.getString("destination_longitude");
                                        String executiveLocation = lastVisitObject.getString("executive_location");
                                        String executiveDistanceTravelled = lastVisitObject.getString("executive_distance_travelled");
                                        String arrivedTime = lastVisitObject.getString("arrived_time");
                                        int meetingStatus = lastVisitObject.getInt("meeting_status");

                                        // Store the values in SharedPreferences
                                        editor.putString("message", message);
                                        editor.putString("access_token", access_token);
                                        editor.putInt("id", id);
                                        editor.putString("name", name);
                                        editor.putString("email", email);
                                        editor.putInt("last_visit_id", lastVisitId);
                                        editor.putInt("start_id", startId);
                                        editor.putString("destination_latitude", destinationLatitude);
                                        editor.putString("destination_longitude", destinationLongitude);
                                        editor.putString("executive_location", executiveLocation);
                                        editor.putString("executive_distance_travelled", executiveDistanceTravelled);
                                        editor.putString("arrived_time", arrivedTime);
                                        editor.putInt("meeting_status", meetingStatus);
                                        editor.apply();

                                    }

                                    Intent intent = new Intent(MainActivity.this, Home.class);
                                    startActivity(intent);
                                    finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.d("Error.Response", error.getMessage());
                            Toast.makeText(MainActivity.this, "Error.Response " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("email", username);
                            params.put("password", password);
                            return params;
                        }
                    };
                    queue.add(postRequest);
                }
            }
        });
    }
}