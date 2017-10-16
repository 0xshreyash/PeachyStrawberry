package com.comp30022.helium.strawberry.components.ar;


import android.annotation.SuppressLint;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.helpers.ColourScheme;

public class ARBanner {
    private TextView infoHUD;
    private boolean noTargetUser = false;
    private boolean hasBadSensor = false;

    public ARBanner(TextView infoHUD) {
        this.infoHUD = infoHUD;
        this.infoHUD.setTextColor(StrawberryApplication.getInstance().getResources().getColor(R.color.white));
    }

    public void display(String string) {
        if (overrideText()) {
            return;
        }
        this.infoHUD.setText(string);
    }

    public void display(String string, int format) {
        if (overrideText()) {
            return;
        }
        this.infoHUD.setText(string);
        this.infoHUD.setTextAlignment(format);
    }

    public void noTappedUserDisplay() {
        this.display("Get tap-py - tap a user!");
        this.noTargetUser = true;
    }

    public void badSensorDisplay() {
        this.display(" Sensor low accuracy, follow these steps:\n" +
                "  1. Tilt your phone forward and back\n" +
                "  2. Move it side to side\n"+
                "  3. Tilt left and right\n", TextView.TEXT_ALIGNMENT_TEXT_START);
        this.hasBadSensor = true;
    }

    public void revokeBadSensorDisplay() {
        this.hasBadSensor = false;
    }

    public void displayDistanceFormatted(double distanceTo, String unit, String userName) {
        @SuppressLint("DefaultLocale")
        String formatted = String.format("%.2f%s away from %s", distanceTo,
                unit, userName);
        // the fact that someone asked us to display a distance to someone implies that at long last
        // someone is selected
        if (this.noTargetUser) {
            this.noTargetUser = false;
        }
        this.display(formatted);
    }

    /**
     * arrived location takes precedence over all other messaging logic. we must tell users that
     * they've arrive at their destination
     * @param username User's name
     */
    public void arrivedLocation(String username) {
        this.infoHUD.setText("You have arrived at " + username + "'s location!");
    }

    private boolean overrideText() {
        return this.hasBadSensor || this.noTargetUser;
    }
}

