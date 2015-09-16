package com.example.tonino.login.Dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tonino.login.R;


/**
 * Created by Danny on 10/06/2015.
 */
public abstract class ConfirmCancelDialog extends CustomLayoutDialog {

    public static final String INTENT_DIALOG = "INTENT_DIALOG";
    public static final String ACTION_TYPE = "ACTION_TYPE";
    public static final int ACTION_CANCEL = 0;
    public static final int ACTION_CONFIRM = 1;
    public static final int ACTION_DELETE = 2;
    public static final String PARAM_OUT_CUSTOM_PARAMS = "custom_params";
    public static final String PARAM_OUT_USER_INPUT = "user_input";

    // when the dialog is created the caller can a bundle as parameter that will be returned to it
    public static final String STATE_CALLER_PARAMS = "caller_params";
    public static final String STATE_TITLE = "title";
    public static final String STATE_CONFIRM_BTN_TEXT = "confirm_btn_text";
    public static final String STATE_CANCEL_BTN_TEXT = "cancel_btn_text";
    public static final String STATE_DELETE_BTN_TEXT = "delete_btn_text";
    public static final String STATE_DISMISS_ON_RESTORE = "dismiss_on_restore";

    public static final int DEFAULT_DELETE_BTN_TEXT = R.string.dialog_delete_btn;

    protected int bodyLayout = NO_LAYOUT;
    protected Button confirmButton;
    protected Button cancelButton;
    protected Button deleteButton;
    protected int titleResource = NO_RESOURCE;
    protected int confirmBtnText = R.string.dialog_confirm_btn;
    protected int cancelBtnText = R.string.dialog_cancel_btn;
    protected int deleteBtnText = NO_RESOURCE;
    protected boolean dismissOnRestore = false;
    protected DialogBtnsListener dialogBtnsListener;
    protected Context context;
    protected Bundle customParams;

