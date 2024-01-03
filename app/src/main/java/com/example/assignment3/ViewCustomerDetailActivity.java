package com.example.assignment3;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment3.databinding.ActivityViewCustomerDetailBinding;
import com.google.android.material.imageview.ShapeableImageView;

public class ViewCustomerDetailActivity extends AppCompatActivity {
    private ActivityViewCustomerDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewCustomerDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Customer customer = (Customer) getIntent().getSerializableExtra("customer");

        ShapeableImageView profilePicture = binding.customerProfilePicture;
        TextView nameText = binding.nameText;
        TextView phoneText = binding.phoneText;
        TextView emailText = binding.emailText;
        TextView addressText = binding.addressText;
        TextView gender = binding.genderText;
        TextView rewardPointText = binding.rewardPointsText;

        assert customer != null;
        // TODO: Set customer profile picture
        nameText.setText(customer.getName());
        phoneText.setText(customer.getPhone());
        emailText.setText(customer.getEmail());
        addressText.setText(customer.getAddress());
        gender.setText(customer.getGender());
        rewardPointText.setText(String.valueOf(customer.getRewardPoint()));

        ImageView backButton = binding.backButton;
        backButton.setOnClickListener(v -> finish());
    }
}