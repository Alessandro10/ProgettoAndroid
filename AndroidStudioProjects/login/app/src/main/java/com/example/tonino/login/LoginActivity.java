package com.example.tonino.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tonino.login.Dialogs.DialogBroadcastReceiver;
import com.example.tonino.login.Dialogs.MessageDialog;
import com.example.tonino.login.Dialogs.NoConnectionDialog;
import com.example.tonino.login.Dialogs.ProgressDialogHandler;
import com.example.tonino.login.Types.Operator;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.widget.LoginButton;

import org.apache.http.HttpStatus;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener,
        ServiceInquirer.OnResponseReceivedCallback {

    public static final String DG_ARG_USERNAME = "username";
    public static final String DG_ARG_PASSWORD = "password;";

    public static final int REGISTER_REQUEST_CODE = 0;
    public static final int EDIT_OPERATOR_CODE = 1;

    protected LoginButton fbLoginButton;
    protected ProfileTracker profileTracker;
    protected CallbackManager callbackManager;

    protected Button loginBtn;
    protected Button registerBtn;
    protected EditText usernameView;
    protected EditText passwordView;
    protected ServiceInquirer serviceInquirer;

    protected ProgressDialogHandler progressDialogHandler;

    protected SharedPreferencesHandler sharedPreferencesHandler;

    protected DialogBroadcastReceiver dialogBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login_operator);


        fbLoginButton = (LoginButton) findViewById(R.id.fb_login_btn);
        fbLoginButton.setReadPermissions("public_profile");
        logoutFB();

        serviceInquirer = ServiceInquirer.getInstance();
        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.login_btn);
        registerBtn = (Button) findViewById(R.id.register_btn);
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        progressDialogHandler = new ProgressDialogHandler();
        // automatically fill username and password if they were saved
        sharedPreferencesHandler = SharedPreferencesHandler.getInstance();
        String username = sharedPreferencesHandler.get(this, R.string.saved_username, null);
        String password = sharedPreferencesHandler.get(this, R.string.saved_password, null);
        if (username != null) {
            usernameView.setText(username);
            passwordView.setText(password);
        }
        if (sharedPreferencesHandler.get(this, R.string.saved_auto_login, false)) {
            ((CheckBox) findViewById(R.id.auto_login)).setChecked(true);
            if (sharedPreferencesHandler.get(this, R.string.saved_use_fb, false)) {
                fbLoginButton.performClick();
            }
            else if (username != null) {
                login(username, password);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AccessToken.getCurrentAccessToken() != null) {
            if (AccessToken.getCurrentAccessToken().isExpired()) {
                AccessToken.refreshCurrentAccessTokenAsync();
            }
        }
        if (callbackManager == null) {
            callbackManager = new CallbackManagerImpl();
        }
        if (profileTracker == null) {
            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile,
                                                       Profile newProfile) {
                    if (newProfile != null) {
                        loggedInFB();
                        stopTracking();
                    }
                }
            };
            profileTracker.startTracking();
        }
        if (dialogBroadcastReceiver != null) {
            dialogBroadcastReceiver.registerForReceive(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialogBroadcastReceiver != null) {
            dialogBroadcastReceiver.unregisterForReceive(this);
        }
    }

    public void hideKeyboard() {
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    /**
     * Handles click of either login or register button.
     * Either way, hides the keyboard.
     *
     * @param v touched view
     */
    @Override
    public void onClick(View v) {
        hideKeyboard();
        // case login button touched
        if (v.getId() == loginBtn.getId()) {
            loginBtnTouched();
        }
        // case register button touched
        else {
            registerBtnTouched();
        }
    }

    /**
     * To be called when the login btn is touched. Retrieves username and password from EditTexts,
     * if one of them is empty displays toast message, else proceeds with login
     */
    protected void loginBtnTouched() {
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.login_error_empty),
                    Toast.LENGTH_SHORT).show();
        }
        else {
            login(username, password);
        }
    }

    /**
     * To be called when the register btn is touched. Starts the RegisterActivity
     */
    protected void registerBtnTouched() {
        startActivityForResult(new Intent(this, RegisterActivity.class), REGISTER_REQUEST_CODE);
    }

    /**
     * Called when the user logged in with FB.
     * Checks if he used to login with a normal account and asks if he wants to connect it.
     * Otherwise proceeds with registration.
     */
    protected void loggedInFB() {
        String username = sharedPreferencesHandler.get(this, R.string.saved_username, null);
        boolean use_fb = sharedPreferencesHandler.get(this, R.string.saved_use_fb, false);
        if (username != null && !use_fb) {
            String password = sharedPreferencesHandler.get(this, R.string.saved_password, null);
            Bundle customParams = new Bundle();
            customParams.putString(DG_ARG_USERNAME, username);
            customParams.putString(DG_ARG_PASSWORD, password);
            new MessageDialog.Builder(this)
                    .setMessageStr(getString(R.string.connect_fb_message, username))
                    .setTitle(R.string.connect_fb_title)
                    .setNoCancelButton()
                    .setDeleteBtnText(R.string.dialog_new_account_btn)
                    .setConfirmBtnText(R.string.dialog_connect_btn)
                    .setCustomParams(customParams)
                    .setDismissOnRestore()
                    .setNotCancelable()
                    .show();
            dialogBroadcastReceiver = new ConnectFBReceiver(this);
        }
        else {
            loginFB(Profile.getCurrentProfile().getId());
        }
    }

    protected class ConnectFBReceiver extends DialogBroadcastReceiver {

        public ConnectFBReceiver(Context context) {
            super(context);
        }

        @Override
        public void onDialogConfirm(Bundle customParams, Bundle userInput) {
            if (customParams != null) {
                connectFB(customParams.getString(DG_ARG_USERNAME),
                        customParams.getString(DG_ARG_PASSWORD),
                        Profile.getCurrentProfile().getId());
            }
        }

        @Override
        public void onDialogCancel() {}

        @Override
        public void onDialogDelete(Bundle customParams) {
            loginFB(Profile.getCurrentProfile().getId());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REGISTER_REQUEST_CODE || requestCode == EDIT_OPERATOR_CODE) {
                Operator operator = data.getParcelableExtra(RegisterActivity.ARG_OUT_OPERATOR);
                usernameView.setText(operator.id);
                passwordView.setText(operator.psw);
                saveUsernamePassword(operator.id, operator.psw);
                startMainActivity(operator);
            }
        }
    }

    /**
     * Sends the login request to the web service.
     *
     * @param username
     * @param password
     */
    public void login(String username, String password)  {
        serviceInquirer.setUserInfo(username, password);
        serviceInquirer.login(this, this);
        progressDialogHandler.show(this, R.string.loading_login);
    }

    /**
     * Sends the login with FB request to the web service
     * @param fbID facebook user id
     */
    public void loginFB(String fbID) {
        serviceInquirer.setUserInfo(fbID, "");
        serviceInquirer.loginFB(this, this);
        progressDialogHandler.show(this, R.string.loading_login);
    }

    /**
     * Saves username and password in shared preferences
     * @param username
     * @param password
     */
    public void saveUsernamePassword(String username, String password) {
        sharedPreferencesHandler.put(this, R.string.saved_username, username);
        sharedPreferencesHandler.put(this, R.string.saved_password, password);
        sharedPreferencesHandler.commit();
    }

    /**
     * Sends the login with FB request to the web service with also login and psw parameters so the
     * accounts are connected.
     * @param username
     * @param password
     * @param fbID facebook user id
     */
    public void connectFB(String username, String password, String fbID) {
        serviceInquirer.setUserInfo(username, password);
        String json = new JSONParser.OperatorJsonifier(null)
                .putLogin(username)
                .putPsw(password)
                .putFbId(fbID)
                .toString();
        serviceInquirer.post(this, getString(R.string.WEB_SERVICE_LOGIN_FB), json, this);
        progressDialogHandler.show(this, R.string.loading_login);
    }

    /**
     * Handles login response from the web service.
     *
     * @param originalUri
     * @param statusCode
     * @param response
     */
    @Override
    public void onResponseReceived(String originalUri, int statusCode, String response) {
        progressDialogHandler.dismiss();
        switch (statusCode) {
            case HttpStatus.SC_OK:
                Operator operator = new JSONParser(response).getOperator();
                if (operator == null) {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.login_error_not_operator)
                            .setTitle(R.string.login_error_title)
                            .setNegativeButton(R.string.dialog_ok_option, null)
                            .show();
                } else {
                    operator.id = serviceInquirer.getUsername();
                    operator.psw = serviceInquirer.getPassword();
                    //saving auto login option
                    sharedPreferencesHandler.put(this, getString(R.string.saved_auto_login),
                            ((CheckBox) findViewById(R.id.auto_login)).isChecked());
                    // if it's a normal login or registration
                    if (originalUri.equals(getString(R.string.WEB_SERVICE_LOGIN))) {
                        // saving username and password for future sessions
                        saveUsernamePassword(operator.id, operator.psw);
                    }
                    // else it's a fb login
                    else {
                        sharedPreferencesHandler.put(this, R.string.saved_use_fb, true);
                        sharedPreferencesHandler.put(this, R.string.saved_username, null);
                        sharedPreferencesHandler.put(this, R.string.saved_password, null);
                        sharedPreferencesHandler.commit();
                    }
                    // if the account is incomplete
                    if (operator.name_operator == null) {
                        Intent intent = new Intent(this, RegisterActivity.class);
                        intent.putExtra(RegisterActivity.ARG_IN_OPERATOR, operator);
                        startActivityForResult(intent, EDIT_OPERATOR_CODE);
                        return;
                    }
                    startMainActivity(operator);
                }
                break;
            case HttpStatus.SC_UNAUTHORIZED:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.login_error_wrong_credentials)
                        .setTitle(R.string.login_error_title)
                        .setNegativeButton(R.string.dialog_ok_option, null)
                        .show();
                break;
            case WebServiceRequestService.NO_INTERNET_CONNECTION:
                NoConnectionDialog.show(this);
                break;
        }
    }

    private void startMainActivity(Operator operator) {
        profileTracker = null;
        Intent intent = new Intent(this, MainActivity_operator.class);
        intent.putExtra(MainActivity_operator.ARG_OPERATOR, operator);
        startActivity(intent);
    }

    public static void logoutFB() {
        AccessToken.setCurrentAccessToken(null);
        Profile.setCurrentProfile(null);
    }

}
