package com.example.tonino.login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.tonino.login.Types.Objective;
import com.example.tonino.login.Types.Operator;
import com.example.tonino.login.Types.Prize;
import com.example.tonino.login.Types.Route;

/**
 * Created by Danny on 21/05/2015.
 */
public class JSONParser {

    public static final String ROUTES_KEY = "routes";
    public static final String ROUTE_ADDED_ID_KEY = "id_route";
    public static final String ROUTE_ID_KEY = "id";
    public static final String ROUTE_DESCRIPTION_KEY = "description";
    public static final String ROUTE_NAME_KEY = "name";
    public static final String ROUTE_TAGS_KEY = "tags";
    public static final String ROUTE_PRIZES_KEY = "prizes";
    public static final String ROUTE_VALIDITY_KEY = "validity_days";
    public static final String ROUTE_ADDED_TAGS_KEY = "tags_to_add";
    public static final String ROUTE_REMOVED_TAGS_KEY = "tags_to_remove";
    public static final String ROUTE_ADDED_OBJECTIVES_KEY = "id_objs_to_add";
    public static final String ROUTE_REMOVED_OBJECTIVES_KEY = "id_objs_to_remove";
    public static final String ROUTE_NEW_OBJECTIVES_KEY = "new_objs";
    public static final String OBJECTIVES_KEY = "objs";
    public static final String OBJECTIVE_ID_KEY = "id";
    public static final String OBJECTIVE_CITY_KEY = "city";
    public static final String OBJECTIVE_DESCRIPTION_KEY = "description";
    public static final String OBJECTIVE_LATITUDE_KEY = "position_latitude";
    public static final String OBJECTIVE_LONGITUDE_KEY = "position_longitude";
    public static final String OBJECTIVE_NAME_KEY = "name";
    public static final String OBJECTIVE_VALIDATION_METHOD_KEY = "validation_method";
    public static final String OBJECTIVE_VALIDATION_CODE_KEY = "validation_code";
    public static final String OPERATORS_KEY = "operators";
    public static final String OPERATOR_IS_OPERATOR_KEY = "is_operator";
    public static final String OPERATOR_FB_ID_KEY = "fb_id";
    public static final String OPERATOR_LOGIN_KEY = "login";
    public static final String OPERATOR_PASSWORD_KEY = "psw";
    public static final String OPERATOR_NAME_KEY = "name_operator";
    public static final String OPERATOR_TAGS_KEY = "tags";
    public static final String OPERATOR_LATITUDE_KEY = "position_latitude";
    public static final String OPERATOR_LONGITUDE_KEY = "position_longitude";
    public static final String OPERATOR_ADDED_TAGS_KEY = "tags_to_add";
    public static final String OPERATOR_REMOVED_TAGS_KEY = "tags_to_remove";
    public static final String PRIZES_KEY = "prizes";
    public static final String PRIZE_OPERATOR_KEY = "id_operator";
    public static final String PRIZE_DESCRIPTION_KEY = "description";
    public static final String PRIZE_VALIDATION_METHOD_KEY = "validation_method";
    public static final String PRIZE_MANDATORY_OBJECTIVES_KEY = "mandatory_objs";
    public static final String PRIZE_VISIBLE_KEY = "visible";
    public static final String PRIZE_REPEAT_DAYS_KEY = "repeat_days";
    public static final String PRIZE_PROBABILITY_KEY = "probability";
    public static final String PRIZE_ID_KEY = "id";
    public static final String PRIZE_NAME_KEY = "name";
    public static final String PRIZE_ROUTE_ID_KEY = "id_route";
    public static final String PRIZE_ADDED_MAND_OBJS_KEY = "mand_objs_to_add";
    public static final String PRIZE_REMOVED_MAND_OBJS_KEY = "mand_objs_to_remove";
    public static final String PRIZE_ID_FOR_REMOVING_KEY = "id_prize";
    public static final String PRIZE_ADDED_ID_KEY = "id_prize";
    public static final String PRIZE_VALIDATION_CODE_KEY = "validation_code";
    public static final String PRIZE_VERIFIED_ID_KEY = "id";
    public static final String PRIZE_LAST_WON_DATE_KEY = "last_won_date";
    public static final String TAGS_KEY = "tags";

