package com.example.assignment3.Customer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.assignment3.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileCustomerActivity extends AppCompatActivity {
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_customer);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        // TODO: Test choose image feature
        ShapeableImageView profilePicture = findViewById(R.id.avatarImage);
        MaterialButton updateButton = findViewById(R.id.updateButton);

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

        updateButton.setOnClickListener(v -> {

        });
    }
}