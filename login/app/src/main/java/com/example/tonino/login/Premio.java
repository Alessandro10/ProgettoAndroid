package com.example.tonino.login;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.NumberPicker;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;


public class Premio extends Fragment {

    boolean click = false;
    List<String> listapreferitiPercorsi = new ArrayList<String>();
    List<String> listapreferitiPremi = new ArrayList<String>();
    public ProgressDialog pd;
    public static final String mBroadcastStringAction = "com.truiton.broadcast.string";
    public static final String mBroadcastIntegerAction = "com.truiton.broadcast.integer";
    public static final String mBroadcastArrayListAction = "com.truiton.broadcast.arraylist3";
    private TextView mTextView;
    private IntentFilter mIntentFilter;

    Boolean position_caputere = false;

    Location positionL;

    private Dictionary<Integer, Percorsi.Percorso> dictPercorso = new Hashtable
            <Integer, Percorsi.Percorso>();

    private Dictionary<String , Percorsi.Operators> dictOperators =
            new Hashtable<String , Percorsi.Operators>();

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

    private String []Alltitle;

    private String titleSelected="";


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
    private static Double latitude, longitude;
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
    private final List<String> Ob_list = new ArrayList<String>();
    private final List<String> Pr_list = new ArrayList<String>();
    private String[] Pr_string = null;



    //--à...............................

    private String Memorizza = "Memorizza" + Salva.getNomeUtente();
    private String PremiVinti = "PremiVinti" + Salva.getNomeUtente();;
    private String PercorsiSvolti = "PercorsiSvolti" + Salva.getNomeUtente();

    //.......................................



    //--------------------------------------------

    public boolean removeAll(String nameKey)
    {
        int i = 0;
        SharedPreferences sp = myContext.getSharedPreferences(Memorizza , 0/*MODE_PRIVATE*/);
        SharedPreferences.Editor mEdit1 = sp.edit();
        for(i = 0 ; i < 10 ; i++)
        {
            mEdit1.remove(nameKey + "_" + i);
        }
        mEdit1.remove(nameKey + "_MaxSize");
        mEdit1.remove(nameKey + "_size");
        return mEdit1.commit();
    }


    public boolean removeArrayList(String nameKey)
    {
        int i = 0;
        SharedPreferences sp = myContext.getSharedPreferences(Memorizza , 0/*MODE_PRIVATE*/);
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
        SharedPreferences sp = myContext.getSharedPreferences(Memorizza , 0/*MODE_PRIVATE*/);
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
        SharedPreferences mSharedPreference1 = myContext.getSharedPreferences(Memorizza, 0/*MODE_PRIVATE*/);
        List<Integer> list = new ArrayList<Integer>();
        int size = mSharedPreference1.getInt(nomeKey + "_size", 0);
        for(int i=0;i<size;i++)
        {
            list.add(mSharedPreference1.getInt(nomeKey + "_" + i, -1)); //list is arraylist
        }
        return list;
    }

    //----------------------------------------------

