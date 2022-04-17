package edu.neu.madcourse.skilift.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.neu.madcourse.skilift.R;
import edu.neu.madcourse.skilift.interfaces.IMessageViewHolder;
import edu.neu.madcourse.skilift.models.Message;

public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder implements IMessageViewHolder {
    private final TextView username;
    private final TextView received_message;
    private final TextView timestamp;

    public ReceivedMessageViewHolder(View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.received_message_username);
        received_message = itemView.findViewById(R.id.received_message);
        timestamp = itemView.findViewById(R.id.received_message_date);
    }

    public void onBind(Message message) {
        username.setText(message.getSender());
        received_message.setText(message.getMessage());
        DateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.US);
        timestamp.setText(dateFormat.format(new Date(message.getTimestamp())));
    }
}