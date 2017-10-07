package com.comp30022.helium.strawberry.components.location;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.comp30022.helium.strawberry.activities.MainActivity;
import com.comp30022.helium.strawberry.patterns.Subscriber;

/**
 * Created by noxm on 17/09/17.
 */

public abstract class LocationServiceFragment extends Fragment implements Subscriber<LocationEvent> {
    private static final String TAG = LocationServiceFragment.class.getSimpleName();

    protected LocationService locationService;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationService = LocationService.getInstance();
        if(locationService != null) {
            locationService.registerSubscriber(this);
        } else {
            Intent i = new Intent(this.getActivity(), MainActivity.class);
            startActivity(i);
            this.getActivity().finish();
        }

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
