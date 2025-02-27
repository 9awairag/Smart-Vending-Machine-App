package com.example.mvm.Operator;

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
import androidx.annotation.RequiresApi;
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
import java.util.Map;

public class ViewCurrentOrders extends AppCompatActivity {
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
        startActivity(new Intent(this,OperatorHomeScreen.class));
        finish();
    }
    AlertDialog.Builder alertBuilder;
    public void onLogoutClick(final Context context) {
        alertBuilder = new AlertDialog.Builder(ViewCurrentOrders.this);
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
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_orders);
        TableLayout ll = findViewById(R.id.table_layout);

        OrderDAO orderDAO = new OrderDAO(this);
        OperatorDAO optDb = new OperatorDAO(this);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Map sessionMap = sharedpreferences.getAll();
        String vehicleId = optDb.getCurrentVehicle((String) sessionMap.get("username"));
        DecimalFormat df = new DecimalFormat("0.00");

        int i = 2;

        if (vehicleId.length() > 0) {
            Cursor orders = orderDAO.getCurrentOrderForVehicle(vehicleId);
            while (orders.moveToNext()) {
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                row.setLayoutParams(lp);

                TextView textView = new TextView(this);
                textView.setText(orders.getString(orders.getColumnIndex("orderId")));
                textView.setWidth(110);
                textView.setGravity(Gravity.CENTER);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                row.addView(textView);

                textView = new TextView(this);
                textView.setText(new UserDAO(this).getUserFullName(orders.getString(orders.getColumnIndex("username"))));
                textView.setWidth(140);
                textView.setGravity(Gravity.CENTER);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                row.addView(textView);

                textView = new TextView(this);
                textView.setText(orders.getString(orders.getColumnIndex("orderStatus")));
                textView.setWidth(110);
                textView.setGravity(Gravity.CENTER);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                row.addView(textView);

                textView = new TextView(this);
                textView.setText("$"+ df.format(Float.parseFloat(orders.getString(orders.getColumnIndex("grandTotal")))));
                textView.setWidth(80);
                textView.setGravity(Gravity.CENTER);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                row.addView(textView);

                final String selectedOrderId = orders.getString(orders.getColumnIndex("orderId"));
                ll.addView(row, i);
                i++;
                row.setClickable(true);

                row.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), OrderDetailsForOperator.class);
                        intent.putExtra("orderId", selectedOrderId);
                        startActivityForResult(intent, 0);
                    }
                });
            }
        }
    }
}
