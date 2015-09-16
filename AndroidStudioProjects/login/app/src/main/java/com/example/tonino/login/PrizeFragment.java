
package com.example.tonino.login;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tonino.login.Dialogs.DialogBroadcastReceiver;
import com.example.tonino.login.Dialogs.MessageDialog;
import com.example.tonino.login.Dialogs.NumberPickerDialog;
import com.example.tonino.login.Dialogs.ScrollViewDialog;
import com.example.tonino.login.Dialogs.YesNoDialog;
import com.example.tonino.login.Types.Objective;
import com.example.tonino.login.Types.Prize;
import com.example.tonino.login.Types.Route;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Danny on 17/06/2015.
 */
public class PrizeFragment extends MapWithListFragment {

    public static final String ARG_ROUTE_ID = "route_id";

    public static final String EDITED_PRIZE_INFO = "edited_prize_info";

    public static final String DIALOG_CREATE_OR_SELECT = "create_or_select";

    public static final int PRIZE_MIN_PROBABILITY = 1;
    public static final int PRIZE_MAX_PROBABILITY = 100;
    public static final int PRIZE_DEFAULT_REPEAT_DAYS = 1;
    public static final int PRIZE_DEFAULT_VALIDATION_METHOD = 1;
    public static final boolean PRIZE_DEFAULT_VISIBILITY = true;

    public Route selectedRoute;
    protected Map<Integer, Prize> prizeMap;
    protected NewBtnDialogHandler newBtnDialogHandler;

    protected class PrizeSavedState extends SavedState {
        public Route.PrizeInfo editedPrizeInfo;

        public PrizeSavedState(SavedState savedState) {
            super(savedState);
        }
    }

    @Override
    protected int getHelpMessage() {
        return R.string.help_edit_prizes;
    }

    @Override
    protected void onContinueCreateView(View rootView, Bundle savedInstanceState) {
        newBtnDialogHandler = new NewBtnDialogHandler(this);
        super.onContinueCreateView(rootView, savedInstanceState);
    }

    @Override
    protected Map<Integer, Field> getThingEditBtnToField() {
        Map<Integer, Field> fieldMap = new HashMap<>();
        fieldMap.put(R.id.prize_name_edit, new Field(R.id.prize_name, R.string.name_label));
        fieldMap.put(R.id.prize_description_edit,
                new Field(R.id.prize_description, R.string.description_label));
        fieldMap.put(R.id.prize_repeat_edit,
                new Field(R.id.prize_repeat, R.string.prize_repeat_label));
        fieldMap.put(R.id.prize_probability_edit,
                new Field(R.id.prize_probability, R.string.prize_probability_label));
        return fieldMap;
    }

    @Override
    protected void setSavedState(Bundle savedInstanceState) {
        super.setSavedState(savedInstanceState);
        PrizeSavedState prizeSavedState = new PrizeSavedState(savedState);
        if (isEditMode) {
            prizeSavedState.editedPrizeInfo = savedInstanceState.getParcelable(EDITED_PRIZE_INFO);
        }
        savedState = prizeSavedState;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isEditMode) {
            outState.putParcelable(EDITED_PRIZE_INFO,
                    ((PrizeEditor) currentEditor).editedPrizeInfo);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isEditMode) {
            newBtnDialogHandler.registerForReceive(mainActivity);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        newBtnDialogHandler.unregisterForReceive(mainActivity);
    }

    @Override
    protected Set<Integer> getThingIdSet() {
        selectedRoute = mainActivity.routeMap.get(getArguments().getInt(ARG_ROUTE_ID));
        prizeMap = mainActivity.prizeMap;
        return selectedRoute.prizes.keySet();
    }

    @Override
    protected Object getThingFromId(int thingId) {
        return prizeMap.get(thingId);
    }

    @Override
    protected void putThingWithId(int thingId, Object thing) {
        prizeMap.put(thingId, (Prize) thing);
    }

    @Override
    protected void removeThingWithId(int thingId) {
        selectedRoute.prizes.remove(thingId);
    }

    /**
     * Overriding to get only the select route objectives
     *
     * @param allObjs a map with all the objectives
     */
    @Override
    protected void setObjMap(Map<Integer, Objective> allObjs) {
        objMap = new HashMap<>();
        for (int objective : selectedRoute.objectives) {
            objMap.put(objective, allObjs.get(objective));
        }
    }

    @Override
    protected void createThingViews() {
        super.createThingViews();
        animateCameraToObjectives(selectedRoute.objectives);
    }

    @Override
    protected void restoreIfEditMode() {
        super.restoreIfEditMode();
        ((PrizeEditor) currentEditor).editedPrizeInfo = ((PrizeSavedState) savedState)
                .editedPrizeInfo;
    }

