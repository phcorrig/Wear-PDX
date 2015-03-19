package com.patrickcorriganjr.wearpdx.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.patrickcorriganjr.wearpdx.TrimetConstants;
import com.patrickcorriganjr.wearpdx.adapters.ArrivalsAdapter;
import com.patrickcorriganjr.wearpdx.adapters.StopsAdapter;
import com.patrickcorriganjr.wearpdx.data.ArrivalInfo;
import com.patrickcorriganjr.wearpdx.R;
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


public class ArrivalsFragment extends Fragment {

    private final static String TAG = ArrivalsFragment.class.getSimpleName();

    private ArrayList<ArrivalInfo> mArrivals;
    private String mDirection;
    private String mStopName;
    private String mStopId;

    @InjectView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.emptyText)
    TextView mEmptyText;

    @InjectView(R.id.arrivalsListView)
    ListView mArrivalsListView;

    @InjectView(R.id.stopDirection)
    TextView mDirectionText;

    @InjectView(R.id.stopLocationName)
    TextView mStopNameText;

    public ArrivalsFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_arrivals, container, false);
        setHasOptionsMenu(true);

        ButterKnife.inject(this, rootView);

        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mArrivalsListView.setEmptyView(mEmptyText);

        Intent intent = getActivity().getIntent();
        if(intent != null){
            mArrivals = intent.getParcelableArrayListExtra(StopsFragment.INTENT_LIST);
            mDirection = intent.getStringExtra(StopsFragment.INTENT_DIRECTION);
            mStopName = intent.getStringExtra(StopsFragment.INTENT_STOP_NAME);
            mStopId = intent.getStringExtra(StopsFragment.INTENT_STOP_ID);
        }

        mDirectionText.setText(mDirection);
        mStopNameText.setText(mStopName);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();


        refresh();
    }

    private void refresh() {
        try {
            getArrivals(mStopId);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    public void getArrivals(String stopId) throws MalformedURLException {
        URL url = new URL("http://developer.trimet.org/ws/V1/arrivals/streetcar/true/locIDs/" + stopId + "/appID/" + TrimetConstants.API_KEY + "/json/true");

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

                            JSONObject rootObject = new JSONObject(jsonData);
                            JSONObject resultSet = rootObject.getJSONObject("resultSet");
                            JSONArray arrivalSet = resultSet.getJSONArray("arrival");

                            mArrivals = new ArrayList<ArrivalInfo>();

                            for(int i = 0; i < arrivalSet.length(); i++){
                                JSONObject arrival = arrivalSet.getJSONObject(i);
                                mArrivals.add(getArrivalInfo(arrival));
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    ArrivalsAdapter adapter = new ArrivalsAdapter(getActivity(), mArrivals);
                                    mArrivalsListView.setAdapter(adapter);
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
}
