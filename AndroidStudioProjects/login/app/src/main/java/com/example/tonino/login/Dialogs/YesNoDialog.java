package com.example.tonino.login.Dialogs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.tonino.login.R;


/**
 * Created by Danny on 14/06/2015.
 */
public class YesNoDialog extends ConfirmCancelDialog {

    public static final String ARG_VALUE = "value";

    protected RadioButton yesBtn;
    protected RadioButton noBtn;

    public YesNoDialog() {
        bodyLayout = R.layout.dialog_yes_no_operator;
    }

    @Override
    protected void onContinueCreateDialog(LayoutInflater inflater,
                                          ViewGroup dialogView, Bundle savedInstanceState) {
        super.onContinueCreateDialog(inflater, dialogView, savedInstanceState);
        yesBtn = (RadioButton) dialogView.findViewById(R.id.radio_yes);
        noBtn = (RadioButton) dialogView.findViewById(R.id.radio_no);
        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.getBoolean(ARG_VALUE)) {
                yesBtn.setChecked(true);
            }
            else {
                noBtn.setChecked(true);
            }
        }
    }

    @Override
    protected void onConfirmFillUserInputBundle(Bundle userInput) {
        userInput.putBoolean(ARG_VALUE, yesBtn.isChecked());
    }

    public static class Builder extends ConfirmCancelDialog.Builder {

        public Builder(FragmentActivity activity) {
            super(activity);
        }

        @Override
        protected ConfirmCancelDialog createDialog() {
            return new YesNoDialog();
        }

        public Builder setParams(boolean value) {
            Bundle args = new Bundle();
            args.putBoolean(ARG_VALUE, value);
            setArguments(args);
            return this;
        }

    }

}

