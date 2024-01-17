package com.example.assignment3.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment3.Admin.ViewCustomerDetailActivity;
import com.example.assignment3.Class.Customer;
import com.example.assignment3.Class.PaymentMethod;
import com.example.assignment3.R;

import java.util.ArrayList;

public class PaymentMethodActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        ListView paymentList = findViewById(R.id.paymentList);

        ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(new PaymentMethod("Paypal", R.drawable.paypal_icon));
        paymentMethods.add(new PaymentMethod("Cash", R.drawable.cash_icon));
        paymentMethods.add(new PaymentMethod("Credit Card", R.drawable.credit_card_icon));

        PaymentMethodAdapter adapter = new PaymentMethodAdapter(this, paymentMethods);
        paymentList.setAdapter(adapter);

        // Implement on click listener for item in the list
        paymentList.setOnItemClickListener(((parent, view1, position, id) -> {
            Intent intent = new Intent();
            intent.putExtra("method", position);
            setResult(101, intent);
            finish();
        }));

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }
}
