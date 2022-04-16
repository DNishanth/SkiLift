package edu.neu.madcourse.skilift;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(this);

        Intent intent = getIntent();
        this.username = intent.getStringExtra("username");

        // TODO: Display username
        // TODO: Set image to profile picture
        // TODO: System for preferred destinations?
        // TODO: Default location for pickup? (Could store general and/or specific location)
    }

    @Override
    public void onClick(View view) {
        Intent editProfileIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        editProfileIntent.putExtra("username", username);
        startActivity(editProfileIntent);
    }
}