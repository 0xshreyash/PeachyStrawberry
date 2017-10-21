package com.comp30022.helium.strawberry.components.ar;

import android.view.View;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.lang.reflect.Field;

import static junit.framework.Assert.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ARBanner.class)
public class ARBannerTest {

    private ARBanner arBanner;
    private ArgumentCaptor<String> stringCaptor = new ArgumentCaptor<>();
    private static final String DUMMY_USERNAME = "ASD";
    private static final double DUMMY_DISTANCE = 100;
    private static final String DUMMY_UNIT = "m";
    private static final String DESTINATION_MESSAGE = "You have arrived at "
            + DUMMY_USERNAME + "'s location!";
    private static final String DISTANCE_MESSAGE =
            String.format("%.2f%s away from %s", DUMMY_DISTANCE, DUMMY_UNIT, DUMMY_USERNAME);

    @Before
    public void setUp() throws Exception {
        TextView textView = mock(TextView.class);
        doNothing().when(textView).setText(this.stringCaptor.capture());
        doNothing().when(textView).setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        this.arBanner = new ARBanner(textView);
    }

    @After
    public void tearDown() throws Exception {
        this.arBanner = null;
        this.stringCaptor = new ArgumentCaptor<>();
    }

    /* The first few tests the logic in text precedence. It follows that:
    *       1) Always show YOU'VE ARRIVED text even if sensor is bad, or if no user is tapped
    *       2) Show either:
    *           a) noTappedUser
    *           b) bad sensor
    *          with equal precedence in first come first serve basis.
    *       3) Display distance. Clearly, we only show distance if there's a user tapped
    *       AND if the sensor's accuracy is good.
    *
    *  Observing the numbering above, the highest precedence text to lowest goes as such
    *
    *  (1) -> (2 a/b) -> (3)
    * */

    /* =========================== BEGIN TEXT PRECEDENCE TEST =============================== */

    /*  ---  HIGHEST PRECEDENCE!! (1) ---- */

    /** SECTION 1, test setting bad sensor / no tapped user BEFORE setting destination **/
    @Test
    public void arrivedLocation_badSensorTest_pre() throws Exception {
        // now, fake bad sensor
        this.arBanner.badSensorDisplay();

        // start test
        this.arBanner.arrivedLocation(DUMMY_USERNAME);
        // there should only be two arguments in our setText call.
        assertEquals(2, this.stringCaptor.getAllValues().size());


        // IT MUST SAY you've arrived at ...
        assertEquals(DESTINATION_MESSAGE,
                this.stringCaptor.getAllValues().get(1));
    }

    @Test
    public void arrivedLocation_noTappedTest_pre() throws Exception {
        // now, fake no user
        this.arBanner.noTappedUserDisplay();

        // start test
        this.arBanner.arrivedLocation(DUMMY_USERNAME);
        // there should only be two arguments in our setText call.
        assertEquals(2, this.stringCaptor.getAllValues().size());


        // IT MUST SAY you've arrived at ...
        assertEquals(DESTINATION_MESSAGE,
                this.stringCaptor.getAllValues().get(1));
    }

    @Test
    public void arrivedLocation_noTappedBadSensorTest_pre() throws Exception {
        // now, fake bad sensor and NO user
        this.arBanner.badSensorDisplay();
        this.arBanner.noTappedUserDisplay();

        // start test
        this.arBanner.arrivedLocation(DUMMY_USERNAME);
        // there should only be two arguments in our setText call.
        assertEquals(2, this.stringCaptor.getAllValues().size());


        // IT MUST SAY you've arrived at ...
        assertEquals(DESTINATION_MESSAGE,
                this.stringCaptor.getAllValues().get(1));
    }

    /** SECTION 2, test setting bad sensor / no tapped user AFTER setting destination **/
    @Test
    public void arrivedLocation_badSensorTest_post() throws Exception {

        // start test
        this.arBanner.arrivedLocation(DUMMY_USERNAME);
        // there should only be two arguments in our setText call.
        assertEquals(1, this.stringCaptor.getAllValues().size());

        // now, fake bad sensor
        this.arBanner.badSensorDisplay();
        // assert that .setText isn't called in ARBanner because of precedence
        assertEquals(1, stringCaptor.getAllValues().size());

        // IT MUST SAY you've arrived at ...
        assertEquals(DESTINATION_MESSAGE,
                this.stringCaptor.getAllValues().get(0));
    }

