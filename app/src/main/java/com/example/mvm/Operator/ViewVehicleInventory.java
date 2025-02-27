package com.example.mvm.Operator;

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
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.OperatorDAO;
import com.example.mvm.DB.OrderDAO;
import com.example.mvm.DB.UserDAO;
import com.example.mvm.MainActivity;
import com.example.mvm.Manager.ManagerHomeScreen;
import com.example.mvm.R;
import com.example.mvm.User.UserHomeScreen;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ViewVehicleInventory extends AppCompatActivity {
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
        alertBuilder = new AlertDialog.Builder(ViewVehicleInventory.this);
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
            case R.id.home:
                if (sessionMap == null)
                    return false;
                if ((sessionMap.get("userType")).equals("Manager"))
                    startActivity(new Intent(this, ManagerHomeScreen.class));
                else if ((sessionMap.get("userType")).equals("Operator"))
                    startActivity(new Intent(this,OperatorHomeScreen.class));
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
        setContentView(R.layout.activity_view_vehicle_inventory_manager);
        Intent receiverIntent = getIntent();
        OperatorDAO optDb = new OperatorDAO(this);
        UserDAO userDAO = new UserDAO(this);
        OrderDAO orderDb = new OrderDAO(this);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Map sessionMap = sharedpreferences.getAll();

        String location = new String();
        String vehicle = new String();
        if ((sessionMap.get("userType")).equals("Manager"))
            location = receiverIntent.getStringExtra("selectedLocationId");
        if ((sessionMap.get("userType")).equals("Operator"))
            vehicle = optDb.getCurrentVehicle(sessionMap.get("username").toString());
        else
            vehicle = receiverIntent.getStringExtra("selectedVehicleId");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 0);
        Date today = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(today);
        Cursor vehicleInventory = optDb.getVehicleInventory(vehicle);
        if (vehicleInventory.getCount() > 0) {
            while (vehicleInventory.moveToNext()) {
                String itemId = vehicleInventory.getString(vehicleInventory.getColumnIndex("itemId"));
                if (itemId.equals("DRINKS"))
                    ((TextView) findViewById(R.id.drinksQuantity)).setText(vehicleInventory.getString(vehicleInventory.getColumnIndex("quantity")));
                else if (itemId.equals("SNACKS"))
                    ((TextView) findViewById(R.id.snacksQuantity)).setText(vehicleInventory.getString(vehicleInventory.getColumnIndex("quantity")));
                else
                    ((TextView) findViewById(R.id.sandwitchQuantity)).setText(vehicleInventory.getString(vehicleInventory.getColumnIndex("quantity")));
            }
        } else {
            (findViewById(R.id.table_layout)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.titleLabel)).setText(((TextView) findViewById(R.id.titleLabel)).getText()+"\n \n No Vehicle assigned \n to you");
        }
        if (receiverIntent.getStringExtra("selectedOperator") != null)
            ((TextView) findViewById(R.id.operator)).setText(receiverIntent.getStringExtra("selectedOperator"));
        else {
            ((TextView) findViewById(R.id.operator)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.operatorText)).setVisibility(View.GONE);
        }
        if ((sessionMap.get("userType")).equals("Manager"))
                ((TextView) findViewById(R.id.location_id)).setText(optDb.getDescription("location", location));
            else {
                ((TextView) findViewById(R.id.location_id)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.locationLabel)).setVisibility(View.GONE);
            }
            if (receiverIntent.getStringExtra("selectedStartTime") != null)
                ((TextView) findViewById(R.id.duration)).setText(receiverIntent.getStringExtra("selectedStartTime") + ":00 - " + receiverIntent.getStringExtra("selectedEndTime") + ": 00");
            else {
                ((TextView) findViewById(R.id.durationLabel)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.duration)).setVisibility(View.GONE);
            }
            String revenue;
            if ((sessionMap.get("userType")).equals("Manager"))
                revenue = orderDb.getOrderRevenue(vehicle, null, date);
            else
                revenue = orderDb.getOrderRevenue(vehicle, null, date);
            DecimalFormat df = new DecimalFormat("0.00");
            ((TextView) findViewById(R.id.revenue)).setText("$ " + df.format(Float.parseFloat(revenue)));
            vehicleInventory.close();
    }
}
