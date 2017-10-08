package com.example.aac088.tutapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SubjectActivity extends AppCompatActivity {
    private ListView lv;
    private Toolbar toolbar;
    private String SubjectAddress = "https://tutapp.000webhostapp.com/get_subject.php";
    private String user_id,location,subject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_list);
        toolbar = (Toolbar) findViewById(R.id.tutorlist_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Available Tutors");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lv = (ListView) findViewById(R.id.tutorlist_lv);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        location = preferences.getString("location",null);

        Downloader d = new Downloader();
        d.execute();
    }

    public class Downloader extends AsyncTask<Void, Integer, String> {
        private ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SubjectActivity.this);
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
                pass_data = URLEncoder.encode("location","UTF-8")
                        + "=" + URLEncoder.encode(location,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL(SubjectAddress);
                HttpURLConnection con= (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                if(pass_data != null){
                    wr.write(pass_data);
                    wr.flush();
                }else{
                    Toast.makeText(SubjectActivity.this,"Error passing data to server", Toast.LENGTH_SHORT).show();
                }

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
                SubjectActivity.Parser p = new SubjectActivity.Parser(s);
                p.execute();
            }else{
                Toast.makeText(SubjectActivity.this,"Unable to download data",Toast.LENGTH_SHORT).show();
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
            pd = new ProgressDialog(SubjectActivity.this);
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SubjectActivity.this,android.R.layout.simple_list_item_1,list);
                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        subject=lv.getItemAtPosition(position).toString();
                        Intent intent = new Intent(SubjectActivity.this,TutorActivity.class);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SubjectActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("subject",subject);
                        editor.putString("location",location);
                        editor.putString("user_id",user_id);
                        editor.commit();
                        SubjectActivity.this.startActivity(intent);

                    }
                });

            }else{
                Toast.makeText(SubjectActivity.this,"Unable to Parse",Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SubjectActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("subject",subject);
                editor.putString("location",location);
                editor.putString("user_id",user_id);
                editor.commit();
            }
        }

        public int parseData(){
            try {
                JSONArray ja = new JSONArray(data);
                JSONObject jo = null;

                list.clear();

                for(int i=0;i<ja.length();i++){
                    jo = ja.getJSONObject(i);

                    String subject =jo.getString("subject");

                    list.add(subject);
                }

                return 1;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }
    }
}
