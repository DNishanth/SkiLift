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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import edu.neu.madcourse.skilift.models.UserProfile;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MyTag";
    private FirebaseDatabase db;
    private EditText usernameEditText;
    private EditText passwordEditText;
    public String tempHashedPassword = "";
    public String tempSalt = "";
    private final boolean DISABLE_PASSWORD_CHECKING = true;

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
        MainActivity mainActivity = this;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // TODO: Have loading icon while logging in
                    queryRetrieveHashedPasswordAndSalt thread = new queryRetrieveHashedPasswordAndSalt();
                    thread.username = username;
                    thread.password = password;
                    thread.mainActivity = mainActivity;
                    new Thread(thread).start();
                }
                else {
                    shortToast("User is not registered");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled in Main Activity login(): " + error);
            }
        };

        DatabaseReference dbRef = db.getReference().child("users").child(username);
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public void continueLogin(String username, String password) {
        Log.d(TAG, "Continuing login");
        // Get password from database and check valid (if password checking disabled flag is off)
        if (!this.tempHashedPassword.equals("") && !this.tempSalt.equals("")) {
             if (this.DISABLE_PASSWORD_CHECKING || checkPassword(password, this.tempHashedPassword, this.tempSalt)) {
                 this.tempHashedPassword = "";
                 this.tempSalt = "";
                 Intent i = new Intent(MainActivity.this, HomeActivity.class);
                 i.putExtra("username", username);
                 updateDeviceID(username);
                 startActivity(i);
            }
            else {
                // TODO: Should have a limit of incorrect passwords for period of time (like phone login locks you out)
                shortToast("Password incorrect");
            }
        }
    }


    private void register(String username, String password) {
        // TODO: Do queries in separate thread?
        // TODO: Make loading icon
        try {
            // Store the username
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        shortToast("Username is already registered");
                    }
                    else {
                        DatabaseReference ref = db.getReference("users").child(username);
                        Task<Void> createUser = ref.setValue("password");
                        createUser.addOnCompleteListener(task -> {
                            if (!createUser.isSuccessful()) {
                                shortToast("Register user failed");
                            }
                            else {
                                shortToast("User " + username + " is now registered");
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled in Main Activity register(): " + error);
                }
            };
            DatabaseReference dbRef = db.getReference().child("users").child(username);
            dbRef.addListenerForSingleValueEvent(valueEventListener);

            // Store the password
            String[] hashedPasswordWithSalt = hashPassword(password, "");
            String hashedPassword = hashedPasswordWithSalt[0];
            String salt = hashedPasswordWithSalt[1];
            ValueEventListener valueEventListener2 = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        shortToast("Username is already registered");
                    }
                    else {
                        DatabaseReference ref = db.getReference("users").child(username).child("password");
                        Task<Void> createUser = ref.setValue(hashedPassword);
                        createUser.addOnCompleteListener(task -> {
                            if (!createUser.isSuccessful()) {
                                shortToast("Register user failed");
                            }
                            else {
                                shortToast("User " + username + " is now registered");
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled in Main Activity register(): " + error);
                }
            };
            DatabaseReference dbRef2 = db.getReference().child("users").child(username).child("password");
            dbRef2.addListenerForSingleValueEvent(valueEventListener2);

            // Store the salt
            ValueEventListener valueEventListener3 = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        shortToast("Username is already registered");
                    }
                    else {
                        DatabaseReference ref = db.getReference("users").child(username).child("salt");
                        Task<Void> createUser = ref.setValue(salt);
                        createUser.addOnCompleteListener(task -> {
                            if (!createUser.isSuccessful()) {
                                shortToast("Register user failed");
                            }
                            else {
                                shortToast("User " + username + " is now registered");
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled in Main Activity register(): " + error);
                }
            };
            DatabaseReference dbRef3 = db.getReference().child("users").child(username).child("salt");
            dbRef3.addListenerForSingleValueEvent(valueEventListener3);

            // Add profile information to database
            ValueEventListener valueEventListener4 = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        shortToast("Username is already registered");
                    }
                    else {
                        UserProfile userProfile = new UserProfile(System.currentTimeMillis()/1000L, 0, "", "", "", "profile_pictures/" + username + ".jpg", 0.0, 0);
                        DatabaseReference ref = db.getReference().child("users").child(username).child("profile");
                        Task<Void> createUser = ref.setValue(userProfile);
                        createUser.addOnCompleteListener(task -> {
                            if (!createUser.isSuccessful()) {
                                shortToast("Register user failed");
                            }
                            else {
                                shortToast("User " + username + " is now registered");
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled in Main Activity register(): " + error);
                }
            };
            DatabaseReference dbRef4 = db.getReference().child("users").child(username).child("profile");
            dbRef4.addListenerForSingleValueEvent(valueEventListener4);
        } catch (Exception e) {
            Log.e(TAG, "Error in hashing: " + e.getMessage());
        }
    }

    private String[] hashPassword(String password, String inputSalt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt;
        if (inputSalt.equals("")) {
            // Create salt
            SecureRandom rand = new SecureRandom();
            salt = new byte[16];
            rand.nextBytes(salt);
        }
        else {
            // Get bytes back from input salt
            salt = Base64.getDecoder().decode(inputSalt);
        }
        String saltStr = Base64.getEncoder().encodeToString(salt);

        // Make hash with salt
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = keyFactory.generateSecret(spec).getEncoded();
        String hashStr = Base64.getEncoder().encodeToString(hash);
        String[] hashWithSalt = {hashStr, saltStr};
        Log.d(TAG, "Hashed password: " + hashStr);
        Log.d(TAG, "Salt: " + saltStr);
        return hashWithSalt;
    }

    private boolean checkPassword(String password, String hashedPassword, String salt) {
        try {
            String[] hashedPasswordWithSalt = hashPassword(password, salt);
            return hashedPasswordWithSalt[0].equals(hashedPassword);
        } catch (Exception e) {
            Log.e(TAG, "Error in checkPassword: " + e.getMessage());
        }
        return false;
    }

    private void shortToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}

class queryRetrieveHashedPasswordAndSalt implements Runnable {

    public String username;
    public String password;
    public MainActivity mainActivity;

    @Override
    public void run() {
        retrieveHashedPasswordAndSalt();
    }

    private void retrieveHashedPasswordAndSalt() {
        // Get password
        ValueEventListener passwordValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(MainActivity.TAG, (String)snapshot.getValue());
                    mainActivity.tempHashedPassword = (String) snapshot.getValue();
                    mainActivity.continueLogin(username, password);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(MainActivity.TAG, "onCancelled in Main Activity retrieveHashedPasswordAndSalt(): " + error);
            }
        };
        DatabaseReference passwordRef = FirebaseDatabase.getInstance().getReference("users").child(username).child("password");
        passwordRef.addListenerForSingleValueEvent(passwordValueEventListener);

        // Get salt
        ValueEventListener saltValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(MainActivity.TAG, (String)snapshot.getValue());
                    mainActivity.tempSalt = (String) snapshot.getValue();
                    mainActivity.continueLogin(username, password);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(MainActivity.TAG, "onCancelled in Main Activity retrieveHashedPasswordAndSalt(): " + error);
            }
        };
        DatabaseReference saltRef = FirebaseDatabase.getInstance().getReference("users").child(username).child("salt");
        saltRef.addListenerForSingleValueEvent(saltValueEventListener);
    }
}