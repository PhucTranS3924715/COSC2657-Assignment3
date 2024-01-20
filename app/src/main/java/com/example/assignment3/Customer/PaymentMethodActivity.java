package com.example.assignment3.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment3.Admin.ViewCustomerDetailActivity;
import com.example.assignment3.Class.Customer;
import com.example.assignment3.Class.PaymentMethod;
import com.example.assignment3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodActivity extends AppCompatActivity {
    private static final String TAG = "PaymentMethodActivity";
    private Customer customer;
    private ArrayList<PaymentMethod> paymentMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        ListView paymentList = findViewById(R.id.paymentList);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Customers").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    customer = document.toObject(Customer.class);

                    paymentMethods = new ArrayList<>();
                    // Check and display payment methods that the customer had
                    if (customer.getPaymentMethods() != null) {  // Error
                        for (PaymentMethod method : customer.getPaymentMethods()) {
                            if (method != null) {
                                if (method.getName().equals("Paypal")) {
                                    paymentMethods.add(new PaymentMethod("Paypal", R.drawable.paypal_icon));
                                }
                                if (method.getName().equals("Credit card")) {
                                    paymentMethods.add(new PaymentMethod("Credit card", R.drawable.credit_card_icon));
                                }
                            }
                        }
                    }
                    paymentMethods.add(new PaymentMethod("Cash", R.drawable.cash_icon));

                    PaymentMethodAdapter adapter = new PaymentMethodAdapter(this, paymentMethods);
                    paymentList.setAdapter(adapter);
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });



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
