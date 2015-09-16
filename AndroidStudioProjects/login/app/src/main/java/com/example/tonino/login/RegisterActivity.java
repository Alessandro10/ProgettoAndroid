package com.example.tonino.login;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tonino.login.Dialogs.DialogBroadcastReceiver;
import com.example.tonino.login.Dialogs.MessageDialog;
import com.example.tonino.login.Dialogs.NoConnectionDialog;
import com.example.tonino.login.Dialogs.ProgressDialogHandler;
import com.example.tonino.login.Dialogs.TagsDialog;
import com.example.tonino.login.Types.Operator;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RegisterActivity extends ActionBarActivity implements View.OnClickListener,
        ServiceInquirer.OnResponseReceivedCallback {

    public static final String ARG_OUT_OPERATOR = "operator";
    public static final String ARG_IN_OPERATOR = ARG_OUT_OPERATOR;

    public static final String STATE_TAGS = "tags";

    protected Operator newOperator;
    protected EditText usernameView;
    protected EditText passwordView;
    protected EditText companyView;
    protected EditText countryView;
    protected EditText cityView;
    protected EditText addressView;
    protected ArrayList<EditText> fieldsList;
    protected ImageButton tagsBtn;
    protected Set<String> allTags;
    protected TagsHandler tagsHandler;
    protected Button registerBnt;
    protected ServiceInquirer serviceInquirer;

    protected Operator oldOperator;

    protected ProgressDialogHandler progressDialogHandler;
    protected TagsBroadcastReceiver tagsBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_operator);

        // get operator if it was passed
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                oldOperator = getIntent().getExtras().getParcelable(ARG_IN_OPERATOR);
            }
        }

        serviceInquirer = ServiceInquirer.getInstance();
        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        companyView = (EditText) findViewById(R.id.company);
        countryView = (EditText) findViewById(R.id.country);
        cityView = (EditText) findViewById(R.id.city);
        addressView = (EditText) findViewById(R.id.address);
        fieldsList = new ArrayList<>();
        if (oldOperator == null) {
            fieldsList.add(usernameView);
            fieldsList.add(passwordView);
        }
        fieldsList.add(companyView);
        fieldsList.add(countryView);
        fieldsList.add(cityView);
        fieldsList.add(addressView);
        tagsBtn = (ImageButton) findViewById(R.id.tag_new_btn);
        registerBnt = (Button) findViewById(R.id.register_btn);
        tagsBtn.setOnClickListener(this);
        registerBnt.setOnClickListener(this);
        tagsHandler = new TagsHandler((ViewGroup) findViewById(R.id.tags_container),
                new HashSet<String>());
        tagsHandler.showAndHandleDeleteTagBtnTouch();
        progressDialogHandler = new ProgressDialogHandler();
        tagsBroadcastReceiver = new TagsBroadcastReceiver(this);
        if (oldOperator != null) {
            findViewById(R.id.username_label).setVisibility(View.GONE);
            findViewById(R.id.username).setVisibility(View.GONE);
            findViewById(R.id.password_label).setVisibility(View.GONE);
            findViewById(R.id.password).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.company)).setText(oldOperator.name_operator);
            ((TextView) findViewById(R.id.company)).setText(oldOperator.name_operator);
            /*Address address = getAddress(oldOperator.position_latitude,
                    oldOperator.position_longitude);*/
            List<Address> address_list = getFromLocation(oldOperator.position_latitude,
                    oldOperator.position_longitude, 1);
            Address address = address_list.iterator().next();
            if (address != null) {
                if (address.getCountryName() != null) {
                    ((TextView) findViewById(R.id.country)).setText(address.getCountryName());
                }
                if (address.getLocality() != null) {
                    ((TextView) findViewById(R.id.city)).setText(address.getLocality());
                }
                if (address.getAddressLine(0) != null) {
                    ((TextView) findViewById(R.id.address)).setText(address.getAddressLine(0));
                }
            }
            tagsHandler.selectedTags.addAll(oldOperator.tags);
            tagsHandler.writeTags(getLayoutInflater());
            ((Button) findViewById(R.id.register_btn)).setText(R.string.dialog_confirm_btn);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (tagsBroadcastReceiver != null) {
            tagsBroadcastReceiver.registerForReceive(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (tagsBroadcastReceiver != null) {
            tagsBroadcastReceiver.unregisterForReceive(this);
        }
    }


    public static List<Address> getFromLocation(double lat, double lng, int maxResult) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String address = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=false&language=" + Locale.getDefault().getCountry(), lat, lng);
        HttpGet httpGet = new HttpGet(address);
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(AllClientPNames.USER_AGENT, "Mozilla/5.0 (Java) Gecko/20081007 java-geocoder");
        client.getParams().setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 5 * 1000);
        client.getParams().setIntParameter(AllClientPNames.SO_TIMEOUT, 25 * 1000);
        HttpResponse response;

        List<Address> retList = null;

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);

            retList = new ArrayList<Address>();

            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                JSONArray results = jsonObject.getJSONArray("results");
                if (results.length() > 0) {
                    for (int i = 0; i < results.length() && i < maxResult; i++) {
                        JSONObject result = results.getJSONObject(i);
                        //Log.e(MyGeocoder.class.getName(), result.toString());
                        Address addr = new Address(Locale.getDefault());
                        // addr.setAddressLine(0, result.getString("formatted_address"));

                        JSONArray components = result.getJSONArray("address_components");
                        String streetNumber = "";
                        String route = "";
                        for (int a = 0; a < components.length(); a++) {
                            JSONObject component = components.getJSONObject(a);
                            JSONArray types = component.getJSONArray("types");
                            if(a == 6)
                            {
                                addr.setCountryName(component.getString("long_name"));
                            }
                            Log.i("CITTA " , component.getString("long_name"));
                            for (int j = 0; j < types.length(); j++) {
                                String type = types.getString(j);
                                if (type.equals("locality")) {
                                    addr.setLocality(component.getString("long_name"));
                                } else if (type.equals("street_number")) {
                                    streetNumber = component.getString("long_name");
                                } else if (type.equals("route")) {
                                    route = component.getString("long_name");
                                }
                            }
                        }
                        addr.setAddressLine(0, route + " " + streetNumber);

                        addr.setLatitude(result.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                        addr.setLongitude(result.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));

                        retList.add(addr);
                    }
                }
            }


        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        } catch (JSONException e) {

        }

        return retList;
    }


    protected Address getAddress(double latitude, double longitude) {
        Address result = null;
        try {
            List<Address> results = new Geocoder(this).getFromLocation(latitude, longitude, 1);
            if (!results.isEmpty()) {
                result = results.get(0);
            }
        } catch (IOException e) {
        }
        return result;
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tagsHandler.selectedTags.addAll(savedInstanceState.getStringArrayList(STATE_TAGS));
        tagsHandler.writeTags(getLayoutInflater());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(STATE_TAGS, new ArrayList<String>(tagsHandler.selectedTags));
    }

    /**
     * Handles click of the register button and of tags button.
     *
     * @param v can only be registerBtn
     */
    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == registerBnt.getId()) {
            // checking the inputs then sending the request to the web service.
            boolean isOneEmpty = false;
            for (EditText view : fieldsList) {
                if (view.getText().toString().isEmpty()) {
                    isOneEmpty = true;
                    break;
                }
            }
            if (isOneEmpty) {
                Toast.makeText(this, getString(R.string.register_error_empty), Toast.LENGTH_SHORT)
                        .show();
            } else {
                String username = usernameView.getText().toString();
                String password = passwordView.getText().toString();
                String name_operator = companyView.getText().toString();
                String fullAddress = countryView.getText().toString() + " " +
                        cityView.getText().toString() + " " + addressView.getText().toString();
                register(username, password, name_operator, fullAddress, tagsHandler.selectedTags);
            }
        }
        else {
            prepareForAddingTags();
        }
    }

    protected void prepareForAddingTags() {
        if (allTags == null) {
            progressDialogHandler.show(this, R.string.loading_tags_message);
            final RegisterActivity thisActivity = this;
            serviceInquirer.get(this, getString(R.string.WEB_SERVICE_TAGS),
                    new ServiceInquirer.OnResponseReceivedCallback() {
                        @Override
                        public void onResponseReceived(String originalUri, int statusCode, String response) {
                            switch (statusCode) {
                                case HttpStatus.SC_OK:
                                    progressDialogHandler.dismiss();
                                    JSONParser parser = new JSONParser(response);
                                    allTags = parser.getTags();
                                    tagsHandler.createTagsDialog(thisActivity, allTags).show();
                                    break;
                                case WebServiceRequestService.NO_INTERNET_CONNECTION:
                                    NoConnectionDialog.show(thisActivity);
                                    break;
                            }
                        }
                    });
        }
        else {
            tagsHandler.createTagsDialog(this, allTags).show();
        }
    }

    public class TagsBroadcastReceiver extends DialogBroadcastReceiver {

        public TagsBroadcastReceiver(Context context) {
            super(context);
        }

        @Override
        public void onDialogConfirm(Bundle customParams, Bundle userInput) {
            tagsHandler.selectedTags.addAll(userInput.getStringArrayList(TagsDialog.ARG_SELECTED_TAGS));
            tagsHandler.writeTags(getLayoutInflater());
        }

        @Override
        public void onDialogCancel() {}

        @Override
        public void onDialogDelete(Bundle customParams) {}
    }

    /**
     * Sends the login request to the web service.
     *
     * @param username
     * @param password
     */
    public void register(String username, String password, String nameOperator, String fullAddress,
                         Set<String> tags)  {
        //LatLng position = getPosition(fullAddress);
        LatLng position = null;
        try {
            position = getLocationFromString(fullAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (position == null) {
            Toast.makeText(this, getString(R.string.register_error_incorrect_address),
                    Toast.LENGTH_SHORT).show();
            Log.i("position = " , fullAddress);
        }
        else {
            newOperator = new Operator();
            newOperator.id = username;
            newOperator.psw = password;
            newOperator.name_operator = nameOperator;
            newOperator.position_latitude = position.latitude;
            newOperator.position_longitude = position.longitude;
            newOperator.tags = tags;
            JSONParser.OperatorJsonifier jsonifier = new JSONParser.OperatorJsonifier(newOperator)
                    .putFields();
            if (oldOperator == null) {
                jsonifier.putTags(tags);
                serviceInquirer.post(this, getString(R.string.WEB_SERVICE_SIGNUP), jsonifier.toString(), this);
            }
            else {
                Set<String> addedTags = new HashSet<>(newOperator.tags);
                Set<String> removedTags = new HashSet<>(oldOperator.tags);
                addedTags.removeAll(oldOperator.tags);
                removedTags.removeAll(newOperator.tags);
                jsonifier.putAddedTags(addedTags);
                jsonifier.putRemovedTags(removedTags);
                serviceInquirer.put(this, getString(R.string.WEB_SERVICE_USER_EDIT), jsonifier.toString(), this);
            }
            progressDialogHandler.show(this, R.string.loading_register);
        }
    }

    /*public LatLng getPosition(String address) {
        LatLng position;
        try {
            List<Address> addressList = new Geocoder(this).getFromLocationName(address, 1);
            if (!addressList.isEmpty()) {
                Address resultingAddress = addressList.get(0);
                position = new LatLng(resultingAddress.getLatitude(),
                        resultingAddress.getLongitude());
            }
            else {
                throw new IOException();
            }
        } catch (IOException e) {
            position = null;
        }
        return position;
    }*/

    public static LatLng getLocationFromString(String address)
            throws JSONException, UnsupportedEncodingException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpGet httpGet = new HttpGet(
                "http://maps.google.com/maps/api/geocode/json?address="
                        + URLEncoder.encode(address, "UTF-8") + "&ka&sensor=false");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject(stringBuilder.toString());

        double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng");

        double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat");

        return new LatLng(lat, lng);
    }

    public static List<Address> getStringFromLocation(double lat, double lng)
            throws ClientProtocolException, IOException, JSONException {

        String address = String
                .format(Locale.ENGLISH,                                 "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
                        + Locale.getDefault().getCountry(), lat, lng);
        HttpGet httpGet = new HttpGet(address);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        List<Address> retList = null;

        response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        int b;
        while ((b = stream.read()) != -1) {
            stringBuilder.append((char) b);
        }

        JSONObject jsonObject = new JSONObject(stringBuilder.toString());

        retList = new ArrayList<Address>();

        if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
            JSONArray results = jsonObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String indiStr = result.getString("formatted_address");
                Address addr = new Address(Locale.getDefault());
                addr.setAddressLine(0, indiStr);
                retList.add(addr);
            }
        }

        return retList;
    }

    public LatLng getPosition(String address) {
        LatLng position;
        try {
            Log.i("address" , address);

            List<Address> addressList = new Geocoder(this).getFromLocationName(address, 1);

            if (!addressList.isEmpty()) {
                Log.i("address","pieno");
                Address resultingAddress = addressList.get(0);
                Log.i("address", " " + resultingAddress.getLatitude() );
                position = new LatLng(resultingAddress.getLatitude(),
                        resultingAddress.getLongitude());
                Log.i("address" , "posizione = " + position);
            }
            else {
                Log.i("address","vuoto");
                throw new IOException();
            }
        } catch (IOException e) {
            position = null;
        }
        return null;//position;
    }

    /**
     * Handles register response from the web service, if request was accepted sends username and
     * password to LoginActivity that will login the operator.
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
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                intent.putExtra(ARG_OUT_OPERATOR, newOperator);
                finish();
                break;
            case HttpStatus.SC_CONFLICT:
                new MessageDialog.Builder(this)
                        .setMessage(R.string.username_taken_message)
                        .setTitle(R.string.username_taken_title)
                        .setNoConfirmButton()
                        .setCancelBtnText(R.string.dialog_ok_option)
                        .show();
                break;
            case WebServiceRequestService.NO_INTERNET_CONNECTION:
                NoConnectionDialog.show(this);
                break;
            default:
                new MessageDialog.Builder(this)
                        .setMessage(R.string.unexpected_error_message)
                        .setTitle(R.string.unexpected_error_title)
                        .setNoConfirmButton()
                        .setCancelBtnText(R.string.dialog_ok_option)
                        .show();
                break;
        }
    }

}