    public static final int IS_OPERATOR = 1;

    /*
     * Parsed json, so the json string is only parsed once.
     */
    private JSONObject json;

    public JSONParser() {
    }

    public JSONParser(String jsonStr) {
        parseJSON(jsonStr);
    }

    public boolean parseJSON(String jsonStr) {
        boolean wellFormed = true;
        try {
            json = new JSONObject(jsonStr);
        } catch (JSONException e) {
            wellFormed = false;
        }
        return wellFormed;
    }

    private Map<Integer, Route> getRoutes_() throws JSONException {
        Map<Integer, Route> routesMap = new HashMap<>();
        JSONArray routesArr = json.getJSONArray(ROUTES_KEY);
        for (int i=0; i<routesArr.length(); i++) {
            JSONObject route = routesArr.getJSONObject(i);
            //getting objectives ids in the route
            JSONArray objsArr = route.getJSONArray(OBJECTIVES_KEY);
            Set<Integer> objs = new HashSet<>();
            for (int j=0; j<objsArr.length(); j++) {
                objs.add(objsArr.getInt(j));
            }
            //getting route's tags
            JSONArray tagsArr = route.getJSONArray(ROUTE_TAGS_KEY);
            Set<String> tags = new HashSet<>();
            for (int j=0; j<tagsArr.length(); j++) {
                tags.add(tagsArr.getString(j));
            }
            //getting prizes in the route
            JSONArray prizesArr = route.getJSONArray(ROUTE_PRIZES_KEY);
            Map <Integer, Route.PrizeInfo> prizesMap = new HashMap<>();
            for (int j=0; j<prizesArr.length(); j++) {
                JSONObject prize = prizesArr.getJSONObject(j);
                JSONArray mandatoryObjsArr = prize.getJSONArray(PRIZE_MANDATORY_OBJECTIVES_KEY);
                int probability = prize.getInt(PRIZE_PROBABILITY_KEY);
                Set<Integer> mandatoryObjsSet = new HashSet<>();
                for (int k=0; k<mandatoryObjsArr.length(); k++) {
                    mandatoryObjsSet.add(mandatoryObjsArr.getInt(k));
                }
                Route.PrizeInfo prizeInfo = new Route.PrizeInfo(probability, mandatoryObjsSet);
                prizesMap.put(prize.getInt(PRIZE_ID_KEY), prizeInfo);
            }
            Route routeObject = new Route();
            routeObject.id = route.getInt(ROUTE_ID_KEY);
            routeObject.objectives = objs;
            routeObject.description = route.getString(ROUTE_DESCRIPTION_KEY);
            routeObject.tags = tags;
            routeObject.prizes = prizesMap;
            routeObject.name = route.getString(ROUTE_NAME_KEY);
            routeObject.validity_days = route.getInt(ROUTE_VALIDITY_KEY);
            routesMap.put(routeObject.id, routeObject);
        }
        return routesMap;
    }

    public Map<Integer, Route> getRoutes() {
        Map<Integer, Route> routesMap;
        try {
            routesMap = getRoutes_();
        } catch (JSONException e) {
            routesMap = new HashMap<>();
            Route route = new Route();
            route.name = e.toString();
            route.description = e.getMessage();
            routesMap.put(0, route);
        }
        return routesMap;
    }

