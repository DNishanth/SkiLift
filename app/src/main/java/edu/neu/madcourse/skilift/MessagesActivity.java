package edu.neu.madcourse.skilift;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import edu.neu.madcourse.skilift.adapters.MessageRVAdapter;
import edu.neu.madcourse.skilift.models.Message;

@SuppressWarnings("rawtypes")
public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MessagesActivity.class.getSimpleName();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final ArrayList<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private String username;
    private String messagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        String groupID = getIntent().getExtras().getString("groupID");
        username = getIntent().getExtras().getString("username");
        messagePath = "groups/" + groupID + "/messages";

        // Create recycler view
        createRecyclerView();

        // Populate recycler view with messages
        populateMessages();

        EditText messageInput = findViewById(R.id.message_input);
        ImageButton sendMessageButton = findViewById(R.id.button_send_message);
        messageInput.setOnClickListener(this);
        sendMessageButton.setOnClickListener(this);
    }

    private void createRecyclerView() {
        // Reversing layout to pin keyboard under messages, otherwise it will overlap
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        adapter = new MessageRVAdapter(messageList, username);
        recyclerView = findViewById(R.id.message_rv);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    // Listen for new messages and update recycler view
    private void populateMessages() {
        DatabaseReference messageRef = db.getReference(messagePath);
        ValueEventListener getMessages = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    messageList.clear();
                    for (DataSnapshot messageID : snapshot.getChildren()) {
                        Message message = messageID.getValue(Message.class);
                        messageList.add(message);
                    }
                    Collections.reverse(messageList);
                    adapter.notifyItemRangeChanged(0, (int) snapshot.getChildrenCount());
                    recyclerView.smoothScrollToPosition(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        };
        messageRef.addValueEventListener(getMessages);
    }

    @Override
    public void onClick(View view) {
        // Send message to group
        EditText messageInput = findViewById(R.id.message_input);
        String messageText = messageInput.getText().toString();
        if (view.getId() == R.id.button_send_message && !messageText.equals("")) {
            db.getReference(messagePath).push().setValue(new Message(username, messageText));
            messageInput.getText().clear();
        }
    }
}