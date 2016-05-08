package com.example.samps_000.fashionapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by samps_000 on 5/7/2016.
 */
public class FeedItem {
    public String time;
    public Bitmap user_image;
    public String name;
    public String location;
    public Bitmap item_pic;
    public FeedItem(){
        super();
    }

    public FeedItem(String name, String time, String location, Bitmap user_image, Bitmap item_pic){
        super();
        this.name = name;
        this.time = time;
        this.location = location;
        this.user_image = user_image;
        this.item_pic = item_pic;
    }
}

