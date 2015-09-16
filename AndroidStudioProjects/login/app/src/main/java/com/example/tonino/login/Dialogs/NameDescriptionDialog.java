package com.example.tonino.login.Dialogs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tonino.login.R;


/**
 * Created by Danny on 14/06/2015.
 */
public class NameDescriptionDialog extends ConfirmCancelDialog {

    public static final String ARG_NAME = "name";
    public static final String ARG_DESCRIPTION = "description";

    protected TextView nameView;
    protected TextView descriptionView;

    public NameDescriptionDialog() {
        bodyLayout = R.layout.dialog_name_description_operator;
    }

    @Override
    protected void onContinueCreateDialog(LayoutInflater inflater,
                                          ViewGroup dialogView, Bundle savedInstanceState) {
        super.onContinueCreateDialog(inflater, dialogView, savedInstanceState);
        nameView = (TextView) dialogView.findViewById(R.id.name);
        descriptionView = (TextView) dialogView.findViewById(R.id.description);
        String name = "", description = "";
        Bundle arguments = getArguments();
        if (arguments != null) {
            name = arguments.getString(ARG_NAME);
            description = arguments.getString(ARG_DESCRIPTION);
        }
        nameView.setText(name);
        descriptionView.setText(description);
    }

    @Override
    protected void onConfirmFillUserInputBundle(Bundle userInput) {
        userInput.putString(ARG_NAME, nameView.getText().toString());
        userInput.putString(ARG_DESCRIPTION, descriptionView.getText().toString());
    }

    public static class Builder extends ConfirmCancelDialog.Builder {

        public Builder(FragmentActivity activity) {
            super(activity);
        }

        @Override
        protected ConfirmCancelDialog createDialog() {
            return new NameDescriptionDialog();
        }

        public Builder setParams(String name, String description) {
            Bundle args = new Bundle();
            args.putString(ARG_NAME, name);
            args.putString(ARG_DESCRIPTION, description);
            setArguments(args);
            return this;
        }

    }

}

