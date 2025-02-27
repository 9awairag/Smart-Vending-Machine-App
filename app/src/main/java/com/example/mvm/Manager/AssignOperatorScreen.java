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

public class AssignOperatorScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button button;
    private Spinner operatorName;
    private Spinner vehicleName;
    private String selectedOperator, selectedVehicleId, operatorAssignedDate;
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
    AlertDialog.Builder alertBuilder;
    public void onLogoutClick(final Context context) {
        alertBuilder = new AlertDialog.Builder(AssignOperatorScreen.this);
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
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,ViewAvailableVehiclesActivity.class));
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_operator_screen);

        Spinner spinner = findViewById(R.id.operator_spinner);
        OperatorDAO optDb = new OperatorDAO(this);
        UserDAO userDb = new UserDAO(this);


        Cursor cursorForOperators = optDb.getOperators();

        final List<String> listOfOperators = new ArrayList<>();
        List<String> listOfOperatorNames = new ArrayList<>();

        Spinner spinner1 = findViewById(R.id.vehicle_spinner);

        Cursor cursorForVehicles = optDb.getVehicles();

        final List<String> listOfVehicles = new ArrayList<>();
        List<String> listOfVehicleNames = new ArrayList<>();

        while (cursorForOperators.moveToNext()) {
            listOfOperators.add(cursorForOperators.getString(cursorForOperators.getColumnIndex("username")));
            listOfOperatorNames.add(userDb.getUserFullName(cursorForOperators.getString(cursorForOperators.getColumnIndex("username"))));
        }
        if (listOfOperators.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AssignOperatorScreen.this,
                    android.R.layout.simple_spinner_item, listOfOperatorNames);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectedOperator = listOfOperators.get(0);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parentView,
                                           View selectedItemView, int position, long id) {
                    selectedOperator = listOfOperators.get(position);
                }

                public void onNothingSelected(AdapterView<?> arg0) {// do nothing
                }

            });
            spinner.setAdapter(adapter);
        }
        while (cursorForVehicles.moveToNext()) {
            listOfVehicles.add(cursorForVehicles.getString(cursorForVehicles.getColumnIndex("vehicleId")));
            listOfVehicleNames.add(cursorForVehicles.getString(cursorForVehicles.getColumnIndex("description")));
        }
        if (listOfVehicles.size() > 0) {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(AssignOperatorScreen.this,
                    android.R.layout.simple_spinner_item, listOfVehicleNames);

            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectedVehicleId = listOfVehicles.get(0);

            spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parentView,
                                           View selectedItemView, int position, long id) {
                    selectedVehicleId = listOfVehicles.get(position);
                }

                public void onNothingSelected(AdapterView<?> arg0) {// do nothing
                }

            });
            spinner1.setAdapter(adapter1);
        }
        TextView dateText = findViewById(R.id.tomrw_date);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        operatorAssignedDate = simpleDateFormat.format(tomorrow);
        dateText.setText(simpleDateFormat.format(tomorrow));

        button = (Button) findViewById(R.id.assign_oprtr);
        operatorName = findViewById(R.id.operator_spinner);
        vehicleName = findViewById(R.id.vehicle_spinner);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                final Dialog dialog = new Dialog(AssignOperatorScreen.this);
                dialog.setContentView(R.layout.activity_dialog);
                TextView textViewUser = (TextView) dialog.findViewById(R.id.textBrand);
                dialog.setTitle("Confirmation");
                if (listOfOperators.size() > 0)
                    textViewUser.setText("Vehicle: " + vehicleName.getSelectedItem().toString() + "\n" + "Assigned Operator: " + operatorName.getSelectedItem().toString());
                else
                    textViewUser.setText("No operators available");
                Button okButton = dialog.findViewById(R.id.ok);
                Button cancelButton = dialog.findViewById(R.id.assignLocation);
                cancelButton.setText("Cancel");
                textViewUser.setTextSize(17);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        if (listOfOperators.size() > 0) {
                            OperatorDAO optdb = new OperatorDAO(AssignOperatorScreen.this);
                            if (optdb.canAssignOperator(selectedOperator, selectedVehicleId, operatorAssignedDate))
                                optdb.assignOperator(selectedOperator, selectedVehicleId, operatorAssignedDate);
                            else {
                                Toast.makeText(getApplicationContext(), "Vehicle has already been assigned !!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                        startActivity(getIntent());
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }});
                dialog.show();
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
