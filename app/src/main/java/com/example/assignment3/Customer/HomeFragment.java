package com.example.assignment3.Customer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.assignment3.BookingFragment;
import com.example.assignment3.HomeFragmentListener;
import com.example.assignment3.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements HomeFragmentListener, OnMapReadyCallback{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private double priceBike = 0, priceCar4 = 0, priceCar7 = 0;

    private String mParam1;
    private String mParam2;

    private Button btnBooking;
    public String rideDocumentId;
    private LatLng pickupLocation;
    private LatLng destinationLocation;
    private String pickupLocationName;
    private String destinationLocationName;
    private String distanceInKilometers;
    private String selectedVehicleType = "";
    private double Price = 0.0;


    private TextView tripPriceBike;
    private TextView tripPriceCar4;
    private TextView tripPriceCar7;
    private Marker pickupMarker;
    private Marker destinationMarker;

    private double discount = 1;

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

    //Draw direction between Pick up point and Destination.
    public void drawRoute() {
        // Use the Google Directions API to get the route information
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(getString(R.string.api_key)) // Replace with your API key
                .build();

        MarkerOptions pickupMarkerOptions = new MarkerOptions().
                position(pickupLocation).title("Pick-up Location").
                icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup));
        MarkerOptions destinationMarkerOptions = new MarkerOptions().
                position(destinationLocation).title("Destination Location").
                icon(BitmapDescriptorFactory.fromResource(R.drawable.destination));

        DirectionsResult result;
        try {
            result = DirectionsApi.newRequest(context)
                    .origin(new com.google.maps.model.LatLng(pickupLocation.latitude, pickupLocation.longitude))
                    .destination(new com.google.maps.model.LatLng(destinationLocation.latitude, destinationLocation.longitude))
                    .mode(TravelMode.DRIVING) // You can change the mode based on your requirements
                    .await();

            if (result.routes != null && result.routes.length > 0) {
                // Extract the polyline from the result and add it to the map
                EncodedPolyline points = result.routes[0].overviewPolyline;
                List<com.google.maps.model.LatLng> decodedPath = points.decodePath();

                // Clear previous markers and polylines
                mMap.clear();

                // Add markers for pick-up and destination
                pickupMarker = mMap.addMarker(pickupMarkerOptions);
                destinationMarker = mMap.addMarker(destinationMarkerOptions);

                // Draw the route on the map
                PolylineOptions polylineOptions = new PolylineOptions();
                for (com.google.maps.model.LatLng latLng : decodedPath) {
                    polylineOptions.add(new LatLng(latLng.lat, latLng.lng));
                }

                int polylineColor = ContextCompat.getColor(requireContext(), R.color.red);
                polylineOptions.color(polylineColor);

                mMap.addPolyline(polylineOptions);

                // TODO: Implement discount
                // Calculate total distance
                if (result.routes != null && result.routes.length > 0) {
                    DirectionsLeg leg = result.routes[0].legs[0];
                    double distanceInMeters = ((DirectionsLeg) leg).distance.inMeters;
                    double distanceInKilometers = distanceInMeters / 1000.0;

                    // Define price rates
                    double priceRateBike = 8000.0;
                    double priceRateCar4 = 15000.0;
                    double priceRateCar7 = 20000.0;

                    // Calculate prices for each vehicle
                    double priceBike = distanceInKilometers * priceRateBike * discount;
                    double priceCar4 = distanceInKilometers * priceRateCar4 * discount;
                    double priceCar7 = distanceInKilometers * priceRateCar7 * discount;

                    // Update the TextViews with the calculated prices
                    tripPriceBike.setText(String.valueOf(priceBike));
                    tripPriceCar4.setText(String.valueOf(priceCar4));
                    tripPriceCar7.setText(String.valueOf(priceCar7));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                data.put("pickPointName", pickupLocationName);
                data.put("dropPointName", destinationLocationName);
                data.put("distance", distanceInKilometers);
                data.put("vehicleType",selectedVehicleType);
                data.put("price",Price);
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
                BookingFragment bookingFragment = BookingFragment.newInstance(rideDocumentId);
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

//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync((OnMapReadyCallback) HomeFragment.this);

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
                pickupLocationName = place.getName();

                pickupLocationAutocompleteFragment.setText(place.getName());

                MarkerOptions startMarkerOptions = new MarkerOptions()
                        .position(place.getLatLng()).title(place.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup));
                mMap.clear();
                mMap.addMarker(startMarkerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 14));
                GeoPoint startGeoPoint = new GeoPoint(pickupLocation.latitude, pickupLocation.longitude);
                Log.i(TAG, "Place: " + ", " + place.getName() + place.getAddress() + ", " + place.getLatLng());

                if (destinationLocation != null) {
                    drawRoute();
                }
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
                destinationLocationName = place.getName();

                destinationAutocompleteFragment.setText(place.getName());

                MarkerOptions destinationMarkerOptions = new MarkerOptions()
                        .position(place.getLatLng()).title(place.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination));
                mMap.clear();
                mMap.addMarker(destinationMarkerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 14));
                GeoPoint destinationGeoPoint = new GeoPoint(destinationLocation.latitude, destinationLocation.longitude);
                Log.i(TAG, "Place: " + ", " + place.getName() + place.getAddress() + ", " + place.getLatLng());

                if (pickupLocation != null) {
                    drawRoute();
                }
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

                //Save the price
                selectedVehicleType = "Bike";
                Price = Double.parseDouble(tripPriceBike.getText().toString());
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

                //Save the price
                selectedVehicleType = "Car4";
                Price = Double.parseDouble(tripPriceCar4.getText().toString());
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

                //Save the price
                selectedVehicleType = "Car7";
                Price = Double.parseDouble(tripPriceCar7.getText().toString());
            }
        });

        LinearLayout paymentMethodLayout = view.findViewById(R.id.paymentMethodLayout);
        ImageView paymentMethodIcon = view.findViewById(R.id.paymentMethodIcon);
        TextView paymentMethodText = view.findViewById(R.id.paymentMethodText);

        // Create activity launcher for PaymentMethodActivity
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == 101) {
                        assert result.getData() != null;
                        int method = result.getData().getIntExtra("method", -1);
                        switch (method) {
                            case 0:
                                paymentMethodIcon.setImageResource(R.drawable.paypal_icon);
                                paymentMethodText.setText("Paypal");
                                break;
                            case 1:
                                paymentMethodIcon.setImageResource(R.drawable.cash_icon);
                                paymentMethodText.setText("Cash");
                                break;
                            case 2:
                                paymentMethodIcon.setImageResource(R.drawable.credit_card_icon);
                                paymentMethodText.setText("Credit Card");
                                break;
                        }
                    }
                });

        paymentMethodLayout.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), PaymentMethodActivity.class);
            launcher.launch(intent);
        });

        LinearLayout voucherLayout = view.findViewById(R.id.voucherLayout);
        tripPriceBike = view.findViewById(R.id.tripPriceBike);
        tripPriceCar4 = view.findViewById(R.id.tripPriceCar4);
        tripPriceCar7 = view.findViewById(R.id.tripPriceCar7);


        if (!tripPriceBike.getText().toString().isEmpty() && !tripPriceCar4.getText().toString().isEmpty() && !tripPriceCar7.getText().toString().isEmpty()) {
            priceBike = Double.parseDouble(tripPriceBike.getText().toString());
            priceCar4 = Double.parseDouble(tripPriceCar4.getText().toString());
            priceCar7 = Double.parseDouble(tripPriceCar7.getText().toString());
        }

        // Set on click listener for voucher fragment
        voucherLayout.setOnClickListener(v -> {
            // Navigate to the VoucherFragment
            ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
            viewPager.setCurrentItem(2);
        });

        // Update the price after the user choose a voucher
        getParentFragmentManager().setFragmentResultListener("discountedPrice", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                discount = result.getDouble("discountedPrice");
                tripPriceBike.setText(String.valueOf(priceBike * discount));
                tripPriceCar4.setText(String.valueOf(priceCar4 * discount));
                tripPriceCar7.setText(String.valueOf(priceCar7 * discount));
            }
        });

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Move map to HCM city
        LatLng HCMCity = new LatLng(10.832859812678445, 106.62375678797527);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HCMCity, 10f));

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) HomeFragment.this);
    }
}
