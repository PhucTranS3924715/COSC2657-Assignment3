package com.example.assignment3;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3.databinding.MessageReceivedContainerBinding;
import com.example.assignment3.databinding.MessageSentContainerBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> chatMessages;
    private final String senderID;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVE = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, String senderID) {
        this.chatMessages = chatMessages;
        this.senderID = senderID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    MessageSentContainerBinding.inflate(LayoutInflater.from(
                            parent.getContext()), parent, false));
        } else {
            return new ReceivedMessageViewHolder(MessageReceivedContainerBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderID.equals(senderID)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVE;
        }
    }

    // View holder for current customer (sent)
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final MessageSentContainerBinding binding;

        SentMessageViewHolder(MessageSentContainerBinding messageSentContainerBinding) {
            super(messageSentContainerBinding.getRoot());
            binding = messageSentContainerBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }

    // View holder for current driver (receive)
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final MessageReceivedContainerBinding binding;

        ReceivedMessageViewHolder(MessageReceivedContainerBinding messageReceivedContainerBinding) {
            super(messageReceivedContainerBinding.getRoot());
            binding = messageReceivedContainerBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }
}
