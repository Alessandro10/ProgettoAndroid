package com.example.tonino.login;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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


public class HomeSwipe extends Fragment implements android.support.v7.app.ActionBar.TabListener {

    private Dictionary<Marker , Integer> dictMarkerIDPercorso = new Hashtable<Marker , Integer>();

    private Dictionary<String , Percorsi.Operators> dictOperators =
            new Hashtable<String , Percorsi.Operators>();

    Dictionary<Integer, Percorsi.Objective> dictOb =
            new Hashtable<Integer, Percorsi.Objective>();
    Dictionary<Integer, Percorsi.PremioS> dictPre =
            new Hashtable<Integer, Percorsi.PremioS>();

    Dictionary<Integer, Percorsi.Percorso> dictPercorso =
            new Hashtable<Integer, Percorsi.Percorso>();

    private String prevTitlePolyline = "";

    public class GestureMap{
        String title;
        List<String> type;
        String description;
        Polyline polyline;
        String distance;
        int hour;
        int minute;
        //Dictionary<Integer , Percorsi.Objective> dictzOb;
        Dictionary<String , Marker> dictMarkers;

        public GestureMap()
        {
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


    private List<GestureMap> infomap = new ArrayList<GestureMap>();
    public BitmapDescriptor[] IconVectorMap;
    private String []Alltitle;
    private Polyline polylineSelect;
    private String titleSelected="";
    private Dictionary<String , Dictionary<Polyline , Integer>>
            AllLine = new Hashtable<String , Dictionary<Polyline , Integer>>();
    private String []colorLine;
    private LatLng position;
    private ArrayList<LatLng> positionList = new ArrayList<LatLng>();
    private ArrayList<Marker> optionsList = new ArrayList<Marker>();
    private Dictionary<Integer , ArrayList<Marker>> AllMarkers =
            new Hashtable<Integer , ArrayList<Marker>>();
    private String coloreprecedente = "#ff00ff";
    private String ordina = "Obbiettivo";
    private int numbertab=1;
    private String testoricerca = "";
    private Boolean ricercasettata = false;
    private int km = 15;
    private static Double latitude, longitude;
    private GoogleMap map;
    private int mHour = 23;
    private int mMinute = 59;
    private Dictionary<Integer, Dictionary<Integer , Integer>> superDict = new Hashtable<Integer , Dictionary<Integer , Integer>>();
    private final Dictionary<Integer, Integer> dictPremio = new Hashtable<Integer , Integer>();
    private final Dictionary<Integer, Integer> dictObiettivo = new Hashtable<Integer , Integer>();
    private boolean [][]bool = new boolean[3][];
    private boolean []settato = new boolean[3];
    final ArrayList seletedItems=new ArrayList();
    final ArrayList seletedItemsOld=new ArrayList();
    private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
            -73.998585);
    private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
    private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);

    ArrayList<LatLng> markerPoints;
    Dictionary<String , ArrayList<LatLng>> route = new Hashtable<String , ArrayList<LatLng>>();
    TextView tvDistanceDuration;
    private final String[] array = {"uno" , "due" , "tre" , "quattro" , "uno" , "due" , "tre" , "quattro" , "uno" , "due" , "tre" , "quattro" , "uno" , "due" , "tre" , "quattro"};
    private final String[] Ob_string = {"Random" , "Natura" , "Sport" , "Città" , "Divertimento" , "Comico"};
    private final String[] Pr_string = {"Random" , "Natura" , "Sport" , "Città" , "Divertimento" , "Comico"};


    private Expandable b = new Expandable();
    private static View view;
    private SupportMapFragment fragment;
    private Dictionary<String, Dictionary<String, String>> dicts = new Hashtable<String, Dictionary<String, String>>();
    private Dictionary<String, String> di = new Hashtable<String, String>();
    private String[] titoli = new String[3];
    private TabHost tabs;
    private ViewPager tabsviewPager;
    private ActionBar mActionBar;
    private FragmentPageAdapter mTabsAdapter;
    private FragmentActivity myContext;

    private GestureMap newGMap = new GestureMap();

    private void ConvertPercorsoToGestureMapDictionary(Dictionary<Integer , Percorsi.Percorso> dict)
    {
        int nomeObiettivo = 0;
        int nomePercorso = 0;
        List<MarkerOptions> lista_marker = new ArrayList<MarkerOptions>();
        Dictionary<String , Marker> dictMarkers;

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
                optionsMarker.icon(IconVectorMap[2]);
                optionsMarker.title(dict.get(titolo).title);
                optionsMarker.snippet(ob.name);
                Marker m = map.addMarker(optionsMarker);
                dictMarkerIDPercorso.put(m , percorso.id);
                //newGMap.dictMarkers.put(ob.name , m);
                dictMarkers.put(ob.name , m);
                lista_marker.add(optionsMarker);
            }

