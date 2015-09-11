package com.example.tonino.login;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;


public class BroadcastService extends Service {
    private String LOG_TAG = null;
    private ArrayList<String> mList;

    private String url = "";

    private Dictionary<Integer, Percorsi.Percorso> dictPercorso = new Hashtable<Integer, Percorsi.Percorso>();

    private Dictionary<String , Percorsi.Operators> dictOperators =
            new Hashtable<String , Percorsi.Operators>();

    private Dictionary<Integer , Percorsi.Objective> dictOb =
            new Hashtable<Integer , Percorsi.Objective>();
    private Dictionary<Integer , Percorsi.PremioS> dictPre =
            new Hashtable<Integer , Percorsi.PremioS>();

    private Dictionary<String , List<String>> dictTypeRoute = new Hashtable<String , List<String>>();
    private Dictionary<String , List<String>> dictTypePrize = new Hashtable<String , List<String>>();

    private List<String> tipiPercorsi = new ArrayList<String>();

    private List<String> tipiPremi = new ArrayList<String>();

    private List<String> titleP = new ArrayList<>();

    private List<String> titlePremi = new ArrayList<>();

    private int type = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        LOG_TAG = this.getClass().getSimpleName();
        mList = new ArrayList<String>();
        mList.add("Object 1");
        mList.add("Object 2");
        mList.add("Object 3");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            url = extras.getString("url");
            type = extras.getInt("tipo");
        }

        new Thread(new Runnable() {
            public void run() {

                Intent broadcastIntent = new Intent();
                /*broadcastIntent.setAction(broadCast.mBroadcastStringAction);
                broadcastIntent.putExtra("Data", "Broadcast Data");
                sendBroadcast(broadcastIntent);

                broadcastIntent.setAction(broadCast.mBroadcastIntegerAction);
                broadcastIntent.putExtra("Data", 10);
                sendBroadcast(broadcastIntent);*/

                String risultato = "";


                HttpGet request;

                int raggio = 30000;

                Data.setHttpclient(new DefaultHttpClient());
                final HttpResponse[] response = new HttpResponse[1];
                final ByteArrayOutputStream[] bb = new ByteArrayOutputStream[1];

                try {

                    request = new HttpGet(url);
                    if (request == null) {
                        //Dictionary<Integer, Percorsi.Percorso> dicts = new Hashtable<Integer, Percorsi.Percorso>();
                        Salva.setPercorsoReceive(null, type);

                        broadcastIntent.setAction("com.truiton.broadcast.arraylist" + type);
                        broadcastIntent.putExtra("Data", mList);
                        sendBroadcast(broadcastIntent);
                    } else {
                        request.setHeader("user-agent", "app");
                        request.setHeader("Accept", "application/json");

                        response[0] = Data.getHttpclient().execute(request);
                        StatusLine statusLine = response[0].getStatusLine();
                        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            try {
                                //RelativeLayout entriesContainer = (RelativeLayout) findViewById(R.id.contain);
                                response[0].getEntity().writeTo(out);
                                risultato = out.toString();
                            } catch (IOException e) {
                                //stampa e.toString() da qualche parte
                            }
                        }
                    }
                } catch (IOException e) {
//stampa e.toString() da qualche parte
                }

                if (type == 10) {
                    if(!risultato.equals("")) {
                        JSONObject mainObject = null;
                        try {
                            mainObject = new JSONObject(risultato);
                            JSONArray tags = mainObject.getJSONArray("tags");
                            List<String> list_tipi = new ArrayList<String>();
                            for (int i = 0; i < tags.length(); i++) {
                                list_tipi.add(((String) tags.get(i)));
                            }
                            Salva.setTipiPercorsoReceive(list_tipi , type);
                            broadcastIntent.setAction("com.truiton.broadcast.arraylist" + type);
                            broadcastIntent.putExtra("Data", mList);
                            sendBroadcast(broadcastIntent);
                        }catch (JSONException e) {

                        }
                    }
                } else {
                    if (!risultato.equals("")) {
                        //TextView tv = (TextView) findViewById(R.id.textjson);
                        List<Percorsi.PremioS> list_premio = new ArrayList<Percorsi.PremioS>();
                        Percorsi.PremioS prime = new Percorsi.PremioS();
                        Percorsi.Percorso p = new Percorsi.Percorso();
                        JSONObject mainObject = null;

                        //tv2.setText("");
                        int iD = 0;
                        try {
                            mainObject = new JSONObject(risultato);
                            //tv2.setText(tv2.getText() + " mainObject ");

                            JSONArray routes = mainObject.getJSONArray("routes");
                            JSONArray JObjs = new JSONArray();
                            JSONArray JPre = new JSONArray();

                            JSONArray Pre = mainObject.getJSONArray("prizes");
                            JSONArray Objs = mainObject.getJSONArray("objs");
                            JSONArray Operators = mainObject.getJSONArray("operators");
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

                                while (iterator.hasNext()) {
                                    String tipo = (String) iterator.next();
                                    if (!tipiPremi.contains(tipo)) {
                                        tipiPremi.add(tipo);
                                    }
                                }

                                dictOperators.put(operatori.id, operatori);
                            }

                            //List<Percorsi.PremiPercorso> lista_premi = new ArrayList<Percorsi.PremiPercorso>();
                            for (int i = 0; i < Pre.length(); i++) {

                                //List<Integer> mandatory_objs = new ArrayList<Integer>();
                                name = ((JSONObject) Pre.get(i)).getString("name");
                                id_operator = ((JSONObject) Pre.get(i)).getString("id_operator");
                                id = ((JSONObject) Pre.get(i)).getString("id");
                                //position_longitude = ((JSONObject) Pre.get(i)).getDouble("position_longitude");
                                //position_latitude = ((JSONObject) Pre.get(i)).getDouble("position_latitude");
                                description = ((JSONObject) Pre.get(i)).getString("description");
                                visible = ((JSONObject) Pre.get(i)).getInt("visible");
                                //percentage = ((JSONObject) Pre.get(i)).getString("percentage");

                                prime.name = name;
                                prime.id_operator = id_operator;
                                prime.description = description;
                                prime.position_latitude = 0.0;//position_latitude;
                                prime.position_longitude = 0.0;//position_longitude;
                                prime.id = Integer.parseInt(id);
                                //prime.percentage = Integer.parseInt(percentage);

                                prime.visible = visible;
                                //prime.mandatory_ob = mandatory_objs;
                                dictPre.put(prime.id, prime);
                                prime = new Percorsi.PremioS();
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
                            //tv2.setText("\n");

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

                                while (iterator.hasNext()) {
                                    String tipo = (String) iterator.next();
                                    if (!tipiPercorsi.contains(tipo)) {
                                        tipiPercorsi.add(tipo);
                                    }

                                }

                                p.title = ((JSONObject) routes.get(i)).getString("name");

                                p.description = ((JSONObject) routes.get(i)).getString("description");

                                JSONArray array_obiettivi = ((JSONObject) routes.get(i)).getJSONArray("objs");
                                for (int j = 0; j < array_obiettivi.length(); j++) {
                                    int num = (int) array_obiettivi.get(j);
                                    p.obiettivi.add(num);
                                }
                                dictPercorso.put(p.id, p);
                                p = new Percorsi.Percorso();
                            }

                            Salva.setPercorsoReceive(dictPercorso, type);
                            Salva.setPremioReceive(dictPre, type);
                            Salva.setObiettivoReceive(dictOb, type);
                            Salva.setOperatoriReceive(dictOperators, type);
                            Salva.setTipiPremiReceive(tipiPremi, type);
                            Salva.setTipiPercorsoReceive(tipiPercorsi, type);

                            broadcastIntent.setAction("com.truiton.broadcast.arraylist" + type);
                            broadcastIntent.putExtra("Data", mList);
                            sendBroadcast(broadcastIntent);
                        } catch (JSONException e) {
                            Dictionary<Integer, Percorsi.Percorso> dictPercorso = new Hashtable<Integer, Percorsi.Percorso>();

                            Dictionary<String, Percorsi.Operators> dictOperators =
                                    new Hashtable<String, Percorsi.Operators>();

                            Dictionary<Integer, Percorsi.Objective> dictOb =
                                    new Hashtable<Integer, Percorsi.Objective>();
                            Dictionary<Integer, Percorsi.PremioS> dictPre =
                                    new Hashtable<Integer, Percorsi.PremioS>();

                            List<String> tipiPercorsi = new ArrayList<String>();

                            List<String> tipiPremi = new ArrayList<String>();

                            Salva.setPercorsoReceive(dictPercorso, type);
                            Salva.setPremioReceive(dictPre, type);
                            Salva.setObiettivoReceive(dictOb, type);
                            Salva.setOperatoriReceive(dictOperators, type);
                            Salva.setTipiPremiReceive(tipiPremi, type);
                            Salva.setTipiPercorsoReceive(tipiPercorsi, type);

                            broadcastIntent.setAction("com.truiton.broadcast.arraylist" + type);
                            broadcastIntent.putExtra("Data", mList);
                            sendBroadcast(broadcastIntent);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
        return START_REDELIVER_INTENT;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // Wont be called as service is not bound

        return null;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}