package com.comp30022.helium.strawberry.components;

import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.components.server.rest.PeachRestInterface;
import com.comp30022.helium.strawberry.components.server.rest.components.PeachCookieStore;

import org.junit.BeforeClass;
import org.junit.Test;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


/**
 * Created by noxm on 10/10/17.
 */

public class PeachCookieStoreTest {
    private static PeachCookieStore peachCookieStore = new PeachCookieStore();

    private static URI uri1;
    private static URI uri1Auth;
    private static URI uri2;
    private static HttpCookie cookie1;
    private static HttpCookie cookie2;
    private static HttpCookie authCookie;

    @BeforeClass
    public static void setup() throws URISyntaxException {
        uri1 = new URI(PeachRestInterface.SERVER_URI);
        uri1Auth = new URI(PeachRestInterface.SERVER_URI + PeachRestInterface.VERSION + "/authorize");
        uri2 = new URI("https://google.com");

        cookie1 = new HttpCookie("test1", "value1");
        cookie2 = new HttpCookie("test2", "value2");
        authCookie = new HttpCookie("auth", "cookie");
    }

    @Test
    public void insertCookie_valid() {
        peachCookieStore.add(uri1, cookie1);
        peachCookieStore.add(uri1, cookie2);
        peachCookieStore.add(uri2, cookie2);

        List<HttpCookie> res = peachCookieStore.get(uri1);
        assertTrue(res.contains(cookie1));
        assertTrue(res.contains(cookie2));

        List<HttpCookie> res2 = peachCookieStore.get(uri2);
        assertFalse(res2.contains(cookie1));
        assertTrue(res2.contains(cookie2));
    }

    @Test
    public void removeCookie_valid() {
        peachCookieStore.remove(uri1, cookie1);

        List<HttpCookie> res = peachCookieStore.get(uri1);
        assertFalse(res.contains(cookie1));

        List<HttpCookie> res2 = peachCookieStore.get(uri2);
        assertFalse(res2.contains(cookie1));
    }

    @Test
    public void authCookie_validBehaviour() {
        peachCookieStore.add(uri1Auth, authCookie);

        List<HttpCookie> res = peachCookieStore.get(uri1);
        assertTrue(res.contains(authCookie));
    }
}
