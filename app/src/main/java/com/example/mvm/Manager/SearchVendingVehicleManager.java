package com.example.mvm.Manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.OperatorDAO;
import com.example.mvm.DB.UserDAO;
import com.example.mvm.MainActivity;
import com.example.mvm.Operator.OperatorHomeScreen;
import com.example.mvm.Operator.ViewVehicleInventory;
import com.example.mvm.R;
import com.example.mvm.User.UserHomeScreen;

import java.text.SimpleDateFormat;
import java.util.*;

public class SearchVendingVehicleManager extends AppCompatActivity {
    SharedPreferences sharedpreferences;

    private String selectedVehicleId, selectedLocationId;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.parent_menu, menu);
        MenuItem cartItem = menu.getItem(4);
        cartItem.setIcon(0);
        cartItem.setTitle("");
        cartItem.setEnabled(false);
        return true;
    }
    AlertDialog.Builder alertBuilder;
    public void onLogoutClick(final Context context) {
        alertBuilder = new AlertDialog.Builder(SearchVendingVehicleManager.this);
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
                    startActivity(new Intent(this,ManagerHomeScreen.class));
                else if ((sessionMap.get("userType")).equals("Operator"))
                    startActivity(new Intent(this, OperatorHomeScreen.class));
                else
                    startActivity(new Intent(this, UserHomeScreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_vending_vehicle_manager);

        Intent receiverIntent = getIntent();

        selectedLocationId = receiverIntent.getStringExtra("selectedLocationId");
        selectedVehicleId = receiverIntent.getStringExtra("selectedVehicleId");

        Spinner locationSpinner = (Spinner) findViewById(R.id.spinner);
        Spinner vehicleSpinner = (Spinner) findViewById(R.id.spinner2);
        OperatorDAO optDb = new OperatorDAO(this);
        optDb.fullfilInventory();
        Cursor cursorForVehicles = optDb.getVehicles();
        Cursor cursorForLocations = optDb.getLocations();

        final List<String> listOfVehicles = new ArrayList<>();
        final List<String> listOfVehicleNames = new ArrayList<>();

        final List<String> listOfLocations = new ArrayList<>();
        final List<String> listOfLocationNames = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 0);
        Date today = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        listOfVehicles.add(null);
        listOfVehicleNames.add("None");
        while (cursorForVehicles.moveToNext()) {
            listOfVehicles.add(cursorForVehicles.getString(cursorForVehicles.getColumnIndex("vehicleId")));
            listOfVehicleNames.add(cursorForVehicles.getString(cursorForVehicles.getColumnIndex("description")));
        }
        if (listOfVehicles.size() > 0) {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(SearchVendingVehicleManager.this,
                    android.R.layout.simple_spinner_item, listOfVehicleNames);

            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            vehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parentView,
                                           View selectedItemView, int position, long id) {
                    selectedVehicleId = listOfVehicles.get(position);
                }

                public void onNothingSelected(AdapterView<?> arg0) {// do nothing
                }

            });
            vehicleSpinner.setAdapter(adapter1);
            if (selectedVehicleId != null)
                vehicleSpinner.setSelection(listOfVehicles.indexOf(selectedVehicleId));
        }

        listOfLocations.add(null);
        listOfLocationNames.add("None");
        while (cursorForLocations.moveToNext()) {
            listOfLocations.add(cursorForLocations.getString(cursorForLocations.getColumnIndex("locationId")));
            listOfLocationNames.add(cursorForLocations.getString(cursorForLocations.getColumnIndex("description")));
        }
        if (listOfLocations.size() > 0) {
            ArrayAdapter<String> locationSpinnerAdapter = new ArrayAdapter<String>(SearchVendingVehicleManager.this,
                    android.R.layout.simple_spinner_item, listOfLocationNames);

            locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parentView,
                                           View selectedItemView, int position, long id) {
                        selectedLocationId = listOfLocations.get(position);
                }

                public void onNothingSelected(AdapterView<?> arg0) {// do nothing
                }

            });
            locationSpinner.setAdapter(locationSpinnerAdapter);
            if (selectedLocationId != null)
                locationSpinner.setSelection(listOfLocations.indexOf(selectedLocationId));
        }

        TableLayout ll = findViewById(R.id.table_layout);

        Cursor assignedVehicles = optDb.getAllVehiclesWithAssignedOperatorAndLocation(selectedVehicleId, selectedLocationId, simpleDateFormat.format(today), "locationId");
        UserDAO userDb = new UserDAO(this);

        int i = 2;

        if (assignedVehicles.getCount() > 0 && (selectedVehicleId != null || selectedLocationId != null)) {
            while (assignedVehicles.moveToNext()) {
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView textView = new TextView(this);
                textView.setText(optDb.getDescription("vehicle", assignedVehicles.getString(assignedVehicles.getColumnIndex("vehicleId"))));
                textView.setWidth(70);
                textView.setGravity(Gravity.CENTER);
                row.addView(textView);

                textView = new TextView(this);
                String locationId = assignedVehicles.getString(assignedVehicles.getColumnIndex("locationId"));
                if (locationId != null && locationId.length() > 0) {
                    textView.setText(optDb.getDescription("location", assignedVehicles.getString(assignedVehicles.getColumnIndex("locationId"))));
                } else
                    textView.setText("-");
                textView.setWidth(140);
                textView.setGravity(Gravity.CENTER);
                row.addView(textView);

                textView = new TextView(this);
                if (assignedVehicles.getString(assignedVehicles.getColumnIndex("startTime")) != null)
                    textView.setText(assignedVehicles.getString(assignedVehicles.getColumnIndex("startTime")) + ":00  -  " + assignedVehicles.getString(assignedVehicles.getColumnIndex("endTime")) + ":00");
                else
                    textView.setText("-");
                textView.setWidth(95);
                textView.setGravity(Gravity.CENTER);
                row.addView(textView);

                textView = new TextView(this);
                String username = assignedVehicles.getString(assignedVehicles.getColumnIndex("username"));
                String fullName = userDb.getUserFullName(username);
                if (username != null && username.length() > 0) {
                    textView.setText(fullName);
                } else
                    textView.setText("-");
                textView.setWidth(85);
                textView.setGravity(Gravity.CENTER);
                row.addView(textView);

                ll.addView(row, i);
                i++;
                final String currentEndTime = assignedVehicles.getString(assignedVehicles.getColumnIndex("endTime"));
                final String currentStartTime = assignedVehicles.getString(assignedVehicles.getColumnIndex("startTime"));
                final String currentVehicle = assignedVehicles.getString(assignedVehicles.getColumnIndex("vehicleId"));
                final String currentLocation = assignedVehicles.getString(assignedVehicles.getColumnIndex("locationId"));
                final String currentOperator = fullName;
                row.setClickable(true);

                row.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ViewVehicleInventory.class);
                        intent.putExtra("selectedVehicleId", currentVehicle);
                        intent.putExtra("selectedLocationId", currentLocation);
                        intent.putExtra("selectedStartTime", currentStartTime);
                        intent.putExtra("selectedEndTime", currentEndTime);
                        intent.putExtra("selectedOperator", currentOperator);
                        startActivityForResult(intent,0);
                    }
                });
            }
        }

    }

    public void searchVehicle(View view) {
        Intent intent = new Intent(this,SearchVendingVehicleManager.class);
        intent.putExtra("selectedVehicleId", selectedVehicleId);
        intent.putExtra("selectedLocationId", selectedLocationId);
        startActivity(intent);
    }
}
