package com.example.assignment3.Admin;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment3.Class.Driver;
import com.example.assignment3.databinding.ActivityViewDriverDetailBinding;
import com.google.android.material.imageview.ShapeableImageView;

public class ViewDriverDetailActivity extends AppCompatActivity {
    private ActivityViewDriverDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewDriverDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Driver driver = (Driver) getIntent().getSerializableExtra("driver");

        ShapeableImageView profilePicture = binding.driverProfilePicture;
        TextView nameText = binding.nameText;
        TextView phoneText = binding.phoneText;
        TextView emailText = binding.emailText;
        TextView addressText = binding.addressText;
        TextView gender = binding.genderText;
        TextView totalEarningText = binding.earningText;
        TextView totalTripText = binding.totalTripText;
        TextView totalDistanceText = binding.totalDistanceText;
        RatingBar reputationPoint = binding.reputationPoint;
        TextView carModelText = binding.carModelText;
        TextView licensePlate = binding.licensePlateText;

        assert driver != null;
        // TODO: Set driver profile picture
        nameText.setText(driver.getName());
        phoneText.setText(driver.getPhone());
        emailText.setText(driver.getEmail());
        addressText.setText(driver.getAddress());
        gender.setText(driver.getGender());
        totalEarningText.setText(String.valueOf(driver.getEarning()));
        totalTripText.setText(String.valueOf(driver.getTotalTrips()));
        totalDistanceText.setText(String.valueOf(driver.getTotalDistance()));
        reputationPoint.setRating((float) driver.getReputationPoint());
        carModelText.setText(driver.getOwnedCar().getModel());
        licensePlate.setText(driver.getOwnedCar().getLicensePlate());

        ImageView backButton = binding.backButton;
        backButton.setOnClickListener(v -> finish());
    }
}