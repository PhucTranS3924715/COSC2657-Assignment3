package com.example.assignment3;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DriverListFragment extends Fragment {
    public static final String TAG = "DriverListFragment";
    private List<User> drivers = new ArrayList<>();

    private ListView driverList;
    private UserListAdapter adapter;
    private SearchView searchView;
    private Spinner sortSpinner;
    private ImageView addUserButton;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DriverListFragment() {}

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
        searchView = view.findViewById(R.id.searchView);
        sortSpinner = view.findViewById(R.id.sortSpinner);
        addUserButton = view.findViewById(R.id.addUserButton);

        // Get drivers database and put in the list
        FirebaseFirestore database = FirebaseFirestore.getInstance();
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

        // Implement on click listener for item in the list
        driverList.setOnItemClickListener(((parent, view1, position, id) -> {
            Driver driver = (Driver) parent.getItemAtPosition(position);
            // TODO: Create activity to view user's details
        }));

        // Implement search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        // Set value for spinner
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.users_sort_options, R.layout.spinner_text_style);
        sortAdapter.setDropDownViewResource(R.layout.spinner_item_text);
        sortSpinner.setAdapter(sortAdapter);

        // Implement sorting for user list (sort by name and email)
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void sortUsers(int position) {
        if (position == 1) {
            Collections.sort(drivers, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        } else if (position == 2) {
            Collections.sort(drivers, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return o1.getEmail().compareTo(o2.getEmail());
                }
            });
        }
        adapter.updateUsers(drivers);
    }
}