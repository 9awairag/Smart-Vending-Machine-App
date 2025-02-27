package com.example.mvm.User;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.OperatorDAO;
import com.example.mvm.MainActivity;
import com.example.mvm.Manager.ManagerHomeScreen;
import com.example.mvm.Operator.OperatorHomeScreen;
import com.example.mvm.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewCart extends AppCompatActivity {
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
        alertBuilder = new AlertDialog.Builder(ViewCart.this);
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
                    startActivity(new Intent(this,UserHomeScreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);

        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Map sessionMap = sharedpreferences.getAll();
        OperatorDAO optDb = new OperatorDAO(this);
        TextView vehicleId = findViewById(R.id.vehicle_id);
        vehicleId.setText(vehicleId.getText()+(optDb.getDescription("vehicle", (String) sessionMap.get("vehicle"))));
        TextView locationId = findViewById(R.id.location_id);
        locationId.setText(locationId.getText()+((optDb.getDescription("location", (String) sessionMap.get("pickupLocation")))));
        TableLayout ll = findViewById(R.id.table_layout);
        TextView duration = findViewById(R.id.duration);
        duration.setText(duration.getText()+(String)(sessionMap.get("timeSlot")));
        float sandwitchesCost = ((Integer) sessionMap.get("sandwitches")) * Float.parseFloat(optDb.getUnitCost("SANDWITCHES"));
        float snacksCost = ((Integer) sessionMap.get("snacks")) * Float.parseFloat(optDb.getUnitCost("SNACKS"));
        float drinksCost = ((Integer) sessionMap.get("drinks")) * Float.parseFloat(optDb.getUnitCost("DRINKS"));
        DecimalFormat df = new DecimalFormat("0.00");

        List<List<String>> list = new ArrayList<>();
        List<String> child = new ArrayList<>();

        int sno = 1;
        if (drinksCost > 0) {
            child.add(String.valueOf(sno));
            child.add("Drinks");
            child.add((String.valueOf(sessionMap.get("drinks"))));
            child.add("$"+df.format(drinksCost));
            list.add(child);
            sno++;
        }
        if (snacksCost > 0) {
            List<String> child1 = new ArrayList<>();
            child1.add(String.valueOf(sno));
            child1.add("Snacks");
            child1.add((String.valueOf(sessionMap.get("snacks"))));
            child1.add("$"+df.format(snacksCost));
            list.add(child1);
        }

        if (sandwitchesCost > 0) {
            List<String> child2 = new ArrayList<>();
            child2.add(String.valueOf(sno));
            child2.add("Sandwiches");
            child2.add((String.valueOf(sessionMap.get("sandwitches"))));
            child2.add("$"+df.format(sandwitchesCost));
            list.add(child2);
        }

        for (int i = 2; i <= list.size() + 1; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            List<String> columns = list.get(i - 2);
            int in = 0;
            for (String column : columns) {
                TextView textView = new TextView(this);
                textView.setText(column);
                textView.setWidth(310);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(16);
                if (in == 3 || in == 2) {
                    textView.setWidth(340);
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                }
                else
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setGravity(Gravity.CENTER);
                row.addView(textView);
                in++;
            }
            ll.addView(row, i);
        }
        float taxAmount = (drinksCost + snacksCost + sandwitchesCost) * 8.25f/100;
        TextView tax = findViewById(R.id.tax_amnt);
        tax.setText(tax.getText()+df.format(taxAmount));
        TextView grandTotal = findViewById(R.id.grandTotal);
        grandTotal.setText(grandTotal.getText()+(String.valueOf(df.format(sessionMap.get("grandTotal")))));
    }
    public void checkout(View view) {
        startActivity(new Intent(this,Checkout.class));
    }
}
