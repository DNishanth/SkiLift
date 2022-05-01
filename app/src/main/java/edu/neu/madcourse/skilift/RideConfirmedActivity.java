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
import android.widget.TextView;

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
import java.util.Date;
import java.util.Locale;

import edu.neu.madcourse.skilift.models.RideInfo;

public class RideConfirmedActivity extends AppCompatActivity {
    private static final String TAG = RideConfirmedActivity.class.getSimpleName();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private String pickupLocation;
    private String rideHostUsername;
    private String username;
    private String rideID;
    private String groupID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_confirmed);
        username = getIntent().getExtras().getString("username");
        rideID = getIntent().getExtras().getString("rideID");
        groupID = getIntent().getExtras().getString("groupID");
        pickupLocation = getIntent().getExtras().getString("pickupLocation");

        Button messageGroupButton = findViewById(R.id.messageGroupButton);
        messageGroupButton.setOnClickListener(view -> {
            Intent messagesIntent = new Intent(this, MessagesActivity.class);
            messagesIntent.putExtra("username", username);
            messagesIntent.putExtra("groupID", groupID);
            startActivity(messagesIntent);
        });

        Button viewRidesButton = findViewById(R.id.viewRidesButton);
        viewRidesButton.setOnClickListener(view -> {
            Intent myRidesIntent = new Intent(this, MyRidesActivity.class);
            myRidesIntent.putExtra("username", username);
            startActivity(myRidesIntent);
        });

        Button homeButton = findViewById(R.id.returnHomeButton);
        homeButton.setOnClickListener(view -> {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            homeIntent.putExtra("username", username);
            startActivity(homeIntent);
        });

        getRide(rideID);
    }

    private void getRide(String rideID) {
        DatabaseReference rideRef = db.getReference("rides/" + rideID);
        ValueEventListener getRide = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RideInfo rideInfo = snapshot.getValue(RideInfo.class);
                    assert rideInfo != null;
                    setupUI(rideInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        };
        rideRef.addListenerForSingleValueEvent(getRide);
    }

    private void setupUI(RideInfo rideInfo) {
        ImageView profilePicture = findViewById(R.id.profilePicture);
        TextView confirmedRideHost = findViewById(R.id.confirmedRideHost);
        TextView destination = findViewById(R.id.rideInfoDestination);
        TextView departureDate = findViewById(R.id.rideInfoDepDate);
        TextView returnDate = findViewById(R.id.rideInfoReturnDate);
        TextView pickupLocationField = findViewById(R.id.pickupLocation);
        TextView car = findViewById(R.id.car);
        TextView plate = findViewById(R.id.plate);

        confirmedRideHost.setText(formatString(R.string.with, rideInfo.getUsername()));
        destination.setText(formatString(R.string.destination,
                rideInfo.getDestination()));
        DateFormat dateFormat = new SimpleDateFormat("M/d/y 'at' h:mm a", Locale.US);
        departureDate.setText(formatString(
                R.string.departureDate,
                dateFormat.format(new Date(rideInfo.getPickupUnixTimestamp() * 1000))));
        returnDate.setText(formatString(
                R.string.returnDate,
                dateFormat.format(new Date(rideInfo.getReturnUnixTimestamp() * 1000))));
        pickupLocationField.setText(formatString(R.string.pickupLocation,
                pickupLocation));
        car.setText(formatString(R.string.car, rideInfo.getCarModel()));
        plate.setText(formatString(R.string.plate, rideInfo.getLicensePlate()));

        // Set the profile picture
        setProfilePicture(profilePicture, rideInfo.getUsername());
    }

    private Spanned formatString(int resId, String text) {
        return Html.fromHtml(getString(resId, text), FROM_HTML_MODE_LEGACY);
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
}