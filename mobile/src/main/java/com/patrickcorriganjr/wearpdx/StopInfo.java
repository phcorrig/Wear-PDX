package com.patrickcorriganjr.wearpdx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Bag Boy Rebel on 3/16/2015.
 */
public class StopInfo {
    private static final String LOG = StopInfo.class.getSimpleName();

    private String mStopName;
    private String mDirection;
    private String mLocationId;
    private String mLatitude;
    private String mLongitude;

    private ArrayList<ArrivalInfo> mArrivalInfoArrayList = new ArrayList<ArrivalInfo>();

    public StopInfo(String stopName, String direction, String locationId, String latitude, String longitude, ArrayList<ArrivalInfo> arrivals){
        mStopName = stopName;
        mDirection = direction;
        mLocationId = locationId;
        mLatitude = latitude;
        mLongitude = longitude;
        mArrivalInfoArrayList = arrivals;

        /*Collections.sort(mArrivalInfoArrayList, new Comparator<ArrivalInfo>() {
            @Override
            public int compare(ArrivalInfo lhs, ArrivalInfo rhs) {
                return lhs.getEstimatedArrival().compareToIgnoreCase(rhs.getEstimatedArrival());
            }
        });*/
    }

    public boolean IsSameLocationID (ArrivalInfo arrivalInfo){
        if(mLocationId.equals(arrivalInfo.getLocationId())){
            return true;
        }
        else{
            return false;
        }
    }

    public void AddArrival(ArrivalInfo arrivalInfo){
        if(IsSameLocationID(arrivalInfo))
        {
            mArrivalInfoArrayList.add(arrivalInfo);
        }
    }

    public ArrivalInfo getArrivalInfo(int index){
        return mArrivalInfoArrayList.get(index);
    }

    public ArrayList<ArrivalInfo> getArrivalInfoArrayList(){
        return mArrivalInfoArrayList;
    }

    public boolean hasArrivalInfo(){
        if(mArrivalInfoArrayList.size() > 0){
            return true;
        }
        else{
            return false;
        }
    }

    public String getStopName() {
        return mStopName;
    }

    public void setStopName(String stopName) {
        mStopName = stopName;
    }

    public String getDirection() {
        return mDirection;
    }

    public void setDirection(String direction) {
        mDirection = direction;
    }

    public String getLocationId() {
        return mLocationId;
    }

    public void setLocationId(String locationId) {
        mLocationId = locationId;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }

    public void setArrivalInfoArrayList(ArrayList<ArrivalInfo> arrivalInfoArrayList) {
        mArrivalInfoArrayList = arrivalInfoArrayList;
    }
}
