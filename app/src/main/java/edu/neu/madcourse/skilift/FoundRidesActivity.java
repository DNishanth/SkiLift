package edu.neu.madcourse.skilift;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

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
        adapter = new RideInfoRVAdapter(rideInfoList, rideInfoClickListener);
        RecyclerView recyclerView = findViewById(R.id.ride_info_rv);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void populateGroupMessages() {

    }

}