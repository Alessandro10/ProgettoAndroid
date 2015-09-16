package com.example.tonino.login.Types;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Danny on 11/06/2015.
 */
public class Objective implements Parcelable {
    public int id;
    public String city;
    public String name;
    public double position_longitude;
    public double position_latitude;
    public int validation_method;
    public String description;
    public String validation_code;

    public Objective() {}

    protected Objective(Parcel in) {
        id = in.readInt();
        city = in.readString();
        name = in.readString();
        position_longitude = in.readDouble();
        position_latitude = in.readDouble();
        validation_method = in.readInt();
        description = in.readString();
        validation_code = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(city);
        dest.writeString(name);
        dest.writeDouble(position_longitude);
        dest.writeDouble(position_latitude);
        dest.writeInt(validation_method);
        dest.writeString(description);
        dest.writeString(validation_code);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Objective> CREATOR = new Parcelable.
            Creator<Objective>() {
                @Override
                public Objective createFromParcel(Parcel in) {
                    return new Objective(in);
                }

                @Override
                public Objective[] newArray(int size) {
                    return new Objective[size];
                }
            };

}
