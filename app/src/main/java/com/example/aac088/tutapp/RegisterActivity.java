package com.example.aac088.tutapp;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText name,email,password,confirm,type;
    private Button reg_btn;
    private String name_str, email_str, pass_str, confirm_str, type_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toolbar = (Toolbar) findViewById(R.id.reg_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText) findViewById(R.id.reg_name_et);
        email = (EditText) findViewById(R.id.reg_email_et);
        password = (EditText) findViewById(R.id.reg_pass_et);
        confirm = (EditText) findViewById(R.id.reg_confirm_et);
        type = (EditText) findViewById(R.id.reg_type_et);

        reg_btn = (Button) findViewById(R.id.reg_reg_btn);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_str = name.getText().toString();
                email_str = email.getText().toString();
                pass_str = password.getText().toString();
                confirm_str = confirm.getText().toString();
                type_str = type.getText().toString();

                validateFields();
            }
        });
    }

    private void validateFields() {
        if(name_str.isEmpty()){
            Toast.makeText(this,"Name can't be empty",Toast.LENGTH_SHORT).show();
        }else if(email_str.isEmpty()){
            Toast.makeText(this,"Email can't be empty",Toast.LENGTH_SHORT).show();
        }else if(pass_str.isEmpty()){
            Toast.makeText(this,"Password can't be empty",Toast.LENGTH_SHORT).show();
        }else if(confirm_str.isEmpty()){
            Toast.makeText(this,"Confirm password can't be empty",Toast.LENGTH_SHORT).show();
        }else if(type_str.isEmpty()){
            Toast.makeText(this,"Type of User can't be empty",Toast.LENGTH_SHORT).show();
        }else if(!pass_str.equals(confirm_str)){
            Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show();
        }else{
            Response.Listener<String> responseListener = new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if(success){
                            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                            RegisterActivity.this.startActivity(intent);
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setMessage("Registration failed");
                            builder.setNegativeButton("Retry",null);
                            builder.create();
                            builder.show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            RegisterRequest registerRequest = new RegisterRequest(name_str,email_str,pass_str,type_str,responseListener);
            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
            queue.add(registerRequest);
        }
        }

}
