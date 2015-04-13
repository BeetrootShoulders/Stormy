package com.hyperglobal.stormy;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DisplayActivity extends ActionBarActivity {

    public static final String TAG = DisplayActivity.class.getSimpleName();

    private CurrentWeather mCurrentWeather;

    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.temperatureValue) TextView mTemperatureValue;
    @InjectView(R.id.summaryValue) TextView mSummaryValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.iconImageView) ImageView mIcon;
    @InjectView(R.id.refreshImageView) ImageView mRefreshImageView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        ButterKnife.inject(this);

        mProgressBar.setVisibility(View.INVISIBLE); // hide progress spinner

        Intent intent = getIntent();

        final double latitude = intent.getDoubleExtra("location[0]",0); // latitude
        Log.d(TAG,"Latitude passed to DisplayActivity: "+latitude);
        final double longitude = intent.getDoubleExtra("location[1]",0); // longitude
        Log.d(TAG,"Longitude passed to DisplayActivity: "+longitude);

        mRefreshImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getForecast(latitude,longitude);
            }
        });

        getForecast(latitude,longitude);

    }

    private void getForecast(double latitude, double longitude) {
        String apiKey = "137e02e9e52117194254b749a8ac2a4e"; // URL variables - api key

        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitude; // build request URL with vars

        if (isNetWorkAvailable()) { // call network availability method...
            toggleRefresh(); // call refresh/progress image visibility method
            // if network is available
            OkHttpClient client = new OkHttpClient(); // set up new okHttpClient

            Request request = new Request.Builder() // set up a new request builder
                    .url(forecastUrl) // assign the forecast the request url
                    .build(); // build the request

            Call call = client.newCall(request); // set up a call to pass the request
            call.enqueue(new Callback() { // enqueue the call so it does not interrupt the UI
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh(); // call refresh/progress image visibility method
                        }
                    });
                    alertUserAboutError(); // call error dialog method
                }

                @Override
                public void onResponse(Response response) throws IOException { // when the callback responds

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh(); // call refresh/progress image visibility method
                        }
                    });

                    try { // try to get the JSON data
                        String jsonData = response.body().string(); // stick it in a variable
                        Log.v(TAG, jsonData); // log it (optional)
                        if (response.isSuccessful()) { // if the response is successful
                            mCurrentWeather = getCurrentDetails(jsonData); // set the CurrentWeather var by calling getCurrentDetails method...
                            runOnUiThread(new Runnable() { // tell android to run this in the main thread so the UI can be updated
                                @Override
                                public void run() {
                                    updateDisplay(); //call the update display method
                                }
                            });

                        } else {
                            alertUserAboutError(); // otherwise call the error dialog function
                        }
                    } catch (IOException e) { // catch response exception (network down, server unavailable etc)
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e){ // catch JSON exception (can't get data)
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        } else { // if network is NOT available...
            Toast.makeText(this, getString(R.string.network_unavailable_message), Toast.LENGTH_LONG).show(); // show Toast message to tell user
        } // end of network availability check
    }

    private void toggleRefresh() { // toggles visibility of refresh and progress images
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        mTemperatureValue.setText(mCurrentWeather.getTemperature()+"");
        mSummaryValue.setText(mCurrentWeather.getSummary());
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime() + " it will be");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance()+"%");
        mHumidityValue.setText(mCurrentWeather.getHumidity()+"");

        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIcon.setImageDrawable(drawable);
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException { // get weather method - returns a CurrentWeather object, takes json from forecast.io
        JSONObject forecast = new JSONObject(jsonData); // init new JSONObject containing JSON
        String timezone = forecast.getString("timezone"); // example of extracting string from root of JSON

        Log.i(TAG,"From JSON: " + timezone); // and logging it

        JSONObject currently = forecast.getJSONObject("currently"); // example of extracting JSON object (ie, nested list)

        CurrentWeather currentWeather = new CurrentWeather(); // setup new var
        currentWeather.setHumidity(currently.getDouble("humidity")); // call setter methods in CurrentWeather to set model data...
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTimeZone(timezone);

        Log.d(TAG,currentWeather.getFormattedTime());

        return currentWeather; // finally, return the output
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

    private void alertUserAboutError() { // generate error dialog
        AlertDialogFragment dialog = new AlertDialogFragment(); // define new AlertDialogFragment var (see AlertDialogFragment class)
        dialog.show(getFragmentManager(),"error_dialog"); // show the dialog
    }


}
