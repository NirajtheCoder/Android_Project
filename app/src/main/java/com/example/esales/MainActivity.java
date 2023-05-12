package com.example.esales;

import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {
    String url= "http://esales.zeetsoftserve.com/api/v1/login";
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

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
                                    JSONObject dataObject = jsonObject.getJSONObject("data");
                                    String message = jsonObject.getString("message");
                                    String access_token = jsonObject.getString("access_token");
                                    String name = dataObject.getString("name");
                                    Integer id = dataObject.getInt("id");
                                    Intent intent = new Intent(MainActivity.this, Home.class);
                                    intent.putExtra("message", message);
                                    intent.putExtra("access_token",access_token);
                                    intent.putExtra("id",id);
                                    intent.putExtra("name",name);
                                    startActivity(intent);
                                   // String name = jsonObject.getString("name");
                                   /* Intent check = new Intent(MainActivity.this, Home.class);
                                    check.putExtra("Extra_Message",name);
                                    startActivity(check);*/

                                } else {
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.d("Error.Response", error.getMessage());
                            Toast.makeText(MainActivity.this, "Error.Response "+error.getMessage(), Toast.LENGTH_SHORT).show();
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