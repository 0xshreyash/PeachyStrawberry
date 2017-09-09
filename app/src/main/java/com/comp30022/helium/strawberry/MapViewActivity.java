package com.comp30022.helium.strawberry;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.comp30022.helium.strawberry.entities.Friend;
import com.comp30022.helium.strawberry.services.LocationService;
import com.comp30022.helium.strawberry.services.MockLocationServices;
import com.comp30022.helium.strawberry.services.NotInstantiatedException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = MapViewActivity.class.getSimpleName();
    private GoogleMap mMap;
    private LocationService mLocationService;
    private ArrayList<Marker> markerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        try {
            mLocationService = LocationService.getInstance();
        } catch (NotInstantiatedException e) {
            Log.d(TAG, e.toString());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MockLocationServices mock = null;
        mock.getInstance();
        Friend friend = null;
        Location currentLocation = mLocationService.getDeviceLocation();
        double lat, lon;
        markerList = new ArrayList<Marker>();

        lat = currentLocation.getLatitude();
        lon = currentLocation.getLongitude();

        LatLng uh = new LatLng(mock.getCoordinate(friend).getY(), mock.getCoordinate(friend).getX());
        Marker dest = mMap.addMarker(new MarkerOptions().position(uh).title("Marker in Union House"));
        markerList.add(dest);

        LatLng curr = new LatLng(lat, lon);
        Marker start = mMap.addMarker(new MarkerOptions().position(curr).title("You are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_circle)));
        markerList.add(start);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        googleMap.animateCamera(cu);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationService.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationService.onPause();
    }
}
