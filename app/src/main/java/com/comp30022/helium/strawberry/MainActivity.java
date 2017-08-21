package com.comp30022.helium.strawberry;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.vuforia.State;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class MainActivity extends AppCompatActivity {
    private TextView info;
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        info.setText(
                                "User ID: "
                                        + loginResult.getAccessToken().getUserId()
                                        + "\n" +
                                        "Auth Token: "
                                        + loginResult.getAccessToken().getToken()
                        );
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        info.setText("Login attempt canceled.");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
//                        info.setText("Login attempt failed.");
                        info.setText(exception.toString());
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void goToMap(View view) {
        Intent intent = new Intent(this, MapViewActivity.class);
//        EditText editText = (EditText) findViewById(R.id.editText);
//        String message = editText.getText().toString();
        intent.putExtra("EXTRA_MESSAGE", "some custom message");
        startActivity(intent);
    }
}