    @Override
    protected void newThingBtnClick() {
        Bundle customParams = new Bundle();
        customParams.putBoolean(DIALOG_CREATE_OR_SELECT, true);
        new MessageDialog.Builder(mainActivity)
                .setMessage(R.string.dialog_msg_new_prize)
                .setTitle(R.string.dialog_title_new_prize)
                .setConfirmBtnText(R.string.dialog_create_option)
                .setDeleteBtnText(R.string.dialog_select_option)
                .setNoCancelButton()
                .setCustomParams(customParams)
                .show();
        animateCameraToObjectives(selectedRoute.objectives);
    }

    public class NewBtnDialogHandler extends DialogBroadcastReceiver {

        //PrizeFragment prizeFragment;

        public NewBtnDialogHandler(PrizeFragment prizeFragment) {
            super(mainActivity);
            //this.prizeFragment = prizeFragment;
        }

        @Override
        public void onDialogConfirm(Bundle customParams, Bundle userInput) {
            if (customParams.getBoolean(DIALOG_CREATE_OR_SELECT, false)) {
                PrizeFragment.super.newThingBtnClick();
            }
            else {
                int selectedPrizeId = userInput.getInt(ScrollViewDialog.ARG_OUT_SELECTED_ID);
                selectedRoute.prizes.put(selectedPrizeId,
                        new Route.PrizeInfo(getRemainingProbability(), new HashSet<Integer>()));
                Prize selectedPrize = prizeMap.get(selectedPrizeId);
                createThingView(selectedPrizeId, selectedPrize);
                showThingScrollView();
                new ExistingNewPrizeEditor(selectedPrizeId, selectedPrize).startEdit(selectedPrize);
            }
        }

        @Override
        public void onDialogCancel() {}

        @Override
        public void onDialogDelete(Bundle customParams) {
            Map<Integer, Prize> notInRoutePrizes = new HashMap<>(prizeMap);
            for(int prizeId : selectedRoute.prizes.keySet()) {
                notInRoutePrizes.remove(prizeId);
            }
            if (notInRoutePrizes.size() == 0) {
                Toast.makeText(mainActivity, getString(R.string.prize_no_selectables),
                        Toast.LENGTH_SHORT).show();
            }
            else {
                new ScrollViewDialog.Builder(mainActivity)
                        .setParams(notInRoutePrizes)
                        .setTitle(R.string.dialog_title_select_prize)
                        .setCustomParams(new Bundle())
                        .show();
            }
        }
    }

    int getRemainingProbability() {
        int remainingProbability = PRIZE_MAX_PROBABILITY;
        for (int prizeId : selectedRoute.prizes.keySet()) {
            remainingProbability -= selectedRoute.prizes.get(prizeId).probability;
        }
        return remainingProbability;
    }

    @Override
    protected Object createNewThing() {
        Prize prize = new Prize();
        prize.id = NEW_THING_ID;
        prize.name = getString(R.string.default_name);
        prize.description = getString(R.string.default_description);
        prize.operator = mainActivity.operatorInfo.id;
        prize.visible = PRIZE_DEFAULT_VISIBILITY;
        prize.repeat_days = PRIZE_DEFAULT_REPEAT_DAYS;
        prize.validation_method = PRIZE_DEFAULT_VALIDATION_METHOD;
        selectedRoute.prizes.put(NEW_THING_ID,
                new Route.PrizeInfo(getRemainingProbability(), new HashSet<Integer>()));
        return prize;
    }

    @Override
    protected int getThingLayoutTemplate() {
        return R.layout.template_prize_edit_operator;
    }

    @Override
    protected int getDefaultBackground() {
        return R.drawable.button_ricerca;
    }

    @Override
    protected void fillThingView(ViewGroup thingView, Object thing) {
        Prize prize = (Prize) thing;
        prize.fillView(thingView, selectedRoute.prizes.get(prize.id).probability);
    }

    @Override
    protected Set<Integer> getObjectivesFromThingId(int thingId) {
        if (!isEditMode) {
            return selectedRoute.prizes.get(thingId).mandatoryObjs;
        }
        else {
            return ((PrizeEditor) currentEditor).editedPrizeInfo.mandatoryObjs;
        }
    }

    @Override
    protected ThingEditor getThingEditor(int thingId, Object thing) {
        return new PrizeEditor(thingId, thing);
    }

    @Override
    protected ThingEditor getNewThingEditor(Object thing) {
        return new NewPrizeEditor(thing);
    }

    protected class PrizeEditor extends ThingEditor {