                final String url = getUrlByPos(lista_marker);
                //coloreprecedente = generaColore(coloreprecedente);

                DownloadTask downloadTask =
                        new DownloadTask(percorso,
                                dictMarkers, "#ff0000", 5);

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

            //dictMap.put(titolo , newGMap);
        }
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


    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void verifyConnection()
    {
        if(!isNetworkAvailable())
        {
            AlertDialog.Builder b =new AlertDialog.Builder(myContext);
            b.setCancelable(false);
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

                    myContext.finish();
                }
            });
            b.create().show();
        }
    }

    private void estrapolatrice(int utilita , int iDp ,String tipo , String titolo , Dictionary<Integer , Percorsi.Percorso> dictPercorso ,
                                Dictionary<Integer , Percorsi.PremioS> dictPre  ,  Dictionary<Integer , Percorsi.Objective> dictOb)
    {
        Dictionary<Integer , String> dictPremi_con_ob = new Hashtable<Integer , String>();
        String name_premi = "";
        String name_obiettivi = "";
        String name_obiettivi_ob = "";
        String description = "";
        //for (Enumeration e = dictPercorso.keys(); e.hasMoreElements(); ) {
            //int key = (int) e.nextElement();
            //Percorsi.Percorso p = dictPercorso.get(key);
        Percorsi.Percorso p = dictPercorso.get(iDp);
            if (p.title.equals(titolo)) {
                description = p.description;
                Iterator<Percorsi.PremiPercorso> it = p.premi.iterator();
                while (it.hasNext()) {
                    Percorsi.PremiPercorso premio = it.next();

                    name_premi = name_premi + " " + dictPre.get(premio.id).name;
                    Iterator<Integer> it2 = premio.obiettivi_obbligatori.iterator();
                    while (it2.hasNext()) {
                        int premio_ob = (int) it2.next();
                        name_obiettivi_ob = name_obiettivi_ob + " , " + dictOb.get(premio_ob).name;
                    }
                    dictPremi_con_ob.put(premio.id, name_obiettivi_ob);
                    name_obiettivi_ob = "";
                }

                Iterator<Integer> it2 = p.obiettivi.iterator();
                while (it2.hasNext()) {
                    int num_obiettivo = it2.next();
                    name_obiettivi = name_obiettivi + " " + dictOb.get(num_obiettivo).name;
                }
                open(utilita, iDp , tipo, titolo, description, name_premi, name_obiettivi, dictPremi_con_ob);
                //break;
            //}
        }
    }

    public void open(int utilita , final int iD , String tipo , String titolo , String descrizione , String premio , String name_obiettivo ,Dictionary<Integer , String> dictPremi_con_ob){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);

        LayoutInflater li = LayoutInflater.from(myContext);
        View promptsView = li.inflate(R.layout.dialog_view, null);

        LayoutInflater li2 = LayoutInflater.from(myContext);
        View promptsView2 = li2.inflate(R.layout.newdialog, null);

        TextView  name = (TextView) promptsView2.findViewById(R.id.titolo);
        TextView  type = (TextView) promptsView.findViewById(R.id.tipo);
        TextView  description = (TextView) promptsView.findViewById(R.id.descrizione);
        //TextView  time = (TextView) promptsView.findViewById(R.id.tempo);
        //TextView  distance = (TextView) promptsView.findViewById(R.id.distanza);
        TextView  price = (TextView) promptsView.findViewById(R.id.premio);

        price.setText(premio);
        name.setText(titolo);
        type.setText(tipo);
        description.setText(descrizione);
        //time.setText(tempo);
        //distance.setText(distanza);

        alertDialogBuilder.setCustomTitle(promptsView2);

        alertDialogBuilder.setView(promptsView);

        String targa;

        if(utilita == 0)
        {
            targa = "Svolgi";
        }
        else
        {
            targa = "Continua";
        }

        alertDialogBuilder.setPositiveButton(targa,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //passa eventuali parametri
                        Dictionary<Integer, Percorsi.Percorso> dictPercorsoDaPassare =
                                new Hashtable<Integer, Percorsi.Percorso>();
                        Percorsi.Percorso p = new Percorsi.Percorso();
                        p = dictPercorso.get(iD);
                        dictPercorsoDaPassare.put(p.id ,p);
                        Salva.setDictPercorsoDaPassare(dictPercorsoDaPassare);
                        Intent intent = new Intent(myContext , PercorsoDaSvolgere.class);
                        myContext.startActivity(intent);

                    }
                });
        alertDialogBuilder.setNegativeButton("Indietro",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });



        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    /*public void open(int utilita , String tipo , String titolo , String descrizione , String tempo , String distanza , String premio){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);

        LayoutInflater li = LayoutInflater.from(myContext);
        View promptsView = li.inflate(R.layout.dialog_view, null);

        LayoutInflater li2 = LayoutInflater.from(myContext);
        View promptsView2 = li2.inflate(R.layout.newdialog, null);

        TextView  name = (TextView) promptsView2.findViewById(R.id.titolo);
        TextView  type = (TextView) promptsView.findViewById(R.id.tipo);
        TextView  description = (TextView) promptsView.findViewById(R.id.descrizione);
        //TextView  time = (TextView) promptsView.findViewById(R.id.tempo);
        //TextView  distance = (TextView) promptsView.findViewById(R.id.distanza);
        TextView  price = (TextView) promptsView.findViewById(R.id.premio);

        price.setText(premio);
        name.setText(titolo);
        type.setText(tipo);
        description.setText(descrizione);
        //time.setText(tempo);
        //distance.setText(distanza);

        alertDialogBuilder.setCustomTitle(promptsView2);

        alertDialogBuilder.setView(promptsView);

        String targa;

        if(utilita == 0)
        {
            targa = "Svolgi";
        }
        else
        {
            targa = "Continua";
        }

        alertDialogBuilder.setPositiveButton(targa,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //passa eventuali parametri
                        Intent intent = new Intent(myContext , es3.class);
                        myContext.startActivity(intent);

                    }
                });
        alertDialogBuilder.setNegativeButton("Indietro",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }*/



    public int dammiPos(String title)
    {
        int i;
        for(i=0;i<11;i++) {
            if(Alltitle[i].equals(title))
            {
                return i;
            }
        }
        return -1;
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

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 50, 50, false);
        return new BitmapDrawable(getResources(), bitmapResized);
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

    private List<MarkerOptions> punti = new ArrayList<MarkerOptions>();

    private List<String> listTitle = new ArrayList<String>();

    private void ChangeColor(String title , BitmapDescriptor icon ,
                             String color , int lineWidth)
    {
        //String title = markerOne.getTitle();
        if(!prevTitlePolyline.equals(title)) {
            if (!prevTitlePolyline.equals("")) {
                for (Enumeration e = dictMap.get(prevTitlePolyline).dictMarkers.keys();
                     e.hasMoreElements(); ) {
                    String titleMarker = (String) e.nextElement();
                    Marker marker = dictMap.get(prevTitlePolyline).dictMarkers.
                            get(titleMarker);
                    marker.setIcon(IconVectorMap[2]);
                }
                if(dictMap.get(prevTitlePolyline) != null) {
                    dictMap.get(prevTitlePolyline).polyline.setColor(Color.parseColor("#ff0000"));
                    dictMap.get(prevTitlePolyline).polyline.setWidth(5);
                    dictMap.get(prevTitlePolyline).polyline.setZIndex(
                            dictMap.get(prevTitlePolyline).polyline.getZIndex() - 100
                    );
                }
            }
            for (Enumeration e = dictMap.get(title).dictMarkers.keys();
                 e.hasMoreElements(); ) {
                String titleMarker = (String) e.nextElement();

                final Marker opt = dictMap.get(title).dictMarkers.
                        get(titleMarker);
                opt.setIcon(icon);

                final Handler handler = new Handler();

                final long startTime = SystemClock.uptimeMillis();
                final long duration = 2000;

                Projection proj = map.getProjection();
                final LatLng markerLatLng = opt.getPosition();
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
            if(dictMap.get(title) != null) {
                dictMap.get(title).polyline.setColor(Color.parseColor(color));
                dictMap.get(title).polyline.setWidth(lineWidth);
                dictMap.get(title).polyline.setZIndex(
                        dictMap.get(title).polyline.getZIndex() + 100
                );
            }
        }
    }


    public static void isNetworkAvailable(final Handler handler, final int timeout) {
        // ask fo message '0' (not connected) or '1' (connected) on 'handler'
        // the answer must be send before before within the 'timeout' (in milliseconds)

        new Thread() {
            private boolean responded = false;
            @Override
            public void run() {
                // set 'responded' to TRUE if is able to connect with google mobile (responds fast)
                new Thread() {
                    @Override
                    public void run() {
                        HttpGet requestForTest = new HttpGet("http://daniele.cortesi2.web.cs.unibo.it/wsgi/routes");
                        try {
                            new DefaultHttpClient().execute(requestForTest); // can last...
                            responded = true;
                        }
                        catch (Exception e) {
                        }
                    }
                }.start();

                try {
                    int waited = 0;
                    while(!responded && (waited < timeout)) {
                        sleep(100);
                        if(!responded ) {
                            waited += 100;
                        }
                    }
                }
                catch(InterruptedException e) {} // do nothing
                finally {
                    if (!responded) { handler.sendEmptyMessage(0); }
                    else { handler.sendEmptyMessage(1); }
                }
            }
        }.start();
    }

    public void ServerProblem()
    {
            AlertDialog.Builder b =new AlertDialog.Builder(myContext);
            b.setCancelable(false);
            b.setTitle("Server Error");
            b.setMessage("Ci dispiace ma il server ha problemi !");
            b.setPositiveButton("Riprova", new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ServerProblem();
                }
            });
            b.setNegativeButton("Chiudi", new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    myContext.finish();
                }
            });
            b.create().show();

    }

    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what != 1) { // code if not connected

                ServerProblem();

            } else { // code if connected

                //ServerProblem();
            }
        }
    };


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // ----------------------

        if(Salva.getDictPercorso().isEmpty()) {
            isNetworkAvailable(h, 14000);
        }


        // ----------------------


        Alltitle = new String[]{
                "Titolo1",
                "Titolo2",
                "Titolo3",
                "Titolo4",
                "Titolo5",
                "Titolo6",
                "Titolo7",
                "Titolo8",
                "Titolo9",
                "Titolo10",
                "titoloSanSevero"
        };

        //IconVectorMap[0] = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        //IconVectorMap[1] = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        // IconVectorMap[2] = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
        colorLine = new String[]{
                "#ff0000",//rosso
                "#0000ff",//blue
                "#9966CC",//lilla
                "#99CBFF",//azzurro
                "#7FFF00",//verde
                "#007FFF",//celeste chiaro
                "#E75480",//viola
                "#F4C430",//sabbia
                "#FFFF00",//giallo
                "#8F00FF"//viola scuro

        };
        IconVectorMap = new BitmapDescriptor[]{
           BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
           BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
           BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
           BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
           BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
           BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
           BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
           BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
           BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW),
           BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
        };

