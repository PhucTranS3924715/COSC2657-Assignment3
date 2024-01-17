package com.example.assignment3;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookingFragment extends Fragment {

    private RelativeLayout searchingForDriverSection;
    private RelativeLayout bookingDetailSection;

    private DocumentReference rideDocumentRef;

    public BookingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        // Initialize your UI elements
        searchingForDriverSection = view.findViewById(R.id.searchingForDriverSection);
        bookingDetailSection = view.findViewById(R.id.bookingDetailSection);

        // Initialize your Firestore reference (adjust the path accordingly)
        rideDocumentRef = FirebaseFirestore.getInstance().collection("Ride")
                .document("rideDocumentID");

        // Add a snapshot listener to the document
        rideDocumentRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                // Handle errors
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



