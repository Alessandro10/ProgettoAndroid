package com.example.tonino.login;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.login.DefaultAudience;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.utils.Logger;
import com.sromku.simple.fb.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private boolean FbBool = false;
    private String username,password;
    private Button ok;
    private EditText editTextUsername,editTextPassword;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    private FragmentActivity myContext;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
    }


    private void okPuoiEntrare()
    {
        String user = username;
        Intent myIntent = new Intent(MainActivity.this, BodyofApp.class);
        myIntent.putExtra("username", user);
        myIntent.putExtra("notifica", 0);
        Salva.setNomeUtente(user);
        startActivity(myIntent);
        this.finish();
    }

    protected void sendJson(final String username, final String pwd) {
        myContext = this;
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                Salva.setHttClient(client);
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    Data.setHttpclient(new DefaultHttpClient());
                    HttpPost post = new HttpPost(Data.getUrl() + "/login");
                    post.setHeader("user-agent", "app");
                    post.setHeader("Accept", "application/json");

                    json.put("login", username);
                    json.put("psw", pwd);
                    StringEntity se = new StringEntity( json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();

                    Log.i("ok" , statusLine.toString());

                    String title = "" , msg = "";

                    if(statusLine.getStatusCode() == 200)
                    {

                        String risposta = response.toString();

                        JSONObject mainObject = null;
                        JSONArray tipiConsigliati = null;

                        List<String> ListTipi = new ArrayList<String>();

                        int iD = 0;
                        String result = EntityUtils.toString(response.getEntity());
                        try {
                            mainObject = new JSONObject(result);
                            tipiConsigliati = mainObject.getJSONArray("tags");
                            for (int i = 0; i < tipiConsigliati.length(); i++) {
                                ListTipi.add(((String) tipiConsigliati.get(i)));
                            }
                            //ListTipi.add("cultura");
                            //ListTipi.add("fifi");
                            Salva.setListaDeiTagsPreferiti(ListTipi);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        Salva.setUsername(username);
                        okPuoiEntrare();
                    }
                    else
                    {
                        AlertDialog.Builder b = new AlertDialog.Builder(myContext);
                        b.setCancelable(false);
                        b.setTitle("ERRORE");
                        b.setMessage("username e/o password sbagliate");
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




    protected void sendJsonWithFB(final String idFb , final boolean new_account_fb
            , final List<String> tipi , final HttpClient ClientVecchio) {
        myContext = this;
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client;
                if(new_account_fb) {
                    Log.i("loginfb" , "NewClient");
                    client = new DefaultHttpClient();
                }
                else
                {
                    Log.i("loginfb" , "ClientVecchio");
                    //client = ClientVecchio;
                    client = Salva.getHttpClient();
                }
                Salva.setHttClient(client);
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    Data.setHttpclient(new DefaultHttpClient());

                    if(new_account_fb) {
                        HttpPost post;
                        Log.i("loginfb" , "loginfb");
                        post = new HttpPost(Data.getUrl() + "/loginfb");
                        json.put("fb_id" , idFb);
                        post.setHeader("user-agent", "app");
                        post.setHeader("Accept", "application/json");
                        StringEntity se = new StringEntity( json.toString());
                        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                        post.setEntity(se);
                        response = client.execute(post);
                    }
                    else
                    {
                        HttpPut post;
                        Log.i("loginfb" , "loginfbElse");
                        post = new HttpPut(Data.getUrl() + "/user/edit");
                        Log.i("loginfb" , "loginfbElse1");
                        JSONArray tags_array = new JSONArray();
                        Iterator<String> iterator = tipi.iterator();
                        while(iterator.hasNext())
                        {
                            String tipoDaMettere = iterator.next();
                            tags_array.put(tipoDaMettere);
                        }
                        json.put("tags_to_add", tags_array);
                        post.setHeader("user-agent", "app");
                        post.setHeader("Accept", "application/json");
                        StringEntity se = new StringEntity( json.toString());
                        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                        post.setEntity(se);

                        response = client.execute(post);

                    }

                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();

                    Log.i("ok" , statusLine.toString());

                    String title = "" , msg = "";
                    final List<String> tipi_da_mettere = new ArrayList<String>();
                    if(statusLine.getStatusCode() == 200)
                    {
                        if(new_account_fb) {
                            String risposta = response.toString();
                            JSONObject mainObject = null;
                            JSONArray tipiConsigliati = null;
                            List<String> ListTipi = new ArrayList<String>();
                            int iD = 0;
                            String result = EntityUtils.toString(response.getEntity());
                            try {
                                mainObject = new JSONObject(result);
                                boolean new_user = (boolean) mainObject.getBoolean("new_user");
                                //boolean new_user = new_account_fb;

                                Log.i("loginfb", " " + new_user);
                                if (new_user) {
                                    Log.i("loginfb", "new_user");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
                                    builder.setTitle("Preferenze");
                                    List<String> list_items = listapreferitiPercorsi;
                                    Log.i("loginfb", "size list " + list_items.size());
                                    final String[] items = new String[list_items.size()];
                                    int i = 0;
                                    boolean[] bool = new boolean[list_items.size()];
                                    Iterator<String> iterator = list_items.iterator();
                                    while (iterator.hasNext()) {
                                        String tipo = iterator.next();
                                        items[i] = tipo;
                                        bool[i] = false;
                                        i++;
                                    }
                                    Log.i("loginfb", "fatto ");
                                    builder.setMultiChoiceItems(items, bool,
                                            new DialogInterface.OnMultiChoiceClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int indexSelected,
                                                                    boolean isChecked) {
                                                    Log.i("loginfb" , "-> " + items[indexSelected]);
                                                    if (isChecked) {
                                                        Log.i("loginfb" , "messo " + items[indexSelected]);
                                                        tipi_da_mettere.add(items[indexSelected]);
                                                    } else {
                                                        Log.i("loginfb" , "tolto " + items[indexSelected]);
                                                        tipi_da_mettere.remove(items[indexSelected]);
                                                    }
                                                }
                                            })
                                            // Set the action buttons
                                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //tipi_da_mettere.add(items[id]);
                                                    Log.i("loginfb", "tipi " + tipi_da_mettere.size());
                                                    Salva.setUsername(username);
                                                    sendJsonWithFB(idFb, false, tipi_da_mettere, ClientVecchio);

                                                }
                                            }).show();
                                }
                                else
                                {
                                    Salva.setUsername(username);
                                    sendJsonWithFB(idFb, false, tipi, ClientVecchio);
                                }
                                if (new_account_fb) {
                                    Log.i("loginfb", "sendJsonWithFB");
                                    //sendJsonWithFB(idFb, false , tipi_da_mettere, ClientVecchio);
                                } else {
                                    //okPuoiEntrare();
                                }
                            } catch (Exception e) {
                                Log.i("loginfb", "errore");
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            Log.i("loginfb" , "TipiNome");
                            Iterator<String> iterator = tipi.iterator();
                            while(iterator.hasNext())
                            {
                                String tiponome = iterator.next();
                                Log.i("loginfb" , "tipoNome = " + tiponome);
                            }
                            Salva.setListaDeiTagsPreferiti(tipi);
                            okPuoiEntrare();
                        }
                    }
                    else
                    {
                        AlertDialog.Builder b = new AlertDialog.Builder(myContext);
                        b.setCancelable(false);
                        b.setTitle("ERRORE");
                        b.setMessage("username e/o password sbagliate " + statusLine.getStatusCode());
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


    List<String> listapreferiti = new ArrayList<String>();

    protected void registrazione(final String username, final String pwd) {
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



    private SimpleFacebook mSimpleFacebook;
    private Button buttonFacebook;




    /**
     * Login example.
     */
    private void setLogin() {


        // Login listener
        final OnLoginListener onLoginListener = new OnLoginListener() {

            @Override
            public void onFail(String reason) {
                buttonFacebook.setText("Fail");
                //mTextStatus.setText(reason);
                //Log.w(TAG, "Failed to login");
            }

            @Override
            public void onException(Throwable throwable) {
                buttonFacebook.setText("exception");
                //mTextStatus.setText("Exception: " + throwable.getMessage());
                //Log.e(TAG, "Bad thing happened", throwable);
            }

            @Override
            public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
                // change the state of the button or do whatever you want

                mSimpleFacebook.getProfile(new OnProfileListener() {
                   @Override
                   public void onComplete(Profile profile) {
                       super.onComplete(profile);
                       final String id = profile.getId();
                       Profile.Properties properties = new Profile.Properties.Builder()
                               .add(Profile.Properties.EMAIL)
                               .add(Profile.Properties.LINK)
                               .add(Profile.Properties.WEBSITE)
                               .add(Profile.Properties.VERIFIED)
                               .build();

                       mSimpleFacebook.getProfile(id , properties , new OnProfileListener() {
                           @Override
                           public void onComplete(Profile profile) {
                               String firstName = profile.getFirstName();
                               String email = profile.getEmail();

                               String name = profile.getName();
                               String lastName = profile.getLastName();
                               String sesso = profile.getGender();
                               String website = profile.getWebsite();
                               String link = profile.getLink();
                               //buttonFacebook.setText(" email " + email + " id " + id );

                               //sendJson(email , id , 1);
                               Salva.setSimpleFacebook(mSimpleFacebook);
                               Salva.setAccountFB(true);

                               List<String> listadefalut = new ArrayList<String>();

                               HttpClient client = new DefaultHttpClient();

                               sendJsonWithFB(id , true , listadefalut , client);
                               final OnLogoutListener onLogoutListener = new OnLogoutListener() {

                                   @Override
                                   public void onLogout() {

                                   }
                               };
                               mSimpleFacebook.logout(onLogoutListener);
                           }
                       });

                        /*buttonFacebook.setText("id " + id + " firstName " + firstName
                                + " email " + email + " lastName " + lastName + " sesso "
                                + sesso + " nome " + name + " link " + link +
                        " website " + website);*/
                   }

               });
                //mTextStatus.setText("Logged in");
                //loggedInUIState();
                //buttonFacebook.setText(mSimpleFacebook.getAccessToken().toString());
               /* buttonFacebook.setText("login " + mSimpleFacebook.getAccessToken().getApplicationId()
                        + " - " + mSimpleFacebook.getAccessToken().getExpires() + " - "
                        + mSimpleFacebook.getAccessToken().getUserId());*/

            }

            @Override
            public void onCancel() {
                buttonFacebook.setText("cancel");
               // mTextStatus.setText("Canceled");
                //Log.w(TAG, "Canceled the login");
            }


        };

        buttonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //buttonFacebook.setText("ciao");
                if(FbBool) {
                    mSimpleFacebook.login(onLoginListener);
                }

            }
        });

    }

    private static final String APP_ID = "1612497275669488";
    private static final String APP_NAMESPACE = "discountwalk";
    public static final String mBroadcastArrayListAction = "com.truiton.broadcast.arraylist10";
    private IntentFilter mIntentFilter;
    private List<String> listapreferitiPercorsi = new ArrayList<String>();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

                listapreferitiPercorsi =  Salva.getTipiPercorsoReceive(10);
                Log.i("listapreferitiPercorsi" , " " + listapreferitiPercorsi.size());

                FbBool = true;

                Intent stopIntent = new Intent(myContext ,
                        BroadcastService.class);
                stopService(stopIntent);


        }

    };


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myContext = this;
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastArrayListAction);

