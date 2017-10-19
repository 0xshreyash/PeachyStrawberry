package com.comp30022.helium.strawberry.components.ar;

import android.location.Location;

import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.mocks.MockLocation;
import com.comp30022.helium.strawberry.mocks.MockUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ARTrackerBeaconTest {
    private static final String MOCK_USER = "mock_user";
    private static final String MOCK_USER_NAME = "asdf";
    private static final double LONGITUDE = 144.2328;
    private static final double LATITUDE = -89.2312;
    private static final double ALTITUDE = 50;
    private static final float DEFAULT_X = 189.12f;
    private static final float DEFAULT_Y = 302.20f;
    private static final boolean DEFAULT_ACTIVE_STATUS = true;
    private static final double EPSILON = 1e-3;

    private ARTrackerBeacon beacon;
    private Location defaultLocation;
    private Location otherLocation;
    private User mockedUser;
    private User randomUser;

    @Before
    public void setUp() throws Exception {
        this.defaultLocation = new MockLocation(LONGITUDE, LATITUDE, ALTITUDE);
        this.otherLocation = new MockLocation(LONGITUDE + 2.32, LATITUDE - 1.23, 0);
        this.mockedUser = MockUser.getMockUser(MOCK_USER, MOCK_USER_NAME);
        this.randomUser = MockUser.getMockUser(MOCK_USER + "g", MOCK_USER_NAME + "a");

        this.beacon = new ARTrackerBeacon(mockedUser, DEFAULT_ACTIVE_STATUS,
                User.ProfilePictureType.LARGE);
        beacon.updateLocation(defaultLocation);
    }

    @After
    public void tearDown() throws Exception {
        beacon = null;
        this.defaultLocation = null;
        this.otherLocation = null;
    }

    /**
     * make sure that our initial location still stays in getLocation() invocation
     *
     * @throws Exception exception
     */
    @Test
    public void getLocation_EQUALS() throws Exception {
        // can't test this due to Location returning NULL in non-instrumented tests
        // ARTrackerBeacon makes a constructor copy of location instead of assignment,
        // causing the polymorphic setters and getters in mock location to be ignored.
//        assertEquals(defaultLocation, this.beacon.getLocation());
    }

    /**
     * Make sure that any different location isn't considered the same
     *
     * @throws Exception exception
     */
    @Test
    public void getLocation_NOTEQUALS() throws Exception {
        // can't test this due to Location returning NULL in non-instrumented tests
        // ARTrackerBeacon makes a constructor copy of location instead of assignment,
        // causing the polymorphic setters and getters in mock location to be ignored.
//        assertNotEquals(otherLocation, this.beacon.getLocation());
    }

    /**
     * Make sure that when we update our location, it's reflected in a GetLocation() invocation
     *
     * @throws Exception exception
     */
    @Test
    public void updateLocation_EQUALS() throws Exception {
        // can't test this due to Location returning NULL in non-instrumented tests
        // ARTrackerBeacon makes a constructor copy of location instead of assignment,
        // causing the polymorphic setters and getters in mock location to be ignored.
//        beacon.updateLocation(otherLocation);
//        assertEquals(otherLocation, this.beacon.getLocation());
    }

    /**
     * Make sure that when we update our location, the old value is gone!
     *
     * @throws Exception exception
     */
    @Test
    public void updateLocation_NOTEQUALS() throws Exception {
        // can't test this due to Location returning NULL in non-instrumented tests
        // ARTrackerBeacon makes a constructor copy of location instead of assignment,
        // causing the polymorphic setters and getters in mock location to be ignored.
        // beacon.updateLocation(otherLocation);
        // assertNotEquals(defaultLocation, this.beacon.getLocation());
    }

    /**
     * Make sure that when we created a beacon for a user, the username is preserved
     *
     * @throws Exception exception
     */
    @Test
    public void getUserName_EQUALS() throws Exception {
        assertEquals(MOCK_USER_NAME, beacon.getUserName());
    }

    /**
     * Make sure that when we created a beacon for a user, another random username isn't the same
     *
     * @throws Exception exception
     */
    @Test
    public void getUserName_NOTEQUALS() throws Exception {
        assertNotEquals(MOCK_USER_NAME + "ASD", beacon.getUserName());
    }

    //@Test
    //public void getProfilePicture() throws Exception {
    //    // can't test this without a running application
    //    return;
    //}

    /**
     * Make sure that the beacon returns the same user that this beacon represents
     *
     * @throws Exception exception
     */
    @Test
    public void getUser_EQUALS() throws Exception {
        assertEquals(mockedUser, beacon.getUser());
    }

    /**
     * Make sure that the beacon doesn't return some other random user!
     *
     * @throws Exception exception
     */
    @Test
    public void getUser_NOTEQUALS() throws Exception {
        assertNotEquals(randomUser, beacon.getUser());
    }

    /**
     * This X and Y represents the screen coordinates for this user's beacon. Make sure getY and
     * getX returns the right value
     *
     * @throws Exception exception
     */
    @Test
    public void setXY_GetterCorrectness() throws Exception {
        this.beacon.setXY(DEFAULT_X, DEFAULT_Y);
        assertEquals(DEFAULT_X, this.beacon.getX(), EPSILON);
        assertEquals(DEFAULT_Y, this.beacon.getY(), EPSILON);
    }

    /**
     * If profile picture has loaded, this beacon should return true for finish loading.
     * Since we can't(AND DIDNT) test that, this should return false.
     * see getProfilePicture() method that requires StrawberryApplication to be running
     *
     * @throws Exception exception
     */
    @Test
    public void finishLoading() throws Exception {
        assertFalse(this.beacon.finishLoading());
    }

    /* THERE ARE ONLY THREE SIZES THAT AR-BEACON USES, HENCE WE'LL ONLY TEST THOSE THREE */
    /*  ============= BEGIN SIZE TEST  =================== */

    /**
     * Should respond to (LARGE) profile picture size requests
     *
     * @throws Exception exception
     */
    @Test
    public void setSize_LARGE() throws Exception {
        this.beacon.setSize(User.ProfilePictureType.LARGE);
        assertEquals(User.ProfilePictureType.LARGE, this.beacon.getSize());
    }

    /**
     * Should respond to (NORMAL) profile picture size requests
     *
     * @throws Exception exception
     */
    @Test
    public void setSize_NORMAl() throws Exception {
        this.beacon.setSize(User.ProfilePictureType.NORMAL);
        assertEquals(User.ProfilePictureType.NORMAL, this.beacon.getSize());
    }

    /**
     * Should respond to (SMALL) profile picture size requests
     *
     * @throws Exception exception
     */
    @Test
    public void setSize_SMALL() throws Exception {
        this.beacon.setSize(User.ProfilePictureType.SMALL);
        assertEquals(User.ProfilePictureType.SMALL, this.beacon.getSize());
    }
    /*  ============= END SIZE TEST  =================== */

    /**
     * Our setup has specified an default active status beacon (i.e. currently in/out of FOCUS mode)
     * FOCUS mode means this beacon will always be rendered no matter what.
     * Beacons not in active state will disappear in FOCUS mode.
     *
     * @throws Exception exception
     */
    @Test
    public void isActive_DefaultConstructor() throws Exception {
        assertEquals(DEFAULT_ACTIVE_STATUS, this.beacon.isActive());
    }

    /**
     * Self explanatory, checks if setting activeness is correct.
     *
     * @throws Exception exception
     */
    @Test
    public void setActive_true() throws Exception {
        this.beacon.setActive(true);
        assertTrue(this.beacon.isActive());
    }

    /**
     * Self explanatory, checks if setting activeness is correct.
     *
     * @throws Exception exception
     */
    @Test
    public void setActive_false() throws Exception {
        this.beacon.setActive(false);
        assertFalse(this.beacon.isActive());
    }

    /**
     * This method tests the euclidian distance formula for our screen space distance.
     * This method is used to check the nearest tapped beacon.
     * EG:
     * - The nearest tapped beacon becomes the active beacon, and is constantly visible
     * - The nearest tapped beacon becomes unactive if tapped again, other non active beacons
     * become visible now
     *
     * @throws Exception exception
     */
    @Test
    public void distanceTo() throws Exception {
        int x1 = 0, y1 = 0;
        int x2 = 1, y2 = 1;
        this.beacon.setXY(x2, y2);
        /* your typical unit square, we all know that the hypotenuse is sqrt(2)

            /
     sqrt 2/ | 1
          /__|
            1

         by euclidian distance

        */
        assertEquals(this.beacon.distanceTo(x1, y1), Math.sqrt(2), EPSILON);
    }

    /**
     * trivial test (getter/setter)
     *
     * @throws Exception exception
     */
    @Test
    public void setVisible() throws Exception {
        this.beacon.setVisible(true);
        assertTrue(this.beacon.isVisible());
    }

    /**
     * trivial test (getter/setter)
     *
     * @throws Exception exception
     */
    @Test
    public void setInvisible() throws Exception {
        this.beacon.setVisible(false);
        assertFalse(this.beacon.isVisible());
    }

    /**
     * By default, ALL beacons should be visible!!!
     * Or else,nothing will appear in our AR camera.
     *
     * @throws Exception exception
     */
    @Test
    public void defaultVisibility() throws Exception {
        assertTrue(this.beacon.isVisible());
    }

    /**
     * An AR beacon is equal to another if and ONLY IF it represents the SAME USER.
     *
     * @throws Exception exception
     */
    @Test
    public void equals_SAMEUSER() throws Exception {

        assertEquals(this.beacon.getUser(), this.mockedUser);
        ARTrackerBeacon sameUserInBeacon = new ARTrackerBeacon(mockedUser, DEFAULT_ACTIVE_STATUS,
                User.ProfilePictureType.LARGE);
        assertTrue(this.beacon.equals(sameUserInBeacon));
    }

    /**
     * An AR beacon is equal to another if and ONLY IF it represents the SAME USER.
     *
     * @throws Exception exception
     */
    @Test
    public void notEquals_DIFFUSER() throws Exception {
        ARTrackerBeacon differentUserInBeacon = new ARTrackerBeacon(this.randomUser,
                DEFAULT_ACTIVE_STATUS, User.ProfilePictureType.LARGE);
        assertNotEquals(this.beacon.getUser(), this.randomUser);
        assertFalse(this.beacon.equals(differentUserInBeacon));
    }

}