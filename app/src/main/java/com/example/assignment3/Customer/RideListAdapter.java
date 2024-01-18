package com.example.assignment3.Customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.assignment3.Class.Ride;
import com.example.assignment3.R;

import java.util.ArrayList;
import java.util.List;

public class RideListAdapter extends ArrayAdapter<Ride> {
    private List<Ride> rides;

    public RideListAdapter(Context context, List<Ride> rides) {
        super(context, 0, rides);
        this.rides = new ArrayList<>(rides);
    }

    @NonNull
    @Override
    public View getView(int position, android.view.View convertView, @NonNull ViewGroup parent) {
        if (rides.isEmpty()) {
            // If the list is empty, return an empty view
            return new View(getContext());
        }

        Ride ride = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_list_layout, parent, false);
        }

        TextView pickupPoint = convertView.findViewById(R.id.pickupPoint);
        TextView destinationPoint = convertView.findViewById(R.id.destinationPoint);
        TextView tripPrice = convertView.findViewById(R.id.tripPrice);

        assert ride != null;
        pickupPoint.setText(ride.getPickPointName());
        destinationPoint.setText(ride.getDropPointName());
        tripPrice.setText(String.valueOf(ride.getTripPrice()));

        return convertView;
    }
}