/*
        int jj = 0;
        //LatLng []p = new LatLng[4];

        List<LatLng> po = new ArrayList<LatLng>();
        //p[0]
        LatLng gg = new LatLng(40.94463909,-74.27719116);
        //p[1]
        LatLng fg = new LatLng(41.58976975,15.39253235);
        //p[2]
        LatLng kk = new LatLng(41.6868319,15.2925532);
        po.add(gg);


        //MarkerOptions []opts = new MarkerOptions[4];
        List<MarkerOptions> opts = new ArrayList<MarkerOptions>();

                MarkerOptions mm = new MarkerOptions();
                mm.position(gg);
                mm.icon(IconVectorMap[0]);
                mm.title("San zvir");
                mm.snippet("Teatro");
                opts.add(mm);
        MarkerOptions mm1 = new MarkerOptions();
        mm1.position(fg);
        mm1.icon(IconVectorMap[0]);
        mm1.title("San zvir");
        mm1.snippet("Teatro");
        opts.add(mm1);
        MarkerOptions mm2 = new MarkerOptions();
        mm2.position(kk);
        mm2.icon(IconVectorMap[0]);
        mm2.title("San zvir");
        mm2.snippet("Teatro");
        opts.add(mm2);
        AddAllMarkerToMap(opts);
*/



        final String []etichetta = {"Vicinanza" , "Consigli" , "Ricerca"};

        tabsviewPager = (ViewPager) getView().findViewById(R.id.tabspager);

        mTabsAdapter = new FragmentPageAdapter(myContext.getSupportFragmentManager());

        tabsviewPager.setAdapter(mTabsAdapter);

        tabs = (TabHost) getView().findViewById(R.id.tabhost);
        tabs.setup();
        TabHost.TabSpec spec = tabs.newTabSpec("Vicinanza");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Vicinanza");

        tabs.addTab(spec);
        TabHost.TabSpec spec2 = tabs.newTabSpec("Mappa");
        //spec = tabs.newTabSpec("tag2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Mappa");
        tabs.addTab(spec2);
        tabs.setCurrentTab(Salva.getTabSelected());

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                Salva.salvaTabSelected(tabs.getCurrentTab());
            }
        });

        tabsviewPager.setCurrentItem(Salva.getPositionTab());
        ((TextView)tabs.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText(Salva.getTitleTab());

        //This helps in providing swiping effect for v7 compat library
        tabsviewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                ((TextView)tabs.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText(etichetta[position]);
                Salva.salvaNumTab(position);
                //getSupportActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }

        map = fragment.getMap();

        //map.clear();

        // Initializing
        markerPoints = new ArrayList<LatLng>();

        // Enable MyLocation Button in the Map
        map.setMyLocationEnabled(true);

        //Salva.salvaMap(map);

        final int[] contatore = {0};

        //verifyConnection();

        final Double[] latitudine = {0.0};
        final Double[] longitudine = {0.0};

        final Boolean[] posizione = {true};

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                // TODO Auto-generated method stub
//map.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                latitudine[0] = arg0.getLatitude();
                longitudine[0] = arg0.getLongitude();
                Location pos = arg0;
                //pos.setLatitude(arg0.getLatitude());
                //pos.setLongitude(arg0.getLongitude());
                Salva.setPosizione(pos);
                if(posizione[0]) {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(latitudine[0], longitudine[0]), 13.0f));
                    posizione[0] = false;
                }
            }
        });
