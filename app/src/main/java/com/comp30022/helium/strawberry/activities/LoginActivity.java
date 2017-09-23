package com.comp30022.helium.strawberry.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.activities.fragments.FacebookFragment;
import com.comp30022.helium.strawberry.patterns.Subscriber;

public class LoginActivity extends AppCompatActivity implements Subscriber<Boolean> {
    private static final String TAG = "PeachLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    protected void onResume() {
        super.onResume();
        FacebookFragment fragment = (FacebookFragment) getFragmentManager().findFragmentById(R.id.login_container);
        fragment.registerSubscriber(this);

        if(StrawberryApplication.getString("token") != null)
            continueToMain();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FacebookFragment fragment = (FacebookFragment) getFragmentManager().findFragmentById(R.id.login_container);
        fragment.deregisterSubscriber(this);
    }

    private void continueToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void update(Boolean res) {
        if (res) {
            continueToMain();
        }
    }
}
