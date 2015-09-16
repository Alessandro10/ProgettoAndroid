package com.example.tonino.login.Types;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tonino.login.R;


/**
 * Created by Danny on 11/06/2015.
 */
public class Prize implements Cloneable, Parcelable {
    public int id;
    public String operator;
    public String description;
    public int validation_method;
    public boolean visible;
    public int repeat_days;
    public String name;

    public static final String VISIBLE = "Yes";
    public static final String NOT_VISIBLE = "No";

    public static final int NO_PROBABILITY = -1;

    public Prize() {}

    /**
     * Fills the given view with all the prize's properties
     *
     * @param view the view to fill
     */
    public void fillView(ViewGroup view, int probability) {
        ((TextView) view.findViewById(R.id.prize_name)).setText(name);
        ((TextView) view.findViewById(R.id.prize_description)).setText(description);
        if (probability != NO_PROBABILITY) {
            ((TextView) view.findViewById(R.id.prize_probability))
                    .setText(Integer.toString(probability));
        }
        ((TextView) view.findViewById(R.id.prize_visible)).setText(visible ? VISIBLE : NOT_VISIBLE);
        ((TextView) view.findViewById(R.id.prize_repeat)).setText(Integer.toString(repeat_days));
    }

    public Prize clone() {
        Prize clonedPrize = new Prize();
        clonedPrize.id = id;
        clonedPrize.operator = operator;
        clonedPrize.description = description;
        clonedPrize.validation_method = validation_method;
        clonedPrize.visible = visible;
        clonedPrize.repeat_days = repeat_days;
        clonedPrize.name = name;
        return clonedPrize;
    }

    protected Prize(Parcel in) {
        id = in.readInt();
        operator = in.readString();
        description = in.readString();
        validation_method = in.readInt();
        visible = in.readByte() != 0x00;
        repeat_days = in.readInt();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(operator);
        dest.writeString(description);
        dest.writeInt(validation_method);
        dest.writeByte((byte) (visible ? 0x01 : 0x00));
        dest.writeInt(repeat_days);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Prize> CREATOR = new Parcelable.Creator<Prize>() {
        @Override
        public Prize createFromParcel(Parcel in) {
            return new Prize(in);
        }

        @Override
        public Prize[] newArray(int size) {
            return new Prize[size];
        }
    };

}
