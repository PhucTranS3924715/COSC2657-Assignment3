package com.example.assignment3;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.assignment3.Class.Customer;
import com.example.assignment3.Class.Driver;
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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FirebaseFirestore firestore;
    private List<Driver> nearbyDrivers;
    private static ListenerRegistration driversListener;
    private GeoPoint currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        Button btnBooking = findViewById(R.id.btnBooking);

        btnBooking.setOnClickListener(view -> onBookingClicked());
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
            Log.e("MapFragment", "Map fragment is null");
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        createLocationCallback();
        currentLocation = getCurrentLocation();
        nearbyDrivers = findNearbyDrivers(currentLocation, 1);

    }

    //Handle Booking
    // TODO: Add function to type Pick-Up point and Drop off point
    public void onBookingClicked(){
        showLoadingScreen();
        //Get user's current location
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            //Save user's current location to the "Pick-point" field in the "Ride" collection
                            savePickPointToFirestore(location);
                        }
                    });
        } else {
            // Request the location permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void savePickPointToFirestore(Location location) {
        String userId = getUserId();

        if (userId != null) {
            // Save user's current location to Firestore in the "Ride" collection
            DocumentReference rideRef = firestore.collection("Ride").document(userId);

            // Assuming you have a model class for the Ride object with a field "pickPoint" of type GeoPoint
            GeoPoint pickPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

            Map<String, Object> update = new HashMap<>();
            update.put("pickPoint", pickPoint);

            rideRef.set(update)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("UserMapActivity", "Pick-point updated in Ride collection");

                        // Add a marker to the pick-point on the map
                        addMarkerToPickPoint(new LatLng(location.getLatitude(), location.getLongitude()));
                    })
                    .addOnFailureListener(e -> Log.e("UserMapActivity", "Error updating pick-point in Ride collection", e));
        } else {
            // Handle the case where the user ID is not available
            Log.e("UserMapActivity", "User ID is null");
        }
    }

    private void addMarkerToPickPoint(LatLng pickPoint) {
        mMap.clear(); // Clear existing markers
        mMap.addMarker(new MarkerOptions().position(pickPoint).title("Pick Point"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickPoint, 15f)); // Zoom to the pick point
    }

    private void showLoadingScreen() {
        // Hide the bookingDetailSection and show the searchingForDriverSection
        findViewById(R.id.bookingDetailSection).setVisibility(View.GONE);
        findViewById(R.id.searchingForDriverSection).setVisibility(View.VISIBLE);
    }

    //Handle Searching for Driver
    public static List<Driver> findNearbyDrivers(GeoPoint userLocation, double radiusInKm) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference driversRef = db.collection("Drivers");

        TaskCompletionSource<List<Driver>> taskCompletionSource = new TaskCompletionSource<>();

        driversRef.get().addOnCompleteListener(queryTask -> {
            if (queryTask.isSuccessful()) {
                List<Driver> allDrivers = new ArrayList<>();
                QuerySnapshot querySnapshot = queryTask.getResult();

                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        GeoPoint driverLocation = document.getGeoPoint("location");
                        if (driverLocation != null) {
                            Driver driver = new Driver();
                            driver.setLocation(driverLocation);
                            driver.setReputationPoint(document.getLong("reputationPoint").intValue());
                            allDrivers.add(driver);
                        }
                    }

                    // Filter drivers within the specified radius
                    List<Driver> nearbyDrivers = filterDriversByRadius(userLocation, allDrivers, radiusInKm);

                    // Sort the list of nearby drivers based on reputation points
                    Collections.sort(nearbyDrivers, (driver1, driver2) ->
                            Double.compare(driver2.getReputationPoint(), driver1.getReputationPoint()));


                    List<Driver> selectedDrivers = null;

                    if (!nearbyDrivers.isEmpty()) {
                        // If multiple drivers have the same highest reputation, select randomly
                        int highestReputation = (int) nearbyDrivers.get(0).getReputationPoint();
                        List<Driver> highestReputationDrivers = getDriversWithHighestReputation(nearbyDrivers, highestReputation);

                        // Select a driver randomly from the ones with the highest reputation
                        selectedDrivers = getRandomDriver(highestReputationDrivers);
                    }

                    // Complete the task with the selected drivers
                    taskCompletionSource.setResult(selectedDrivers);
                } else {
                    Log.e("UserMapActivity", "QuerySnapshot is null");
                    taskCompletionSource.setException(new Exception("QuerySnapshot is null"));
                }
            } else {
                Log.e("UserMapActivity", "Error getting documents.", queryTask.getException());
                taskCompletionSource.setException(queryTask.getException());
            }
        });

        try {
            Tasks.await(taskCompletionSource.getTask());
        } catch (Exception e) {
            // Handle the exception if any
            Log.e("UserMapActivity", "Error while waiting for result", e);
        }

        return taskCompletionSource.getTask().getResult();
    }


    private static List<Driver> filterDriversByRadius(GeoPoint userLocation, List<Driver> allDrivers, double radiusInKm) {
        List<Driver> nearbyDrivers = new ArrayList<>();
        for (Driver driver : allDrivers) {
            GeoPoint driverLocation = driver.getLocation();
            double distance = calculateDistance(userLocation, driverLocation);

            if (distance <= radiusInKm) {
                nearbyDrivers.add(driver);
            }
        }
        return nearbyDrivers;
    }

    //Get driver with the highest reputation point
    private static List<Driver> getDriversWithHighestReputation(List<Driver> drivers, int highestReputation) {
        List<Driver> highestReputationDrivers = new ArrayList<>();
        for (Driver driver : drivers) {
            if (driver.getReputationPoint() == highestReputation) {
                highestReputationDrivers.add(driver);
            } else {
                // The list is sorted, so break once a driver with lower reputation is encountered
                break;
            }
        }
        return highestReputationDrivers;
    }

    //Get random driver if there are drivers with the same reputation points
    private static List<Driver> getRandomDriver(List<Driver> drivers) {
        List<Driver> randomDrivers = new ArrayList<>();
        if (!drivers.isEmpty()) {
            int randomIndex = new Random().nextInt(drivers.size());
            randomDrivers.add(drivers.get(randomIndex));
        }
        return randomDrivers;
    }

    // Stop listening to Firestore updates when needed
    public static void stopListeningToDrivers() {
        if (driversListener != null) {
            driversListener.remove();
        }
    }

    //Handle user's current location
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
            Log.e("UserMapActivity", "User not authenticated");
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
            Log.e("UserMapActivity", "User ID is null");
        }
    }

    private void updateCustomerLocation(String userId, GeoPoint location) {
        // Update location field in the Customer collection
        DocumentReference customerRef = firestore.collection("Customers").document(userId);

        Map<String, Object> update = new HashMap<>();
        update.put("location", location);

        customerRef.update(update)
                .addOnSuccessListener(aVoid -> Log.d("UserMapActivity", "Location updated in Customer collection"))
                .addOnFailureListener(e -> Log.e("UserMapActivity", "Error updating location in Customer collection", e));
    }

    // Haversine formula to calculate distance between two GeoPoint points
    private static double calculateDistance(GeoPoint point1, GeoPoint point2) {
        double R = 6371; // Earth radius in kilometers

        double lat1 = Math.toRadians(point1.getLatitude());
        double lon1 = Math.toRadians(point1.getLongitude());
        double lat2 = Math.toRadians(point2.getLatitude());
        double lon2 = Math.toRadians(point2.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in kilometers
    }

    // Get current location (store in variable)
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
