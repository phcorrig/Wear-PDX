package com.patrickcorriganjr.wearpdx.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.patrickcorriganjr.wearpdx.R;
import com.patrickcorriganjr.wearpdx.data.ArrivalInfo;
import com.patrickcorriganjr.wearpdx.data.StopInfo;

import java.util.ArrayList;

/**
 * Created by Bag Boy Rebel on 3/16/2015.
 */
public class ArrivalsAdapter extends BaseAdapter {

    private ArrayList<ArrivalInfo> mArrivals;
    private Context mContext;

    public ArrivalsAdapter(Context context, ArrayList<ArrivalInfo> arrivals){
        mContext = context;
        mArrivals = arrivals;
    }

    @Override
    public int getCount() {
        if(mArrivals == null){
            return 0;
        }
        return mArrivals.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrivals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.arrival_list_item, null);
            holder = new ViewHolder();
            holder.stopName = (TextView) convertView.findViewById(R.id.arrivalItemStopNameText);
            holder.arrivalTime = (TextView) convertView.findViewById(R.id.arrivalTimeText);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ArrivalInfo arrivalInfo = mArrivals.get(position);
        holder.stopName.setText(arrivalInfo.getShortSign());
        holder.arrivalTime.setText(arrivalInfo.getTimeToArrival());

        return convertView;
    }

    private static class ViewHolder {
        TextView stopName;
        TextView arrivalTime;
    }
}
