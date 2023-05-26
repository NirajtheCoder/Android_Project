package com.example.esales;

import static com.example.esales.R.id.date_set;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Exit extends AppCompatActivity {
    String url = "http://esales.zeetsoftserve.com/api/v1/meeting_details";
    RequestQueue queue;
    private TextView date_set;
    private EditText phone_num, email_id, desig, emp_name, note_input;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        queue = Volley.newRequestQueue(this);
       /* if (queue == null) {
            queue = Volley.newRequestQueue(Exit.this);
        }*/

        date_set = findViewById(R.id.date_set);
        emp_name = findViewById(R.id.emp_name);
        phone_num = findViewById(R.id.phone_num);
        email_id = findViewById(R.id.email_id);
        desig = findViewById(R.id.desig);
        note_input = findViewById(R.id.note_input);
        Button save = findViewById(R.id.save);
        String phoneNumberPattern = "\\d{10}";
        getSupportActionBar().hide();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        String date = dateFormat.format(calendar.getTime());
        date_set.setText(date);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        int user_id = preferences.getInt("id", 0);
        int visit_id = preferences.getInt("last_visit_id", 0);
        String executive_location_name = preferences.getString("executive_location", "");
        //int id = preferences.getInt("id",0);
        String access_token = preferences.getString("access_token", "");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = emp_name.getText().toString();
                String phone = phone_num.getText().toString();
                String email = email_id.getText().toString();
                String designation = desig.getText().toString();
                String notes = note_input.getText().toString();

                if (name.isEmpty()) {
                    emp_name.setError("Name is required");
                    emp_name.requestFocus();
                } else if (name.length() >= 25) {
                    emp_name.setError("Please enter less than 25 characters in the user name");
                    emp_name.requestFocus();
                } else if (phone.isEmpty()) {
                    phone_num.setError("Phone number is required");
                    phone_num.requestFocus();
                } else if (!phone.matches(phoneNumberPattern)) {
                    phone_num.setError("Invalid phone number. Please enter a 10-digit number");
                    phone_num.requestFocus();
                } else if (email.isEmpty()) {
                    email_id.setError("Email is required");
                    email_id.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    email_id.setError("Invalid email. Please enter a valid email address");
                    email_id.requestFocus();
                } else if (designation.isEmpty()) {
                    desig.setError("Designation is required");
                    desig.requestFocus();
                } else if (notes.isEmpty()) {
                    note_input.setError("Notes required");
                    note_input.requestFocus();
                } else try {
                    {

                     /*   editor.putString("employe_name", employe_name);
                        editor.putString("p_phone", p_phone);
                        editor.putString("e_email", e_email);
                        editor.putString("d_desig", d_desig);
                        editor.putString("notes", notes);
                        editor.apply();
*/
                       /* Intent intent = new Intent(Exit.this, Exit_Arive.class);
                        startActivity(intent);
                        finish();*/
                        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean status = jsonObject.getBoolean("status");

                                    if (status) {
                                        JSONArray data = jsonObject.getJSONArray("data");
                                        if (data.length() > 0) {
                                            JSONObject firstData = data.getJSONObject(0);

                                            String id = firstData.getString("id");
                                            String user_id = firstData.getString("user_id");
                                            String visit_id = firstData.getString("visit_id");
                                            String executive_location_name = firstData.getString("executive_location_name");


                                            Intent intent = new Intent(Exit.this, Exit_Arive.class);
                                            intent.putExtra("id", id);
                                            intent.putExtra("user_id", user_id);
                                            intent.putExtra("visit_id", visit_id);
                                            intent.putExtra("executive_location_name", executive_location_name);
                                            editor.putString("employe_name", name);
                                            editor.putString("p_phone", phone);
                                            editor.putString("e_email", email);
                                            editor.putString("d_desig", designation);
                                            editor.putString("notes", notes);
                                            editor.apply();
                                            startActivity(intent);

                                        } else {
                                            // handle empty data array
                                            Toast.makeText(Exit.this, "No data available", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // handle false status
                                        String message = jsonObject.getString("message");
                                        Toast.makeText(Exit.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error.Response", error.getMessage());
                                Toast.makeText(Exit.this, "Error.Response " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();

                                //params.put("id", String.valueOf(id));
                                params.put("user_id", String.valueOf(user_id));
                                params.put("visit_id", String.valueOf(visit_id));
                                params.put("executive_location_name", executive_location_name);
                                params.put("name", (name));
                                params.put("designation", designation);
                                params.put("phone", (phone));
                                params.put("email", (email));
                                params.put("notes", notes);
                                return params;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Authorization", "Bearer " + access_token);
                                return headers;
                            }
                        };
                        queue.add(postRequest);

                /*Intent intent = new Intent(Exit.this, Exit_Arive.class);
                startActivity(intent);
                finish();*/
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}