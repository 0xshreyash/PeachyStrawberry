package com.comp30022.helium.strawberry.components.server.rest;

import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;

import java.util.Map;

/**
 * This wrapper should be used to construct rest calls and
 * then user the rest interface to send the calls using
 * Volley.
 */
public class PeachRestInterface {
    private static final String urlPrefix = "https://gnomie.me/";
    private static final String version = "v1";

    public static void get(String peachPath, StrawberryListener strawberryListener) {
        String url = (peachPath == null) ? urlPrefix : urlPrefix + version + peachPath;
        RestInterface.get(url, strawberryListener);
    }

    public static void post(String peachPath, Map<String, String> params, StrawberryListener strawberryListener) {
        String url = (peachPath == null) ? urlPrefix : urlPrefix + version + peachPath;
        RestInterface.post(url, params, strawberryListener);
    }

    public static void put(String peachPath, Map<String, String> params, StrawberryListener strawberryListener) {
        String url = (peachPath == null) ? urlPrefix : urlPrefix + version + peachPath;
        RestInterface.put(url, params, strawberryListener);
    }

    public static void delete(String peachPath, Map<String, String> params, StrawberryListener strawberryListener) {
        String url = (peachPath == null) ? urlPrefix : urlPrefix + version + peachPath;
        RestInterface.delete(url, params, strawberryListener);
    }
}
