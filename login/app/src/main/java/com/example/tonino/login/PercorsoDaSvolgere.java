package com.example.tonino.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

///  obj/verify
public class PercorsoDaSvolgere extends FragmentActivity {

    private boolean nuovoPremioVinto = false;

    private boolean zoom = false;

    private String coloreprecedente = "#ff0000";

    public Context myContext = this;

    private SupportMapFragment fm;

    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    TextView tvDistanceDuration;

    private Dictionary<Marker , Integer> dictMarkerIDPercorso = new Hashtable<Marker , Integer>();

    private Dictionary<Integer , List<Integer>> dictObjsDone = new Hashtable<Integer , List<Integer>>();

    private Dictionary<String , Percorsi.Operators> dictOperators =
            new Hashtable<String , Percorsi.Operators>();

    Dictionary<Integer, Percorsi.Objective> dictOb =
            new Hashtable<Integer, Percorsi.Objective>();
    Dictionary<Integer, Percorsi.PremioS> dictPre =
            new Hashtable<Integer, Percorsi.PremioS>();

    private String prevTitlePolyline = "";

    public class GestureMap{
        String title;
        List<String> type;
        String description;
        Polyline polyline;
        String distance;
        int hour;
        int minute;
        int id;
        List<Integer> listIdObjective;
        //Dictionary<Integer , Percorsi.Objective> dictzOb;
        Dictionary<String , Marker> dictMarkers;

        public GestureMap()
        {
            listIdObjective = new ArrayList<Integer>();
            id = -1;
            distance = "";
            hour = 0;
            minute = 0;
            //premio = new ArrayList<Percorsi.PremioS>();
            description = "";
            dictMarkers = new Hashtable<String , Marker>();
            //dictzOb = new Hashtable<Integer , Percorsi.Objective>();
            Dictionary dictMarkers;
            title = new String();
            type = new ArrayList<String>();
        }

        private void clear()
        {
            distance = "";
            hour = 0;
            minute = 0;
            description = "";
            //premio.clear();
            title = "";
            polyline = null;
            //dictzOb = new Hashtable<Integer , Percorsi.Objective>();
            dictMarkers = new Hashtable<String , Marker>();
            type.clear();
        }
    }

    private GestureMap gestureMap = new GestureMap();


    private Dictionary<String , GestureMap> dictMap = new Hashtable<String , GestureMap>();



    private Dictionary<Integer, Percorsi.Percorso> dictPercorso =
            new Hashtable<Integer, Percorsi.Percorso>();


    //--à...............................

    private String Memorizza = "Memorizza" + Salva.getNomeUtente();
    private String PremiVinti = "PremiVinti" + Salva.getNomeUtente();
    private String PercorsiSvolti = "PercorsiSvolti" + Salva.getNomeUtente();

    //.......................................



    //--------------------------------------------

    public boolean removeAll(String nameKey)
    {
        int i = 0;
        SharedPreferences sp = getSharedPreferences(Memorizza , 0/*MODE_PRIVATE*/);
        SharedPreferences.Editor mEdit1 = sp.edit();
        for(i = 0 ; i < 10 ; i++)
        {
            mEdit1.remove(nameKey + "_" + i);
        }
        mEdit1.remove(nameKey + "_MaxSize");
        mEdit1.remove(nameKey + "_size");
        return mEdit1.commit();
    }

