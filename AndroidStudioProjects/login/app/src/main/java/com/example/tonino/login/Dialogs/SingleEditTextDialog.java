package com.example.tonino.login.Dialogs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.tonino.login.R;


/**
 * Created by Danny on 14/06/2015.
 */
public class SingleEditTextDialog extends ConfirmCancelDialog {

    public static final String STATE_IS_INTEGER_TEXT = "is_integer_text";

    public static final String ARG_TEXT = "text";

    protected EditText editText;
    protected boolean isIntegerText = false;

    public SingleEditTextDialog() {
        bodyLayout = R.layout.dialog_edit_field_operator;
    }

    public void setIntegerText() {
        isIntegerText = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_INTEGER_TEXT, isIntegerText);
    }

    @Override
    protected void onContinueCreateDialog(LayoutInflater inflater,
                                          ViewGroup dialogView, Bundle savedInstanceState) {
        super.onContinueCreateDialog(inflater, dialogView, savedInstanceState);
        if (savedInstanceState != null) {
            isIntegerText = savedInstanceState.getBoolean(STATE_IS_INTEGER_TEXT);
        }
        editText = (EditText) dialogView.findViewById(R.id.dialog_edit_text);
        Bundle arguments = getArguments();
        String text = "";
        if (arguments != null) {
            text = arguments.getString(ARG_TEXT);
        }
        editText.setText(text);
        if (isIntegerText) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //automatically show keyboard
        if(editText.requestFocus()) {
            getDialog().getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onConfirmFillUserInputBundle(Bundle userInput) {
        userInput.putString(ARG_TEXT, editText.getText().toString());
    }

    public static class Builder extends ConfirmCancelDialog.Builder {

        public Builder(FragmentActivity activity) {
            super(activity);
        }

        @Override
        protected ConfirmCancelDialog createDialog() {
            return new SingleEditTextDialog();
        }

        public Builder setParams(String text) {
            Bundle args = new Bundle();
            args.putString(ARG_TEXT, text);
            setArguments(args);
            return this;
        }

        public Builder isIntegerText(boolean isInteger) {
            if (isInteger) {
                ((SingleEditTextDialog) dialog).setIntegerText();
            }
            return this;
        }

    }

}

