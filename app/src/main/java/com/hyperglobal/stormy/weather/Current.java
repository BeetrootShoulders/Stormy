package com.hyperglobal.stormy.weather;

import com.hyperglobal.stormy.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by bmac on 12/04/2015.
 */
public class Current {
    private String mIcon;
    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private String mTimeZone;


    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getIcon() {
        return mIcon;
    }

    public int getIconId(){ // returns icon id
        return Forecast.getIconId(mIcon);
    }

    public long getTime() {
        return mTime;
    }

    public String getFormattedTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm"); // set up a new date format
        formatter.setTimeZone(TimeZone.getTimeZone(mTimeZone)); // set the timezone
        Date dateTime = new Date(mTime * 1000); // convert the time from the JSON to milliseconds
        String timeString = formatter.format(dateTime); // format that time as per the formatter var above
        return timeString; // return it
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getTemperature() {
        double celsius = (mTemperature-32)*0.55556;
        return (int) Math.round(celsius); // return a cast integer, rounded to remove decimal points, converting F to C.
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getPrecipChance() {
        double precipPerctentage = mPrecipChance * 100;
        return (int) Math.round(precipPerctentage);
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }


}

