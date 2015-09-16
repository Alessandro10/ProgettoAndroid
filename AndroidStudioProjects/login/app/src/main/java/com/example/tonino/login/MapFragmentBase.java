package com.example.tonino.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Danny on 25/05/2015.
 */
public abstract class MapFragmentBase extends Fragment {

    protected MainActivity_operator mainActivity;
    protected GoogleMap mMap; // Might be null if Google Play services APK is not available.
    protected SupportMapFragment mMapFragment;

    public MapFragmentBase() {
    }

    protected Marker placeMarker(double lat, double lng, String title, String snippet) {
        return mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(title)
                                .snippet(snippet));
    }

    private void setUpMapIfNeeded() {
        // Try to obtain the map from the SupportMapFragment.
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                // Do a null check to confirm that we have not already instantiated the map.
                if (mMap == null) {
                    mMap = googleMap;
                    onMapSetup();
                }
            }
        });
    }

    //to be overridden by subclass
    protected abstract void onMapSetup();

    //to be overridden by subclass
    protected abstract int getLayoutToInflate();

    //to be overridden by subclass
    protected abstract void onContinueCreateView(View rootView, Bundle savedInstanceState);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(getLayoutToInflate(), container, false);
        FragmentManager fm = getChildFragmentManager();
        mMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mMapFragment).commit();
        }
        onContinueCreateView(rootView, savedInstanceState);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mainActivity = (MainActivity_operator) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

}