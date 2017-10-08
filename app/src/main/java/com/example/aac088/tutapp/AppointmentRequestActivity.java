package com.example.aac088.tutapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AppointmentRequestActivity extends AppCompatActivity {
    private Button request_btn;
    private EditText name,email,date;
    private String subject_str,name_str,email_str,date_str;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_request);
        name = (EditText) findViewById(R.id.apnt_request_name_et);
        email = (EditText) findViewById(R.id.apnt_request_email_et);
        date = (EditText) findViewById(R.id.apnt_date_et);
        toolbar = (Toolbar) findViewById(R.id.apnt_request_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Appointment Request");


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        subject_str = preferences.getString("subject",null);


        request_btn = (Button) findViewById(R.id.apnt_request_btn);

        request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_str = name.getText().toString();
                String email_str = email.getText().toString();
                String date_str = date.getText().toString();


                //PASS DATA
                Response.Listener<String> responseListener = new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if(success){
                            Toast.makeText(AppointmentRequestActivity.this, "Appointment Scheduled Successfully", Toast.LENGTH_SHORT).show();

                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentRequestActivity.this);
                            builder.setMessage("Something went wrong with your request try again")
                                    .setNegativeButton("Retry",null)
                                    .create()
                                    .show();
                            }
                        } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            AppointmentRequest loginRequest = new AppointmentRequest(name_str,email_str,subject_str,date_str,responseListener);
            RequestQueue queue = Volley.newRequestQueue(AppointmentRequestActivity.this);
                queue.add(loginRequest);
        }
        });
    }
}
