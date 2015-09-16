package com.example.tonino.login;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

public class WebServiceRequestService extends IntentService {

    public static final String SERVICE_NAME = "WebServiceRequestService";

    public static final String PARAM_OUT_RESPONSE = "response";
    public static final String PARAM_OUT_STATUS_CODE = "statusCode";
    public static final String PARAM_OUT_URI = "uri";
    public static final String PARAM_OUT_COOKIES = "cookies";
    public static final String PARAM_IN_CALLER = "caller";
    public static final String PARAM_IN_URI = PARAM_OUT_URI;
    public static final String PARAM_IN_COOKIES = PARAM_OUT_COOKIES;
    public static final String PARAM_IN_METHOD = "method";
    public static final String PARAM_IN_JSON = "json";

    public static final int NO_INTERNET_CONNECTION = 0;

    private DefaultHttpClient httpClient;
    private String baseUri;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p/>
     * super parameter is used to name the worker thread, important only for debugging.
     */
    public WebServiceRequestService() {
        super(SERVICE_NAME);
    }

    public static final String COOKIE_COMMENT = "comment";
    public static final String COOKIE_COMMENT_URL = "commenturl";
    public static final String COOKIE_VALUE = "value";
    public static final String COOKIE_DOMAIN = "domain";
    public static final String COOKIE_PATH = "path";
    public static final String COOKIE_PORTS = "ports";
    public static final String COOKIE_VERSION = "version";

    public static Bundle cookieListToBundle(List<Cookie> cookieList) {
        Bundle bundle = new Bundle();
        for (Cookie cookie : cookieList) {
            Bundle cookieBundle = new Bundle();
            cookieBundle.putString(COOKIE_COMMENT, cookie.getComment());
            cookieBundle.putString(COOKIE_COMMENT_URL, cookie.getCommentURL());
            cookieBundle.putString(COOKIE_VALUE, cookie.getValue());
            cookieBundle.putString(COOKIE_DOMAIN, cookie.getDomain());
            cookieBundle.putString(COOKIE_PATH, cookie.getPath());
            cookieBundle.putIntArray(COOKIE_PORTS, cookie.getPorts());
            cookieBundle.putInt(COOKIE_VERSION, cookie.getVersion());
            bundle.putBundle(cookie.getName(), cookieBundle);
        }
        return bundle;
    }

    public static void setHttpCookiesFromBundle(DefaultHttpClient httpClient, Bundle bundle) {
        if (bundle != null) {
            for (String cookieName : bundle.keySet()) {
                Bundle cookieBundle = bundle.getBundle(cookieName);
                BasicClientCookie2 cookie = new BasicClientCookie2(cookieName,
                        cookieBundle.getString(COOKIE_VALUE));
                cookie.setComment(cookieBundle.getString(COOKIE_COMMENT));
                cookie.setCommentURL(cookieBundle.getString(COOKIE_COMMENT_URL));
                cookie.setDomain(cookieBundle.getString(COOKIE_DOMAIN));
                cookie.setPath(cookieBundle.getString(COOKIE_PATH));
                cookie.setPorts(cookieBundle.getIntArray(COOKIE_PORTS));
                cookie.setVersion(cookieBundle.getInt(COOKIE_VERSION));
                httpClient.getCookieStore().addCookie(cookie);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        httpClient = new DefaultHttpClient();
        baseUri = getString(R.string.WEB_SERVICE_URL);
    }

    private class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "DELETE";

        public String getMethod() {
            return METHOD_NAME;
        }

        public HttpDeleteWithBody(final String uri) {
            super();
            setURI(URI.create(uri));
        }

        public HttpDeleteWithBody(final URI uri) {
            super();
            setURI(uri);
        }

        public HttpDeleteWithBody() {
            super();
        }
    }

    private class Response {
        public int statusCode;
        public String body;
    }

    private Response executeHttpRequest(HttpRequestBase request) {
        setHeaders(request);
        HttpResponse httpResponse;
        Response response = new Response();
        try {
            httpResponse = httpClient.execute(request);
            StatusLine statusLine = httpResponse.getStatusLine();
            response.statusCode = statusLine.getStatusCode();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                entity.writeTo(out);
                response.body = out.toString();
            }
        } catch (IOException e) {
            response.statusCode = NO_INTERNET_CONNECTION;
        }
        return response;
    }

    private void setHeaders(HttpRequestBase request) {
        request.setHeader("user-agent", "appdani");
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String intentStr = extras.getString(PARAM_IN_CALLER);
        Intent resultIntent = new Intent(intentStr);
        String uri = extras.getString(PARAM_IN_URI);
        Bundle cookiesBundle = extras.getBundle(PARAM_IN_COOKIES);
        String method = extras.getString(PARAM_IN_METHOD);
        String json = extras.getString(PARAM_IN_JSON);
        setHttpCookiesFromBundle(httpClient, cookiesBundle);
        Response requestResponse = null;
        String completeUri = baseUri + uri;
        HttpRequestBase request = null;
        if (method.equals("GET")) {
            request = new HttpGet(completeUri);
        } else {
            HttpEntityEnclosingRequestBase requestWithEntiy = null;
            switch (method) {
                case "POST":
                    requestWithEntiy = new HttpPost(completeUri);
                    break;
                case "PUT":
                    requestWithEntiy = new HttpPut(completeUri);
                    break;
                case "DELETE":
                    requestWithEntiy = new HttpDeleteWithBody(completeUri);
                    break;
            }
            if (json != null) {
                try {
                    requestWithEntiy.setEntity(new StringEntity(json));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            request = requestWithEntiy;
        }
        requestResponse = executeHttpRequest(request);
        resultIntent.putExtra(PARAM_OUT_URI, uri);
        resultIntent.putExtra(PARAM_OUT_COOKIES, cookieListToBundle(httpClient.getCookieStore()
                .getCookies()));
        resultIntent.putExtra(PARAM_OUT_STATUS_CODE, requestResponse.statusCode);
        resultIntent.putExtra(PARAM_OUT_RESPONSE, requestResponse.body);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }

}

