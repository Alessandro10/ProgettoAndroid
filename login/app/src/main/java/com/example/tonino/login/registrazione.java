package com.example.tonino.login;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class registrazione extends ActionBarActivity {


    List<String> listapreferiti = new ArrayList<String>();
    Boolean us1 = false, passw_sec = false;
    Button btn = null;
    String default_tipo;
    Boolean isOperator = true;

    private boolean [][]bool = new boolean[3][];
    private boolean []settato = new boolean[3];
    private Dictionary<Integer, Dictionary<Integer , Integer>> superDict = new Hashtable<Integer , Dictionary<Integer , Integer>>();

    private final Dictionary<Integer, Integer> dictPremio = new Hashtable<Integer , Integer>();

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


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
                        for (Enumeration e = superDict.get(typeDictionary).keys(); e.hasMoreElements(); ) {
                            int key = (int) e.nextElement();
                            int tipo = (int) superDict.get(typeDictionary).get(key);
                            if (tipo == -1) {
                                tipo = 2;
                                superDict.get(typeDictionary).put(key, tipo);
                            } else if (tipo == -2) {
                                tipo = 1;
                                superDict.get(typeDictionary).put(key, tipo);
                            }
                            Salva.setBoolRegistrazione(bool[typeDictionary]);
                            Salva.setDizionarioRegistrazione(superDict);

                            //SalvaSituazione();
                        }
                        int j = 0;
                        listapreferiti.clear();
                        for (j = 0; j < Arraytipi.length; j++) {
                            if (bool[typeDictionary][j]) {
                                listapreferiti.add(Arraytipi[j]);
                            }
                        }
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
        levelDialog = builder.create();
        levelDialog.show();
    }

    private boolean inizializzaDict(Integer typeDictionary , final String[] items)
    {
        int i = 0;
        int dim = items.length;
        bool[typeDictionary] = new boolean[dim+1];
        for(i=0;i<dim;i++)
        {
            superDict.get(typeDictionary).put(i , 2);
            bool[typeDictionary][i] = false;
        }
        return true;
    }

    private IntentFilter mIntentFilter;
    public static final String mBroadcastStringAction = "com.truiton.broadcast.string";
    public static final String mBroadcastIntegerAction = "com.truiton.broadcast.integer";
    public static final String mBroadcastArrayListAction = "com.truiton.broadcast.arraylist3";

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*mTextView.setText(mTextView.getText()
                    + "Broadcast From Service: \n");*/
            if (intent.getAction().equals(mBroadcastStringAction)) {
                /*mTextView.setText(mTextView.getText()
                        + intent.getStringExtra("Data") + "\n\n");*/
            } else if (intent.getAction().equals(mBroadcastIntegerAction)) {
                /*mTextView.setText(mTextView.getText().toString()
                        + intent.getIntExtra("Data", 0) + "\n\n");*/
            } else if (intent.getAction().equals(mBroadcastArrayListAction)) {
                /*mTextView.setText(mTextView.getText()
                        + intent.getStringArrayListExtra("Data").toString()
                        + "\n\n");*/

                int i = 0;
                List<String> tipi = Salva.getTipiPercorsoReceive(3);
                Arraytipi = new String[tipi.size()];
                Iterator typelist = tipi.iterator();
                while(typelist.hasNext())
                {
                    Arraytipi[i] = (String)typelist.next();
                    i++;
                }
                superDict.put(1 , dictPremio);
                inizializzaDict(1 , Arraytipi);
                Salva.setArraytipiRegistrazione(Arraytipi);


                Button btN = (Button) findViewById(R.id.select2);

                btN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Multiple("Preferenze", Arraytipi , 1);
                    }
                });

                Intent stopIntent = new Intent(myContext ,
                        BroadcastService.class);
                stopService(stopIntent);
            }
        }
    };

    private String []Arraytipi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);
        mIntentFilter.addAction(mBroadcastIntegerAction);
        mIntentFilter.addAction(mBroadcastArrayListAction);

        if(savedInstanceState != null)
        {
            Arraytipi = Salva.getArrayTipiRegistrazione();
            superDict.put(1 , dictPremio);
            inizializzaDict(1 , Arraytipi);
            bool[1] = Salva.getBoolRegistrazione();
            superDict = Salva.getDizionarioRegistrazione();
            Button btN = (Button) findViewById(R.id.select2);

            btN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Multiple("Preferenze", Arraytipi , 1);
                }
            });
        }

        else {
            String url = Data.getUrl() + "/routes";

            Intent intent2 = new Intent(myContext, BroadcastService.class);
            intent2.putExtra("url", url);
            intent2.putExtra("tipo", 3);
            startService(intent2);
        }


        TextView nome_azienda = (TextView) findViewById(R.id.nome_azienda_text);
        EditText nome_azienda_text = (EditText) findViewById(R.id.nome_azienda);
        TextView tipo = (TextView) findViewById(R.id.tipo);
        Button tipo_text = (Button) findViewById(R.id.select);
        Button tipo_text2 = (Button) findViewById(R.id.selectb);
        TextView citta = (TextView) findViewById(R.id.citta_text);
        EditText citta_text = (EditText) findViewById(R.id.citta);
        TextView via = (TextView) findViewById(R.id.via_text);
        EditText via_text = (EditText) findViewById(R.id.via);
        TextView numero_civico = (TextView) findViewById(R.id.numero_civico_text);
        EditText numero_civico_text = (EditText) findViewById(R.id.numero_civico);
        Intent intent = getIntent();
        String valore = intent.getExtras().getString("tipo");

        if(valore.equals("utente"))
        {
            isOperator = false;

            nome_azienda.setVisibility(View.GONE);
            nome_azienda_text.setVisibility(View.GONE);
            tipo.setVisibility(View.GONE);
            tipo_text.setVisibility(View.GONE);
            tipo_text2.setVisibility(View.GONE);
            citta_text.setVisibility(View.GONE);
            citta.setVisibility(View.GONE);
            via_text.setVisibility(View.GONE);
            via.setVisibility(View.GONE);
            numero_civico_text.setVisibility(View.GONE);
            numero_civico.setVisibility(View.GONE);
        }

        btn = (Button) findViewById(R.id.select);
        Button btn2 = (Button) findViewById(R.id.selectb);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                superSpinner();
            }

        });
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                superSpinner();
            }

        });

        Button tipo_azienda = (Button) findViewById(R.id.select);
        default_tipo = tipo_azienda.getText().toString();

        final Button invio = (Button) findViewById(R.id.invio);

        invio.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    invio.setBackgroundDrawable( getResources().getDrawable(R.drawable.bottone_selected) );
                }
                if(event.getAction() == MotionEvent.ACTION_UP ||event.getAction() == MotionEvent.ACTION_MOVE ) {
                    invio.setBackgroundDrawable( getResources().getDrawable(R.drawable.bottone));
                }

                return false;
            }
        });

        invio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                EditText psw1 = (EditText) findViewById(R.id.password);
                EditText psw2 = (EditText) findViewById(R.id.password2);
                String passw1 = psw1.getText().toString();
                String passw2 = psw2.getText().toString();
                TextView ps1_error = (TextView) findViewById(R.id.psw1_error);
                TextView ps2_error = (TextView) findViewById(R.id.psw2_error);
                Boolean security = false;
                if (passw1.equals(passw2)) {
                    //ciao
                    passw_sec = true;
                    ps1_error.setVisibility(View.INVISIBLE);
                    ps2_error.setVisibility(View.INVISIBLE);
                    security = true;
                    security = control_security_password(passw1, 8);
                    if(security)
                    {
                        passw_sec = true;
                    }
                    //security = control_security_password(passw1 , 6);
                }
                if (security == false || !passw1.equals(passw2)) {
                    passw_sec = false;
                    ps1_error.setVisibility(View.VISIBLE);
                    ps2_error.setVisibility(View.VISIBLE);
                }

                EditText usr = (EditText) findViewById(R.id.username);
                TextView usr_error = (TextView) findViewById(R.id.username_error);
                String user = usr.getText().toString();
                if (control_security_password(user, 6)) {
                    us1 = true;
                    usr_error.setVisibility(View.INVISIBLE);
                } else {
                    us1 = false;
                    usr_error.setVisibility(View.VISIBLE);
                }

                if(passw_sec && us1)
                {
                    invio.setText("inviato");

                    sendJson(user , passw1);
                }

                if (isOperator)
                {

                    EditText nome_azienda = (EditText) findViewById(R.id.nome_azienda);
                    TextView azienda_error = (TextView) findViewById(R.id.azienda_error);
                    String nomeaz = nome_azienda.getText().toString();
                    if (nomeaz.length() >= 3) {
                        azienda_error.setVisibility(View.INVISIBLE);
                    } else {
                        azienda_error.setVisibility(View.VISIBLE);
                    }

                    Button tipo_azienda = (Button) findViewById(R.id.select);
                    TextView tipo_error = (TextView) findViewById(R.id.tipo_error);
                    String tipo_scelto = tipo_azienda.getText().toString();
                    if (!tipo_scelto.equals(default_tipo)) {
                        tipo_error.setVisibility(View.INVISIBLE);
                    } else {
                        tipo_error.setVisibility(View.VISIBLE);
                    }

                    EditText citta = (EditText) findViewById(R.id.citta);
                    EditText via = (EditText) findViewById(R.id.via);
                    EditText numero_civico = (EditText) findViewById(R.id.numero_civico);

                    String citta_string = citta.getText().toString();
                    String via_string = via.getText().toString();
                    String numero_civico_string = numero_civico.getText().toString();

                    TextView citta_error = (TextView) findViewById(R.id.citta_error);
                    TextView via_error = (TextView) findViewById(R.id.via_error);
                    TextView numero_civico_error = (TextView) findViewById(R.id.numero_civico_error);

                    if(citta_string.length() >= 3)
                    {
                        citta_error.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        citta_error.setVisibility(View.VISIBLE);
                    }

                    if(via_string.length() >= 2)
                    {
                        via_error.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        via_error.setVisibility(View.VISIBLE);
                    }
                    if(control_security_password(numero_civico_string , 3))
                    {
                        numero_civico_error.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        numero_civico_error.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
    }

    private Context myContext = this;

    protected void sendJson(final String username, final String pwd) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    Data.setHttpclient(new DefaultHttpClient());
                    HttpPost post = new HttpPost(Data.getUrl() + "/signup");
                    post.setHeader("user-agent", "app");
                    post.setHeader("Accept", "application/json");
                    json.put("login", username);
                    json.put("psw", pwd);
                    json.put("is_operator" , false);


                    JSONArray tags = new JSONArray();
                    int j = 0;
                    Iterator iterator = listapreferiti.iterator();
                    while(iterator.hasNext())
                    {
                        tags.put((String)iterator.next());
                    }
                    if(listapreferiti.size() >= 1) {
                        json.put("tags", tags);
                    }


                    StringEntity se = new StringEntity( json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    final StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();

                    String title = "" , msg = "";

                    if(statusLine.getStatusCode() == 200)
                    {
                        title = "REGISTRAZIONE OK";
                        msg = "la registrazione è avvenuta con successo";
                    }
                    else
                    {
                        title = "PROBLEMA NELLA REGISTRAZIONE";
                        msg = "non è stato possibile eseguire la registrazione riprova più tardi";
                    }

                    AlertDialog.Builder b =new AlertDialog.Builder(myContext);
                    b.setCancelable(false);
                    b.setTitle(title);
                    b.setMessage(msg);
                    b.setNeutralButton("OK", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(statusLine.getStatusCode() == 200)
                            {
                                finish();
                            }
                        }
                    });
                    b.create().show();

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

    public boolean control_security_password (String psw , Integer min) {
        //String regex = "[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        String regex;
        if(min == 8) {
            regex = "((?=.*[0-9])(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[:punct:]).{8,})";
        }
        else if (min == 6)
        {
            regex = "^[a-z0-9_-]{3,16}$";
        }
        else
        {
            regex = "^[0-9]{1,3}([a-z]?)$";
        }
        Boolean normal_char = false, number = false, special_char = false, size = false;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(psw);

        if (matcher.matches()) {
            Log.v("security", "TRUE");
            return true;
        } else {
            Log.v("security", "FALSE");
            return false;
        }
    }


    //Dialog dialog = new Dialog(this);

    private void superSpinner() {

        AlertDialog levelDialog = null;

// Strings to Show In Dialog with Radio Buttons
        final CharSequence[] items = {" Natura "," Sport "," Intrattenimento "," Relax "};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select The Difficulty Level");
        final AlertDialog finalLevelDialog = levelDialog;
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                btn.setText("" + items[item]);
                dialog.dismiss();

            }
        });
        levelDialog = builder.create();
        levelDialog.show();

    }


    public  void displayAlert()
    {
        new AlertDialog.Builder(this).setMessage("Hi , I am Alert Dialog")
                .setTitle("My Alert")
                .setCancelable(true)
                .setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton){
                                finish();
                            }
                        })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registrazione, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
