package com.example.tonino.login.Dialogs;

import android.support.v4.app.FragmentActivity;

import com.example.tonino.login.R;


/**
 * Created by Danny on 14/06/2015.
 */
public class HelpDialog {

    public static ConfirmCancelDialog show(FragmentActivity activity, int helpMessage) {
        return new MessageDialog.Builder(activity)
                .setMessage(helpMessage)
                .setTitle(R.string.help_title)
                .setCancelBtnText(R.string.dialog_ok_option)
                .setNoConfirmButton()
                .show();
    }

}