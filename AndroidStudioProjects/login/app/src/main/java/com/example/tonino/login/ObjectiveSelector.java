package com.example.tonino.login;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Danny on 10/06/2015.
 */
public class ObjectiveSelector implements GoogleMap.OnMarkerClickListener {

    protected MainActivity_operator mainActivity;
    protected GoogleMap mMap;
    protected BitmapDescriptor defaultMarkerIcon;
    protected BitmapDescriptor editedMarkerIcon;

    // map of already existing objectives
    protected Map<Marker, Integer> markerObjMap;

    // objectives related to the thing to edit
    protected Set<Integer> thingOriginalObjs;
    protected Set<Integer> thingNewObjs;


    public ObjectiveSelector(MainActivity_operator activity, GoogleMap mMap,
                             Map<Marker, Integer> markerObjMap, Set<Integer> thingOriginalObjs) {
        mainActivity = activity;
        this.mMap = mMap;
        defaultMarkerIcon = mainActivity.defaultMarkerIcon;
        editedMarkerIcon = mainActivity.editedMarkerIcon;
        mMap.setOnMarkerClickListener(this);
        this.markerObjMap = markerObjMap;
        this.thingOriginalObjs = new HashSet<>(thingOriginalObjs);
        thingNewObjs = thingOriginalObjs;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        int objId = markerObjMap.get(marker);
        if (thingNewObjs.contains(objId)) {
            marker.setIcon(defaultMarkerIcon);
            thingNewObjs.remove(objId);
        }
        else {
            marker.setIcon(editedMarkerIcon);
            thingNewObjs.add(objId);
        }
        marker.showInfoWindow();
        return true;
    }

    public void cancelEdit() {
        thingNewObjs = thingOriginalObjs;
    }

    public void quitEdit() {
        mMap.setOnMarkerClickListener(null);
    }

}

