package com.comp30022.helium.strawberry.components.server.rest.components;

import android.util.Log;

import com.comp30022.helium.strawberry.components.server.rest.PeachRestInterface;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by noxm on 19/09/17.
 */

public class PeachCookieStore implements CookieStore {
    private static final String TAG = PeachCookieStore.class.getSimpleName();
    private static final String AUTH_SUB_PATH = "authorize";

    Map<URI, List<HttpCookie>> cookieMap = new HashMap<>();
    HttpCookie authCookie = null;

    public PeachCookieStore() {

    }

    @Override
    public void add(URI uri, HttpCookie httpCookie) {
        // detect auth cookie
        if (uri.getHost().equals(PeachRestInterface.HOSTNAME) && uri.getPath().contains(AUTH_SUB_PATH))
            authCookie = httpCookie;

//        Log.d(TAG, "Added cookie " + httpCookie);
        if (!cookieMap.containsKey(uri)) {
            cookieMap.put(uri, new ArrayList<HttpCookie>());
        }

        cookieMap.get(uri).add(httpCookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
//        Log.d(TAG, "Get cookie " + uri);
        List<HttpCookie> cookieList = (cookieMap.containsKey(uri)) ? cookieMap.get(uri) : new ArrayList<HttpCookie>();

        // embed our server auth cookie
        if (uri.getHost().equals(PeachRestInterface.HOSTNAME) && authCookie != null) {
            cookieList.add(authCookie);
        }

        return cookieList;
    }

    @Override
    public List<HttpCookie> getCookies() {
        Log.d(TAG, "Get Cookies");
        List<HttpCookie> all = new ArrayList<>();

        for (URI key : cookieMap.keySet()) {
            all.addAll(cookieMap.get(key));
        }
        return all;
    }

    @Override
    public List<URI> getURIs() {
        Log.d(TAG, "Get URIs");
        List<URI> all = new ArrayList<>();
        all.addAll(cookieMap.keySet());

        return all;
    }

    @Override
    public boolean remove(URI uri, HttpCookie httpCookie) {
        Log.d(TAG, "Remove " + uri);
        List<HttpCookie> result = cookieMap.remove(uri);

        return (result != null);
    }

    @Override
    public boolean removeAll() {
        Log.d(TAG, "Remove all");
        cookieMap.clear();

        return true;
    }
}
