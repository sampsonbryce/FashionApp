package com.example.samps_000.fashionapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends ArrayAdapter<FeedItem> {
    private final LruCache<String, Bitmap> userCache;
    private final LruCache<String, Bitmap> itemCache;
    LayoutInflater inflater;
    Context context;
    int layoutResourceId;
    FeedItem[] data = null;

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static Bitmap decodeSampledBitmapFromByteArray(byte[] decoded_image,
                                                          int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(decoded_image, 0, decoded_image.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(decoded_image, 0, decoded_image.length, options);
    }

    public FeedAdapter(Context context, int resource, FeedItem[] data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.inflater = ((Activity) context).getLayoutInflater();
        this.data = data;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        userCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        itemCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        //Set image size to phone size
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        int size;

        if (height > width){
            size = width;
        }
        else{
            size = height;
        }

        for (int i = 0; i < data.length;i++) {
            FeedItem item = data[i];
            byte[] decoded_user_image = item.user_image;
            byte[] decoded_item_image = item.item_pic;

            Bitmap user_bitmap = decodeSampledBitmapFromByteArray(decoded_user_image, size/6, size/6);
            Bitmap item_bitmap = decodeSampledBitmapFromByteArray(decoded_item_image, size, size);

            addBitmapToMemoryCache(String.valueOf(i), user_bitmap, userCache);
            addBitmapToMemoryCache(String.valueOf(i), item_bitmap, itemCache);
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap, LruCache cache) {
        if (getBitmapFromMemCache(key, cache) == null) {
            cache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key, LruCache cache) {
        return (Bitmap) cache.get(key);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FeedItemHolder holder = null;
        if (row == null) {
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new FeedItemHolder();
            holder.user_image_view = (ImageView) row.findViewById(R.id.userProfPic);
            holder.user_name_view = (TextView) row.findViewById(R.id.userName);
            holder.location_view = (TextView) row.findViewById(R.id.locationText);
            holder.time_view = (TextView) row.findViewById(R.id.timeText);
            holder.description_view = (TextView) row.findViewById(R.id.descriptionText);
            holder.feed_image_view = (ImageView) row.findViewById(R.id.feedPic);
            holder.yes_count_view = (TextView) row.findViewById(R.id.yesText);
            holder.tips_count_view = (TextView) row.findViewById(R.id.tipsText);
            row.setTag(holder);
        } else {
            holder = (FeedItemHolder) row.getTag();
        }

        final FeedItem item = data[position];

        holder.user_image_view.setImageBitmap(getBitmapFromMemCache(String.valueOf(position), userCache));
        holder.user_name_view.setText(item.name);
        holder.location_view.setText(item.location);
        holder.time_view.setText(item.time);
        holder.description_view.setText(item.desc);
        holder.feed_image_view.setImageBitmap(getBitmapFromMemCache(String.valueOf(position), itemCache));
        holder.post_id = item.post_id;
        String yes_count = "";
        String tips_count = "";
        if (item.yes_count != "0"){
            yes_count = item.yes_count;
        }
        if (item.tips_count != "0"){
            tips_count = item.tips_count;
        }
        holder.yes_count_view.setText(yes_count);
        holder.tips_count_view.setText(tips_count);
        return row;
    }

    static class FeedItemHolder {
        ImageView user_image_view;
        TextView description_view;
        TextView user_name_view;
        TextView location_view;
        TextView time_view;
        ImageView feed_image_view;
        TextView tips_count_view;
        TextView yes_count_view;
        int post_id;
    }
}

    /*

    private static class BitmapWorkerParams{
        byte[] decoded_image;
        String key;

        BitmapWorkerParams(byte[] decoded_image, String key){
            this.decoded_image = decoded_image;
            this.key = key;
        }
    }

    class BitmapWorkerTask extends AsyncTask<BitmapWorkerParams, Void, Bitmap> {
        // Decode image in background.

        @Override
        protected Bitmap doInBackground(BitmapWorkerParams... params) {
            final Bitmap bitmap = decodeSampledBitmapFromByteArray(params[0].decoded_image, 100, 100);
            addBitmapToMemoryCache(String.valueOf(params[0].key), bitmap);
            return bitmap;
        }
    }

    public void loadBitmap(int position, ImageView imageView) {
        final String imageKey = String.valueOf(position);

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            BitmapWorkerParams params = new BitmapWorkerParams(imageKey)
            BitmapWorkerTask task = new BitmapWorkerTask(mImageView);
            task.execute();
        }
    }
    */
