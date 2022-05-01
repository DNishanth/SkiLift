package edu.neu.madcourse.skilift;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.neu.madcourse.skilift.adapters.MessageRVAdapter;
import edu.neu.madcourse.skilift.models.Message;

@SuppressWarnings("rawtypes")
public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MessagesActivity.class.getSimpleName();
    private final String serverToken = "AAAAvacgsy8:APA91bGhXGnZLKsBaOZVBDeZ0L3FtE9ey8K1YtbLHBnx4564zYkmLab3xilfK_YcwgpoHKODcRsB06AtBCmZwzjaK22ag6mPra5a_NDue7sMcqNt0EJsW0aHL7Gk2iUk3tYtMituB7cB";
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final ArrayList<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private String username;
    private String messagePath;
    private String message = "";
    private ArrayList<String> groupUsers = new ArrayList<>();
    private String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        groupID = getIntent().getExtras().getString("groupID");
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

    private void populateOtherUsers() {
        String groupPath = "groups/" + groupID + "/members";
        DatabaseReference groupRef = db.getReference().child(groupPath);
        ValueEventListener getUsers = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    groupUsers.clear();
                    for (DataSnapshot userID : snapshot.getChildren()) {
                        String groupUsername = userID.getValue(String.class);
                        groupUsers.add(groupUsername);
                    }
                    Collections.reverse(groupUsers);
                    // adapter.notifyItemRangeChanged(0, (int) snapshot.getChildrenCount());
                    // recyclerView.smoothScrollToPosition(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        };
        groupRef.addValueEventListener(getUsers);
    }

    private JSONObject createNotificationJSON(String deviceID, String sender) {
        JSONObject payload = new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            payload.put("to", deviceID);
            payload.put("priority", "high");
            notification.put("title", "New Message");
            notification.put("body", sender + " sent you a message!");
            notification.put("icon", "notificationIcon");
            notification.put("color", "#008000");
            data.put("message", message);
            payload.put("data", data);
            payload.put("notification", notification);
        }
        catch (JSONException exception) {
            exception.printStackTrace();
        }
        return payload;
    }

    private void postPayload(JSONObject notification) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + serverToken);
                conn.setDoOutput(true);
                OutputStream stream = conn.getOutputStream();
                stream.write(notification.toString().getBytes());
                Log.d(TAG, "Status: " + conn.getResponseCode());
                Log.d(TAG, "Response: " + conn.getResponseMessage());
                conn.disconnect();
            }
            catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
                for (StackTraceElement s: e.getStackTrace()) {
                    Log.e(TAG, s.toString());
                }
            }
        });
    }

    private void sendNotification(String targetUsername, String sender) {
        String userPath = "users/" + targetUsername + "/deviceID";
        DatabaseReference userRef = db.getReference().child(userPath);

        // Send notification to deviceID
        ValueEventListener getDeviceID = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String deviceID = snapshot.getValue(String.class);
                    postPayload(createNotificationJSON(deviceID, sender));
                }
                else {
                    Log.e(TAG, "Missing deviceID");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "getDeviceID onCancelled: " + error);
            }
        };
        userRef.addListenerForSingleValueEvent(getDeviceID);
    }

    @Override
    public void onClick(View view) {
        // Send message to group
        EditText messageInput = findViewById(R.id.message_input);
        String messageText = messageInput.getText().toString();
        message = messageInput.getText().toString();
        populateOtherUsers();
        if (view.getId() == R.id.button_send_message && !messageText.equals("")) {
            db.getReference(messagePath).push().setValue(new Message(username, messageText));
            DatabaseReference dbUsernameRef = db.getReference().child("users").child(username);
            ValueEventListener checkValidTarget = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (String uN : groupUsers) {
                            if (!uN.equals(username)) {
                                sendNotification(uN, username);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "checkValidTarget onCancelled: " + error);
                }
            };
            dbUsernameRef.addListenerForSingleValueEvent(checkValidTarget);
            messageInput.getText().clear();
        }
    }

    private void shortToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}