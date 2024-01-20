package com.example.assignment3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.assignment3.Class.Driver;
import com.example.assignment3.Customer.HomeFragment;
import com.example.assignment3.Customer.LoginForCustomerActivity;
import com.example.assignment3.Customer.MessageActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class BookingFragment extends Fragment {
    private static final String TAG = "BookingFragment";
    private GeoPoint pickPoint;
    private GeoPoint dropPoint;
    private String pickPointName;
    private String dropPointName;
    private double Price = 0.0;
    private String distanceInKilometers;

    private RelativeLayout searchingForDriverSection;
    private RelativeLayout bookingDetailSection;
    private String rideDocumentId;
    private Button cancelBooking;
    String driverID = AppData.getInstance().getDriverID();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        // Set up Firestore database
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        // Create activity launcher
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {});

        if (getArguments() != null) {
            rideDocumentId = getArguments().getString("rideDocumentId");
        }

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

        // Initialize your Firestore reference (adjust the path accordingly)
        DocumentReference rideDocumentRef = database.collection("Ride")
                .document(rideDocumentId);

        rideDocumentRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the information and update UI
                        DocumentReference driverDocRef = FirebaseFirestore.getInstance().collection("Drivers").document(driverID);
                        driverDocRef.get()
                                .addOnSuccessListener(driverSnapshot -> {
                                    if (driverSnapshot.exists()) {
                                        String driverName = driverSnapshot.getString("name");
                                        TextView driverNameTextView = view.findViewById(R.id.driverName);
                                        driverNameTextView.setText(driverName);
                                        String reputationPoint = driverSnapshot.getString("reputationPoint");
                                        TextView pointTextView = view.findViewById(R.id.reputationPoint);
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
                    TextView pickPointNameTextView = view.findViewById(R.id.pickUpLocationBooking);
                    pickPointNameTextView.setText(pickPointName);
                    TextView dropPointNameTextView = view.findViewById(R.id.destinationLocationBooking);
                    dropPointNameTextView.setText(dropPointName);
                    TextView distanceTextView = view.findViewById(R.id.distance);
                    distanceTextView.setText(getString(R.string.distance_format, distanceInKilometers));
                    TextView priceTextView = view.findViewById(R.id.price);
                    priceTextView.setText(getString(R.string.price_format, (int) Price));
                })
                .addOnFailureListener(e ->{
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

        // Cancel booking
        cancelBooking = view.findViewById(R.id.cancelButtonBooking);
        cancelBooking.setOnClickListener( v ->{
            database.collection("Ride").document(rideDocumentId).delete()
                    .addOnSuccessListener(aVoid ->{
                        Log.d("BookingFragment", "Ride document deleted successfully");
                        Intent intent1 = new Intent(view.getContext(), HomeFragment.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear all activities in the stack
                        launcher.launch(intent1);
                    }).addOnFailureListener(e -> Log.w("BookingFragment", "Error deleting document", e));
        });
        return view;
    }

    private com.google.android.gms.maps.model.LatLng convertToGoogleMapsLatLng(GeoPoint geoPoint) {
        double latitude = geoPoint.getLatitude();
        double longitude = geoPoint.getLongitude();
        return new com.google.android.gms.maps.model.LatLng(latitude, longitude);
    }

    private GeoPoint convertToGeoPoint(LatLng latLng) {
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;
        return new GeoPoint(latitude, longitude);
    }
}



