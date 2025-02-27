package com.example.mvm.Manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.UserDAO;
import com.example.mvm.MainActivity;
import com.example.mvm.R;

public class VehicleOperatorDetailsActivity extends AppCompatActivity {
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
        startActivity(new Intent(this,ViewOperators.class));
        finish();
    }
    AlertDialog.Builder alertBuilder;
    public void onLogoutClick(final Context context) {
        alertBuilder = new AlertDialog.Builder(VehicleOperatorDetailsActivity.this);
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
        switch (item.getItemId()) {
            case R.id.logout:
                onLogoutClick(getApplicationContext());
                return true;
            case R.id.home:
                startActivity(new Intent(this,ManagerHomeScreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_operator_details);

        String username = getIntent().getStringExtra("Name");
        UserDAO db = new UserDAO(this);
        Cursor cursor = db.getUserDetails(username);

        while(cursor.moveToNext()) {
            ((TextView) findViewById(R.id.first_name_oprtr)).setText(cursor.getString(cursor.getColumnIndex("firstname")));
            ((TextView) findViewById(R.id.last_name_oprtr)).setText(cursor.getString(cursor.getColumnIndex("lastname")));
            ((TextView) findViewById(R.id.oprtr_id)).setText(cursor.getString(cursor.getColumnIndex("username")));
            ((TextView) findViewById(R.id.email_id_oprtr)).setText(cursor.getString(cursor.getColumnIndex("email")));
            ((TextView) findViewById(R.id.ph_no_oprtr)).setText(cursor.getString(cursor.getColumnIndex("phone")));
            ((TextView) findViewById(R.id.addrs_oprtr)).setText(cursor.getString(cursor.getColumnIndex("address")));
        }
    }
}