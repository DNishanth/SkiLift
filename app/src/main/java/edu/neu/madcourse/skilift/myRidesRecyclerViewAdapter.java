package edu.neu.madcourse.skilift;

import android.content.Context;
import android.content.Intent;
import android.telecom.TelecomManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class myRidesRecyclerViewAdapter extends RecyclerView.Adapter<myRidesRecyclerViewAdapter.MyViewHolder> {

  Context context;

  ArrayList<myRideModel> rides;

  String username;

  FirebaseDatabase db;

  public myRidesRecyclerViewAdapter(Context context, ArrayList<myRideModel> rides, String username){
      this.context = context;
      this.rides = rides;
      this.username = username;
  }

  @NonNull
  @Override
  public myRidesRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.my_rides_row, parent, false);
    return new myRidesRecyclerViewAdapter.MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull myRidesRecyclerViewAdapter.MyViewHolder holder, int position) {
      holder.destination.setText(this.rides.get(position).getDestination());
      holder.license.setText(this.rides.get(position).getLicense());
      holder.origin.setText(this.rides.get(position).getOrigin());
      holder.pickup.setText(this.rides.get(position).getPickup());
      holder.returnTime.setText(this.rides.get(position).getReturnTime());
      holder.driver.setText(this.rides.get(position).getDriver());
      holder.driver.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent profileIntent = new Intent(context, ProfileActivity.class);
        profileIntent.putExtra("username", username);
        profileIntent.putExtra("profileUsername", holder.driver.getText());
        context.startActivity(profileIntent);
      }
      });
      db = FirebaseDatabase.getInstance();
      int index = position;
      holder.delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            db.getReference("rides").child(rides.get(index).getRideID()).removeValue();
        }
      });

  }

  @Override
  public int getItemCount() {
    return rides.size();
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder{

    TextView origin, destination, pickup, returnTime, license, driver;
    ImageView delete;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);
      origin = itemView.findViewById(R.id.myRideOrigin);
      destination = itemView.findViewById(R.id.myRideDestination);
      license = itemView.findViewById(R.id.myRideLicense);
      pickup = itemView.findViewById(R.id.myRidePickup);
      returnTime = itemView.findViewById(R.id.myRideReturn);
      driver = itemView.findViewById(R.id.myRideDriver);
      delete = itemView.findViewById(R.id.delete);
    }
  }
}
