package com.example.mvm.Operator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.OperatorDAO;
import com.example.mvm.MainActivity;
import com.example.mvm.Manager.ManagerHomeScreen;
import com.example.mvm.R;
import com.example.mvm.User.UserHomeScreen;

import java.util.Map;

public class ViewSchedule extends AppCompatActivity {
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
        alertBuilder = new AlertDialog.Builder(ViewSchedule.this);
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
                    startActivity(new Intent(this,OperatorHomeScreen.class));
                else
                    startActivity(new Intent(this, UserHomeScreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Map sessionMap = sharedpreferences.getAll();
        OperatorDAO operatorDAO = new OperatorDAO(this);
        String username = sessionMap.get("username").toString();
        Cursor operatorDetails = operatorDAO.getSchedule(username);

        TextView vehicleId = findViewById(R.id.VehicleID);
        TableLayout table = findViewById(R.id.table_layout);
        TextView vehicleType = findViewById(R.id.txtAssigned);

        if (operatorDetails.getCount() <= 0) {
            table.setVisibility(View.GONE);
            vehicleId.setVisibility(View.GONE);
            vehicleType.setText("No vehicle assigned");
            return;
        }

        int i = 0;
        while (operatorDetails.moveToNext()) {
            if (i == 0) {
                vehicleType.setText(vehicleType.getText()+operatorDAO.getVehicleType(operatorDetails.getString(operatorDetails.getColumnIndex("vehicleId"))));
                vehicleId.setText(vehicleId.getText() + operatorDAO.getDescription("vehicle", operatorDetails.getString(operatorDetails.getColumnIndex("vehicleId"))));
            }
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            TextView textView = new TextView(this);
            textView.setText("Location Intersection: "+operatorDAO.getDescription("location", operatorDetails.getString(operatorDetails.getColumnIndex("locationId"))));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(18);
            row.addView(textView);

            table.addView(row, i);
            i++;

            row = new TableRow(this);
            lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            textView = new TextView(this);
            textView.setText("Time Slot : "+operatorDetails.getString(operatorDetails.getColumnIndex("startTime")) +":00 - "+operatorDetails.getString(operatorDetails.getColumnIndex("endTime"))+":00");
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(18);
            row.addView(textView);
            table.addView(row, i);
            i++;

            row = new TableRow(this);
            lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            textView = new TextView(this);
            row.addView(textView);
            table.addView(row, i);
            i++;
        }
     }
}
