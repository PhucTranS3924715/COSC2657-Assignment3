package com.example.assignment3;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.assignment3.Class.Customer;
import com.example.assignment3.Class.Driver;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.assignment3.Customer.HomeFragment;
import com.example.assignment3.Customer.LoginForCustomerActivity;
import com.example.assignment3.Customer.MessageActivity;
import com.example.assignment3.Customer.Rating;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;

import java.util.List;
import java.util.List;

    public class BookingFragment extends Fragment implements OnMapReadyCallback {
        GoogleMap mMap;
        private GeoPoint pickPoint;
        private GeoPoint dropPoint;
        private String pickPointName;
        private String dropPointName;
        private double Price = 0.0;
        private String distanceInKilometers;
        private static final long REFRESH_INTERVAL = 1000; // 1 seconds
        private Marker driverMarker;
        private Marker pickupMarker;
        private Marker destinationMarker;
        private LatLng pickupLocation;
        private LatLng destinationLocation;
        private Handler handler = new Handler();
        private RelativeLayout searchingForDriverSection;
        private RelativeLayout bookingDetailSection;
        private String rideDocumentId;
        private Button cancelBooking;
        String driverID = AppData.getInstance().getDriverID();
        private Customer customer;
        private String uid;
        private Button sosButton;
        private static final String TAG = "BookingFragment";

        public BookingFragment() {
        }

        public static BookingFragment newInstance(String rideDocumentId) {
            BookingFragment fragment = new BookingFragment();
            Bundle args = new Bundle();
            args.putString("rideDocumentId", rideDocumentId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mMap = googleMap;

            // Initialize the driver marker on the map with a default location
            driverMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(0, 0)) // Default position
                    .title("Driver Location"));

            // Fetch the updated driver location from Firestore
            LatLng updatedLocation = getUpdatedDriverLocationFromFirestore();

            // Update the driver marker with the actual location
            if (driverMarker != null) {
                driverMarker.setPosition(updatedLocation);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(updatedLocation, 10f));

            // Retrieve pickup and destination locations from Firestore
            FirebaseFirestore.getInstance().collection("Ride")
                    .document(rideDocumentId) // Assuming rideDocumentId is set correctly
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            pickPoint = documentSnapshot.getGeoPoint("PickPoint");
                            dropPoint = documentSnapshot.getGeoPoint("DropPoint");

                            if (pickPoint != null && dropPoint != null) {
                                pickupLocation = convertToGoogleMapsLatLng(pickPoint);
                                destinationLocation = convertToGoogleMapsLatLng(dropPoint);

                                // Now that pickup and destination locations are retrieved, proceed with the rest of the logic
                                if (!isDriverNearPickup(updatedLocation, pickupLocation)) {
                                    drawRoute(convertToGeoPoint(updatedLocation), convertToGeoPoint(pickupLocation));
                                }

                                if (!isDriverNearDestination(updatedLocation, pickupLocation)) {
                                    drawRoute(convertToGeoPoint(updatedLocation), convertToGeoPoint(destinationLocation));
                                }

                                if (driverMarker != null) {
                                    animateMarker(driverMarker, updatedLocation);
                                }
                            } else {
                                Log.e(TAG, "Pickup or destination points are null");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to fetch pickup and destination locations from Firestore
                        e.printStackTrace();
                    });

            startDriverLocationUpdates();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            View view = inflater.inflate(R.layout.fragment_booking, container, false);

            // Get customer UID
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            assert user != null;
            String uidCustomer = user.getUid();

            getDocumentIdByFieldValue("Ride", "uidCustomer",uidCustomer);

            // Set up Firestore database
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            // Create activity launcher
            ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), result -> {
                    });