        protected Route.PrizeInfo originalPrizeInfo;
        protected Route.PrizeInfo editedPrizeInfo;

        public PrizeEditor(int thingId, Object thing) {
            super(mainActivity, thingId, thing);
            originalPrizeInfo = selectedRoute.prizes.get(thingId);
        }

        @Override
        protected boolean isFieldInteger(int field) {
            return ((field == R.id.prize_repeat) || (field == R.id.prize_probability));
        }

        @Override
        protected int maxForIntegerField(int field) {
            if (field == R.id.prize_probability) {
                return getRemainingProbability() + editedPrizeInfo.probability;
            }
            else {
                return NumberPickerDialog.DEFAULT_MAX;
            }
        }

        @Override
        protected void otherOnClickBehaviour(View v) {
            Prize editedPrize = (Prize) editedThing;
            Bundle customParams = new Bundle();
            // no need to set any custom parameter, the "is visible" dialog is the only "special"
            // one this subclass show
            new YesNoDialog.Builder(mainActivity)
                    .setParams(editedPrize.visible)
                    .setCustomParams(customParams)
                    .setTitle(R.string.prize_visible_label)
                    .show();
        }

        @Override
        protected ObjectiveSelector newObjectiveSelector() {
            return new ObjectiveSelector(mainActivity, mMap, markerObjMap,
                    editedPrizeInfo.mandatoryObjs);
        }

        @Override
        protected Object cloneEditedThing(Object editedThing) {
            return ((Prize) editedThing).clone();
        }

        @Override
        public void startEdit(Object editedThing) {
            editedPrizeInfo = new Route.PrizeInfo(originalPrizeInfo);
            showVisibilityEditBtn();
            newBtnDialogHandler.unregisterForReceive(mainActivity);
            super.startEdit(editedThing);
        }

        protected void showVisibilityEditBtn() {
            thingView.findViewById(R.id.prize_visible_edit).setVisibility(View.VISIBLE);
        }

        @Override
        public void quitEdit() {
            editedPrizeInfo = null;
            thingView.findViewById(R.id.prize_visible_edit).setVisibility(View.GONE);
            newBtnDialogHandler.registerForReceive(mainActivity);
            super.quitEdit();
        }

        @Override
        protected void listenToEditBtns() {
            super.listenToEditBtns();
            thingView.findViewById(R.id.prize_visible_edit).setOnClickListener(this);
        }

        @Override
        protected void unlistenToEditBtns() {
            super.unlistenToEditBtns();
            thingView.findViewById(R.id.prize_visible_edit).setOnClickListener(null);
        }

        @Override
        public void saveEdit() {
            if (editedPrizeInfo.mandatoryObjs.size() == 0) {
                Toast.makeText(mainActivity, getString(R.string.prize_error_no_mand_obj),
                        Toast.LENGTH_SHORT).show();
            }
            else {
                super.saveEdit();
            }
        }

        @Override
        protected String getJsonForSave() {
            Prize prize = (Prize) editedThing;
            return new JSONParser.PrizeJsonifier(prize).putId().putFields()
                    .putRouteId(selectedRoute.id)
                    .putProbability(editedPrizeInfo.probability)
                    .putAddedMandatoryObjs(getAddedMandObjs())
                    .putRemovedMandatoryObjs(getRemovedMandObjs()).toString();
        }

        protected Set<Integer> getAddedMandObjs() {
            Set<Integer> addedMandObjs = new HashSet<>(editedPrizeInfo.mandatoryObjs);
            addedMandObjs.removeAll(originalPrizeInfo.mandatoryObjs);
            return  addedMandObjs;
        }

        protected Set<Integer> getRemovedMandObjs() {
            Set<Integer> removedMandObjs = new HashSet<>(originalPrizeInfo.mandatoryObjs);
            removedMandObjs.removeAll(editedPrizeInfo.mandatoryObjs);
            return  removedMandObjs;
        }

        @Override
        protected void sendSaveRequest(String json) {
            serviceInquirer.put(mainActivity, getSaveUri(), json, this);
        }

