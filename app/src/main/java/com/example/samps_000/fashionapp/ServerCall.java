package com.example.samps_000.fashionapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by samps_000 on 2/23/2016.
 */
public class ServerCall extends AsyncTask<String, Void, List> {
    private static final int HTTP_BAD_REQUEST = 400;
    private OnServerCallCompleted listener;

    public ServerCall(OnServerCallCompleted listener){
        this.listener = listener;
    }


    @Override
    protected List doInBackground(String... params) {
        Log.d("checkValues", params[0]);
        Log.d("checkValues", params[1]);
        Log.d("checkValues", params[2]);

        String json_string = params[0];
        String ext = params[1];
        String req_method = null;
        if (params.length > 2) {
            req_method = params[2];
        }

        String response;
        int status;
        List<String> list = new ArrayList<>();

        String SERVER_URL = "http://ec2-52-33-232-213.us-west-2.compute.amazonaws.com:3000";

        InputStreamReader is;
        HttpURLConnection conn;

        try {
            Log.d("checks", "Create URL: " + SERVER_URL + ext);
            URL url = new URL(SERVER_URL + ext);

            Log.d("checks", "Create new connection");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /*milliseconds*/);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setFixedLengthStreamingMode(json_string.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json");
            if (req_method != null) {
                conn.setRequestMethod(req_method);
            }


            Log.d("checks", "CONN: " + conn.toString());
            OutputStream os = conn.getOutputStream();
            Log.d("checks", "OS: " + os.toString());
            os.write(json_string.getBytes("UTF-8"));

            Log.d("checks", "Write Json object in string");

            os.flush();
            os.close();
            Log.d("CHECK", json_string);
            Log.d("CHECK", os.toString());

            Log.d("checks", "Connect");
            conn.connect();

            //Get Response

            Log.d("checks", "Get Response");
            status = conn.getResponseCode();
            Log.d("checks", "STATUS:" + status);
            list.add(Integer.toString(status));
            Log.d("checks", "conn" + conn);
            if (status >= HTTP_BAD_REQUEST){
                is = new InputStreamReader(conn.getErrorStream());
            }
            else {
                is = new InputStreamReader(conn.getInputStream());
            }
            BufferedReader reader = new BufferedReader(is);

            String line;

            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            // Response from server after create process will be stored in response variable.
            Log.d("checks", "Convert response to string");
            response = sb.toString();
            list.add(response);
            Log.d("Response: ", response);


        } catch (MalformedURLException e) {
            //e.printStackTrace();
            Log.d("Exceptions", "M_URL_E: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Exceptions", "IO_E: " + e.toString());
        }
        return list;
    }

    @Override
    protected void onPostExecute(List response_list) {
        int status = Integer.valueOf((String) response_list.get(0));
        String response = null;
        if (response_list.size() < 2){
            Log.d("errors", "No response string from server");
        }
        else {
            response = (String) response_list.get(1);
            Log.d("response", String.valueOf(status) + " " + response);
        }
        if (response_list.size() > 1 && response == null) {
            Log.d("Errors", "response == null");
        } else {
            listener.PerformResponse(status);
        }
    }
}

