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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
public class CreatePost extends Activity implements OnServerCallCompleted {

    public static final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView postPicture;

    private Bitmap postImage = null;

    private static final String POST_EXT = "/post";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);




        postPicture = (ImageView) findViewById(R.id.postImage);
    }

    public void selectImageClicked(View view) {
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

        if (resultCode == RESULT_OK) {
            //everything went ok

            Log.d("checks", "check5");

            if (requestCode == IMAGE_GALLERY_REQUEST) {
                //if we are here, results are from image gallery

                //address of image on sd card
                Uri imageURI = data.getData();
                //declare a string to read image data from sd card
                InputStream inputStream;

                //getting input stream based on URI
                try {
                    inputStream = getContentResolver().openInputStream(imageURI);

                    //get a bitmap from the stream
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    Bitmap image = BitmapFactory.decodeStream(inputStream, null, options);

                    //Set image
                    postPicture.setImageBitmap(image);

                    postImage = image;

                } catch (FileNotFoundException e) {
                    Log.d("Exception", "File not found: " + e.toString());
                }
            }
        }
    }

    public void postClicked(View view) {
        //get description
        EditText descEdit = (EditText) findViewById(R.id.descText);

        //get image
        String imageEncoded = encodeToString(postImage);
        postImage.recycle();
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("image", imageEncoded);

            jObject.put("desc", descEdit.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ServerCall(CreatePost.this).execute(jObject.toString(), POST_EXT, "POST");
    }

    private String encodeToString(final Bitmap b) {
        //Might need to make this run on thread

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        b.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        String encodedString = Base64.encodeToString(byte_arr, 0);
        return encodedString;
    }

    @Override
    public void PerformResponse(int status) {
        if (status == 200){
            Toast.makeText(CreatePost.this, "POSTED", Toast.LENGTH_SHORT).show();

        }
        else if(status == 500){
            Toast.makeText(CreatePost.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
            Log.d("Status Code: ", Integer.toString(status));
        }
        else{
            Toast.makeText(CreatePost.this, "Could not post! :(", Toast.LENGTH_SHORT).show();
            Log.d("Status Code: ", Integer.toString(status));
        }
    }
}
