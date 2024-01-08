package com.example.assignment3.Customer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.assignment3.Class.Customer;
import com.example.assignment3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpForCustomerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_for_customer);

        ImageView backButton = findViewById(R.id.backButton);
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText phoneEditText = findViewById(R.id.phoneEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button signUpButton = findViewById(R.id.signUpButton);

        // Create activity launcher
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {});

        // Implement back button
        backButton.setOnClickListener(v -> finish());

        // Set up Firestore database and Authentication
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Implement sign up button
        signUpButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            boolean isValid = true;
            if (name.isEmpty()) {
                nameEditText.setError("Enter your name");
                isValid = false;
            }
            if (phone.isEmpty()) {
                phoneEditText.setError("Enter your phone");
                isValid = false;
            }
            if (email.isEmpty()) {
                emailEditText.setError("Invalid email address");
                isValid = false;
            }
            if (password.isEmpty()) {
                passwordEditText.setError("Enter your password");
                isValid = false;
            }

            if (isValid) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        assert user != null;
                        String uid = user.getUid();

                        Customer customer = new Customer(name, phone, email);
                        database.collection("Customers").document(uid).set(customer);

                        Intent intent = new Intent(SignUpForCustomerActivity.this, CustomerMainActivity.class);
                        launcher.launch(intent);
                    }
                });
            }
        });
    }
}