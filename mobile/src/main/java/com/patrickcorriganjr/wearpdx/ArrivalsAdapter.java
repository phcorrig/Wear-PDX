package com.patrickcorriganjr.wearpdx;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Bag Boy Rebel on 3/16/2015.
 */
public class ArrivalsAdapter extends ArrayAdapter<ArrivalInfo> {
    public ArrivalsAdapter(Context context, int resource, List<ArrivalInfo> objects) {
        super(context, resource, objects);
    }
}
