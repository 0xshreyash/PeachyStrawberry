package com.comp30022.helium.strawberry.components;

import android.location.Location;
import android.support.test.runner.AndroidJUnit4;

import com.comp30022.helium.strawberry.components.location.LocationService;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.mocks.MockGoogleApiClient;
import com.comp30022.helium.strawberry.mocks.MockPeachServerInterface;
import com.comp30022.helium.strawberry.patterns.Event;
import com.comp30022.helium.strawberry.patterns.Subscriber;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.TimerTask;

import static junit.framework.Assert.assertTrue;

/**
 * Created by noxm on 26/08/17.
 */
@RunWith(AndroidJUnit4.class)
public class LocationServiceTest implements Subscriber<Event>{

    private static LocationService locService;
    private static MockGoogleApiClient mockGoogleApi;
    private boolean interfaceReady;
    private MockPeachServerInterface peachServerInterface;

    @BeforeClass
    public static void beforeClass() throws InterruptedException {
        LocationService svc = new LocationService();

        mockGoogleApi = new MockGoogleApiClient();
        svc.setup(mockGoogleApi);

        locService = LocationService.getInstance();
    }

    public void setupServerInterface() throws InterruptedException {
        peachServerInterface = new MockPeachServerInterface(this);

        while(!interfaceReady) {
            Thread.sleep(100);
        }
    }

    @Test
    public void getLocationService_valid() throws Exception {
        assertTrue(locService != null);
    }

    @Test
    public void getCurrentLocation_notNull() throws Exception {
        mockGoogleApi.updateLocation(locService);

        Location currLoc = locService.getDeviceLocation();
        assertTrue(currLoc != null);
    }

    @Test
    public void getUserLocation_notNull() throws Exception {
        setupServerInterface();

        User testUser = User.getUser(MockPeachServerInterface.SAMPLE_ID);
        locService.addTracker(testUser);

        TimerTask task = locService.getLocationQueryTimerTask();
        task.run();

        assertTrue(locService.getUserLocation(testUser) != null);
    }

    @Override
    public void update(Event info) {
        interfaceReady = true;
    }
}