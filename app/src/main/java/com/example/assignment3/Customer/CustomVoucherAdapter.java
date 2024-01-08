package com.example.assignment3.Customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment3.R;
import com.example.assignment3.Class.Voucher;

import java.util.List;

public class CustomVoucherAdapter extends BaseAdapter{
    private Context context;
    private List<Voucher> vouchers; // Replace YourDataModel with the actual data model class

    @Override
    public int getCount() {
        return vouchers.size();
    }

    @Override
    public Voucher getItem(int position) {
        return vouchers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_item_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Voucher currentVoucher = getItem(position);

        holder.voucherNameTextView.setText(currentVoucher.getVoucherName());


        holder.expirationTextView.setText(currentVoucher.getExpirationDate());

        return convertView;
    }

    static class ViewHolder {
        ImageView itemImageView; // Assuming you have an image for each voucher
        TextView voucherNameTextView;
        TextView expirationTextView;
        // Add other views as needed

        ViewHolder(View view) {
            itemImageView = view.findViewById(R.id.itemImageView);
            voucherNameTextView = view.findViewById(R.id.voucherName);
            expirationTextView = view.findViewById(R.id.voucherExpirationDate);
        }
    }
}