/*
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.setIcon(BitmapDescriptorFactory
                        .fromResource(R.drawable.drawingpin1_blue));
                return false;
            }
        });
*/

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                //open(1, "DRAGSTART", "natura", "tante belle cose", "02:03", "5km", "Buono Sconto");
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                if (isNetworkAvailable()) {
                    //open(1, "DRAG", "natura", "tante belle cose", "02:03", "5km", "Buono Sconto");
                }
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                //open(1, "DRAGEND", "natura", "tante belle cose", "02:03", "5km", "Buono Sconto");
            }
        });


        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabs.getCurrentTab() == 1) {
                    map.clear();
                    map.setMyLocationEnabled(true);

                    dictPercorso = Salva.getPercorsoReceive(Salva.getPositionTab());
                    dictOb = Salva.getObiettivoReceive(Salva.getPositionTab());
                    dictPre = Salva.getPremioReceive(Salva.getPositionTab());
                    dictOperators = Salva.getOperatoriReceive(Salva.getPositionTab());
                    if(dictPercorso != null) {
                        ConvertPercorsoToGestureMapDictionary(dictPercorso);
                    }
                }
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                Drawable fg = (Drawable) getResources().getDrawable(R.drawable.markerselezione);
                Drawable ng = resize2(fg);
                Bitmap bitM = drawableToBitmap(ng);
                //opt.setIcon(BitmapDescriptorFactory.fromBitmap(bitM));

                int id = dictMarkerIDPercorso.get(marker);
                String title = dictPercorso.get(id).title;
                String titleID = title + id;

                ChangeColor(titleID , BitmapDescriptorFactory.fromBitmap(bitM), "#0000ff", 10);

                return false;
            }
        });


        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                if (isNetworkAvailable())
                {
                    String title = marker.getTitle();

                    int id2 = dictMarkerIDPercorso.get(marker);
                    String title2 = dictPercorso.get(id2).title;
                    String titleID = title2 + id2;

                    String titleOb = marker.getSnippet();
                    String tipo = "";
                    String premio = "";
                    if(dictMap.get(titleID).type.size() != 0) {
                        Iterator<String> it = dictMap.get(titleID).type.iterator();
                        while (it.hasNext()) {
                            String type = (String) it.next();
                            tipo = tipo + type + " ";
                        }
                    }
                    int id = dictMarkerIDPercorso.get(marker);
                    String titolo = dictPercorso.get(id).title;
                    estrapolatrice(0 , id , tipo , titolo , dictPercorso , dictPre , dictOb);
                }
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                String newTitle = prevTitlePolyline;
                prevTitlePolyline = "";
                ChangeColor(newTitle , IconVectorMap[2] , "#ff0000" , 5);
                prevTitlePolyline = "";
            }
        });


    }

    @Override
    public void onTabReselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabSelected(ActionBar.Tab selectedtab, FragmentTransaction arg1) {
        // TODO Auto-generated method stub
        tabsviewPager.setCurrentItem(selectedtab.getPosition()); //update tab position on tap

        ((TextView)tabs.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText(selectedtab.getText() + " " + selectedtab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
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



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_swipe_view, null, false);

        return v;
    }

    // ----------------------------------

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
            gestureMap.dictMarkers = dictMarkers;
            gestureMap.title = route.title;
            gestureMap.polyline = x;
            gestureMap.type = route.type;
            gestureMap.description = route.description;
            gestureMap.distance = "2";//distance.toString();
            dictMap.put(route.title + route.id , gestureMap);

            dialog.dismiss();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //getActivity().setTitle("SS");
    }
}