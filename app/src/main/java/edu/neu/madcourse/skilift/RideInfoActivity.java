package edu.neu.madcourse.skilift;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.neu.madcourse.skilift.models.RideInfo;
import edu.neu.madcourse.skilift.models.UserProfile;

public class RideInfoActivity extends AppCompatActivity {
    private static final String TAG = FoundRidesActivity.class.getSimpleName();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_info);

        getRide(getIntent().getExtras().getString("rideID"));
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
        String profilePath = "users/" + rideInfo.getUsername() + "/profile";
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

        username.setText(rideInfo.getUsername());
        userRating.setRating((float) userProfile.getRating());
        departureLocation.setText(formatString(R.string.departureLocation,
                rideInfo.getDepartureLocation()));
        destination.setText(formatString(R.string.destination,
                rideInfo.getDestination()));
        DateFormat dateFormat = new SimpleDateFormat("M/d/y 'at' h:mm a", Locale.US);
        departureDate.setText(formatString(
                R.string.departureDate, dateFormat.format(new Date(rideInfo.getPickupDate()))));
        returnDate.setText(formatString(R.string.returnDate,
                dateFormat.format(new Date(rideInfo.getReturnDate()))));
        dateFormat = new SimpleDateFormat("M/d/y", Locale.US);
        memberDate.setText(formatString(R.string.memberDate,
                dateFormat.format(new Date(userProfile.getMemberDate()))));
        ridesCompleted.setText(formatString(R.string.ridesCompleted,
                String.valueOf(userProfile.getRidesCompleted())));
        skiType.setText(formatString(R.string.skiType,
                userProfile.getSkiType()));
        favoriteMountains.setText(formatString(R.string.favoriteMountains,
                userProfile.getFavoriteMountains()));
        funFact.setText(formatString(R.string.funFact,
                userProfile.getFunFact()));
    }

    private Spanned formatString(int resId, String text) {
        return Html.fromHtml(getString(resId, text), FROM_HTML_MODE_LEGACY);
    }
}