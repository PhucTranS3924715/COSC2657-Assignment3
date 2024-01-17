package com.example.assignment3.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.assignment3.Class.Voucher;
import com.example.assignment3.databinding.ActivityCreateVoucherBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Calendar;

public class CreateVoucherActivity extends AppCompatActivity {
    private ActivityCreateVoucherBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateVoucherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EditText voucherNameText = binding.voucherNameText;
        EditText voucherDiscountText = binding.voucherDiscountText;
        TextView voucherExpirationText = binding.voucherExpirationText;

        // Implement a date picker for date text view
        voucherExpirationText.setOnClickListener(v -> {
            // Create Calendar object to get current date
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CreateVoucherActivity.this, (view, year1, monthOfYear, dayOfMonth) -> {
                String date1 = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                voucherExpirationText.setText(date1);
            }, year, month, day);
            datePickerDialog.show();
        });

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        Button createVoucherButton = binding.createVoucherButton;
        createVoucherButton.setOnClickListener(v -> {
            String name = voucherNameText.getText().toString();
            String discount = voucherDiscountText.getText().toString();
            String date = voucherExpirationText.getText().toString();

            boolean isValid = true;
            if (name.isEmpty()) {
                voucherNameText.setError("Invalid name");
                isValid = false;
            }
            if (discount.isEmpty()) {
                voucherDiscountText.setError("Invalid discount");
                isValid = false;
            }
            if (date.isEmpty()) {
                voucherExpirationText.setError("Invalid date");
                isValid = false;
            }

            if (isValid) {
                Voucher voucher = new Voucher(name, Double.parseDouble(discount), date);
                database.collection("Vouchers").add(voucher);
                setResult(101);
                finish();
            }
        });
    }
}