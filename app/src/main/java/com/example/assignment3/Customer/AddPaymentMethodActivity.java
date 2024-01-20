package com.example.assignment3.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.assignment3.Class.Customer;
import com.example.assignment3.Class.PaymentMethod;
import com.example.assignment3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class AddPaymentMethodActivity extends AppCompatActivity {
    private static final String TAG = "AddPaymentMethod";
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);

        Spinner paymentSpinner = findViewById(R.id.paymentSpinner);
        EditText detailsInput = findViewById(R.id.detailsInput);
        Button addButton = findViewById(R.id.addButton);

        // Set values for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_types, R.layout.spinner_item_text);
        adapter.setDropDownViewResource(R.layout.spinner_text_style);
        paymentSpinner.setAdapter(adapter);

        paymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = (String) parent.getItemAtPosition(position);
                if (text.equals("Paypal")) {
                    detailsInput.setHint("Enter Paypal email");
                } else {
                    detailsInput.setHint("Enter credit card numbers");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

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
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        addButton.setOnClickListener(v -> {
            String type = paymentSpinner.getSelectedItem().toString();
            String details = detailsInput.getText().toString();

            boolean isValid = true;
            if (details.isEmpty()) {
                detailsInput.setError("Invalid payment details");
                isValid = false;
            }

            if (isValid) {
                PaymentMethod paymentMethod = new PaymentMethod();
                if (type.equals("Paypal")) {
                    paymentMethod.setName("Paypal");
                    paymentMethod.setIcon(R.drawable.paypal_icon);
                    paymentMethod.setDetail(details);
                } else {
                    paymentMethod.setName("Credit card");
                    paymentMethod.setIcon(R.drawable.credit_card_icon);
                    paymentMethod.setDetail(details);
                }

                // Check if the customer already has this payment method
                for (PaymentMethod method : customer.getPaymentMethods()) {
                    if (method.getName().equals(paymentMethod.getName())) {
                        detailsInput.setError("This payment method already exists");
                        return;
                    }
                }

                customer.getPaymentMethods().add(paymentMethod);

                new AlertDialog.Builder(this).setTitle("Confirm save")
                        .setMessage("Are you sure you want to save these changes?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            // Update the customer data in Firestore
                            db.collection("Customers").document(uid)
                                    .set(customer, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> Log.d(TAG,
                                            "DocumentSnapshot successfully updated!"))
                                    .addOnFailureListener(
                                            e -> Log.w(TAG, "Error updating document", e));
                            finish();
                        }).setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        });

    }
}