    @Test
    public void arrivedLocation_noTappedTest_post() throws Exception {

        // start test
        this.arBanner.arrivedLocation(DUMMY_USERNAME);
        // there should only be two arguments in our setText call.
        assertEquals(1, this.stringCaptor.getAllValues().size());

        // now, fake no user
        this.arBanner.noTappedUserDisplay();
        // assert that .setText isn't called in ARBanner because of precedence
        assertEquals(1, stringCaptor.getAllValues().size());

        // IT MUST SAY you've arrived at ...
        assertEquals(DESTINATION_MESSAGE,
                this.stringCaptor.getAllValues().get(0));
    }

    @Test
    public void arrivedLocation_noTappedBadSensorTest_post() throws Exception {

        // start test
        this.arBanner.arrivedLocation(DUMMY_USERNAME);
        // there should only be two arguments in our setText call.
        assertEquals(1, this.stringCaptor.getAllValues().size());

        // now, fake bad sensor and NO user
        this.arBanner.badSensorDisplay();
        this.arBanner.noTappedUserDisplay();
        // assert that .setText isn't called in ARBanner because of precedence
        assertEquals(1, stringCaptor.getAllValues().size());

        // IT MUST SAY you've arrived at ...
        assertEquals(DESTINATION_MESSAGE,
                this.stringCaptor.getAllValues().get(0));
    }

    @Test
    public void arrivedLocation_generalDispReqeust() throws Exception {

        // start test
        this.arBanner.arrivedLocation(DUMMY_USERNAME);
        // there should only be two arguments in our setText call.
        assertEquals(1, this.stringCaptor.getAllValues().size());

        // now, fake bad sensor and NO user
        this.arBanner.badSensorDisplay();
        this.arBanner.noTappedUserDisplay();
        // assert that .setText isn't called in ARBanner because of precedence
        assertEquals(1, stringCaptor.getAllValues().size());


        // fake even normal display requests
        this.arBanner.display(DUMMY_USERNAME);
        // assert that .setText isn't called in ARBanner because of precedence
        assertEquals(1, stringCaptor.getAllValues().size());

        // IT MUST SAY you've arrived at ...
        assertEquals(DESTINATION_MESSAGE,
                this.stringCaptor.getAllValues().get(0));
    }

    /* --- HIGHEST PRECEDENCE (1) test done --- */



    /*  --- Second highest precedence (2) -- shared between badSensor and noUserTapped (FSFC basis)  --- */
    @Test
    public void noTappedUserDisplay_first() throws Exception {
        // we first say no tapped user, now we expect to see that the display says no tapped user
        this.arBanner.noTappedUserDisplay();
        assertEquals(1, stringCaptor.getAllValues().size());

        // we then try to make it say bad sensor, but since no user is selected, it says no user
        // selected due to FCFS behaviour of same precedence.
        this.arBanner.badSensorDisplay();
        assertEquals(1, stringCaptor.getAllValues().size());

        assertEquals(ARBanner.NO_TAPPED_USER_MSG, stringCaptor.getValue());
    }

    @Test
    public void noTappedUserDisplay_second() throws Exception {
        // we first say bad sensor, now we expect to see that the display says bad sensor
        this.arBanner.badSensorDisplay();
        assertEquals(1, stringCaptor.getAllValues().size());

        // we then try to make it say no tapped user, but since there's a bad sensor, it will say
        // bad sensor due to FCFS behaviour of same precedence.
        this.arBanner.noTappedUserDisplay();
        assertEquals(1, stringCaptor.getAllValues().size());

        assertEquals(ARBanner.BAD_SENSOR_MSG, stringCaptor.getValue());
    }

    @Test
    public void noTappedUserDisplay_generalDispReqaust() throws Exception {
        // we first say no tapped user, now we expect to see that the display says no tapped user
        this.arBanner.noTappedUserDisplay();
        assertEquals(1, stringCaptor.getAllValues().size());

        // we then try to make it say bad sensor, but since no user is selected, it says no user
        // selected due to FCFS behaviour of same precedence.
        this.arBanner.badSensorDisplay();
        assertEquals(1, stringCaptor.getAllValues().size());
        // add on more! make it display something random too
        this.arBanner.display(DUMMY_USERNAME);
        assertEquals(1, stringCaptor.getAllValues().size());

        assertEquals(ARBanner.NO_TAPPED_USER_MSG, stringCaptor.getValue());
    }