    @Override
    protected void onContinueCreateDialog(LayoutInflater inflater,
                                          ViewGroup dialogView, Bundle savedInstanceState) {
        confirmButton = (Button) rootDialogView.findViewById(R.id.dialog_confirm_btn);
        cancelButton = (Button) rootDialogView.findViewById(R.id.dialog_cancel_btn);
        deleteButton = (Button) rootDialogView.findViewById(R.id.dialog_delete_btn);
        if (bodyLayout != NO_LAYOUT) {
            ViewGroup dialogBody = (ViewGroup) rootDialogView.findViewById(R.id.dialog_body);
            dialogBody.removeAllViews();
            inflater.inflate(bodyLayout, dialogBody);
        }
        dialogBtnsListener = new DialogBtnsListener(this);
        if (savedInstanceState != null) {
            customParams = savedInstanceState.getBundle(STATE_CALLER_PARAMS);
            titleResource = savedInstanceState.getInt(STATE_TITLE);
            confirmBtnText = savedInstanceState.getInt(STATE_CONFIRM_BTN_TEXT);
            cancelBtnText = savedInstanceState.getInt(STATE_CANCEL_BTN_TEXT);
            deleteBtnText = savedInstanceState.getInt(STATE_DELETE_BTN_TEXT);
            dismissOnRestore = savedInstanceState.getBoolean(STATE_DISMISS_ON_RESTORE, false);
            if (dismissOnRestore) {
                dismiss();
            }
        }
        if (titleResource != NO_RESOURCE) {
            setTitle(titleResource);
        }
        if (cancelBtnText != NO_RESOURCE) {
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText(cancelBtnText);
            if (confirmBtnText != NO_RESOURCE || deleteBtnText != NO_RESOURCE) {
                if (confirmBtnText == NO_RESOURCE) {
                    /*RelativeLayout.LayoutParams cancelButtonParams = (RelativeLayout.LayoutParams)
                            cancelButton.getLayoutParams();
                    cancelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    cancelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_END);*/
                } else if (deleteBtnText == NO_RESOURCE) {
                    /*RelativeLayout.LayoutParams cancelButtonParams = (RelativeLayout.LayoutParams)
                            cancelButton.getLayoutParams();
                    cancelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    cancelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_START);*/
                }
            }
        }
        if (confirmBtnText != NO_RESOURCE) {
            confirmButton.setVisibility(View.VISIBLE);
            confirmButton.setText(confirmBtnText);
        }
        if (deleteBtnText != NO_RESOURCE) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setText(deleteBtnText);
        }
    }

    public void setNoConfirmButton() {
        confirmBtnText = NO_RESOURCE;
    }

    public void setNoCancelButton() {
        cancelBtnText = NO_RESOURCE;
    }

    public void setDeleteButton() {
        deleteBtnText = DEFAULT_DELETE_BTN_TEXT;
    }

    public void setTitleResource(int titleResource) {
        this.titleResource = titleResource;
    }

    public void setConfirmBtnText(int textResource) {
        confirmBtnText = textResource;
    }

    public void setCancelBtnText(int textResource) {
        cancelBtnText = textResource;
    }

    public void setDeleteBtnText(int textResource) {
        deleteBtnText = textResource;
    }

    public void setDismissOnRestore() {
        dismissOnRestore = true;
    }

    public void setCustomParams(Bundle customParams) {
        this.customParams = customParams;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(STATE_CALLER_PARAMS, customParams);
        outState.putInt(STATE_TITLE, titleResource);
        outState.putInt(STATE_CONFIRM_BTN_TEXT, confirmBtnText);
        outState.putInt(STATE_CANCEL_BTN_TEXT, cancelBtnText);
        outState.putInt(STATE_DELETE_BTN_TEXT, deleteBtnText);
        outState.putBoolean(STATE_DISMISS_ON_RESTORE, dismissOnRestore);
    }

    protected void onCancelClick() {
        Intent intent = new Intent(INTENT_DIALOG);
        intent.putExtra(ACTION_TYPE, ACTION_CANCEL);
        intent.putExtra(PARAM_OUT_CUSTOM_PARAMS, customParams);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    protected void onConfirmClick() {
        Intent intent = new Intent(INTENT_DIALOG);
        intent.putExtra(ACTION_TYPE, ACTION_CONFIRM);
        intent.putExtra(PARAM_OUT_CUSTOM_PARAMS, customParams);
        Bundle userInput = new Bundle();
        onConfirmFillUserInputBundle(userInput);
        intent.putExtra(PARAM_OUT_USER_INPUT, userInput);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    protected abstract void onConfirmFillUserInputBundle(Bundle userInput);

    protected void onDeleteClick() {
        Intent intent = new Intent(INTENT_DIALOG);
        intent.putExtra(ACTION_TYPE, ACTION_DELETE);
        intent.putExtra(PARAM_OUT_CUSTOM_PARAMS, customParams);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public interface DialogCallbacks {
        public void onDialogConfirm(Bundle customParams, Bundle userInput);
        public void onDialogCancel();
        public void onDialogDelete(Bundle customParams);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    protected static class DialogBtnsListener implements View.OnClickListener {

        protected ConfirmCancelDialog dialog;

        public DialogBtnsListener(ConfirmCancelDialog dialog) {
            this.dialog = dialog;
            dialog.confirmButton.setOnClickListener(this);
            dialog.cancelButton.setOnClickListener(this);
            dialog.deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int vId = view.getId();
            /*switch (vId) {
                case R.id.dialog_confirm_btn:
                    dialog.onConfirmClick();
                    break;
                case R.id.dialog_cancel_btn:
                    dialog.onCancelClick();
                    break;
                case R.id.dialog_delete_btn:
                    dialog.onDeleteClick();
                    break;
            }*/
            if(vId == R.id.dialog_confirm_btn)
            {
                dialog.onConfirmClick();
            }
            else if(vId == R.id.dialog_cancel_btn)
            {
                dialog.onCancelClick();
            }
            else if(vId == R.id.dialog_delete_btn)
            {
                dialog.onDeleteClick();
            }
            dialog.dismiss();
        }
    }

    public static abstract class Builder {
        protected FragmentActivity activity;
        protected ConfirmCancelDialog dialog;

        public Builder(FragmentActivity activity) {
            this.activity = activity;
            dialog = createDialog();
        }

        protected abstract ConfirmCancelDialog createDialog();

        public Builder setDeleteButton() {
            dialog.setDeleteButton();
            return this;
        }

        public Builder setNoConfirmButton() {
            dialog.setNoConfirmButton();
            return this;
        }

        public Builder setNoCancelButton() {
            dialog.setNoCancelButton();
            return this;
        }

        public Builder setTitle(int titleResource) {
            dialog.setTitleResource(titleResource);
            return this;
        }

        public Builder setConfirmBtnText(int textResource) {
            dialog.setConfirmBtnText(textResource);
            return this;
        }

        public Builder setCancelBtnText(int textResource) {
            dialog.setCancelBtnText(textResource);
            return this;
        }

        public Builder setDeleteBtnText(int textResource) {
            dialog.setDeleteBtnText(textResource);
            return this;
        }

        public Builder setDismissOnRestore() {
            dialog.setDismissOnRestore();
            return this;
        }

        public Builder setArguments(Bundle arguments) {
            dialog.setArguments(arguments);
            return this;
        }

        public Builder setCustomParams(Bundle customParams) {
            dialog.setCustomParams(customParams);
            return this;
        }

        public Builder setNotCancelable() {
            dialog.setCancelable(false);
            return this;
        }

        public ConfirmCancelDialog show() {
            dialog.show(activity.getSupportFragmentManager(),
                    activity.getString(R.string.dialog_tag));
            return dialog;
        }

    }

}
