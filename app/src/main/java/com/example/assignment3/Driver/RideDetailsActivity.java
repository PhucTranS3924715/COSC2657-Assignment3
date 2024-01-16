package com.example.assignment3.Driver;

import com.example.assignment3.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RideDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);

        String rideDocumentId = getIntent().getStringExtra("rideDocumentId");

        // Initialize other views and variables

        Button acceptButton = findViewById(R.id.acceptButton);
        Button declineButton = findViewById(R.id.declineButton);

        DocumentReference rideDocRef = FirebaseFirestore.getInstance().collection("Ride").document(rideDocumentId);

        rideDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the information and update UI
                        String customerUid = documentSnapshot.getString("uidCustomer");

                        DocumentReference customerDocRef = FirebaseFirestore.getInstance().collection("Customers").document(customerUid);
                        customerDocRef.get()
                                .addOnSuccessListener(customerSnapshot -> {
                                    if (customerSnapshot.exists()) {
                                        // Retrieve customer information and update UI
                                        String customerName = customerSnapshot.getString("name");
                                        TextView customerNameTextView = findViewById(R.id.customerNameTextView);
                                        customerNameTextView.setText(customerName);
                                        String gender = customerSnapshot.getString("gender");
                                        TextView genderTextView = findViewById(R.id.genderTextView);
                                        genderTextView.setText(gender);
                                        String customerPhoneNumber = customerSnapshot.getString("phone");
                                        TextView phoneNumberTextView = findViewById(R.id.phoneNumberTextView);
                                        phoneNumberTextView.setText(customerPhoneNumber);

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

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the logic when the driver accepts the ride
                finish();
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the logic when the driver declines the ride
                finish();
            }
        });
    }
}