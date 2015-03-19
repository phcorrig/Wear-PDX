package com.patrickcorriganjr.wearpdx;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ArrivalsFragment extends Fragment {

    private final static String TAG = ArrivalsFragment.class.getSimpleName();

    private ArrayList<ArrivalInfo> mArrivals;
    private String mDirection;
    private String mStopId;

    @InjectView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.emptyText)
    TextView mEmptyText;

    @InjectView(R.id.arrivalsListView)
    ListView mArrivalsListView;

    @InjectView(R.id.stopDirection)
    TextView mDirectionText;

    @InjectView(R.id.stopLocationId)
    TextView mStopIdText;

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
            mStopId = intent.getStringExtra(StopsFragment.INTENT_STOP_ID);
        }

        //refresh();


        return rootView;
    }

    private void refresh() {

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
}
