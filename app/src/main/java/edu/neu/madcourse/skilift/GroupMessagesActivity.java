package edu.neu.madcourse.skilift;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

import edu.neu.madcourse.skilift.adapters.GroupMessageRVAdapter;
import edu.neu.madcourse.skilift.interfaces.IGroupMessageClickListener;
import edu.neu.madcourse.skilift.models.GroupMessage;
import edu.neu.madcourse.skilift.viewholders.GroupMessageViewHolder;

public class GroupMessagesActivity extends AppCompatActivity {
    private static final String TAG = GroupMessagesActivity.class.getSimpleName();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final ArrayList<GroupMessage> groupMessageList = new ArrayList<>();
    private RecyclerView.Adapter<GroupMessageViewHolder> adapter;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messages);
        username = getIntent().getExtras().getString("username");

        // Create recycler view
        createRecyclerView();

        // Populate recycler view with each group the user is a part of
        populateGroupMessages();
    }

    private void createRecyclerView() {
        // Open group message on click
        IGroupMessageClickListener messageGroupClickListener = itemPosition -> {
            String groupID = groupMessageList.get(itemPosition).getGroupID();
            Intent messagesIntent = new Intent(this, MessagesActivity.class);
            messagesIntent.putExtra("username", username);
            messagesIntent.putExtra("groupID", groupID);
            startActivity(messagesIntent);
        };
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new GroupMessageRVAdapter(groupMessageList, messageGroupClickListener);
        RecyclerView recyclerView = findViewById(R.id.group_messages_rv);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    // Get groupIDs of user and populate recycler view with their group members
    private void populateGroupMessages() {
        String groupsPath = "users/" + username + "/groups";
        DatabaseReference groupsRef = db.getReference(groupsPath);
        ValueEventListener getGroupMessages = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot groupID : snapshot.getChildren()) {
                        // Add group members in groupID as item in recycler view
                        addGroupMembers(groupID.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        };
        groupsRef.addListenerForSingleValueEvent(getGroupMessages);
    }

    // Get all groups members under groupID and display alphabetically
    private void addGroupMembers(String groupID) {
        String groupMembersPath = "groups/" + groupID + "/members";
        DatabaseReference groupMembersRef = db.getReference(groupMembersPath);
        ValueEventListener getGroupMembers = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<String> groupMembersList = new ArrayList<>();
                    for (DataSnapshot groupMember : snapshot.getChildren()) {
                        String name = groupMember.getValue(String.class);
                        assert name != null;
                        if (!name.equals(username)) groupMembersList.add(name);
                    }
                    if (!groupMembersList.isEmpty())  {
                        groupMessageList.add(new GroupMessage(groupID, groupMembersList));
                        adapter.notifyItemInserted(groupMembersList.size());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        };
        groupMembersRef.addListenerForSingleValueEvent(getGroupMembers);
    }
}
