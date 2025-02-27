package com.example.mvm.EndUser;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.DBManager;
import com.example.mvm.MainActivity;
import com.example.mvm.R;

public class ForgotPasswordScreen extends AppCompatActivity {
    String email, newPassword, confirmPassword;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_screen);
    }
    public void resetPassword(View view) {
        email = ((EditText) findViewById(R.id.confirmEmail)).getText().toString().trim();
        newPassword = ((EditText) findViewById(R.id.newPassword)).getText().toString().trim();
        confirmPassword = ((EditText) findViewById(R.id.confirmPassword)).getText().toString().trim();
        if (email.length() != 0 && newPassword.length() != 0 || confirmPassword.length() != 0) {
            if (newPassword.equals(confirmPassword)) {
                SQLiteDatabase sqldb = this.openOrCreateDatabase("VendingVehicleMachine.db", MODE_PRIVATE, null);
                String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='tbl_registerUser'";
                Cursor mCursor = sqldb.rawQuery(sql, null);
                if (mCursor.getCount() <= 0) {
                    Toast.makeText(getApplicationContext(), "This emailid doesn't belong to any registered user", Toast.LENGTH_SHORT).show();
                    ((EditText) findViewById(R.id.confirmEmail)).setText("");
                    ((EditText) findViewById(R.id.newPassword)).setText("");
                    ((EditText) findViewById(R.id.confirmPassword)).setText("");
                } else {
                    String queryForCheckingEmail = "Select * from tbl_registerUser where email = '" + email + "'";
                    Cursor cursor = sqldb.rawQuery(queryForCheckingEmail, null);
                    if (cursor.getCount() <= 0) {
                        Toast.makeText(getApplicationContext(), "This emailid doesn't belong to any registered user", Toast.LENGTH_SHORT).show();
                        ((EditText) findViewById(R.id.confirmEmail)).setText("");
                        ((EditText) findViewById(R.id.newPassword)).setText("");
                        ((EditText) findViewById(R.id.confirmPassword)).setText("");
                    } else {
                        DBManager db = new DBManager(this);
                        ContentValues cv = new ContentValues();
                        cv.put("password", newPassword);
                        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
                        sqLiteDatabase.update("tbl_registerUser", cv, "email='" + email + "'", null);
                        Toast.makeText(getApplicationContext(), "Reset Password Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Passwords' fields doesn't match", Toast.LENGTH_SHORT).show();
                ((EditText) findViewById(R.id.confirmEmail)).setText("");
                ((EditText) findViewById(R.id.newPassword)).setText("");
                ((EditText) findViewById(R.id.confirmPassword)).setText("");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Enter required fields", Toast.LENGTH_SHORT).show();
            ((EditText) findViewById(R.id.confirmEmail)).setText("");
            ((EditText) findViewById(R.id.newPassword)).setText("");
            ((EditText) findViewById(R.id.confirmPassword)).setText("");
        }
    }
}
