package com.example.assignment3;

import android.content.Intent;
import android.util.Log;

import com.example.assignment3.Driver.RideDetailsActivity;
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
        // UpdateDriverFCMToken(token);
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
}