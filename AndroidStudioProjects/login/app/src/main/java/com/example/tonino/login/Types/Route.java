package com.example.tonino.login.Types;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tonino.login.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Parcelable but assumes that the following fields are not null:
 * - objectives
 * - tags
 * - prizes
 */
public class Route implements Cloneable, Parcelable {
    public int id;
    public Set<Integer> objectives;
    public String description;
    public String name;
    public Set<String> tags;
    public Map<Integer, PrizeInfo> prizes;
    public int validity_days;

    public Route() {
        objectives = new HashSet<>();
        tags = new HashSet<>();
        prizes = new HashMap<>();
    }

    public static class PrizeInfo implements Parcelable {
        public int probability;
        public Set<Integer> mandatoryObjs;

        public PrizeInfo(int probability, Set<Integer> mandatoryObjs) {
            this.probability = probability;
            this.mandatoryObjs = mandatoryObjs;
        }

        public PrizeInfo(PrizeInfo prizeInfo) {
            probability = prizeInfo.probability;
            mandatoryObjs = new HashSet<>(prizeInfo.mandatoryObjs);
        }

        protected PrizeInfo(Parcel in) {
            probability = in.readInt();
            // assuming madatoryObjs wasn't be null
            ArrayList<Integer> mandatoryObjsList =  new ArrayList<>();
            in.readList(mandatoryObjsList, Integer.class.getClassLoader());
            mandatoryObjs = new HashSet<>(mandatoryObjsList);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(probability);
            // assuming madatoryObjs isn't be null
            dest.writeList(new ArrayList<>(mandatoryObjs));
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<PrizeInfo> CREATOR = new Parcelable.Creator<PrizeInfo>() {
            @Override
            public PrizeInfo createFromParcel(Parcel in) {
                return new PrizeInfo(in);
            }

            @Override
            public PrizeInfo[] newArray(int size) {
                return new PrizeInfo[size];
            }
        };

    }

    /**
     * Fills the given view with all the route's properties and optionally fills its tags.
     * @param inflater inflater to create the tags views
     * @param view the view to fill
     */
    public void fillView(LayoutInflater inflater, ViewGroup view) {
        ((TextView) view.findViewById(R.id.routeName)).setText(name);
        ((TextView) view.findViewById(R.id.routeDescription)).setText(description);
        ((TextView) view.findViewById(R.id.routeValidity))
                .setText(Integer.toString(validity_days));
        writeTags(inflater, (ViewGroup) view.findViewById(R.id.tags_container));
    }

    /**
     * Fills the given tagsContiner with the tags, inflating the template_tag.xml
     *
     * @param inflater the inflater to create the new tag views
     * @param tagsContainer the ViewGroup that should contain all the tags
     */
    public void writeTags(LayoutInflater inflater, ViewGroup tagsContainer) {
        tagsContainer.removeAllViews();
        for(String tag : tags) {
            View tagView = inflater.inflate(R.layout.template_tag_operator, tagsContainer, false);
            ((TextView) tagView.findViewById(R.id.tag_name)).setText(tag);
            tagsContainer.addView(tagView);
        }
    }

    public Route clone() {
        Route clonedRoute = new Route();
        clonedRoute.id = this.id;
        clonedRoute.objectives = new HashSet<>(this.objectives);
        clonedRoute.description = this.description;
        clonedRoute.name = this.name;
        clonedRoute.tags = new HashSet<>(this.tags);
        clonedRoute.prizes = new HashMap<>();
        for(int key : this.prizes.keySet()) {
            PrizeInfo prizeInfo = this.prizes.get(key);
            clonedRoute.prizes.put(key, new PrizeInfo(prizeInfo));
        }
        clonedRoute.validity_days = this.validity_days;
        return clonedRoute;
    }

    protected Route(Parcel in) {
        id = in.readInt();
        int [] objectivesArray = in.createIntArray();
        objectives = new HashSet<>();
        for (int objective : objectivesArray) {
            objectives.add(objective);
        }
        description = in.readString();
        name = in.readString();
        String [] tagsArray = in.createStringArray();
        tags = new HashSet<>(Arrays.asList(tagsArray));
        int [] prizesArray = in.createIntArray();
        prizes = new HashMap<>();
        Parcelable [] prizeInfoArray = in.readParcelableArray(PrizeInfo.class.getClassLoader());
        for (int i = 0; i < prizesArray.length; i++) {
            int prize = prizesArray[i];
            PrizeInfo prizeInfo = (PrizeInfo) prizeInfoArray[i];
            prizes.put(prize, prizeInfo);
        }
        validity_days = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        int [] objectivesArray = new int[objectives.size()];
        int i=0;
        for (int objective : objectives) {
            objectivesArray[i] = objective;
            i++;
        }
        dest.writeIntArray(objectivesArray);
        dest.writeString(description);
        dest.writeString(name);
        String [] tagsArray = new String[tags.size()];
        i = 0;
        for (String tag : tags) {
            tagsArray[i] = tag;
            i++;
        }
        dest.writeStringArray(tagsArray);
        int [] prizesArray = new int[prizes.keySet().size()];
        i = 0;
        for (int prize : prizes.keySet()) {
            prizesArray[i] = prize;
            i++;
        }
        dest.writeIntArray(prizesArray);
        Parcelable [] prizeInfoArray = new Parcelable[prizes.size()];
        for (int j = 0; j < prizesArray.length; j++) {
            int prize = prizesArray[j];
            PrizeInfo prizeInfo = prizes.get(prize);
            prizeInfoArray[j] = prizeInfo;
        }
        dest.writeParcelableArray(prizeInfoArray, 0);
        dest.writeInt(validity_days);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

}
