package com.example.tonino.login;

import org.apache.http.client.HttpClient;

/**
 * Created by tonino on 05/05/15.
 */
public class Data {
    private static HttpClient httpclient;
    private static String url = "http://daniele.cortesi2.web.cs.unibo.it/wsgi";

    public static String getUrl() {
        return url;
    }
    public static HttpClient getHttpclient() {
        return httpclient;
    }
    public static void setHttpclient(HttpClient newhttpclient) {
        httpclient = newhttpclient;
    }
}