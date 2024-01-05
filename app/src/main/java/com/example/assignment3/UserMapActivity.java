package com.example.assignment3;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class UserMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                LatLng location = new LatLng(0, 0); // Set the initial location
                googleMap.addMarker(new MarkerOptions().position(location).title("Marker"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            });
        } else {
            Log.e("MapFragment", "Map fragment is null");
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        createLocationCallback();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                .setIntervalMillis(5000) // Update interval in milliseconds
                .setMaxUpdates(3000) // Fastest update interval in milliseconds
                .build();
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Handle the location update
                    updateMapLocation(location);
                }
            }
        };
    }

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            // Request the location permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startLocationUpdates();
            } else {
                // Permission denied, handle accordingly (e.g., show a message or disable location-related features)
                Log.e("Location", "Location permission denied");
            }
        }
    }


    private void updateMapLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear(); // Clear existing markers
        mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f)); // Zoom to the current location
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Your map-related code
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}


