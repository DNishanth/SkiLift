package edu.neu.madcourse.skilift.viewholders;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.skilift.interfaces.IRideInfoClickListener;

public class RideInfoViewHolder extends RecyclerView.ViewHolder {

    public RideInfoViewHolder(View itemView, IRideInfoClickListener rideInfoClickListener) {
        super(itemView);
        itemView.setOnClickListener(view -> {
            int itemPosition = getLayoutPosition();
            rideInfoClickListener.onItemClick(itemPosition);
        });
    }
}