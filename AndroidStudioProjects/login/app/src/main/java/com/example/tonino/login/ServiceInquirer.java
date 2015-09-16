package com.example.tonino.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ServiceInquirer extends BroadcastReceiver {

    private static final String INTENT_RESPONSE_RECEIVED = "INTENT_RESPONSE_RECEIVED";
    public static final String USERNAME_PARAMETER = "login";
    public static final String PASSWORD_PARAMETER = "psw";

    protected static ServiceInquirer instance;

    // Queue of callbacks, each implemented by who is calling the ServiceInquirer.
    private ArrayList<OnResponseReceivedCallback> callbackQueue;
    private String username;
    private String password;
    private Bundle cookiesBundle;

    public ServiceInquirer() {
        callbackQueue = new ArrayList<>();
    }

    public static ServiceInquirer getInstance() {
        if (instance == null) {
            instance = new ServiceInquirer();
        }
        return instance;
    }

    public static ServiceInquirer getInstance(String username, String password) {
        getInstance();
        instance.setUserInfo(username, password);
        return instance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        int statusCode = extras.getInt(WebServiceRequestService.PARAM_OUT_STATUS_CODE);
        String response = extras.getString(WebServiceRequestService.PARAM_OUT_RESPONSE);
        String originalUri = extras.getString(WebServiceRequestService.PARAM_OUT_URI);
        cookiesBundle = extras.getBundle(WebServiceRequestService.PARAM_OUT_COOKIES);
        OnResponseReceivedCallback callback = callbackQueue.remove(0);
        if (callbackQueue.isEmpty()) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
        }
        if (callback != null) {
            callback.onResponseReceived(originalUri, statusCode, response);
        }
    }

    public void setUserInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setCookiesBundle(Bundle cookiesBundle) {
        this.cookiesBundle = cookiesBundle;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Bundle getCookiesBundle() {
        return cookiesBundle;
    }

    private void request(Context context, String what, String method, String json,
                        OnResponseReceivedCallback callback) {
        if (callbackQueue.isEmpty()) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(INTENT_RESPONSE_RECEIVED);
            LocalBroadcastManager.getInstance(context).registerReceiver(this, filter);
        }
        Intent intent = new Intent(context, WebServiceRequestService.class);
        intent.putExtra(WebServiceRequestService.PARAM_IN_CALLER, INTENT_RESPONSE_RECEIVED);
        intent.putExtra(WebServiceRequestService.PARAM_IN_URI, what);
        intent.putExtra(WebServiceRequestService.PARAM_IN_COOKIES, cookiesBundle);
        intent.putExtra(WebServiceRequestService.PARAM_IN_METHOD, method);
        intent.putExtra(WebServiceRequestService.PARAM_IN_JSON, json);
        callbackQueue.add(callback);
        context.startService(intent);
    }

    public void get(Context context, String what, OnResponseReceivedCallback callback) {
        request(context, what, "GET", null, callback);
    }

    public void post(Context context, String what, String json, OnResponseReceivedCallback callback) {
        request(context, what, "POST", json, callback);
    }

    public void put(Context context, String what, String json, OnResponseReceivedCallback callback) {
        request(context, what, "PUT", json, callback);
    }

    public void delete(Context context, String what, String json,
                       OnResponseReceivedCallback callback) {
        request(context, what, "DELETE", json, callback);
    }

    public void login(Context context, OnResponseReceivedCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put(USERNAME_PARAMETER, username);
            json.put(PASSWORD_PARAMETER, password);
            post(context, context.getString(R.string.WEB_SERVICE_LOGIN), json.toString(), callback);
        } catch (JSONException e) {
            //will never happen
        }
    }

    public void loginFB(Context context, OnResponseReceivedCallback callback) {
        String json = new JSONParser.OperatorJsonifier(null)
                .putFbId(username)
                .putIsOperator()
                .toString();
        post(context, context.getString(R.string.WEB_SERVICE_LOGIN_FB), json, callback);
    }

    public interface OnResponseReceivedCallback {
        public void onResponseReceived(String originalUri, int statusCode, String response);
    }

}
