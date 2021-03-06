package edu.neu.madcourse.skilift;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.neu.madcourse.skilift.models.Message;
import edu.neu.madcourse.skilift.models.RideInfo;
import edu.neu.madcourse.skilift.models.UserProfile;

public class RideInfoActivity extends AppCompatActivity {
    private static final String TAG = RideInfoActivity.class.getSimpleName();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private String rideHostUsername;
    private String rideID;
    private String groupID;
    private String username;
    private String pickupLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_info);
        username = getIntent().getExtras().getString("username");
        rideID = getIntent().getExtras().getString("rideID");
        groupID = getIntent().getExtras().getString("groupID");
        pickupLocation = getIntent().getExtras().getString(pickupLocation);

        Button messageUserButton = findViewById(R.id.messageUserButton);
        messageUserButton.setOnClickListener(view -> sendMessage());

        Button confirmRideButton = findViewById(R.id.confirmRideButton);
        confirmRideButton.setOnClickListener(view -> confirmRide(rideID));

        getRide(rideID);
    }

    private void confirmRide(String rideID) {
        Intent confirmedIntent = new Intent(this, RideConfirmedActivity.class);
        String myRidesPath = "users/" + username + "/rides";
        DatabaseReference myRidesRef = db.getReference(myRidesPath);
        ValueEventListener checkRideExists = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot rideIDKey : snapshot.getChildren()) {
                        String rideIDVal = rideIDKey.getValue(String.class);
                        if (rideID.equals(rideIDVal)) {
                            shortToast("You have already confirmed this ride!");
                            return;
                        }
                    }
                }
                // Store rideID under user's rides
                DatabaseReference newRideRef = db.getReference(myRidesPath).push();
                newRideRef.setValue(rideID);
                // Store username under group members
                String groupMembersPath = "groups/" + groupID + "/members";
                db.getReference(groupMembersPath).push().setValue(username);
                // Store groupID under username
                String userRideGroupsPath = "users/" + username + "/ride_groups";
                DatabaseReference userGroupsRef = db.getReference(userRideGroupsPath).push();
                userGroupsRef.setValue(groupID);

                confirmedIntent.putExtra("username", username);
                confirmedIntent.putExtra("rideID", rideID);
                confirmedIntent.putExtra("groupID", groupID);
                confirmedIntent.putExtra("pickupLocation", pickupLocation);
                startActivity(confirmedIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        };
        myRidesRef.addListenerForSingleValueEvent(checkRideExists);
    }

    private void getRide(String rideID) {
        DatabaseReference rideRef = db.getReference("rides/" + rideID);
        ValueEventListener getRide = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RideInfo rideInfo = snapshot.getValue(RideInfo.class);
                    assert rideInfo != null;
                    getUserProfile(rideInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        };
        rideRef.addListenerForSingleValueEvent(getRide);
    }

    private void getUserProfile(RideInfo rideInfo) {
        rideHostUsername = rideInfo.getUsername();
        String profilePath = "users/" + rideHostUsername + "/profile";
        DatabaseReference profileRef = db.getReference(profilePath);
        ValueEventListener getProfile = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    assert userProfile != null;
                    setupUI(rideInfo, userProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        };
        profileRef.addListenerForSingleValueEvent(getProfile);
    }

    private void setupUI(RideInfo rideInfo, UserProfile userProfile) {
        ImageView profilePicture = findViewById(R.id.profilePicture);
        TextView username = findViewById(R.id.rideInfoUsername);
        RatingBar userRating = findViewById(R.id.rideInfoUserRating);
        TextView departureLocation = findViewById(R.id.rideInfoDepLocation);
        TextView destination = findViewById(R.id.rideInfoDestination);
        TextView departureDate = findViewById(R.id.rideInfoDepDate);
        TextView returnDate = findViewById(R.id.rideInfoReturnDate);
        TextView memberDate = findViewById(R.id.rideInfoMemberDate);
        TextView ridesCompleted = findViewById(R.id.rideInfoRidesCompleted);
        TextView skiType = findViewById(R.id.rideInfoSkiType);
        TextView favoriteMountains = findViewById(R.id.rideInfoFavMountains);
        TextView funFact = findViewById(R.id.rideInfoFunFact);

        Log.d(TAG, "pickupdate: " + rideInfo.getPickupUnixTimestamp() * 1000);
        Log.d(TAG, "returndate: " + rideInfo.getReturnUnixTimestamp() * 1000);

        username.setText(rideInfo.getUsername());
        userRating.setRating((float) userProfile.getRating());
        departureLocation.setText(formatString(R.string.departureLocation,
                rideInfo.getDepartureLocation()));
        destination.setText(formatString(R.string.destination,
                rideInfo.getDestination()));
        DateFormat dateFormat = new SimpleDateFormat("M/d/y 'at' h:mm a", Locale.US);
        departureDate.setText(formatString(
                R.string.departureDate,
                dateFormat.format(new Date(rideInfo.getPickupUnixTimestamp() * 1000))));
        returnDate.setText(formatString(
                R.string.returnDate,
                dateFormat.format(new Date(rideInfo.getReturnUnixTimestamp() * 1000))));
        dateFormat = new SimpleDateFormat("M/d/y", Locale.US);
        memberDate.setText(formatString(R.string.memberDate,
                dateFormat.format(new Date(userProfile.getMemberDate() * 1000))));
        ridesCompleted.setText(formatString(R.string.ridesCompleted,
                String.valueOf(userProfile.getRidesCompleted())));
        skiType.setText(formatString(R.string.skiType,
                userProfile.getSkiType()));
        favoriteMountains.setText(formatString(R.string.favoriteMountains,
                userProfile.getFavoriteMountains()));
        funFact.setText(formatString(R.string.funFact,
                userProfile.getFunFact()));
        // Set the profile picture
        setProfilePicture(profilePicture, rideInfo.getUsername());
    }

    private Spanned formatString(int resId, String text) {
        return Html.fromHtml(getString(resId, text), FROM_HTML_MODE_LEGACY);
    }

    private void sendMessage() {
        ArrayList<String> users = new ArrayList<>();
        users.add(username);
        users.add(rideHostUsername);
        users.sort(String.CASE_INSENSITIVE_ORDER);
        String groupMembers = users.toString();
        String groupID = groupMembers.substring(1, groupMembers.length() - 1);

        Intent messagesIntent = new Intent(this, MessagesActivity.class);
        messagesIntent.putExtra("username", username);
        messagesIntent.putExtra("groupID", groupID);
        startActivity(messagesIntent);

        DatabaseReference groupRef = db.getReference("groups/" + groupID);
        ValueEventListener getGroup = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    startActivity(messagesIntent);
                }
                else {
                    String groupMembersPath = "groups/" + groupID + "/members";
                    users.forEach(user -> {
                        // Store username under group
                        DatabaseReference groupMembersRef = db.getReference(groupMembersPath).push();
                        groupMembersRef.setValue(user);
                        // Store groupID under username
                        String userGroupsPath = "users/" + user + "/groups";
                        DatabaseReference userGroupsRef = db.getReference(userGroupsPath).push();
                        userGroupsRef.setValue(groupID);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        };
        groupRef.addListenerForSingleValueEvent(getGroup);
    }

    private void setProfilePicture(ImageView profilePictureImageView, String username) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference profilePictureRef = storage.getReference().child("profile_pictures").child(username+".jpg");
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

    private void shortToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}