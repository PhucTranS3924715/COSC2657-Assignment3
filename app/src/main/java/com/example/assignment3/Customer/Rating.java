package com.example.assignment3.Customer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.example.assignment3.R;

public class Rating extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        // Create activity launcher
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {});

        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        Button submitRatingButton = (Button) findViewById(R.id.SubmitRatingButton);
        submitRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() >= 3){
                    Intent intent = new Intent(Rating.this, GoodRating.class);
                    launcher.launch(intent);
                }else {
                    Intent intent = new Intent(Rating.this, BadRating.class);
                    launcher.launch(intent);
                }
            }
        });
    }
}