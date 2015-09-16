package com.example.tonino.login.Dialogs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.tonino.login.R;


/**
 * Created by Danny on 10/06/2015.
 */
public class CustomProgressDialog extends CustomLayoutDialog {

    protected int messageResource;

    public CustomProgressDialog() {
        dialogLayout = R.layout.dialog_progress_layout_operator;
    }

    @Override
    protected void onContinueCreateDialog(LayoutInflater inflater, ViewGroup dialogView,
                                          Bundle savedInstanceState) {
        setCancelable(false);
        if (messageResource != NO_RESOURCE) {
            setMessage(messageResource);
        }
    }

    protected void setMessageResource(int messageResource) {
        this.messageResource = messageResource;
    }

    public static class Builder {

        protected CustomProgressDialog dialog;
        FragmentActivity fragmentActivity;

        public Builder(FragmentActivity activity) {
            fragmentActivity = activity;
            dialog = new CustomProgressDialog();
        }

        public Builder setMessage(int messageResource) {
            dialog.setMessageResource(messageResource);
            return this;
        }

        public CustomProgressDialog show() {
            dialog.show(fragmentActivity.getSupportFragmentManager(),
                    fragmentActivity.getString(R.string.dialog_tag));
            return dialog;
        }
    }

}
