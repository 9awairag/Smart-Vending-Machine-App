package com.example.mvm.User;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.braintreepayments.cardform.view.CardForm;
import com.example.mvm.DB.OperatorDAO;
import com.example.mvm.DB.OrderDAO;
import com.example.mvm.MainActivity;
import com.example.mvm.Manager.ManagerHomeScreen;
import com.example.mvm.Operator.OperatorHomeScreen;
import com.example.mvm.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Checkout extends AppCompatActivity {
    SharedPreferences sharedpreferences;

    CardForm cardForm;
    Button buy;
    AlertDialog.Builder alertBuilder;
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.parent_menu, menu);
        MenuItem cartItem = menu.getItem(4);
        cartItem.setIcon(0);
        cartItem.setTitle("");
        cartItem.setEnabled(false);
        return true;
    }
    AlertDialog.Builder alertBuilder1;
    public void onLogoutClick(final Context context) {
        alertBuilder1 = new AlertDialog.Builder(Checkout.this);
        alertBuilder1.setTitle("Confirm Logout");
        alertBuilder1.setMessage("Are you sure you want to logout ?");
        alertBuilder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(context, MainActivity.class));
                dialogInterface.dismiss();
            }
        });
        alertBuilder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertBuilder1.create();
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
                    startActivity(new Intent(this,UserHomeScreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        cardForm = findViewById(R.id.card_form);
        buy = findViewById(R.id.placeOrder);
        cardForm.cardRequired(true)
                .cardholderName(1)
                .expirationRequired(true)
                .cvvRequired(true)
                .setup(Checkout.this);
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardForm.isValid()) {
                    alertBuilder = new AlertDialog.Builder(Checkout.this);
                    alertBuilder.setTitle("Confirm before purchase");
                    alertBuilder.setMessage("Card number: " + cardForm.getCardNumber() + "\n" +
                            "Card expiry date: " + cardForm.getExpirationDateEditText().getText().toString() + "\n" +
                            "Card CVV: " + cardForm.getCvv());
                    alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            OrderDAO orderDao = new OrderDAO(Checkout.this);
                            OperatorDAO optDb = new OperatorDAO(Checkout.this);
                            sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                            Map sessionMap = sharedpreferences.getAll();
                            String username = (String) sessionMap.get("username");
                            String vehicleId = (String) sessionMap.get("vehicle");
                            String pickupTime = (String) sessionMap.get("timeSlot");
                            String locationId = (String) sessionMap.get("pickupLocation");
                            float grandTotal = (float) sessionMap.get("grandTotal");
                            int drinks = Integer.parseInt(String.valueOf(sessionMap.get("drinks")));
                            int sandwitches = Integer.parseInt(String.valueOf(sessionMap.get("sandwitches")));
                            int snacks = Integer.parseInt(String.valueOf(sessionMap.get("snacks")));
                            SimpleDateFormat formatForOrderId = new SimpleDateFormat("yyyyMMddHHmmss");
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = new Date();
                            String orderId = formatForOrderId.format(date);
                            boolean updatedInventoryForDrinks = true, updatedInventoryForSandwitches = true, updatedInventoryForSnacks = true;
                            if (drinks > 0)
                                updatedInventoryForDrinks = optDb.updateInventory(simpleDateFormat.format(date), "DRINKS", vehicleId, drinks);
                            if (updatedInventoryForDrinks) {
                                if (sandwitches > 0)
                                    updatedInventoryForSandwitches = optDb.updateInventory(simpleDateFormat.format(date), "SANDWITCHES", vehicleId, sandwitches);
                                if (updatedInventoryForSandwitches) {
                                    if (snacks > 0)
                                        updatedInventoryForSnacks = optDb.updateInventory(simpleDateFormat.format(date), "SNACKS", vehicleId, snacks);
                                    if (!updatedInventoryForSnacks)
                                        Toast.makeText(Checkout.this, "Invalid quantity of snacks were added", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(Checkout.this, "Invalid quantity of sandwitches were added", Toast.LENGTH_LONG).show();
                                }
                            } else
                                Toast.makeText(Checkout.this, "Invalid quantity of drinks were added", Toast.LENGTH_LONG).show();
                            if (updatedInventoryForDrinks && updatedInventoryForSandwitches && updatedInventoryForSnacks) {
                                boolean placedOrder = orderDao.placeOrder(orderId, username, vehicleId, locationId, pickupTime, grandTotal);
                                boolean addedDrinkToOrder = true, addedSnacksToOrder = true, addedSandwitchesToOrder = true;
                                if (placedOrder) {
                                    if (drinks > 0)
                                        addedDrinkToOrder = orderDao.addOrderItems(orderId, "DRINKS", drinks, ((Integer) sessionMap.get("drinks")) * Float.parseFloat(optDb.getUnitCost("DRINKS")));
                                    if (sandwitches > 0)
                                        addedSandwitchesToOrder = orderDao.addOrderItems(orderId, "SANDWITCHES", sandwitches, ((Integer) sessionMap.get("sandwitches")) * Float.parseFloat(optDb.getUnitCost("SANDWITCHES")));
                                    if (snacks > 0)
                                        addedSnacksToOrder = orderDao.addOrderItems(orderId, "SNACKS", snacks, ((Integer) sessionMap.get("snacks")) * Float.parseFloat(optDb.getUnitCost("SNACKS")));
                                    if (addedSnacksToOrder && addedSandwitchesToOrder && addedDrinkToOrder) {
                                        Toast.makeText(Checkout.this, "Thank you for purchase", Toast.LENGTH_LONG).show();
                                        SharedPreferences.Editor session = sharedpreferences.edit();
                                        session.remove("cart");
                                        session.remove("drinks");
                                        session.remove("snacks");
                                        session.remove("grandTotal");
                                        session.remove("sandwitches");
                                        session.remove("subtotal");
                                        session.remove("pickupLocation");
                                        session.remove("timeSlot");
                                        session.remove("location");
                                        session.remove("vehicle");
                                        session.commit();
                                        dialogInterface.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), OrderDetails.class);
                                        intent.putExtra("orderId", orderId);
                                        startActivityForResult(intent, 0);
                                    }
                                } else
                                    Toast.makeText(Checkout.this, "Unable to place order, Try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();

                } else {
                    Toast.makeText(Checkout.this, "Please complete the form", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
