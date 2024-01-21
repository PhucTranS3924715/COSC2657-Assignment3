package com.example.assignment3.Customer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.assignment3.Class.Customer;
import com.example.assignment3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class SOSSetupActivity extends AppCompatActivity {

    private EditText editTextSOSPhone;
    private EditText editTextSOSMessage;
    private Customer customer;
    private FirebaseFirestore db;
    private String uid;
    private static final String TAG = "SOSSetupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_setup);

        // Initialize UI elements
        editTextSOSPhone = findViewById(R.id.sosPhoneNumber);
        editTextSOSMessage = findViewById(R.id.sosMessage);
        Button buttonSaveSOS = findViewById(R.id.submitSOSButton);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        uid = user.getUid();

        db = FirebaseFirestore.getInstance();
        db.collection("Customers").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    customer = document.toObject(Customer.class);
                    if (!customer.getSosPhone().isEmpty() && !customer.getSosMessage().isEmpty()) {
                        editTextSOSPhone.setText(customer.getSosPhone());
                        editTextSOSMessage.setText(customer.getSosMessage());
                    }
                    buttonSaveSOS.setOnClickListener(v -> saveSOSInformation());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void saveSOSInformation() {
        String sosPhone = editTextSOSPhone.getText().toString().trim();
        String sosMessage = editTextSOSMessage.getText().toString().trim();

        boolean isValid = true;
        if (sosPhone.isEmpty()) {
            editTextSOSPhone.setError("Invalid phone number");
            isValid = false;
        }
        if (sosMessage.isEmpty()) {
            editTextSOSMessage.setError("Invalid message");
            isValid = false;
        }
        if (isValid) {
            if (customer.getSosPhone().isEmpty() && customer.getSosMessage().isEmpty()) {
                new AlertDialog.Builder(this).setTitle("Confirm add SOS")
                        .setMessage("Are you sure you want to add this SOS?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            customer.setSosPhone(sosPhone);
                            customer.setSosMessage(sosMessage);
                            db.collection("Customers").document(uid)
                                    .set(customer, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> Log.d(TAG,
                                            "DocumentSnapshot successfully updated!"))
                                    .addOnFailureListener(
                                            e -> Log.w(TAG, "Error updating document", e));
                            finish();
                        }).setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
            } else {
                new AlertDialog.Builder(this).setTitle("Confirm update SOS")
                        .setMessage("You already have an SOS. Do you want to overwrite SOS?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            customer.setSosPhone(sosPhone);
                            customer.setSosMessage(sosMessage);
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
        }
    }
}