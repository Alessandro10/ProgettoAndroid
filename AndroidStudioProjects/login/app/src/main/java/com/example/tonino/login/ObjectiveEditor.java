package com.example.tonino.login;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import com.example.tonino.login.Dialogs.ConfirmCancelDialog;
import com.example.tonino.login.Dialogs.NameDescriptionDialog;
import com.example.tonino.login.Types.Objective;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Danny on 10/06/2015.
 */
public class ObjectiveEditor extends ObjectiveSelector implements GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener, ConfirmCancelDialog.DialogCallbacks {

    // used as custom parameter when creating the dialog after a long map touch to as the user
    // name and description of the new objective
    public static final String IS_NEW_OBJECTIVE = "is_new_objective";
    public static final String OBJECTIVE_LAT = "new_objective_lat";
    public static final String OBJECTIVE_LNG = "new_objective_lng";

    protected BitmapDescriptor newMarkerIcon;

    // map of newly created objectives, initially empty
    protected Map<Marker, Objective> newMarkerObjMap;

    // for use in onDialogConfirm method, referenced when user clicked on a new marker and edited
    // the objective info in the dialog. Can be null if the activity was deallocated
    private Marker lastMarker;

    // whether the dialog to edit an objective is showing, used to save fragment state
    public boolean isEditingObjective;

    public ObjectiveEditor(MainActivity_operator activity, GoogleMap mMap,
                           Map<Marker, Integer> markerObjMap, Set<Integer> thingOriginalObjs) {
        super(activity, mMap, markerObjMap, thingOriginalObjs);
        newMarkerIcon = mainActivity.newMarkerIcon;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        newMarkerObjMap = new HashMap<>();
    }

    protected String getCity(final LatLng latLng) {
        String city = null;
        try {
            List<Address> results = new Geocoder(mainActivity)
                    .getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (!results.isEmpty()) {
                city = results.get(0).getLocality();
            }
        } catch (IOException e) {
        }
        if (city == null) {
            Toast.makeText(mainActivity, mainActivity.getString(R.string.objective_error_city),
                    Toast.LENGTH_SHORT).show();
            city = mainActivity.getString(R.string.objective_unknown_city);
        }
        return city;
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        Bundle customParams = new Bundle();
        customParams.putBoolean(IS_NEW_OBJECTIVE, true);
        customParams.putDouble(OBJECTIVE_LAT, latLng.latitude);
        customParams.putDouble(OBJECTIVE_LNG, latLng.longitude);
        new NameDescriptionDialog.Builder(mainActivity)
                .setTitle(R.string.dialog_title_new_objective)
                .setCustomParams(customParams)
                .show();
        isEditingObjective = true;
        // see onDialogConfirm (isNewObjectiveBeignCreated part)
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (!newMarkerObjMap.keySet().contains(marker)) {
            super.onMarkerClick(marker);
        }
        else {
            Objective objective = newMarkerObjMap.get(marker);
            Bundle customParams = new Bundle();
            customParams.putDouble(OBJECTIVE_LAT, marker.getPosition().latitude);
            customParams.putDouble(OBJECTIVE_LNG, marker.getPosition().longitude);
            new NameDescriptionDialog.Builder(mainActivity)
                    .setParams(objective.name, objective.description)
                    .setTitle(R.string.dialog_title_edit_objective)
                    .setDeleteButton()
                    .setCustomParams(customParams)
                    .show();
            isEditingObjective = true;
            lastMarker = marker;
            // see onDialogConfirm (!isNewObjectiveBeignCreated part) and onDialogDelete
        }
        return true;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Objective objective = newMarkerObjMap.get(marker);
        LatLng latLng = marker.getPosition();
        objective.position_latitude = latLng.latitude;
        objective.position_longitude = latLng.longitude;
        objective.city = getCity(latLng);
    }

