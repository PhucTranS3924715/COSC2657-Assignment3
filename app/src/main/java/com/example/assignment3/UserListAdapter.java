package com.example.assignment3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> implements Filterable {
    private List<User> originalUsers;
    private List<User> filteredUsers;
    private Filter filter;

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
        userEmail.setText(user.getEmail());

        return convertView;
    }

    @Override
    public int getCount() {
        return filteredUsers.size();
    }

    @Override
    public User getItem(int position) {
        return filteredUsers.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if (constraint == null || constraint.length() == 0) {
                        results.values = originalUsers;
                        results.count = originalUsers.size();
                    } else {
                        List<User> filteredList = new ArrayList<>();
                        String filterPattern = constraint.toString().toLowerCase().trim();
                        for (User user : originalUsers) {
                            if (user.getName().toLowerCase().contains(filterPattern)) {
                                filteredList.add(user);
                            }
                        }
                        results.values = filteredList;
                        results.count = filteredList.size();
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredUsers = (List<User>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
        return filter;
    }

    public void updateUsers(List<User> users) {
        this.originalUsers = new ArrayList<>(users);
        this.filteredUsers = new ArrayList<>(users);
        notifyDataSetChanged();
    }
}
