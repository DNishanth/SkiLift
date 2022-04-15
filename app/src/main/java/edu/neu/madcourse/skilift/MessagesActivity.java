package edu.neu.madcourse.skilift;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.neu.madcourse.skilift.models.Message;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Button createGroupButton = findViewById(R.id.button_create_group);
        Button sendMessageButton = findViewById(R.id.button_send_message);

        createGroupButton.setOnClickListener(this);
        sendMessageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        TextView groupIDField = findViewById(R.id.group_id_field);
        if (view.getId() == R.id.button_create_group) {
            // Create group and save groupID under each user in group
            String groupID = db.getReference("groups").push().getKey();
            String groupMembersPath = "groups/" + groupID + "/members";
            groupIDField.setText(groupID);

            String[] users = {"test", "test2"};
            for (String user : users) {
                // Store username under group
                DatabaseReference groupMembersRef = db.getReference(groupMembersPath).push();
                groupMembersRef.setValue(user);

                // Store groupID under username
                String userGroupsPath = "users/" + user + "/groups";
                DatabaseReference userGroupsRef = db.getReference(userGroupsPath).push();
                userGroupsRef.setValue(groupID);
            }
        }
        else if (view.getId() == R.id.button_send_message) {
            // Send message in group
            String groupID = groupIDField.getText().toString();
            String messagePath = "groups/" + groupID + "/messages";
            Message message = new Message("sendingUsername", "message text");
            Log.d("MessagesActivity", messagePath);
            db.getReference(messagePath).push().setValue(message);
        }
    }
}