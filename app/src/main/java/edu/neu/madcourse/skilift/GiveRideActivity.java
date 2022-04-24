package edu.neu.madcourse.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.FirebaseDatabase;

import edu.neu.madcourse.skilift.models.Message;
import edu.neu.madcourse.skilift.models.Resorts;
import edu.neu.madcourse.skilift.models.RideInfo;

public class GiveRideActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    TextView locationText;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_ride);

        // Create adapter to autocomplete destination field
        ArrayAdapter<String> destinationAdapter = new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                Resorts.resortArray);
        AutoCompleteTextView destinationField = findViewById(R.id.giveARideDestinationField);
        destinationField.setAdapter(destinationAdapter);

        // Create adapter for ski rack dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                new String[]{"Yes", "No"});
        Spinner skiRackField = findViewById(R.id.giveARideSkiRackField);
        skiRackField.setAdapter(adapter);
    }

    protected void openActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.giveARideBackButton:
                openActivity(HomeActivity.class);
                break;
            case R.id.giveARideSubmitButton:
                submitRide();
                break;
            default:
                break;
        }
    }


    // TODO: Use some type of date/time picker to get time info
    // TODO: We need to split car license and model if we want to display separately in RideInfo
    // TODO: Add a method to check fields aren't empty, show error messages with EditText's setError
    private void submitRide() {
        AutoCompleteTextView destination = findViewById(R.id.giveARideDestinationField);
        EditText passengers = findViewById(R.id.giveARidePassengersField);
        EditText carModel = findViewById(R.id.giveARideCarLicenseModelField);
//        EditText carLicence = findViewById(R.id.giveARideCarLicenceField);
        Spinner skiRack = findViewById(R.id.giveARideSkiRackField);
        EditText specialRequests = findViewById(R.id.giveARideSpecialRequestsField);

        RideInfo rideInfo = new RideInfo(
                getIntent().getExtras().getString("username"),
                1650780310,
                1650891600,
                destination.getText().toString(),
                Integer.parseInt(passengers.getText().toString()),
                carModel.getText().toString(),
                "123 1234",
                skiRack.getSelectedItem().toString(),
                specialRequests.getText().toString());
        db.getReference("rides").push().setValue(rideInfo);
        openActivity(MyRidesActivity.class);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}