package com.patrickcorriganjr.wearpdx.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.patrickcorriganjr.wearpdx.data.ArrivalInfo;

import java.util.List;

/**
 * Created by Bag Boy Rebel on 3/16/2015.
 */
public class ArrivalsAdapter extends ArrayAdapter<ArrivalInfo> {
    public ArrivalsAdapter(Context context, int resource, List<ArrivalInfo> objects) {
        super(context, resource, objects);
    }
}
