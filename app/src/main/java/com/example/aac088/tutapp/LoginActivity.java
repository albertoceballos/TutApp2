package com.example.aac088.tutapp;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private Button login_btn;
    private EditText username, password;
    private TextView reg_btn;
    private String user_str,pass_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.login_username_et);
        password = (EditText) findViewById(R.id.login_password_et);

        login_btn= (Button) findViewById(R.id.login_btn);

        reg_btn = (TextView) findViewById(R.id.login_new_user_txtvw);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_str = username.getText().toString();
                pass_str = password.getText().toString();

                validateFields();

            }
        });

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regIntent);
            }
        });
    }

    private void validateFields() {
        if(user_str.isEmpty()){
            Toast.makeText(this,"User Field can't be empty",Toast.LENGTH_SHORT).show();
        }else if(pass_str.isEmpty()){
            Toast.makeText(this,"Password Field can't be empty",Toast.LENGTH_SHORT).show();
        }else{
            Response.Listener<String> responseListener = new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if(success){
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id",user_str);
                            intent.putExtras(bundle);
                            LoginActivity.this.startActivity(intent);

                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage("Username or Password Incorrect")
                                    .setNegativeButton("Retry",null)
                                    .create()
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            LoginRequest loginRequest = new LoginRequest(user_str,pass_str,responseListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(loginRequest);
        }
        }
}
