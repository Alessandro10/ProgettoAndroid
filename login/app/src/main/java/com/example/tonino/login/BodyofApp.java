package com.example.tonino.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class BodyofApp extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    boolean girato = false;

    Bundle savedInstanceState2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bodyof_app);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setCustomView(View.GONE);

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
        //mTitle = getTitle();
        mTitle = Salva.getTitleFragment();
        //mTitle = "Ciao";
        // Set up the drawer.
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
                //mTitle = getString(R.string.home);
                break;
            case 2:
                //mTitle = getString(R.string.premio);
                mTitle = "Premi";
                break;
            case 3:
                //mTitle = getString(R.string.obiettivo);
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
        if(id == R.id.Help)
        {AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);


            alertDialogBuilder.setTitle("HELP");

            alertDialogBuilder.setMessage("Informazioni sull'utilizzo dell'app");


            alertDialogBuilder.setNeutralButton("OK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


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
