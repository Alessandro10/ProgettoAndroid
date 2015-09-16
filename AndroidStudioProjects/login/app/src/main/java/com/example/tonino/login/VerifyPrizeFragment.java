package com.example.tonino.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tonino.login.Dialogs.NoConnectionDialog;
import com.example.tonino.login.Types.Prize;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;

import org.apache.http.HttpStatus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Danny on 19/06/2015.
 */
public class VerifyPrizeFragment extends Fragment implements
        ServiceInquirer.OnResponseReceivedCallback, View.OnClickListener {

    public static final String MARKET_URI = "market://details?id=com.google.zxing.client.android";
    public static final String SCAN_QRCODE_INTENT = "com.google.zxing.client.android.SCAN";
    public static final String SCAN_QRCODE_MODE_KEY = "SCAN_MODE";
    public static final String SCAN_QRCODE_MODE = "QR_CODE_MODE";
    public static final String SCAN_QRCODE_RESULT_KEY = "SCAN_RESULT";
    public static final int SCAN_QRCODE_REQUEST_CODE = 0;

    public static final String STATE_WON_PRIZE = "won_prize" ;
    public static final String STATE_FAILURE_MSG = "failure_msg";
    public static final String STATE_ALREADY_SHARED = "already_shared";

    protected MainActivity_operator mainActivity;
    protected ViewGroup rootView;
    protected ViewGroup prizeView;
    protected TextView failureView;
    protected Button validateBtn;
    protected ShareButton shareButton;
    protected boolean alreadyShared = false;

    protected Prize savedWonPrize;

    protected DelayedActivityResult delayedActivityResult;

    public static class DelayedActivityResult {

        public int requestCode;
        public int resultCode;
        public Intent data;

        public DelayedActivityResult(int requestCode, int resultCode, Intent data) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.data = data;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity_operator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_verified_prize_operator, container, false);
        prizeView = (ViewGroup) rootView.findViewById(R.id.prize);
        failureView = (TextView) rootView.findViewById(R.id.failure);
        validateBtn = (Button) rootView.findViewById(R.id.validate_btn);
        shareButton = (ShareButton) rootView.findViewById(R.id.fb_share_btn);
        validateBtn.setOnClickListener(this);
        if (savedInstanceState != null) {
            savedWonPrize = savedInstanceState.getParcelable(STATE_WON_PRIZE);
            String failureMsg = savedInstanceState.getString(STATE_FAILURE_MSG);
            alreadyShared = savedInstanceState.getBoolean(STATE_ALREADY_SHARED, false);
            if (savedWonPrize != null) {
                success(savedWonPrize);
            }
            else if (failureMsg != null) {
                failure(failureMsg);
            }
            else {
                scanQrCodeIfPossible();
            }
        }
        else {
            scanQrCodeIfPossible();
        }
        return rootView;
    }

    /**
     * @return whether the user has an app installed that can scan QR Codes
     */
    public boolean isScanningAvailable() {
        Intent intent = new Intent(SCAN_QRCODE_INTENT);
        intent.putExtra(SCAN_QRCODE_MODE_KEY, SCAN_QRCODE_MODE);
        PackageManager mgr = mainActivity.getPackageManager();
        List<ResolveInfo> list = mgr
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return (list.size() > 0);
    }

    /**
     * Starts the QR Code scanner app.
     * Doesn't check if there is one available, so it could cause a fatal exception.
     */
    public void scanQrCodeIfPossible() {
        if (isScanningAvailable()) {
            Intent intent = new Intent(SCAN_QRCODE_INTENT);
            intent.putExtra(SCAN_QRCODE_MODE_KEY, SCAN_QRCODE_MODE);
            startActivityForResult(intent, SCAN_QRCODE_REQUEST_CODE);
        }
        else {
            failure(R.string.validation_no_scanner);
            new AlertDialog.Builder(mainActivity)
                    .setTitle(R.string.validation_no_scanner_title)
                    .setMessage(R.string.validation_no_scanner_message)
                    .setNegativeButton(R.string.validation_no_scanner_not_now, null)
                    .setPositiveButton(R.string.validation_no_scanner_install,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri marketUri = Uri.parse(MARKET_URI);
                                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                                    startActivity(marketIntent);
                                }
                            })
                    .show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (delayedActivityResult != null) {
            handleActivityResult(delayedActivityResult);
            delayedActivityResult = null;
        }
    }

    /**
     * Receives the scanned QR Code from the scanner app and send validation request to the web
     * service
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        delayedActivityResult = new DelayedActivityResult(requestCode, resultCode, data);
    }

    public void handleActivityResult(DelayedActivityResult delayedActivityResult) {
        int requestCode = delayedActivityResult.requestCode;
        int resultCode = delayedActivityResult.resultCode;
        Intent data = delayedActivityResult.data;
        if (requestCode == SCAN_QRCODE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String validationCode = data.getStringExtra(SCAN_QRCODE_RESULT_KEY);
                mainActivity.getServiceInquirer()
                        .post(mainActivity,
                                getString(R.string.WEB_SERVICE_PRIZE_VERIFIY),
                                new JSONParser.ValidationCodeJsonifier(validationCode)
                                        .putValidationCode().toString(),
                                this);
                mainActivity.showLoadingDialog(R.string.loading_verifying);
            }
        }
        mainActivity.closeDrawer();
    }

    /**
     * Handles "Validate another prize" button touch
     * @param v "Validate another prize" button
     */
    @Override
    public void onClick(View v) {
        scanQrCodeIfPossible();
    }

    /**
     * Handles the response from the web serivice related to the QR Code validation.
     * Displays the prize info if the code was correct, or a failure message.
     *
     * @param originalUri not used
     * @param statusCode response http status code
     * @param response json received
     */
    @Override
    public void onResponseReceived(String originalUri, int statusCode, String response) {
        mainActivity.dismissLoadingDialog();
        JSONParser parser = new JSONParser(response != null ? response : "");
        switch (statusCode) {
            case HttpStatus.SC_BAD_REQUEST:
                String lastWonDate = parser.getPrizeLastWinDate();
                if (lastWonDate != null) {
                    try {
                        Date wonDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastWonDate);
                        failure(getString(R.string.validation_already_done,
                                DateFormat.getDateInstance(DateFormat.LONG, Locale.US).format(wonDate)));
                    } catch (ParseException e) {
                        failure(R.string.validation_wrong_code);
                    }
                }
                else {
                    failure(R.string.validation_wrong_code);
                }
                break;
            case HttpStatus.SC_FORBIDDEN:
                failure(R.string.validation_wrong_operator);
                break;
            case HttpStatus.SC_OK:
                Prize wonPrize;
                wonPrize = parser.getVerifiedPrize();
                if (wonPrize == null) {
                    failure(R.string.unexpected_error_message);
                }
                else {
                    alreadyShared = false;
                    success(wonPrize);
                }
                break;
            case WebServiceRequestService.NO_INTERNET_CONNECTION:
                NoConnectionDialog.show(mainActivity);
                break;
        }
    }

    /**
     * Hide the prize info views and displays the given failure text.
     *
     * @param failureText failure text to display
     */
    protected void failure(int failureText) {
        failure(getString(failureText));
    }

    /**
     * Hide the prize info views and displays the given failure text.
     *
     * @param failureText failure text to display
     */
    protected void failure(String failureText) {
        prizeView.setVisibility(View.GONE);
        shareButton.setVisibility(View.GONE);
        savedWonPrize = null;
        failureView.setText(failureText);
    }

    /**
     * Hide the failure message and displays the won prize info
     *
     * @param wonPrize prize whose info are to be shown
     */
    protected void success(Prize wonPrize) {
        savedWonPrize = wonPrize;
        failureView.setVisibility(View.GONE);
        wonPrize.fillView(prizeView, Prize.NO_PROBABILITY);
        prizeView.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.VISIBLE);
        if (!alreadyShared) {
            enableFBShareBtn();
            shareButton.setShareContent(new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(getString(R.string.fb_share_url)))
                    .setImageUrl(Uri.parse(getString(R.string.fb_share_img_url)))
                    .setContentTitle(getString(R.string.fb_share_title, wonPrize.name))
                    .setContentDescription(getString(R.string.fb_share_message,
                            wonPrize.description))
                    .build());
            shareButton.registerCallback(mainActivity.callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    alreadyShared = true;
                    disableFBShareBtn();
                }

                @Override
                public void onCancel() {}

                @Override
                public void onError(FacebookException e) {}
            });
        }
        else {
            disableFBShareBtn();
        }
    }

    protected void enableFBShareBtn() {
        shareButton.setEnabled(true);
        shareButton.setText(R.string.fb_share_button);
    }

    protected void disableFBShareBtn() {
        shareButton.setEnabled(false);
        shareButton.setText(R.string.fb_share_button_disabled);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_WON_PRIZE, savedWonPrize);
        outState.putString(STATE_FAILURE_MSG, failureView.getText().toString());
        outState.putBoolean(STATE_ALREADY_SHARED, alreadyShared);
    }

}