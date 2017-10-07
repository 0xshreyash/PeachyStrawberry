package com.comp30022.helium.strawberry.components.ar;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import com.comp30022.helium.strawberry.components.location.LocationService;
import com.comp30022.helium.strawberry.entities.StrawberryCallback;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.entities.exceptions.FacebookIdNotSetException;

import java.net.MalformedURLException;

public class ARTrackerBeacon {
    private User user;
    private Location location;
    private Bitmap profilePicture;
    private static final String TAG = ARTrackerBeacon.class.getSimpleName();

    public ARTrackerBeacon(User user) {
        this.user = user;
//        this.location = LocationService.getInstance().getUserLocation(user);
        // TODO: FINISH DEBUG
        Location uh = new Location("ALSKJD");
        uh.setLongitude(144.960961);
        uh.setLatitude(-37.796927);
        this.location = uh;
    }

    public ARTrackerBeacon(ARTrackerBeacon trackerBeacon) {
        this.user = trackerBeacon.user;
//        this.location = trackerBeacon.location;
        // TODO: FINISH DEBUG
        Location uh = new Location("ALSKJD");
        uh.setLongitude(144.960961);
        uh.setLatitude(-37.796927);
        this.location = uh;
    }

    public Location getLocation() {
        return new Location(location);
    }

    public void updateLocation(Location newLocation) {
        // TODO: FINISH DEBUG
//        this.location = new Location(newLocation);
    }

    public String getUserName() {
        return user.getUsername();
    }

    public Bitmap getProfilePicture(final ARRenderer context) {
        if (profilePicture == null) {
            StrawberryCallback<Bitmap> callback = new StrawberryCallback<Bitmap>() {
                @Override
                public void run(Bitmap bitmap) {
                    profilePicture = bitmap;
                    context.getArActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.invalidate();
                        }
                    });
                }
            };
            try {
                user.getFbPicture(User.ProfilePictureType.LARGE, callback);
            } catch (FacebookIdNotSetException | MalformedURLException e) {
                Log.e(TAG, e.toString());
            }
            return null;
        } else {
            return profilePicture;
        }
    }

    public User getUser() {
        return user;
    }
}
