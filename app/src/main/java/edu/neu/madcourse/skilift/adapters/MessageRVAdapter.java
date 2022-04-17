package edu.neu.madcourse.skilift.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.skilift.R;
import edu.neu.madcourse.skilift.interfaces.IMessageViewHolder;
import edu.neu.madcourse.skilift.models.Message;
import edu.neu.madcourse.skilift.viewholders.ReceivedMessageViewHolder;
import edu.neu.madcourse.skilift.viewholders.SentMessageViewHolder;

@SuppressWarnings("rawtypes")
public class MessageRVAdapter extends RecyclerView.Adapter {
    private final ArrayList<Message> messageList;
    private final String username;

    public MessageRVAdapter(ArrayList<Message> messageList, String username) {
        this.messageList = messageList;
        this.username = username;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                viewType, parent, false);
        return viewType == R.layout.sent_message_card ?
                new SentMessageViewHolder(v) : new ReceivedMessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((IMessageViewHolder) holder).onBind(messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getSender().equals(username) ?
                R.layout.sent_message_card : R.layout.received_message_card;
    }
}
