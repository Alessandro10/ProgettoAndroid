package com.example.tonino.login;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

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

public class Service2 extends Service {
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    private NotificationManager mNotificationManager;
    private int notificationID = 100;
    private int numMessages = 0;
    private Context myContext;

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

                    String title = "" , msg = "";

                    PendingIntent resultPendingIntent = null;

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
                            Salva.setListaDeiTagsPreferiti(ListTipi);

                            Intent resultIntent = new Intent(myContext, BodyofApp.class);
                            resultIntent.putExtra("notifica" , 1);

                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(myContext);
                            stackBuilder.addParentStack(BodyofApp.class);

      /* Adds the Intent that starts the Activity to the top of the stack */
                            stackBuilder.addNextIntent(resultIntent);
                            resultPendingIntent =
                                    stackBuilder.getPendingIntent(
                                            0,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    );

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else {

                        Intent resultIntent = new Intent(myContext, MainActivity.class);
                        resultIntent.putExtra("notifica", 1);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(myContext);
                        stackBuilder.addParentStack(MainActivity.class);

      /* Adds the Intent that starts the Activity to the top of the stack */
                        stackBuilder.addNextIntent(resultIntent);
                        resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                    }

                        NotificationCompat.Builder  mBuilder =
                                new NotificationCompat.Builder(myContext);

                        mBuilder.setContentTitle("Fantastico !!!");
                        mBuilder.setContentText("Hai vinto un nuovo premio");
                        mBuilder.setTicker("Congratulazione hai completato tutti gli obiettivi e hai vinto un premio");
                        mBuilder.setSmallIcon(R.drawable.coppa);

      /* Increase notification number every time a new notification arrives */
                        mBuilder.setNumber(++numMessages);

                        mBuilder.setContentIntent(resultPendingIntent);

                        mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

      /* notificationID allows you to update the notification later on. */
                        mNotificationManager.notify("TAG" , notificationID, mBuilder.build());



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



    protected void displayNotification() {
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", 0/*MODE_PRIVATE*/);

        String username = loginPreferences.getString("username", "");
        String password = loginPreferences.getString("password", "");

        sendJson(username , password);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        new Thread(new Runnable() {
            @Override
            public void run() {
                    displayNotification();
            }
        }).start();

        return START_STICKY;
    }

    protected void cancelNotification() {
        mNotificationManager.cancel("TAG" , notificationID);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        cancelNotification();
    }
}
