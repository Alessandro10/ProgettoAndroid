package com.example.tonino.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private String username,password;
    private Button ok;
    private EditText editTextUsername,editTextPassword;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    private FragmentActivity myContext;


    private void vaiaqualcosa()
    {
        Intent intent = new Intent(MainActivity.this , broadCast.class);
        startActivity(intent);
    }


    private void okPuoiEntrare(String username , String password)
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
                        okPuoiEntrare(username , pwd);
                    }
                    else
                    {
                        AlertDialog.Builder b =new AlertDialog.Builder(myContext);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            int c = extras.getInt("notifica");

            if (c == 1) {
                stopService(new Intent(getBaseContext(), Service2.class));
            }
        }


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
                //okPuoiEntrare("cioa" , "aiis");

                //doSomethingElse();
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