    public void savePremioVinto(int iDPremio)
    {
        List<Integer> list = loadArrayList(PremiVinti);
        if(cercaIntListPos(list , iDPremio) == -1) {
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
                return position;
            }
        }
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













    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(mBroadcastArrayListAction)) {
                dictPercorso = Salva.getPercorsoReceive(3);
                dictOb = Salva.getObiettivoReceive(3);
                dictPre = Salva.getPremioReceive(3);
                dictOperators = Salva.getOperatoriReceive(3);
                tipiPercorsi = Salva.getTipiPercorsoReceive(3);
                tipiPremi = Salva.getTipiPremiReceive(3);
                DizionarioPremiVinti(dictPercorso);
                Intent stopIntent = new Intent(myContext ,
                        BroadcastService.class);
                getActivity().stopService(stopIntent);
                pd.dismiss();
            }

        }

    };

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





    public void open(int utilita , String tipo , String titolo , String descrizione , String tempo , String distanza , String premio){
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



    // --------------------------------------

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

    String []array_premi = null;
    String []array_obiettivi = null;

    public Dictionary<String , List<Integer>> DizionarioPremiVinti(Dictionary<Integer, Percorsi.Percorso> dictPercorso)
    {

        if(Salva.getCancellaPremio() != -1)
        {
            removePremioVinto(Salva.getCancellaPremio());
            Salva.setCancellaPremio(-1);
        }

        List<Integer> list = new ArrayList<Integer>();

        Dictionary<Integer, Percorsi.PremioS> dictPremiVinti = getPremiVinti();

        Dictionary<String , List<Integer>> dic = new Hashtable<String , List<Integer>>();

        ExpandableListView expListView = null;

        expListView = (ExpandableListView) getView().findViewById(R.id.expand);

        if(expListView != null) {
            Toast.makeText(myContext , "dimPremi " + dictPremiVinti.size() , Toast.LENGTH_SHORT).show();
            for (Enumeration e = dictPremiVinti.keys(); e.hasMoreElements(); ) {
                int idP = (int)e.nextElement();
                Toast.makeText(myContext , "idP " + idP , Toast.LENGTH_SHORT).show();
                if(!cercaIntList(list , idP)) {
                    Toast.makeText(myContext , "entrato " + idP , Toast.LENGTH_SHORT).show();
                    list.add(idP);
                }
            }
            dic.put("Premi Vinti" , list);
            b.creaExpandableListView(0, 1, expListView, myContext , dic, dictPercorso, dictPre, dictOb, dictOperators);

        }
        return dic;
    }

    public void reimpostaTutto()
    {
        EditText tv = (EditText) getView().findViewById(R.id.cerca);
        tv.setText(Salva.getTestoRicercaPO(0));
        testoricerca = Salva.getTestoRicercaPO(0);

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


    private Dictionary<String, List<Integer>> filtraPerPremio(Dictionary<String, List<Integer>> dictD , String text) {
        Dictionary<String, List<Integer>> dic = new Hashtable<String, List<Integer>>();

        List<String> lista = new ArrayList<String>();
        List<Integer> list_num = new ArrayList<Integer>();
        for (Enumeration e = dictD.keys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            Iterator it = dictD.get(key).iterator();
            while (it.hasNext()) {
                int cod_p = (int) it.next();
                String name_p = dictPre.get(cod_p).name;
                if (((name_p.toLowerCase().contains(text.toLowerCase())) || text.equals(""))) {
                    list_num.add(cod_p);
                }
            }
            if(text.equals(""))
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


    private boolean cercainList(List<String> lista , String key) {
        Iterator iterator = lista.iterator();
        while(iterator.hasNext())
        {
            String name = (String) iterator.next();
            if(name.equals(key))
            {
                return true;
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



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);





        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastArrayListAction);

        position_caputere = true;
//String url = "http://daniele.cortesi2.web.cs.unibo.it/wsgi/routes/44.5461898/11.1927492/16000";
        Data.setHttpclient(new DefaultHttpClient());
        String url = Data.getUrl() + "/routes";

        Intent intent = new Intent(myContext, BroadcastService.class);
        intent.putExtra("url", url);
        intent.putExtra("tipo", 3);
        myContext.startService(intent);
        pd = new ProgressDialog(myContext);
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.show();

        final Button cerca = (Button) getView().findViewById(R.id.ricerca);

        cerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List list = loadArrayList(PremiVinti);
                Iterator it = list.iterator();
                String stringa = "";
                while(it.hasNext())
                {
                    stringa = stringa +" " + it.next();
                }


                AlertDialog.Builder b2 =new AlertDialog.Builder(myContext);
                b2.setCancelable(false);
                b2.setTitle("Premio Vinto");
                b2.setMessage(stringa);
                b2.setPositiveButton("Riprova", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                b2.setNegativeButton("Esci", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                b2.create().show();

                list = loadArrayList(PercorsiSvolti);
                it = list.iterator();
                stringa = "";
                while(it.hasNext())
                {
                    stringa = stringa +" " + it.next();
                }


                AlertDialog.Builder b1 =new AlertDialog.Builder(myContext);
                b1.setCancelable(false);
                b1.setTitle("Premio Vinto");
                b1.setMessage(stringa);
                b1.setPositiveButton("Riprova", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                b1.setNegativeButton("Esci", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                b1.create().show();

                verifyConnection();
                ExpandableListView expListView;

                EditText tv = (EditText) getView().findViewById(R.id.cerca);
                String text = tv.getText().toString();

                expListView = (ExpandableListView) getView().findViewById(R.id.expand);

                // preparing list data
                Dictionary<String, Dictionary<String, String>> dicts2 = new Hashtable<String, Dictionary<String, String>>();
                Dictionary<String, String> di2 = new Hashtable<String, String>();

                Dictionary<String, List<Integer>> dic = new Hashtable<String, List<Integer>>();

                dic = DizionarioPremiVinti(dictPercorso);

                dic = filtraPerPremio(dic, text);

                b.creaExpandableListView(0, 1, expListView, myContext, dic, dictPercorso, dictPre, dictOb, dictOperators);

                SalvaSituazione();
            }
        });

        cerca.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cerca.setBackgroundDrawable(getResources().getDrawable(R.drawable.crea_azienda_selected));

                }
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_MOVE) {
                    cerca.setBackgroundDrawable(getResources().getDrawable(R.drawable.crea_azienda));
                }

                return false;
            }
        });

        if(Salva.getSettaggio(0))
        {

            reimpostaTutto();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SalvaSituazione();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        SalvaSituazione();
    }

    public void SalvaSituazione()
    {
        TextView tv = (TextView) getView().findViewById(R.id.cerca);
        String text = tv.getText().toString();
        Salva.salvataggio(0 , 0 , text);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(pd != null) {
            pd.dismiss();
        }
        SalvaSituazione();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //mIntentFilter = new IntentFilter();
        View v = inflater.inflate(R.layout.fragment_premio, null, false);
        return v;
    }

}