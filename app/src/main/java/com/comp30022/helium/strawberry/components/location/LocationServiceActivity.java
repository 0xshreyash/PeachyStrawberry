package com.comp30022.helium.strawberry.components.location;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.comp30022.helium.strawberry.patterns.Subscriber;

/**
 * Created by noxm on 17/09/17.
 */

public abstract class LocationServiceActivity extends AppCompatActivity implements Subscriber<Location> {
    private static final String TAG = "LocationServiceActivity";

    protected LocationService locationService;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationService = LocationService.getInstance();

        locationService.registerSubscriber(this);

        onCreateAction(savedInstanceState);
    }

    @Override
    protected final void onResume() {
        super.onResume();
        locationService.onResume();
        onResumeAction();
    }

    @Override
    protected final void onPause() {
        super.onPause();
        locationService.onPause();
        onPauseAction();
    }

    /**
     * Override if you want further on resume actions
     */
    protected void onResumeAction() {

    }

    protected void onPauseAction() {

    }

    protected void onCreateAction(Bundle savedInstanceState) {

    }
}
