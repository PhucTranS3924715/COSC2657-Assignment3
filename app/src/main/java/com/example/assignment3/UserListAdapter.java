package com.example.assignment3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {
    private List<User> originalUsers;
    private List<User> filteredUsers;

    public UserListAdapter(Context context, List<User> users) {
        super(context, 0, users);
        this.originalUsers = new ArrayList<>(users);
        this.filteredUsers = new ArrayList<>(users);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_list_layout, parent, false);
        }

        TextView userName = convertView.findViewById(R.id.userName);
        TextView userPhone = convertView.findViewById(R.id.userPhone);
        TextView userEmail = convertView.findViewById(R.id.userEmail);

        assert user != null;
        userName.setText(user.getName());
        userPhone.setText(user.getPhone());
        // TODO: Get user email

        return convertView;
    }
}