    private Map<Integer, Objective> getObjectives_() throws JSONException {
        Map<Integer, Objective> objectivesMap = new HashMap<>();
        JSONArray objectivesArr = json.getJSONArray(OBJECTIVES_KEY);
        for (int i=0; i<objectivesArr.length(); i++) {
            JSONObject objective = objectivesArr.getJSONObject(i);
            Objective ObjectiveObject = new Objective();
            ObjectiveObject.id = objective.getInt(OBJECTIVE_ID_KEY);
            ObjectiveObject.city = objective.getString(OBJECTIVE_CITY_KEY);
            ObjectiveObject.name = objective.getString(OBJECTIVE_NAME_KEY);
            ObjectiveObject.position_longitude = objective.getDouble(OBJECTIVE_LONGITUDE_KEY);
            //ObjectiveObject.validation_method = objective.getInt(OBJECTIVE_VALIDATION_METHOD_KEY);
            ObjectiveObject.position_latitude = objective.getDouble(OBJECTIVE_LATITUDE_KEY);
            ObjectiveObject.description = objective.getString(OBJECTIVE_DESCRIPTION_KEY);
            objectivesMap.put(ObjectiveObject.id, ObjectiveObject);
        }
        return objectivesMap;
    }

    public Map<Integer, Objective> getObjectives() {
        Map<Integer, Objective> objectivesMap;
        try {
            objectivesMap = getObjectives_();
        } catch (JSONException e) {
            objectivesMap = null;
        }
        return objectivesMap;
    }

    public Objective[] getNewObjectivesIdAndValCode() {
        Objective[] idsAndCodes;
        try {
            JSONArray objectivesArr = json.getJSONArray(OBJECTIVES_KEY);
            idsAndCodes = new Objective[objectivesArr.length()];
            for (int i = 0; i < objectivesArr.length(); i++) {
                JSONObject objective = objectivesArr.getJSONObject(i);
                Objective objectiveObject = new Objective();
                objectiveObject.id = objective.getInt(OBJECTIVE_ID_KEY);
                objectiveObject.validation_code = objective
                        .getString(OBJECTIVE_VALIDATION_CODE_KEY);
                idsAndCodes[i] = objectiveObject;
            }
        } catch (JSONException e) {
            idsAndCodes = null;
        }
        return idsAndCodes;
    }

    public int getNewRouteId() {
        int routeId = 0;
        try {
            routeId = json.getInt(ROUTE_ADDED_ID_KEY);
        } catch (JSONException e) {
            // will never happen
        }
        return routeId;
    }

    public Operator getOperator() {
        Operator operator = null;
        try {
            if (json.getInt(OPERATOR_IS_OPERATOR_KEY) == IS_OPERATOR) {
                operator = new Operator();
                //getting operator's tags
                JSONArray tagsArr = json.getJSONArray(OPERATOR_TAGS_KEY);
                Set<String> tags = new HashSet<>();
                for (int j=0; j<tagsArr.length(); j++) {
                    tags.add(tagsArr.getString(j));
                }
                operator.name_operator = json.getString(OPERATOR_NAME_KEY);
                if (operator.name_operator.equals("null")) {
                    operator.name_operator = null;
                }
                operator.tags = tags;
                operator.position_longitude = json.getDouble(OPERATOR_LONGITUDE_KEY);
                operator.position_latitude = json.getDouble(OPERATOR_LATITUDE_KEY);
            }
        } catch (JSONException e) {
        }
        return operator;
    }

    private Prize getPrize_(JSONObject prize) throws JSONException {
        Prize prizeObject = new Prize();
        prizeObject.id = prize.getInt(PRIZE_ID_KEY);
        prizeObject.description = prize.getString(PRIZE_DESCRIPTION_KEY);
        //prizeObject.validation_method = prize.getInt(PRIZE_VALIDATION_METHOD_KEY);
        prizeObject.visible = (prize.getInt(PRIZE_VISIBLE_KEY) == 1);
        prizeObject.repeat_days = prize.getInt(PRIZE_REPEAT_DAYS_KEY);
        prizeObject.name = prize.getString(PRIZE_NAME_KEY);
        return prizeObject;
    }