    /**
     * XXX: duplicate logic of displayDistanceFormatted_noUserTapped
     * @throws Exception
     */
    @Test
    public void noTappedUserDisplay_distanceOverride() throws Exception {
        // we first say no tapped user, now we expect to see that the display says no tapped user
        this.arBanner.noTappedUserDisplay();
        assertEquals(1, stringCaptor.getAllValues().size());

        // but if we say that there's a distance, then there's no way there's no user tapped.
        this.arBanner.displayDistanceFormatted(DUMMY_DISTANCE, DUMMY_UNIT, DUMMY_USERNAME);
        assertEquals(2, stringCaptor.getAllValues().size());

        assertEquals(DISTANCE_MESSAGE, stringCaptor.getValue());
    }


    /**
     * XXX: Duplicate of noTappedUser_second() -- uses same logic
     *
     * But this time, we try calling it twice/thrice/... and it should still hold
     * @throws Exception exception
     */
    @Test
    public void badSensorDisplay_first() throws Exception {
        for (int i = 0; i < 10; ++i) {
            // we first say bad sensor, now we expect to see that the display says bad sensor
            this.arBanner.badSensorDisplay();
            assertEquals(1, stringCaptor.getAllValues().size());
        }

        // we then try to make it say no tapped user, but since there's a bad sensor, it will say
        // bad sensor due to FCFS behaviour of same precedence.
        this.arBanner.noTappedUserDisplay();
        assertEquals(1, stringCaptor.getAllValues().size());

        assertEquals(ARBanner.BAD_SENSOR_MSG, stringCaptor.getValue());

    }

    /**
     * If no user is tapped, but now someone requested to display distance, this
     * IMPLICITLY MEANS THAT WE'VE ALREADY GOT A TAPPED USER
     *
     * otherwise, we wouldn't have a user's distance to begin with!!!
     *
     * XXX: duplicate of noTappedUser_first() -- uses same logic
     *
     * But this time, we try calling it twice/thrice/... and it should still hold
     * @throws Exception exception
     */
    @Test
    public void badSensorDisplay_second() throws Exception {
        for (int i = 0; i < 10; ++i) {
            // we first say no tapped user, now we expect to see that the display says no tapped user
            this.arBanner.noTappedUserDisplay();
            assertEquals(1, stringCaptor.getAllValues().size());
        }

        // we then try to make it say bad sensor, but since no user is selected, it says no user
        // selected due to FCFS behaviour of same precedence.
        this.arBanner.badSensorDisplay();
        assertEquals(1, stringCaptor.getAllValues().size());

        assertEquals(ARBanner.NO_TAPPED_USER_MSG, stringCaptor.getValue());
    }

    @Test
    public void badSensor_generalDisplayRequest() throws Exception {
        // we first say bad sensor, now we expect to see that the display says bad sensor
        this.arBanner.badSensorDisplay();
        assertEquals(1, stringCaptor.getAllValues().size());

        this.arBanner.display(DUMMY_USERNAME);
        // bad sensor display takes precedence
        assertEquals(1, stringCaptor.getAllValues().size());

        assertEquals(ARBanner.BAD_SENSOR_MSG, stringCaptor.getValue());
    }
    /* --- END Second highest precedence (2) -- shared between badSensor and noUserTapped (FSFC basis) --- */





    /* ---   LOWEST PRECEDENCE!!! (3)   --- */

    @Test
    public void displayDistanceFormatted_sensorOverride() throws Exception {
        // we first say bad sensor, now we expect to see that the display says bad sensor
        this.arBanner.badSensorDisplay();
        assertEquals(1, stringCaptor.getAllValues().size());

        this.arBanner.displayDistanceFormatted(DUMMY_DISTANCE, "m", DUMMY_USERNAME);
        // sensor should still take precedence!
        assertEquals(1, stringCaptor.getAllValues().size());

        assertEquals(ARBanner.BAD_SENSOR_MSG, stringCaptor.getValue());

    }

    /**
     * If no user is tapped, but now someone requested to display distance, this
     * IMPLICITLY MEANS THAT WE'VE ALREADY GOT A TAPPED USER
     *
     * otherwise, we wouldn't have a user's distance to begin with!!!
     *
     * XXX: Duplicate logic test of noTappedUserDisplay_distanceOverride
     *
     */
    @Test
    public void displayDistanceFormatted_noUserTapped() throws Exception {
        // say no user is tapped
        this.arBanner.noTappedUserDisplay();
        assertEquals(1, stringCaptor.getAllValues().size());

        this.arBanner.displayDistanceFormatted(DUMMY_DISTANCE, "m", DUMMY_USERNAME);
        // by saying we have distance, we're saying that we've got a tapped user now!
        assertEquals(2, stringCaptor.getAllValues().size());

        assertEquals(DISTANCE_MESSAGE, stringCaptor.getValue());
    }

