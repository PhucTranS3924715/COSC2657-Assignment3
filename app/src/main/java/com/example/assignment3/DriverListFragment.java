package com.example.assignment3;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DriverListFragment extends Fragment {
    public static final String TAG = "DriverListFragment";
    private ListView driverList;
    private UserListAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DriverListFragment() {}

    // TODO: Rename and change types and number of parameters
    public static DriverListFragment newInstance(String param1, String param2) {
        DriverListFragment fragment = new DriverListFragment();
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
        View view = inflater.inflate(R.layout.fragment_driver_list, container, false);
        driverList = view.findViewById(R.id.driverList);

        // Get the database
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        List<User> drivers = new ArrayList<>();
        database.collection("Drivers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    drivers.add(document.toObject(Driver.class));
                }
                adapter = new UserListAdapter(getActivity(), drivers);
                driverList.setAdapter(adapter);
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });

        return view;
    }
}