    private Map<Integer, Prize> getPrizes_() throws JSONException {
        Map<Integer, Prize> prizesMap = new HashMap<>();
        JSONArray prizesArr = json.getJSONArray(PRIZES_KEY);
        for (int i=0; i<prizesArr.length(); i++) {
            JSONObject prize = prizesArr.getJSONObject(i);
            Prize prizeObject = getPrize_(prize);
            prizeObject.operator = prize.getString(PRIZE_OPERATOR_KEY);
            prizesMap.put(prizeObject.id, prizeObject);
        }
        return prizesMap;
    }

    public Map<Integer, Prize> getPrizes() {
        Map<Integer, Prize> prizesMap;
        try {
            prizesMap = getPrizes_();
        } catch (JSONException e) {
            prizesMap = null;
        }
        return prizesMap;
    }

    public int getNewPrizeId() {
        int prizeId = 0;
        try {
            prizeId = json.getInt(PRIZE_ADDED_ID_KEY);
        } catch (JSONException e) {
            // will never happen
        }
        return prizeId;
    }

    public int getVerifiedPrizeId() {
        int prizeId = 0;
        try {
            prizeId = json.getInt(PRIZE_VERIFIED_ID_KEY);
        } catch (JSONException e) {
            // will never happen
        }
        return prizeId;
    }

    public Prize getVerifiedPrize() {
        Prize prize;
        try {
            prize = getPrize_(json);
        } catch (JSONException e) {
            // will never happen
            prize = null;
        }
        return prize;
    }

