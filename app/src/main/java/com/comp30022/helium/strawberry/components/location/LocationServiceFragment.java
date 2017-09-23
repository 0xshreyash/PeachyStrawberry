package com.comp30022.helium.strawberry.components.location;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.comp30022.helium.strawberry.patterns.Subscriber;
import com.comp30022.helium.strawberry.components.location.exceptions.NotInstantiatedException;

/**
 * Created by noxm on 17/09/17.
 */

public abstract class LocationServiceFragment extends Fragment implements Subscriber<LocationEvent> {
    private static final String TAG = "LocationServiceFragment";

    protected LocationService locationService;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            locationService = LocationService.getInstance();
        } catch (NotInstantiatedException e) {
            Log.e(TAG, e.getMessage());
        }

        locationService.registerSubscriber(this);

        onCreateAction(savedInstanceState);
    }

    @Override
    public final void onResume() {
        super.onResume();
        locationService.onResume();
        onResumeAction();
    }

    @Override
    public final void onPause() {
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
