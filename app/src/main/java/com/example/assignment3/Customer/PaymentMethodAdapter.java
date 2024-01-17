package com.example.assignment3.Customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.assignment3.Class.PaymentMethod;
import com.example.assignment3.Class.Voucher;
import com.example.assignment3.R;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodAdapter extends ArrayAdapter<PaymentMethod> {
    private List<PaymentMethod> paymentMethods;

    public PaymentMethodAdapter(Context context, List<PaymentMethod> paymentMethods) {
        super(context, 0, paymentMethods);
        this.paymentMethods = new ArrayList<>(paymentMethods);
    }

    @NonNull
    @Override
    public View getView(int position, android.view.View convertView, @NonNull ViewGroup parent) {
        PaymentMethod paymentMethod = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.payment_method_list_layout, parent, false);
        }

        ImageView paymentIcon = convertView.findViewById(R.id.paymentIcon);
        TextView paymentMethodText = convertView.findViewById(R.id.paymentMethodText);

        assert paymentMethod != null;
        paymentIcon.setImageResource(paymentMethod.getIcon());
        paymentMethodText.setText(paymentMethod.getName());

        return convertView;
    }
}