    public String getPrizeLastWinDate() {
        String date;
        try {
            if (json != null) {
                date = json.getString(PRIZE_LAST_WON_DATE_KEY);
            }
            else {
                date = null;
            }
        }  catch (JSONException e) {
            // will never happen
            date = null;
        }
        return date;
    }

    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        try {
            JSONArray tagsArr = json.getJSONArray(TAGS_KEY);
            for (int i = 0; i < tagsArr.length(); i++) {
                 tags.add(tagsArr.getString(i));
            }
        } catch (JSONException e) {
            tags = null;
        }
        return tags;
    }

    public static class Jsonifier {
        protected JSONObject jsonified;

        public Jsonifier() {
            jsonified = new JSONObject();
        }

        public String toString() {
            return jsonified.toString();
        }

        public JSONObject getJson() {
            return jsonified;
        }
    }

    public static class ObjectiveJsonifier extends Jsonifier {
        protected Objective objective;

        public ObjectiveJsonifier(Objective objective) {
            super();
            this.objective = objective;
        }

        public ObjectiveJsonifier putFields() {
            try {
                jsonified.put(OBJECTIVE_NAME_KEY, objective.name);
                jsonified.put(OBJECTIVE_DESCRIPTION_KEY, objective.description);
                jsonified.put(OBJECTIVE_CITY_KEY, objective.city);
                jsonified.put(OBJECTIVE_LATITUDE_KEY, objective.position_latitude);
                jsonified.put(OBJECTIVE_LONGITUDE_KEY, objective.position_longitude);
                jsonified.put(OBJECTIVE_VALIDATION_METHOD_KEY, objective.validation_method);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }
    }

    public static class RouteJsonifier extends Jsonifier {

        Route route;

        public RouteJsonifier(Route route) {
            super();
            this.route = route;
        }

        public RouteJsonifier putFields() {
            try {
                jsonified.put(ROUTE_NAME_KEY, route.name);
                jsonified.put(ROUTE_DESCRIPTION_KEY, route.description);
                jsonified.put(ROUTE_VALIDITY_KEY, route.validity_days);
            } catch (JSONException e) {
                //will never happen
            }
            return this;
        }

        public RouteJsonifier putId() {
            try {
                jsonified.put(ROUTE_ID_KEY, route.id);
            } catch (JSONException e) {
                //will never happen
            }
            return this;
        }

        public RouteJsonifier putIdForDelete() {
            try {
                JSONArray ids = new JSONArray();
                ids.put(route.id);
                jsonified.put(ROUTE_ID_KEY, ids);
            } catch (JSONException e) {
                //will never happen
            }
            return this;
        }

        public RouteJsonifier putAddedTags(Set<String> addedTags) {
            try {
                if (addedTags != null && !addedTags.isEmpty()) {
                    JSONArray tagsToAdd = new JSONArray();
                    for (String addedTag : addedTags) {
                        tagsToAdd.put(addedTag);
                    }
                    jsonified.put(ROUTE_ADDED_TAGS_KEY, tagsToAdd);
                }
            } catch (JSONException e) {
                //will never happen
            }
            return this;
        }

        public RouteJsonifier putRemovedTags(Set<String> removedTags) {
            try {
                if (removedTags != null && !removedTags.isEmpty()) {
                    JSONArray tagsToRemove = new JSONArray();
                    for (String addedTag : removedTags) {
                        tagsToRemove.put(addedTag);
                    }
                    jsonified.put(ROUTE_REMOVED_TAGS_KEY, tagsToRemove);
                }
            } catch (JSONException e) {
                //will never happen
            }
            return this;
        }

        public RouteJsonifier putAddedObjs(Set<Integer> addedObjs) {
            try {
                if (addedObjs != null && !addedObjs.isEmpty()) {
                    JSONArray objsToAdd = new JSONArray();
                    for(int obj : addedObjs) {
                        objsToAdd.put(obj);
                    }
                    jsonified.put(ROUTE_ADDED_OBJECTIVES_KEY, objsToAdd);
                }
            } catch (JSONException e) {
                //will never happen
            }
            return this;
        }

        public RouteJsonifier putRemovedObjs(Set<Integer> removedObjs) {
            try {
                if (removedObjs != null && !removedObjs.isEmpty()) {
                    JSONArray objsToRemove = new JSONArray();
                    for (int removedObj : removedObjs) {
                        objsToRemove.put(removedObj);
                    }
                    jsonified.put(ROUTE_REMOVED_OBJECTIVES_KEY, objsToRemove);
                }
            } catch (JSONException e) {
                //will never happen
            }
            return this;
        }

        public RouteJsonifier putNewObjs(Objective[] newObjs) {
            try {
                if (newObjs != null && newObjs.length > 0) {
                    JSONArray objsNew = new JSONArray();
                    for (Objective newObj : newObjs) {
                        objsNew.put(new ObjectiveJsonifier(newObj).putFields().getJson());
                    }
                    jsonified.put(ROUTE_NEW_OBJECTIVES_KEY, objsNew);
                }
            } catch (JSONException e) {
                //will never happen
            }
            return this;
        }

    }

    public static class PrizeJsonifier extends Jsonifier {
        protected Prize prize;

        public PrizeJsonifier(Prize prize) {
            super();
            this.prize = prize;
        }

        public PrizeJsonifier putId() {
            try {
                jsonified.put(PRIZE_ID_KEY, prize.id);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public PrizeJsonifier putIdForAdding() {
            try {
                jsonified.put(PRIZE_ADDED_ID_KEY, prize.id);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public PrizeJsonifier putIdForRemoving() {
            try {
                jsonified.put(PRIZE_ID_FOR_REMOVING_KEY, prize.id);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public PrizeJsonifier putFields() {
            try {
                jsonified.put(PRIZE_NAME_KEY, prize.name);
                jsonified.put(PRIZE_DESCRIPTION_KEY, prize.description);
                jsonified.put(PRIZE_VISIBLE_KEY, prize.visible);
                jsonified.put(PRIZE_REPEAT_DAYS_KEY, prize.repeat_days);
                jsonified.put(OBJECTIVE_VALIDATION_METHOD_KEY, prize.validation_method);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public PrizeJsonifier putRouteId(int routeId) {
            try {
                jsonified.put(PRIZE_ROUTE_ID_KEY, routeId);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public PrizeJsonifier putProbability(int probability) {
            try {
                jsonified.put(PRIZE_PROBABILITY_KEY, probability);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public PrizeJsonifier putAddedMandatoryObjs(Set<Integer> addedMandatoryObjs) {
            try {
                if (addedMandatoryObjs != null && !addedMandatoryObjs.isEmpty()) {
                    JSONArray objsToAdd = new JSONArray();
                    for(int obj : addedMandatoryObjs) {
                        objsToAdd.put(obj);
                    }
                    jsonified.put(PRIZE_ADDED_MAND_OBJS_KEY, objsToAdd);
                }
            } catch (JSONException e) {
                //will never happen
            }
            return this;
        }

        public PrizeJsonifier putRemovedMandatoryObjs(Set<Integer> removedMandatoryObjs) {
            try {
                if (removedMandatoryObjs != null && !removedMandatoryObjs.isEmpty()) {
                    JSONArray objsToRemove = new JSONArray();
                    for (int removedObj : removedMandatoryObjs) {
                        objsToRemove.put(removedObj);
                    }
                    jsonified.put(PRIZE_REMOVED_MAND_OBJS_KEY, objsToRemove);
                }
            } catch (JSONException e) {
                //will never happen
            }
            return this;
        }

    }

    public static class ValidationCodeJsonifier extends Jsonifier {

        protected String validationCode;

        public ValidationCodeJsonifier (String validationCode) {
            this.validationCode = validationCode;
        }

        public ValidationCodeJsonifier putValidationCode() {
            try {
                jsonified.put(PRIZE_VALIDATION_CODE_KEY, validationCode);
            } catch (JSONException e) {
                //will never happen
            }
            return this;
        }

    }

    public static class OperatorJsonifier extends Jsonifier {
        protected Operator operator;

        public OperatorJsonifier(Operator operator) {
            super();
            this.operator = operator;
        }

        public OperatorJsonifier putLogin() {
            try {
                jsonified.put(OPERATOR_LOGIN_KEY, operator.id);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public OperatorJsonifier putPsw() {
            try {
                jsonified.put(OPERATOR_PASSWORD_KEY, operator.psw);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public OperatorJsonifier putLogin(String login) {
            try {
                jsonified.put(OPERATOR_LOGIN_KEY, login);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public OperatorJsonifier putPsw(String psw) {
            try {
                jsonified.put(OPERATOR_PASSWORD_KEY, psw);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public OperatorJsonifier putFbId(String fbId) {
            try {
                jsonified.put(OPERATOR_FB_ID_KEY, fbId);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public OperatorJsonifier putFields() {
            try {
                jsonified.put(OPERATOR_LOGIN_KEY, operator.id);
                jsonified.put(OPERATOR_PASSWORD_KEY, operator.psw);
                jsonified.put(OPERATOR_NAME_KEY, operator.name_operator);
                jsonified.put(OPERATOR_LATITUDE_KEY, operator.position_latitude);
                jsonified.put(OPERATOR_LONGITUDE_KEY, operator.position_longitude);
                putIsOperator();
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public OperatorJsonifier putIsOperator() {
            try {
                jsonified.put(OPERATOR_IS_OPERATOR_KEY, true);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public OperatorJsonifier putTags(Set<String> tags) {
            try {
                JSONArray tagsArray = new JSONArray();
                for(String tag: tags) {
                    tagsArray.put(tag);
                }
                jsonified.put(OPERATOR_TAGS_KEY, tagsArray);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public OperatorJsonifier putAddedTags(Set<String> tags) {
            try {
                JSONArray tagsArray = new JSONArray();
                for(String tag: tags) {
                    tagsArray.put(tag);
                }
                jsonified.put(OPERATOR_ADDED_TAGS_KEY, tagsArray);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

        public OperatorJsonifier putRemovedTags(Set<String> tags) {
            try {
                JSONArray tagsArray = new JSONArray();
                for(String tag: tags) {
                    tagsArray.put(tag);
                }
                jsonified.put(OPERATOR_REMOVED_TAGS_KEY, tagsArray);
            } catch (JSONException e) {
                // will never happen
            }
            return this;
        }

    }

}
