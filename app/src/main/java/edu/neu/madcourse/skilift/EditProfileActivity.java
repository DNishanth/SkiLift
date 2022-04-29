package edu.neu.madcourse.skilift;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import edu.neu.madcourse.skilift.models.Resorts;
import edu.neu.madcourse.skilift.models.UserProfile;

// TODO: Fix layout (resorts spinner and fun fact)
public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView profilePictureImageView;
    StorageReference profilePictureRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    String username;
    ArrayAdapter<String> resortsDropdownAdapter;
    ArrayAdapter<String> skierTypeDropdownAdapter;
    UserProfile userProfile;
    Spinner skiTypeSpinner;
    Spinner favoriteMountainsSpinner;
    EditText funFactEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        this.username = intent.getStringExtra("username");

        // Set reference to path of profile picture
        this.profilePictureRef = storage.getReference().child("profile_pictures").child(username+".jpg");

        // Set profile picture to stored image
        this.profilePictureImageView = findViewById(R.id.editProfilePictureImageView);
        setProfilePicture();

        // Get each of the editable fields
        skiTypeSpinner = findViewById(R.id.editProfileSkiTypeSpinner);
        favoriteMountainsSpinner = findViewById(R.id.preferredResortsSpinner);
        funFactEditText = findViewById(R.id.editProfileFunFactEditText);

        // Create skier type dropdown
        skierTypeDropdownAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, new String[]{"Ski", "Snowboard"});
        skierTypeDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skiTypeSpinner.setAdapter(skierTypeDropdownAdapter);

        // Create the resorts dropdown
        resortsDropdownAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Resorts.resortArray);
        resortsDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        favoriteMountainsSpinner.setAdapter(resortsDropdownAdapter);

        // Get UserProfile data
        getProfileData();

        // Edit image button
        FloatingActionButton editProfilePictureButton = findViewById(R.id.editProfilePictureFloatingActionButton);
        editProfilePictureButton.setOnClickListener(this);

        // Save profile button
        FloatingActionButton editProfileSavePrefsButton = findViewById(R.id.editProfileSavePrefsFloatingActionButton);
        editProfileSavePrefsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editProfilePictureFloatingActionButton:
                takeProfilePicture();
                break;
            case R.id.editProfileSavePrefsFloatingActionButton:
                savePrefs();
                break;
        }
    }

    private void takeProfilePicture() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            // Get camera runtime permission
            if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission, so ask
                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
            }
            else {
                // Take the picture
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        }
        else {
            // TODO: Tell user that you need camera to take picture
            Log.d(MainActivity.TAG, "Device does not have a camera");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                profilePictureImageView.setImageBitmap(bitmap);
                savePictureToCloud(bitmap);
            }
        }
    }

    private void savePictureToCloud(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profilePictureRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // TODO: Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(), "Could not upload image", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
            }
        });
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
                    userProfile = snapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        skiTypeSpinner.setSelection(skierTypeDropdownAdapter.getPosition(userProfile.getSkiType()));
                        favoriteMountainsSpinner.setSelection(resortsDropdownAdapter.getPosition(userProfile.getFavoriteMountains()));
                        funFactEditText.setText(userProfile.getFunFact());
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

    private void savePrefs() {
        // Update profile information database
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile newUserProfile = new UserProfile(userProfile.getMemberDate(),
                        userProfile.getRidesCompleted(),
                        skiTypeSpinner.getSelectedItem().toString(),
                        favoriteMountainsSpinner.getSelectedItem().toString(),
                        funFactEditText.getText().toString(),
                        userProfile.getProfilePictureSrc(),
                        userProfile.getRating(),
                        userProfile.getNumRatings());
                DatabaseReference ref = db.getReference().child("users").child(username).child("profile");
                Task<Void> updateProfile = ref.setValue(newUserProfile);
                updateProfile.addOnCompleteListener(task -> {
                    if (!updateProfile.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Profile failed to update", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(MainActivity.TAG, "onCancelled in Main Activity register(): " + error);
            }
        };
        DatabaseReference dbRef = db.getReference().child("users").child(username).child("profile");
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }
}