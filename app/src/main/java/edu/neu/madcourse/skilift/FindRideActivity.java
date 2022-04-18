package edu.neu.madcourse.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class FindRideActivity extends AppCompatActivity implements OnMapReadyCallback {

  FusedLocationProviderClient fusedLocationProviderClient;

  LocationRequest locationRequest;

  TextView locationText;

  GoogleMap map;

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ride);


        locationText = findViewById(R.id.locationText);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);

        mapFragment.getMapAsync(this);

        updateGPS();
    }


    public void onMapReady(@NonNull GoogleMap googleMap){
        map = googleMap;
        locationRequest = LocationRequest.create();

        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }
    private void updateGPS(){
      System.out.println("Firing updateGPS");
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
      System.out.println("Firing onRequest Permissions Result");
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);

      if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
        updateGPS();
      }else{
        Toast.makeText(this, "App needs permission to work correctly", Toast.LENGTH_SHORT).show();
        finish();
      }



    }

    private void updateUIValues(Location location){
      System.out.println("Firing Update UI values");

      locationText.setText(String.valueOf("Pickup Location\n" + location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
      LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
      map.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
      map.moveCamera(CameraUpdateFactory.zoomTo(15.0F));
      map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

    }

}
