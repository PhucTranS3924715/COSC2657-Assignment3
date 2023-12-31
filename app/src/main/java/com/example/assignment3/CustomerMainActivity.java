package com.example.assignment3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomerMainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private static final String TAG = "CustomerMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Set up the ViewPager and adapter
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new HomeFragment();
                    case 1:
                        return new ActivityFragment();
                    case 2:
                        return new VoucherFragment();
                    case 3:
                        return new ProfileFragment();
                    default:
                        return new HomeFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 4;
            }
        };
        viewPager.setAdapter(adapter);

        // Set up the BottomNavigationView
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (id == R.id.activity) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (id == R.id.voucher) {
                viewPager.setCurrentItem(2);
                return true;
            } else if (id == R.id.profile) {
                viewPager.setCurrentItem(3);
                return true;
            }
            return false;
        });

        // Update bottom navigation view when swiping the view pager
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigation.getMenu().getItem(position).setChecked(true);
            }
        });

        // Get current token
        String token = String.valueOf(FirebaseMessaging.getInstance().getToken());
        Log.i(TAG, token);

        // Update current user token in the database
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("Customers").document(uid).update("fcmToken", token);

        Button signoutButton = findViewById(R.id.signoutButton);
        signoutButton.setOnClickListener(v -> {
            database.collection("Customers").document(uid).update("fcmToken", FieldValue.delete())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        FirebaseAuth.getInstance().signOut();
                        finish();
                    }).addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
        });

    }
}