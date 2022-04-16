package edu.neu.madcourse.skilift;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class EditProfileActivity extends AppCompatActivity {

    // TODO: Set image
    // TODO: Be able to change name??
    // TODO: Set favorite destination(s)
    // TODO: Set default location for pickup (general and/or specific)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

//        // Create the resorts dropdown
//        Spinner resortsDropdown = findViewById(R.id.preferredResortsSpinner);
//        // TODO: Create array for this spinner which does not include already included resorts
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.resorts_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        resortsDropdown.setAdapter(adapter);
    }
}