package com.example.tonino.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.listeners.OnPublishListener;

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
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class Expandable {

    private Context mContext;

    public void creaExpandableListView(int color , int utilita , ExpandableListView expListView, final Context myContext,
                                       Dictionary<String , List<Integer>> dicts , Dictionary<Integer , Percorsi.Percorso> dictPercorso ,
                                       Dictionary<Integer , Percorsi.PremioS> dictPre  ,  Dictionary<Integer , Percorsi.Objective> dictOb ,
                                       Dictionary<String , Percorsi.Operators> dictOperators)
    {
        expListView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                mContext = myContext;

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        prepareListData(color , utilita, dicts, expListView, myContext , dictPercorso , dictPre , dictOb , dictOperators);
    }

    private void estrapolatrice(int utilita , int iDp ,String tipo , String titolo , Dictionary<Integer , Percorsi.Percorso> dictPercorso ,
                                Dictionary<Integer , Percorsi.PremioS> dictPre  ,  Dictionary<Integer , Percorsi.Objective> dictOb)
    {
        Dictionary<Integer , String> dictPremi_con_ob = new Hashtable<Integer , String>();
        String name_premi = "";
        String name_obiettivi = "";
        String name_obiettivi_ob = "";
        String description = "";
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
            open(utilita, iDp , tipo, titolo, description, name_premi, name_obiettivi, dictPremi_con_ob , dictPercorso);
            //break;
            //}
        }
    }


    public void open(int utilita , final int iD ,String tipo , String titolo , String descrizione
            , String premio , String name_obiettivo ,Dictionary<Integer , String> dictPremi_con_ob ,
                     final Dictionary<Integer , Percorsi.Percorso> dictPercorso){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.dialog_view, null);

        LayoutInflater li2 = LayoutInflater.from(mContext);
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
                            Intent intent = new Intent(mContext , PercorsoDaSvolgere.class);
                            mContext.startActivity(intent);

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



    protected void sendJson(final int idPrize , final String namePremio) {

        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = Salva.getHttpClient();
                Toast.makeText(mContext , "Salvato client" , Toast.LENGTH_SHORT).show();
                Salva.setHttClient(client);
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    Data.setHttpclient(new DefaultHttpClient());
                    HttpPost post = new HttpPost(Data.getUrl() + "/prize/collect");
                    post.setHeader("user-agent", "app");
                    post.setHeader("Accept", "application/json");
                    json.put("id_prize", idPrize);
                    StringEntity se = new StringEntity( json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();


                    String title = "" , msg = "";

                    if(statusLine.getStatusCode() == 200)
                    {

                        String risposta = response.toString();

                        JSONObject mainObject = null;

                        int iD = 0;
                        String validation_code = "";
                        String result = EntityUtils.toString(response.getEntity());
                        try {
                            mainObject = new JSONObject(result);
                            validation_code = mainObject.getString("validation_code");
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        openPremio(namePremio , validation_code ,Salva.getAccountFB());
                    }
                    else
                    {
                        String messaggio = "";
                        if(statusCode == 403)
                        {
                            messaggio = "Premio non autorizzato o non più esistente";
                            Salva.setCancellaPremio(idPrize);
                        }
                        else
                        {
                            messaggio = "Server non raggiugibile";
                        }

                        AlertDialog.Builder b =new AlertDialog.Builder(mContext);
                        b.setCancelable(false);
                        b.setTitle("ERRORE");
                        b.setMessage(messaggio);
                        b.setNeutralButton("OK", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        b.create().show();
                    }

                    /*Checking response */
                    if(response!=null){
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    }

                } catch(Exception e) {
                    e.printStackTrace();

                }

                Looper.loop(); //Loop in the message queue
            }
        };
        t.start();
    }




    public void openPremio(String nome , String codice , boolean AccountFb)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.premio_view, null);

        WebView myWebView = (WebView) promptsView.findViewById(R.id.webview);
        myWebView.loadUrl("http://chart.apis.google.com/chart?cht=qr&chs=200x200&chl=" + codice +"&chld=H|0");

        LayoutInflater li2 = LayoutInflater.from(mContext);
        View promptsView2 = li2.inflate(R.layout.newdialog, null);

        TextView  name = (TextView) promptsView2.findViewById(R.id.titolo);

        name.setText(nome);

        alertDialogBuilder.setCustomTitle(promptsView2);

        alertDialogBuilder.setView(promptsView);

        if(!AccountFb) {
            alertDialogBuilder.setNeutralButton("OK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {


                        }
                    });
        }
        else
        {
            alertDialogBuilder.setPositiveButton("Condividi",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            SimpleFacebook mSimpleFacebook = Salva.getmSimpleFacebook();




                            final Feed feed = new Feed.Builder()
                            .setMessage("Ha vinto il premio!!!Usa anche tu la magnifica app DiscountWalk")
                                    .setName("DiscountWalk")
                                    .setCaption("Code less, do the same.")
                                    .setDescription("Ha vinto un grande premio!!! Usa anche tu la magnifica app DiscountWalk!")
                                    .setLink("http://daniele.cortesi2.web.cs.unibo.it/wsgi/routes")
                                    .setPicture("https://raw.githubusercontent.com/Alessandro10/ProgettoAndroid/master/Desktop/applogo200X200.png")
                                    .build();

                            SimpleFacebook.getInstance().publish(feed, true, new OnPublishListener() {

                                @Override
                                public void onException(Throwable throwable) {
                                    //mResult.setText(throwable.getMessage());
                                    Log.i("risultato", "Exception");
                                }

                                @Override
                                public void onFail(String reason) {
                                    //mResult.setText(reason);
                                    Log.i("risultato", "Fail");
                                }

                                @Override
                                public void onComplete(String response) {
                                    //mResult.setText(response);
                                    Log.i("risultato", "Complete");
                                }
                            });

                        }
                    });
            alertDialogBuilder.setNegativeButton("Indietro",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {


                        }
                    });
        }

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void ese(int color , final int utilita , List<String> titoli/*String[] titoli*/ ,
                     Dictionary<String , List<String>> dict , ExpandableListView expListView , final Context myContext ,
                     final Dictionary<Integer , Percorsi.Percorso> dictPercorso , final Dictionary<Integer , Percorsi.PremioS> dictPre ,
                     final Dictionary<Integer , Percorsi.Objective> dictOb , final Dictionary<String , Percorsi.Operators> dictOperators){
        ExpandableListAdapter listAdapter;
        final List<String> listDataHeader;
        final HashMap<String, List<String>> listDataChild;
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        List<String>[] primo = new ArrayList[titoli.size() + 1];
        int i = 0;
        Iterator it = titoli.iterator();
        while(it.hasNext())
        {
            String tit = (String) it.next();
            if(dict.get(tit) != null) {
                primo[i] = new ArrayList<String>();
                listDataHeader.add(tit);
                List<String> lista_Percorsi = dict.get(tit);
                primo[i] = lista_Percorsi;
                i++;
            }
        }
    }

    private void prepareListData(int color , final int utilita ,
                                 Dictionary<String , List<Integer>> dict , ExpandableListView expListView , final Context myContext ,
                                 final Dictionary<Integer , Percorsi.Percorso> dictPercorso , final Dictionary<Integer , Percorsi.PremioS> dictPre ,
                                 final Dictionary<Integer , Percorsi.Objective> dictOb , final Dictionary<String , Percorsi.Operators> dictOperators) {
        ExpandableListAdapter listAdapter;
        final List<String> listDataHeader;
        final HashMap<String, List<String>> listDataChild;
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        List<String>[] primo = new ArrayList[dict.size() + 1];
        int i = 0;

        if(utilita != 1) {
            for (Enumeration e = dict.keys(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();
                if (dict.get(key) != null) {
                    primo[i] = new ArrayList<String>();
                    listDataHeader.add(key);
                    List<Integer> lista_Percorsi = dict.get(key);
                    Iterator iterator = lista_Percorsi.iterator();
                    while (iterator.hasNext()) {
                        int iD = (int) iterator.next();
                        primo[i].add(dictPercorso.get(iD).title);
                    }
                    i++;
                }
            }
        }
        else {
            for (Enumeration e = dict.keys(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();
                if (dict.get(key) != null) {
                    primo[i] = new ArrayList<String>();
                    listDataHeader.add(key);
                    List<Integer> lista_Premi = dict.get(key);
                    Iterator iterator = lista_Premi.iterator();
                    while (iterator.hasNext()) {
                        int iD = (int) iterator.next();
                        primo[i].add(dictPre.get(iD).name);
                    }
                    i++;
                }
            }
        }
        int j = 0;
        for (j = 0 ; j < i ; j++) {
                listDataChild.put(listDataHeader.get(j), primo[j]);
        }
        listAdapter = new ExpandableListAdapter(myContext , dict , listDataHeader, listDataChild , color);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                /*Toast.makeText(myContext,
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                /*Toast.makeText(myContext,
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();*/

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                /*Toast.makeText(
                        myContext,
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition) + " : " + id, Toast.LENGTH_SHORT)
                        .show();*/
                if (utilita == 0 || utilita == 2) {
                    estrapolatrice(utilita, (int)id , listDataHeader.get(groupPosition), listDataChild.get(
                            listDataHeader.get(groupPosition)).get(
                            childPosition), dictPercorso, dictPre, dictOb);
                } else {
                    sendJson((int)id ,  listDataChild.get(
                            listDataHeader.get(groupPosition)).get(
                            childPosition));
                }
                return false;
            }
        });

        Salva.OnRotation(true);
    }

}
