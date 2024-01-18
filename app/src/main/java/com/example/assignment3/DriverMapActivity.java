package com.example.assignment3;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.assignment3.Class.Customer;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FirebaseFirestore firestore;
    private GeoPoint currentLocation;
    private Marker pickupMarker;
    private Marker destinationMarker;
    private static final String TAG = "DriverMapActivity";
    private List<Customer> nearbyCustomers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                LatLng location = new LatLng(0, 0); // Set the initial location
                googleMap.addMarker(new MarkerOptions().position(location).title("Marker"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            });
        } else {
            Log.e(TAG, "Map fragment is null");
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        createLocationCallback();
        currentLocation = getCurrentLocation();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                .setIntervalMillis(2000) // Update interval in milliseconds
                .setMaxUpdates(1000) // Fastest update interval in milliseconds
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

    private String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            Log.e(TAG, "User not authenticated");
            return null;
        }
    }

    private GeoPoint getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request last known location and return the GeoPoint
            Location lastLocation = fusedLocationClient.getLastLocation().getResult();
            if (lastLocation != null) {
                return new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
            } else {
                // Handle the case where last known location is not available
                return null;
            }
        } else {
            // If permission is not granted, return null or handle accordingly
            Log.e("Location", "Location permission denied");
            return null;
        }
    }

    private void updateMapLocation(Location location) {
        String userId = getUserId();

        if (userId != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Update location field in the Customer collection
            updateCustomerLocation(userId, new GeoPoint(location.getLatitude(), location.getLongitude()));

            mMap.clear(); // Clear existing markers
            mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f)); // Zoom to the current location
        } else {
            // Handle the case where the user ID is not available
            Log.e(TAG, "User ID is null");
        }
    }

    private void updateCustomerLocation(String userId, GeoPoint location) {
        // Update location field in the Customer collection
        DocumentReference driverRef = firestore.collection("Drivers").document(userId);

        Map<String, Object> update = new HashMap<>();
        update.put("location", location);

        driverRef.update(update)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Location updated in Drivers collection"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating location in Drivers collection", e));
    }

    public void drawRoute(GeoPoint pickLocation, GeoPoint dropLocation) {
        // Use the Google Directions API to get the route information
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(getString(R.string.api_key)) // Replace with your API key
                .build();

        LatLng pickupLocation = convertToGoogleMapsLatLng(pickLocation);
        LatLng destinationLocation = convertToGoogleMapsLatLng(dropLocation);

        MarkerOptions pickupMarkerOptions = new MarkerOptions()
                .position(pickupLocation)
                .title("Pick-up Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup));

        MarkerOptions destinationMarkerOptions = new MarkerOptions()
                .position(destinationLocation)
                .title("Destination Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination));

        DirectionsResult result;
        try {
            result = DirectionsApi.newRequest(context)
                    .origin(new com.google.maps.model.LatLng(pickupLocation.latitude, pickupLocation.longitude))
                    .destination(new com.google.maps.model.LatLng(destinationLocation.latitude, destinationLocation.longitude))
                    .mode(TravelMode.DRIVING) // You can change the mode based on your requirements
                    .await();

            if (result.routes != null && result.routes.length > 0) {
                // Extract the polyline from the result and add it to the map
                EncodedPolyline points = result.routes[0].overviewPolyline;
                List<com.google.maps.model.LatLng> decodedPath = points.decodePath();

                // Clear previous markers and polylines
                mMap.clear();

                // Add markers for pick-up and destination
                pickupMarker = mMap.addMarker(pickupMarkerOptions);
                destinationMarker = mMap.addMarker(destinationMarkerOptions);

                // Draw the route on the map
                PolylineOptions polylineOptions = new PolylineOptions();
                for (com.google.maps.model.LatLng latLng : decodedPath) {
                    polylineOptions.add(new com.google.android.gms.maps.model.LatLng(latLng.lat, latLng.lng));
                }

                int polylineColor = ContextCompat.getColor(this, R.color.red);
                polylineOptions.color(polylineColor);

                mMap.addPolyline(polylineOptions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Convert Geo Point to LatLng
    private com.google.android.gms.maps.model.LatLng convertToGoogleMapsLatLng(GeoPoint geoPoint) {
        double latitude = geoPoint.getLatitude();
        double longitude = geoPoint.getLongitude();
        return new com.google.android.gms.maps.model.LatLng(latitude, longitude);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request last known location and update the marker
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            updateMapLocation(location); // Update marker immediately
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f)); // Zoom to the current location
                        }
                    });
        } else {
            // If permission is not granted, handle accordingly
            Log.e("Location", "Location permission denied");
        }
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