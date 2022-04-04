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
import java.util.concurrent.Semaphore;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

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
//                    String[] hashedPasswordAndSalt = retrieveHashedPasswordAndSalt(username);
//                    String hashedPassword = hashedPasswordAndSalt[0];
//                    String salt = hashedPasswordAndSalt[1];
//                    Log.d(TAG, "Login Hashed Password: " + hashedPassword);
//                    Log.d(TAG, "Login Salt: " + salt);
                    // TODO: Get password from database and check
                    // if (checkPassword(password, hashedPassword)) {
                    if (true) {
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


    private void register(String username, String password) {
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

    private String[] retrieveHashedPasswordAndSalt(String username) {
        // TODO: Doesn't work (semaphores are never released)
        // Get hashed password and salt in thread
        Semaphore passwordSemaphore = new Semaphore(0);
        Semaphore saltSemaphore = new Semaphore(0);
        Semaphore allFinishedSemaphore = new Semaphore(0);
        queryRetrieveHashedPasswordAndSalt thread = new queryRetrieveHashedPasswordAndSalt();
        thread.username = username;
        thread.passwordSemaphore = passwordSemaphore;
        thread.saltSemaphore = saltSemaphore;
        thread.allFinishedSemaphore = allFinishedSemaphore;
        new Thread(thread).start();

        // Free the thread
        try {
            Log.d(TAG, "Got allFinishedSemaphore");
            allFinishedSemaphore.acquire();
        } catch (InterruptedException e) {
            // TODO: Log
            e.printStackTrace();
        }
        String[] hashedPasswordAndSalt = {thread.hashedPasswordAndSalt[0], thread.hashedPasswordAndSalt[1]};
        thread.isFree = true;

        Log.d(TAG, "Hashed password in retrieve: " + hashedPasswordAndSalt[0]);
        Log.d(TAG, "Salt in retrieve: " + hashedPasswordAndSalt[1]);
        return hashedPasswordAndSalt;
    }

    private void shortToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}

class queryRetrieveHashedPasswordAndSalt implements Runnable {

    public String username;
    public String[] hashedPasswordAndSalt = {"", ""};
    public boolean isFree = false;
    public Semaphore passwordSemaphore;
    public Semaphore saltSemaphore;
    public Semaphore allFinishedSemaphore;

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
                    hashedPasswordAndSalt[0] = (String) snapshot.getValue();
                    passwordSemaphore.release();
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
                    hashedPasswordAndSalt[1] = (String) snapshot.getValue();
                    saltSemaphore.release();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(MainActivity.TAG, "onCancelled in Main Activity retrieveHashedPasswordAndSalt(): " + error);
            }
        };
        DatabaseReference saltRef = FirebaseDatabase.getInstance().getReference("users").child(username).child("salt");
        saltRef.addListenerForSingleValueEvent(saltValueEventListener);

        try {
            Log.d(MainActivity.TAG, "Got password semaphore");
            passwordSemaphore.acquire();
            Log.d(MainActivity.TAG, "Got salt semaphore");
            saltSemaphore.acquire();
        } catch (InterruptedException e) {
            // TODO: Log
            e.printStackTrace();
        }
        allFinishedSemaphore.release();
        Log.d(MainActivity.TAG, "Released allFinishedSemaphore");
    }
}