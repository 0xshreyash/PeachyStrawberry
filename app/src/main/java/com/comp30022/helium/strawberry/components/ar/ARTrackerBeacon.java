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
    private Bitmap normalProfilePicture;
    private Bitmap smallprofilePicture;
    private boolean activeSelected;
    private User.ProfilePictureType size;
    private static final String TAG = ARTrackerBeacon.class.getSimpleName();
    private float x, y;
    private boolean loadDone;
    private User.ProfilePictureType pendingSize;

    public ARTrackerBeacon(User user, boolean activeSelected, User.ProfilePictureType size) {
        this.user = user;
        this.location = LocationService.getInstance().getUserLocation(user);
        this.activeSelected = activeSelected;
        this.size = size;
        this.pendingSize = User.ProfilePictureType.SMALL;
        loadProfilePictures();
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

    public Bitmap getProfilePicture() {
        if (size == User.ProfilePictureType.SMALL) {
            return smallprofilePicture;
        }
        if (size == User.ProfilePictureType.LARGE) {
            return bigProfilePicture;
        }
        if (size == User.ProfilePictureType.NORMAL) {
            return normalProfilePicture;
        }
        return null;
    }


    public User getUser() {
        return user;
    }

    public void setXY(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean finishLoading() {
        return this.loadDone;
    }

    public void setSize(User.ProfilePictureType newSize) {
        switch (newSize) {
            case SMALL:
                this.size = User.ProfilePictureType.SMALL;
                break;
            case LARGE:
                this.size = User.ProfilePictureType.LARGE;
                break;
            case NORMAL:
                this.size = User.ProfilePictureType.NORMAL;
                break;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public User.ProfilePictureType getSize() {
        switch (this.size) {
            case SMALL:
                return User.ProfilePictureType.SMALL;
            case LARGE:
                return User.ProfilePictureType.LARGE;
            case NORMAL:
                return User.ProfilePictureType.NORMAL;
            default:
                return null;
        }
    }

    private void loadProfilePictures() {
        StrawberryCallback<Bitmap> callback = new StrawberryCallback<Bitmap>() {
            @Override
            public void run(Bitmap bitmap) {
                if (pendingSize == User.ProfilePictureType.SMALL) {
                    smallprofilePicture = BitmapHelper.makeCircular(bitmap);
                    pendingSize = User.ProfilePictureType.NORMAL;
                    loadProfilePictures();
                } else if (pendingSize== User.ProfilePictureType.NORMAL) {
                    normalProfilePicture = BitmapHelper.makeCircular(bitmap);
                    pendingSize = User.ProfilePictureType.LARGE;
                    loadProfilePictures();
                } else {
                    bigProfilePicture = BitmapHelper.makeCircular(bitmap);
                    loadDone = true;
                }
            }
        };
        try {
            user.getFbPicture(this.pendingSize, callback);
        } catch (FacebookIdNotSetException | MalformedURLException e) {
            Log.e(TAG, e.toString());
        }
    }


}
