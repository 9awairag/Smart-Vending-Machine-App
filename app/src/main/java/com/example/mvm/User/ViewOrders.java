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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.OperatorDAO;
import com.example.mvm.DB.OrderDAO;
import com.example.mvm.MainActivity;
import com.example.mvm.Manager.ManagerHomeScreen;
import com.example.mvm.Operator.OperatorHomeScreen;
import com.example.mvm.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ViewOrders extends AppCompatActivity {
    SharedPreferences sharedpreferences;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.parent_menu, menu);
        return true;
    }
    AlertDialog.Builder alertBuilder;
    public void onLogoutClick(final Context context) {
        alertBuilder = new AlertDialog.Builder(ViewOrders.this);
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
    public void onBackPressed() {
        startActivity(new Intent(this,UserHomeScreen.class));
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
        setContentView(R.layout.activity_view_orders);
        TableLayout ll = findViewById(R.id.table_layout);
        OrderDAO orderDAO = new OrderDAO(this);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Map sessionMap = sharedpreferences.getAll();
        Cursor cursor = orderDAO.getOrders((String) sessionMap.get("username"));
        while (cursor.moveToNext()) {
            try {
                if (cursor.getString(cursor.getColumnIndex("orderStatus")).equals("Pending") && simpleDateFormat.parse(simpleDateFormat.format(new Date())).after(simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex("orderDate")))))
                    orderDAO.changeOrderStatus(cursor.getString(cursor.getColumnIndex("orderId")), "Not Completed");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Cursor orders = orderDAO.getOrders((String) sessionMap.get("username"));
        OperatorDAO optDb = new OperatorDAO(this);
        int i = 2;


        while (orders.moveToNext()) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);

            TextView textView = new TextView(this);
            textView.setText(orders.getString(orders.getColumnIndex("orderId")));
            textView.setWidth(120);
            textView.setGravity(Gravity.CENTER);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            row.addView(textView);

            textView = new TextView(this);
            textView.setText(optDb.getDescription("location", orders.getString(orders.getColumnIndex("locationId"))));
            textView.setWidth(160);
            textView.setGravity(Gravity.CENTER);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            row.addView(textView);

            textView = new TextView(this);
            textView.setText(orders.getString(orders.getColumnIndex("orderDate")) + "  " + orders.getString(orders.getColumnIndex("pickupTime")));
            textView.setWidth(60);
            textView.setGravity(Gravity.CENTER);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            row.addView(textView);

            textView = new TextView(this);
            textView.setText(orders.getString(orders.getColumnIndex("orderStatus")));
            textView.setWidth(60);
            textView.setGravity(Gravity.CENTER);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            row.addView(textView);

            final String selectedOrderId = orders.getString(orders.getColumnIndex("orderId"));
            ll.addView(row, i);
            i++;
            row.setClickable(true);

            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), OrderDetails.class);
                    intent.putExtra("orderId", selectedOrderId);
                    startActivityForResult(intent, 0);
                }
            });
        }
    }
}
