//this is what will open once user logs into twitter account (shows timeline/homepage)
package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActiviy";
        TwitterClient client;
        RecyclerView rvTweets;
        List<Tweet> tweets;
        TweetsAdapter adapter;
        SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_activiy);
        //creates a layout for you already

        client = TwitterApp.getRestClient( this);

        swipeContainer =findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,

                android.R.color.holo_green_light,

                android.R.color.holo_orange_light,

                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data!");
                populateHomeTimeLine();
            }
        });
        rvTweets = findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);
        populateHomeTimeLine();

    }

    private void populateHomeTimeLine() {
     client.getHomeTimeLine(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Headers headers, JSON json) {
             Log.i(TAG, "onSuccess!" +json.toString());
             JSONArray jsonArray = json.jsonArray;
             try {
                 adapter.clear();
                 adapter.addAll(Tweet.fromJsonArray(jsonArray));
                 tweets.addAll(Tweet.fromJsonArray(jsonArray));
                 adapter.notifyDataSetChanged();
             } catch (JSONException e) {
                 Log.e(TAG, "Json exception", e);

             }
         }

         @Override
         public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
             Log.e(TAG, "onFailure!" + response, throwable);
         }
     });
    }
}
