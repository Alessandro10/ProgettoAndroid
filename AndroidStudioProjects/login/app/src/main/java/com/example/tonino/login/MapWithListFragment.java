package com.example.tonino.login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tonino.login.Dialogs.ConfirmCancelDialog;
import com.example.tonino.login.Dialogs.DialogBroadcastReceiver;
import com.example.tonino.login.Dialogs.MessageDialog;
import com.example.tonino.login.Dialogs.NoConnectionDialog;
import com.example.tonino.login.Dialogs.NumberPickerDialog;
import com.example.tonino.login.Dialogs.SingleEditTextDialog;
import com.example.tonino.login.Types.Objective;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by Danny on 26/05/2015.
 */
public abstract class MapWithListFragment extends MapFragmentBase implements
        ServiceInquirer.OnResponseReceivedCallback, View.OnClickListener {

    public static final String STATE_LAST_SELECTED_THING_ID = "last_selected_thing_id";
    public static final String STATE_IS_EDIT_MODE = "is_edit_mode";
    public static final String STATE_EDITED_THING = "edited_thing";
    public static final String EDITOR_DIALOG_FIELD = "field";
    public static final String EDITOR_DIALOG_DELETE = "delete";

    public static final int NEW_THING_ID = 0;
    public static final int NOTHING_SELECTED = -1;
    public static final int NO_FIELD = 0;

    // padding when moving camera to objectives
    protected final int CAMERA_PADDING = 80;
    // zoom level when moving camera to a single objective
    protected final float SINGLE_OBJECTIVE_ZOOM = 15;

    protected BitmapDescriptor defaultMarkerIcon;
    protected BitmapDescriptor selectedMarkerIcon;
    protected BitmapDescriptor editedMarkerIcon;
    protected ViewGroup rootView;
    protected ServiceInquirer serviceInquirer;
    protected LayoutInflater inflater;
    protected SavedState savedState;

    protected ViewGroup thingContainer;
    protected Set<Integer> thingIdSet;
    protected Map<Integer, ViewGroup> thingViewMap;
    protected Map<Integer, Objective> objMap;
    protected Map<Integer, Marker> objMarkerMap;
    protected Map<Marker, Integer> markerObjMap;
    protected int lastSelectedThingId = NOTHING_SELECTED;
    protected ImageButton newThingBtn;
    protected ViewGroup editControls;
    protected Button saveEditBtn;
    protected Button cancelEditBtn;
    protected Button helpBtn;
    protected boolean isEditMode = false;
    protected boolean modeChanged = false;
    protected ThingEditor currentEditor;

    public static Map<Integer, Field> thingEditBtnToField = null;

    public MapWithListFragment() {}

    protected class Field {
        int id;
        int title;

        public Field(int id, int title) {
            this.id = id;
            this.title = title;
        }
    }

    protected class SavedState {
        public Parcelable editedThing;

        public SavedState() {}

        public SavedState(SavedState savedState) {
            editedThing = savedState.editedThing;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity_operator) activity;
        if (mainActivity.initializeMarkerIcons()) {
            defaultMarkerIcon = mainActivity.defaultMarkerIcon;
            selectedMarkerIcon = mainActivity.selectedMarkerIcon;
            editedMarkerIcon = mainActivity.editedMarkerIcon;
        }
        else {
            Toast.makeText(mainActivity, "can't initialize google maps", Toast.LENGTH_SHORT).show();
        }
        serviceInquirer = mainActivity.getServiceInquirer();
        inflater = mainActivity.getLayoutInflater();
    }

    @Override
    protected int getLayoutToInflate() {
        return R.layout.fragment_map_with_list_operator;
    }

    /**
     * @param rootView fragment root view
     * @param savedInstanceState state when activity was deallocated
     */
    @Override
    protected void onContinueCreateView(final View rootView, Bundle savedInstanceState) {
        newThingBtn = (ImageButton) rootView.findViewById(R.id.new_thing_btn);
        editControls = (ViewGroup) rootView.findViewById(R.id.edit_thing_controls);
        saveEditBtn = (Button) rootView.findViewById(R.id.save_edit_btn);
        cancelEditBtn = (Button) rootView.findViewById(R.id.cancel_edit_btn);
        helpBtn = (Button) rootView.findViewById(R.id.help_btn);
        thingViewMap = new HashMap<>();
        objMarkerMap = new HashMap<>();
        markerObjMap = new HashMap<>();
        this.rootView = (ViewGroup) rootView;
        rootView.findViewById(R.id.help_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.example.tonino.login.Dialogs.HelpDialog.show(mainActivity, getHelpMessage());
            }
        });
        if (savedInstanceState != null) {
            setSavedState(savedInstanceState);
        }
        thingContainer = (ViewGroup) rootView.findViewById(R.id.thing_container);
        thingEditBtnToField = getThingEditBtnToField();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentEditor != null) {
            currentEditor.registerForReceive(mainActivity);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (currentEditor != null) {
            currentEditor.unregisterForReceive(mainActivity);
        }
    }

    protected abstract int getHelpMessage();

    protected abstract Map<Integer, Field> getThingEditBtnToField();

    protected void setSavedState(Bundle savedInstanceState) {
        lastSelectedThingId = savedInstanceState.getInt(STATE_LAST_SELECTED_THING_ID);
        isEditMode = savedInstanceState.getBoolean(STATE_IS_EDIT_MODE);
        modeChanged = true;
        savedState = new SavedState();
        savedState.editedThing = savedInstanceState.getParcelable(STATE_EDITED_THING);
    }

    @Override
    protected void onMapSetup() {
        // check if mappings are initialized
        if (mainActivity.objMap == null) {
            mainActivity.initializeMaps(this, false);
            // later onResponseReceived will be called, with all mappings set
        }
        else {
            // mappings are already ready, calling onResponseReceived with dummy parameters,
            // they are not read anyway
            onResponseReceived(null, 0, null);
        }
    }

    @Override
    public void onResponseReceived(String originalUri, int statusCode, String response) {
        // this is called only when mappings are initialized
        thingIdSet = getThingIdSet();
        if (!thingIdSet.isEmpty()) {
            showThingScrollView();
        }
        setObjMap(mainActivity.objMap);
        setMarkers();
        createThingViews();
    }

    protected void showThingScrollView() {
        rootView.findViewById(R.id.thing_scroll_view).setVisibility(View.VISIBLE);
    }

    protected void hideThingScrollView() {
        rootView.findViewById(R.id.thing_scroll_view).setVisibility(View.GONE);
    }

    // subclasses must return their thingMap.keySet here
    protected abstract Set<Integer> getThingIdSet();

    // subclasses must implement this with return (Object) thingMap.get(thingId)
    protected abstract Object getThingFromId(int thingId);

    // subclasses must implement this with thingMap.put(thingId, (Thing) thing)
    protected abstract void putThingWithId(int thingId, Object thing);

    // subclasses must implement this with thingMap.remove(thingId)
    protected abstract void removeThingWithId(int thingId);

    // subclasses can override this method if they don't want all the objectives
    protected void setObjMap(Map<Integer, Objective> allObjs) {
        objMap = allObjs;
    }

    protected void setMarkers() {
        for (int objId : objMap.keySet()) {
            Objective obj = objMap.get(objId);
            Marker objMarker = placeMarker(obj.position_latitude, obj.position_longitude,
                    obj.name, obj.description);
            objMarker.setIcon(defaultMarkerIcon);
            objMarkerMap.put(objId, objMarker);
            markerObjMap.put(objMarker, objId);
        }
    }

    protected void createThingViews() {
        for (int thingId : thingIdSet) {
            Object thing;
            if (lastSelectedThingId == thingId && isEditMode) {
                thing = savedState.editedThing;
            }
            else {
                thing = getThingFromId(thingId);
            }
            createThingView(thingId, thing);
        }
        mainActivity.dismissLoadingDialog();
        if (lastSelectedThingId != NOTHING_SELECTED) {
            if (isEditMode) {
                restoreIfEditMode();
            }
            else {
                selectThing(lastSelectedThingId, false);
            }
        }
        savedState = null;
        rootView.findViewById(R.id.new_thing_btn).setOnClickListener(this);
    }


    /**
     * subclasses can override this with their logic to restore more saved content (that they
     * stored in savedState overriding onSaveInstanceState) when app was in edit mode when
     * deallocated for memory
     */
    protected void restoreIfEditMode() {
        // won't produce NullPointerException because since app was in edit mode there was a
        // selected thing for sure
        if (lastSelectedThingId == NEW_THING_ID) { // and if so automatically the selected one
            currentEditor = getNewThingEditor(getThingFromId(lastSelectedThingId));
        }
        else {
            currentEditor = getThingEditor(lastSelectedThingId, getThingFromId(lastSelectedThingId));
        }
        currentEditor.startEdit(savedState.editedThing);
    }

    // subclasses must implement this with their logic to create a new thing
    protected abstract Object createNewThing();

    @Override
    public void onClick(View v) {
        newThingBtnClick();
    }

    protected void newThingBtnClick() {
        Object thing = createNewThing();
        putThingWithId(NEW_THING_ID, thing);
        ViewGroup thingView = createThingView(NEW_THING_ID, thing);
        ThingEditor newThingEditor = getNewThingEditor(thing);
        thingView.findViewById(R.id.edit_btn).setOnClickListener(newThingEditor);
        newThingEditor.startEdit(thing);
        showThingScrollView();
    }

    public ViewGroup createThingView(int thingId, Object thing) {
        ViewGroup thingView = (ViewGroup) inflater
                .inflate(getThingLayoutTemplate(), thingContainer, false);
        thingViewMap.put(thingId, thingView);
        fillThingView(thingView, thing);
        thingView.setOnClickListener(new ThingHighlighter(thingId, thingView));
        thingContainer.addView(thingView);
        return  thingView;
    }

    protected abstract int getThingLayoutTemplate();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_LAST_SELECTED_THING_ID, lastSelectedThingId);
        outState.putBoolean(STATE_IS_EDIT_MODE, isEditMode);
        if (isEditMode) {
            outState.putParcelable(STATE_EDITED_THING, currentEditor.getEditedThing());
        }
    }

    /**
     * Fills a thingView with all the thing properties and optionally fills its tags.
     * @param thingView the view to be filled
     * @param thing the thing object
     */
    protected abstract void fillThingView(ViewGroup thingView, Object thing);

    /**
     * Highlights a thingView by changing its background color to edit or select color and scrolling
     * the ScrollView so that the thingView is completely visible.
     * Also highlights the objective markers by calling highlightObjectiveMarkers().
     * @param thingId id of the thing whose view is to be selected
     * @param forEdit whether to highlight the thingView for editing or selecting
     */
    private void selectThing(int thingId, boolean forEdit) {
        final ViewGroup thingView = thingViewMap.get(thingId);
        if (thingId != lastSelectedThingId || modeChanged) {
            ViewGroup lastSelectedThing = thingViewMap.get(lastSelectedThingId);
            if (lastSelectedThing != null) {
                lastSelectedThing.setBackgroundResource(getDefaultBackground());
            }
            int selectedBackground = forEdit ? R.drawable.edited : R.drawable.selected;
            thingView.setBackgroundResource(selectedBackground);
            lastSelectedThingId = thingId;
            modeChanged = false;
            highlightObjectiveMarkers(getObjectivesFromThingId(thingId), forEdit);
        }
        // move camera to the objectives
        animateCameraToObjectives(getObjectivesFromThingId(thingId));
        // scroll to the selected thing on next screen update
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ((ScrollView) thingContainer.getParent()).scrollTo(0, thingView.getTop());
            }
        });
    }

    protected abstract int getDefaultBackground();

    /**
     * Move the camera with an animation on the objectives on the map
     *
     * @param objectives set of the objectives to move to
     */
    protected void animateCameraToObjectives(final Set<Integer> objectives) {
        if (objectives != null && objectives.size() > 0) {
            final CameraUpdate cameraUpdate;
            if (objectives.size() == 1) {
                Marker objMarker = objMarkerMap.get(objectives.iterator().next());
                cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .zoom(SINGLE_OBJECTIVE_ZOOM).target(objMarker.getPosition()).build());
            } else {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int objective : objectives) {
                    Marker marker = objMarkerMap.get(objective);
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, CAMERA_PADDING);
            }
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.animateCamera(cameraUpdate);
                }
            });
        }
    }

    protected abstract Set<Integer> getObjectivesFromThingId(int thingId);

    /**
     * Highlights the objectives's markers with select or edit color.
     *
     * @param objectives the objectives whose markers are to be highlighted
     * @param forEdit whether highlight with edit or select color
     */
    private void highlightObjectiveMarkers(Set<Integer> objectives, boolean forEdit) {
        for (int objId : objMap.keySet()) {
            objMarkerMap.get(objId).setIcon(defaultMarkerIcon);
        }
        for (int objId : objectives) {
            BitmapDescriptor icon = forEdit ? editedMarkerIcon : selectedMarkerIcon;
            objMarkerMap.get(objId).setIcon(icon);
        }
    }

    private void setEditMode() {
        isEditMode = true;
        modeChanged = true;
    }

    private void setNormalMode() {
        isEditMode = false;
        modeChanged = true;
    }

    /**
     * Class responsible for listening to the thingView touch and request its highlighting.
     */
    protected class ThingHighlighter implements View.OnClickListener {

        protected int thingId;

        public ThingHighlighter(int thingId, ViewGroup thingView) {
            this.thingId = thingId;
            thingView.findViewById(R.id.edit_btn).setOnClickListener(this);
            thingView.findViewById(R.id.delete_btn).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId != R.id.edit_btn && vId != R.id.delete_btn) {
                if (!isEditMode) {
                    selectThing(thingId, false);
                }
            }
            else {
                Object thing = getThingFromId(thingId);
                ThingEditor thingEditor = getThingEditor(thingId, thing);
                thingEditor.startEdit(thing);
                if (vId == R.id.delete_btn) {
                    thingEditor.confirmDelete();
                }
            }
        }
    }

    /**
     * Subclasses implements this method by creating and returning a concrete implementation of
     * ThingEditor.
     *
     * @param thingId id of the thing to edit
     * @param thing the thing object to edit
     * @return the subclass concrete implementation of ThingEditor
     */
    protected abstract ThingEditor getThingEditor(int thingId, Object thing);

    /**
     * Subclasses implements this method by creating and returning a concrete implementation of
     * ThingEditor that is supposed to edit a newly created thing.
     *
     * @param thing the thing object to edit
     * @return the subclass concrete implementation of NewThingEditor
     */
    protected abstract ThingEditor getNewThingEditor(Object thing);

    /**
     * Class responsible for everything related to editing a thing
     */
    protected abstract class ThingEditor extends DialogBroadcastReceiver implements
            View.OnClickListener,
            ServiceInquirer.OnResponseReceivedCallback,
            ConfirmCancelDialog.DialogCallbacks {

        public int thingId;
        public Object originalThing;
        public Object editedThing;
        public ViewGroup thingView;
        public ObjectiveSelector objectiveSelector;
        public boolean tagsChanged = false;

        public ThingEditor(Context context, int thingId, Object thing) {
            super(context);
            this.thingId = thingId;
            originalThing = thing;
            thingView = thingViewMap.get(thingId);
        }

        public Parcelable getEditedThing() {
            return (Parcelable) editedThing;
        }

        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == saveEditBtn.getId()) {
                saveEdit();
            }
            else if (vId == cancelEditBtn.getId()) {
                cancelEdit();
            }
            else {
                if (thingEditBtnToField.keySet().contains(vId)) {
                    int editedFieldId = thingEditBtnToField.get(vId).id;
                    v = thingView.findViewById(editedFieldId);
                    int titleResource = thingEditBtnToField.get(vId).title;
                    String text = ((TextView) v).getText().toString();
                    Bundle customParams = new Bundle();
                    customParams.putInt(EDITOR_DIALOG_FIELD, editedFieldId);
                    if (!isFieldInteger(editedFieldId)) {
                        new SingleEditTextDialog
                                .Builder(mainActivity).setParams(text)
                                .setCustomParams(customParams)
                                .setTitle(titleResource)
                                .show();
                    }
                    else {
                        new NumberPickerDialog.Builder(mainActivity)
                                .setParams(Integer.valueOf(text), maxForIntegerField(editedFieldId))
                                .setCustomParams(customParams)
                                .setTitle(titleResource)
                                .show();
                    }
                    //see onDialogConfirm (lastEditedFieldId != NO_FIELD part)
                }
                else {
                    otherOnClickBehaviour(v);
                }
            }
        }

        protected void confirmDelete() {
            Bundle customParams = new Bundle();
            customParams.putBoolean(EDITOR_DIALOG_DELETE, true);
            new MessageDialog.Builder(mainActivity)
                    .setMessage(R.string.dialog_confirm_delete_msg)
                    .setTitle(R.string.dialog_title_delete)
                    .setCustomParams(customParams)
                    .show();
        }

        protected abstract boolean isFieldInteger(int field);

        protected abstract int maxForIntegerField(int field);

        protected abstract void otherOnClickBehaviour(View v);

        protected abstract ObjectiveSelector newObjectiveSelector();

        protected abstract Object cloneEditedThing(Object editedThing);

        public void startEdit(Object editedThing) {
            listenToEditBtns();
            setEditMode();
            currentEditor = this;
            this.editedThing = cloneEditedThing(editedThing);
            objectiveSelector = newObjectiveSelector();
            // show help btn
            helpBtn.setVisibility(View.VISIBLE);
            showEditFieldBtns();
            // register for handle save-cancel touches, show them and hide new thing btn
            newThingBtn.setVisibility(View.GONE);
            editControls.setVisibility(View.VISIBLE);
            cancelEditBtn.setOnClickListener(this);
            saveEditBtn.setOnClickListener(this);
            hideAllEditDeleteBtns();
            selectThing(thingId, true);
        }

        public void quitEdit() {
            unlistenToEditBtns();
            unregisterForReceive(mainActivity);
            setNormalMode();
            editedThing = null;
            currentEditor = null;
            objectiveSelector.quitEdit();
            objectiveSelector = null;
            // hide help btn
            helpBtn.setVisibility(View.GONE);
            hideEditFieldBtns();
            // unregister for handle save-cancel touches, hide them and show new thing btn
            saveEditBtn.setOnClickListener(null);
            cancelEditBtn.setOnClickListener(null);
            editControls.setVisibility(View.GONE);
            newThingBtn.setVisibility(View.VISIBLE);
            showAllEditDeleteBtns();
            selectThing(thingId, false);
        }

        protected void listenToEditBtns() {
            for (int id : thingEditBtnToField.keySet()) {
                thingView.findViewById(id).setOnClickListener(this);
            }
        }

        protected void unlistenToEditBtns() {
            for (int id : thingEditBtnToField.keySet()) {
                thingView.findViewById(id).setOnClickListener(null);
            }
        }

        protected void showEditFieldBtns() {
            for (int id : thingEditBtnToField.keySet()) {
                thingView.findViewById(id).setVisibility(View.VISIBLE);
            }
        }

        protected void hideEditFieldBtns() {
            for (int id : thingEditBtnToField.keySet()) {
                thingView.findViewById(id).setVisibility(View.GONE);
            }
        }

        public void saveEdit() {
            mainActivity.showLoadingDialog(R.string.saving_message);
            String json = getJsonForSave();
            sendSaveRequest(json);
        }

        protected abstract String getJsonForSave();

        protected abstract void sendSaveRequest(String json);

        public void cancelEdit() {
            objectiveSelector.cancelEdit();
            fillThingView(thingView, originalThing);
            quitEdit();
        }

        private void hideAllEditDeleteBtns() {
            for(int id : thingViewMap.keySet()) {
                ViewGroup view = thingViewMap.get(id);
                view.findViewById(R.id.edit_btn).setVisibility(View.GONE);
                view.findViewById(R.id.delete_btn).setVisibility(View.GONE);
            }
        }

        private void showAllEditDeleteBtns() {
            for(int id : thingViewMap.keySet()) {
                ViewGroup view = thingViewMap.get(id);
                view.findViewById(R.id.edit_btn).setVisibility(View.VISIBLE);
                view.findViewById(R.id.delete_btn).setVisibility(View.VISIBLE);
            }
        }

        /**
         * Called when user confirms an edit of field in the dialog
         *
         */
        @Override
        public void onDialogConfirm(final Bundle customParams, final Bundle userInput) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int lastEditedFieldId = customParams.getInt(EDITOR_DIALOG_FIELD, NO_FIELD);
                    boolean deleteWasTouched = customParams.getBoolean(EDITOR_DIALOG_DELETE);
                    if (lastEditedFieldId != NO_FIELD) {
                        // user was editing a text field
                        String newValue;
                        if (isFieldInteger(lastEditedFieldId)) {
                            int newIntValue = userInput.getInt(NumberPickerDialog.ARG_VALUE);
                            newValue = Integer.toString(newIntValue);
                        }
                        else {
                           newValue = userInput.getString(SingleEditTextDialog.ARG_TEXT);
                        }
                        updateEditedThingField(lastEditedFieldId, newValue);
                        ((TextView) thingView.findViewById(lastEditedFieldId)).setText(newValue);
                    }
                    else if (deleteWasTouched) {
                        delete();
                    }
                    else {
                        otherOnDialogConfirm(customParams, userInput);
                    }
                }
            });
        }

        protected abstract void updateEditedThingField(int field, String newValue);

        protected void delete() {
            mainActivity.showLoadingDialog(R.string.deleting_message);
            sendDeleteRequest(getJsonForDelete());
        }

        protected abstract String getJsonForDelete();

        protected abstract void sendDeleteRequest(String json);

        protected abstract void otherOnDialogConfirm(Bundle customParams, Bundle userInput);

        @Override
        public void onDialogCancel() {}
        @Override
        public void onDialogDelete(Bundle customParams) {}

        /**
         * Shows user readable info of the response received from the webservice related
         * to the edit thing request.
         *
         * @param originalUri the uri the request was made to
         * @param statusCode HTTP status code of the response
         * @param response body of the response
         */
        @Override
        public void onResponseReceived(String originalUri, int statusCode, String response) {
            mainActivity.dismissLoadingDialog();
            if (statusCode == WebServiceRequestService.NO_INTERNET_CONNECTION) {
                NoConnectionDialog.show(mainActivity);
                return;
            }
            if (originalUri.equals(getSaveUri())) {
                if (statusCode == HttpStatus.SC_OK) {
                    Toast.makeText(getActivity(), getString(R.string.successful_save),
                            Toast.LENGTH_SHORT).show();
                    successfulSave(response);
                    originalThing = editedThing;
                    putThingWithId(thingId, editedThing);
                    quitEdit();
                }
            }
            else if (originalUri.equals(getDeleteUri())) {
                if (statusCode == HttpStatus.SC_OK) {
                    Toast.makeText(getActivity(), getString(R.string.successful_delete),
                            Toast.LENGTH_SHORT).show();
                    removeThingWithId(thingId);
                    destroyThingView();
                    currentEditor = null;
                }
            }
        }

        protected void destroyThingView() {
            thingViewMap.remove(thingId);
            thingContainer.removeView(thingView);
            lastSelectedThingId = NOTHING_SELECTED;
            if (thingIdSet.isEmpty()) {
                hideThingScrollView();
            }
        }

        protected abstract String getSaveUri();

        protected abstract String getDeleteUri();

        /**
         * Called when save request to the webservice was succesful
         *
         * @param response json string of the response from the webservice
         */
        protected abstract void successfulSave(String response);

    }

}
