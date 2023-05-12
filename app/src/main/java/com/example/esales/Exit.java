package com.example.esales;

import static com.example.esales.R.id.date_set;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Exit extends AppCompatActivity {
private  TextView date_set;
private EditText PersonName,dis_editText,phone,email,desig,emp_name,note_input;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        date_set = (TextView) findViewById(R.id.date_set);
        PersonName = (EditText) findViewById(R.id.PersonName);
        dis_editText =(EditText) findViewById(R.id.dis_editText);
        emp_name = (EditText) findViewById(R.id.emp_name);
        phone = (EditText)findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        desig = (EditText) findViewById(R.id.desig);
        note_input = (EditText)findViewById(R.id.note_input);
        Button save = findViewById(R.id.save);
        getSupportActionBar().hide();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        String date = dateFormat.format(calendar.getTime());
        date_set.setText(date);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String p_name = PersonName.getText().toString();
                String distance = dis_editText.getText().toString();
                String employe_name = emp_name.getText().toString();
                String p_phone = phone.getText().toString();
                String e_email = email.getText().toString();
                String d_desig = desig.getText().toString();
                String notes = note_input.getText().toString();

                if (p_name.isEmpty()){
                    PersonName.setError("location name is required");
                    PersonName.requestFocus();
                }  else if (distance.isEmpty()) {
                    dis_editText.setError("Distance is required");
                    dis_editText.requestFocus();
                }else if (employe_name.isEmpty()) {
                    emp_name.setError("name is required");
                    emp_name.requestFocus();
                }
                else if (p_phone.isEmpty()) {
                    phone.setError("mobile number is required");
                    phone.requestFocus();
                } else if (e_email.isEmpty()) {
                    email.setError("Email is required.");
                    email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(e_email).matches()) {
                    email.setError("Enter Valid Email");
                    email.requestFocus();
                } else if (d_desig.isEmpty()) {
                    desig.setError("designation is required");
                    desig.requestFocus();
                } else if (notes.isEmpty()) {
                    note_input.setError("notes required");
                    note_input.requestFocus();
                }

            }
        });

    }
}