package com.example.mvm.User;

import android.annotation.SuppressLint;
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
import com.example.mvm.DB.OrderDAO;
import com.example.mvm.DB.UserDAO;
import com.example.mvm.MainActivity;
import com.example.mvm.Manager.ManagerHomeScreen;
import com.example.mvm.Operator.OperatorHomeScreen;
import com.example.mvm.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

public class OrderDetails extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    private int drinks = 0, sandwitches = 0, snacks = 0;
    private String vehicleId="";

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.parent_menu, menu);
        return true;
    }
    AlertDialog.Builder alertBuilder;
    public void onLogoutClick(final Context context) {
        alertBuilder = new AlertDialog.Builder(OrderDetails.this);
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
    public void cancelOrder(View view) {
        onCancelOrder(getApplicationContext());
    }
    public void onCancelOrder(final Context context) {
        alertBuilder = new AlertDialog.Builder(OrderDetails.this);
        alertBuilder.setTitle("Confirm Cancel Order");
        alertBuilder.setMessage("Are you sure you want to Cancel the Order ?");
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String orderId = getIntent().getStringExtra("orderId");
                OrderDAO orderDAO = new OrderDAO(OrderDetails.this);
                OperatorDAO operatorDAO = new OperatorDAO(OrderDetails.this);
                orderDAO.changeOrderStatus(orderId, "Cancelled");
                if (drinks > 0)
                    operatorDAO.refillOrderItems(vehicleId, "DRINKS", drinks);
                if (sandwitches > 0)
                    operatorDAO.refillOrderItems(vehicleId, "SANDWITCHES", sandwitches);
                if (snacks > 0)
                    operatorDAO.refillOrderItems(vehicleId, "SNACKS", snacks);
                Toast.makeText(getApplicationContext(), "Order Cancelled Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context,ViewOrders.class));
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
    public void onBackPressed() {
        startActivity(new Intent(this,ViewOrders.class));
        finish();
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
        setContentView(R.layout.activity_order_details);
        TableLayout ll = findViewById(R.id.table_layout);
        OrderDAO orderDAO = new OrderDAO(this);
        UserDAO userDAO = new UserDAO(this);
        OperatorDAO operatorDAO = new OperatorDAO(this);
        String orderId = getIntent().getStringExtra("orderId");
        Cursor orderDetails = orderDAO.getOrderDetails(orderId);
        Cursor orderItems = orderDAO.getOrderItems(orderId);
        DecimalFormat df = new DecimalFormat("0.00");

        orderDetails.moveToNext();

        String username = orderDetails.getString(orderDetails.getColumnIndex("username"));
        Cursor userDetails = userDAO.getUserDetails(username);
        userDetails.moveToNext();

        TextView orderIdView = findViewById(R.id.orderId);
        orderIdView.setText("Order ID : "+orderId);

        TextView firstNameText = findViewById(R.id.firstname);
        firstNameText.setText("First Name : "+userDetails.getString(userDetails.getColumnIndex("firstname")));

        TextView lastnameText = findViewById(R.id.lastname);
        lastnameText.setText("Last Name : "+userDetails.getString(userDetails.getColumnIndex("lastname")));

        TextView contactnoText = findViewById(R.id.contactNo);
        contactnoText.setText("Phone Number : "+userDetails.getString(userDetails.getColumnIndex("phone")));

        TextView pickupTimeText = findViewById(R.id.pickupTime);
        pickupTimeText.setText("Pickup Time : "+orderDetails.getString(orderDetails.getColumnIndex("pickupTime")));

        TextView orderDateText = findViewById(R.id.orderDate);
        orderDateText.setText("Date : "+orderDetails.getString(orderDetails.getColumnIndex("orderDate")));

        TextView vehicleText = findViewById(R.id.vehicleId);
        vehicleText.setText("Vehicle : "+operatorDAO.getDescription("vehicle", orderDetails.getString(orderDetails.getColumnIndex("vehicleId"))));

        TextView locationText = findViewById(R.id.locationId);
        locationText.setText("Location Intersection: "+operatorDAO.getDescription("location", orderDetails.getString(orderDetails.getColumnIndex("locationId"))));

        TextView orderStatusText = findViewById(R.id.orderStatus);
        orderStatusText.setText(" Order Status : "+orderDetails.getString(orderDetails.getColumnIndex("orderStatus")));

        TextView grandTotalText = findViewById(R.id.grandTotal);
        grandTotalText.setText("     Grand Total : $"+df.format(Float.parseFloat(orderDetails.getString(orderDetails.getColumnIndex("grandTotal")))));

        vehicleId = orderDetails.getString(orderDetails.getColumnIndex("vehicleId"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Button cancelOrder = findViewById(R.id.btn_CancelOrder);

        if (orderDetails.getString(orderDetails.getColumnIndex("orderStatus")).equals("Not Completed") || orderDetails.getString(orderDetails.getColumnIndex("orderStatus")).equals("Completed") || orderDetails.getString(orderDetails.getColumnIndex("orderStatus")).equals("Cancelled")) {
            cancelOrder.setEnabled(false);
        }
        int i = 2;
        float subtotal = 0;
        while (orderItems.moveToNext()) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);

            TextView textView = new TextView(this);
            textView.setText(String.valueOf(i-1));
            textView.setWidth(100);
            textView.setGravity(Gravity.CENTER);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            row.addView(textView);

            textView = new TextView(this);
            textView.setText(operatorDAO.getDescription("item", orderItems.getString(orderItems.getColumnIndex("itemId"))));
            textView.setWidth(100);
            textView.setGravity(Gravity.CENTER);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            row.addView(textView);

            textView = new TextView(this);
            textView.setText(orderItems.getString(orderItems.getColumnIndex("quantity")));
            textView.setWidth(100);
            textView.setGravity(Gravity.CENTER);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            row.addView(textView);

            textView = new TextView(this);
            textView.setText("$"+df.format(Float.parseFloat(orderItems.getString(orderItems.getColumnIndex("totalCost")))));
            textView.setWidth(100);
            textView.setGravity(Gravity.CENTER);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            row.addView(textView);

            if (orderItems.getString(orderItems.getColumnIndex("itemId")).equals("DRINKS"))
                drinks = Integer.parseInt(orderItems.getString(orderItems.getColumnIndex("quantity")));
            else if (orderItems.getString(orderItems.getColumnIndex("itemId")).equals("SNACKS"))
                snacks = Integer.parseInt(orderItems.getString(orderItems.getColumnIndex("quantity")));
            else
                sandwitches = Integer.parseInt(orderItems.getString(orderItems.getColumnIndex("quantity")));
            subtotal += Float.parseFloat(orderItems.getString(orderItems.getColumnIndex("totalCost")));

            ll.addView(row, i);
            i++;

        }
        float taxAmount = Float.parseFloat(orderDetails.getString(orderDetails.getColumnIndex("grandTotal")))-subtotal;
        TextView orderTax = findViewById(R.id.tax_view);
        orderTax.setText(orderTax.getText()+df.format(taxAmount));
        orderDetails.close();
        orderItems.close();
    }
}
