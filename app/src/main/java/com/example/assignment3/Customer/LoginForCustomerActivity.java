package com.example.assignment3.Customer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment3.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginForCustomerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_for_customer);

        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button signInButton = findViewById(R.id.signInButton);
        TextView signUpText = findViewById(R.id.signUpText);
        TextView forgotPasswordText = findViewById(R.id.forgotPasswordText);

        // Create activity launcher
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {});

        // Create underline for sign up text
        SpannableString content = new SpannableString(signUpText.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        signUpText.setText(content);

        // Create launcher to move to sign up activity
        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginForCustomerActivity.this, SignUpForCustomerActivity.class);
            launcher.launch(intent);
        });

        // Set up Firebase Authentication
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //Implement Reset password
        forgotPasswordText.setOnClickListener(v -> {
            // Start the MessageActivity
            Intent intent = new Intent(LoginForCustomerActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

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
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {  // If sign in success
                        Intent intent = new Intent(LoginForCustomerActivity.this, CustomerMainActivity.class);
                        launcher.launch(intent);
                    } else {
                        Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}