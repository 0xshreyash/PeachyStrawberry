package com.comp30022.helium.strawberry.components.map;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import com.comp30022.helium.strawberry.R;

import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.activities.fragments.MapFragment;
import com.comp30022.helium.strawberry.components.map.exceptions.NoSuchMarkerException;
import com.comp30022.helium.strawberry.helpers.FetchUrl;
import com.comp30022.helium.strawberry.components.location.PathParserTask;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by noxm on 16/09/17.
 */

public class StrawberryMap {
    private static final String TAG = "StrawberryMap";
    private GoogleMap googleMap;
    private String mode;
    private MapFragment mapFragment;

    private Map<String, Marker> markers;
    private Map<String, Polyline> paths;

    public StrawberryMap(GoogleMap googleMap, MapFragment mapFragment) {
        this.googleMap = googleMap;
        this.mapFragment = mapFragment;
        this.markers = new HashMap<>();
        this.paths = new HashMap<>();
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for(String key: markers.keySet()) {
                    if(markers.get(key).getId().equals(marker.getId())) {
                        StrawberryApplication.setString(StrawberryApplication.SELECTED_USER_TAG, key);
                        break;
                    }
                }
                return false;
            }
        });

        String savedTransport = StrawberryApplication.getString(StrawberryApplication.SELECTED_TRANSPORT_TAG);
        if(savedTransport == null)
            setMode("transit");
        else
            setMode(savedTransport);
    }

    public boolean updatePath(String markerName1, String markerName2) {
        if(!mapFragment.isAdded()) {
            Log.e(TAG, "Map fragment is not attached to Activity!");
            return false;
        }
        // Checks, whether start and end locations are captured
        Marker marker1 = markers.get(markerName1);
        Marker marker2 = markers.get(markerName2);

        if(marker1 == null || marker2 == null) {
            return false;
        }

        LatLng origin = marker1.getPosition();
        LatLng dest = marker2.getPosition();

        if (origin == null)
            throw new NoSuchMarkerException(markerName1);
        if (dest == null)
            throw new NoSuchMarkerException(markerName1);

        Log.d(TAG, "Updating path from " + markerName1 + origin + " to " + markerName2 + dest);

        // Getting URL to the Google Directions API
        String pathName = markerName1 + markerName2;

        // Path parser task converts String -> Polyline
        PathParserTask pathParserTask = new PathParserTask(pathName, this);
        // FetchUrl simply downloads something from a url and puts as String
        FetchUrl fetchUrl = new FetchUrl(pathParserTask);

        // Start downloading json data from Google Directions API
        String url = getPathDownloadUrl(origin, dest);
        fetchUrl.execute(url);

//        List<Location> locations = new ArrayList<>();
//        Location originLoc = new Location("");
//        originLoc.setLongitude(origin.longitude);
//        originLoc.setLatitude(origin.latitude);
//
//        Location destLoc = new Location("");
//        destLoc.setLongitude(dest.longitude);
//        destLoc.setLatitude(dest.latitude);
//
//        // only move to current user's location
//        locations.add(originLoc);
////        locations.add(destLoc);
//        moveCamera(originLoc, 17);

        return true;
    }

    public void updateMarker(String markerName, String title, Location location) {
        LatLng curr;

        if (location != null)
            curr = new LatLng(location.getLatitude(), location.getLongitude());
        else
            curr = null;

        Marker lastMarker = markers.get(markerName);

        if (curr == null && lastMarker != null) {
            lastMarker.remove();
        }

        assert curr != null;

        if (lastMarker == null) {
            Marker marker = googleMap.addMarker(new MarkerOptions().position(curr).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_circle)));
            markers.put(markerName, marker);
        } else {
            lastMarker.setPosition(curr);
        }
    }

    public float getCurrentZoom() {
        return googleMap.getCameraPosition().zoom;
    }

    public void setCameraLocation(Location location, float zoom) {
        CameraPosition cp = CameraPosition.fromLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoom);
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cp);
        googleMap.moveCamera(cu);
    }

    public void moveCamera(Location location, float zoom) {
        CameraPosition cp = CameraPosition.fromLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoom);
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cp);
        googleMap.animateCamera(cu);
    }

    public void moveCamera(List<Location> locations, int bound) {
        //move map camera
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Location location : locations) {
            builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, bound);
        googleMap.animateCamera(cu);
    }
    /**
     * Helper for path finder, download path
     *
     * @param origin
     * @param dest
     * @return
     */
    private String getPathDownloadUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + mapFragment.getString(R.string.google_directions_api);
        Log.i(TAG, url);
        return url;
    }

    /**
     * Change transportation mode
     * @param newMode
     */
    public void setMode(String newMode) {
        mode = "&mode=" + newMode;
    }

    public void updatePolyline(String name, PolylineOptions polylineOptions) {
        Polyline path = paths.get(name);

        if (path != null)
            path.remove();

        path = googleMap.addPolyline(polylineOptions);
        paths.put(name, path);
    }

    public void changeText(String name, String value) {
        mapFragment.changeText(name, value);
    }

    public void deleteAllPaths() {
        for(Polyline path : paths.values()) {
            path.remove();
        }
    }

    public Map<String, Marker> getMarkers() {
        return markers;
    }

    /**
     * THis critically crashes if not on main thread
     * @param id
     * @param bitmap
     */
    public void updateMarkerImage(final String id, final Bitmap bitmap) {
        mapFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker marker = markers.get(id);
                if(marker != null) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                }
            }
        });
    }

    public void setInfoWindowAdapter(GoogleMap.InfoWindowAdapter infoWindowAdapter) {
        googleMap.setInfoWindowAdapter(infoWindowAdapter);
    }
}
