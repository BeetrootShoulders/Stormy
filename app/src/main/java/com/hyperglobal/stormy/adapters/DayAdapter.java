package com.hyperglobal.stormy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyperglobal.stormy.R;
import com.hyperglobal.stormy.weather.Day;

import org.w3c.dom.Text;

/**
 * Created by bmac on 14/04/2015.
 */
public class DayAdapter extends BaseAdapter {
    private Context mContext;
    private Day[] mDays;

    public DayAdapter(Context context, Day[] days){
        mContext = context;
        mDays = days;

    }

    @Override
    public int getCount() {
        return mDays.length;
    }

    @Override
    public Object getItem(int position) {
        return mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0; //not used
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder; // instantiate the viewholder (which holds the items in each list item in the layout)

        if (convertView == null){ // if there are no list item views setup (ie it's the first time the list is loaded), set them up
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item,null); // inflate the XML from daily_list_item to a new view
            holder = new ViewHolder(); // make a new instance of our ViewHolder (ie set up blank Image and TextViews)
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView); // set the layout views to relevant items from the layout
            holder.temperatureLabel = (TextView) convertView.findViewById(R.id.temperatureLabel);
            holder.dayLabel = (TextView) convertView.findViewById(R.id.dayNameLabel);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag(); // otherwise, just use the setup above
        }

        Day day = mDays[position]; // init a variable with the day data from the current position in the array

        holder.iconImageView.setImageResource(day.getIconId()); // display the data in the view ...
        holder.temperatureLabel.setText(day.getTemperatureMax() + "");
        if (position == 0) {
            holder.dayLabel.setText("Today");

        } else {
            holder.dayLabel.setText(day.getDayOfTheWeek());
        }

        return convertView; // return the view
    }

    private static class ViewHolder{ //holds the views in the List layout (ie each individual item in a list)
        ImageView iconImageView;
        TextView temperatureLabel;
        TextView dayLabel;
    }
}
