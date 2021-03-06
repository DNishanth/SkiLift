package edu.neu.madcourse.skilift;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    String username;
    StorageReference profilePictureRef;
    FloatingActionButton profileButton;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
        setProfilePicture();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.profileButton = findViewById(R.id.profileHomeFloatingActionButton);
        FloatingActionButton messagesButton = findViewById(R.id.messagesHomeFloatingActionButton);
        Button myRidesButton = findViewById(R.id.myRidesHomeButton);
        Button findRideButton = findViewById(R.id.findRideHomeButton);
        Button giveRideButton = findViewById(R.id.giveRideHomeButton);
        profileButton.setOnClickListener(this);
        messagesButton.setOnClickListener(this);
        myRidesButton.setOnClickListener(this);
        findRideButton.setOnClickListener(this);
        giveRideButton.setOnClickListener(this);

        Intent intent = getIntent();
        this.username = intent.getStringExtra("username");

        // Set image to profile picture
        this.profilePictureRef = storage.getReference().child("profile_pictures").child(username+".jpg");
        setProfilePicture();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profileHomeFloatingActionButton:
                Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                profileIntent.putExtra("username", username);
                startActivity(profileIntent);
                break;
            case R.id.messagesHomeFloatingActionButton:
                Intent messagesIntent = new Intent(HomeActivity.this, GroupMessagesActivity.class);
                messagesIntent.putExtra("username", username);
                startActivity(messagesIntent);
                break;
            case R.id.myRidesHomeButton:
                Intent myRidesIntent = new Intent(HomeActivity.this, MyRidesActivity.class);
                myRidesIntent.putExtra("username", username);
                startActivity(myRidesIntent);
                break;
            case R.id.findRideHomeButton:
                Intent findRideIntent = new Intent(HomeActivity.this, FindRideActivity.class);
                findRideIntent.putExtra("username", username);
                startActivity(findRideIntent);
                break;
            case R.id.giveRideHomeButton:
                Intent giveRideIntent = new Intent(HomeActivity.this, GiveRideActivity.class);
                giveRideIntent.putExtra("username", username);
                startActivity(giveRideIntent);
                break;
        }
    }

    private void setProfilePicture() {
        final long ONE_MEGABYTE = 1024 * 1024;
        profilePictureRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Set profile picture ImageView to downloaded data
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profileButton.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Could not get image, so set profile pic to default
                profileButton.setImageResource(R.drawable.default_user_img);
            }
        });
    }
}