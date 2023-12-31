package com.example.assignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment3.databinding.ActivityMessageBinding;

public class MessageActivity extends AppCompatActivity {
    private ActivityMessageBinding binding;
    private Driver currentDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView driverName = binding.driverNameText;
        TextView carType = binding.carTypeText;
        ImageView driverPicture = binding.driverPicture;

        // Get the driver
        currentDriver = (Driver) getIntent().getSerializableExtra("driver");
        assert currentDriver != null;
        driverName.setText(currentDriver.getName());
        carType.setText(currentDriver.getOwnedCar().getModel());
        // TODO: Set driver's picture
    }
}