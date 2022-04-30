package edu.neu.madcourse.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.cert.PolicyNode;
import java.util.ArrayList;

public class MyRidesActivity extends AppCompatActivity {

     ArrayList<myRideModel> rides = new ArrayList<>();
     myRidesRecyclerViewAdapter adapter;
     String username;
     Button past;
     Button upcoming;
     Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);
        context = this;

      username = getIntent().getExtras().getString("username");

      past = findViewById(R.id.past);
      upcoming = findViewById(R.id.upcoming);
      upcoming.setSelected(true);
      populateRides(R.id.upcoming);

      past.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          past.setSelected(true);
          upcoming.setSelected(false);
          populateRides(R.id.past);
        }
      });
      upcoming.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          past.setSelected(false);
          upcoming.setSelected(true);
          populateRides(R.id.upcoming);
        }
      });

      RecyclerView recyclerView = findViewById(R.id.myRidesRecycler);



      adapter = new myRidesRecyclerViewAdapter(this, rides);

      recyclerView.setAdapter(adapter);

      recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void populateRides(int id){

      DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("rides");
      reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          rides.clear();

          for(DataSnapshot snapshot : dataSnapshot.getChildren()){

            if(snapshot.child("username").getValue().toString().equals(username)){
              String departureLocation = (String) snapshot.child("departureLocation").getValue();
              if (departureLocation == null) {
                  departureLocation = "";
              };
              String destination = snapshot.child("destination").getValue().toString();
              String pickupDate = snapshot.child("pickupDate").getValue().toString();
              String returnDate = snapshot.child("returnDate").getValue().toString();
              String licensePlate = snapshot.child("licensePlate").getValue().toString();
              Long rideTime = Long.parseLong(snapshot.child("pickupDate").getValue().toString());

              myRideModel ride = new myRideModel(departureLocation, destination, pickupDate, returnDate, licensePlate);
              System.out.println("NOW:");
              System.out.println(System.currentTimeMillis());
              System.out.println("Ride time:");
              System.out.println(rideTime);
              if(id == R.id.upcoming && (rideTime > System.currentTimeMillis()) ){
                rides.add(ride);
              }else if(id == R.id.past && (rideTime < System.currentTimeMillis()) ){
                rides.add(ride);
              }

            }

          }
          adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });
    }
}