//            if (getArguments() != null) {
//                rideDocumentId = getArguments().getString("rideDocumentId");
//            }

            // Initialize your UI elements
            searchingForDriverSection = view.findViewById(R.id.searchingForDriverSection);
            bookingDetailSection = view.findViewById(R.id.bookingDetailSection);

            ImageView messageButton = view.findViewById(R.id.messageButton);
            messageButton.setOnClickListener(v -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Drivers").document(driverID).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Driver driver = document.toObject(Driver.class);
                            // Start the MessageActivity
                            Intent intent = new Intent(getActivity(), MessageActivity.class);
                            intent.putExtra("userID", driverID);
                            intent.putExtra("user", driver);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });

            });

            if (rideDocumentId != null) {
                // Initialize your Firestore reference (adjust the path accordingly)
                DocumentReference rideDocumentRef =
                        database.collection("Ride").document(rideDocumentId);

                rideDocumentRef.get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Retrieve the information and update UI
                                DocumentReference driverDocRef =
                                        FirebaseFirestore.getInstance().collection("Drivers")
                                                .document(driverID);
                                driverDocRef.get()
                                        .addOnSuccessListener(driverSnapshot -> {
                                            if (driverSnapshot.exists()) {
                                                String driverName =
                                                        driverSnapshot.getString("name");
                                                TextView driverNameTextView =
                                                        view.findViewById(R.id.driverName);
                                                driverNameTextView.setText(driverName);
                                                String reputationPoint =
                                                        driverSnapshot.getString("reputationPoint");
                                                TextView pointTextView =
                                                        view.findViewById(R.id.reputationPoint);
                                                pointTextView.setText(reputationPoint);
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure
                                        });

                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                        });

                rideDocumentRef.get()
                        .addOnSuccessListener(documentSnapshot -> {
                            pickPoint = documentSnapshot.getGeoPoint("PickPoint");
                            dropPoint = documentSnapshot.getGeoPoint("DropPoint");
                            pickPointName = documentSnapshot.getString("pickPointName");
                            dropPointName = documentSnapshot.getString("dropPointName");
                            Price = documentSnapshot.getDouble("price");
                            distanceInKilometers = documentSnapshot.getString("distance");
                            TextView pickPointNameTextView =
                                    view.findViewById(R.id.pickUpLocationBooking);
                            pickPointNameTextView.setText(pickPointName);
                            TextView dropPointNameTextView =
                                    view.findViewById(R.id.destinationLocationBooking);
                            dropPointNameTextView.setText(dropPointName);
                            TextView distanceTextView = view.findViewById(R.id.distance);
                            distanceTextView.setText(
                                    getString(R.string.distance_format, distanceInKilometers));
                            TextView priceTextView = view.findViewById(R.id.price);
                            priceTextView.setText(getString(R.string.price_format, (int) Price));
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                        });

                // Add a snapshot listener to the document
                rideDocumentRef.addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Get the current uidDriver from the document
                        String uidDriver = documentSnapshot.getString("uidDriver");

                        // Update UI based on the uidDriver
                        if (uidDriver == null) {
                            // If uidDriver is null, show searchingForDriverSection
                            searchingForDriverSection.setVisibility(View.VISIBLE);
                            bookingDetailSection.setVisibility(View.GONE);
                        } else {
                            // If uidDriver is not null, show bookingDetailSection
                            searchingForDriverSection.setVisibility(View.GONE);
                            bookingDetailSection.setVisibility(View.VISIBLE);
                        }
                    }
                });
            } else {
                Log.e("BookingFragment", "rideDocumentId is null");
            }

            // Cancel booking
            cancelBooking = view.findViewById(R.id.cancelButtonBooking);
            cancelBooking.setOnClickListener(v -> {
                database.collection("Ride").document(rideDocumentId).delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d("BookingFragment", "Ride document deleted successfully");
                            Intent intent1 = new Intent(view.getContext(), HomeFragment.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear all activities in the stack
                            launcher.launch(intent1);
                        }).addOnFailureListener(
                                e -> Log.w("BookingFragment", "Error deleting document", e));
            });

            uid = user.getUid();
            sosButton = view.findViewById(R.id.sosButton);
            sosButton.setOnClickListener(v -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Customers").document(uid).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            customer = document.toObject(Customer.class);
                            sendSOSMessage();
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
            });

            return view;
        }

        private com.google.android.gms.maps.model.LatLng convertToGoogleMapsLatLng(
                GeoPoint geoPoint) {
            double latitude = geoPoint.getLatitude();
            double longitude = geoPoint.getLongitude();
            return new com.google.android.gms.maps.model.LatLng(latitude, longitude);
        }

        private GeoPoint convertToGeoPoint(LatLng latLng) {
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            return new GeoPoint(latitude, longitude);
        }

        // draw direction between two GeoPoint location
        public void drawRoute(GeoPoint pickLocation, GeoPoint dropLocation) {
            // Use the Google Directions API to get the route information
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.api_key)) // Replace with your API key
                    .build();

            LatLng pickupLocation = convertToGoogleMapsLatLng(pickLocation);
            LatLng destinationLocation = convertToGoogleMapsLatLng(dropLocation);

            MarkerOptions pickupMarkerOptions = new MarkerOptions()
                    .position(pickupLocation)
                    .title("Pick-up Location");
            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup));

            MarkerOptions destinationMarkerOptions = new MarkerOptions()
                    .position(destinationLocation)
                    .title("Destination Location");
            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.destination));

            DirectionsResult result;
            try {
                result = DirectionsApi.newRequest(context)
                        .origin(new com.google.maps.model.LatLng(pickupLocation.latitude,
                                pickupLocation.longitude))
                        .destination(new com.google.maps.model.LatLng(destinationLocation.latitude,
                                destinationLocation.longitude))
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
                        polylineOptions.add(new com.google.android.gms.maps.model.LatLng(latLng.lat,
                                latLng.lng));
                    }

                    int polylineColor = ContextCompat.getColor(requireContext(), R.color.red);
                    polylineOptions.color(polylineColor);

                    mMap.addPolyline(polylineOptions);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void getDocumentIdByFieldValue(String collectionPath, String fieldName, String targetValue) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create a query to find the document with the specified field value
            Query query = db.collection(collectionPath).whereEqualTo(fieldName, targetValue);

            query.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                // Retrieve the document ID
                                rideDocumentId = documentSnapshot.getId();
                                // Use the retrieved document ID
                                Log.d("BookingFragment", "Document ID: " + rideDocumentId);
                                return; // Stop after finding the first matching document
                            }

                            // If no matching document is found
                            Log.e("BookingFragment", "No document found with the specified criteria");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the error
                            Log.e("UserMapActivity", "Error getting documents: " + e.getMessage());
                        }
                    });
        }

        private LatLng getUpdatedDriverLocationFromFirestore() {

            // Assuming you have a Firestore reference
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference driverLocationRef = db.collection("Drivers").document(driverID);

            // Fetch the driver's location document from Firestore
            driverLocationRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Get the GeoPoint location data from the document
                            GeoPoint geoPoint = documentSnapshot.getGeoPoint("location");

                            if (geoPoint != null) {
                                // Convert the GeoPoint to LatLng
                                LatLng updatedLocation = convertToGoogleMapsLatLng(geoPoint);

                                // Call the method to smoothly animate the marker
                                animateMarker(driverMarker, updatedLocation);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to fetch the location from Firestore
                        e.printStackTrace();
                    });

            // Return a default location if needed
            return new LatLng(0, 0); // Replace with an appropriate default location
        }

        private void animateMarker(final Marker marker, final LatLng toPosition) {
            final LatLng startPosition = marker.getPosition();
            final long duration = 1000; // Animation duration in milliseconds

            final long start = SystemClock.uptimeMillis();
            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    double lng = t * toPosition.longitude + (1 - t) * startPosition.longitude;
                    double lat = t * toPosition.latitude + (1 - t) * startPosition.latitude;

                    marker.setPosition(new LatLng(lat, lng));

                    if (t < 1.0) {
                        // Post again 16ms later for smooth animation
                        handler.postDelayed(this, 16);
                    }
                }
            });
        }

        private void startDriverLocationUpdates() {
            handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
        }

        private void stopDriverLocationUpdates() {
            // Remove callbacks to stop periodic updates
            handler.removeCallbacks(refreshRunnable);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            // Unregister the callback when the fragment is destroyed
            stopDriverLocationUpdates();
        }

        private boolean isDriverNearPickup(LatLng driverLocation, LatLng pickupLocation) {
            // Calculate the distance between the driver and pickup locations
            float[] distanceResults = new float[1];
            Location.distanceBetween(
                    driverLocation.latitude, driverLocation.longitude,
                    pickupLocation.latitude, pickupLocation.longitude,
                    distanceResults);

            // Check if the distance is less than 50m
            return distanceResults[0] < 50;
        }

        private boolean isDriverNearDestination(LatLng driverLocation, LatLng destinationLocation) {
            // Calculate the distance between the driver and destination locations
            float[] distanceResults = new float[1];
            Location.distanceBetween(
                    driverLocation.latitude, driverLocation.longitude,
                    destinationLocation.latitude, destinationLocation.longitude,
                    distanceResults);

            // Check if the distance is less than 50m
            return distanceResults[0] < 50;
        }

        private Runnable refreshRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    // Create activity launcher
                    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                            new ActivityResultContracts.StartActivityForResult(), result -> {
                            });

                    // Fetch the updated driver location from Firestore
                    LatLng updatedLocation = getUpdatedDriverLocationFromFirestore();

                    MarkerOptions pickupMarkerOptions = new MarkerOptions()
                            .position(pickupLocation)
                            .title("Pick-up Location");
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup));

                    MarkerOptions destinationMarkerOptions = new MarkerOptions()
                            .position(destinationLocation)
                            .title("Destination Location");

                    if (updatedLocation != null && pickupLocation != null && destinationLocation != null) {
                    // Check conditions for drawing routes
                        if (!isDriverNearPickup(updatedLocation, pickupLocation)) {
                            // Draw route from driver's location to pickup point
                            drawRoute(convertToGeoPoint(updatedLocation), convertToGeoPoint(pickupLocation));
                            pickupMarker = mMap.addMarker(pickupMarkerOptions);
                        }else{
                            cancelBooking.setVisibility(View.GONE);
                        }

                        if (!isDriverNearDestination(updatedLocation, pickupLocation)) {
                            // Draw route from driver's location to destination point
                            drawRoute(convertToGeoPoint(updatedLocation), convertToGeoPoint(destinationLocation));
                            destinationMarker = mMap.addMarker(destinationMarkerOptions);
                        }
                        }else {
                            Intent destinationIntent = new Intent(getActivity(), Rating.class);
                            launcher.launch(destinationIntent);
                        }
                        if (driverMarker != null) {
                            // Animate the marker
                            animateMarker(driverMarker, updatedLocation);
                        }
                } catch (Exception e) {
                    // Handle the exception, e.g., log it or take appropriate action
                    e.printStackTrace();
                } finally {
                    // After updating or handling the error, schedule the next refresh
                    handler.postDelayed(this, REFRESH_INTERVAL);
                }
            }
        };

        private void sendSOSMessage() {
            if (!customer.getSosPhone().isEmpty() && !customer.getSosMessage().isEmpty()) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(customer.getSosPhone(), null, customer.getSosMessage(), null, null);
            } else {
                Toast.makeText(getActivity(), "SOS phone number or message is empty", Toast.LENGTH_SHORT).show();
            }
        }

    }