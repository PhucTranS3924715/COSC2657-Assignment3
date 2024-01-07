package com.example.assignment3.Customer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.assignment3.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileCustomerActivity extends AppCompatActivity {
    private Uri imageUri;
    private static final String TAG = "EditProfileCustomer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_customer);

        // Set up Firestore database and Authentication
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        // Create reference for current user
        DocumentReference customerRef = database.collection("Customers").document(uid);
        Map<String, Object> update = new HashMap<>();

        // Initialize edit text
        EditText nameInput = (EditText) findViewById(R.id.nameInput);
        EditText emailInput = (EditText) findViewById(R.id.emailInput);
        EditText phoneInput = (EditText) findViewById(R.id.phoneInput);
        EditText addressInput = (EditText) findViewById(R.id.addressInput);
        Button updateButton = (Button) findViewById(R.id.updateButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);

         // Set interaction for update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString();
                String email = emailInput.getText().toString();
                String phone = phoneInput.getText().toString();
                String address = addressInput.getText().toString();

                update.put("name", name);
                update.put("email", email);
                update.put("phone", phone);
                update.put("address", address);

                customerRef.update(update)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Profile updated in Customer collection"))
                        .addOnFailureListener(e -> Log.e(TAG, "Error updating profile in Customer collection", e));
            }
        });

        // Set interaction for delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerRef.delete()
                        .addOnSuccessListener(aVoid -> {
                            // Document successfully deleted
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        })
                        .addOnFailureListener(e -> {
                            // Handle any errors
                            Log.e(TAG, "Error deleting document", e);
                        });
            }
        });

        // TODO: Test choose image feature
        ShapeableImageView profilePicture = findViewById(R.id.avatarImage);


        // Create activity launcher
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    try {
                        assert result.getData() != null;
                        imageUri = result.getData().getData();
                        profilePicture.setImageURI(imageUri);
                    } catch (Exception e) {
                        Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
                    }
                });

        profilePicture.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            launcher.launch(Intent.createChooser(intent, "Choose an Image"));
        });
//        updateButton.setOnClickListener(v -> {
//
//        });
    }
}