    /**
     * Finally, a positive test for success
     */
    @Test
    public void displayDistanceFormatted() throws Exception {
        this.arBanner.displayDistanceFormatted(DUMMY_DISTANCE, DUMMY_UNIT, DUMMY_USERNAME);
        assertEquals(1, stringCaptor.getAllValues().size());

        assertEquals(DISTANCE_MESSAGE, stringCaptor.getValue());
    }

    /**
     * Display vs display Distance formatted should compete FCFS.
     * Finally, a positive test for success, overriding a normal display request
     */
    @Test
    public void displayDistanceFormatted_overrideRegularDispRequest() throws Exception {
        this.arBanner.display(DUMMY_USERNAME);
        assertEquals(1, stringCaptor.getAllValues().size());


        this.arBanner.displayDistanceFormatted(DUMMY_DISTANCE, DUMMY_UNIT, DUMMY_USERNAME);
        // the first one should've went through, now we should see 2 message captured
        assertEquals(2, stringCaptor.getAllValues().size());

        assertEquals(DISTANCE_MESSAGE, stringCaptor.getAllValues().get(1));
    }

    /**
     * Display vs display Distance formatted should compete FCFS.
     */
    @Test
    public void normalDispRequest() throws Exception {
        this.arBanner.displayDistanceFormatted(DUMMY_DISTANCE, DUMMY_UNIT, DUMMY_USERNAME);
        assertEquals(1, stringCaptor.getAllValues().size());

        this.arBanner.display(DUMMY_USERNAME);
        assertEquals(2, stringCaptor.getAllValues().size());

        assertEquals(DUMMY_USERNAME, stringCaptor.getAllValues().get(1));
    }

    /* ---   END LOWEST PRECEDENCE!!! (3)   --- */


    /* SPECIAL CASES TEST **/

    /**
     * this special case tests that if we request to display distance, it can only mean
     * that we have not ARRIVED at our target location.
     *
     * If it was previously said that we've arrived, we need to override that status
     * and display the new distance instead.
     *
     * This case happens when we arrived at location but then moved away again!
     */
    @Test
    public void displayDistance_imply_notArrived() throws Exception {
        // begin walking simulation

        // 1. walking towards target with good sensor, tapped user (duh), and distance message
       this.arBanner.displayDistanceFormatted(DUMMY_DISTANCE, DUMMY_UNIT, DUMMY_USERNAME);
       assertEquals(1, stringCaptor.getAllValues().size());
       assertEquals(DISTANCE_MESSAGE, stringCaptor.getValue());

        // 2. arrived at target
        this.arBanner.arrivedLocation(DUMMY_USERNAME);
        assertEquals(2, stringCaptor.getAllValues().size());
        assertEquals(DESTINATION_MESSAGE, stringCaptor.getAllValues().get(1));

        // 3. moved away from target / target moved away from user
        this.arBanner.displayDistanceFormatted(DUMMY_DISTANCE, DUMMY_UNIT, DUMMY_USERNAME);
        assertEquals(3, stringCaptor.getAllValues().size());
        assertEquals(DISTANCE_MESSAGE, stringCaptor.getAllValues().get(2));

    }

    /* END SPECIAL CASES TEST **/



    /* =========================== END TEXT PRECEDENCE TEST =============================== */



    /**
     * Make sure we correctly revoke bad sensor status when the sensor accuracy has improved!
     * @throws Exception exception
     */
    @Test
    public void revokeBadSensorDisplay() throws Exception {
        // first, make sure we deliberately make it bad
        this.arBanner.badSensorDisplay();
        Field hasBadSensor = getSensorField();
        assertTrue((boolean) hasBadSensor.get(this.arBanner));

        // then, make it good again
        this.arBanner.revokeBadSensorDisplay();
        assertFalse((boolean) hasBadSensor.get(this.arBanner));
    }

    private Field getSensorField() throws Exception {
        Field hasBadSensor = this.arBanner.getClass().getDeclaredField("hasBadSensor");
        hasBadSensor.setAccessible(true);
        return hasBadSensor;
    }

    private Field getTargetField() throws Exception {
        Field noTargetUser = this.arBanner.getClass().getDeclaredField("noTargetUser");
        noTargetUser.setAccessible(true);
        return noTargetUser;
    }

}