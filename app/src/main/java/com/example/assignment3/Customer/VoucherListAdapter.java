package com.example.assignment3.Customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.assignment3.Class.Voucher;
import com.example.assignment3.R;

import java.util.ArrayList;
import java.util.List;

public class VoucherListAdapter extends ArrayAdapter<Voucher> {
    private List<Voucher> vouchers;

    public VoucherListAdapter(Context context, List<Voucher> vouchers) {
        super(context, 0, vouchers);
        this.vouchers = new ArrayList<>(vouchers);
    }

    @NonNull
    @Override
    public View getView(int position, android.view.View convertView, @NonNull ViewGroup parent) {
        Voucher voucher = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.voucher_list_layout, parent, false);
        }

        TextView voucherName = convertView.findViewById(R.id.voucherName);
        TextView voucherExpirationDate = convertView.findViewById(R.id.voucherExpirationDate);

        assert voucher != null;
        voucherName.setText(voucher.getVoucherName());
        voucherExpirationDate.setText(voucher.getExpirationDate());

        return convertView;
    }
}
