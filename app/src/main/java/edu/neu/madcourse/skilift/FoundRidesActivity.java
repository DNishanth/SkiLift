package edu.neu.madcourse.skilift;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.neu.madcourse.skilift.adapters.GroupMessageRVAdapter;
import edu.neu.madcourse.skilift.adapters.RideInfoRVAdapter;
import edu.neu.madcourse.skilift.interfaces.IGroupMessageClickListener;
import edu.neu.madcourse.skilift.interfaces.IRideInfoClickListener;
import edu.neu.madcourse.skilift.models.RideInfo;
import edu.neu.madcourse.skilift.viewholders.RideInfoViewHolder;

public class FoundRidesActivity extends AppCompatActivity {
    private static final String TAG = FoundRidesActivity.class.getSimpleName();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final ArrayList<RideInfo> rideInfoList = new ArrayList<>();
    private RecyclerView.Adapter<RideInfoViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_rides);

        // Create recycler view
        createRecyclerView();

        // Populate recycler view with each group the user is a part of
        populateGroupMessages();
    }

    private void createRecyclerView() {
        String username = getIntent().getExtras().getString("username");
        // Open group message on click
        IRideInfoClickListener rideInfoClickListener = itemPosition -> {
            String rideID = rideInfoList.get(itemPosition).getRideID();
            Intent rideInfoIntent = new Intent(this,
                    RideInfoActivity.class);
            rideInfoIntent.putExtra("username", username);
            rideInfoIntent.putExtra("rideID", rideID);
            startActivity(rideInfoIntent);
        };
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new RideInfoRVAdapter(rideInfoList, rideInfoClickListener, this);
        RecyclerView recyclerView = findViewById(R.id.ride_info_rv);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    // TODO: Currently displays all rides, we need to filter based on destination/location
    private void populateGroupMessages() {
        DatabaseReference ridesRef = db.getReference("rides");
        ValueEventListener getRides = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot rideID : snapshot.getChildren()) {
                        rideInfoList.add(rideID.getValue(RideInfo.class));
                    }
                    adapter.notifyItemInserted(rideInfoList.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        };
        ridesRef.addListenerForSingleValueEvent(getRides);
    }
}