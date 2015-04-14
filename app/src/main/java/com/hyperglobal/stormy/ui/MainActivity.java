package com.hyperglobal.stormy.ui;

import android.content.Context;
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

import com.hyperglobal.stormy.R;
import com.hyperglobal.stormy.weather.Current;
import com.hyperglobal.stormy.weather.Day;
import com.hyperglobal.stormy.weather.Forecast;
import com.hyperglobal.stormy.weather.Hour;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Forecast mForecast;

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
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mProgressBar.setVisibility(View.INVISIBLE); // hide progress spinner

        final double latitude = 36.755; // latitude
        final double longitude = -3.873; // longitude

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
                            mForecast = parseForecastDetails(jsonData); // set the CurrentWeather var by calling getCurrentDetails method...
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
        Current current = mForecast.getCurrent();
        mTemperatureValue.setText(current.getTemperature()+"");
        mSummaryValue.setText(current.getSummary());
        mTimeLabel.setText("At " + current.getFormattedTime() + " it will be");
        mPrecipValue.setText(current.getPrecipChance()+"%");
        mHumidityValue.setText(current.getHumidity()+"");

        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIcon.setImageDrawable(drawable);
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException{
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData); // see getHourlyForecast for explanation - works in the same way.
        String timezone = forecast.getString("timezone"); // example of extracting string from root of JSON
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];

        for (int i = 0; i < data.length(); i++){
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();

            day.setTime(jsonDay.getLong("time"));
            day.setIcon(jsonDay.getString("icon"));
            day.setSummary(jsonDay.getString("summary"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setTimezone(timezone);

            days[i] = day;
        }
        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData); // init new JSONObject containing JSON
        String timezone = forecast.getString("timezone"); // example of extracting string from root of JSON
        JSONObject hourly = forecast.getJSONObject("hourly"); // make an object containing everything in the JSON object named 'hourly'
        JSONArray data = hourly.getJSONArray("data"); // then make an array containing everything in the JSON array named 'data'

        Hour[] hours = new Hour[data.length()]; // set up a new array the length of which is the same as the 'data' array

        for (int i = 0; i < data.length(); i++){ // loop through the data array
            JSONObject jsonHour = data.getJSONObject(i); // get a fresh JSONObject from the 'data' array at index i
            Hour hour = new Hour(); // set up a new hour array

            hour.setSummary(jsonHour.getString("summary")); // populate the hour array with the data from 'data' array (at the location defined by loop
            hour.setIcon(jsonHour.getString("icon"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimeZone(timezone);

            hours[i] = hour;
        }
        return hours;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException { // get weather method - returns a CurrentWeather object, takes json from forecast.io
        JSONObject forecast = new JSONObject(jsonData); // init new JSONObject containing JSON
        String timezone = forecast.getString("timezone"); // example of extracting string from root of JSON

        Log.i(TAG,"From JSON: " + timezone); // and logging it

        JSONObject currently = forecast.getJSONObject("currently"); // example of extracting JSON object (ie, nested list)

        Current current = new Current(); // setup new var
        current.setHumidity(currently.getDouble("humidity")); // call setter methods in CurrentWeather to set model data...
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setSummary(currently.getString("summary"));
        current.setTimeZone(timezone);

        Log.d(TAG, current.getFormattedTime());

        return current; // finally, return the output
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
