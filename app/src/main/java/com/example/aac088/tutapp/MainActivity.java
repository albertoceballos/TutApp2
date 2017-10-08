package com.example.aac088.tutapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView lv;
    private String addressMaster = "https://tutapp.000webhostapp.com/get_location.php";
    private String location=null,user_id="a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv= (ListView) findViewById(R.id.main_lv);

        Downloader d = new Downloader();
        d.execute();
    }

    public class Downloader extends AsyncTask<Void, Integer, String>{
        private ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setTitle("Downloading");
            pd.setMessage("Downloading... Please wait");
           pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String data=null;
            data = downloadData();
            return data;
        }

        private String downloadData() {
            InputStream is = null;
            String line = null;
            String pass_data=null;

            try {
                URL url = new URL(addressMaster);
                HttpURLConnection con= (HttpURLConnection) url.openConnection();

                is = new BufferedInputStream(con.getInputStream());

                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                StringBuffer sb = new StringBuffer();

                if(br != null){
                    while((line=br.readLine()) != null){
                        sb.append(line+"\n");
                    }
                }else{
                    return null;
                }

                return sb.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                if(is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if(s != null){
                Parser p = new Parser(s);
                p.execute();
            }else{
                Toast.makeText(MainActivity.this,"Unable to download data",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class Parser extends AsyncTask<Void,Integer,Integer>{
        private String data;
        private ProgressDialog pd;
        private ArrayList<String> list= new ArrayList<>();

        Parser(String data){
            this.data=data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setTitle("Parser");
            pd.setMessage("Parsing... Please wait");
            pd.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return parseData();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            pd.dismiss();

            if(integer==1){
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,list);
                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        location=lv.getItemAtPosition(position).toString();
                        Intent intent = new Intent(MainActivity.this,SubjectActivity.class);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("location",location);
                        editor.putString("user_id",user_id);
                        editor.commit();
                        MainActivity.this.startActivity(intent);

                    }
                });

            }else{
                Toast.makeText(MainActivity.this,"Unable to Parse",Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("location",location);
                editor.putString("user_id",user_id);
                editor.commit();
            }
        }

        public int parseData(){
            try {
                System.out.println(data);
                JSONArray ja = new JSONArray(data);
                JSONObject jo = null;

                list.clear();

                for(int i=0;i<ja.length();i++){
                    jo = ja.getJSONObject(i);

                    String location =jo.getString("location");

                    list.add(location);
                }

                return 1;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }
    }
}

