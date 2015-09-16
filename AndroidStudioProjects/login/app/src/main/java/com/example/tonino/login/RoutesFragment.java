package com.example.tonino.login;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import com.example.tonino.login.Dialogs.NoConnectionDialog;
import com.example.tonino.login.Dialogs.NumberPickerDialog;
import com.example.tonino.login.Dialogs.TagsDialog;
import com.example.tonino.login.Types.Objective;
import com.example.tonino.login.Types.Route;
import com.google.android.gms.maps.model.Marker;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Danny on 26/05/2015.
 */
public class RoutesFragment extends MapWithListFragment {

    public static final String STATE_NEW_OBJECTIVES = "new_objectives";
    public static final String STATE_EDITING_OBJECTIVE = "editing_objective";

    private Map<Integer, Route> routeMap;

    public RoutesFragment() {}

    protected class RouteSavedState extends SavedState {
        public Objective[] newObjectives;

        public RouteSavedState(SavedState savedState) {
            editedThing = savedState.editedThing;
        }
    }

    @Override
    protected int getHelpMessage() {
        return R.string.help_edit_routes;
    }

    @Override
    protected Map<Integer, Field> getThingEditBtnToField() {
        Map<Integer, Field> editBtnToField = new HashMap<>();
        editBtnToField.put(R.id.routeNameEdit,
                new Field(R.id.routeName, R.string.route_name_tag));
        editBtnToField.put(R.id.routeDescriptionEdit,
                new Field(R.id.routeDescription, R.string.route_description_tag));
        editBtnToField.put(R.id.routeValidityEdit,
                new Field(R.id.routeValidity, R.string.route_validity_tag));
        return editBtnToField;
    }

    @Override
    protected void setSavedState(Bundle savedInstanceState) {
        super.setSavedState(savedInstanceState);
        RouteSavedState routeSavedState = new RouteSavedState(savedState);
        Parcelable [] parceledObjectivesArray = savedInstanceState
                .getParcelableArray(STATE_NEW_OBJECTIVES);
        if (parceledObjectivesArray != null) {
            routeSavedState.newObjectives = new Objective[parceledObjectivesArray.length];
            int i = 0;
            for (Parcelable parceledObjective : parceledObjectivesArray) {
                routeSavedState.newObjectives[i] = (Objective) parceledObjective;
                i++;
            }
        }
        savedState = routeSavedState;
    }

    @Override
    protected Set<Integer> getThingIdSet() {
        routeMap = mainActivity.routeMap;
        return routeMap.keySet();
    }

    @Override
    protected Object getThingFromId(int thingId) {
        return routeMap.get(thingId);
    }

    @Override
    protected void putThingWithId(int thingId, Object thing) {
        routeMap.put(thingId, (Route) thing);
    }

    @Override
    protected void removeThingWithId(int thingId) {
        routeMap.remove(thingId);
    }

    @Override
    protected void restoreIfEditMode() {
        super.restoreIfEditMode();
        Objective [] savedNewObjectives = ((RouteSavedState) savedState).newObjectives;
        if (savedNewObjectives != null) {
            ((ObjectiveEditor) currentEditor.objectiveSelector).setNewObjectives(savedNewObjectives);
        }
    }

    @Override
    protected Object createNewThing() {
        Route route = new Route();
        route.name = getString(R.string.default_name);
        route.description = getString(R.string.default_description);
        route.validity_days = 1;
        route.id = NEW_THING_ID;
        return route;
    }

