package com.comp30022.helium.strawberry.entities;


import android.util.Log;

import com.android.volley.Response;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.components.server.exceptions.InstanceExpiredException;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;
import com.comp30022.helium.strawberry.patterns.exceptions.NotInstantiatedException;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String id, username;

    public User(String id) {
        this.id = id;
        this.username = "";
        getLatestUsername();
    }

    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof User) {
            User other = (User) obj;
            return other.getId().equals(this.getId());
        }
        return false;
    }

    private void getLatestUsername() {
        try {
            PeachServerInterface.getInstance().getUser(id, new StrawberryListener(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject self = new JSONObject(response.toString());
                        username = self.get("username").toString();
                        Log.d("PeachUser", "Username updated to " + username);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null));

        } catch (NotInstantiatedException | InstanceExpiredException e) {
            e.printStackTrace();
        }
    }
}
