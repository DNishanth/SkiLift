package edu.neu.madcourse.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.util.Calendar;
import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.neu.madcourse.skilift.models.Resorts;
import edu.neu.madcourse.skilift.models.RideInfo;

public class GiveRideActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    EditText leavingOnDateEditText;
    EditText pickupTimeEditText;
    EditText returnDateEditText;
    EditText returnTimeEditText;
    AutoCompleteTextView destinationAutoCompleteTextView;
    EditText passengersEditText;
    EditText carLicenseEditText;
    EditText carModelEditText;
    Spinner skiRackSpinner;
    EditText specialRequestsEditText;

    private int leavingOnDateMonth;
    private int leavingOnDateDay;
    private int leavingOnDateYear;

    private int pickupTimeHour;
    private int pickupTimeMinute;

    private int returnDateMonth;
    private int returnDateDay;
    private int returnDateYear;

    private int returnTimeHour;
    private int returnTimeMinute;

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private String username;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    TextView locationText;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_ride);
        findViewsFields();

        leavingOnDateEditText.setOnClickListener(this);
        pickupTimeEditText.setOnClickListener(this);
        returnDateEditText.setOnClickListener(this);
        returnTimeEditText.setOnClickListener(this);

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
        if (view == leavingOnDateEditText) {
            final Calendar leavingOnDateCalendar = Calendar.getInstance();
            leavingOnDateMonth = leavingOnDateCalendar.get(Calendar.MONTH);
            leavingOnDateDay = leavingOnDateCalendar.get(Calendar.DAY_OF_MONTH);
            leavingOnDateYear = leavingOnDateCalendar.get(Calendar.YEAR);

            DatePickerDialog leavingOnDateDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    leavingOnDateEditText.setText((month + 1) + "/" + day + "/" + year);
                }
            }, leavingOnDateYear, leavingOnDateMonth, leavingOnDateDay);
            leavingOnDateDatePickerDialog.setTitle("Enter Departure Date");
            leavingOnDateDatePickerDialog.show();
        }
        else if (view == pickupTimeEditText) {
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
        }
        else if (view == returnDateEditText) {
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
        }
        else if (view == returnTimeEditText) {
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

    private void findViewsFields() {

        leavingOnDateEditText = findViewById(R.id.giveARideLeavingOnField);
        pickupTimeEditText = findViewById(R.id.giveARidePickupTimeField);
        returnDateEditText = findViewById(R.id.giveARideReturnDateField);
        returnTimeEditText = findViewById(R.id.giveARideReturnTimeField);
        destinationAutoCompleteTextView = findViewById(R.id.giveARideDestinationField);
        passengersEditText = findViewById(R.id.giveARidePassengersField);
        carLicenseEditText = findViewById(R.id.giveARideCarLicenseField);
        carModelEditText = findViewById(R.id.giveARideCarModelField);
        skiRackSpinner = findViewById(R.id.giveARideSkiRackField);
        specialRequestsEditText = findViewById(R.id.giveARideSpecialRequestsField);

    }

    private boolean checkFields() {

        boolean anyFieldEmpty = false;

        findViewsFields();

        if (TextUtils.isEmpty(leavingOnDateEditText.getText().toString())) {
            leavingOnDateEditText.setError("Please enter a departure date!");
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
        if (TextUtils.isEmpty(passengersEditText.getText().toString())) {
            passengersEditText.setError("Please enter the maximum number of passengers!");
            anyFieldEmpty = true;
        }
        if (TextUtils.isEmpty(carLicenseEditText.getText().toString())) {
            carLicenseEditText.setError("Please enter your license plate number!");
            anyFieldEmpty = true;
        }
        if (TextUtils.isEmpty(carModelEditText.getText().toString())) {
            carModelEditText.setError("Please enter the model of your car!");
            anyFieldEmpty = true;
        }
//        if (TextUtils.isEmpty(skiRackSpinner.getText().toString())) {
//            skiRackSpinner.setError("Please enter whether or not your car has a ski rack!");
//            anyFieldEmpty = true;
//        }
//        if (TextUtils.isEmpty(specialRequestsEditText.getText().toString())) {
//            specialRequestsEditText.setError("Please enter any special requests!");
//            anyFieldEmpty = true;
//        }

        return anyFieldEmpty;

    }

    private int getDateAsInt(EditText dateField) {
        String dateString = dateField.getText().toString();
        String dateStringParsed = dateString.replaceAll("\\/", "");
        return Integer.parseInt(dateStringParsed);
    }

    private int getTimeAsInt(EditText timeField) {
        String timeString = timeField.getText().toString();
        String timeStringParsed = timeString.replaceAll("\\:", "");
        return Integer.parseInt(timeStringParsed);
    }

    private void submitRide() {

        // Do not submit ride details if any required fields are empty (special requests may be empty)
        if (checkFields()) {
            return;
        }

        findViewsFields();

        String rideID = db.getReference("rides").push().getKey();
        RideInfo rideInfo = new RideInfo(
                rideID,
                username,
                "placeholder location",
                getDateAsInt(leavingOnDateEditText),
                getDateAsInt(returnDateEditText),
                destinationAutoCompleteTextView.getText().toString(),
                Integer.parseInt(passengersEditText.getText().toString()),
                carModelEditText.getText().toString(),
                carLicenseEditText.getText().toString(),
                skiRackSpinner.getSelectedItem().toString(),
                specialRequestsEditText.getText().toString());
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}