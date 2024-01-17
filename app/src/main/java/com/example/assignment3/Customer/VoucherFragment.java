package com.example.assignment3.Customer;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.assignment3.Admin.UserListAdapter;
import com.example.assignment3.Admin.ViewCustomerDetailActivity;
import com.example.assignment3.Class.Customer;
import com.example.assignment3.Class.Voucher;
import com.example.assignment3.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VoucherFragment extends Fragment {
    public static final String TAG = "VoucherFragment";
    private List<Voucher> vouchers = new ArrayList<>();

    private ListView voucherList;
    private VoucherListAdapter voucherAdapter;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public VoucherFragment() {}

    public static VoucherFragment newInstance(String param1, String param2) {
        VoucherFragment fragment = new VoucherFragment();
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
        View view = inflater.inflate(R.layout.fragment_voucher, container, false);
        voucherList = view.findViewById(R.id.voucherList);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("Vouchers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    vouchers.add(document.toObject(Voucher.class));
                }
                voucherAdapter = new VoucherListAdapter(getActivity(), vouchers);
                voucherList.setAdapter(voucherAdapter);
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });

        // Implement on click listener for item in the list
        voucherList.setOnItemClickListener((parent, view1, position, id) -> {
            Voucher voucher = (Voucher) parent.getItemAtPosition(position);

            // Pass the discount back to the HomeFragment
            Bundle bundle = new Bundle();
            bundle.putDouble("discountedPrice", 1 - voucher.getVoucherDiscount());
            getParentFragmentManager().setFragmentResult("discountedPrice", bundle);

            // Navigate back to the HomeFragment
            ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
            viewPager.setCurrentItem(0);
        });

        return view;
    }
}