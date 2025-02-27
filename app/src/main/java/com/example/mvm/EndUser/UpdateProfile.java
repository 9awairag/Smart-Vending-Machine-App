package com.example.mvm.EndUser;

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
import android.widget.EditText;
import android.widget.TextView;
import com.example.mvm.MainActivity;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.UserDAO;
import com.example.mvm.Manager.ManagerHomeScreen;
import com.example.mvm.Operator.OperatorHomeScreen;
import com.example.mvm.R;
import com.example.mvm.User.UserHomeScreen;
import com.example.mvm.User.ViewCart;

import java.util.Map;

public class UpdateProfile extends AppCompatActivity {
    SharedPreferences sharedpreferences;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.parent_menu, menu);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Map sessionMap = sharedpreferences.getAll();
        MenuItem cartItem = menu.getItem(4);
        cartItem.setIcon(0);
        cartItem.setTitle("");
        cartItem.setEnabled(false);
        return true;
    }
    AlertDialog.Builder alertBuilder;
    public void onLogoutClick(final Context context) {
        alertBuilder = new AlertDialog.Builder(UpdateProfile.this);
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
                    startActivity(new Intent(this, UserHomeScreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Map sessionMap = sharedpreferences.getAll();
        if (sessionMap == null || sessionMap.isEmpty()) {
            startActivity(new Intent(this,MainActivity.class));
            return;
        }
        UserDAO db = new UserDAO(this);
        Cursor cursor = db.getUserDetails((sessionMap.get("username")).toString());

        while(cursor.moveToNext()) {
            ((EditText) findViewById(R.id.firstnameText)).setText(cursor.getString(cursor.getColumnIndex("firstname")));
            ((EditText) findViewById(R.id.lastnameText)).setText(cursor.getString(cursor.getColumnIndex("lastname")));
            ((TextView) findViewById(R.id.usernameText)).setText(cursor.getString(cursor.getColumnIndex("username")));
            ((TextView) findViewById(R.id.usertypeText)).setText(cursor.getString(cursor.getColumnIndex("usertype")));
            ((EditText) findViewById(R.id.emailText)).setText(cursor.getString(cursor.getColumnIndex("email")));
            ((EditText) findViewById(R.id.phoneText)).setText(cursor.getString(cursor.getColumnIndex("phone")));
            ((EditText) findViewById(R.id.streetaddressText)).setText(cursor.getString(cursor.getColumnIndex("address")));
            ((EditText) findViewById(R.id.cityText)).setText(cursor.getString(cursor.getColumnIndex("city")));
            ((EditText) findViewById(R.id.stateText)).setText(cursor.getString(cursor.getColumnIndex("state")));
            ((EditText) findViewById(R.id.zipcodeText)).setText(cursor.getString(cursor.getColumnIndex("zipcode")));
        }
    }
    public void update(View view) {
        String firstNameText = ((EditText) findViewById(R.id.firstnameText)).getText().toString().trim();
        String lastNameText = ((EditText) findViewById(R.id.lastnameText)).getText().toString().trim();
        String emailText = ((EditText) findViewById(R.id.emailText)).getText().toString().trim();
        String phoneText = ((EditText) findViewById(R.id.phoneText)).getText().toString().trim();
        String addressText = ((EditText) findViewById(R.id.streetaddressText)).getText().toString().trim();
        String cityText = ((EditText) findViewById(R.id.cityText)).getText().toString().trim();
        String stateText = ((EditText) findViewById(R.id.stateText)).getText().toString().trim();
        String zipcodeText = ((EditText) findViewById(R.id.zipcodeText)).getText().toString().trim();

        UserDAO db = new UserDAO(this);
        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Map sessionMap = sharedpreferences.getAll();
        db.updateProfile((sessionMap.get("username")).toString(), firstNameText, lastNameText, phoneText, emailText, addressText, cityText, stateText, zipcodeText);

        Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ViewProfile.class));
    }
}
