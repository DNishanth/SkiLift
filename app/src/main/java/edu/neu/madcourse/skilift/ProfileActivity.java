package edu.neu.madcourse.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import edu.neu.madcourse.skilift.models.UserProfile;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    String profileUsername;
    String username;
    ImageView profilePictureImageView;
    StorageReference profilePictureRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onStart() {
      super.onStart();
      getProfileData();
      setProfilePicture();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView usernameTextView = findViewById(R.id.profileUsernameTextView);
        Button editProfileButton = findViewById(R.id.editProfileButton);
        Button setRatingButton = findViewById(R.id.profileSetRatingButton);
        this.profilePictureImageView = findViewById(R.id.profilePictureImageView);
        editProfileButton.setOnClickListener(this);
        setRatingButton.setOnClickListener(this);

        Intent intent = getIntent();
        this.username = intent.getStringExtra("username");
        this.profileUsername = intent.getStringExtra("profileUsername");
        if (profileUsername == null || profileUsername.equals(username)) {
            profileUsername = username;
            setRatingButton.setVisibility(View.GONE);
        }
        if (!profileUsername.equals(username)) {
            editProfileButton.setVisibility(View.GONE);
        }

        // Set image to profile picture
        this.profilePictureRef = storage.getReference().child("profile_pictures").child(profileUsername+".jpg");
        setProfilePicture();

        // Get UserProfile data
        getProfileData();

        // Display profileUsername
        usernameTextView.setText(this.profileUsername);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.editProfileButton:
                Intent editProfileIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                editProfileIntent.putExtra("username", username);
                startActivity(editProfileIntent);
                break;
            case R.id.profileSetRatingButton:
                openRatingPickerDialog();
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
                        skiTypeTextView.setText("Skier Type: " + userProfile.getSkiType());
                        favoriteMountainsTextView.setText("Favorite Mountain: " + userProfile.getFavoriteMountains());
                        funFactTextView.setText("Fun Fact: " + userProfile.getFunFact());
                        if (userProfile.getNumRatings() == 0) {
                            ratingTextView.setText("No ratings");
                        }
                        else if (String.valueOf(userProfile.getRating()).length() > 3) {
                            ratingTextView.setText("Rating: " + String.valueOf(userProfile.getRating()).substring(0, 3) + " - " + userProfile.getNumRatings() + " Ratings");
                        }
                        else {
                            ratingTextView.setText("Rating: " + String.valueOf(userProfile.getRating()) + " - " + userProfile.getNumRatings() + " Ratings");
                        }
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
        DatabaseReference dbRef = db.getReference().child("users").child(profileUsername).child("profile");
        dbRef.addListenerForSingleValueEvent(profileListener);
    }

    private void openRatingPickerDialog() {
        NumberPicker ratingNumberPicker = new NumberPicker(ProfileActivity.this);
        ratingNumberPicker.setMinValue(1);
        ratingNumberPicker.setMaxValue(5);

        FrameLayout frameLayout = new FrameLayout(ProfileActivity.this);
        frameLayout.addView(ratingNumberPicker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
        alertDialogBuilder.setView(frameLayout);
        alertDialogBuilder.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
            // Update rating with value
            updateRating(ratingNumberPicker.getValue());
        });
        alertDialogBuilder.setNegativeButton(android.R.string.cancel, null);
        alertDialogBuilder.show();
    }

    private void updateRating(int newRating) {
        // Update the rating in the database
        ValueEventListener updateRatingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        double rating = userProfile.getRating();
                        int numRatings = userProfile.getNumRatings();
                        double newTotalRating = ((rating * numRatings) + newRating) / (numRatings + 1);
                        UserProfile newProfile = new UserProfile(
                                userProfile.getMemberDate(),
                                userProfile.getRidesCompleted(),
                                userProfile.getSkiType(),
                                userProfile.getFavoriteMountains(),
                                userProfile.getFunFact(),
                                userProfile.getProfilePictureSrc(),
                                newTotalRating,
                                numRatings + 1
                        );
                        DatabaseReference ref = db.getReference().child("users").child(profileUsername).child("profile");
                        Task<Void> updateRating = ref.setValue(newProfile);
                        updateRating.addOnCompleteListener(task -> {
                            if (!updateRating.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Failed to update user rating", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ProfileActivity.this, "Successfully rated user " + String.valueOf(newRating) + " stars", Toast.LENGTH_SHORT).show();
                                getProfileData();
                            }
                        });
                    }
                    else {
                        Log.w(MainActivity.TAG, "Null userProfile in getProfileData in ProfileActivity");
                    }
                }
                else {
                    Log.e(MainActivity.TAG, "");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(MainActivity.TAG, "onCancelled in Main Activity register(): " + error);
            }
        };
        DatabaseReference dbRef = db.getReference().child("users").child(profileUsername).child("profile");
        dbRef.addListenerForSingleValueEvent(updateRatingListener);
    }
}
