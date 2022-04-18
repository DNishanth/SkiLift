package edu.neu.madcourse.skilift;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FloatingActionButton profileButton = findViewById(R.id.profileHomeFloatingActionButton);
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
}