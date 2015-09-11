package com.example.tonino.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.login.DefaultAudience;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.utils.Logger;
import com.sromku.simple.fb.utils.Utils;

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
import java.util.Iterator;
import java.util.List;


public class BodyofApp extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    boolean girato = false;

    Bundle savedInstanceState2;

    private static final String APP_ID = "1612497275669488";
    private static final String APP_NAMESPACE = "discountwalk";

    private SimpleFacebook mSimpleFacebook;
    private Button buttonFacebook;

    private FragmentActivity myContext = this;

    protected void sendJson(final String username, final String pwd , final String idFb) {
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
                    HttpPost post = new HttpPost(Data.getUrl() + "/loginfb");
                    post.setHeader("user-agent", "app");
                    post.setHeader("Accept", "application/json");
                    json.put("fb_id" , idFb);
                    json.put("login", username);
                    json.put("psw", pwd);
                    JSONArray tags = new JSONArray();
                    List<String> listapreferiti = Salva.getListaDeiTagsPreferiti();
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
                            AlertDialog.Builder b = new AlertDialog.Builder(myContext);
                            b.setCancelable(false);
                            b.setTitle("ENTRATO");
                            b.setMessage("FATTO");
                            b.setNeutralButton("OK", new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            b.create().show();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
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


    /**
     * Login example.
     */
    private void setLogin() {
        Log.i("idFb" , "SETLOGIN");

        // Login listener
        final OnLoginListener onLoginListener = new OnLoginListener() {

            @Override
            public void onFail(String reason) {
                Log.i("idFb" , "FAIL");
                //buttonFacebook.setText("Fail");
                //mTextStatus.setText(reason);
                //Log.w(TAG, "Failed to login");
            }

            @Override
            public void onException(Throwable throwable) {
                Log.i("idFb" , "ERRORE");
                //buttonFacebook.setText("exception");
                //mTextStatus.setText("Exception: " + throwable.getMessage());
                //Log.e(TAG, "Bad thing happened", throwable);
            }



            @Override
            public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
                // change the state of the button or do whatever you want
                Log.i("idFb" , "login");
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
                                //buttonFacebook.setText(
                                //" email " + email + " id " + id );
                                /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);


                                alertDialogBuilder.setTitle("HELP");

                                alertDialogBuilder.setMessage(" idFb = " + id);


                                alertDialogBuilder.setNeutralButton("OK",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                            }
                                        });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();*/
                                Log.i("idFb", " id = " + id);
                                Salva.setIdFacebook(id);
                                sendJson(Salva.getUsername() , Salva.getPassword() , Salva.getIdFacebook());
                                //sendJson(email , id , 1);
                                //Salva.setSimpleFacebook(mSimpleFacebook);

                               /*final OnLogoutListener onLogoutListener = new OnLogoutListener() {

                                   @Override
                                   public void onLogout() {

                                   }
                               };
                               mSimpleFacebook.logout(onLogoutListener);*/
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
                Log.i("idFb" , "CANC");
                //buttonFacebook.setText("cancel");
                // mTextStatus.setText("Canceled");
                //Log.w(TAG, "Canceled the login");
            }


        };

        mSimpleFacebook.login(onLoginListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bodyof_app);
        Log.i("idFb" , "INIZIO");
        Utils.updateLanguage(getApplicationContext(), "en");
        Utils.printHashKey(getApplicationContext());


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

        //buttonFacebook = (Button) findViewById(R.id.facebook);



        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            int c = extras.getInt("notifica");
            if (savedInstanceState != null) {
            }
            if (c == 1) {
                stopService(new Intent(getBaseContext(), Service2.class));
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(1))
                        .commit();
                Bundle args = new Bundle();
                args.putInt("dato", 1);
                Premio premio = new Premio();
                premio.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, premio)
                        .commit();
            }
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = Salva.getTitleFragment();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        //Salva.cambiafragment = true;
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.app.Fragment existingFragment;
        Salva.salvaNumberFragment(position);
        switch (position) {
            case 0:
                mTitle = "Home";
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new HomeSwipe())
                            .commit();
                break;
            case 1:
                mTitle = "Premi";
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new Premio())
                            .commit();
                break;
            case 2:
                mTitle = "Percorsi";
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new Obiettivo())
                            .commit();
                break;
        }
}

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = "Home";
                break;
            case 2:
                mTitle = "Premi";
                break;
            case 3:
                mTitle = "Percorsi";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        ViewGroup container = null;
        LayoutInflater inflater = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_registrazione, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            if(Salva.getAccountFB())
                getMenuInflater().inflate(R.menu.bodyof_app_fb, menu);
            else
                getMenuInflater().inflate(R.menu.bodyof_app, menu);
            restoreActionBar();

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.LoginFb)
        {
            setLogin();

            /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);


            alertDialogBuilder.setTitle("HELP");

            alertDialogBuilder.setMessage("Informazioni sull'utilizzo dell'app");


            alertDialogBuilder.setNeutralButton("OK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();*/

        }
        if(id == R.id.Logout)
        {
            SharedPreferences loginPreferences;
            SharedPreferences.Editor loginPrefsEditor;
            loginPreferences = getSharedPreferences("loginPrefs", 0/*MODE_PRIVATE*/);
            loginPrefsEditor = loginPreferences.edit();
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
            //autoEntra
            Intent myIntent = new Intent(BodyofApp.this, MainActivity.class);
            myIntent.putExtra("autoEntra" , false);
            startActivity(myIntent);
            this.finish();

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_bodyof_app, container, false);
            return rootView;
        }

        private FragmentActivity myContext;

        @Override
        public void onAttach(Activity activity) {

            myContext=(FragmentActivity) activity;
            super.onAttach(activity);
            ((BodyofApp) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
