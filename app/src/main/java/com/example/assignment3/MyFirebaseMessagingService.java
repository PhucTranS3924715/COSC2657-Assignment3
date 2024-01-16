package com.example.assignment3;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.assignment3.Driver.RideDetailsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServ";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        // Log the new token to the console
        Log.d(TAG, "Refreshed token: " + token);

        // TODO: Implement your logic for handling the new FCM token.
        updateDriverFCMToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if the message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            // Display the notification.
            NotificationService.sendNotificationToDriver(getApplicationContext(),
                    remoteMessage.getData().get("driverUid"),
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());

            Intent intent = new Intent(this, RideDetailsActivity.class);
            intent.putExtra("rideDocumentId", remoteMessage.getData().get("rideDocumentId"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void updateDriverFCMToken(String newToken) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String driverUid = user.getUid();

            // Perform the database update
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            DocumentReference driverRef = database.collection("Drivers").document(driverUid);

            driverRef.update("fcmToken", newToken)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "FCM token updated in the database");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to update FCM token in the database", e);
                        }
                    });
        } else {
            Log.e(TAG, "User is null. Unable to update FCM token in the database.");
        }
    }
}