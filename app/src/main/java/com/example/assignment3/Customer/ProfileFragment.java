package com.example.assignment3.Customer;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.assignment3.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ShapeableImageView profilePicture;
    private static final long REFRESH_INTERVAL = 5 * 1000; // 5 seconds
    private Handler handler = new Handler();
    private String profileImageUri;

    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // TODO: Create profile UI

        // Create activity launcher
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {});

        // Implement on click listener to launch edit profile activity
        RelativeLayout informationSection = view.findViewById(R.id.informationSection);
        informationSection.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), EditProfileCustomerActivity.class);
            launcher.launch(intent);
        });

        profilePicture = view.findViewById(R.id.avatarImage);
        // Set up Firestore database and Authentication
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        // Create reference for current user
        DocumentReference customerRef = database.collection("Customers").document(uid);

        customerRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Retrieve the profileImageUri from the document
                    profileImageUri = document.getString("profileImageUri");

                    // Now you can use profileImageUri to load the image with Glide or any other library
                    if (profileImageUri != null) {
                        Glide.with(this)
                                .load(profileImageUri)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(profilePicture);
                    }
                } else {
                    Log.d("ProfileFragment", "No such document");
                }
            } else {
                Log.d("ProfileFragment", "get failed with ", task.getException());
            }
        });
        // Sign out button example
        TextView logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            database.collection("Customers").document(uid).update("fcmToken", FieldValue.delete())
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ProfileFragment", "DocumentSnapshot successfully updated!");
                        FirebaseAuth.getInstance().signOut();
                    }).addOnFailureListener(e -> Log.w("ProfileFragment", "Error updating document", e));
        });
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
        return view;
    }
    // Define a Runnable to update the avatar periodically
    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (profileImageUri != null) {
                Glide.with(requireContext())
                        .load(profileImageUri)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(profilePicture);
            }

            // After updating, schedule the next refresh
            handler.postDelayed(this, REFRESH_INTERVAL);
        }
    };
}