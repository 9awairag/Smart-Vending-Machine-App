package com.example.mvm.Manager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.OperatorDAO;
import com.example.mvm.DB.UserDAO;
import com.example.mvm.MainActivity;
import com.example.mvm.Operator.OperatorHomeScreen;
import com.example.mvm.R;
import com.example.mvm.User.UserHomeScreen;

import java.text.SimpleDateFormat;
import java.util.*;

public class AssignLocationScreen extends AppCompatActivity {

    private Button button;
    private Spinner locationName;
    private Spinner vehicleName;
    private String selectedLocation, selectedVehicleId, operatorAssignedDate, selectedStartTime, selectedEndTime;
    AlertDialog.Builder alertBuilder;
    SharedPreferences sharedpreferences;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.parent_menu, menu);
        MenuItem cartItem = menu.getItem(4);
        cartItem.setIcon(0);
        cartItem.setTitle("");
        cartItem.setEnabled(false);
        return true;
    }
    public void onBackPressed() {
        startActivity(new Intent(this, ViewAvailableVehiclesActivity.class));
        finish();
    }
    public void onLogoutClick(final Context context) {
        alertBuilder = new AlertDialog.Builder(AssignLocationScreen.this);
        alertBuilder.setTitle("Confirm Logout");
        alertBuilder.setMessage("Are you sure you want to logout ?");
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(context, MainActivity.class));
                dialogInterface.dismiss();
            }
        });
        alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Map sessionMap = sharedpreferences.getAll();
        switch (item.getItemId()) {
            case R.id.logout:
                onLogoutClick(getApplicationContext());
                return true;
            case R.id.home:
                if (sessionMap == null)
                    return false;
                if ((sessionMap.get("userType")).equals("Manager"))
                    startActivity(new Intent(this, ManagerHomeScreen.class));
                else if ((sessionMap.get("userType")).equals("Operator"))
                    startActivity(new Intent(this, OperatorHomeScreen.class));
                else
                    startActivity(new Intent(this, UserHomeScreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_location_screen);
        OperatorDAO optDb = new OperatorDAO(this);
        UserDAO userDb = new UserDAO(this);

        final List<String> listOfLocations = new ArrayList<>();
        List<String> locationOfLocationNames = new ArrayList<>();

        final List<String> listOfVehicles = new ArrayList<>();
        List<String> listOfVehicleNames = new ArrayList<>();

        final List<String> listOfStartTime = new ArrayList<>();
        final List<String> listOfEndTime = new ArrayList<>();
        final List<String> listOfTimeSlotsToDisplay = new ArrayList<>();

        Spinner vehicleSpinner = findViewById(R.id.vehicles_spinner1);
        Spinner locationSpinner = findViewById(R.id.location_spinner);
        final Spinner timeSlotSpinner = findViewById(R.id.time_slot_spinner);

        Cursor cursorForLocations = optDb.getLocations();
        Cursor cursorForVehicles = optDb.getVehicles();

        while (cursorForLocations.moveToNext()) {
            listOfLocations.add(cursorForLocations.getString(cursorForLocations.getColumnIndex("locationId")));
            locationOfLocationNames.add(cursorForLocations.getString(cursorForLocations.getColumnIndex("description")));
        }
        if (listOfLocations.size() > 0) {
            ArrayAdapter<String> locationSpinnerAdapter = new ArrayAdapter<String>(AssignLocationScreen.this,
                    android.R.layout.simple_spinner_item, locationOfLocationNames);

            locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectedLocation = listOfLocations.get(0);
            locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parentView,
                                           View selectedItemView, int position, long id) {
                    selectedLocation = listOfLocations.get(position);

                    Cursor cursorForTimeSlots = new OperatorDAO(AssignLocationScreen.this).getTimeSlot(selectedLocation);
                    listOfStartTime.clear();
                    listOfEndTime.clear();
                    listOfTimeSlotsToDisplay.clear();

                    while (cursorForTimeSlots.moveToNext()) {
                        listOfStartTime.add(cursorForTimeSlots.getString(cursorForTimeSlots.getColumnIndex("startTime")));
                        listOfEndTime.add(cursorForTimeSlots.getString(cursorForTimeSlots.getColumnIndex("endTime")));
                        listOfTimeSlotsToDisplay.add(cursorForTimeSlots.getString(cursorForTimeSlots.getColumnIndex("startTime")) + ":00 -" + cursorForTimeSlots.getString(cursorForTimeSlots.getColumnIndex("endTime")) +":00");
                    }
                    if (listOfStartTime.size() > 0 && listOfEndTime.size() > 0) {
                        ArrayAdapter<String> timeSlotSpinnerAdapter = new ArrayAdapter<String>(AssignLocationScreen.this,
                                android.R.layout.simple_spinner_item, listOfTimeSlotsToDisplay);

                        timeSlotSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        selectedStartTime = listOfStartTime.get(0);
                        selectedEndTime = listOfEndTime.get(0);
                        timeSlotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            public void onItemSelected(AdapterView<?> parentView,
                                                       View selectedItemView, int position, long id) {
                                selectedStartTime = listOfStartTime.get(position);
                                selectedEndTime = listOfEndTime.get(position);
                            }

                            public void onNothingSelected(AdapterView<?> arg0) {// do nothing
                            }

                        });
                        timeSlotSpinner.setAdapter(timeSlotSpinnerAdapter);
                    }



                }

                public void onNothingSelected(AdapterView<?> arg0) {// do nothing
                }

            });
            locationSpinner.setAdapter(locationSpinnerAdapter);
        }
        while (cursorForVehicles.moveToNext()) {
            listOfVehicles.add(cursorForVehicles.getString(cursorForVehicles.getColumnIndex("vehicleId")));
            listOfVehicleNames.add(cursorForVehicles.getString(cursorForVehicles.getColumnIndex("description")));
        }
        if (listOfVehicles.size() > 0) {
            ArrayAdapter<String> vehicleSpinnerAdapter = new ArrayAdapter<String>(AssignLocationScreen.this,
                    android.R.layout.simple_spinner_item, listOfVehicleNames);

            vehicleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectedVehicleId = listOfVehicles.get(0);

            vehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parentView,
                                           View selectedItemView, int position, long id) {
                    selectedVehicleId = listOfVehicles.get(position);
                }

                public void onNothingSelected(AdapterView<?> arg0) {// do nothing
                }

            });
            vehicleSpinner.setAdapter(vehicleSpinnerAdapter);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        operatorAssignedDate = simpleDateFormat.format(tomorrow);

        button = (Button) findViewById(R.id.assign_loc_time);
        locationName = findViewById(R.id.location_spinner);
        vehicleName = findViewById(R.id.vehicles_spinner1);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                final Dialog dialog = new Dialog(AssignLocationScreen.this);
                dialog.setContentView(R.layout.activity_dialog_box);
                TextView textViewUser = (TextView) dialog.findViewById(R.id.textBrand);
                Button okButton = dialog.findViewById(R.id.ok);
                Button cancelButton = dialog.findViewById(R.id.assignLocation);
                cancelButton.setText("Cancel");
                textViewUser.setTextSize(17);
                dialog.setTitle("Confirmation");
                textViewUser.setText("Vehicle: " + vehicleName.getSelectedItem().toString() + "\n" + "Location Intersection: " + locationName.getSelectedItem().toString() +"\nTime slot:   " + selectedStartTime+":00 - "+selectedEndTime+":00");

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        OperatorDAO optdb = new OperatorDAO(AssignLocationScreen.this);
                        if (optdb.canAssignLocation(selectedVehicleId, operatorAssignedDate))
                            optdb.assignLocation(selectedLocation, selectedVehicleId, selectedStartTime, selectedEndTime, operatorAssignedDate);
                        else {
                            Toast.makeText(getApplicationContext(), "Operator not assigned !!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        startActivity(getIntent());
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }
}
