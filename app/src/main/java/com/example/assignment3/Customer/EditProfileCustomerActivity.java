package com.example.assignment3.Customer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.assignment3.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfileCustomerActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private EditText addressInput;
    private EditText passwordInput;
    private Button updateButton;
    private Button deleteButton;
    private Spinner spinnerGender;
    private ShapeableImageView profilePicture;

    private Uri imageUri;
    private DocumentReference userDocRef;
    private String selectedGender;
    private static final String TAG = "EditProfileCustomer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_customer);

        // Set up Firestore database and Authentication
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        // Create reference for current user
        DocumentReference customerRef = database.collection("Customers").document(uid);
        Map<String, Object> update = new HashMap<>();

        // Initialize edit text
        nameInput = (EditText) findViewById(R.id.nameInput);
        emailInput = (EditText) findViewById(R.id.emailInput);
        phoneInput = (EditText) findViewById(R.id.phoneInput);
        addressInput = (EditText) findViewById(R.id.addressInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        updateButton = (Button) findViewById(R.id.updateButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        spinnerGender = findViewById(R.id.spinnerGender);

         // Set interaction for update button
        updateButton.setOnClickListener(v -> {
                // Get all texts from edit texts
                String name = nameInput.getText().toString();
                String email = emailInput.getText().toString();
                String phone = phoneInput.getText().toString();
                String address = addressInput.getText().toString();
                String password = passwordInput.getText().toString();
            spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // Retrieve the selected gender
                    selectedGender = getResources().getStringArray(R.array.gender_options)[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Do nothing if nothing is selected
                }
            });

                boolean isValid = true;
                if (email.isEmpty()) {
                    emailInput.setError("Invalid email");
                    isValid = false;
                }
                if (password.isEmpty()) {
                    passwordInput.setError("Invalid password");
                    isValid = false;
                }
                if (name.isEmpty()) {
                    nameInput.setError("Invalid name");
                    isValid = false;
                }
                if (phone.isEmpty()) {
                    phoneInput.setError("Invalid phone number");
                    isValid = false;
                }
                if (address.isEmpty()) {
                    addressInput.setError("Invalid ward");
                    isValid = false;
                }

            if (isValid) {
                // Create an alert dialog to confirm
                new AlertDialog.Builder(this).setTitle("Confirm save")
                        .setMessage("Are you sure you want to save these changes?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            user.verifyBeforeUpdateEmail(email)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.d(TAG, "User email address updated.");
                                            Toast.makeText(EditProfileCustomerActivity.this,
                                                    "User email address updated.",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.w(TAG, "Failed to update email.",
                                                    task1.getException());
                                            Toast.makeText(EditProfileCustomerActivity.this,
                                                    "Failed to update email.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            user.updatePassword(password)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Log.d(TAG, "User password updated.");
                                            Toast.makeText(EditProfileCustomerActivity.this,
                                                    "User password updated.",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.w(TAG, "Failed to update password.",
                                                    task2.getException());
                                            Toast.makeText(EditProfileCustomerActivity.this,
                                                    "Failed to update password",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            update.put("name", name);
                            update.put("phone", phone);
                            update.put("address", address);
                            update.put("gender", selectedGender);
                            uploadImageToStorageAndFirestore(imageUri, customerRef);
                        }).setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        });

        // Set interaction for delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerRef.delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error deleting document", e);
                        });

                user.delete();
            }
        });

        // TODO: Test choose image feature
        ShapeableImageView profilePicture = findViewById(R.id.avatarImage);

        // Create activity launcher
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    try {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            imageUri = result.getData().getData();
                            if (imageUri != null) {
                                profilePicture.setBackground(null);
                                profilePicture.setImageDrawable(null); // Clear existing image
                                profilePicture.setImageURI(imageUri);
                            } else {
                                Toast.makeText(this, "Image URI is null!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error handling image selection result", Toast.LENGTH_SHORT).show();
                    }
                });

        profilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(Intent.createChooser(intent, "Choose an Image"));
        });
    }
    private void uploadImageToStorageAndFirestore(Uri imageUri, DocumentReference customerRef) {
        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference to the location where you want to store the image
        // Using the user's UID as a unique identifier for the image
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference imageRef = storageRef.child("profile_images/" + uid + "/profile.jpg");

        // Check if an older image exists
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Older image exists, delete it
            imageRef.delete().addOnSuccessListener(aVoid -> {
                // Upload the new image
                uploadNewImage(imageUri, imageRef, customerRef);
            }).addOnFailureListener(e -> {
                // Handle failure to delete the older image
                Toast.makeText(EditProfileCustomerActivity.this, "Failed to delete the older image", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            // No older image exists, upload the new image directly
            uploadNewImage(imageUri, imageRef, customerRef);
        });
    }

    private void updateImageUriInFirestore(String newImageUri, DocumentReference customerRef) {
        customerRef.update("profileImageUri", newImageUri)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    Toast.makeText(EditProfileCustomerActivity.this, "Image URI updated in Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(EditProfileCustomerActivity.this, "Failed to update image URI in Firestore", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadNewImage(Uri imageUri, StorageReference imageRef, DocumentReference customerRef) {
        // Upload the new image
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, now get the download URL
                    imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // Update Firestore with the download URL
                                updateImageUriInFirestore(uri.toString(), customerRef);
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure to get download URL
                                Toast.makeText(EditProfileCustomerActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle failure to upload image
                    Toast.makeText(EditProfileCustomerActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }
}