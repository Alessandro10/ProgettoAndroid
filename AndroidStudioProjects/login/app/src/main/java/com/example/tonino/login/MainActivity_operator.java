package com.example.tonino.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.example.tonino.login.Dialogs.CustomProgressDialog;
import com.example.tonino.login.Dialogs.NoConnectionDialog;
import com.example.tonino.login.Dialogs.ProgressDialogHandler;
import com.example.tonino.login.Types.Objective;
import com.example.tonino.login.Types.Operator;
import com.example.tonino.login.Types.Prize;
import com.example.tonino.login.Types.Route;
import com.facebook.CallbackManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class MainActivity_operator extends ActionBarActivity
        implements NavigationDrawerFragment_operator.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment_operator mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private ServiceInquirer serviceInquirer;

    public static final String STATE_ROUTES = "routes";
    public static final String STATE_OBJECTIVES = "objectives";
    public static final String STATE_PRIZES = "prizes";
    public static final String STATE_OPERATOR = "operator";
    public static final String STATE_COOKIES = "cookies";

    public static final String ARG_OPERATOR = "operator";

    public BitmapDescriptor defaultMarkerIcon;
    public BitmapDescriptor selectedMarkerIcon;
    public BitmapDescriptor editedMarkerIcon;
    public BitmapDescriptor newMarkerIcon;
    public Operator operatorInfo;
    public Map<Integer, Route> routeMap;
    public Map<Integer, Objective> objMap;
    public Map<Integer, Prize> prizeMap;
    public Set<String> allTags;
    public ProgressDialogHandler progressDialogHandler;

    public CallbackManager callbackManager;

    public final int [] titles = {R.string.title_routes, R.string.title_prizes,
            R.string.title_profile, R.string.title_verify, R.string.title_logout};

    public static final int MY_ROUTES = 0;
    public static final int MY_PRIZES = 1;
    public static final int MY_PROFILE = 2;
    public static final int VALIDATE_PRIZE = 3;
    public static final int LOG_OUT = 4;

    /**
     * Converts an int rgb to color to a float hue
     *
     * @param rgbColor the int rgb color to be converted
     * @return the float hue
     */
    private float rgbToHue(int rgbColor) {
        double red = Color.red(rgbColor)/255.0;
        double green = Color.green(rgbColor)/255.0;
        double blue = Color.blue(rgbColor)/255.0;
        double max = Math.max(red, Math.max(green, blue));
        double min = Math.min(red, Math.min(green, blue));
        double delta = max - min;
        double hue = 0;
        double constDegrees = 60;
        if (delta != 0) {
            if (max == red) {
                hue = constDegrees*(((green - blue)/delta)%6);
            }
            else if (max == green) {
                hue = constDegrees*((blue - red)/delta + 2);
            }
            else {
                hue = constDegrees*((red - green)/delta +4);
            }
        }
        return (float) hue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent startingIntent = getIntent();
        operatorInfo = startingIntent.getParcelableExtra(ARG_OPERATOR);
        serviceInquirer = ServiceInquirer.getInstance(operatorInfo.id, operatorInfo.psw);
        progressDialogHandler = new ProgressDialogHandler();
        super.onCreate(savedInstanceState);

        initializeMarkerIcons ();

        setContentView(R.layout.activity_main_operator);

        mNavigationDrawerFragment = (NavigationDrawerFragment_operator)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (mTitle == null) {
            mTitle = getTitle();
        }

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        if (resultCode == RESULT_OK && requestCode == LoginActivity.EDIT_OPERATOR_CODE) {
            operatorInfo = data.getParcelableExtra(RegisterActivity.ARG_OUT_OPERATOR);
        }
    }

    /**
     * Tries to initialize the Google Maps API in order to initialize hte marker icons.
     * @return whether the initialization was succesful
     */
    public boolean initializeMarkerIcons () {
        int result = MapsInitializer.initialize(this);
        if (result == ConnectionResult.SUCCESS) {
            defaultMarkerIcon = BitmapDescriptorFactory
                    .defaultMarker(rgbToHue(getResources().getColor(R.color.default_color)));
            selectedMarkerIcon = BitmapDescriptorFactory
                    .defaultMarker(rgbToHue(getResources().getColor(R.color.select_color)));
            editedMarkerIcon = BitmapDescriptorFactory
                    .defaultMarker(rgbToHue(getResources().getColor(R.color.edit_color)));
            newMarkerIcon = BitmapDescriptorFactory
                    .defaultMarker(rgbToHue(getResources().getColor(R.color.new_marker_color)));
        }
        return (result == ConnectionResult.SUCCESS);
    }

    public void initializeMaps(final ServiceInquirer.OnResponseReceivedCallback callback,
                               final boolean dismissDialog) {
        showLoadingDialog(R.string.loading_webservice_message);
        final MainActivity_operator thisActivity = this;
        serviceInquirer.get(this, getString(R.string.WEB_SERVICE_ROUTES),
                new ServiceInquirer.OnResponseReceivedCallback() {
                    @Override
                    public void onResponseReceived(String originalUri, int statusCode,
                                                   String response) {
                        switch (statusCode) {
                            case HttpStatus.SC_OK:
                                if (dismissDialog) {
                                    dismissLoadingDialog();
                                }
                                JSONParser parser = new JSONParser(response);
                                routeMap = parser.getRoutes();
                                objMap = parser.getObjectives();
                                prizeMap = parser.getPrizes();
                                callback.onResponseReceived(originalUri, statusCode, response);
                                break;
                            case WebServiceRequestService.NO_INTERNET_CONNECTION:
                                NoConnectionDialog.show(thisActivity);
                                break;
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Route [] routesArray;
        Objective [] objectivesArray;
        Prize [] prizesArray;
        int i = 0;
        if (routeMap != null && routeMap.size() > 0) {
            routesArray = new Route[routeMap.size()];
            i = 0;
            for (Integer route : routeMap.keySet()) {
                routesArray[i] = routeMap.get(route);
                i++;
            }
            outState.putParcelableArray(STATE_ROUTES, routesArray);
        }
        if (objMap != null && objMap.size() > 0) {
            objectivesArray = new Objective[objMap.size()];
            i = 0;
            for (Integer objective : objMap.keySet()) {
                objectivesArray[i] = objMap.get(objective);
                i++;
            }
            outState.putParcelableArray(STATE_OBJECTIVES, objectivesArray);
        }
        if (prizeMap != null && prizeMap.size() > 0) {
            prizesArray = new Prize[prizeMap.size()];
            i = 0;
            for (Integer prize : prizeMap.keySet()) {
                prizesArray[i] = prizeMap.get(prize);
                i++;
            }
            outState.putParcelableArray(STATE_PRIZES, prizesArray);
        }
        outState.putParcelable(STATE_OPERATOR, operatorInfo);
        outState.putBundle(STATE_COOKIES, serviceInquirer.getCookiesBundle());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceInquirer.setCookiesBundle(savedInstanceState.getBundle(STATE_COOKIES));
        operatorInfo = savedInstanceState.getParcelable(STATE_OPERATOR);
        Parcelable [] parceledRoutesArray =  savedInstanceState.getParcelableArray(STATE_ROUTES);
        if (parceledRoutesArray != null) {
            routeMap = new HashMap<>();
            for (Parcelable parceledRoute : parceledRoutesArray) {
                Route route = (Route) parceledRoute;
                routeMap.put(route.id, route);
            }
        }
        Parcelable [] parceledObjectivesArray = savedInstanceState
                .getParcelableArray(STATE_OBJECTIVES);
        if (parceledObjectivesArray != null) {
            objMap = new HashMap<>();
            for (Parcelable parceledObjective : parceledObjectivesArray) {
                Objective objective = (Objective) parceledObjective;
                objMap.put(objective.id, objective);
            }
        }
        Parcelable [] parceledPrizesArray = savedInstanceState.getParcelableArray(STATE_PRIZES);
        if (parceledPrizesArray != null) {
            prizeMap = new HashMap<>();
            for (Parcelable parceledPrize : parceledPrizesArray) {
                Prize prize = (Prize) parceledPrize;
                prizeMap.put(prize.id, prize);
            }
        }
    }

    public CustomProgressDialog showLoadingDialog(int loadingTextResource) {
        return progressDialogHandler.show(this, loadingTextResource);
    }

    public void dismissLoadingDialog() {
        progressDialogHandler.dismiss();
    }

    public ServiceInquirer getServiceInquirer() {
        return serviceInquirer;
    }

    public void closeDrawer() {
        mNavigationDrawerFragment.closeDrawer();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, boolean fromSavedInstanceState) {
        // update the main content by replacing fragmentso
        Fragment newFragment = null;
        switch (position) {
            case MY_ROUTES:
                newFragment = new RoutesFragment();
                break;
            case MY_PRIZES:
                newFragment = new SelectRouteFragment();
                break;
            case MY_PROFILE:
                mNavigationDrawerFragment.selectOldItem();
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.putExtra(RegisterActivity.ARG_IN_OPERATOR, operatorInfo);
                startActivityForResult(intent, LoginActivity.EDIT_OPERATOR_CODE);
                return;
            case VALIDATE_PRIZE:
                newFragment = new VerifyPrizeFragment();
                break;
            case LOG_OUT:
                LoginActivity.logoutFB();
                finish();
                return;
        }
        mTitle = getString(titles[position]);
        if (!fromSavedInstanceState) {
            replaceFragment(newFragment);
        }
    }

    public void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, newFragment, "xxx").commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LoginActivity.logoutFB();
    }

    /**
     * Replace current main fragment with PrizeFragment for editing prizes of the route with the
     * given id
     *
     * @param routeId id of the route whose prizes are to be edited
     */
    public void loadPrizeFragment(int routeId) {
        PrizeFragment prizeFragment = new PrizeFragment();
        Bundle args = new Bundle();
        args.putInt(PrizeFragment.ARG_ROUTE_ID, routeId);
        prizeFragment.setArguments(args);
        replaceFragment(prizeFragment);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

}

