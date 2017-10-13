package com.comp30022.helium.strawberry.mocks;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.comp30022.helium.strawberry.components.location.LocationService;
import com.comp30022.helium.strawberry.mocks.exceptions.NotImplementedMockMethod;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by noxm on 10/10/17.
 */

public class MockGoogleApiClient extends GoogleApiClient {

    private static final Location NEW_LOCATION = new Location("");

    public void updateLocation(LocationService locationServiceToTest) {
        NEW_LOCATION.setLongitude(new Random().nextDouble());
        NEW_LOCATION.setLatitude(new Random().nextDouble());
        locationServiceToTest.onLocationChanged(NEW_LOCATION);
    }

    @Override
    public boolean hasConnectedApi(@NonNull Api<?> api) {
        throw new NotImplementedMockMethod();
    }

    @NonNull
    @Override
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        throw new NotImplementedMockMethod();
    }

    @Override
    public void connect() {

    }

    @Override
    public ConnectionResult blockingConnect() {
        throw new NotImplementedMockMethod();
    }

    @Override
    public ConnectionResult blockingConnect(long l, @NonNull TimeUnit timeUnit) {
        throw new NotImplementedMockMethod();
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void reconnect() {

    }

    @Override
    public PendingResult<Status> clearDefaultAccountAndReconnect() {
        throw new NotImplementedMockMethod();
    }

    @Override
    public void stopAutoManage(@NonNull FragmentActivity fragmentActivity) {

    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isConnecting() {
        return false;
    }

    @Override
    public void registerConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {

    }

    @Override
    public boolean isConnectionCallbacksRegistered(@NonNull ConnectionCallbacks connectionCallbacks) {
        throw new NotImplementedMockMethod();
    }

    @Override
    public void unregisterConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {

    }

    @Override
    public void registerConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {

    }

    @Override
    public boolean isConnectionFailedListenerRegistered(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        throw new NotImplementedMockMethod();
    }

    @Override
    public void unregisterConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {

    }

    @Override
    public void dump(String s, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strings) {

    }
}