    public boolean removeTutto()
    {
        SharedPreferences preferences = getSharedPreferences(Memorizza, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        return editor.commit();

    }


    public boolean removeArrayList(String nameKey)
    {
        int i = 0;
        SharedPreferences sp = getSharedPreferences(Memorizza , 0/*MODE_PRIVATE*/);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.remove(nameKey + "_size");
        //mEdit1.putInt(nameKey + "_size", list.size()); /* sKey is an array List*/
        int size = sp.getInt(nameKey + "_MaxSize" , 0);
        for(i = 0 ; i < size ; i++)
        {
            mEdit1.remove(nameKey + "_" + i);
        }
        mEdit1.remove(nameKey + "_MaxSize");
        mEdit1.remove(nameKey + "_size");
        return mEdit1.commit();
    }

    public boolean saveArrayList(List<Integer> list , String nameKey)
    {
        int i = 0;
        SharedPreferences sp = getSharedPreferences(Memorizza , 0/*MODE_PRIVATE*/);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt(nameKey + "_size", list.size()); /* sKey is an array List*/
        if(sp.getInt(nameKey + "_MaxSize" , 0) < list.size())
        {
            mEdit1.putInt(nameKey + "_MaxSize" , list.size());
        }
        mEdit1.putInt(nameKey + "_size", list.size());
        Iterator iterator = list.iterator();
        while(iterator.hasNext())
        {
            int codID = (int)iterator.next();
            //mEdit1.remove(nameKey + "_" + i);
            mEdit1.putInt(nameKey + "_" + i, codID);
            i++;
        }
        return mEdit1.commit();
    }

//-----------------------------------------

    public List<Integer> loadArrayList(String nomeKey)
    {
        SharedPreferences mSharedPreference1 = getSharedPreferences(Memorizza, 0/*MODE_PRIVATE*/);
        List<Integer> list = new ArrayList<Integer>();
        int size = mSharedPreference1.getInt(nomeKey + "_size", 0);
        for(int i=0;i<size;i++)
        {
            int element = mSharedPreference1.getInt(nomeKey + "_" + i, -1);
            if(element != -1) {
                list.add(element);
            }
        }
        return list;
    }

    //----------------------------------------------

    public void savePremioVinto(int iDPremio)
    {
        List<Integer> list = loadArrayList(PremiVinti);
        Toast.makeText(myContext , "loadArrayList  " + list , Toast.LENGTH_SHORT).show();
        Toast.makeText(myContext , "dim loadArray" + list.size() , Toast.LENGTH_SHORT).show();
        if(cercaIntListPos(list , iDPremio) == -1) {
            Toast.makeText(myContext , "idPremio " + iDPremio , Toast.LENGTH_SHORT).show();
            list.add(iDPremio);
            saveArrayList(list, PremiVinti);
        }
    }

    public boolean removePremioVinto(int iDPremio)
    {
        boolean bool = false;
        List<Integer> list = loadArrayList(PremiVinti);
        int elementRemove = cercaIntListPos(list , iDPremio);
        if(elementRemove > -1) {
            list.remove(elementRemove);
            bool = true;
            saveArrayList(list , PremiVinti);
        }
        return bool;
    }

    public boolean removePercorso(int idPercorso)
    {
        boolean bool = false;
        List<Integer> list = loadArrayList(PercorsiSvolti);
        int elementRemove = cercaIntListPos(list , idPercorso);
        if(elementRemove > -1) {
            list.remove(elementRemove);
            bool = true;
            saveArrayList(list, PercorsiSvolti);
            removeArrayList("" + idPercorso);
        }
        return bool;
    }

    //----------------------------------------------

    private int cercaIntListPos(List<Integer> list , int key)
    {
        int position = -1;
        Iterator iterator = list.iterator();
        while(iterator.hasNext())
        {
            int num = (int) iterator.next();
            position++;
            if(num == key)
            {
                Toast.makeText(myContext , "TROVATO VALORE = " + key + " position = " + position , Toast.LENGTH_SHORT).show();
                return position;
            }
        }
        Toast.makeText(myContext , "-1 id = " + key , Toast.LENGTH_SHORT).show();
        return -1;
    }

    public void saveObiettivoSvolto(int idPercorso , int iDObiettivo)
    {
        List<Integer> list = loadArrayList(PercorsiSvolti);
        List<Integer> listObiettivi = loadArrayList(""+idPercorso);

        if(cercaIntListPos(list , idPercorso) == -1) {
            list.add(idPercorso);
            saveArrayList(list , PercorsiSvolti);
        }
        listObiettivi.add(iDObiettivo);
        saveArrayList(listObiettivi , ""+idPercorso);
    }

    //---------------------------------------------

    public Dictionary<Integer , Percorsi.PremioS> getPremiVinti()
    {
        Dictionary<Integer , Percorsi.PremioS> dictPremiVinti = new Hashtable<Integer , Percorsi.PremioS>();
        List<Integer> list = loadArrayList(PremiVinti);

        Iterator iterator = list.iterator();
        while(iterator.hasNext())
        {
            int iDPremio = (int)iterator.next();
            if(dictPre.get(iDPremio) != null) {
                Percorsi.PremioS premio = dictPre.get(iDPremio);
                dictPremiVinti.put(iDPremio, premio);
                Toast.makeText(myContext , "Messo il premio " + iDPremio , Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(myContext , "non esiste il premio " + iDPremio , Toast.LENGTH_SHORT).show();
            }
        }
        return dictPremiVinti;
    }

    //--------------------------------------------

    public Dictionary<Integer , Percorsi.Percorso> getPercorsiSvolti()
    {
        Dictionary<Integer , Percorsi.Percorso> dictPercorsoIniziato = new Hashtable<Integer , Percorsi.Percorso>();
        List<Integer> list = loadArrayList(PercorsiSvolti);

        Iterator iterator = list.iterator();
        while(iterator.hasNext())
        {
            int iDPercorso = (int)iterator.next();
            if(dictPercorso.get(iDPercorso) != null) {
                Percorsi.Percorso percorso = dictPercorso.get(iDPercorso);
                dictPercorsoIniziato.put(iDPercorso, percorso);
            }
        }
        return dictPercorsoIniziato;
    }

    //--------------------------------------------

    public boolean getObiettiviSvoltiPerPercorso(int idPercorso , int idObiettivo)
    {
        List<Integer> list = loadArrayList("" + idPercorso);
        if(list != null)
        {
            Iterator iterator = list.iterator();
            while(iterator.hasNext())
            {
                int idOb = (int)iterator.next();
                if(idOb == idObiettivo)
                {
                    return true;
                }
            }
        }
        return false;
    }
// -------------------------------------------------


    private boolean ObjsDone(int percorsoID , int obiettivoID)
    {
        if(dictObjsDone.get(percorsoID) != null) {
            List<Integer> list = dictObjsDone.get(percorsoID);
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                int id = (int) iterator.next();
                if (id == obiettivoID) {
                    return true;
                }
            }
        }
        return false;
    }

    private void impostaaTrue()
    {
        nuovoPremioVinto = true;
    }

    private void ConvertPercorsoToGestureMapDictionary(Dictionary<Integer , Percorsi.Percorso> dict)
    {
        map.clear();
        int nomeObiettivo = 0;
        int nomePercorso = 0;
        List<MarkerOptions> lista_marker = new ArrayList<MarkerOptions>();
        Dictionary<String , Marker> dictMarkers;

        if(nuovoPremioVinto) {
            startService();
            nuovoPremioVinto = false;
        }

        for (Enumeration e = dict.keys(); e.hasMoreElements();)
        {
            int titolo = (int) e.nextElement();
            final Percorsi.Percorso percorso = dict.get(titolo);
            lista_marker.clear();
            dictMarkers = new Hashtable<String , Marker>();

            List<Integer> objecty = dict.get(titolo).obiettivi;

            Iterator lista_obiettivi = objecty.iterator();
            while(lista_obiettivi.hasNext())
            {
                int id = (int) lista_obiettivi.next();

                Percorsi.Objective ob = dictOb.get(id);
                MarkerOptions optionsMarker = new MarkerOptions();
                LatLng pos = new LatLng(ob.latitudine , ob.longitudine);
                optionsMarker.position(pos);
                if(!getObiettiviSvoltiPerPercorso(percorso.id , ob.id))
                {
                    optionsMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                }
                else
                {
                    optionsMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                }
                optionsMarker.title(dict.get(titolo).title);
                optionsMarker.snippet(ob.name);
                if(map == null)
                {
                    fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
                    map = fm.getMap();
                    map.setMyLocationEnabled(true);
                }

                Marker m = map.addMarker(optionsMarker);
                dictMarkerIDPercorso.put(m , percorso.id);
                //newGMap.dictMarkers.put(ob.name , m);
                dictMarkers.put(ob.name, m);
                lista_marker.add(optionsMarker);
            }

            final String url = getUrlByPos(lista_marker);
            coloreprecedente = generaColore(coloreprecedente);

            DownloadTask downloadTask =
                    new DownloadTask(percorso,
                            dictMarkers, coloreprecedente , 5);
            downloadTask.execute(url);
            //Salva.setGMap(map);
        }
    }

    public String generaColore(String prevColor)
    {
        String []lettere = {"a" , "b" , "c" , "d" , "e" , "f"};
        String nextColor="#";
        int i;
        int k;
        int n = 20;
        for(k=1 ; k<=3 ; k++) {
            int j=0;
            String c = prevColor.substring(k,k+1);
            while ((j != 4) && (prevColor.substring(k,k+1).equals(c))){
                j++;
                Random rand1 = new Random();
                n = rand1.nextInt(16) + 0;
                c = ""+n;
            }
            if (n > 9) {
                nextColor = nextColor + lettere[n - 10];
            } else {
                nextColor = nextColor + n;
            }
        }
        for(i=4 ; i<=6 ; i++) {
            Random rand = new Random();
            n = rand.nextInt(16) + 0;
            if (n > 9) {
                nextColor = nextColor + lettere[n - 10];
            } else {
                nextColor = nextColor + n;
            }
        }
        return nextColor;
    }



    private String getUrlByPos(List<MarkerOptions> options) {
        // Origin of route


        String way = "waypoints=";

        Iterator<MarkerOptions> it = options.iterator();
        Boolean first = true;
        Boolean firstWay = true;
        String str_origin="";
        String str_dest="";
        if(options.size() < 2)
        {
            while(it.hasNext())
            {
                MarkerOptions ops = (MarkerOptions) it.next();
                str_origin = "origin=" + ops.getPosition().latitude + "," + ops.getPosition().longitude;
                str_dest = "destination=" +
                        ops.getPosition().latitude + "," + ops.getPosition().longitude;
            }
        }
        else {
            while (it.hasNext()) {
                MarkerOptions ops = (MarkerOptions) it.next();
                if (first) {
                    str_origin = "origin=" + ops.getPosition().latitude + "," + ops.getPosition().longitude;
                    first = false;
                }
                if (it.hasNext()) {
                    str_dest = "destination=" +
                            ops.getPosition().latitude + "," + ops.getPosition().longitude;
                } else if (!first) {
                    if (firstWay) {
                        way = way + ops.getPosition().latitude + "," + ops.getPosition().longitude;
                        firstWay = false;
                    } else {
                        way = way + "|" + ops.getPosition().latitude + "," + ops.getPosition().longitude;
                    }
                }
            }
        }
        // Sensor enabled
        String sensor = "sensor=false";

        String mode="mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+way+"&"+sensor+"&"+mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }




    public void verifyConnection()
    {
        if(!isNetworkAvailable())
        {
            AlertDialog.Builder b =new AlertDialog.Builder(this);
            b.setTitle("Connessione assente");
            b.setMessage("Non sei connesso a internet!");
            b.setPositiveButton("Riprova", new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    verifyConnection();
                }
            });
            b.setNegativeButton("Esci", new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finish();
                }
            });
            b.create().show();
        }
    }


    public void readQRcode()
    {
        try {
                verifyConnection();
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
                startActivityForResult(intent, 0);

        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);
        }
    }


    private Thread t;
    private Thread t2;
    private ProgressDialog progress;

    public void controlQrcode(String msg){

        verifyConnection();
        sendJson(msg);

    }

    public void startService() {
        startService(new Intent(getBaseContext(), Service2.class));
    }

    protected void sendJson(final String qrcode) {
        final Context myContext = this;
        new Thread() {
            @Override
            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread

                String mss = "";
                HttpClient client = Salva.getHttpClient();

                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();
                String title = "", msg = "";
                try {
                    Data.setHttpclient(new DefaultHttpClient());
                    HttpPost post = new HttpPost(Data.getUrl() + "/obj/verify");
                    post.setHeader("user-agent", "app");
                    post.setHeader("Accept", "application/json");
                    json.put("validation_code", qrcode);

                    StringEntity se = new StringEntity(json.toString());

                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                    post.setEntity(se);


                    response = client.execute(post);
                    if (response != null) {
                        final StatusLine statusLine = response.getStatusLine();
                        int statusCode = statusLine.getStatusCode();

                        if (statusCode == 200) {

                            //dictObjsDone.put();

                            String result = "";
                            if (response.getEntity() != null) {
                                result = EntityUtils.toString(response.getEntity());
                            }
                            title = "REGISTRAZIONE OK";
                            if (result != null) {
                                // -------------------------------------------------------------------------------------------------
                                List<Percorsi.PremioS> list_premio = new ArrayList<Percorsi.PremioS>();
                                Percorsi.PremioS prime = new Percorsi.PremioS();
                                Percorsi.Percorso p = new Percorsi.Percorso();
                                JSONObject mainObject = null;

                                AlertDialog.Builder b =new AlertDialog.Builder(myContext);
                                b.setCancelable(false);
                                b.setTitle("Premio Vinto");
                                b.setMessage(result);
                                b.setPositiveButton("Riprova", new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                b.setNegativeButton("Esci", new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                b.create().show();

                                try {
                                    mainObject = new JSONObject(result);
                                    JSONArray routes = mainObject.getJSONArray("routes");
                                    JSONArray JObjs = new JSONArray();
                                    JSONArray JPre = new JSONArray();
                                    JSONArray Pre = mainObject.getJSONArray("prizes");
                                    JSONArray Objs = mainObject.getJSONArray("objs");
                                    JSONArray Operators = mainObject.getJSONArray("operators");
                                    JSONArray PrizesWon = mainObject.getJSONArray("prizes_won");
                                    String name = "";
                                    String city = "";
                                    String id = "";
                                    Double position_longitude, position_latitude;
                                    String validation_method = "";
                                    String nameObiettivo = "";
                                    String description = "";
                                    String percentage = "";
                                    String id_operator = "";
                                    int visible = 0;
                                    JSONArray mandatory_ob;
                                    String mff = "inzio";

                                    final int ObiettivoIDfatto = mainObject.getInt("verified_obj");

                                    for (int i = 0; i < Operators.length(); i++) {
                                        Percorsi.Operators operatori = new Percorsi.Operators();
                                        operatori.id = ((JSONObject) Operators.get(i)).getString("id");
                                        operatori.name_operator = ((JSONObject) Operators.get(i)).getString("name_operator");
                                        operatori.longitudine = ((JSONObject) Operators.get(i)).getDouble("position_longitude");
                                        operatori.latitudine = ((JSONObject) Operators.get(i)).getDouble("position_latitude");
                                        JSONArray list_type_operator = ((JSONObject) Operators.get(i)).getJSONArray("tags");
                                        for (int j = 0; j < list_type_operator.length(); j++) {
                                            operatori.type.add((String) list_type_operator.get(j));
                                        }
                                        Iterator iterator = operatori.type.iterator();
                                        dictOperators.put(operatori.id, operatori);
                                    }
                                    for (int i = 0; i < Pre.length(); i++) {
                                        name = ((JSONObject) Pre.get(i)).getString("name");
                                        id_operator = ((JSONObject) Pre.get(i)).getString("id_operator");
                                        id = ((JSONObject) Pre.get(i)).getString("id");
                                        description = ((JSONObject) Pre.get(i)).getString("description");
                                        visible = ((JSONObject) Pre.get(i)).getInt("visible");
                                        prime.name = name;
                                        prime.id_operator = id_operator;
                                        prime.description = description;
                                        prime.position_latitude = 0.0;//position_latitude;
                                        prime.position_longitude = 0.0;//position_longitude;
                                        prime.id = Integer.parseInt(id);
                                        prime.visible = visible;
                                        dictPre.put(prime.id, prime);
                                        prime = new Percorsi.PremioS();
                                    }

                                    for (int j = 0; j < PrizesWon.length(); j++) {
                                        int premio_vinto = (int)((JSONObject) PrizesWon.get(j)).getInt("id");
                                        savePremioVinto(premio_vinto);
                                        impostaaTrue();
                                    }



                                    for (int i = 0; i < Objs.length(); i++) {
                                        name = ((JSONObject) Objs.get(i)).getString("name");
                                        city = ((JSONObject) Objs.get(i)).getString("city");
                                        id = ((JSONObject) Objs.get(i)).getString("id");
                                        position_longitude = ((JSONObject) Objs.get(i)).getDouble("position_longitude");
                                        position_latitude = ((JSONObject) Objs.get(i)).getDouble("position_latitude");
                                        description = ((JSONObject) Objs.get(i)).getString("description");
                                        validation_method = ((JSONObject) Objs.get(i)).getString("validation_method");
                                        Percorsi.Objective ob = new Percorsi.Objective();
                                        ob.name = name;
                                        ob.city = city;
                                        ob.description = description;
                                        ob.latitudine = position_latitude;
                                        ob.longitudine = position_longitude;
                                        ob.id = Integer.parseInt(id);
                                        ob.validation_method = Integer.parseInt(validation_method);
                                        dictOb.put(ob.id, ob);
                                    }
                                    for (int i = 0; i < routes.length(); i++) {
                                        Dictionary mandatory_objs_dict = new Hashtable<Integer, List<Integer>>();
                                        p.id = ((JSONObject) routes.get(i)).getInt("id");
                                        JSONArray premio_per_percorso = new JSONArray();
                                        premio_per_percorso = ((JSONObject) routes.get(i)).getJSONArray("prizes");
                                        for (int k = 0; k < premio_per_percorso.length(); k++) {
                                            List<Integer> mandatory_objs = new ArrayList<Integer>();
                                            int idD = ((JSONObject) premio_per_percorso.get(k)).getInt("id");
                                            mandatory_ob = ((JSONObject) premio_per_percorso.get(k)).getJSONArray("mandatory_objs");
                                            for (int j = 0; j < mandatory_ob.length(); j++) {
                                                mandatory_objs.add((Integer) mandatory_ob.get(j));
                                            }
                                            Percorsi.PremiPercorso premiP = new Percorsi.PremiPercorso();
                                            premiP.id = idD;
                                            premiP.obiettivi_obbligatori = mandatory_objs;
                                            p.premi.add(premiP);
                                        }
                                        JSONArray tags = ((JSONObject) routes.get(i)).getJSONArray("tags");
                                        for (int i2 = 0; i2 < tags.length(); i2++) {
                                            if (!p.type.contains((String) tags.get(i2))) {
                                                p.type.add((String) tags.get(i2));
                                            }
                                        }
                                        Iterator iterator = p.type.iterator();
                                        p.title = ((JSONObject) routes.get(i)).getString("name");
                                        p.description = ((JSONObject) routes.get(i)).getString("description");
                                        JSONArray array_obiettivi = ((JSONObject) routes.get(i)).getJSONArray("objs");
                                        for (int j = 0; j < array_obiettivi.length(); j++) {
                                            int num = (int) array_obiettivi.get(j);
                                            p.obiettivi.add(num);
                                        }
                                        dictPercorso.put(p.id, p);

                                        saveObiettivoSvolto(p.id, ObiettivoIDfatto);

                                        controllaPercorsoCompletatoPerEliminazione(p.id);

                                        p = new Percorsi.Percorso();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // --------------------------------------------------------------------------------------------

                            }
                        } else {
                            title = "PROBLEMA NELLA REGISTRAZIONE";
                            msg = "non è stato possibile eseguire " +
                                    "la registrazione riprova più tardi " + statusCode;
                        }

                    } else {
                        title = "NESSUNA RISPOSTA";
                        msg = "non è stato possibile eseguire";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        ConvertPercorsoToGestureMapDictionary(dictPercorso);
                    }
                });
                Looper.loop(); //Loop in the message queue
            }
        }.start();
    }

    private String codiceSuper = "niente";


    private void controllaPercorsoCompletatoPerEliminazione(int idPercorso)
    {
        Percorsi.Percorso percorso = dictPercorso.get(idPercorso);
        List<Integer> list = percorso.obiettivi;
        Iterator iterator = list.iterator();
        while(iterator.hasNext()) {
            int obID = (int) iterator.next();

            if (!getObiettiviSvoltiPerPercorso(idPercorso, obID)) {
                return;
            }
        }
        removePercorso(idPercorso);
        dictPercorso.remove(idPercorso);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt("f", 1);
        savedInstanceState.putInt("f", 2);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                codiceSuper = contents;
                controlQrcode(codiceSuper);
            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percorso_da_svolgere);



        //boolean val = removeTutto();

        //Toast.makeText(myContext , "" + val , Toast.LENGTH_SHORT).show();

        dictPercorso = Salva.getDictPercorsoDaPassare();
        dictOb = Salva.getObiettivoReceive(Salva.getPositionTab());
        dictPre = Salva.getPremioReceive(Salva.getPositionTab());
        dictOperators = Salva.getOperatoriReceive(Salva.getPositionTab());

        final Button btn = (Button) findViewById(R.id.button);
        btn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundDrawable( getResources().getDrawable(R.drawable.bottone_selected) );
                }
                if(event.getAction() == MotionEvent.ACTION_UP ||event.getAction() == MotionEvent.ACTION_MOVE ) {
                    btn.setBackgroundDrawable( getResources().getDrawable(R.drawable.bottone));
                }

                return false;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readQRcode();
            }
        });



        try {
            // Loading map
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Enable MyLocation Button in the Map
        map.setMyLocationEnabled(true);

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                // TODO Auto-generated method stub
                Location pos = arg0;
                Salva.setPosizione(pos);
                if(!zoom) {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(arg0.getLatitude(), arg0.getLongitude()), 13.0f));
                    zoom = true;
                }
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                String newTitle = prevTitlePolyline;
                prevTitlePolyline = "";
                ChangeColor(newTitle , BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE) , "#ff0000" , 5);
                prevTitlePolyline = "";
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                Drawable fg = (Drawable) getResources().getDrawable(R.drawable.markerselezione);
                Drawable ng = resize2(fg);
                Bitmap bitM = drawableToBitmap(ng);
                int id = dictMarkerIDPercorso.get(marker);
                String title = dictPercorso.get(id).title;
                String titleID = title + id;
                ChangeColor(titleID , BitmapDescriptorFactory.fromBitmap(bitM), "#0000ff", 10);
                return false;
            }
        });
        ConvertPercorsoToGestureMapDictionary(dictPercorso);
    }


    private void ChangeColor(String title , BitmapDescriptor icon ,
                             String color , int lineWidth)
    {
        if(!prevTitlePolyline.equals(title)) {
            if (!prevTitlePolyline.equals("")) {
                for (Enumeration e = dictMap.get(prevTitlePolyline).dictMarkers.keys();
                     e.hasMoreElements(); ) {
                    String titleMarker = (String) e.nextElement();
                    Marker marker = dictMap.get(prevTitlePolyline).dictMarkers.
                            get(titleMarker);
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                }

            }
            for (Enumeration e = dictMap.get(title).dictMarkers.keys();
                 e.hasMoreElements(); ) {
                String titleMarker = (String) e.nextElement();

                final Marker opt = dictMap.get(title).dictMarkers.
                        get(titleMarker);
                //opt.setIcon(icon);

                final Handler handler = new Handler();

                final long startTime = SystemClock.uptimeMillis();
                final long duration = 2000;

                Projection proj = map.getProjection();
                final LatLng markerLatLng = opt.getPosition();
                final LatLng returnPos = opt.getPosition();

                Point startPoint = proj.toScreenLocation(markerLatLng);
                startPoint.offset(0, -100);
                final LatLng startLatLng = proj.fromScreenLocation(startPoint);

                final Interpolator interpolator = new BounceInterpolator();

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        long elapsed = SystemClock.uptimeMillis() - startTime;
                        float t = interpolator.getInterpolation((float) elapsed / duration);
                        double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                        double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                        opt.setPosition(new LatLng(lat, lng));

                        if (t < 1.0) {
                            // Post again 16ms later.
                            handler.postDelayed(this, 16);
                        }
                    }
                });
                // --------------------
            }
            prevTitlePolyline = title;
        }
    }
    private Drawable resize2(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 95, 105, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth()/3, canvas.getHeight()/3);
        drawable.draw(canvas);

        return bitmap;
    }


    private void initilizeMap() {
        if (map == null) {
            fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            // Getting Map for the SupportMapFragment
            map = fm.getMap();
            map = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            // check if map is created successfully or not
            if (map == null) {
                Toast.makeText(myContext ,
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private String getDirectionsUrl3(){

        // Origin of route
        String str_origin = "origin="+markerPoints.get(0).latitude+","+markerPoints.get(0).longitude;

        // Destination of route
        String str_dest = "destination="+markerPoints.get(markerPoints.size()-1).latitude+","+markerPoints.get(markerPoints.size()-1).longitude;

        int i;
        String way = "";
        if(markerPoints.size()>2) {
            way = "waypoints=" + markerPoints.get(1).latitude + "," + markerPoints.get(1).longitude;
            for (i = 2; i < markerPoints.size() - 1; i++) {
                way = way +"|"+ markerPoints.get(i).latitude + "," + markerPoints.get(i).longitude;
            }
        }
        // Sensor enabled
        String sensor = "sensor=false";

        String mode="mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+way+"&"+sensor+"&"+mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }


    private String getDirectionsUrl2(LatLng origin,LatLng dest , LatLng intermezzo , LatLng intermezzo2){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String way = "waypoints="+intermezzo.latitude+","+intermezzo.longitude
                +"|"+intermezzo2.latitude+","+intermezzo2.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        String mode="mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+way+"&"+sensor+"&"+mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        String mode="mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_es3, menu);
        return true;
    }

    private void drawMarker(LatLng point){
        markerPoints.add(point);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if(markerPoints.size()==1){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }else if(markerPoints.size()==2){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        map.addMarker(options);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        private String verycolore;
        private int lineWidth;
        private String titolo;
        private Dictionary<String , Marker> dictMarkers;
        private Dictionary<Integer , Percorsi.Objective> dictzOb;
        private String description = "";
        private List<String> type = new ArrayList<String>();
        private List<String> premio = new ArrayList<String>();
        Percorsi.Percorso route;

        public DownloadTask(Percorsi.Percorso percorso ,
                            Dictionary<String , Marker> dictMarkers2,
                            String colore, int linewidth){
            route = percorso;
            verycolore = colore;
            lineWidth = linewidth;
            dictMarkers = dictMarkers2;
        }


        // Downloading data in non-ui thread
        @Override
        public String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask(route ,
                    dictMarkers , verycolore , lineWidth);

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }


    }

    /**
     * A class to parse the Google Places in JSON format
     */
    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        private String verycolore;
        private int lineWidth;
        private String titolo;
        private Dictionary<String , Marker> dictMarkers;
        private Dictionary<Integer , Percorsi.Objective> dictzOb;
        private String description = "";
        private List<String> type = new ArrayList<String>();
        private List<String> premio = new ArrayList<String>();
        Percorsi.Percorso route;

        public ParserTask(Percorsi.Percorso percorso ,
                          Dictionary<String , Marker> dictMarkers2,
                          String colore, int linewidth){
            route = percorso;
            verycolore = colore;
            lineWidth = linewidth;
            dictMarkers = dictMarkers2;
        }


        private ProgressDialog dialog = new ProgressDialog(myContext);
        @Override
        public void onPreExecute() {
            this.dialog.setMessage("Sto inserendo i percorsi sulla mappa");
            this.dialog.show();
            //g.show();
        }

        // Parsing the data in non-ui thread
        @Override
        public List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }


        // Executes in UI thread, after the parsing process
        @Override
        public void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            ArrayList<LatLng> points2 = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";
            String duration2 = "";
            JSONObject distance2;
            Double dist = 0.0;

            if (result.size() < 1) {
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();

                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                LatLng []pointsVector = new LatLng[path.size()];

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    /*if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }*/
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(lineWidth);
                lineOptions.color(Color.parseColor(verycolore));

            }
            Polyline x = map.addPolyline(lineOptions);
            GestureMap gestureMap = new GestureMap();
            List<Integer> listaOb = new ArrayList<Integer>();
            gestureMap.dictMarkers = dictMarkers;
            gestureMap.title = route.title;
            gestureMap.id = route.id;
            gestureMap.polyline = x;
            gestureMap.type = route.type;
            gestureMap.description = route.description;
            gestureMap.distance = "2";//distance.toString();
            dictMap.put(route.title + route.id , gestureMap);

            dialog.dismiss();

        }
    }




}