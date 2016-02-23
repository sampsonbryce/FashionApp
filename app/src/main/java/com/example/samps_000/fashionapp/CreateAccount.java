package com.example.samps_000.fashionapp;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by samps_000 on 1/7/2016.
 */
public class CreateAccount extends Activity {

    String SERVER_URL = "http://ec2-52-33-232-213.us-west-2.compute.amazonaws.com";
    String CREATE_ACCOUNT_EXT = "/create_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    public void createClicked(View view) {

        new Create().execute();
    }

    protected class Create extends AsyncTask<Void, Void, Void> {

        String response = null;
        int status;

        final EditText Name = (EditText) findViewById(R.id.name);
        final EditText Email = (EditText) findViewById(R.id.email);
        final EditText Uname = (EditText) findViewById(R.id.username);
        final EditText Pass = (EditText) findViewById(R.id.password);
        final EditText Cpass = (EditText) findViewById(R.id.confirmPassword);
        final String NameText = Name.getText().toString();
        final String EmailText = Email.getText().toString();
        final String UnameText = Uname.getText().toString();
        final String PassText = Pass.getText().toString();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            errors();
        }

            @Override
            protected Void doInBackground (Void... params){

            InputStreamReader is = null;
            HttpURLConnection conn = null;

            try {
                Log.d("checks", "Create URL: " + SERVER_URL + CREATE_ACCOUNT_EXT);
                URL url = new URL(SERVER_URL + CREATE_ACCOUNT_EXT);
                JSONObject jObject = new JSONObject();
                jObject.put("Name", NameText);
                jObject.put("Email", EmailText);
                jObject.put("Uname", UnameText);
                jObject.put("Pass", PassText);

                String text = jObject.toString();

                Log.d("checks", "Create new connection");
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /*milliseconds*/);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(text.getBytes().length);


                conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                //os = new BufferedOutputStream(conn.getOutputStream());
                OutputStream os = conn.getOutputStream();
                os.write(text.getBytes("UTF-8"));

                Log.d("checks", "Write Json object in string");

                //os.write(text.getBytes());
                os.flush();
                os.close();
                Log.d("CHECK", text);
                Log.d("CHECK", os.toString());

                Log.d("checks", "Connect");
                conn.connect();

                //Get Repsonse

                Log.d("checks", "Get Response");
                status = conn.getResponseCode();
                is = new InputStreamReader(conn.getInputStream());
                BufferedReader reader = new BufferedReader(is);

                String line = "";

                StringBuilder sb = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                // Response from server after create process will be stored in response variable.
                Log.d("checks", "Convert response to string");
                response = sb.toString();
                Log.d("Response: ", response);



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
            reply();
        }

        private void errors(){
            if (!checkInput(Name, Email, Uname, Pass, Cpass))
            {
                Toast.makeText(CreateAccount.this, "Incomplete Information", Toast.LENGTH_SHORT).show();

            } else if (!Pass.getText().toString().equals(Cpass.getText().toString()))

            {
                Log.d("PASSWORDS", "p1: " + Pass.getText().toString() + " pass2: " + Cpass.getText().toString() + "|");
                Toast.makeText(CreateAccount.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
            }
        }

        private void reply() {
            if (response == null){
                Log.d("Errors", "response == null");
            }
            else if (status != 200)
            {
                Toast.makeText(CreateAccount.this, "Could not create account. :(", Toast.LENGTH_SHORT).show();
                Log.d("Status Code: ", Integer.toString(status));
            }
            else
            {
                Toast.makeText(CreateAccount.this, "Account Created", Toast.LENGTH_SHORT).show();
            }
        }


        private boolean checkInput(EditText name, EditText email, EditText uname, EditText pass, EditText cpass) {


            ArrayList<EditText> data = new ArrayList<>();

            data.add(name);
            data.add(email);
            data.add(uname);
            data.add(pass);
            data.add(cpass);

            boolean fieldsFilled = true;

            for (int i = 0; i < 5; i++) {
                //Log.d("CheckData", data.get(i).getText().toString());
                if (data.get(i).getText().toString().matches("")) {
                    fieldsFilled = false;
                }
            }


            return fieldsFilled;
        }

    }

}

