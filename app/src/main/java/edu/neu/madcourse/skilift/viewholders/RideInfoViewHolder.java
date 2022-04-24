package edu.neu.madcourse.skilift.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.skilift.R;
import edu.neu.madcourse.skilift.interfaces.IRideInfoClickListener;

public class RideInfoViewHolder extends RecyclerView.ViewHolder {
    public ImageView profilePicture;
    public TextView username;
    public RatingBar userRating;
    public TextView departureLocation;
    public TextView destination;
    public TextView departureDate;
    public TextView returnDate;

    public RideInfoViewHolder(View itemView, IRideInfoClickListener rideInfoClickListener) {
        super(itemView);
        profilePicture = itemView.findViewById(R.id.rideInfoProfilePicture);
        username = itemView.findViewById(R.id.rideInfoUsername);
        userRating = itemView.findViewById(R.id.rideInfoUserRating);
        departureLocation = itemView.findViewById(R.id.rideInfoDepLocation);
        destination = itemView.findViewById(R.id.rideInfoDestination);
        departureDate = itemView.findViewById(R.id.rideInfoDepDate);
        returnDate = itemView.findViewById(R.id.rideInfoReturnDate);
        itemView.setOnClickListener(view -> {
            int itemPosition = getLayoutPosition();
            rideInfoClickListener.onItemClick(itemPosition);
        });
    }
}