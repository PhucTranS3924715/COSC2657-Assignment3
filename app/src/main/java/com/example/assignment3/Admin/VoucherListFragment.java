package com.example.assignment3.Admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.assignment3.Class.Voucher;
import com.example.assignment3.Customer.VoucherListAdapter;
import com.example.assignment3.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VoucherListFragment extends Fragment {
    public static final String TAG = "VoucherListFragment";

    private List<Voucher> vouchers = new ArrayList<>();
    private VoucherListAdapter voucherAdapter;
    private ListView voucherList;

    private FirebaseFirestore database;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public VoucherListFragment() {
        // Required empty public constructor
    }

    public static VoucherListFragment newInstance(String param1, String param2) {
        VoucherListFragment fragment = new VoucherListFragment();
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
        getVoucherList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voucher_list, container, false);

        voucherList = view.findViewById(R.id.voucherList);

        // Create activity launcher
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == 101) {
                        Toast.makeText(view.getContext(), "Voucher created", Toast.LENGTH_SHORT)
                                .show();
                        getVoucherList();
                    }
                });

        Button addVoucherButton = view.findViewById(R.id.addVoucherButton);
        addVoucherButton.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), CreateVoucherActivity.class);
            launcher.launch(intent);
        });

        return view;
    }

    private void getVoucherList() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("Vouchers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                vouchers.clear(); // Clear the old data
                for (QueryDocumentSnapshot document : task.getResult()) {
                    vouchers.add(document.toObject(Voucher.class));
                }
                voucherAdapter = new VoucherListAdapter(getActivity(), vouchers);
                voucherList.setAdapter(voucherAdapter);
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
    }
}