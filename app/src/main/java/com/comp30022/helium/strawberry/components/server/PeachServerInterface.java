package com.comp30022.helium.strawberry.components.server;

import com.comp30022.helium.strawberry.components.location.exceptions.NotInstantiatedException;
import com.comp30022.helium.strawberry.components.server.rest.PeachRestInterface;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;

import java.util.HashMap;

/**
 * Created by noxm on 17/09/17.
 */

public class PeachServerInterface {
    private static PeachServerInterface instance = null;

    public static PeachServerInterface getInstance() throws NotInstantiatedException {
        if(instance == null)
            throw new NotInstantiatedException();

        return instance;
    }

    public static void init(String facebookToken) {
        if(instance != null)
            return;
        instance = new PeachServerInterface(facebookToken);
    }

    private PeachServerInterface(String token) {
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        PeachRestInterface.post("/v1/authorization", tokenMap, new StrawberryListener());
    }
}
