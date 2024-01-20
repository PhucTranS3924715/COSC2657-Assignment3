package com.example.assignment3.Customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment3.ChatAdapter;
import com.example.assignment3.ChatMessage;
import com.example.assignment3.Class.Customer;
import com.example.assignment3.Class.Driver;
import com.example.assignment3.Class.User;
import com.example.assignment3.databinding.ActivityMessageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "MessageActivity";
    private ActivityMessageBinding binding;
    private User receiverUser;
    private String receiverID;  // Other user ID (currently chatting with)
    private String senderID;  // Current user ID
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore database;

    private static final String KEY_SENDER_ID = "senderId";
    private static final String KEY_RECEIVER_ID = "receiverIds";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TIMESTAMP = "timestamp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get current user ID (Sender ID)
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        senderID = user.getUid();

        TextView userName = binding.userNameText;
        ImageView profilePicture = binding.profilePicture;

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, senderID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.chatRecycleView.setLayoutManager(layoutManager);
        binding.chatRecycleView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();

        // Get the other user currently chatting with
        receiverUser = (User) getIntent().getSerializableExtra("user");  // TODO: Implement function to send the user object
        receiverID = (String) getIntent().getSerializableExtra("userID");  // TODO: Implement function to send the user ID
        assert receiverUser != null;

        // Set name and picture for other user currently chatting with
        userName.setText(receiverUser.getName());
        // TODO: Set user's profile picture

        // Implement on send message button
        binding.layoutSend.setOnClickListener(v -> {
            HashMap<String, Object> message = new HashMap<>();
            message.put(KEY_SENDER_ID, senderID);
            message.put(KEY_RECEIVER_ID, receiverID);
            message.put(KEY_MESSAGE, binding.messageInput.getText().toString());
            message.put(KEY_TIMESTAMP, new Date());
            database.collection("Chats").add(message);
            binding.messageInput.setText(null);
        });

        listenMessages();
    }

    private void listenMessages() {
        database.collection("Chats")
                .whereIn(KEY_SENDER_ID, Arrays.asList(senderID, receiverID))
                .whereIn(KEY_RECEIVER_ID, Arrays.asList(senderID, receiverID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int oldCount = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = documentChange.getDocument().getString(KEY_SENDER_ID);
                    chatMessage.receiverID = documentChange.getDocument().getString(KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            int newCount = chatMessages.size();
            if (oldCount == 0) {
                chatAdapter.notifyDataSetChanged();
            } else if(newCount > oldCount) {
                chatAdapter.notifyItemRangeInserted(oldCount, newCount - oldCount);
                binding.chatRecycleView.smoothScrollToPosition(chatMessages.size() - 1);
            }
        }
    };

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMM dd, yyyy 'AT' HH:mm", Locale.getDefault()).format(date);
    }
}