package edu.neu.madcourse.skilift.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.skilift.R;
import edu.neu.madcourse.skilift.interfaces.IRideInfoClickListener;
import edu.neu.madcourse.skilift.models.RideInfo;
import edu.neu.madcourse.skilift.viewholders.RideInfoViewHolder;

public class RideInfoRVAdapter extends RecyclerView.Adapter<RideInfoViewHolder> {
    private final ArrayList<RideInfo> rideInfoList;
    private final IRideInfoClickListener rideInfoClickListener;

    public RideInfoRVAdapter(ArrayList<RideInfo> rideInfoList,
                             IRideInfoClickListener rideInfoClickListener) {
        this.rideInfoList = rideInfoList;
        this.rideInfoClickListener = rideInfoClickListener;
    }

    @NonNull
    @Override
    public RideInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_info_card,
                parent, false);
        return new RideInfoViewHolder(v, rideInfoClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RideInfoViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return rideInfoList.size();
    }
}
