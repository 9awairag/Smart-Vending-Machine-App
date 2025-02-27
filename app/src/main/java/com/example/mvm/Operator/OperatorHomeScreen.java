package com.example.mvm.Operator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.OperatorDAO;
import com.example.mvm.EndUser.ContactUsActivity;
import com.example.mvm.MainActivity;
import com.example.mvm.EndUser.ViewProfile;
import com.example.mvm.Manager.ManagerHomeScreen;
import com.example.mvm.R;
import com.example.mvm.User.UserHomeScreen;

import java.util.Map;

public class OperatorHomeScreen extends AppCompatActivity {
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
        onLogoutClick(getApplicationContext());
    }
    AlertDialog.Builder alertBuilder;
    public void onLogoutClick(final Context context) {
        alertBuilder = new AlertDialog.Builder(OperatorHomeScreen.this);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operator_home_screen);
        OperatorDAO oprdb = new OperatorDAO(this);
        oprdb.fullfilInventory();
    }
    public void contactUs(View view) {
        startActivity(new Intent(this, ContactUsActivity.class));
    }
    public void viewCurrentOrders(View view) {
        startActivity(new Intent(this,ViewCurrentOrders.class));
    }
    public void viewProfile(View view) {startActivity(new Intent(this, ViewProfile.class));}
    public void viewVehicleInventory(View view) {startActivity(new Intent(this,ViewVehicleInventory.class));}
    public void viewSchedule(View view) {startActivity(new Intent(this,ViewSchedule.class));}

}
