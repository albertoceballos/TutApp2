package com.example.aac088.tutapp;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aac088 on 10/8/2017.
 */

public class RegisterRequest extends StringRequest{
    private static final String REGISTER_REQUEST_URL = "https://tutapp.000webhostapp.com/register.php";
    private Map<String,String> params;

    public RegisterRequest(String name, String email, String password,String type, Response.Listener<String> listener){
        super(Method.POST,REGISTER_REQUEST_URL,listener,null);
        params = new HashMap<>();
        params.put("email",email);
        params.put("name",name);
        params.put("type",type);
        params.put("password",password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
