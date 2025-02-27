package com.example.mvm.EndUser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.example.mvm.MainActivity;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.UserDAO;
import com.example.mvm.Manager.ManagerHomeScreen;
import com.example.mvm.Operator.OperatorHomeScreen;
import com.example.mvm.R;
import com.example.mvm.User.UserHomeScreen;
import com.example.mvm.User.ViewCart;

import java.util.Map;

public class ChangePassword extends AppCompatActivity {
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
        alertBuilder = new AlertDialog.Builder(ChangePassword.this);
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
        setContentView(R.layout.activity_change_password);
        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Map sessionMap = sharedpreferences.getAll();
        if (sessionMap == null || sessionMap.isEmpty())
            startActivity(new Intent(this,MainActivity.class));
    }
    public void change_password(View view) {
        alertBuilder = new AlertDialog.Builder(ChangePassword.this);
        alertBuilder.setTitle("Confirmation");
        alertBuilder.setMessage("Are you sure you want to change the password ?");
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                UserDAO db = new UserDAO(getApplicationContext());
                SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                Map sessionMap = sharedpreferences.getAll();
                String currentPassword = ((EditText) findViewById(R.id.oldpasswordText)).getText().toString().trim();

                String username = sessionMap.get("username").toString();
                if (db.checkPassword(username, currentPassword)) {
                    String newPassword = ((EditText) findViewById(R.id.newPassword)).getText().toString().trim();
                    if (!newPassword.equals(currentPassword)) {
                        String confirmPassword = ((EditText) findViewById(R.id.confirmPassword)).getText().toString().trim();
                        if (newPassword.equals(confirmPassword)) {
                            db.changePassword(username, newPassword);
                            SharedPreferences.Editor session = sharedpreferences.edit();
                            session.clear();
                            session.commit();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            dialogInterface.dismiss();
                            Toast.makeText(getApplicationContext(), "Reset Password Successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Passwords' fields doesn't match", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "New password cannot be same as current password", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Current Password is incorrect", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        if (((EditText) findViewById(R.id.confirmPassword)).getText().toString().trim().length() > 0 && ((EditText) findViewById(R.id.oldpasswordText)).getText().toString().trim().length() > 0 && ((EditText) findViewById(R.id.newPassword)).getText().toString().trim().length() > 0) {
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();
        } else
            Toast.makeText(getApplicationContext(), "All fields are required to fill", Toast.LENGTH_SHORT).show();

    }
}
