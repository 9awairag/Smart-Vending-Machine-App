package com.example.mvm.User;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.OperatorDAO;
import com.example.mvm.DB.OrderDAO;
import com.example.mvm.MainActivity;
import com.example.mvm.Manager.ManagerHomeScreen;
import com.example.mvm.Operator.OperatorHomeScreen;
import com.example.mvm.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ViewVehicleInventoryUser extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    private Button button;
    private String drinksTotalQuantity, snacksTotalQuantity, sandwitchesTotalQuantity, operator, duration, location;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.parent_menu, menu);
        return true;
    }
    AlertDialog.Builder alertBuilder;
    public void onLogoutClick(final Context context) {
        alertBuilder = new AlertDialog.Builder(ViewVehicleInventoryUser.this);
        alertBuilder.setTitle("Confirm Logout");
        alertBuilder.setMessage("Are you sure you want to logout ?");
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor session = sharedpreferences.edit();
                session.clear();
                session.commit();
                startActivity(new Intent(context,MainActivity.class));
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
            case R.id.cart:
                if (sessionMap.get("cart") != null) {
                    startActivity(new Intent(this, ViewCart.class));
                    return true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
                    return false;
                }
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
        setContentView(R.layout.activity_view_vehicle_inventory_user);
        final Intent receiverIntent = getIntent();
        OperatorDAO optDb = new OperatorDAO(this);
        OrderDAO orderDb = new OrderDAO(this);
        location = receiverIntent.getStringExtra("selectedLocationId");
        String vehicle = receiverIntent.getStringExtra("selectedVehicleId");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 0);
        Date today = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(today);
        Cursor vehicleInventory = optDb.getVehicleInventory(vehicle);

        EditText sandwitchesQuantity = findViewById(R.id.addSandwitchQuantity);
        EditText snacksQuantity = findViewById(R.id.addSnacksQuantity);
        EditText drinksQuantity = findViewById(R.id.addDrinkQuantity);

        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        Map sessionMap = sharedpreferences.getAll();
        if (sessionMap.get("cart") != null) {
            if (sessionMap.get("drinks") != null && Integer.parseInt(sessionMap.get("drinks").toString()) > 0)
                drinksQuantity.setText(sessionMap.get("drinks").toString());
            if (sessionMap.get("snacks") != null && Integer.parseInt(sessionMap.get("snacks").toString()) > 0)
                snacksQuantity.setText(sessionMap.get("snacks").toString());
            if (sessionMap.get("sandwitches") != null && Integer.parseInt(sessionMap.get("sandwitches").toString()) > 0)
                sandwitchesQuantity.setText(sessionMap.get("sandwitches").toString());
        }
        while (vehicleInventory.moveToNext()) {
            String itemId = vehicleInventory.getString(vehicleInventory.getColumnIndex("itemId"));
            if (itemId.equals("DRINKS")) {
                drinksTotalQuantity = vehicleInventory.getString(vehicleInventory.getColumnIndex("quantity"));
                ((TextView) findViewById(R.id.drinksQuantity)).setText(drinksTotalQuantity);
            } else if (itemId.equals("SNACKS")) {
                snacksTotalQuantity = vehicleInventory.getString(vehicleInventory.getColumnIndex("quantity"));
                ((TextView) findViewById(R.id.snacksQuantity)).setText(snacksTotalQuantity);
            } else {
                sandwitchesTotalQuantity = vehicleInventory.getString(vehicleInventory.getColumnIndex("quantity"));
                ((TextView) findViewById(R.id.sandwitchQuantity)).setText(sandwitchesTotalQuantity);
            }
        }
        String pickupLocation = optDb.getDescription("location", location);
        ((TextView) findViewById(R.id.location_id)).setText(pickupLocation);
        duration = receiverIntent.getStringExtra("selectedStartTime")+":00 - "+receiverIntent.getStringExtra("selectedEndTime")+":00";
        ((TextView) findViewById(R.id.duration)).setText(duration);

        button = (Button) findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                SharedPreferences.Editor session = sharedpreferences.edit();

                EditText sandwitchesText = findViewById(R.id.addSandwitchQuantity);
                EditText snacksText = findViewById(R.id.addSnacksQuantity);
                EditText drinksText = findViewById(R.id.addDrinkQuantity);

                int sandwitches = sandwitchesText.getText().length() <= 0 ? 0 : Integer.parseInt("" + sandwitchesText.getText());
                int drinks = drinksText.getText().length() <= 0 ? 0 : Integer.parseInt("" + drinksText.getText());
                int snacks = snacksText.getText().length() <= 0 ? 0 : Integer.parseInt("" + snacksText.getText());

                int totalSandwitches = Integer.parseInt(sandwitchesTotalQuantity);
                int totalDrinks = Integer.parseInt(drinksTotalQuantity);
                int totalSnacks = Integer.parseInt(snacksTotalQuantity);

                if (totalSandwitches < sandwitches || totalDrinks < drinks || totalSnacks < snacks) {
                    Toast.makeText(getApplicationContext(), "Invalid quantity of items", Toast.LENGTH_SHORT).show();
                }
                else {
                    OperatorDAO optDb = new OperatorDAO(ViewVehicleInventoryUser.this);
                    session.putInt("sandwitches", sandwitches);
                    session.putInt("drinks", drinks);
                    session.putInt("snacks", snacks);
                    session.putString("pickupLocation", location);
                    session.putString("timeSlot", duration);
                    session.putString("vehicle", receiverIntent.getStringExtra("selectedVehicleId"));
                    float costOfSandwitch = optDb.getUnitCost("SANDWITCHES").length() == 0 ? 0 : Float.parseFloat(optDb.getUnitCost("SANDWITCHES"));
                    float costOfDrink = optDb.getUnitCost("DRINKS").length() == 0 ? 0 : Float.parseFloat(optDb.getUnitCost("DRINKS"));
                    float costOfSnack = optDb.getUnitCost("SNACKS").length() == 0 ? 0 : Float.parseFloat(optDb.getUnitCost("SNACKS"));
                    session.putFloat("subtotal", ((costOfDrink * drinks) + (costOfSnack * snacks) + (costOfSandwitch * sandwitches)));
                    session.putFloat("grandTotal", ((costOfDrink * drinks) + (costOfSnack * snacks) + (costOfSandwitch * sandwitches))*8.25f/100  +((costOfDrink * drinks) + (costOfSnack * snacks) + (costOfSandwitch * sandwitches)));
                    session.putBoolean("cart", true);
                    session.commit();
                    if (snacks == 0 && sandwitches == 0 && drinks == 0) {
                        session.remove("cart");
                        session.commit();
                        Toast.makeText(getApplicationContext(), "No items added to the cart", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(ViewVehicleInventoryUser.this, ViewVehicleInventoryUser.class);
                        intent.putExtra("selectedVehicleId", receiverIntent.getStringExtra("selectedVehicleId"));
                        intent.putExtra("selectedLocationId", location);
                        intent.putExtra("selectedStartTime", receiverIntent.getStringExtra("selectedStartTime"));
                        intent.putExtra("selectedEndTime", receiverIntent.getStringExtra("selectedEndTime"));
                        Toast.makeText(getApplicationContext(), "Items added to the cart", Toast.LENGTH_SHORT).show();
                        startActivityForResult(intent, 0);
                    }
                }
            }
        });
        vehicleInventory.close();
    }
}
