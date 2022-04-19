package edu.neu.madcourse.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class FindRideActivity extends AppCompatActivity implements OnMapReadyCallback {

  FusedLocationProviderClient fusedLocationProviderClient;

  LocationRequest locationRequest;

  TextView locationText;

  GoogleMap map;

  @Override
  protected void onStart() {
    super.onStart();
    if(isLocationDataOn(this)){
      updateGPS();
    }else{
      View findARideLayout = findViewById(R.id.findARideLayout);
      Snackbar snackbar = Snackbar.make(findARideLayout, "Location services need to be enabled in Settings to use App", Snackbar.LENGTH_INDEFINITE);
      snackbar.getView().setOnClickListener(view -> snackbar.dismiss());
      snackbar.show();
    }
  }


  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ride);


        locationText = findViewById(R.id.locationText);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);

        mapFragment.getMapAsync(this);

        Button back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            openHomeActivity();
          }
        });
    }

  public void openHomeActivity() {
    Intent theIntent = new Intent(this, HomeActivity.class);
    startActivity(theIntent);
  };


    public void onMapReady(@NonNull GoogleMap googleMap){
        map = googleMap;
        locationRequest = LocationRequest.create();

        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }
    private void updateGPS(){

      fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(FindRideActivity.this);
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

      Geocoder geo = new Geocoder(FindRideActivity.this);

      try {
        List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        locationText.setText("Pickup Location\n" + String.valueOf(addresses.get(0).getAddressLine(0)));
      }catch(Exception e){
        locationText.setText(String.valueOf("Pickup Location\n" + location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
      }

    }
  public boolean isLocationDataOn (Context context){
    LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    return manager != null && LocationManagerCompat.isLocationEnabled(manager);
  }


}
