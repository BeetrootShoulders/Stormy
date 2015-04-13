package com.hyperglobal.stormy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = DisplayActivity.class.getSimpleName();

    private double[] mGeoCode;
    private isNetWorkAvailable mIsNetworkAvailable;

    @InjectView(R.id.locationText) TextView mLocationText;
    @InjectView(R.id.submitButton) Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String location = mLocationText.getText().toString();
                Log.d(TAG,location);
                getLatLong(location);
            }
        });
    }

    private void getLatLong(String location) {
        String apiKey = "AIzaSyANxdW9Pr0cXXSQBjSRuEZUh2ZoWxUz6Tg";
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + location + "&key=" + apiKey;

        if (isNetWorkAvailable()){
            Log.d(TAG,"NETWORK IS AVAILABLE");
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(TAG, "callback failed");
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    Log.d(TAG, "We have a response");
                    try {
                        String jsonData = response.body().string();
                        if (response.isSuccessful()) {
                            try {
                                mGeoCode = getLocation(jsonData);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                                        intent.putExtra("location",mGeoCode);
                                        startActivity(intent);
                                    }
                                });
                            } catch (JSONException e) {
                                Log.e(TAG, "Exception caught ", e);
                            }
                        } else {
                            Log.e(TAG, "response was not successful");
                        }
                    } catch (IOException e){
                        Log.e(TAG,"IOException");
                    }
                }
            });
        }
    }

    private double[] getLocation(String jsonData) throws JSONException{

        double[] latlong = new double[2];
        JSONObject data = new JSONObject(jsonData);
        Log.v(TAG,jsonData);

        JSONArray a = data.getJSONArray("results");

        for (int i = 0; i < a.length(); i++){
            JSONObject item = a.getJSONObject(i);
            JSONObject location = item.getJSONObject("geometry").getJSONObject("location");
            latlong[0] = location.getDouble("lat");
            latlong[1] = location.getDouble("lng");
        }

        Log.d(TAG,"Latitude: "+latlong[0]);
        Log.d(TAG,"Longitude: "+latlong[1]);
        return latlong;
    }

    private boolean isNetWorkAvailable() { // method to check network availability
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); // set up new connection manager
        NetworkInfo networkInfo = manager.getActiveNetworkInfo(); // set up network info var and populate with manager
        boolean isAvailable = false; // init availability var

        if (networkInfo != null && networkInfo.isConnected()){ // if network info is not null (ie there is a connection) and connection is live...
            isAvailable = true; // set availability var to true...
        }
        return isAvailable; // return it
    }

}
