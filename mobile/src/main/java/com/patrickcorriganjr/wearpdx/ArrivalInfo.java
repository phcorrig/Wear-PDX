package com.patrickcorriganjr.wearpdx;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Bag Boy Rebel on 3/16/2015.
 */
public class ArrivalInfo implements Parcelable {

    private final static String TAG = ArrivalInfo.class.getSimpleName();
    private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    private String mScheduledArrival;
    private String mEstimatedArrival;
    private String mFullSign;
    private String mShortSign;
    private String mLocationId;
    private String mDirection;



    public ArrivalInfo(String scheduledArrival, String estimatedArrival, String fullSign, String shortSign, String locationId){
        mScheduledArrival = scheduledArrival;
        mEstimatedArrival = estimatedArrival;
        mFullSign = fullSign;
        mShortSign = shortSign;
        mLocationId = locationId;
    }

    public String getTimeToArrival(){
        String timeToArrival = "";
        Date estimatedDate = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            estimatedDate = simpleDateFormat.parse(mEstimatedArrival);
            long now = System.currentTimeMillis();
            timeToArrival = DateUtils.getRelativeTimeSpanString(estimatedDate.getTime(), now, DateUtils.MINUTE_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }



        return timeToArrival;
    }

    public String getScheduledArrival() {
        return mScheduledArrival;
    }

    public String getEstimatedArrival() {
        return mEstimatedArrival;
    }

    public String getFullSign() {
        return mFullSign;
    }

    public String getShortSign() {
        return mShortSign;
    }

    public String getLocationId() {
        return mLocationId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mScheduledArrival);
        dest.writeString(mEstimatedArrival);
        dest.writeString(mFullSign);
        dest.writeString(mShortSign);
        dest.writeString(mLocationId);
    }

    private ArrivalInfo(Parcel in){
        mScheduledArrival = in.readString();
        mEstimatedArrival = in.readString();
        mFullSign = in.readString();
        mShortSign = in.readString();
        mLocationId = in.readString();
    }

    public static final Creator<ArrivalInfo> CREATOR = new Parcelable.Creator<ArrivalInfo>(){

        @Override
        public ArrivalInfo createFromParcel(Parcel source) {
            return new ArrivalInfo(source);
        }

        @Override
        public ArrivalInfo[] newArray(int size) {
            return new ArrivalInfo[size];
        }
    };
}
