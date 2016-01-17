package com.example.samps_000.fashionapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by samps_000 on 1/13/2016.
 */
public class DataFromServerExample extends Activity {


    ArrayList<HashMap<Integer, String>> People = new ArrayList<HashMap<Integer, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_data_example);
    }

    public void createClicked(View view){
        Log.d("checks", "check1");
        new CheckUsername().execute();
    }

    protected class CheckUsername extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            Log.d("checks", "check2");

            URL url = null;
            InputStream in = null;
            String text = null;
            JSONObject jObject;
            JSONArray data;


            try {
                url = new URL("http://10.0.2.2/PHPProjects/DBConnectTest3.php");
            } catch (MalformedURLException e) {
                Log.d("Expetion", "MalformedURL: " + e.toString());
            }


            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (java.io.IOException e) {
                Log.d("Expetion", "IOException: " + e.toString());
            }

            Log.d("checks", "check3");

            try {
                text = readStream(in);
            }
                finally{
                try {
                    in.close();
                }catch(java.io.IOException e){
                    Log.d("Excpetion", "IOExceptionClose: " + e.toString());
                }
            }

            Log.d("results", text);

            try {
                jObject = new JSONObject(text);
                data = jObject.getJSONArray("People");
                for(int i = 0; i < data.length(); i++) {
                    JSONObject Person = data.getJSONObject(i);
                    HashMap<Integer, String> h = new HashMap<>();
                    h.put(Integer.parseInt(Person.getString("id")), Person.getString("name"));
                    People.add(h);
                }
            }catch (org.json.JSONException e){
                Log.d("Exception", "JSONException: " + e.toString());
            }

            Log.d("results", People.toString());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.d("checks", "check4");
            final TextView ID1 = (TextView) findViewById(R.id.ID1);
            final TextView Name1 = (TextView) findViewById(R.id.Name2);
            final TextView ID2 = (TextView) findViewById(R.id.ID1);
            final TextView Name2 = (TextView) findViewById(R.id.Name2);

            ArrayList<TextView> TV = new ArrayList<>();

            TV.add(ID1);
            TV.add(Name1);
            TV.add(ID2);
            TV.add(Name2);

            Log.d("checks", "check5");



            //Setting part doesn't really work
            int a = 0;
            for(int i = 0; i < People.size();i++){

                HashMap<Integer, String> h = People.get(i);

                ArrayList<Integer> keys = new ArrayList<>(h.keySet());

                Log.d("error", keys.toString());

                TV.get(a).setText(keys.get(0).toString());
                TV.get(a+1).setText(h.get(i+1));
                a += 2;
            }

            Log.d("checks", "check6");
        }

        private String readStream(InputStream is) {
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while (i != -1) {
                    bo.write(i);
                    i = is.read();
                }
                return bo.toString();
            } catch (IOException e) {
                return "";
            }
        }
    }
}
