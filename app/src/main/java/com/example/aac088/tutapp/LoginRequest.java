package com.example.aac088.tutapp;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aac088 on 10/8/2017.
 */

public class LoginRequest extends StringRequest{
    private static final String LOGIN_REQUEST_URL = "https://tutapp.000webhostapp.com/login.php";
    private Map<String, String> params;

    public LoginRequest(String email,String password,Response.Listener<String> listener){
        super(Method.POST,LOGIN_REQUEST_URL,listener,null);
        params = new HashMap<>();
        params.put("email",email);
        params.put("password",password);
    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }
}
