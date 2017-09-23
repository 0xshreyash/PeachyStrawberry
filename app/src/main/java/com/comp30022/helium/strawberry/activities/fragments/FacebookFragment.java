package com.comp30022.helium.strawberry.activities.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.patterns.Publisher;
import com.comp30022.helium.strawberry.patterns.Subscriber;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.List;

public class FacebookFragment extends Fragment implements Publisher<Boolean> {
    private static final String TAG = FacebookFragment.class.getSimpleName();
    private TextView info;

    private AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private List<Subscriber<Boolean>> subscribers = new ArrayList<>();

    public FacebookFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);

        // new auth tracker
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    StrawberryApplication.remove("token");
                    info.setText("Log out");

                } else {
                    StrawberryApplication.setString("token", currentAccessToken.getToken());
                    notifyAllSubscribers(true);
                }
            }
        };

        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        info = (TextView) view.findViewById(R.id.info);

        loginButton.setReadPermissions("email", "user_friends", "user_likes");
        loginButton.setFragment(this);

        // Callback registration
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText("UserID: " + loginResult.getAccessToken().getUserId() +"\n" +
                            "AuthToken: " + loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                info.setText("Login cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                info.setText("Login Error: " + exception.getMessage());
            }
        });

        // get the saved token and display
        String token = StrawberryApplication.getString("token");
        if(token != null) {
            info.setText("Saved Token: " + token);
        } else {
            info.setText("Login for more information");
        }

        PeachServerInterface.init(token);

        return view;
    }

    private void notifyAllSubscribers(Boolean res) {
        for(Subscriber<Boolean> sub: subscribers) {
            sub.update(res);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void registerSubscriber(Subscriber<Boolean> sub) {
        subscribers.add(sub);
    }

    @Override
    public void deregisterSubscriber(Subscriber<Boolean> sub) {
        subscribers.remove(sub);
    }
}
