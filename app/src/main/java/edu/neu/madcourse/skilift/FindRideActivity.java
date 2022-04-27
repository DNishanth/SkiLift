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
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import edu.neu.madcourse.skilift.models.Message;
import edu.neu.madcourse.skilift.models.Resorts;

public class FindRideActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    FusedLocationProviderClient fusedLocationProviderClient;

    LocationRequest locationRequest;

    TextView locationText;

    GoogleMap map;

    EditText pickupDateEditText;
    EditText pickupTimeEditText;
    EditText returnDateEditText;
    EditText returnTimeEditText;
    AutoCompleteTextView destinationAutoCompleteTextView;

    private int pickupDateMonth;
    private int pickupDateDay;
    private int pickupDateYear;

    private int pickupTimeHour;
    private int pickupTimeMinute;

    private int returnDateMonth;
    private int returnDateDay;
    private int returnDateYear;

    private int returnTimeHour;
    private int returnTimeMinute;

    @Override
    protected void onStart() {
        super.onStart();
        if (isLocationDataOn(this)) {
            updateGPS();
        } else {
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
        findViewsFields();

        pickupDateEditText.setOnClickListener(this);
        pickupTimeEditText.setOnClickListener(this);
        returnDateEditText.setOnClickListener(this);
        returnTimeEditText.setOnClickListener(this);

        locationText = findViewById(R.id.locationText);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);

        mapFragment.getMapAsync(this);

        Button back = findViewById(R.id.backButton);
        back.setOnClickListener(view -> openActivity(HomeActivity.class));

        Button searchRidesButton = findViewById(R.id.searchRidesButton);
        searchRidesButton.setOnClickListener(view -> {
            if (!checkFields()) {
                openActivity(FoundRidesActivity.class);
            }
        });

        // Create adapter to autocomplete destination field
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                Resorts.resortArray);
        AutoCompleteTextView textView = findViewById(R.id.findARideDestinationField);
        textView.setAdapter(adapter);
    }

    public void openActivity(Class activity) {
        String username = getIntent().getExtras().getString("username");
        Intent intent = new Intent(this, activity);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    ;

    private void findViewsFields() {

        pickupDateEditText = findViewById(R.id.editTextTextPersonName2);
        pickupTimeEditText = findViewById(R.id.editTextTextPersonName3);
        returnDateEditText = findViewById(R.id.editTextTextPersonName4);
        returnTimeEditText = findViewById(R.id.editTextTextPersonName5);
        destinationAutoCompleteTextView = findViewById(R.id.findARideDestinationField);

    }


    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        locationRequest = LocationRequest.create();

        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void updateGPS() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(FindRideActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updateGPS();
        } else {
            Toast.makeText(this, "App needs permission to work correctly", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void updateUIValues(Location location) {


        locationText.setText(String.valueOf("Pickup Location\n" + location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        map.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        map.moveCamera(CameraUpdateFactory.zoomTo(15.0F));
        map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        Geocoder geo = new Geocoder(FindRideActivity.this);

        try {
            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationText.setText("Pickup Location\n" + String.valueOf(addresses.get(0).getAddressLine(0)));
        } catch (Exception e) {
            locationText.setText(String.valueOf("Pickup Location\n" + location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
        }

    }

    public boolean isLocationDataOn(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager != null && LocationManagerCompat.isLocationEnabled(manager);
    }

    private boolean checkFields() {

        boolean anyFieldEmpty = false;

        findViewsFields();

        if (TextUtils.isEmpty(pickupDateEditText.getText().toString())) {
            pickupDateEditText.setError("Please enter a departure date!");
            anyFieldEmpty = true;
        }
        if (TextUtils.isEmpty(pickupTimeEditText.getText().toString())) {
            pickupTimeEditText.setError("Please enter a pickup time!");
            anyFieldEmpty = true;
        }
        if (TextUtils.isEmpty(returnDateEditText.getText().toString())) {
            returnDateEditText.setError("Please enter a return date!");
            anyFieldEmpty = true;
        }
        if (TextUtils.isEmpty(returnTimeEditText.getText().toString())) {
            returnTimeEditText.setError("Please enter a return time!");
            anyFieldEmpty = true;
        }
        if (TextUtils.isEmpty(destinationAutoCompleteTextView.getText().toString())) {
            destinationAutoCompleteTextView.setError("Please enter a destination!");
            anyFieldEmpty = true;
        }

        return anyFieldEmpty;

    }

    @Override
    public void onClick(View view) {
        if (view == pickupDateEditText) {
            final Calendar leavingOnDateCalendar = Calendar.getInstance();
            pickupDateMonth = leavingOnDateCalendar.get(Calendar.MONTH);
            pickupDateDay = leavingOnDateCalendar.get(Calendar.DAY_OF_MONTH);
            pickupDateYear = leavingOnDateCalendar.get(Calendar.YEAR);

            DatePickerDialog leavingOnDateDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    pickupDateEditText.setText((month + 1) + "/" + day + "/" + year);
                }
            }, pickupDateYear, pickupDateMonth, pickupDateDay);
            leavingOnDateDatePickerDialog.setTitle("Enter Departure Date");
            leavingOnDateDatePickerDialog.show();
        } else if (view == pickupTimeEditText) {
            final Calendar pickupTimeCalendar = Calendar.getInstance();
            pickupTimeHour = pickupTimeCalendar.get(Calendar.HOUR_OF_DAY);
            pickupTimeMinute = pickupTimeCalendar.get(Calendar.MINUTE);

            TimePickerDialog pickupTimeTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    pickupTimeEditText.setText(hour + ":" + minute);
                }
            }, pickupTimeHour, pickupTimeMinute, true);
            pickupTimeTimePickerDialog.setTitle("Enter Pickup Time");
            pickupTimeTimePickerDialog.show();
        } else if (view == returnDateEditText) {
            final Calendar returnDateCalendar = Calendar.getInstance();
            returnDateMonth = returnDateCalendar.get(Calendar.MONTH);
            returnDateDay = returnDateCalendar.get(Calendar.DAY_OF_MONTH);
            returnDateYear = returnDateCalendar.get(Calendar.YEAR);

            DatePickerDialog returnDateDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    returnDateEditText.setText((month + 1) + "/" + day + "/" + year);
                }
            }, returnDateYear, returnDateMonth, returnDateDay);
            returnDateDatePickerDialog.setTitle("Enter Return Date");
            returnDateDatePickerDialog.show();
        } else if (view == returnTimeEditText) {
            final Calendar returnTimeCalendar = Calendar.getInstance();
            returnTimeHour = returnTimeCalendar.get(Calendar.HOUR_OF_DAY);
            returnTimeMinute = returnTimeCalendar.get(Calendar.MINUTE);

            TimePickerDialog returnTimeTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    returnTimeEditText.setText(hour + ":" + minute);
                }
            }, returnTimeHour, returnTimeMinute, true);
            returnTimeTimePickerDialog.setTitle("Enter Return Time");
            returnTimeTimePickerDialog.show();
        }
    }
}