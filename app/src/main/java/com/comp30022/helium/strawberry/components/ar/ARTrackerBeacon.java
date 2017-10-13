package com.comp30022.helium.strawberry.components.ar;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import com.comp30022.helium.strawberry.components.location.LocationService;
import com.comp30022.helium.strawberry.entities.StrawberryCallback;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.entities.exceptions.FacebookIdNotSetException;
import com.comp30022.helium.strawberry.helpers.BitmapHelper;

import java.net.MalformedURLException;

public class ARTrackerBeacon {
    private User user;
    private Location location;
    private Bitmap bigProfilePicture;
    private Bitmap smallprofilePicture;
    private static final String TAG = ARTrackerBeacon.class.getSimpleName();

    public ARTrackerBeacon(User user) {
        this.user = user;
        this.location = LocationService.getInstance().getUserLocation(user);
    }

    public ARTrackerBeacon(ARTrackerBeacon trackerBeacon) {
        this.user = trackerBeacon.user;
        this.location = trackerBeacon.location;
    }

    public Location getLocation() {
        return new Location(location);
    }

    public void updateLocation(Location newLocation) {
        this.location = new Location(newLocation);
    }

    public String getUserName() {
        return user.getUsername();
    }

    public Bitmap getProfilePicture(final ARRenderer context, final boolean small) {
        if (small && smallprofilePicture != null) {
            return smallprofilePicture;
        }
        if (!small && bigProfilePicture != null) {
            return bigProfilePicture;
        }
        StrawberryCallback<Bitmap> callback = new StrawberryCallback<Bitmap>() {
            @Override
            public void run(Bitmap bitmap) {
                if (small) {
                    smallprofilePicture = BitmapHelper.makeCircular(bitmap);
                } else {
                    bigProfilePicture = BitmapHelper.makeCircular(bitmap);
                }
                context.getArActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // redraw the image since we've just received the profile picture
                        context.invalidate();
                    }
                });
            }
        };
        try {
            User.ProfilePictureType profilePictureSize = small ? User.ProfilePictureType.SMALL :
                    User.ProfilePictureType.LARGE;
            user.getFbPicture(profilePictureSize, callback);
        } catch (FacebookIdNotSetException | MalformedURLException e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }


    public User getUser() {
        return user;
    }
}
