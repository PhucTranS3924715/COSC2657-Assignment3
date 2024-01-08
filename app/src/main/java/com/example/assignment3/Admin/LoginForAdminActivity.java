package com.example.assignment3.Admin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.assignment3.Driver.DriverProfileActivity;
import com.example.assignment3.Driver.LoginForDriverActivity;
import com.example.assignment3.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginForAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_for_admin);

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
                // Compare with a pre-defined email and password for admin
                if (email.equals(getString(R.string.admin_username)) && password.equals(getString(R.string.admin_password))) {
                    Intent intent = new Intent(LoginForAdminActivity.this, AdminMainActivity.class);
                    launcher.launch(intent);
                } else {
                    Toast.makeText(this, "Invalid login detail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}