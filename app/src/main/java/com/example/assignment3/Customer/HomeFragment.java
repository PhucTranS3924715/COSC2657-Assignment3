package com.example.assignment3.Customer;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.example.assignment3.BookingFragment;
import com.example.assignment3.HomeFragmentListener;
import com.example.assignment3.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements HomeFragmentListener{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Button btnBooking;
    public String rideDocumentId;
    private LatLng pickupLocation;
    private LatLng destinationLocation;

    public HomeFragment() {}

    private static final String TAG = "Info";
    GoogleMap mMap;
    View bikeLayout, car4Layout, car7Layout;
    TextView bikeText, car4Text, car7Text;
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mapContainer, fragment);
        transaction.addToBackStack(null); // Add the transaction to the back stack
        transaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnBooking = view.findViewById(R.id.btnBooking);

        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new ride object has pick up location, destination, and customerID

                // Set up Firestore database and Authentication
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                assert user != null;
                // Get current customer ID
                String uid = user.getUid();

                Map<String, Object> data = new HashMap<>();
                data.put("uidCustomer", uid);
                data.put("uidDriver", null);
                data.put("PickPoint", pickupLocation);
                data.put("DropPoint", destinationLocation);
                data.put("status", "Incomplete");
                data.put("DriverLocation", null);

                // Add data to firestore
                DocumentReference docRef = database.collection("Ride").document();
                docRef.set(data).addOnSuccessListener(aVoid -> {
                    // Handle success if needed
                    rideDocumentId = docRef.getId();
                    Log.d("HomeFragment", "DocumentSnapshot added with ID: " + rideDocumentId);
                }).addOnFailureListener(e -> {
                    // Handle failure if needed
                    Log.e("HomeFragment", "Error adding document: " + e.getMessage());
                });

                // Open the BookingFragment when the "Book Now" button is clicked
                BookingFragment bookingFragment = new BookingFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mapContainer, bookingFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        bikeLayout = view.findViewById(R.id.bikeLayout);
        car4Layout = view.findViewById(R.id.car4Layout);
        car7Layout = view.findViewById(R.id.car7Layout);
        bikeText = view.findViewById(R.id.bikeText);
        car4Text = view.findViewById(R.id.car4Text);
        car7Text = view.findViewById(R.id.car7Text);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) HomeFragment.this);

        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()){
            Places.initialize(requireContext(), apiKey);
        }

        PlacesClient placesClient = Places.createClient(requireActivity());

        AutocompleteSupportFragment pickupLocationAutocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.pickupLocationAutoComplete);

        pickupLocationAutocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(10.769444, 106.681944),
                new LatLng(10.769444, 106.681944)));

        pickupLocationAutocompleteFragment.setCountries("VN");

        // Specify the types of place data to return.
        pickupLocationAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        pickupLocationAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                pickupLocation = place.getLatLng();

                pickupLocationAutocompleteFragment.setText(place.getName());

                MarkerOptions startMarkerOptions = new MarkerOptions().position(place.getLatLng()).title(place.getName());
                mMap.clear();
                mMap.addMarker(startMarkerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 14));
                GeoPoint startGeoPoint = new GeoPoint(pickupLocation.latitude, pickupLocation.longitude);
                Log.i(TAG, "Place: " + ", " + place.getName() + place.getAddress() + ", " + place.getLatLng());
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        AutocompleteSupportFragment destinationAutocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.destinationAutoComplete);

        destinationAutocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(10.769444, 106.681944),
                new LatLng(10.769444, 106.681944)));

        destinationAutocompleteFragment.setCountries("VN");

        // Specify the types of place data to return.
        destinationAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        destinationAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                destinationLocation = place.getLatLng();

                destinationAutocompleteFragment.setText(place.getName());

                MarkerOptions destinationMarkerOptions = new MarkerOptions().position(place.getLatLng()).title(place.getName());
                mMap.clear();
                mMap.addMarker(destinationMarkerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 14));
                GeoPoint destinationGeoPoint = new GeoPoint(destinationLocation.latitude, destinationLocation.longitude);
                Log.i(TAG, "Place: " + ", " + place.getName() + place.getAddress() + ", " + place.getLatLng());
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        bikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change background color
                bikeLayout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.vehicle_selected_background));
                car4Layout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.blue_outline_background));
                car7Layout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.blue_outline_background));

                // Change text color
                bikeText.setTextColor(Color.parseColor("#FFFFFFFF"));
                car4Text.setTextColor(Color.parseColor("#6DBAED"));
                car7Text.setTextColor(Color.parseColor("#6DBAED"));
            }
        });

        car4Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change background color
                bikeLayout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.blue_outline_background));
                car4Layout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.vehicle_selected_background));
                car7Layout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.blue_outline_background));

                // Change text color
                bikeText.setTextColor(Color.parseColor("#6DBAED"));
                car4Text.setTextColor(Color.parseColor("#FFFFFFFF"));
                car7Text.setTextColor(Color.parseColor("#6DBAED"));
            }
        });

        car7Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change background color
                bikeLayout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.blue_outline_background));
                car4Layout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.blue_outline_background));
                car7Layout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.vehicle_selected_background));

                // Change text color
                bikeText.setTextColor(Color.parseColor("#6DBAED"));
                car4Text.setTextColor(Color.parseColor("#6DBAED"));
                car7Text.setTextColor(Color.parseColor("#FFFFFFFF"));
            }
        });
        // TODO: Create home UI
        return view;
    }

    // Convert LatLng into geo point
    GeoPoint convertLatLngToGeoPoint(LatLng latLng) {
        return new GeoPoint(latLng.latitude, latLng.longitude);
    }

    @Override
    public void onBookingClicked() {
        replaceFragment(new BookingFragment());
    }
}