package com.example.mvm.EndUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.DBManager;
import com.example.mvm.R;
import com.example.mvm.MainActivity;

public class Register extends AppCompatActivity {

    EditText firstNameText,lastNameText,usernameText,passwordText,emailText,phoneText,addressText,cityText,
            stateText,zipcodeText,secquestionText,secanswerText;
    RadioGroup radioGroup;
    RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);
        firstNameText= (EditText)findViewById(R.id.firstNameText);
        lastNameText= (EditText)findViewById(R.id.lastNameText);
        usernameText= (EditText)findViewById(R.id.usernameText);
        passwordText= (EditText)findViewById(R.id.passwordText);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        emailText= (EditText)findViewById(R.id.emailText);
        phoneText= (EditText)findViewById(R.id.phoneText);
        addressText= (EditText)findViewById(R.id.addressText);
        cityText= (EditText)findViewById(R.id.cityText);
        stateText= (EditText)findViewById(R.id.stateText);
        zipcodeText= (EditText)findViewById(R.id.zipcodeText);
        secquestionText= (EditText)findViewById(R.id.secquestionText);
        secanswerText= (EditText)findViewById(R.id.secanswerText);

    }

    public void addRecord(View view)
    {
        DBManager db = new DBManager(this);
        int radioId = radioGroup.getCheckedRadioButtonId();
        String rdtext = null;
        if(radioId != -1 && firstNameText.getText().toString().trim().length() != 0 && lastNameText.getText().toString().trim().length() != 0
                && usernameText.getText().toString().trim().length() != 0 && passwordText.getText().toString().trim().length() != 0
                && emailText.getText().toString().trim().length() != 0 && phoneText.getText().toString().trim().length() != 0
                && addressText.getText().toString().trim().length() != 0 && cityText.getText().toString().trim().length() != 0
                && stateText.getText().toString().trim().length() != 0 && zipcodeText.getText().toString().trim().length() != 0) {
            if (radioId != -1) {
                radioButton = findViewById(radioId);
                rdtext = (String) radioButton.getText();
            }


            String result = db.addRecord(firstNameText.getText().toString(), lastNameText.getText().toString(),
                    usernameText.getText().toString(), passwordText.getText().toString(), rdtext,
                    emailText.getText().toString(), phoneText.getText().toString(), addressText.getText().toString(),
                    cityText.getText().toString(), stateText.getText().toString(), zipcodeText.getText().toString(),
                    secquestionText.getText().toString(), secanswerText.getText().toString());

        /*String result= db.addRecord(firstNameText.getText().toString(),lastNameText.getText().toString(),
                usernameText.getText().toString(), passwordText.getText().toString(),rdtext);*/
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            if (result.equals("Account Created Successfully"))
                startActivity(new Intent(this, MainActivity.class));

            firstNameText.setText("");
            lastNameText.setText("");
            usernameText.setText("");
            passwordText.setText("");
            //radioButton.setText("");
            emailText.setText("");
            phoneText.setText("");
            addressText.setText("");
            cityText.setText("");
            stateText.setText("");
            zipcodeText.setText("");
            secquestionText.setText("");
            secanswerText.setText("");
        } else {
            Toast.makeText(getApplicationContext(), "Enter required fields", Toast.LENGTH_SHORT).show();
            firstNameText.setText("");
            lastNameText.setText("");
            usernameText.setText("");
            passwordText.setText("");
            emailText.setText("");
            phoneText.setText("");
            addressText.setText("");
            cityText.setText("");
            stateText.setText("");
            zipcodeText.setText("");
            secquestionText.setText("");
            secanswerText.setText("");
            radioGroup.clearCheck();
        }
    }
}
