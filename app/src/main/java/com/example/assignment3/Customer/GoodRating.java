package com.example.assignment3.Customer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.assignment3.R;
import com.google.firebase.auth.FirebaseAuth;

public class GoodRating extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create activity launcher
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {});

        Button doneButton = findViewById(R.id.DoneButton);
        TextView skipButton = findViewById(R.id.textView8);

        doneButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, HomeFragment.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear all activities in the stack
            launcher.launch(intent1);
        });

        skipButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, HomeFragment.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear all activities in the stack
            launcher.launch(intent1);
        });
    }
}