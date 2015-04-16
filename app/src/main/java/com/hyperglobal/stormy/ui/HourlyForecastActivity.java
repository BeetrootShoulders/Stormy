package com.hyperglobal.stormy.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.hyperglobal.stormy.R;
import com.hyperglobal.stormy.adapters.HourAdapter;
import com.hyperglobal.stormy.weather.Hour;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HourlyForecastActivity extends ActionBarActivity {

    private Hour[] mHours;

    @InjectView (R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        mHours = Arrays.copyOf(parcelables,parcelables.length,Hour[].class);

        HourAdapter hourAdapter = new HourAdapter(this,mHours); // define a new hourAdapter with our HourAdapter class, pass in the array of data and ...
        mRecyclerView.setAdapter(hourAdapter); // .. set the view to use it

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this); // RecyclerView requires a layout manager to handle new list items. Define it and...
        mRecyclerView.setLayoutManager(layoutManager); // ... set the view to use it

        mRecyclerView.setHasFixedSize(true); // if your list has a known, fixed number of items, set this to improve performance.

    }


}
