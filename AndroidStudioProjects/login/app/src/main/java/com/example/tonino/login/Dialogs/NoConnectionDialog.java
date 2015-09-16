package com.example.tonino.login.Dialogs;

import android.support.v4.app.FragmentActivity;

import com.example.tonino.login.R;


/**
 * Created by Danny on 21/06/2015.
 */
public class NoConnectionDialog {

    public static ConfirmCancelDialog show(FragmentActivity activity) {
        return new MessageDialog.Builder(activity)
                .setMessage(R.string.dialog_msg_check_connection)
                .setTitle(R.string.dialog_title_connection_error)
                .setCancelBtnText(R.string.dialog_ok_option)
                .setNoConfirmButton()
                .show();
    }

}
