package com.example.mvm.User;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.EndUser.ContactUsActivity;
import com.example.mvm.MainActivity;
import com.example.mvm.EndUser.ViewProfile;
import com.example.mvm.Manager.ManagerHomeScreen;
import com.example.mvm.Operator.OperatorHomeScreen;
import com.example.mvm.R;

import java.util.Map;

public class UserHomeScreen extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.parent_menu, menu);
        return true;
    }

    public void onBackPressed() {
        onLogoutClick(getApplicationContext());
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_screen);
        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Map sessionMap = sharedpreferences.getAll();
        if (sessionMap == null || sessionMap.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            return;
        }
    }
    public void contactUs(View view) {
        startActivity(new Intent(this, ContactUsActivity.class));
    }
    public void viewProfile(View view) {startActivity(new Intent(this, ViewProfile.class));}
    public void searchVendingVehicle(View view) {startActivity(new Intent(this, SearchVendingVehicleUser.class)); }
    public void viewOrders(View view) {startActivity(new Intent(this,ViewOrders.class)); }
    AlertDialog.Builder alertBuilder;
    public void onLogoutClick(final Context context) {
        alertBuilder = new AlertDialog.Builder(UserHomeScreen.this);
        alertBuilder.setTitle("Confirm Logout");
        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor session = sharedpreferences.edit();
        alertBuilder.setMessage("Are you sure you want to logout ?");
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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

}
