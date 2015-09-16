package com.example.tonino.login.Dialogs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.example.tonino.login.R;


/**
 * Created by Danny on 14/06/2015.
 */
public class NumberPickerDialog extends ConfirmCancelDialog {

    public static final String ARG_VALUE = "value";
    public static final String ARG_MAX = "max";

    public static final int DEFAULT_MAX = 30;
    public static final int DEFAULT_MIN = 0;

    protected NumberPicker numberPicker;

    public NumberPickerDialog() {
        bodyLayout = R.layout.dialog_seek_bar_operator;
    }

    @Override
    protected void onContinueCreateDialog(LayoutInflater inflater,
                                          ViewGroup dialogView, Bundle savedInstanceState) {
        super.onContinueCreateDialog(inflater, dialogView, savedInstanceState);
        numberPicker = (NumberPicker) dialogView.findViewById(R.id.number_picker);
        Bundle arguments = getArguments();
        if (arguments != null) {
            numberPicker.setMinValue(DEFAULT_MIN);
            numberPicker.setMaxValue(arguments.getInt(ARG_MAX, DEFAULT_MAX));
            numberPicker.setValue(arguments.getInt(ARG_VALUE));
        }
    }

    @Override
    protected void onConfirmFillUserInputBundle(Bundle userInput) {
        userInput.putInt(ARG_VALUE, numberPicker.getValue());
    }

    public static class Builder extends ConfirmCancelDialog.Builder {

        public Builder(FragmentActivity activity) {
            super(activity);
        }

        @Override
        protected ConfirmCancelDialog createDialog() {
            return new NumberPickerDialog();
        }

        public Builder setParams(int value, int max) {
            Bundle args = new Bundle();
            args.putInt(ARG_VALUE, value);
            args.putInt(ARG_MAX, max);
            setArguments(args);
            return this;
        }

    }

}

