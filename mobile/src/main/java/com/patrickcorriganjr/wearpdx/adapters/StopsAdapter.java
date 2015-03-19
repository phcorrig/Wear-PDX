package com.patrickcorriganjr.wearpdx.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.patrickcorriganjr.wearpdx.R;
import com.patrickcorriganjr.wearpdx.data.StopInfo;

import java.util.ArrayList;

/**
 * Created by Bag Boy Rebel on 3/17/2015.
 */
public class StopsAdapter extends BaseAdapter {

    private ArrayList<StopInfo> mStops;
    private Context mContext;

    public StopsAdapter(Context context, ArrayList<StopInfo> stops){
        mContext = context;
        mStops = stops;
    }

    @Override
    public int getCount() {
        if(mStops == null){
            return 0;
        }
        return mStops.size();
    }

    @Override
    public Object getItem(int position) {
        return mStops.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.stop_list_item, null);
            holder = new ViewHolder();
            holder.stopDescription = (TextView) convertView.findViewById(R.id.stop_desc_textview);
            holder.stopDirection = (TextView) convertView.findViewById(R.id.stop_dir_textview);
            holder.timeToArrival = (TextView) convertView.findViewById(R.id.time_next_textview);
            holder.transitLine = (TextView) convertView.findViewById(R.id.next_arrival_textview);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        StopInfo stopInfo = mStops.get(position);
        holder.stopDirection.setText(stopInfo.getDirection());
        holder.stopDescription.setText(stopInfo.getStopName());
        if(stopInfo.hasArrivalInfo()){
            holder.timeToArrival.setText(stopInfo.getArrivalInfo(0).getTimeToArrival());
            holder.transitLine.setText(stopInfo.getArrivalInfo(0).getShortSign());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView stopDescription;
        TextView stopDirection;
        TextView timeToArrival;
        TextView transitLine;
    }
}
