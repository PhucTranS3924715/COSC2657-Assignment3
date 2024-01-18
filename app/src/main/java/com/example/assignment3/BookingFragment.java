package com.example.assignment3;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.example.assignment3.Customer.MessageActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookingFragment extends Fragment {

    private RelativeLayout searchingForDriverSection;
    private RelativeLayout bookingDetailSection;

    public BookingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

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