        @Override
        protected void updateEditedThingField(int field, String newValue) {
            Prize editedPrize = (Prize) editedThing;
            switch (field) {
                case R.id.prize_name:
                    editedPrize.name = newValue;
                    break;
                case R.id.prize_description:
                    editedPrize.description = newValue;
                    break;
                case R.id.prize_repeat:
                    editedPrize.repeat_days = Integer.parseInt(newValue);
                    break;
                case R.id.prize_probability:
                    editedPrizeInfo.probability = Math.max(PRIZE_MIN_PROBABILITY,
                            Math.min(Integer.parseInt(newValue), PRIZE_MAX_PROBABILITY));
            }
            /*
            if(field == R.id.prize_name)
            {
                editedPrize.name = newValue;
            }
            else if(field == R.id.prize_description)
            {
                editedPrize.description = newValue;
            }
            else if(field == R.id.prize_repeat)
            {
                editedPrize.repeat_days = Integer.parseInt(newValue);
            }
            else if(field == R.id.prize_probability)
            {
                editedPrizeInfo.probability = Math.max(PRIZE_MIN_PROBABILITY,
                        Math.min(Integer.parseInt(newValue), PRIZE_MAX_PROBABILITY));
            }
            */
        }

        @Override
        protected String getJsonForDelete() {
            Prize prize = (Prize) originalThing;
            return new JSONParser.PrizeJsonifier(prize)
                    .putIdForRemoving()
                    .putRouteId(selectedRoute.id)
                    .toString();
        }

        @Override
        protected void sendDeleteRequest(String json) {
            serviceInquirer.delete(mainActivity, getDeleteUri(), json, this);
        }

        @Override
        protected void otherOnDialogConfirm(Bundle customParams, Bundle userInput) {
            Prize prize = (Prize) editedThing;
            prize.visible = userInput.getBoolean(YesNoDialog.ARG_VALUE);
            ((TextView) thingView.findViewById(R.id.prize_visible))
                    .setText(prize.visible ? Prize.VISIBLE : Prize.NOT_VISIBLE);
        }

        @Override
        protected String getSaveUri() {
            return getString(R.string.WEB_SERVICE_PRIZES_EDIT);
        }

        @Override
        protected String getDeleteUri() {
            return getString(R.string.WEB_SERVICE_ROUTE_REMOVE_PRIZE);
        }

        @Override
        protected void successfulSave(String response) {
            selectedRoute.prizes.put(thingId, editedPrizeInfo);
            originalPrizeInfo = editedPrizeInfo;
        }
    }

    protected class NewPrizeEditor extends PrizeEditor {

        public NewPrizeEditor(Object thing) {
            super(NEW_THING_ID, thing);
        }

        @Override
        public void cancelEdit() {
            quitEdit();
            prizeMap.remove(NEW_THING_ID);
            selectedRoute.prizes.remove(NEW_THING_ID);
            destroyThingView();
        }

        @Override
        protected String getSaveUri() {
            return getString(R.string.WEB_SERVICE_PRIZE_ADD);
        }

        @Override
        protected void sendSaveRequest(String json) {
            serviceInquirer.post(mainActivity, getSaveUri(), json, this);
        }

        @Override
        protected String getJsonForSave() {
            Prize prize = (Prize) editedThing;
            return new JSONParser.PrizeJsonifier(prize)
                    .putFields()
                    .putRouteId(selectedRoute.id)
                    .putProbability(editedPrizeInfo.probability)
                    .putAddedMandatoryObjs(getAddedMandObjs())
                    .toString();
        }

        @Override
        protected void successfulSave(String response) {
            Prize newPrize = (Prize) editedThing;
            thingId = newPrize.id = new JSONParser(response).getNewPrizeId();
            prizeMap.remove(NEW_THING_ID);
            selectedRoute.prizes.remove(NEW_THING_ID);
            thingViewMap.remove(NEW_THING_ID);
            thingViewMap.put(thingId, thingView);
            super.successfulSave(response);
            thingView.setOnClickListener(new ThingHighlighter(thingId, thingView));
        }
    }

    protected class ExistingNewPrizeEditor extends PrizeEditor {

        ExistingNewPrizeEditor(int prizeId, Prize prize) {
            super(prizeId, prize);
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            destroyThingView();
            selectedRoute.prizes.remove(thingId);
        }

        @Override
        protected void showEditFieldBtns() {
            // not showing the buttons, so user can't edit anything
            return;
        }

        @Override
        protected void showVisibilityEditBtn() {
            // same as above
            return;
        }

        @Override
        protected String getSaveUri() {
            return getString(R.string.WEB_SERVICE_ROUTE_ADD_PRIZE);
        }

        @Override
        protected String getJsonForSave() {
            Prize prize = (Prize) editedThing;
            return new JSONParser.PrizeJsonifier(prize)
                    .putIdForAdding()
                    .putRouteId(selectedRoute.id)
                    .putProbability(editedPrizeInfo.probability)
                    .putAddedMandatoryObjs(getAddedMandObjs())
                    .toString();
        }

        @Override
        protected void sendSaveRequest(String json) {
            serviceInquirer.post(mainActivity, getSaveUri(), json, this);
        }

    }

}
