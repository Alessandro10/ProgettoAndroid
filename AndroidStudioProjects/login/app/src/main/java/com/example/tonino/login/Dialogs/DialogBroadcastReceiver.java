package com.example.tonino.login.Dialogs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

public abstract class DialogBroadcastReceiver extends BroadcastReceiver implements
        ConfirmCancelDialog.DialogCallbacks {

    protected boolean isRegistered = false;

    public DialogBroadcastReceiver(Context context) {
        registerForReceive(context);
    }

    public void registerForReceive(Context context) {
        if (!isRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConfirmCancelDialog.INTENT_DIALOG);
            LocalBroadcastManager.getInstance(context).registerReceiver(this, filter);
            isRegistered = true;
        }
    }

    public void unregisterForReceive(Context context) {
        if (isRegistered) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
            isRegistered = false;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        int action = extras
                .getInt(ConfirmCancelDialog.ACTION_TYPE, ConfirmCancelDialog.ACTION_CANCEL);
        final Bundle customParams = extras.getBundle(ConfirmCancelDialog.PARAM_OUT_CUSTOM_PARAMS);
        final Bundle userInput = extras.getBundle(ConfirmCancelDialog.PARAM_OUT_USER_INPUT);
        switch (action) {
            case ConfirmCancelDialog.ACTION_CANCEL:
                onDialogCancel();
                break;
            case ConfirmCancelDialog.ACTION_CONFIRM:
                onDialogConfirm(customParams, userInput);
                break;
            case ConfirmCancelDialog.ACTION_DELETE:
                onDialogDelete(customParams);
                break;
        }
    }

}
