package com.example.assignment3.Customer;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.assignment3.Admin.ViewCustomerDetailActivity;
import com.example.assignment3.Class.Customer;
import com.example.assignment3.Class.Ride;
import com.example.assignment3.Class.Voucher;
import com.example.assignment3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActivityFragment extends Fragment {
    public static final String TAG = "ActivityFragment";
    private List<Ride> rides = new ArrayList<>();

    private ListView rideActivityList;
    private RideListAdapter adapter;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ActivityFragment() {
        // Required empty public constructor
    }

    public static ActivityFragment newInstance(String param1, String param2) {
        ActivityFragment fragment = new ActivityFragment();
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
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        rideActivityList = view.findViewById(R.id.rideActivityList);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();
        // How to access the rides list of the current user using Firebase Firestore?
        database.collection("Customers").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Customer customer = document.toObject(Customer.class);
                    assert customer != null;

                    rides = customer.getHistoryRides();

                    adapter = new RideListAdapter(view.getContext(), rides);
                    rideActivityList.setAdapter(adapter);
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        // Create activity launcher
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {});

        // Implement on click listener for item in the list
        rideActivityList.setOnItemClickListener(((parent, view1, position, id) -> {
            Ride ride = (Ride) parent.getItemAtPosition(position);
            // TODO: Implement activity to view trip history
            Intent intent = new Intent();
            intent.putExtra("ride", ride);
            launcher.launch(intent);
        }));

        return view;
    }
}