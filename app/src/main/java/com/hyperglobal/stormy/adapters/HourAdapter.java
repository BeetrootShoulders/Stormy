package com.hyperglobal.stormy.adapters;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyperglobal.stormy.R;
import com.hyperglobal.stormy.weather.Hour;

/**
 * Created by cshop on 15/04/2015.
 */
public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder>{

    private Hour[] mHours;
    private Context mContext;

    public HourAdapter(Context context, Hour[] hours ){
        mContext = context;
        mHours = hours;
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) { // creates first set of views (equivalent of if convertView == null statement in ListView adapter
        View view = LayoutInflater.from(viewGroup.getContext()) // inflate the view using ...
                .inflate(R.layout.hourly_list_item, viewGroup, false); //... the hourly_list_item layout
        HourViewHolder viewHolder = new HourViewHolder(view); // make a new viewHolder using our HourViewHolder class (below)
        return viewHolder; // return it
    }

    @Override
    public void onBindViewHolder(HourViewHolder hourViewHolder, int i) { // binds the data to the viewHolder
        hourViewHolder.bindHour(mHours[i]);
    }

    @Override
    public int getItemCount() {
        return mHours.length;
    }

    public class HourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{ // RecyclerView contains both viewholder creation and assignment code, unlike ListView. Also makes it clickable.


        public TextView mTimeLabel; // define vars for each field
        public TextView mSummaryLabel;
        public TextView mTemperatureLabel;
        public ImageView mIconImageView;

        public HourViewHolder(View itemView) { // constructor for viewholder
            super(itemView);

            mTimeLabel = (TextView) itemView.findViewById(R.id.timeLabel); // define what views are used by what variables
            mSummaryLabel = (TextView) itemView.findViewById(R.id.summaryTextView);
            mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureTextView);
            mIconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);

            itemView.setOnClickListener(this); // set the onclick listener to the listener defined below
        }

        public void bindHour(Hour hour){ // bind data to view vars
            mTimeLabel.setText(hour.getHour());
            mSummaryLabel.setText(hour.getSummary());
            mTemperatureLabel.setText(hour.getTemperature()+"");
            mIconImageView.setImageResource(hour.getIconId());
        }

        @Override
        public void onClick(View v) {
            String time = mTimeLabel.getText().toString();
            String temperature = mTemperatureLabel.getText().toString();
            String summary = mSummaryLabel.getText().toString();
            String message = String.format("At %s it will be %s and %s", time, temperature, summary);
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }
}
