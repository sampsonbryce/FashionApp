package com.example.samps_000.fashionapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by samps_000 on 1/16/2016.
 */
public class CreatePost extends Activity {

    public static final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView postPicture;


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
        if(resultCode == RESULT_OK){
            //everything went ok

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
                } catch (FileNotFoundException e) {
                    Log.d("Exception", "File not found: " + e.toString());
                }
            }
        }
    }
}
