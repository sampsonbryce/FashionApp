package com.example.samps_000.fashionapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by samps_000 on 1/15/2016.
 */
public class Login extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText password = (EditText) findViewById(R.id.loginPassword);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());
    }

    public void createLinkClicked(View view){
        Intent i = new Intent(Login.this, CreateAccount.class);
        startActivity(i);
    }

    public void loginClicked(View view){

        new CheckLogin().execute();
    }

    protected class CheckLogin extends AsyncTask<Void, Void, Void>{

        EditText Email = (EditText)findViewById(R.id.loginEmail);
        EditText Pass = (EditText)findViewById(R.id.loginPassword);

        String EmailText = Email.getText().toString();
        String PassText = Pass.getText().toString();

        String response = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            checkInput(Email, Pass);
        }

        @Override
        protected Void doInBackground(Void... params) {

            OutputStream os = null;
            InputStreamReader is = null;
            HttpURLConnection conn = null;

            try {
                Log.d("checks", "check1");
                URL url = new URL("http://10.0.2.2/PHPProjects/Login.php");
                JSONObject jObject = new JSONObject();
                jObject.put("Email", EmailText);
                jObject.put("Pass", PassText);

                String text = jObject.toString();

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /*milliseconds*/);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(text.getBytes().length);

                Log.d("checks", "check2");

                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                conn.connect();

                os = new BufferedOutputStream(conn.getOutputStream());
                os.write(text.getBytes());

                os.flush();


                //Get Response
                is = new InputStreamReader(conn.getInputStream());
                BufferedReader reader = new BufferedReader(is);

                String line = "";

                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                // Response from server after create process will be stored in response variable.
                response = sb.toString();

                Log.d("checks", "check3");

                Log.d("Response", response);


            } catch (MalformedURLException e) {
                //e.printStackTrace();
                Log.d("Exceptions", "M_URL_E: " + e.toString());
            } catch (JSONException e) {
                //e.printStackTrace();
                Log.d("Exceptions", "JSON_E: " + e.toString());
            } catch (IOException e) {
                //e.printStackTrace();
                Log.d("Exceptions", "IO_E: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String[] data = response.split("\\:");

            String top = null;
            String id = null;

            if(data.length > 1) {

                top = data[0];
                id = data[1];
            }

            if (top != null && top.equals("Account Exists")){
                //Start App
                Intent i = new Intent(Login.this, Feed.class);
                Log.d("Verification", response);
            }
            else{
                Log.d("Errors", response);
            }

        }

        private boolean checkInput(EditText email, EditText pass) {


            ArrayList<EditText> data = new ArrayList<>();

            data.add(email);
            data.add(pass);

            boolean fieldsFilled = true;

            for (int i = 0; i < 2; i++) {
                //Log.d("CheckData", data.get(i).getText().toString());
                if (data.get(i).getText().toString().matches("")) {
                    fieldsFilled = false;
                }
            }

            return fieldsFilled;
        }
    }
}
