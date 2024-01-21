package com.example.assignment3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.assignment3.Class.Customer;
import com.example.assignment3.Class.Driver;
import com.example.assignment3.Customer.HomeFragment;
import com.example.assignment3.Customer.MessageActivity;
import com.example.assignment3.Driver.DriverProfileActivity;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public interface RouteDrawingCallback {
        void onRouteDrawn();
    }

    private static final String CHANNEL_ID = "arrival_notification_channel";
    private static final int NOTIFICATION_ID = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FirebaseFirestore firestore;
    private GeoPoint currentLocation;
    private Marker pickupMarker;
    private Marker destinationMarker;
    private LatLng pickupLocation;
    private LatLng destinationLocation;
    private GeoPoint pickPoint;
    private GeoPoint dropPoint;
    private String pickPointName;
    private String dropPointName;
    private double Price = 0.0;
    private String distanceInKilometers;
    private String rideDocumentId;
    private SwitchCompat switchCompat;
    private TextView statusView;
    private ConstraintLayout bookingDriver;
    private Button cancelRide;
    private boolean isOnline;
    private boolean hasDriverArrived = false;

    private DocumentReference rideDocRef;

    String uidCustomer = AppData.getInstance().getUidCustomer();
    private static final String TAG = "DriverMapActivity";

    public DriverMapActivity(String rideDocumentId) {
        this.rideDocumentId = rideDocumentId;
    }

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private void updateRideLocation(GeoPoint location) {
        // Update location field in the Ride collection
        DocumentReference rideRef = firestore.collection("Ride").document(rideDocumentId);

        Map<String, Object> update = new HashMap<>();
        update.put("DriverLocation", location);

        rideRef.update(update)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Driver location updated in Ride collection"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating driver location in Ride collection", e));
    }

    public DriverMapActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        switchCompat = findViewById(R.id.switchCompat);
        statusView = findViewById(R.id.StatusView);
        bookingDriver = findViewById(R.id.bookingDriver);
        cancelRide = findViewById(R.id.cancelButtonBooking);

        createNotificationChannel();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> handleSwitchToggle(isChecked));

        if (rideDocumentId != null) {
            rideDocRef = FirebaseFirestore.getInstance().collection("Ride").document(rideDocumentId);
            // Listen for changes in the Ride document
            rideDocRef.addSnapshotListener((documentSnapshot, e) -> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.");
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Get the value of uidDriver from the document
                    String uidDriver = documentSnapshot.getString("uidDriver");

                    // Check if uidDriver is not null
                    if (uidDriver != null) {
                        // If uidDriver is not null, hide SwitchCompat and StatusView
                        switchCompat.setVisibility(View.INVISIBLE);
                        statusView.setVisibility(View.INVISIBLE);
                        bookingDriver.setVisibility(View.VISIBLE);

                        // Update the driver's location in the Ride document
                        GeoPoint driverLocation = getCurrentLocation();
                        if (driverLocation != null) {
                            updateRideLocation(driverLocation);

                        }

                        rideDocRef.get()
                                .addOnSuccessListener(rideSnapshot -> {
                                    if (rideSnapshot.exists()) {
                                        // Retrieve the information and update UI
                                        DocumentReference customerDocRef =
                                                FirebaseFirestore.getInstance().collection("Customers")
                                                        .document(uidCustomer);
                                        customerDocRef.get()
                                                .addOnSuccessListener(driverSnapshot -> {
                                                    if (driverSnapshot.exists()) {
                                                        String customerName =
                                                                driverSnapshot.getString("name");
                                                        TextView customerNameTextView = findViewById(R.id.customerName);
                                                        customerNameTextView.setText(customerName);
                                                        String phoneNumber =
                                                                driverSnapshot.getString("phone");
                                                        TextView phoneTextView = findViewById(R.id.CustomerPhoneNumber);
                                                        phoneTextView.setText(phoneNumber);
                                                    }
                                                });
                                    }
                                });

                        rideDocRef.get()
                                .addOnSuccessListener(rideSnapshot -> {
                                    pickPoint = rideSnapshot.getGeoPoint("PickPoint");
                                    dropPoint = documentSnapshot.getGeoPoint("DropPoint");
                                    pickPointName = documentSnapshot.getString("pickPointName");
                                    dropPointName = documentSnapshot.getString("dropPointName");
                                    Price = documentSnapshot.getDouble("price");
                                    distanceInKilometers = documentSnapshot.getString("distance");
                                    TextView pickPointNameTextView = findViewById(R.id.pickUp);
                                    pickPointNameTextView.setText(pickPointName);
                                    TextView dropPointNameTextView = findViewById(R.id.destination);
                                    dropPointNameTextView.setText(dropPointName);
                                    TextView distanceTextView = findViewById(R.id.Tripdistance);
                                    distanceTextView.setText(getString(R.string.distance_format, distanceInKilometers));
                                    TextView priceTextView = findViewById(R.id.Tripprice);
                                    priceTextView.setText(getString(R.string.price_format, (int) Price));
                                });

                        if (rideDocumentId != null && !rideDocumentId.isEmpty()) {
                            drawRouteFromDriverToPickup();
                        }
                    } else {
                        // If uidDriver is null, show SwitchCompat and StatusView
                        switchCompat.setVisibility(View.VISIBLE);
                        statusView.setVisibility(View.VISIBLE);
                        bookingDriver.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }



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

        // Cancel Ride
        cancelRide = findViewById(R.id.cancelButtonBooking);
        cancelRide.setOnClickListener(v -> {
            firestore.collection("Ride").document(rideDocumentId).delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("DriverMapActivty", "Ride document deleted successfully");
                    }).addOnFailureListener(
                            e -> Log.w("DriverMapActivty", "Error deleting document", e));
        });

        // Create activity launcher
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {});

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        createLocationCallback();
        currentLocation = getCurrentLocation();

        ImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(DriverMapActivity.this, DriverProfileActivity.class);
            launcher.launch(intent);
        });

        // Implement message activity
        ImageView messageButton = findViewById(R.id.messageButton);
        messageButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Customers").document(uidCustomer).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Customer customer = document.toObject(Customer.class);
                        // Start the MessageActivity
                        Intent intent = new Intent(DriverMapActivity.this, MessageActivity.class);
                        intent.putExtra("userID", uidCustomer);
                        intent.putExtra("user", customer);
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });

        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Arrival Notification";
            String description = "Notification for driver arrival";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void drawRouteFromDriverToPickup() {
        drawRouteFromDriverToPickup(null);
    }

    // Function to draw route from driver's location to pickup point
    private void drawRouteFromDriverToPickup(RouteDrawingCallback callback) {
        FirebaseFirestore.getInstance().collection("Ride")
                .document(rideDocumentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (pickPoint != null && currentLocation != null) {
                        // Check if the driver has arrived at the pickup point
                        if (!hasDriverArrived && isDriverAtPickup(currentLocation, pickPoint)) {
                            // Driver has arrived, set the flag and draw route to the destination
                            hasDriverArrived = true;
                            cancelRide.setVisibility(View.GONE);
                            drawRoute(currentLocation, dropPoint, (RouteDrawingCallback) () -> {
                                if (callback != null) {
                                    callback.onRouteDrawn();
                                }
                            });
                        } else {
                            // Driver has not arrived, draw route to the pickup point
                            drawRoute(currentLocation, pickPoint, new RouteDrawingCallback() {
                                @Override
                                public void onRouteDrawn() {
                                    if (callback != null) {
                                        callback.onRouteDrawn();
                                    }
                                }
                            });
                        }
                    } else {
                        // Handle non-existent document
                        Log.e(TAG, "Ride document does not exist");
                        if (callback != null) {
                            callback.onRouteDrawn();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle Firestore retrieval failure
                    Log.e(TAG, "Error getting pickup location from Firestore", e);
                    if (callback != null) {
                        callback.onRouteDrawn();
                    }
                });
    }

    private boolean isDriverAtPickup(GeoPoint driverLocation, GeoPoint pickupPoint) {
        return driverLocation.getLatitude() == pickupPoint.getLatitude()
                && driverLocation.getLongitude() == pickupPoint.getLongitude();
    }

    // Function to handle SwitchCompat toggle
    private void handleSwitchToggle(boolean isChecked) {
        // Update the online/offline status
        isOnline = isChecked;

        // Update the "status" field in the "Drivers" collection
        updateDriverStatus(isOnline);
    }

    private void updateDriverStatus(boolean online) {
        String userId = getUserId();

        if (userId != null) {
            // Update location field in the Customer collection
            DocumentReference driverRef = firestore.collection("Drivers").document(userId);

            Map<String, Object> update = new HashMap<>();
            update.put("status", online ? "Online" : "Offline");

            driverRef.update(update)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Status updated in Drivers collection"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating status in Drivers collection", e));
        } else {
            // Handle the case where the user ID is not available
            Log.e(TAG, "User ID is null");
        }
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

            checkArrivalAtDestination();
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

    public void drawRoute(GeoPoint pickLocation, GeoPoint dropLocation, RouteDrawingCallback callback) {
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

            if (callback != null) {
                callback.onRouteDrawn();
            }

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

    private void checkArrivalAtDestination() {
        if (currentLocation != null && destinationLocation != null) {
            if (currentLocation.getLatitude() == destinationLocation.latitude
                    && currentLocation.getLongitude() == destinationLocation.longitude) {
                // Driver has arrived at the destination
                bookingDriver.setVisibility(View.GONE);
                addEarningAndNotify();

            }
        }
    }

    private void addEarningAndNotify() {
        //Salary for Driver
        addEarningToDriver();

        // Display in-app notification
        displayNotification("You have arrived!");
    }

    private void addEarningToDriver() {
        // Update the "earning" field in the "Drivers" collection
        String userId = getUserId();

        if (userId != null) {
            DocumentReference driverRef = firestore.collection("Drivers").document(userId);

            // Retrieve the current earning value
            driverRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    double currentEarning = documentSnapshot.getDouble("earning");

                    // Add the "price" value from the Ride Collection to the current earning
                    double updatedEarning = currentEarning + Price;

                    // Update the "earning" field in the Drivers collection
                    Map<String, Object> update = new HashMap<>();
                    update.put("earning", updatedEarning);

                    driverRef.update(update)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Earning updated in Drivers collection"))
                            .addOnFailureListener(e -> Log.e(TAG, "Error updating earning in Drivers collection", e));
                }
            });
        } else {
            // Handle the case where the user ID is not available
            Log.e(TAG, "User ID is null");
        }
    }

    private void displayNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Arrival Notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
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