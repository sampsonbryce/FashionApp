package com.example.samps_000.fashionapp;

import android.graphics.Bitmap;

/**
 * Created by samps_000 on 5/7/2016.
 */
public class FeedItem {
    public String time;
    public byte[] user_image;
    public byte[] item_pic;
    public String name;
    public String location;
    public String desc;
    public String yes_count;
    public String tips_count;
    public int post_id;
    public FeedItem(){
        super();
    }

    public FeedItem(String name, String time, String location, String desc, byte[] user_image, byte[] item_pic, String yes_count, String tips_count, int post_id){
        super();
        this.name = name;
        this.time = time;
        this.location = location;
        this.user_image = user_image;
        this.item_pic = item_pic;
        this.desc = desc;
        this.yes_count = yes_count;
        this.tips_count = tips_count;
        this.post_id = post_id;
    }
}

