package com.hawa.android.hawa;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Hawa extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    public GoogleMap mMap;
    private GoogleMap googleMap;
    private List<Address> addresses;
    private GPSTracker mGPS;
    private static final String LOG_TAG = Hawa.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // show error dialog if GooglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_hawa);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
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

        mGPS = new GPSTracker(this);
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            addresses = gcd.getFromLocation(mGPS.getLatitude(), mGPS.getLongitude(), 1);
            if (addresses.size() > 0) {
                Log.v(LOG_TAG, "Location = " + addresses.get(0).getLocality());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mGPS.canGetLocation) {
            mGPS.getLocation();

            Log.v(LOG_TAG, "Lat" + mGPS.getLatitude() + "Lon" + mGPS.getLongitude());
        } else {
            Log.v(LOG_TAG, "Unable to find location.");
        }

        // Add a marker in Sydney and move the camera
        LatLng mLocation = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());
        mMap.addMarker(new MarkerOptions().position(mLocation).title("Marker in Sydney"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mGPS.getLatitude(), mGPS.getLongitude()), 15.0f));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mGPS.getLatitude(), mGPS.getLongitude()), 15.0f));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
