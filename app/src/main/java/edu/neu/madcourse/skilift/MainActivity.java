package edu.neu.madcourse.skilift;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MyTag";
    private FirebaseDatabase db;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.usernameEditText = findViewById(R.id.usernameInputEditText);
        this.passwordEditText = findViewById(R.id.passwordInputEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        db = FirebaseDatabase.getInstance();
    }

    public void updateDeviceID(String username) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Device ID could not be updated");
            }
            else {
                String deviceID = task.getResult();
                String tokenPath = "users/" + username;
                DatabaseReference updateRef = db.getReference().child(tokenPath);
                Map<String, Object> updates = new HashMap<>(); // Stores any key value changes
                updates.put("deviceID", deviceID);
                updateRef.updateChildren(updates);
            }
        });
    }

    @Override
    public void onClick(View view) {
        String username = this.usernameEditText.getText().toString();
        String password = this.passwordEditText.getText().toString();
        // TODO: Do stricter username and password checking here
        if (username.equals("") || password.equals("")) {
            shortToast("Please enter a username and password");
            return;
        }
        switch (view.getId()) {
            case R.id.loginButton:
                login(username, password);
                break;
            case R.id.registerButton:
                register(username, password);
                break;
        }
    }


    private void login(String username, String password) {
        // TODO: Check the username with the hashed password
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Intent i =new Intent(MainActivity.this, HomeActivity.class);
                    i.putExtra("username", username);
                    updateDeviceID(username);
                    startActivity(i);
                }
                else {
                    shortToast("User is not registered");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled in Main Activity login()");
            }
        };

        DatabaseReference dbRef = db.getReference().child("users").child(username);
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }


    private void register(String username, String password) {
        // TODO: Also store the hashed password
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    shortToast("Username is already registered");
                }
                else {
                    // TODO: These are placeholder values, will change once we decide on a db model
                    DatabaseReference ref = db.getReference("users").child(username);
                    Task<Void> createUser = ref.setValue("password");
                    createUser.addOnCompleteListener(task -> {
                        if (!createUser.isSuccessful()) {
                            shortToast("Register user failed");
                        }
                        else {
                            shortToast("Username " + username + " is now registered");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        };
        DatabaseReference dbRef = db.getReference().child("users").child(username);
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void shortToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}