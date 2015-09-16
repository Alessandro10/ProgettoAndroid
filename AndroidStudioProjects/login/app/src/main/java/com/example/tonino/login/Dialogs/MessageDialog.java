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
public class MessageDialog extends ConfirmCancelDialog {

    public static final String STATE_MESSAGE_RESOURCE = "message_resource";
    public static final String STATE_MESSAGE_STRING = "message_str";

    int messageResource = NO_RESOURCE;
    String messageStr;

    @Override
    protected void onContinueCreateDialog(LayoutInflater inflater, ViewGroup dialogView, Bundle savedInstanceState) {
        super.onContinueCreateDialog(inflater, dialogView, savedInstanceState);
        if (savedInstanceState != null) {
            messageResource = savedInstanceState.getInt(STATE_MESSAGE_RESOURCE);
            messageStr = savedInstanceState.getString(STATE_MESSAGE_STRING);
        }
        if (messageResource != NO_RESOURCE) {
            setMessage(messageResource);
        }
        else if (messageStr != null) {
            ((TextView) rootDialogView.findViewById(R.id.dialog_message)).setText(messageStr);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_MESSAGE_RESOURCE, messageResource);
        outState.putString(STATE_MESSAGE_STRING, messageStr);
    }

    @Override
    protected void onConfirmFillUserInputBundle(Bundle userInput) {
        // no need to return any input from the user
    }

    public void setMessageResource(int messageResource) {
        this.messageResource = messageResource;
    }

    public void setMessageStr(String message) {
        messageStr = message;
    }

    public static class Builder extends ConfirmCancelDialog.Builder {

        public Builder(FragmentActivity activity) {
            super(activity);
        }

        @Override
        protected ConfirmCancelDialog createDialog() {
            return new MessageDialog();
        }

        public Builder setMessage(int messageResource) {
            ((MessageDialog) dialog).setMessageResource(messageResource);
            return this;
        }

        public Builder setMessageStr(String message) {
            ((MessageDialog) dialog).setMessageStr(message);
            return this;
        }

    }

}

