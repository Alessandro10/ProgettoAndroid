package com.example.tonino.login;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class Consigliati extends Fragment {

    boolean click = false;
    List<String> listaSuperPreferiti = new ArrayList<String>();
    List<String> listapreferitiPercorsi = new ArrayList<String>();
    List<String> listapreferitiPremi = new ArrayList<String>();

    public ProgressDialog pd;

    private Location positionL;

    public static final String mBroadcastArrayListAction = "com.truiton.broadcast.arraylist1";
    private TextView mTextView;
    private IntentFilter mIntentFilter;

    private Boolean position_caputere = false;

    private ExpandableListView expListView;

    private Dictionary<String , Percorsi.Operators> dictOperators =
            new Hashtable<String , Percorsi.Operators>();

    private Dictionary<Integer, Percorsi.Percorso> dictPercorso = new Hashtable
            <Integer, Percorsi.Percorso>();

    private Dictionary<Integer , Percorsi.Objective> dictOb =
            new Hashtable<Integer , Percorsi.Objective>();
    private Dictionary<Integer , Percorsi.PremioS> dictPre =
            new Hashtable<Integer , Percorsi.PremioS>();

    private Dictionary<String , List<Integer>> dictTypeRoute = new Hashtable<String , List<Integer>>();
    private Dictionary<String , List<Integer>> dictTypePrize = new Hashtable<String , List<Integer>>();

    private List<String> tipiPercorsi = new ArrayList<String>();

    private List<String> tipiPremi = new ArrayList<String>();

    private List<String> titleP = new ArrayList<>();

    private List<String> titlePremi = new ArrayList<>();

    private String coloreprecedente = "#ff00ff";
    private Dictionary<String , Dictionary<String,String>> dicts = new Hashtable<String , Dictionary<String,String>>();
    private Dictionary<String , String> di = new Hashtable<String , String>();
    private String[] titoli = new String[3];
    private Expandable b = new Expandable();
    private String ordina = " Obiettivo ";
    private int numbertab=1;
    private String testoricerca = "";
    private Boolean ricercasettata = false;
    private int km = 15;
    private static View view;
    private FragmentActivity myContext;

    private int mHour = 23;
    private int mMinute = 59;
    private Dictionary<Integer, Dictionary<Integer , Integer>> superDict = new Hashtable<Integer , Dictionary<Integer , Integer>>();
    private final Dictionary<Integer, Integer> dictPremio = new Hashtable<Integer , Integer>();
    private final Dictionary<Integer, Integer> dictObiettivo = new Hashtable<Integer , Integer>();
    private boolean [][]bool = new boolean[3][];
    private boolean []settato = new boolean[3];
    TabHost tabs;
    final ArrayList seletedItems=new ArrayList();
    final ArrayList seletedItemsOld=new ArrayList();
    TextView tvDistanceDuration;
    private final String[] array = {"uno" , "due" , "tre" , "quattro" , "uno" , "due" , "tre" , "quattro" , "uno" , "due" , "tre" , "quattro" , "uno" , "due" , "tre" , "quattro"};
    private String[] Ob_string = null;
    private String[] Pr_string = null;

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

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    public void ricercaAvanzata()
    {
        final TextView testo = (TextView) getView().findViewById(R.id.avanzata);
        final LinearLayout linea2 = (LinearLayout) getView().findViewById(R.id.linea2);

        testo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String text = testo.getText().toString();
                if(text.equals("ricerca avanzata >>"))
                {
                    ricercasettata = true;
                    testo.setText("<< ricerca standard");
                    linea2.setVisibility(View.VISIBLE);
                }
                else {
                    ricercasettata = false;
                    testo.setText("ricerca avanzata >>");
                    linea2.setVisibility(View.GONE);
                }
                SalvaSituazione();
            }
        });
    }

    public void impostaDurata() {
        TextView select3 = (TextView) getView().findViewById(R.id.select3);
        String tempo;
        if (mHour >= 10 && mMinute >= 10) {
            tempo = "Durata\n\t" + mHour + ":" + mMinute;
        } else if (mHour < 10 && mMinute >= 10) {
            tempo = "Durata\n\t0" + mHour + ":" + mMinute;
        } else if (mHour >= 10 && mMinute < 10) {
            tempo = "Durata\n\t" + mHour + ":0" + mMinute;
        } else {
            tempo = "Durata\n\t0" + mHour + ":0" + mMinute;
        }
        select3.setText(tempo);
    }

    private void Single() {

        AlertDialog levelDialog = null;

// Strings to Show In Dialog with Radio Buttons
        final String[] items = {" Obiettivo "," Premio "};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        builder.setTitle("Seleziona tipo di Ordinamento");
        final AlertDialog finalLevelDialog = levelDialog;
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                ordina = items[item];
                SalvaSituazione();
                Button btn = (Button) getView().findViewById(R.id.ordina);
                btn.setText("Ordina per : " + ordina);
                cambiaVisualizzazione(ordina);
                dialog.dismiss();
            }
        });
        levelDialog = builder.create();
        levelDialog.show();
    }

    private Dictionary<String , List<Integer>> cambiaVisualizzazione(String ordina)
    {
        ExpandableListView expListView;
        expListView = (ExpandableListView) getView().findViewById(R.id.expand);
        List<String> list = new ArrayList<String>();
        Dictionary<String , List<Integer>> dic = new Hashtable<String , List<Integer>>();
        if(ordina.equals(" Premio ")) {
            dic = creaPreEx(dictTypePrize);
        }
        else {
            dic = creaPreEx(dictTypeRoute);
        }
        b.creaExpandableListView(1, 0, expListView, myContext , dic, dictPercorso, dictPre, dictOb, dictOperators);
        return dic;
    }

    private boolean inizializzaDict(Integer typeDictionary , final String[] items)
    {
        int i = 0;
        int dim = items.length;
        bool[typeDictionary] = new boolean[dim+1];
        for(i=0;i<=dim;i++)
        {
            superDict.get(typeDictionary).put(i , 2);
            bool[typeDictionary][i] = false;
        }
        return true;
    }

    //2 non selezionato
    //1 selezionato
    //-1 da selezionato a non selezionato
    //-2 da non selezionato a selezionato



    private void Multiple(String title , final String[] items , final int typeDictionary) {
        AlertDialog levelDialog = null;
        // arraylist to keep the selected items

        settato[typeDictionary] = true;
        String[] stringa = new String[10];
        int ii;

        int i = -1;
        for (Enumeration e = superDict.get(typeDictionary).keys(); e.hasMoreElements(); ) {
            int key = (int) e.nextElement();
            i++;
            Log.v("tipo ", " " + superDict.get(typeDictionary).get(key));
            if (superDict.get(typeDictionary).get(key) == 1) {
                bool[typeDictionary][i] = true;
            }
            if (superDict.get(typeDictionary).get(key) == 2) {
                bool[typeDictionary][i] = false;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        builder.setTitle(title);
        builder.setMultiChoiceItems(items, bool[typeDictionary],
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            Log.v("isChecked" , indexSelected + " " + isChecked);
                            int c = superDict.get(typeDictionary).get(indexSelected);
                            superDict.get(typeDictionary).put(indexSelected , -c);
                        } // else if (seletedItems.contains(indexSelected)) {
                        else
                        {
                            int c = superDict.get(typeDictionary).get(indexSelected);
                            superDict.get(typeDictionary).put(indexSelected , -c);
                            //seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for (Enumeration e = superDict.get(typeDictionary).keys(); e.hasMoreElements();) {
                            int key = (int) e.nextElement();
                            int tipo = (int) superDict.get(typeDictionary).get(key);
                            if (tipo == -1)
                            {
                                tipo = 2;
                                superDict.get(typeDictionary).put(key, tipo);
                            }
                            else if(tipo == -2)
                            {
                                tipo = 1;
                                superDict.get(typeDictionary).put(key, tipo);
                            }
                            SalvaSituazione();
                        }
                        int j = 0;
                        listapreferitiPercorsi = new ArrayList<String>();
                        Iterator iterator = listaSuperPreferiti.iterator();
                        while(iterator.hasNext())
                        {
                            String name = (String)iterator.next();
                            if (bool[2][j]) {
                                listapreferitiPercorsi.add(name);
                            }
                            j++;
                        }
                        listapreferitiPremi = new ArrayList<String>();
                        j = 0;
                        Iterator iterator2 = tipiPremi.iterator();
                        while(iterator2.hasNext())
                        {
                            String name = (String)iterator2.next();
                            if (bool[1][j]) {
                                listapreferitiPremi.add(name);
                            }
                            j++;
                        }
                        Salva.setListPreferiti(listapreferitiPercorsi , listapreferitiPremi , 1);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for (Enumeration e = superDict.get(typeDictionary).keys(); e.hasMoreElements(); ) {
                            int key = (int) e.nextElement();
                            int tipo = (int) superDict.get(typeDictionary).get(key);
                            if (tipo < 0) {
                                tipo = -tipo;
                                superDict.get(typeDictionary).put(key, tipo);
                            }
                        }
                    }
                });
        levelDialog = builder.create();//AlertDialog dialog; create like this outside onClick
        levelDialog.show();
    }

    boolean savedI = false;

    Bundle Stato;

    public void salvaStato()
    {
        Stato = new Bundle();
        Stato.putInt("tab" , tabs.getCurrentTab());
        Stato.putBooleanArray("settato" , settato);
        int i;
        for(i=1;i<=2;i++) {
            Stato.putBooleanArray("bool"+i , bool[i]);
        }
        Stato.putInt("ore" , mHour);
        Stato.putInt("minuti" , mMinute);
    }

    public void NumberDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);

        LayoutInflater li = LayoutInflater.from(myContext);
        View promptsView = li.inflate(R.layout.numberdialog, null);
        final NumberPicker np = (NumberPicker) promptsView.findViewById(R.id.numberPicker);

        np.setMinValue(0);
        np.setMaxValue(15);
        np.setValue(15);

        alertDialogBuilder.setTitle("Imposta distanza");
        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder.setPositiveButton("Imposta",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        int distance = np.getValue();
                        TextView number = (TextView) getView().findViewById(R.id.distanza);
                        number.setText("Distanza\n\t" + distance + " km");
                        km = distance;
                        SalvaSituazione();
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

    public Dictionary<String , List<Integer>> DizionarioNelleVicinanze(Dictionary<Integer, Percorsi.Percorso> dictPercorso)
    {
        Dictionary<String , List<Integer>> dic = new Hashtable<String , List<Integer>>();

        ExpandableListView expListView = null;

        expListView = (ExpandableListView) getView().findViewById(R.id.expand);

        if(expListView != null) {
            int i;
            int indice = 0;
            titlePremi = tipiPremi;
            for (Enumeration e = dictPercorso.keys(); e.hasMoreElements(); ) {
                int key = (int) e.nextElement();
                Percorsi.Percorso percorso = dictPercorso.get(key);
                int idp = percorso.id;
                //titleP[indice] = namep;
                Iterator iterator = percorso.type.iterator();
                while (iterator.hasNext()) {
                    String tipo = (String) iterator.next();
                    if(ListContain(listaSuperPreferiti , tipo)) {
                        List<Integer> nomi;
                        if (dictTypeRoute.get(tipo) == null) {
                            nomi = new ArrayList<Integer>();
                        } else {
                            nomi = dictTypeRoute.get(tipo);
                        }
                        nomi.add(idp);
                        dictTypeRoute.put(tipo, nomi);
                    }
                }

                Iterator iterator2 = percorso.premi.iterator();
                while (iterator2.hasNext()) {
                    Percorsi.PremiPercorso premiper = (Percorsi.PremiPercorso) iterator2.next();
                    String id = dictPre.get(premiper.id).id_operator;
                    List<String> type = dictOperators.get(id).type;
                    Iterator iterator3 = type.iterator();
                    while (iterator3.hasNext()) {
                        String tipo = (String) iterator3.next();
                        List<Integer> nomi;
                        if (dictTypePrize.get(tipo) == null) {
                            nomi = new ArrayList<Integer>();
                        } else {
                            nomi = dictTypePrize.get(tipo);
                        }
                        nomi.add(idp);
                        dictTypePrize.put(tipo, nomi);
                    }
                }
            }

            dic = cambiaVisualizzazione(ordina);

            if(!Salva.getSettaggio(1))
            {
                settato[1] = false;
                settato[2] = false;

                final Button premio = (Button) getView().findViewById(R.id.premio);
                final Button obiettivo = (Button) getView().findViewById(R.id.obiettivo);

                //Ob_string = new String[tipiPercorsi.size()];
                Ob_string = new String[listaSuperPreferiti.size()];
                Pr_string = new String[tipiPremi.size()];

                inizializzaDict(2, Ob_string);
                inizializzaDict(1, Pr_string);

                //Iterator iterator1 = tipiPercorsi.iterator();
                Iterator iterator1 = listaSuperPreferiti.iterator();
                int ii = 0;
                while (iterator1.hasNext()) {
                    Ob_string[ii] = (String) iterator1.next();
                    ii++;
                }

                Iterator iterator11 = tipiPremi.iterator();
                ii = 0;
                while (iterator11.hasNext()) {
                    Pr_string[ii] = (String) iterator11.next();
                    ii++;
                }

                Salva.setPr_string(Pr_string, 1);

                Salva.setOb_string(Ob_string, 1);

                premio.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if(Pr_string.length > 0) {
                            Multiple("Premio", Pr_string, 1);
                        }
                    }
                });

                obiettivo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Ob_string.length > 0) {
                            Multiple("Obiettivo", Ob_string, 2);
                        }
                    }
                });
            }
        }
        return dic;
    }

    private boolean ListContain(List lista , String stringa)
    {
        if(!lista.isEmpty()) {
            Iterator it = lista.iterator();
            while (it.hasNext()) {
                String element = (String) it.next();
                if (element.equals(stringa)) {
                    return true;
                }
            }
        }
        return false;
    }


    private Dictionary<String , List<Integer>> creaPreEx(Dictionary<String , List<Integer>> dictType)
    {
        List<Integer> list_int = new ArrayList<Integer>();
        Dictionary<String , List<Integer>> dic = new Hashtable<String , List<Integer>>();
        for (Enumeration e = dictType.keys(); e.hasMoreElements(); ) {
            String tiponame = (String) e.nextElement();
            if(ordina.equals(" Premio ") || listaSuperPreferiti.isEmpty() || cercainList(listaSuperPreferiti , tiponame)) {
                List<Integer> lista = dictType.get(tiponame);
                Iterator iterator2 = lista.iterator();
                while (iterator2.hasNext())
                {
                    int id = (int)iterator2.next();
                    if(!cercaIntList(list_int , id)) {
                        list_int.add(id);
                    }
                }
                dic.put(tiponame, list_int);
                list_int = new ArrayList<Integer>();
            }
        }
        return dic;
    }

    public void reimpostaTutto()
    {
        listapreferitiPercorsi = Salva.getListPreferitiPercorsi(1);
        listapreferitiPremi = Salva.getListPreferitiPremi(1);
        EditText tv = (EditText) getView().findViewById(R.id.cerca);
        tv.setText(Salva.getTestoricerca(1));
        testoricerca = Salva.getTestoricerca(1);
        //superDict = Salva.getDictionary(0);
        ordina = Salva.getOrdine(1);
        mHour = Salva.getOre(1);
        mMinute = Salva.getMinuti(1);
        km = Salva.getDistanza(1);
        Button btn = (Button) getView().findViewById(R.id.select3);
        String text = "Durata\n\t";
        String ora , minuto;
        if(mHour < 10)
        {
            ora = "0" + mHour;
        }
        else
        {
            ora = "" + mHour;
        }
        if(mMinute < 10)
        {
            minuto = "0" + mMinute;
        }
        else
        {
            minuto = "" + mMinute;
        }
        text = text + ora + ":" + minuto;
        btn.setText(text);
        Button btn2 = (Button) getView().findViewById(R.id.distanza);
        btn2.setText("Distanza\n\t" + km + "km");
        Button btn3 = (Button) getView().findViewById(R.id.ordina);
        btn3.setText("Ordina per : " + ordina);
        Pr_string = Salva.getPr_string(1);
        Ob_string = Salva.getOb_string(1);

        inizializzaDict(2, Ob_string);
        inizializzaDict(1, Pr_string);

        superDict = Salva.getDictionary(1);

        final Button premio = (Button) getView().findViewById(R.id.premio);
        final Button obiettivo = (Button) getView().findViewById(R.id.obiettivo);

        premio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(Pr_string.length > 0) {
                    Multiple("Premio", Pr_string, 1);
                }
            }
        });

        obiettivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Ob_string.length > 0) {
                    Multiple("Obiettivo", Ob_string, 2);
                }
            }
        });

        if(Salva.getRicerca(1)) {
            TextView t = (TextView) getView().findViewById(R.id.avanzata);
            t.performClick();
        }
        /*if(!testoricerca.equals(""))
        {
            Button cerca = (Button) getView().findViewById(R.id.ricerca);
            cerca.performClick();
        }*/

    }

    private boolean cercainVector(String[] vector , String key) {
        int i = 0;

        for (i = 0 ; i<vector.length ; i++)
        {
            if(key.equals(vector[i]))
            {
                return true;
            }
        }

        return false;
    }

    private boolean cercainList(List<String> lista , String key) {
        if(lista != null) {
            Iterator iterator = lista.iterator();
            while (iterator.hasNext()) {
                String name = (String) iterator.next();
                if (name.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean cercaIntList(List<Integer> list , int key)
    {

        Iterator iterator = list.iterator();
        while(iterator.hasNext())
        {
            int num = (int) iterator.next();
            if(num == key)
            {
                return true;
            }
        }
        return false;
    }

    private Dictionary<String, List<Integer>> filtraPerObiettivo(Dictionary<String, List<Integer>> dictD , String text , int typeFiltro) {
        Dictionary<String, List<Integer>> dic = new Hashtable<String, List<Integer>>();

        List<String> lista = new ArrayList<String>();
        List<Integer> list_num = new ArrayList<Integer>();
        for (Enumeration e = dictD.keys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            Iterator it = dictD.get(key).iterator();
            while (it.hasNext()) {
                int cod_p = (int) it.next();
                String name_p = dictPercorso.get(cod_p).title;
                if (!cercaIntList(list_num , cod_p) && (cercainList(listapreferitiPercorsi, key) || cercainList(listapreferitiPremi, key) || typeFiltro == 1) &&
                        ((name_p.toLowerCase().contains(text.toLowerCase())) || text.equals(""))) {
                    list_num.add(cod_p);
                }
            }
            if(text.equals("") && !list_num.isEmpty())
            {
                dic.put(key , list_num);
                list_num = new ArrayList<Integer>();
                //list_num.clear();
            }
        }
        if(!text.equals(""))
        {
            dic.put(text , list_num);
        }


        return dic;
        //b.creaExpandableListView(0 , 1, expListView, myContext, titolo, dicts2 , dictPercorso , dictPre , dictOb , dictOperators);
    }


    @Override
    public void onResume() {
        super.onResume();
        if(pd != null) {
            pd.dismiss();
        }
        getActivity().registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(pd != null) {
            pd.dismiss();
        }
        getActivity().unregisterReceiver(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(mBroadcastArrayListAction)) {

                dictPercorso = Salva.getPercorsoReceive(1);
                dictOb = Salva.getObiettivoReceive(1);
                dictPre = Salva.getPremioReceive(1);
                dictOperators = Salva.getOperatoriReceive(1);
                tipiPercorsi = Salva.getTipiPercorsoReceive(1);
                tipiPremi = Salva.getTipiPremiReceive(1);
                DizionarioNelleVicinanze(dictPercorso);
                Intent stopIntent = new Intent(myContext ,
                        BroadcastService.class);
                getActivity().stopService(stopIntent);
                pd.dismiss();
            }

        }

    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastArrayListAction);

        listaSuperPreferiti = Salva.getListaDeiTagsPreferiti();

        final Button cerca = (Button) getView().findViewById(R.id.ricerca);

        final Button button_obbiettivo = (Button) getView().findViewById(R.id.ordina);

        button_obbiettivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Single();
            }
        });

        button_obbiettivo.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    button_obbiettivo.setBackgroundDrawable( getResources().getDrawable(R.drawable.redbutton_selected) );
                }
                if(event.getAction() == MotionEvent.ACTION_UP ||event.getAction() == MotionEvent.ACTION_MOVE ) {
                    button_obbiettivo.setBackgroundDrawable( getResources().getDrawable(R.drawable.redbutton));
                }

                return false;
            }
        });

        cerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyConnection();
                ExpandableListView expListView;

                EditText tv = (EditText) getView().findViewById(R.id.cerca);
                String text = tv.getText().toString();

                expListView = (ExpandableListView) getView().findViewById(R.id.expand);

                // preparing list data
                Dictionary<String, Dictionary<String, String>> dicts2 = new Hashtable<String, Dictionary<String, String>>();
                Dictionary<String, String> di2 = new Hashtable<String, String>();

                int typeFiltro = 1;
                if(Salva.getRicerca(1) || ricercasettata)
                {
                    typeFiltro = 0;
                }

                Dictionary<String , List<Integer>> dic = new Hashtable<String , List<Integer>>();

                dic = DizionarioNelleVicinanze(dictPercorso);

                dic = filtraPerObiettivo(dic, text, typeFiltro);

                if(text.equals(""))
                {
                    b.creaExpandableListView(1 , 0 , expListView , myContext , dic , dictPercorso , dictPre , dictOb , dictOperators);
                }
                else if(dicts2.isEmpty())
                {
                    b.creaExpandableListView(1 , 0 , expListView , myContext , dic , dictPercorso , dictPre , dictOb , dictOperators);
                }
                else
                {
                    b.creaExpandableListView(1 , 1, expListView, myContext, dic , dictPercorso , dictPre , dictOb , dictOperators);
                }

                SalvaSituazione();
            }
        });


        cerca.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    cerca.setBackgroundDrawable( getResources().getDrawable(R.drawable.redbutton_selected) );
                    //generaColore(coloreprecedente);
                }
                if(event.getAction() == MotionEvent.ACTION_UP ||event.getAction() == MotionEvent.ACTION_MOVE ) {
                    cerca.setBackgroundDrawable( getResources().getDrawable(R.drawable.redbutton));
                }

                return false;
            }
        });

        superDict.put(1 , dictPremio);
        superDict.put(2 , dictObiettivo);

        final Button premio = (Button) getView().findViewById(R.id.premio);
        final Button obiettivo = (Button) getView().findViewById(R.id.obiettivo);

        premio.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    premio.setBackgroundDrawable( getResources().getDrawable(R.drawable.redbutton_selected) );
                }
                if(event.getAction() == MotionEvent.ACTION_UP ||event.getAction() == MotionEvent.ACTION_MOVE ) {
                    premio.setBackgroundDrawable( getResources().getDrawable(R.drawable.redbutton));
                }
                return false;
            }
        });

        obiettivo.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    obiettivo.setBackgroundDrawable( getResources().getDrawable(R.drawable.redbutton_selected) );
                }
                if(event.getAction() == MotionEvent.ACTION_UP ||event.getAction() == MotionEvent.ACTION_MOVE ) {
                    obiettivo.setBackgroundDrawable( getResources().getDrawable(R.drawable.redbutton));
                }
                return false;
            }
        });

        settato[1] = false;
        settato[2] = false;

        premio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Multiple("Premio" , Pr_string , 1);
            }
        });

        obiettivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Multiple("Obiettivo" , Ob_string , 2);
            }
        });

        final TextView select3 = (TextView) getView().findViewById(R.id.select3);

        select3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    select3.setBackgroundDrawable( getResources().getDrawable(R.drawable.redbutton_selected) );
                }
                if(event.getAction() == MotionEvent.ACTION_UP ||event.getAction() == MotionEvent.ACTION_MOVE ) {
                    select3.setBackgroundDrawable( getResources().getDrawable(R.drawable.redbutton));
                }
                return false;
            }
        });

        select3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                TimePickerDialog tpd = new TimePickerDialog(myContext,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                mHour = hourOfDay;
                                mMinute = minute;
                                impostaDurata();

                            }
                        }, mHour, mMinute, true);
                tpd.show();
            }
        });

        final TextView distanza = (TextView)getView().findViewById(R.id.distanza);

        distanza.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                NumberDialog();
                SalvaSituazione();

            }
        });

        distanza.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    distanza.setBackgroundDrawable( getResources().getDrawable(R.drawable.redbutton_selected) );
                }
                if(event.getAction() == MotionEvent.ACTION_UP ||event.getAction() == MotionEvent.ACTION_MOVE ) {
                    distanza.setBackgroundDrawable( getResources().getDrawable(R.drawable.redbutton));
                }

                return false;
            }
        });

        //ricerca avanzata

        ricercaAvanzata();

        if(Salva.getSettaggio(1))
        {
            reimpostaTutto();
        }

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

        positionL = location;

        int raggio = 16000;
        Data.setHttpclient(new DefaultHttpClient());
        String url = Data.getUrl() + "/routes/" +
                + positionL.getLatitude()
                + "/" + positionL.getLongitude() + "/" + raggio;

        if(!Salva.getAggiorna(1)) {
            Salva.setAggiorna(true , 1);
            Intent intent = new Intent(myContext, BroadcastService.class);
            intent.putExtra("url", url);
            intent.putExtra("tipo", 1);
            myContext.startService(intent);
            pd = new ProgressDialog(myContext);
            pd.setMessage("loading");
            pd.setCancelable(false);
            pd.show();
        }
        else
        {
            dictPercorso = Salva.getPercorsoReceive(1);
            dictOb = Salva.getObiettivoReceive(1);
            dictPre = Salva.getPremioReceive(1);
            dictOperators = Salva.getOperatoriReceive(1);
            tipiPercorsi = Salva.getTipiPercorsoReceive(1);
            tipiPremi = Salva.getTipiPremiReceive(1);
            DizionarioNelleVicinanze(dictPercorso);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SalvaSituazione();
    }


    public void SalvaSituazione()
    {
        TextView tv = (TextView) getView().findViewById(R.id.cerca);
        String text = tv.getText().toString();
        Salva.salvataggio(0 , 1 , superDict , mHour , mMinute , km , ricercasettata , text , ordina);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SalvaSituazione();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = null;

        v = inflater.inflate(R.layout.activity_consigliati, null, false);
        return v;
    }

}