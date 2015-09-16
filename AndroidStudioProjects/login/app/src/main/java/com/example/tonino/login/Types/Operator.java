package com.example.tonino.login.Types;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Parcelable but assumes tags is never null.
 */
public class Operator implements Parcelable {
    public String id;
    public String psw;
    public String name_operator;
    public Set<String> tags;
    public double position_longitude;
    public double position_latitude;

    public Operator() {}

    protected Operator(Parcel in) {
        id = in.readString();
        psw = in.readString();
        name_operator = in.readString();
        ArrayList<String> tagsArray = new ArrayList<>();
        in.readList(tagsArray, String.class.getClassLoader());
        tags = new HashSet<>(tagsArray);
        position_longitude = in.readDouble();
        position_latitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(psw);
        dest.writeString(name_operator);
        dest.writeList(new ArrayList<>(tags));
        dest.writeDouble(position_longitude);
        dest.writeDouble(position_latitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Operator> CREATOR = new Parcelable.Creator<Operator>() {
        @Override
        public Operator createFromParcel(Parcel in) {
            return new Operator(in);
        }

        @Override
        public Operator[] newArray(int size) {
            return new Operator[size];
        }
    };

}