    @Override
    public void onDialogConfirm(Bundle customParams, Bundle userInput) {
        isEditingObjective = false;
        String name = userInput.getString(NameDescriptionDialog.ARG_NAME);
        String description = userInput.getString(NameDescriptionDialog.ARG_DESCRIPTION);
        boolean isNewObjectiveBeingCreated = customParams.getBoolean(IS_NEW_OBJECTIVE);
        double latitude = customParams.getDouble(OBJECTIVE_LAT);
        double longitude = customParams.getDouble(OBJECTIVE_LNG);
        LatLng latLng = new LatLng(latitude, longitude);
        if (isNewObjectiveBeingCreated) {
            // user long touched the map to create a new objective
            Objective objective = new Objective();
            objective.name = name;
            objective.description = description;
            objective.position_latitude = latLng.latitude;
            objective.position_longitude = latLng.longitude;
            objective.validation_method = 1;
            objective.city = getCity(latLng);
            Marker newMarker = addMarker(latLng);
            newMarkerObjMap.put(newMarker, objective);
            newMarker.setTitle(name);
            newMarker.setSnippet(description);
        }
        else {
            // user touched an existing new objective marker
            if (lastMarker == null)  {
                // activity was deallocated, we no longer have the marker but we have its longitude
                // and latitude
                lastMarker = getLastMarkerBeforeActivityWasDestroyed(latLng);
                if (lastMarker == null) {
                    Toast.makeText(mainActivity,
                            mainActivity.getString(R.string.objective_error_marker),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Objective objective = newMarkerObjMap.get(lastMarker);
            objective.name = name;
            objective.description = description;
            lastMarker.setTitle(name);
            lastMarker.setSnippet(description);
            lastMarker.showInfoWindow();
        }
    }

    @Override
    public void onDialogCancel() {}

    @Override
    public void onDialogDelete(Bundle customParams) {
        // user touched a new objective marker and touched delete button in the dialog
        if (lastMarker == null)  {
            // activity was deallocated, we no longer have the marker but we have its longitude
            // and latitude
            double latitude = customParams.getDouble(OBJECTIVE_LAT);
            double longitude = customParams.getDouble(OBJECTIVE_LNG);
            LatLng latLng = new LatLng(latitude, longitude);
            lastMarker = getLastMarkerBeforeActivityWasDestroyed(latLng);
            if (lastMarker == null) {
                Toast.makeText(mainActivity,
                        mainActivity.getString(R.string.objective_error_marker),
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
        lastMarker.remove();
        newMarkerObjMap.remove(lastMarker);
    }

    /**
     * Does what the name says, can return null if marker is not found
     *
     * @param latLng latLng object of marker location, built from the dialog customParams
     * @return the marker selected, or null if it's not found
     */
    protected Marker getLastMarkerBeforeActivityWasDestroyed(LatLng latLng) {
        for(Marker marker : newMarkerObjMap.keySet()) {
            if (marker.getPosition().equals(latLng)) {
                lastMarker = marker;
            }
        }
        return lastMarker;
    }

    private Marker addMarker(LatLng latLng) {
        return mMap.addMarker(new MarkerOptions().position(latLng).draggable(true)
                .icon(newMarkerIcon));
    }

    public Map<Marker, Objective> getNewMarkerObjMap() {
        return newMarkerObjMap;
    }

    public Objective[] getNewObjectives() {
        Objective[] newObjectives = null;
        if (newMarkerObjMap.size() > 0) {
            newObjectives = new Objective[newMarkerObjMap.size()];
            int i = 0;
            for (Marker marker : newMarkerObjMap.keySet()) {
                newObjectives[i] = newMarkerObjMap.get(marker);
                i++;
            }
        }
        return newObjectives;
    }

    public void setNewObjectives(Objective [] newObjectives) {
        for (Objective newObjective : newObjectives) {
            LatLng latLng = new LatLng(newObjective.position_latitude,
                    newObjective.position_longitude);
            Marker marker = addMarker(latLng);
            marker.setTitle(newObjective.name);
            marker.setSnippet(newObjective.description);
            newMarkerObjMap.put(marker, newObjective);
        }
    }

    public void cancelEdit() {
        super.cancelEdit();
        for(Marker marker : newMarkerObjMap.keySet()) {
            marker.remove();
        }
    }

    public void quitEdit() {
        super.quitEdit();
        mMap.setOnMapLongClickListener(null);
        mMap.setOnMarkerClickListener(null);
    }

}

