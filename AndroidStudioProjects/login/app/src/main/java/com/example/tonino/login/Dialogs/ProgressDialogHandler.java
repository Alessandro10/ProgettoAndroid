package com.example.tonino.login.Dialogs;

import android.support.v4.app.FragmentActivity;

/**
 * Created by Danny on 21/06/2015.
 */
public class ProgressDialogHandler {

    CustomProgressDialog progressDialog;

    public CustomProgressDialog show(FragmentActivity activity, int loadingTextResource) {
        progressDialog = new CustomProgressDialog.Builder(activity)
                .setMessage(loadingTextResource)
                .show();
        return progressDialog;
    }

    public void dismiss() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