    @Override
    protected int getThingLayoutTemplate() {
        return R.layout.template_route_edit_operator;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isEditMode) {
            ObjectiveEditor objectiveEditor = (ObjectiveEditor) currentEditor.objectiveSelector;
            outState.putParcelableArray(STATE_NEW_OBJECTIVES,
                    objectiveEditor.getNewObjectives());
            outState.putBoolean(STATE_EDITING_OBJECTIVE,
                    objectiveEditor.isEditingObjective);
        }
    }

    @Override
    protected int getDefaultBackground() {
        return R.drawable.button_ricerca;
    }

    /**
     * Fills a routeView with all the route's properties and optionally fills its tags.
     *
     * @param thingView the route view to be filled
     * @param thing the Route object
     */
    @Override
    protected void fillThingView(ViewGroup thingView, Object thing) {
        Route route = (Route) thing;
        route.fillView(inflater, thingView);
    }

    @Override
    protected Set<Integer> getObjectivesFromThingId(int thingId) {
        if (!isEditMode) {
            return routeMap.get(thingId).objectives;
        }
        else {
            return ((Route) currentEditor.editedThing).objectives;
        }
    }

    @Override
    protected ThingEditor getThingEditor(int thingId, Object thing) {
        return new RouteEditor(thingId, thing);
    }

    @Override
    protected ThingEditor getNewThingEditor(Object thing) {
        return new NewRouteEditor(thing);
    }

    /**
     * Class responsible for everything related to editing a route.
     */
    private class RouteEditor extends ThingEditor {

        protected ViewGroup tagsContainer;
        protected TagsHandler tagsHandler;
        // used when saving route edits for storing them until the webservice response is received
        protected Marker [] addedMarkers;
        protected Objective[] addedObjectives;

        public RouteEditor(int routeId, Object thing) {
            super(mainActivity, routeId, thing);
            tagsContainer = (ViewGroup) thingView.findViewById(R.id.tags_container);
        }

        @Override
        protected boolean isFieldInteger(int field) {
            return (field == R.id.routeValidity);
        }

        @Override
        protected int maxForIntegerField(int field) {
            return NumberPickerDialog.DEFAULT_MAX;
        }

        @Override
        protected void otherOnClickBehaviour(View v) {
            prepareForAddingTags();
        }

        @Override
        protected ObjectiveEditor newObjectiveSelector() {
            Route editedRoute = (Route) editedThing;
            return new ObjectiveEditor(mainActivity, mMap, markerObjMap, editedRoute.objectives);
        }

        @Override
        protected Object cloneEditedThing(Object editedThing) {
            Route editedRoute = (Route) editedThing;
            return editedRoute.clone();
        }

        @Override
        public void startEdit(Object editedThing) {
            // show add tag btn
            thingView.findViewById(R.id.tag_new_btn).setVisibility(View.VISIBLE);
            super.startEdit(editedThing);
            tagsHandler = new TagsHandler(tagsContainer, ((Route) this.editedThing).tags);
            tagsHandler.showAndHandleDeleteTagBtnTouch();
        }

        @Override
        public void quitEdit() {
            tagsHandler.hideDeleteTagBtns();
            tagsHandler = null;
            // hide add tag btn
            thingView.findViewById(R.id.tag_new_btn).setVisibility(View.GONE);
            super.quitEdit();
        }

        @Override
        protected void listenToEditBtns() {
            super.listenToEditBtns();
            thingView.findViewById(R.id.tag_new_btn).setOnClickListener(this);
        }

        @Override
        protected void unlistenToEditBtns() {
            super.unlistenToEditBtns();
            thingView.findViewById(R.id.tag_new_btn).setOnClickListener(null);
        }

        @Override
        protected String getJsonForSave() {
            Route editedRoute = (Route) editedThing;
            Set<String> removedTags = getRemovedTags();
            Set<String> addedTags = getAddedTags();
            Set<Integer> removedObjs = getRemovedObjectives();
            Set<Integer> addedObjs = getAddedObjectives();
            getNewObjectivesFromObjectiveEditor();
            return new JSONParser.RouteJsonifier(editedRoute).putId().putFields()
                    .putAddedTags(addedTags).putRemovedTags(removedTags).putAddedObjs(addedObjs)
                    .putRemovedObjs(removedObjs).putNewObjs(addedObjectives).toString();
        }

        protected void getNewObjectivesFromObjectiveEditor() {
            addedMarkers = null;
            addedObjectives = null;
            Map<Marker, Objective> newMarkerObjMap = ((ObjectiveEditor) objectiveSelector)
                    .getNewMarkerObjMap();
            if (newMarkerObjMap.size() > 0) {
                addedMarkers = new Marker[newMarkerObjMap.keySet().size()];
                addedObjectives = new Objective[newMarkerObjMap.keySet().size()];
                int i = 0;
                for (Marker marker : newMarkerObjMap.keySet()) {
                    addedMarkers[i] = marker;
                    addedObjectives[i] = newMarkerObjMap.get(marker);
                    i++;
                }
            }
        }

        protected Set<String> getRemovedTags() {
            Route editedRoute = (Route) editedThing;
            Route originalRoute = (Route) originalThing;
            Set<String> removedTags = new HashSet<>(originalRoute.tags);
            removedTags.removeAll(editedRoute.tags);
            return removedTags;
        }

        protected Set<String> getAddedTags() {
            Route editedRoute = (Route) editedThing;
            Route originalRoute = (Route) originalThing;
            Set<String> addedTags = new HashSet<>(editedRoute.tags);
            addedTags.removeAll(originalRoute.tags);
            return addedTags;
        }

        protected Set<Integer> getRemovedObjectives() {
            Route editedRoute = (Route) editedThing;
            Route originalRoute = (Route) originalThing;
            Set<Integer> removedObjs = new HashSet<>(originalRoute.objectives);
            removedObjs.removeAll(editedRoute.objectives);
            return removedObjs;
        }

        protected Set<Integer> getAddedObjectives() {
            Route editedRoute = (Route) editedThing;
            Route originalRoute = (Route) originalThing;
            Set<Integer> addedObjs = new HashSet<>(editedRoute.objectives);
            addedObjs.removeAll(originalRoute.objectives);
            return addedObjs;
        }

        @Override
        protected void sendSaveRequest(String json) {
            serviceInquirer.put(mainActivity, getSaveUri(), json, this);
        }

        private void prepareForAddingTags() {
            Set<String> allTags = mainActivity.allTags;
            if (allTags == null) {
                mainActivity.showLoadingDialog(R.string.loading_tags_message);
                serviceInquirer.get(mainActivity, getString(R.string.WEB_SERVICE_TAGS),
                        new ServiceInquirer.OnResponseReceivedCallback() {
                            @Override
                            public void onResponseReceived(String originalUri, int statusCode, String response) {
                                switch (statusCode) {
                                    case HttpStatus.SC_OK:
                                        JSONParser parser = new JSONParser(response);
                                        Set<String> allTags = parser.getTags();
                                        mainActivity.allTags = allTags;
                                        mainActivity.dismissLoadingDialog();
                                        showTagsDialog(allTags);
                                        break;
                                    case WebServiceRequestService.NO_INTERNET_CONNECTION:
                                        NoConnectionDialog.show(mainActivity);
                                        break;
                                }
                            }
                        });
            }
            else {
                showTagsDialog(allTags);
            }
        }

        private void showTagsDialog(Set<String> allTags) {
            tagsHandler.createTagsDialog(mainActivity, allTags).show();
            // see otherOnDialogConfirm
        }

        @Override
        protected void updateEditedThingField(int field, String newValue) {
            Route editedRoute = (Route) editedThing;
            switch (field) {
                case R.id.routeName:
                    editedRoute.name = newValue;
                    break;
                case R.id.routeDescription:
                    editedRoute.description = newValue;
                    break;
                case R.id.routeValidity:
                    editedRoute.validity_days = Integer.parseInt(newValue);
                    break;
            }
        }

        @Override
        protected String getJsonForDelete() {
            return new JSONParser.RouteJsonifier((Route) originalThing).putIdForDelete().toString();
        }

        @Override
        protected void sendDeleteRequest(String json) {
            serviceInquirer.delete(mainActivity, getDeleteUri(), json, this);
        }

        /**
         * Called on select new tags dialog confirm
         *
         * @param customParams bundle with custom parameters set when the dialog was created
         * @param userInput bundle with the selected tags
         */
        @Override
        protected void otherOnDialogConfirm(Bundle customParams, Bundle userInput) {
            // the dialog that sent the broadcast wasn't the tags one, so it must be the
            // ObjectiveEditor one
            if (!customParams.getBoolean(TagsHandler.HANDLER_DIALOG_TAGS, false)) {
                ((ObjectiveEditor) objectiveSelector).onDialogConfirm(customParams, userInput);
            }
            else {
                // the dialog was the tags one
                Route editedRoute = (Route) editedThing;
                Set<String> selectedTags = new HashSet<>();
                selectedTags.addAll(userInput
                        .getStringArrayList(TagsDialog.ARG_SELECTED_TAGS));
                if (!selectedTags.isEmpty()) {
                    editedRoute.tags.addAll(selectedTags);
                    tagsHandler.writeTags(inflater);
                    tagsChanged = true;
                }
            }
        }

        @Override
        public void onDialogDelete(Bundle customParams) {
            ((ObjectiveEditor) objectiveSelector).onDialogDelete(customParams);
        }

        @Override
        protected String getSaveUri() {
            return getString(R.string.WEB_SERVICE_ROUTES_EDIT);
        }

        @Override
        protected String getDeleteUri() {
            return getString(R.string.WEB_SERVICE_ROUTES_DELETE);
        }

        /**
         * Called when save request to the webservice was succesful
         *
         * @param response json string of the response from the webservice
         */
        @Override
        protected void successfulSave(String response) {
            Route editedRoute = (Route) editedThing;
            // removing the objectives also from the mandatory ones of the prizes
            Set<Integer> removedObjs = getRemovedObjectives();
            for(int prizeId : editedRoute.prizes.keySet()) {
                Route.PrizeInfo prizeInfo = editedRoute.prizes.get(prizeId);
                for(int objectiveId : removedObjs) {
                    prizeInfo.mandatoryObjs.remove(objectiveId);
                }
            }
            if (addedObjectives != null && addedObjectives.length > 0) {
                JSONParser jsonParser = new JSONParser(response);
                Objective[] results = jsonParser
                        .getNewObjectivesIdAndValCode();
                for (int i = 0; i < results.length; i++) {
                    addedObjectives[i].id = results[i].id;
                    addedObjectives[i].validation_code = results[i]
                            .validation_code;
                    objMap.put(addedObjectives[i].id, addedObjectives[i]);
                    objMarkerMap.put(addedObjectives[i].id,
                            addedMarkers[i]);
                    markerObjMap.put(addedMarkers[i],
                            addedObjectives[i].id);
                    editedRoute.objectives.add(addedObjectives[i].id);
                }
            }
        }

    }

    private class NewRouteEditor extends RouteEditor {

        public NewRouteEditor(Object thing) {
            super(NEW_THING_ID, thing);
        }

        @Override
        public void cancelEdit() {
            objectiveSelector.cancelEdit();
            quitEdit();
            routeMap.remove(NEW_THING_ID);
            destroyThingView();
        }

        @Override
        protected String getJsonForSave() {
            Route editedRoute = (Route) editedThing;
            getNewObjectivesFromObjectiveEditor();
            return new JSONParser.RouteJsonifier(editedRoute).putFields()
                    .putAddedTags(getAddedTags()).putAddedObjs(getAddedObjectives())
                    .putNewObjs(addedObjectives).toString();
        }

        @Override
        protected String getSaveUri() {
            return getString(R.string.WEB_SERVICE_ROUTES_ADD);
        }

        @Override
        protected void sendSaveRequest(String json) {
            serviceInquirer.post(mainActivity, getSaveUri(), json,
                    this);
        }

        @Override
        protected void successfulSave(String response) {
            Route editedRoute = (Route) editedThing;
            routeMap.remove(NEW_THING_ID);
            thingViewMap.remove(NEW_THING_ID);
            editedRoute.id = new JSONParser(response).getNewRouteId();
            thingId = editedRoute.id;
            thingViewMap.put(editedRoute.id, thingView);
            thingView.setOnClickListener(new ThingHighlighter(thingId, thingView));
            super.successfulSave(response);
        }
    }

}