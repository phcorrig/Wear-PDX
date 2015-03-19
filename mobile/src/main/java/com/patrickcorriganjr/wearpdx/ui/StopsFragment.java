package com.patrickcorriganjr.wearpdx.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.patrickcorriganjr.wearpdx.data.ArrivalInfo;
import com.patrickcorriganjr.wearpdx.R;
import com.patrickcorriganjr.wearpdx.data.StopInfo;
import com.patrickcorriganjr.wearpdx.adapters.StopsAdapter;
import com.patrickcorriganjr.wearpdx.TrimetConstants;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class StopsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final static String TAG = StopsFragment.class.getSimpleName();

    public final static String INTENT_LIST = "ArrivalList";
    public final static String INTENT_DIRECTION = "ArrivalDirection";
    public final static String INTENT_STOP_NAME = "StopName";
    public final static String INTENT_STOP_ID = "StopId";

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double mLatitude;
    private double mLongitude;
    private int mRadius;
    private ArrayList<StopInfo> mStops;

    @InjectView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.emptyText)
    TextView mEmptyText;

    @InjectView(R.id.locationListView)
    ListView mStopListView;

    public StopsFragment() {
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stops, container, false);
        setHasOptionsMenu(true);

        ButterKnife.inject(this, rootView);

        mRadius = 200;
        mLatitude = 45.5138850;
        mLongitude = -122.6828660;

        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        mStopListView.setEmptyView(mEmptyText);

        mStopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ArrivalsActivity.class);
                intent.putParcelableArrayListExtra(INTENT_LIST, mStops.get(position).getArrivalInfoArrayList());
                intent.putExtra(INTENT_DIRECTION, mStops.get(position).getDirection());
                intent.putExtra(INTENT_STOP_NAME, mStops.get(position).getStopName());
                intent.putExtra(INTENT_STOP_ID, mStops.get(position).getLocationId());
                startActivity(intent);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        refresh();
    }

    private void refresh() {
        try {
            getStops(mLatitude, mLongitude);
        } catch (MalformedURLException e) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.http_error), Toast.LENGTH_LONG).show();
        }
    }

    SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refresh();
        }
    };

    public void getStops(double latitude, double longitude) throws MalformedURLException {
        URL url = new URL("http://developer.trimet.org/ws/V1/stops/ll/" + latitude + "," + longitude + "/meters/" + mRadius + "/appID/" + TrimetConstants.API_KEY + "/json/true");
        Log.d(TAG, url.toString());

        if(isNetworkAvailable()) {
            if (!mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }


            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    Toast.makeText(getActivity(), getActivity().getString(R.string.http_error), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String jsonData = response.body().string();
                    Log.v(TAG, jsonData);
                    if (response.isSuccessful()) {
                        ArrayList<String> locationIds = new ArrayList<String>();
                        // TODO parse details
                        try {
                            JSONObject jsonObject = new JSONObject(jsonData);
                            JSONObject resultSet = jsonObject.getJSONObject("resultSet");
                            JSONArray locationSet = resultSet.getJSONArray("location");
                            for(int i = 0; i < locationSet.length(); i++){
                                JSONObject jObj = locationSet.getJSONObject(i);
                                locationIds.add(jObj.getString("locid"));
                            }
                            Log.d(TAG, "Stop Ids: " + locationIds);
                            getArrivals(locationIds);


                        } catch (JSONException e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.json_error), Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), getActivity().getString(R.string.http_error), Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            });
        }
        else{
            Toast.makeText(getActivity(), getActivity().getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
        }
    }

    public void getArrivals(ArrayList<String> stopIds) throws MalformedURLException {
        URL url = new URL("http://developer.trimet.org/ws/V1/arrivals/streetcar/true/locIDs/" + TextUtils.join(",", stopIds) + "/appID/" + TrimetConstants.API_KEY + "/json/true");

        if(isNetworkAvailable()) {
            if (!mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }


            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    Toast.makeText(getActivity(), getActivity().getString(R.string.http_error), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    String jsonData = response.body().string();
                    Log.v(TAG, jsonData);
                    if (response.isSuccessful()) {
                        // TODO parse details
                        try {
                            mStops = getStopList(jsonData);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    StopsAdapter adapter = new StopsAdapter(getActivity(), mStops);
                                    mStopListView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        } catch (JSONException e) {
                            Log.d(TAG, e.getMessage());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.json_error), Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), getActivity().getString(R.string.http_error), Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            });
        }
        else{
            Toast.makeText(getActivity(), getActivity().getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<StopInfo> getStopList(String json) throws JSONException {
        ArrayList<StopInfo> stops = new ArrayList<>();

        JSONObject rootObject = new JSONObject(json);
        JSONObject resultSet = rootObject.getJSONObject("resultSet");
        JSONArray locationSet = resultSet.getJSONArray("location");
        JSONArray arrivalSet = resultSet.getJSONArray("arrival");

        for(int i = 0; i < locationSet.length(); i++){
            ArrayList<ArrivalInfo> arrivals = new ArrayList<ArrivalInfo>();
            JSONObject location = locationSet.getJSONObject(i);

            for(int j = 0; j < arrivalSet.length(); j++){
                JSONObject arrival = arrivalSet.getJSONObject(j);
                if(arrival.getString("locid").equals(location.getString("locid"))){
                    arrivals.add(getArrivalInfo(arrival));
                }
            }
            stops.add(getStopInfo(location, arrivals));
        }
        
        return stops;
    }

    private ArrivalInfo getArrivalInfo(JSONObject jsonArrival) throws JSONException {
        String scheduledArrival = jsonArrival.getString("scheduled");
        String estimatedArrival = scheduledArrival;
        if(jsonArrival.has("estimated")) {
            estimatedArrival = jsonArrival.getString("estimated");
        }
        String fullSign = jsonArrival.getString("fullSign");
        String shortSign = jsonArrival.getString("shortSign");
        String locationId = jsonArrival.getString("locid");

        return new ArrivalInfo(scheduledArrival, estimatedArrival, fullSign, shortSign, locationId);
    }

    private StopInfo getStopInfo(JSONObject json, ArrayList<ArrivalInfo> arrivals) throws JSONException {
        String stopName = json.getString("desc");
        String direction = json.getString("dir");
        String locationID = json.getString("locid");
        String latitude = json.getString("lat");
        String longitude = json.getString("lng");

        ArrayList<ArrivalInfo> arrivalsSameId = new ArrayList<ArrivalInfo>();
        for(ArrivalInfo arrival : arrivals){
            if(arrival.getLocationId().equals(locationID)){
                arrivalsSameId.add(arrival);
            }
        }

        return new StopInfo(stopName, direction, locationID, latitude, longitude, arrivalsSameId);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLastKnownLocation();
    }

    private void getLastKnownLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            mLatitude = (int) mLastLocation.getLatitude();
            mLongitude = (int) mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }
}
