package edu.neu.madcourse.skilift;

import android.content.Context;
import android.telecom.TelecomManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class myRidesRecyclerViewAdapter extends RecyclerView.Adapter<myRidesRecyclerViewAdapter.MyViewHolder> {

  Context context;

  ArrayList<myRideModel> rides;

  public myRidesRecyclerViewAdapter(Context context, ArrayList<myRideModel> rides){
      this.context = context;
      this.rides = rides;
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
  }

  @Override
  public int getItemCount() {
    return rides.size();
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder{

    TextView origin, destination, pickup, returnTime, license;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);
      origin = itemView.findViewById(R.id.myRideOrigin);
      destination = itemView.findViewById(R.id.myRideDestination);
      license = itemView.findViewById(R.id.myRideLicense);
      pickup = itemView.findViewById(R.id.myRidePickup);
      returnTime = itemView.findViewById(R.id.myRideReturn);
    }
  }
}
