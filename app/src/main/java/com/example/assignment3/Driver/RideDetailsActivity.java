package com.example.assignment3.Driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment3.AppData;
import com.example.assignment3.DriverMapActivity;
import com.example.assignment3.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class RideDetailsActivity extends AppCompatActivity {
    private GeoPoint pickPoint;
    private GeoPoint dropPoint;
    private String pickPointName;
    private String dropPointName;

    String driverID = AppData.getInstance().getDriverID();
    String uidCustomer = AppData.getInstance().getUidCustomer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);

        String rideDocumentId = getIntent().getStringExtra("rideDocumentId");

        // Initialize other views and variables

        Button acceptButton = findViewById(R.id.acceptButton);
        Button declineButton = findViewById(R.id.declineButton);

        DocumentReference rideDocRef = FirebaseFirestore.getInstance().collection("Ride").document(rideDocumentId);

        // Get Pick Point and Drop Point
        rideDocRef.get()
                        .addOnSuccessListener(documentSnapshot -> {
                            pickPoint = documentSnapshot.getGeoPoint("PickPoint");
                            dropPoint = documentSnapshot.getGeoPoint("DropPoint");
                            pickPointName = documentSnapshot.getString("pickPointName");
                            dropPointName = documentSnapshot.getString("dropPointName");

                            TextView pickPointNameTextView = findViewById(R.id.pickUpLocationTextView);
                            pickPointNameTextView.setText(pickPointName);
                            TextView dropPointNameTextView = findViewById(R.id.dropOffLocationTextView);
                            dropPointNameTextView.setText(dropPointName);
        })
                                .addOnFailureListener(e ->{
                                    // Handle failure
                                });

        rideDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the information and update UI

                        DocumentReference customerDocRef = FirebaseFirestore.getInstance().collection("Customers").document(uidCustomer);
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
               rideDocRef.update("driverUid", driverID);
               finish();
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RideDetailsActivity.this, DriverMapActivity.class);
                startActivity(intent);
            }
        });
    }
}