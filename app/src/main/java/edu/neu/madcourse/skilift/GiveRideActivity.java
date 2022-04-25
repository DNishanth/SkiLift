package edu.neu.madcourse.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;

import android.Manifest;
import android.content.Context;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.neu.madcourse.skilift.models.Resorts;
import edu.neu.madcourse.skilift.models.RideInfo;

public class GiveRideActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private String username;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    TextView locationText;
    GoogleMap map;

    String leavingOnDateText;
    String pickupTimeText;
    String returnDateText;
    String returnTimeText;
    String destinationText;
    String passengersText;
    String carLicenseText;
    String carModelText;
    String skiRackText;
    String specialRequestsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_give_ride);
        locationText = findViewById(R.id.giveARideLocationText);
        username = getIntent().getExtras().getString("username");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.giveARideMap);
        mapFragment.getMapAsync(this);

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

    @Override
    protected void onStart() {
        super.onStart();
        if (isLocationDataOn(this)) {
            updateGPS();
        }
        else {
            View giveARideLayout = findViewById(R.id.giveARideLayout);
            Snackbar snackbar = Snackbar.make(giveARideLayout, "Location services need to be enabled in Settings to use App", Snackbar.LENGTH_INDEFINITE);
            snackbar.getView().setOnClickListener(view -> snackbar.dismiss());
            snackbar.show();
        }
    }

    protected void openActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("username", username);
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

        String rideID = db.getReference("rides").push().getKey();
        RideInfo rideInfo = new RideInfo(
                rideID,
                username,
                "placeholder location",
                1650780310,
                1650891600,
                destination.getText().toString(),
                Integer.parseInt(passengers.getText().toString()),
                carModel.getText().toString(),
                "123 1234",
                skiRack.getSelectedItem().toString(),
                specialRequests.getText().toString());
        db.getReference("rides/" + rideID).setValue(rideInfo);
        openActivity(MyRidesActivity.class);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        locationRequest = LocationRequest.create();

        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void updateGPS(){

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(GiveRideActivity.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                }
            });
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            updateGPS();
        }else{
            Toast.makeText(this, "App needs permission to work correctly", Toast.LENGTH_SHORT).show();
            finish();
        }



    }

    private void updateUIValues(Location location){


        locationText.setText(String.valueOf("Pickup Location\n" + location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        map.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        map.moveCamera(CameraUpdateFactory.zoomTo(15.0F));
        map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        Geocoder geo = new Geocoder(GiveRideActivity.this);

        try {
            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationText.setText("Pickup Location\n" + String.valueOf(addresses.get(0).getAddressLine(0)));
        } catch(Exception e) {
            locationText.setText(String.valueOf("Pickup Location\n" + location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
        }

    }

    public boolean isLocationDataOn (Context context){
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager != null && LocationManagerCompat.isLocationEnabled(manager);
    }
}