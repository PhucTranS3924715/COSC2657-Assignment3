package com.example.assignment3.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.assignment3.Class.Car;
import com.example.assignment3.Class.Driver;
import com.example.assignment3.R;
import com.example.assignment3.databinding.ActivityCreateDriverAccountBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateDriverAccountActivity extends AppCompatActivity {
    private ActivityCreateDriverAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateDriverAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ShapeableImageView profilePicture = binding.driverProfilePicture;
        EditText nameText = binding.nameText;
        EditText phoneText = binding.phoneText;
        EditText addressText = binding.addressText;
        EditText emailText = binding.emailText;
        EditText passwordText = binding.passwordText;
        Spinner genderSpinner = binding.genderSpinner;
        EditText carModelText = binding.carModelText;
        EditText licensePlateText = binding.licensePlateText;
        EditText carSeatsText = binding.carSeatsText;
        Button createAccountButton = binding.createAccountButton;

        // Set values for gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_types, R.layout.spinner_text_style);
        adapter.setDropDownViewResource(R.layout.spinner_item_text);
        genderSpinner.setAdapter(adapter);

        // Set up Firestore and Authentication
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // TODO: Create driver's profile picture

        createAccountButton.setOnClickListener(v -> {
            String name = nameText.getText().toString();
            String phone = phoneText.getText().toString();
            String address = addressText.getText().toString();
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            String gender = genderSpinner.getSelectedItem().toString();
            String carModel = carModelText.getText().toString();
            String licensePlate = licensePlateText.getText().toString();
            String carSeats = carSeatsText.getText().toString();

            boolean isValid = true;
            if (name.isEmpty()) {
                nameText.setError("Enter Driver's name");
                isValid = false;
            }
            if (phone.isEmpty()) {
                phoneText.setError("Enter Driver's phone");
                isValid = false;
            }
            if (address.isEmpty()) {
                addressText.setError("Enter Driver's address");
                isValid = false;
            }
            if (email.isEmpty()) {
                emailText.setError("Enter Driver's email");
                isValid = false;
            }
            if (password.isEmpty()) {
                passwordText.setError("Enter Driver's password");
                isValid = false;
            }
            if (gender.equals("")) {
                Toast.makeText(this, "Enter your gender", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (carModel.isEmpty()) {
                carModelText.setError("Enter Driver's car model");
                isValid = false;
            }
            if (licensePlate.isEmpty()) {
                licensePlateText.setError("Enter Driver's car license plate");
                isValid = false;
            }
            if (carSeats.isEmpty() || Integer.parseInt(carSeats) > 7) {
                carSeatsText.setError("Invalid number of seats");
                isValid = false;
            }

            if (isValid) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        assert user != null;
                        String uid = user.getUid();

                        Driver driver = new Driver(name, phone, email, address, gender, new Car(carModel, licensePlate, Integer.parseInt(carSeats), uid));
                        database.collection("Drivers").document(uid).set(driver);
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });

        // Implement back button
        ImageView backButton = binding.backButton;
        backButton.setOnClickListener(v -> finish());
    }
}