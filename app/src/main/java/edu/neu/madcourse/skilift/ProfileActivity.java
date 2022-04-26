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
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import edu.neu.madcourse.skilift.models.UserProfile;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    String username;
    ImageView profilePictureImageView;
    StorageReference profilePictureRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView usernameTextView = findViewById(R.id.profileUsernameTextView);
        Button editProfileButton = findViewById(R.id.editProfileButton);
        this.profilePictureImageView = findViewById(R.id.profilePictureImageView);
        editProfileButton.setOnClickListener(this);

        Intent intent = getIntent();
        this.username = intent.getStringExtra("username");

        // Set image to profile picture
        this.profilePictureRef = storage.getReference().child("profile_pictures").child(username+".jpg");
        setProfilePicture();

        // Get UserProfile data
        getProfileData();

        // Display username
        usernameTextView.setText(this.username);

        // TODO: System for preferred destinations?
        // TODO: Default location for pickup? (Could store general and/or specific location)
    }

    @Override
    public void onClick(View view) {
        Intent editProfileIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        editProfileIntent.putExtra("username", username);
        startActivity(editProfileIntent);
    }

    private void setProfilePicture() {
        final long ONE_MEGABYTE = 1024 * 1024;
        profilePictureRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Set profile picture ImageView to downloaded data
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePictureImageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Could not get image, so set profile pic to default
                profilePictureImageView.setImageResource(R.drawable.default_user_img);
            }
        });
    }

    private void getProfileData() {
        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        TextView ridesCompletedTextView = findViewById(R.id.profileRidesCompletedTextView);
                        TextView skiTypeTextView = findViewById(R.id.profileSkiTypeTextView);
                        TextView favoriteMountainsTextView = findViewById(R.id.profileFavoriteMountainsTextView);
                        TextView funFactTextView = findViewById(R.id.profileFunFactTextView);
                        TextView ratingTextView = findViewById(R.id.profileRatingTextView);
                        ridesCompletedTextView.setText("Rides Completed: " + String.valueOf(userProfile.getRidesCompleted()));
                        skiTypeTextView.setText("Skiier Type: " + userProfile.getSkiType());
                        favoriteMountainsTextView.setText("Favorite Mountains: " + userProfile.getFavoriteMountains());
                        funFactTextView.setText("Fun Fact: " + userProfile.getFunFact());
                        ratingTextView.setText("Rating: " + String.valueOf(userProfile.getRating()));
                    }
                    else {
                        Log.w(MainActivity.TAG, "Null userProfile in getProfileData in ProfileActivity");
                    }
                } catch (Exception e) {
                    Log.e(MainActivity.TAG, "Error in getProfileData in ProfileActivity");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(MainActivity.TAG, "Could not get profile data");
            }
        };
        DatabaseReference dbRef = db.getReference().child("users").child(username).child("profile");
        dbRef.addListenerForSingleValueEvent(profileListener);
    }
}