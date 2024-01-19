package com.example.assignment3.Driver;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.assignment3.Customer.CustomerMainActivity;
import com.example.assignment3.Customer.LoginForCustomerActivity;
import com.example.assignment3.DriverMapActivity;
import com.example.assignment3.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginForDriverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_for_driver);

        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button signInButton = findViewById(R.id.signInButton);

        // Create activity launcher
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {});

        // Set up Firebase Authentication
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Implement sign in button
        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            boolean isValid = true;
            if (email.isEmpty()) {
                emailEditText.setError("Invalid email address");
                isValid = false;
            }
            if (password.isEmpty()) {
                passwordEditText.setError("Invalid password");
                isValid = false;
            }

            if (isValid) {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {  // If sign in success
                                Intent intent = new Intent(LoginForDriverActivity.this,
                                        DriverMapActivity.class);
                                launcher.launch(intent);
                            } else {
                                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}