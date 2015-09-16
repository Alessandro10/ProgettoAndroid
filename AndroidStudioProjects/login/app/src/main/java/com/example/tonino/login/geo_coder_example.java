package com.example.tonino.login;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class geo_coder_example extends Activity {

    EditText searchIn;
    Button searchButton;
    ListView searchOut;

    private ArrayAdapter<String> adapter;

    Geocoder geocoder;
    final static int maxResults = 5;
    List<Address> locationList;
    List<String> locationNameList;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_coder_example);
        searchIn = (EditText)findViewById(R.id.serchin);
        searchButton = (Button)findViewById(R.id.serch);
        searchOut = (ListView)findViewById(R.id.serchout);

        //searchButton.setOnClickListener(searchButtonOnClickListener);

        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationName = searchIn.getText().toString();
                LatLng pos = null;
                try {
                    pos = getLocationFromString(locationName);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i("posizione = ", " " + pos.latitude + " - " + pos.longitude);
            }
        });

        //geocoder = new Geocoder(this, Locale.ENGLISH);

        /*locationNameList = new ArrayList<String>(); //empty in start
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, locationNameList);
        searchOut.setAdapter(adapter);*/
    }

    OnClickListener searchButtonOnClickListener =
            new OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    String locationName = searchIn.getText().toString();

                    Toast.makeText(getApplicationContext(),
                            "Search for: " + locationName,
                            Toast.LENGTH_SHORT).show();

                    if(locationName == null){
                        Toast.makeText(getApplicationContext(),
                                "locationName == null",
                                Toast.LENGTH_LONG).show();
                    }else{
                        try {
                            Toast.makeText(getApplicationContext(),
                                    "INIZIO",
                                    Toast.LENGTH_LONG).show();
                            locationList = geocoder.getFromLocationName(locationName, maxResults);

                            Toast.makeText(getApplicationContext(),
                                    "FINE",
                                    Toast.LENGTH_LONG).show();

                            if(locationList == null){
                                Toast.makeText(getApplicationContext(),
                                        "locationList == null",
                                        Toast.LENGTH_LONG).show();
                            }else{
                                if(locationList.isEmpty()){
                                    Toast.makeText(getApplicationContext(),
                                            "locationList is empty",
                                            Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),
                                            "number of result: " + locationList.size(),
                                            Toast.LENGTH_LONG).show();

                                    locationNameList.clear();

                                    for(Address i : locationList){
                                        if(i.getFeatureName() == null){
                                            locationNameList.add("unknown");
                                        }else{
                                            locationNameList.add(i.getFeatureName());
                                        }
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            }


                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(),
                                    "network unavailable or any other I/O problem occurs " + locationName,
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }};

}