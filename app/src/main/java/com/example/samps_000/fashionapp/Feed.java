package com.example.samps_000.fashionapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.samps_000.fashionapp.FeedAdapter.FeedItemHolder;

public class Feed extends AppCompatActivity {

    private static String GET_FEED_EXT = "/get_feed";
    private static String YES_CLICKED_EXT = "/yes_clicked";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        JodaTimeAndroid.init(this);

        setTitle("InStyle");
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#26d9d9")));
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.action_layout);
        Window window = this.getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(Color.parseColor("#26d9d9"));
        }

        OkhttpServerRequest request_object = new OkhttpServerRequest(this, GET_FEED_EXT);
        Request request = request_object.getRequest();

        request_object.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("FAILURE", "failed to call " + GET_FEED_EXT + " " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONArray arr = null;
                if (response.code() == 200) {
                    try {
                        String response_info = response.body().string();
                        arr = new JSONArray(response_info);
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                    if (arr != null) {
                        try {
                            populateAdapter(getFeedItems(arr));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    Log.d("FAILED", response.message());
                }
            }
        });
    }

    public void feedIconClicked(View view){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void populateAdapter(final List<FeedItem> feed_items) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FeedItem[] feed_data_array = new FeedItem[feed_items.size()];
                feed_data_array = feed_items.toArray(feed_data_array);

                FeedAdapter feed_adapter = new FeedAdapter(Feed.this, R.layout.feed_list_item, feed_data_array);

                ListView feed_list_view = (ListView) findViewById(R.id.feedListView);

                feed_list_view.setAdapter(feed_adapter);
            }
        });

    }

    private List<FeedItem> getFeedItems(JSONArray data) throws JSONException {
        List<FeedItem> feed_data = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject cur_object = data.getJSONObject(i);
            String encodedImage = (String) cur_object.get("image");
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

            String location = (String) cur_object.get("location");
            String time = timeDifference((String) cur_object.get("date_created"));
            String desc = (String) cur_object.get("description");
            String name = cur_object.get("first_name") + " " + cur_object.get("last_name");
            String yes_count = String.valueOf(cur_object.get("yes_count"));
            String tip_count = String.valueOf(cur_object.get("tips_count"));
            int post_id = (int) cur_object.get("post_id");
            FeedItem item = new FeedItem(name, time, location, desc, decodedString, decodedString, yes_count, tip_count, post_id);
            feed_data.add(item);
        }
        return feed_data;
    }

    private String timeDifference(String datetime){
        datetime = datetime.replace("T", " ");
        datetime = datetime.substring(0, datetime.indexOf('.'));

        String[] data = datetime.split(" ");
        String date = data[0];
        String time = data[1];
        String[] date_array = date.split("-");
        String[] time_array = time.split(":");
        int[] time_data = new int[time_array.length];
        for(int i = 0; i < time_array.length; i++) {
            time_data[i] = Integer.parseInt(time_array[i]);
        }
        int[] date_data = new int[date_array.length];
        for(int i = 0; i < date_array.length; i++) {
            date_data[i] = Integer.parseInt(date_array[i]);
        }

        DateTime postDate = new DateTime(date_data[0], date_data[1], date_data[2], time_data[0], time_data[1], time_data[2], DateTimeZone.UTC);
        DateTime curDate = new DateTime(DateTimeZone.UTC);

        Duration duration = new Duration(postDate, curDate);

        int days = (int) duration.getStandardDays();
        int hours = (int) duration.getStandardHours();
        int minutes = (int) duration.getStandardMinutes();

        String final_time = null;
        if (days <= 0 && hours <= 0){
            if (minutes <= 0){
               final_time = "less than a minute ago";
            }
            if (minutes == 1){
                final_time = String.valueOf(minutes) + " minute ago";
            }
            else{
                final_time = String.valueOf(minutes) + " minutes ago";
            }
        }
        else if(days <= 0 && hours > 0) {
            if (hours == 1) {
                final_time = String.valueOf(hours) + " hour ago";
            }
            else {
                final_time = String.valueOf(hours) + " hours ago";
            }
        }
        else{
            if (days == 1) {
                final_time = String.valueOf(days) + " day ago";
            }
            else{
                final_time = String.valueOf(days) + " days ago";
            }
        }

        return final_time;
    }

    public void postIconClicked(View view) {
        Intent i = new Intent(Feed.this, CreatePost.class);
        startActivity(i);
    }

    public void yesClicked(View view){
        TextView yesText = (TextView) view.findViewById(R.id.yesText);
        if (yesText.getText().equals("")){
            yesText.setText("1");
        }
        else{
            yesText.setText(String.valueOf(Integer.valueOf(Integer.valueOf((String) yesText.getText()) + 1)));
        }

        FeedItemHolder view_holder = (FeedItemHolder) ((View)((View) view.getParent()).getParent()).getTag();
        int post_id = view_holder.post_id;

        RequestBody requestBody = new FormBody.Builder()
                .add("post_id", String.valueOf(post_id))
                .build();

        OkhttpServerRequest request_object = new OkhttpServerRequest(this, YES_CLICKED_EXT, requestBody);
        Request request = request_object.getRequest();

        request_object.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("FAILURE", "failed to call " + YES_CLICKED_EXT + " " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    Log.d("FAILED", response.message());
                }
            }
        });
    }
}
