package edu.neu.madcourse.skilift;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.neu.madcourse.skilift.models.Resorts;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView profilePictureImageView;
    // TODO: Set image
    // TODO: Be able to change name??
    // TODO: Set favorite destination(s)
    // TODO: Set default location for pickup (general and/or specific)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Create the image
        this.profilePictureImageView = findViewById(R.id.editProfilePictureImageView);
        // TODO: Get the image of this user's profile picture and set ImageView

        // Edit image button
        FloatingActionButton editProfilePictureButton = findViewById(R.id.editProfilePictureFloatingActionButton);
        editProfilePictureButton.setOnClickListener(this);

        // Create the resorts dropdown
        // TODO: Create array for this spinner which does not include already included resorts
        // TODO: Allow user to select favorite mountain?
        Spinner resortsDropdown = findViewById(R.id.preferredResortsSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Resorts.resortArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resortsDropdown.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editProfilePictureFloatingActionButton:
                takeProfilePicture();
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
                // TODO: Take the picture
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        }
        else {
            Log.d(MainActivity.TAG, "Device does not have a camera");
            // TODO: Tell user that you need camera to take picture
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                profilePictureImageView.setImageBitmap(bitmap);
                // TODO: Send bitmap as file to cloud storage
                // TODO: Save path of file to user data
            }
        }
    }
}