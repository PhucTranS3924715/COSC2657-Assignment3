package com.example.assignment3;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreHelper {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static Task<DocumentSnapshot> getCustomer(String customerUid) {
        return db.collection("Customers").document(customerUid).get();
    }

    public static Task<String> getCustomerFCMToken(String customerUid) {
        return getCustomer(customerUid).continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                return task.getResult().getString("fcmToken");
            } else {
                // Handle the case where fetching FCM token fails
                return null;
            }
        });
    }
}


