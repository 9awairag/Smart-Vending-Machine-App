package com.example.mvm.Manager;

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
import com.example.mvm.MainActivity;
import com.example.mvm.DB.UserDAO;
import com.example.mvm.Operator.OperatorHomeScreen;
import com.example.mvm.R;
import com.example.mvm.User.UserHomeScreen;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ViewAvailableVehiclesActivity extends AppCompatActivity {
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
        alertBuilder = new AlertDialog.Builder(ViewAvailableVehiclesActivity.this);
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
        startActivity(new Intent(this,ManagerHomeScreen.class));
        finish();
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
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_available_vehicles);
        OperatorDAO optDb = new OperatorDAO(this);
        UserDAO userDb = new UserDAO(this);
        TableLayout ll = findViewById(R.id.table_layout);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Cursor cursor = optDb.getAllVehiclesWithAssignedOperatorAndLocation(null, null, simpleDateFormat.format(tomorrow), "vehicleId");

        int i = 2;

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView textView = new TextView(this);
                textView.setText(optDb.getDescription("vehicle", cursor.getString(cursor.getColumnIndex("vehicleId"))));
                textView.setWidth(70);
                textView.setGravity(Gravity.CENTER);
                row.addView(textView);

                textView = new TextView(this);
                String username = cursor.getString(cursor.getColumnIndex("username"));
                if (username != null && username.length() > 0) {
                    textView.setText(userDb.getUserFullName(username));
                } else
                    textView.setText("-");
                textView.setWidth(90);
                textView.setGravity(Gravity.CENTER);
                row.addView(textView);

                textView = new TextView(this);
                String locationId = cursor.getString(cursor.getColumnIndex("locationId"));
                if (locationId != null && locationId.length() > 0) {
                    textView.setText(optDb.getDescription("location", cursor.getString(cursor.getColumnIndex("locationId"))));
                } else
                    textView.setText("-");
                textView.setWidth(140);
                textView.setGravity(Gravity.CENTER);
                row.addView(textView);

                textView = new TextView(this);
                if (cursor.getString(cursor.getColumnIndex("startTime")) != null)
                    textView.setText(cursor.getString(cursor.getColumnIndex("startTime")) + ":00  -  " + cursor.getString(cursor.getColumnIndex("endTime")) + ":00");
                else
                    textView.setText("-");
                textView.setWidth(95);
                textView.setGravity(Gravity.CENTER);
                row.addView(textView);

                ll.addView(row, i);
                i++;
            }
        } else {
            Cursor cursorForVehicles = optDb.getVehicles();
            while (cursorForVehicles.moveToNext()) {
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView textView = new TextView(this);
                textView.setText(cursorForVehicles.getString(cursorForVehicles.getColumnIndex("description")));
                textView.setWidth(70);
                textView.setGravity(Gravity.CENTER);
                row.addView(textView);

                textView = new TextView(this);
                textView.setText("-");
                textView.setWidth(90);
                textView.setGravity(Gravity.CENTER);
                row.addView(textView);

                textView = new TextView(this);
                textView.setWidth(140);
                textView.setGravity(Gravity.CENTER);
                row.addView(textView);

                textView = new TextView(this);
                textView.setText("-");
                textView.setWidth(95);
                textView.setGravity(Gravity.CENTER);
                row.addView(textView);

                ll.addView(row, i);
                i++;
            }
        }
    }
    public void assignOperatorScreen(View view) {
        startActivity(new Intent(this,AssignOperatorScreen.class));
    }
    public void assignLocationScreen(View view) {
        startActivity(new Intent(this,AssignLocationScreen.class));
    }
}
