package com.example.samps_000.fashionapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedAdapter extends ArrayAdapter<FeedItem> {
    Context context;
    int layoutResourceId;
    FeedItem[] data = null;

    public FeedAdapter(Context context, int resource, FeedItem[] data) {
        super(context, resource, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FeedItemHolder holder = null;
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new FeedItemHolder();
            holder.user_image_view = (ImageView)row.findViewById(R.id.userProfPic);
            holder.user_name_view = (TextView)row.findViewById(R.id.userName);
            holder.location_view = (TextView)row.findViewById(R.id.locationText);
            holder.time_view = (TextView)row.findViewById(R.id.timeText);
            holder.feed_image_view = (ImageView) row.findViewById(R.id.feedPic);
            row.setTag(holder);
        }
        else
        {
            holder = (FeedItemHolder) row.getTag();
        }

        FeedItem item = data[position];
        holder.user_image_view.setImageBitmap(item.user_image);
        holder.user_name_view.setText(item.name);
        holder.location_view.setText(item.location);
        holder.time_view.setText(item.time);
        holder.feed_image_view.setImageBitmap(item.item_pic);
        return row;
    }

    static class FeedItemHolder {
    ImageView user_image_view;
    TextView user_name_view;
    TextView location_view;
    TextView time_view;
    ImageView feed_image_view;
    }
}

