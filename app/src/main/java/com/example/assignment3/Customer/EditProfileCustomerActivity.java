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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.assignment3.Class.Customer;
import com.example.assignment3.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
        //passwordInput = (EditText) findViewById(R.id.passwordInput);
        spinnerGender = findViewById(R.id.spinnerGender);

        updateButton = (Button) findViewById(R.id.updateButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        profilePicture = findViewById(R.id.avatarImage);

        // Set values for gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_types, R.layout.spinner_item_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                // Retrieve the selected gender
                selectedGender = getResources().getStringArray(
                        R.array.gender_options)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected
            }
        });

        customerRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Retrieve the profileImageUri from the document
                    String profileImageUri = document.getString("profileImageUri");

                    // Now you can use profileImageUri to load the image with Glide or any other library
                    if (profileImageUri != null) {
                        Glide.with(this)
                                .load(profileImageUri)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(profilePicture);
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        // Set old information of the customer
        database.collection("Customers").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Customer customer = document.toObject(Customer.class);
                    assert customer != null;

                    nameInput.setText(customer.getName());
                    emailInput.setText(customer.getEmail());
                    phoneInput.setText(customer.getPhone());
                    addressInput.setText(customer.getAddress());

                    // Check if the gender field is null or empty
                    if (customer.getGender() != null && !customer.getGender().isEmpty()) {
                        int position = adapter.getPosition(customer.getGender());
                        spinnerGender.setSelection(position);
                    } else {
                        // Set a default value for the spinner
                        int defaultPosition = adapter.getPosition(
                                "Gender..."); // Change this to the default gender value
                        if (defaultPosition >= 0) {
                            spinnerGender.setSelection(defaultPosition);
                        } else {
                            // If "Gender..." is not found, set the default to the first item in the adapter
                            spinnerGender.setSelection(0);
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        // Set interaction for update button
        updateButton.setOnClickListener(v -> {
            // Get all texts from edit texts
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();
            String phone = phoneInput.getText().toString();
            String address = addressInput.getText().toString();
            selectedGender = spinnerGender.getSelectedItem().toString();
            //String password = passwordInput.getText().toString();

            // Create an alert dialog to confirm
            new AlertDialog.Builder(this).setTitle("Confirm save")
                    .setMessage("Are you sure you want to save these changes?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        user.verifyBeforeUpdateEmail(email)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "User email address updated.");
                                    } else {
                                        Log.w(TAG, "Failed to update email.",
                                                task1.getException());
                                    }
                                });
                        update.put("name", name);
                        update.put("phone", phone);
                        update.put("address", address);
                        update.put("gender", selectedGender);
                        customerRef.update(update);
                        uploadImageToStorageAndFirestore(imageUri, customerRef);
                    }).setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert).show();

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
                                Toast.makeText(this, "Image URI is null!", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else {
                            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error handling image selection result",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        profilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(Intent.createChooser(intent, "Choose an Image"));
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void uploadImageToStorageAndFirestore(Uri imageUri, DocumentReference customerRef) {
        // Check if an image is selected
        if (imageUri != null) {
            // Initialize Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Create a reference to the location where you want to store the image
            // Using the user's UID as a unique identifier for the image
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference imageRef = storageRef.child("profile_images/" + uid + "/profile.jpg");

            // Delete the older image and upload the new one
            deleteOldImageAndUploadNew(imageUri, imageRef, customerRef);
        } else {
            // No new image selected, update Firestore with the existing image URI
            updateExistingImageUriInFirestore(customerRef);
        }
    }

    private void updateImageUriInFirestore(String newImageUri, DocumentReference customerRef) {
        customerRef.update("profileImageUri", newImageUri)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    Toast.makeText(EditProfileCustomerActivity.this,
                            "Image URI updated in Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(EditProfileCustomerActivity.this,
                            "Failed to update image URI in Firestore", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadNewImage(Uri imageUri, StorageReference imageRef,
                                DocumentReference customerRef) {
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
                                Toast.makeText(EditProfileCustomerActivity.this,
                                        "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle failure to upload image
                    Toast.makeText(EditProfileCustomerActivity.this, "Failed to upload image",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteOldImageAndUploadNew(Uri imageUri, StorageReference imageRef, DocumentReference customerRef) {
        // Check if an older image exists
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Older image exists, delete it
            imageRef.delete().addOnSuccessListener(aVoid -> {
                // Upload the new image after deleting the older one
                uploadNewImage(imageUri, imageRef, customerRef);
            }).addOnFailureListener(e -> {
                // Handle failure to delete the older image
                Toast.makeText(EditProfileCustomerActivity.this, "Failed to delete the older image",
                        Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            // No older image exists, upload the new image directly
            uploadNewImage(imageUri, imageRef, customerRef);
        });
    }

    private void updateExistingImageUriInFirestore(DocumentReference customerRef) {
        // Get the existing image URI from Firestore and update it
        customerRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String existingImageUri = document.getString("profileImageUri");
                    if (existingImageUri != null) {
                        updateImageUriInFirestore(existingImageUri, customerRef);
                    }
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }
}