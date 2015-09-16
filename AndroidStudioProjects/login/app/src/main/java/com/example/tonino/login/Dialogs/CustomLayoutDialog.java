package com.example.tonino.login.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tonino.login.R;


/**
 * Created by Danny on 10/06/2015.
 */
public abstract class CustomLayoutDialog extends DialogFragment {

    public static final int NO_LAYOUT = 0;
    public static final int NO_RESOURCE = 0;

    protected int dialogLayout = R.layout.dialog_base_layout_operator;

    protected ViewGroup rootDialogView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (dialogLayout != NO_LAYOUT) {
            rootDialogView = (ViewGroup) inflater.inflate(dialogLayout, null);
            builder.setView(rootDialogView);
        }
        onContinueCreateDialog(inflater, rootDialogView, savedInstanceState);
        return builder.create();
    }

    protected abstract void onContinueCreateDialog(LayoutInflater inflater, ViewGroup dialogView,
                                                   Bundle savedInstanceState);

    public void setMessage(int messageResource) {
        ((TextView) rootDialogView.findViewById(R.id.dialog_message)).setText(messageResource);
    }

    public void setTitle(int titleResource) {
        ((TextView) rootDialogView.findViewById(R.id.dialog_title)).setText(titleResource);
    }

}
