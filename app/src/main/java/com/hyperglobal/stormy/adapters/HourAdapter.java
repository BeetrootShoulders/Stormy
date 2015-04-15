package com.hyperglobal.stormy.adapters;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyperglobal.stormy.R;

/**
 * Created by cshop on 15/04/2015.
 */
public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder>{

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(HourViewHolder hourViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class HourViewHolder extends RecyclerView.ViewHolder{

        public TextView mTimeLabel;
        public TextView mSummaryLabel;
        public TextView mTemperatureLabel;
        public ImageView mIconImageView;

        public HourViewHolder(View itemView) {
            super(itemView);

            mTimeLabel = (TextView) itemView.findViewById(R.id.timeLabel);
            mSummaryLabel = (TextView) itemView.findViewById(R.id.summaryTextView);
            mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureTextView);
            mIconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
            //3:29 in custom adaptors and viewholders vid
        }
    }
}
