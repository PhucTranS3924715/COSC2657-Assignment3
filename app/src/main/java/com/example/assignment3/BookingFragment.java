package com.example.assignment3;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.assignment3.Customer.MessageActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class BookingFragment extends Fragment {
    private GeoPoint pickPoint;
    private GeoPoint dropPoint;
    private String pickPointName;
    private String dropPointName;
    private double Price = 0.0;
    private String distanceInKilometers;

    private RelativeLayout searchingForDriverSection;
    private RelativeLayout bookingDetailSection;
    private String rideDocumentId;
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

        if (getArguments() != null) {
            rideDocumentId = getArguments().getString("rideDocumentId");
        }

        // Initialize your UI elements
        searchingForDriverSection = view.findViewById(R.id.searchingForDriverSection);
        bookingDetailSection = view.findViewById(R.id.bookingDetailSection);

        ImageView messageButton = view.findViewById(R.id.messageButton);

        messageButton.setOnClickListener(v -> {
            // Start the MessageActivity
            Intent intent = new Intent(getActivity(), MessageActivity.class);
            startActivity(intent);
        });

        // Initialize your Firestore reference (adjust the path accordingly)
        DocumentReference rideDocumentRef = FirebaseFirestore.getInstance().collection("Ride")
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

        return view;
    }
}



