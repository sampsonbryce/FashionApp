package com.example.samps_000.fashionapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by samps_000 on 1/16/2016.
 */
public class CreatePost extends Activity {

    public static final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView postPicture;

    private Bitmap postImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postPicture = (ImageView) findViewById(R.id.postImage);
    }

    public void selectImageClicked(View view){
        //invoke the image gallery using an implicit intent
        Intent imageSelector = new Intent(Intent.ACTION_PICK);

        //where to look for image data
        File imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String imageDirectoryPath = imageDirectory.getPath();

        //get URI representation
        Uri data = Uri.parse(imageDirectoryPath);

        imageSelector.setDataAndType(data, "image/*");

        startActivityForResult(imageSelector, IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("checks", "check4");

        if(resultCode == RESULT_OK){
            //everything went ok

            Log.d("checks", "check5");

            if(requestCode == IMAGE_GALLERY_REQUEST){
                //if we are here, results are from image gallery


                //address of image on sd card
                Uri imageURI = data.getData();
                //declare a string to read image data from sd card
                InputStream inputStream;

                //getting input stream based on URI
                try {
                    inputStream = getContentResolver().openInputStream(imageURI);

                    //get a bitmap from the stream
                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    //Set image
                    postPicture.setImageBitmap(image);

                    postImage = image;

                } catch (FileNotFoundException e) {
                    Log.d("Exception", "File not found: " + e.toString());
                }
            }
        }
    }

    public void postClicked(View view){

        String imageEncoded = encodeToString(postImage);
        new sendToServer().execute(imageEncoded);
    }

    private String encodeToString(final Bitmap b){
        //Might need to make this run on thread

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        b.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        String encodedString = Base64.encodeToString(byte_arr, 0);
        return encodedString;
    }


    protected class sendToServer extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... params) {

            OutputStream os = null;
            InputStreamReader is = null;
            HttpURLConnection conn = null;

            try {
                Log.d("checks", "check1");
                URL url = new URL("http://10.0.2.2/PHPProjects/s3/upload.php");
                JSONObject jObject = new JSONObject();

                if (params[0] != null)
                    Log.d("image", params[0]);
                else
                    Log.d("image", "No params");

                jObject.put("image", params[0]);

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
                String response = sb.toString();

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


    }
}
