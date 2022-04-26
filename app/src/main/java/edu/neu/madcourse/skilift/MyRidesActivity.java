package edu.neu.madcourse.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

      username = getIntent().getExtras().getString("username");

      RecyclerView recyclerView = findViewById(R.id.myRidesRecycler);

      populateRides();

      adapter = new myRidesRecyclerViewAdapter(this, rides);

      recyclerView.setAdapter(adapter);

      recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void populateRides(){
      DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("rides");
      reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          rides.clear();

          for(DataSnapshot snapshot : dataSnapshot.getChildren()){

            if(snapshot.child("username").getValue().toString().equals(username)){
              String departureLocation = snapshot.child("departureLocation").getValue().toString();
              String destination = snapshot.child("destination").getValue().toString();
              String pickupDate = snapshot.child("pickupDate").getValue().toString();
              String returnDate = snapshot.child("returnDate").getValue().toString();
              String licensePlate = snapshot.child("licensePlate").getValue().toString();

              myRideModel ride = new myRideModel(departureLocation, destination, pickupDate, returnDate, licensePlate);
              rides.add(ride);
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
