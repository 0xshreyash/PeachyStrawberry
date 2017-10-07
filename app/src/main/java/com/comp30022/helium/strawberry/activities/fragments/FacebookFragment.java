package com.comp30022.helium.strawberry.activities.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
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

public class FacebookFragment extends Fragment implements Publisher<String> {
    private static final String TAG = FacebookFragment.class.getSimpleName();

    private AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private List<Subscriber<String>> subscribers = new ArrayList<>();

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
                    Log.i(TAG, "Logout");

                } else {
                    StrawberryApplication.setString("token", currentAccessToken.getToken());
                    Log.d(TAG, "Token is: " + currentAccessToken.getToken());
                    notifyAllSubscribers(currentAccessToken.getToken());
                }
            }
        };

        // login button config
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "user_friends", "user_likes");
        loginButton.setFragment(this);
        // Callback registration
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "UserID: " + loginResult.getAccessToken().getUserId() + "\n" + "AuthToken: " + loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                Log.w(TAG, "Login cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "Login Error: " + exception.getMessage());
            }
        });

        // get the saved token and log
        String token = StrawberryApplication.getString("token");
        if (token != null) {
            Log.i(TAG, "Saved Token: " + token);
        } else {
            Log.i(TAG, "Login for more information");
        }

        return view;
    }

    private void notifyAllSubscribers(String res) {
        for (Subscriber<String> sub : subscribers) {
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
    public void registerSubscriber(Subscriber<String> sub) {
        subscribers.add(sub);
    }

    @Override
    public void deregisterSubscriber(Subscriber<String> sub) {
        subscribers.remove(sub);
    }
}
