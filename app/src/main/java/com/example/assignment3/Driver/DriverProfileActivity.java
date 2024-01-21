package com.example.assignment3.Driver;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment3.Class.Customer;
import com.example.assignment3.Class.Driver;
import com.example.assignment3.DriverMapActivity;
import com.example.assignment3.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DriverProfileActivity extends AppCompatActivity {
    private static final String TAG = "DriverProfileActivity";
    private Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });

        TextView driverName = findViewById(R.id.driverName);
        TextView driverRatingScore = findViewById(R.id.driverRatingScore);
        TextView driverIncome = findViewById(R.id.driverIncome);
        TextView driverTotalTrips = findViewById(R.id.driverTotalTrips);
        TextView driverTotalDistance = findViewById(R.id.driverTotalDistance);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();
        uid = "IR2ciX3SKfMGSbIGeqNkDIPfC5H3";

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Drivers").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    driver = document.toObject(Driver.class);

                    // Set driver information
                    driverName.setText(driver.getName());
                    driverRatingScore.setText(String.valueOf(driver.getReputationPoint()));
                    driverIncome.setText(String.valueOf(driver.getEarning()));
                    driverTotalTrips.setText(String.valueOf(driver.getTotalTrips()));
                    driverTotalDistance.setText(String.valueOf(driver.getTotalDistance()));
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        // Create activity launcher
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {});

        ShapeableImageView editIcon = findViewById(R.id.editIcon);
        editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(DriverProfileActivity.this, EditProfileDriverActivity.class);
            launcher.launch(intent);
        });
    }
}