//String url = "http://daniele.cortesi2.web.cs.unibo.it/wsgi/routes/44.5461898/11.1927492/16000";
        Data.setHttpclient(new DefaultHttpClient());
        String url = Data.getUrl() + "/tags";

        Intent intent = new Intent(this, BroadcastService.class);
        intent.putExtra("url", url);
        intent.putExtra("tipo", 10);
        startService(intent);



        //mSimpleFacebook = SimpleFacebook.getInstance(this);

        // test local language
        Utils.updateLanguage(getApplicationContext(), "en");
        Utils.printHashKey(getApplicationContext());

        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            int c = extras.getInt("notifica");

            if (c == 1) {
                stopService(new Intent(getBaseContext(), Service2.class));
            }
        }

        SimpleFacebook.initialize(this);

        // set log to true
        Logger.DEBUG_WITH_STACKTRACE = true;

        // initialize facebook configuration
        Permission[] permissions = new Permission[] {
                Permission.PUBLIC_PROFILE,
                Permission.EMAIL,
                Permission.USER_EVENTS,
                Permission.USER_ACTIONS_MUSIC,
                Permission.USER_FRIENDS,
                Permission.USER_GAMES_ACTIVITY,
                Permission.USER_BIRTHDAY,
                //Permission.USER_GROUPS,
                Permission.PUBLISH_ACTION };

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(APP_ID)
                .setNamespace(APP_NAMESPACE)
                .setPermissions(permissions)
                .setDefaultAudience(DefaultAudience.FRIENDS)
                .setAskForAllPermissionsAtOnce(false)
                .build();

        SimpleFacebook.setConfiguration(configuration);

        mSimpleFacebook = SimpleFacebook.getInstance(this);

        buttonFacebook = (Button) findViewById(R.id.facebook);
        setLogin();



        ok = (Button)findViewById(R.id.login);
        ok.setOnClickListener((View.OnClickListener) this);
        editTextUsername = (EditText)findViewById(R.id.username);
        editTextPassword = (EditText)findViewById(R.id.password);
        saveLoginCheckBox = (CheckBox)findViewById(R.id.checkBox);
        loginPreferences = getSharedPreferences("loginPrefs", 0/*MODE_PRIVATE*/);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            editTextUsername.setText(loginPreferences.getString("username", ""));
            editTextPassword.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
            ok.performClick();
            }
            else
            {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
            }
            /*loginPrefsEditor.clear();
            loginPrefsEditor.commit();*///toglie ricorda password


        final Button crea_account_utente = (Button) findViewById(R.id.crea_account_utente);

        crea_account_utente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this, registrazione.class);
                myIntent.putExtra("tipo" , "utente");
                startActivity(myIntent);
            }

        });

        final Button crea_account_azienda = (Button) findViewById(R.id.crea_account_azienda);
        crea_account_azienda.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this, registrazione.class);
                myIntent.putExtra("tipo" , "azienda");
                startActivity(myIntent);
            }

        });


        final Button login = (Button) findViewById(R.id.login);


        login.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    login.setBackgroundDrawable( getResources().getDrawable(R.drawable.bottone_selected) );
                }
                if(event.getAction() == MotionEvent.ACTION_UP ||event.getAction() == MotionEvent.ACTION_MOVE ) {
                    login.setBackgroundDrawable( getResources().getDrawable(R.drawable.bottone));
                }

                return false;
            }
        });

        crea_account_utente.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    crea_account_utente.setBackgroundDrawable( getResources().getDrawable(R.drawable.crea_utente_selected) );
                }
                if(event.getAction() == MotionEvent.ACTION_UP ||event.getAction() == MotionEvent.ACTION_MOVE ) {
                    crea_account_utente.setBackgroundDrawable( getResources().getDrawable(R.drawable.crea_utente));
                }

                return false;
            }
        });

        crea_account_azienda.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    crea_account_azienda.setBackgroundDrawable(getResources().getDrawable(R.drawable.crea_azienda_selected));
                }
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_MOVE) {
                    crea_account_azienda.setBackgroundDrawable(getResources().getDrawable(R.drawable.crea_azienda));
                }

                return false;
            }
        });


        final EditText usr = (EditText) findViewById(R.id.username);
        EditText psw = (EditText) findViewById(R.id.password);

        usr.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                visible_error_login(false, 1);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                Log.v("before" , "change");
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                Log.v("onText" , "change");
            }

        });


        psw.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                visible_error_login(false, 2);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                Log.v("before", "change");
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                Log.v("onText", "change");
            }

        });
    }

    public void onClick(View view) {
        if (view == ok) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editTextUsername.getWindowToken(), 0);

            username = editTextUsername.getText().toString();
            password = editTextPassword.getText().toString();

            if (controlla_correttezza(username, password)) {
            if (saveLoginCheckBox.isChecked()) {
                loginPrefsEditor.putBoolean("saveLogin", true);
                loginPrefsEditor.putString("username", username);
                loginPrefsEditor.putString("password", password);
                loginPrefsEditor.putBoolean("autoEntra", true);
                loginPrefsEditor.commit();
            } else {
                loginPrefsEditor.clear();
                loginPrefsEditor.commit();
            }
                sendJson(username, password);

            }
            else
            {
                visible_error_login(true, 3);
            }
        }
    }

    public void doSomethingElse() {
        String user = "Alex";
            Intent myIntent = new Intent(MainActivity.this, BodyofApp.class);
            myIntent.putExtra("username", user);
            myIntent.putExtra("notifica", 0);
            startActivity(myIntent);
            this.finish();
    }

    private void visible_error_login(boolean visibility, int tipo) {
        TextView usr_error = (TextView) findViewById(R.id.login_username_error);
        TextView psw_error = (TextView) findViewById(R.id.login_pasw_error);
        if (visibility) {
            if (tipo != 1) {
                usr_error.setVisibility(View.VISIBLE);
            }
            if (tipo != 2) {
                psw_error.setVisibility(View.VISIBLE);
            }
        } else {
            if (tipo != 1) {
                usr_error.setVisibility(View.INVISIBLE);
            }
            if (tipo != 2) {
                psw_error.setVisibility(View.INVISIBLE);
            }
        }

    }


    private boolean controlla_correttezza(String user, String password) {
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
