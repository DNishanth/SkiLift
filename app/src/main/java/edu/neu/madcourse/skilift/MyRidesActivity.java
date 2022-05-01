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

import java.sql.Timestamp;

import java.util.ArrayList;

public class MyRidesActivity extends AppCompatActivity {

     ArrayList<myRideModel> rides = new ArrayList<>();
     ArrayList<String> passengerRideIds = new ArrayList<>();
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

    private String helperDateFormat(String date){
      String newDate = "";
      int i;
      if(date.length() == 8){
        for(i = 0; i < date.length(); i++){
          if(i == 1 || i == 3) {
            newDate += date.charAt(i) + "/";
          }else{
            newDate += date.charAt(i);
          }
        }
      }else{
        for(i = 0; i < date.length(); i++){
          if(i == 0 || i == 2) {
            newDate += date.charAt(i) + "/";
          }else{
            newDate += date.charAt(i);
          }
        }
      }
      return newDate;
    }

    private void populateRides(int id){

      DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("rides");
      DatabaseReference passengerReference = FirebaseDatabase.getInstance().getReference().child(username).child("rides");
      passengerReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

            String rideId = snapshot.getValue().toString();
            passengerRideIds.add(rideId);
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });


      reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          rides.clear();

          for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String rideId = snapshot.child("rideID").getValue().toString();
            if (snapshot.child("username").getValue().toString().equals(username) || passengerRideIds.contains(rideId)) {
              String departureLocation = (String) snapshot.child("departureLocation").getValue();
              if (departureLocation == null) {
                departureLocation = "";
              };
              String destination = snapshot.child("destination").getValue().toString();
              String pickupDate = helperDateFormat(snapshot.child("pickupDate").getValue().toString());
              String pickupTime = snapshot.child("pickupTime").getValue().toString();
              String fullPickup = pickupTime + " " + pickupDate;
              String returnDate = helperDateFormat(snapshot.child("returnDate").getValue().toString());
              String returnTime = snapshot.child("returnTime").getValue().toString();
              String fullReturn = returnTime + " " + returnDate;
              String licensePlate = snapshot.child("licensePlate").getValue().toString();
              Long rideTime = Long.parseLong(snapshot.child("pickupUnixTimestamp").getValue().toString());

              myRideModel ride = new myRideModel(departureLocation, destination, fullPickup, fullReturn, licensePlate, snapshot.child("username").getValue().toString());

              String secondsString = String.valueOf(System.currentTimeMillis());
              int now = Integer.valueOf(secondsString.substring(0, secondsString.length() - 3));
              System.out.println("NOW:");
              System.out.println(now);
              System.out.println("Ride time:");
              System.out.println(rideTime);

              if (id == R.id.upcoming && (rideTime > now)) {
                rides.add(ride);
              } else if (id == R.id.past && (rideTime < now)) {
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
