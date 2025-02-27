package com.example.mvm.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderDAO extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private static final String dbname = "VendingVehicleMachine.db";
    public OrderDAO( Context context) {
        super(context,dbname , null, 1);
    }

    public String getOrderRevenue(String vehicleId, String locationId, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String condition;
        if (locationId != null)
           condition = "vehicleId = '"+vehicleId+"' and locationId = '"+locationId+"' and orderDate = '"+date+"'";
        else
            condition = "vehicleId = '"+vehicleId+"' and orderDate = '"+date+"'";
        Cursor cursor = db.query("Orders", null, condition, null, null, null, null);
        float revenue = 0;
        while(cursor.moveToNext()) {
            revenue+= Float.parseFloat(cursor.getString(cursor.getColumnIndex("grandTotal")));
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(revenue);
    }

    public boolean placeOrder(String orderId, String username, String vehicleId, String locationId, String pickupTime, float grandTotal) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        SimpleDateFormat formatForDate = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        cv.put("orderId", orderId);
        cv.put("username", username);
        cv.put("vehicleId", vehicleId);
        cv.put("locationId", locationId);
        cv.put("pickupTime", pickupTime);
        cv.put("orderDate", formatForDate.format(date));
        cv.put("grandTotal", grandTotal);
        cv.put("orderStatus", "Pending");
        long result  = db.insert("Orders",null, cv);
        if (result < 0)
            return false;
        return true;
    }
    public boolean addOrderItems(String orderId, String itemId, int quantity, float cost) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("orderId", orderId);
        cv.put("itemId", itemId);
        cv.put("quantity", quantity);
        cv.put("totalCost", cost);
        long result  = db.insert("OrderItem",null, cv);
        if (result < 0)
            return false;
        return true;
    }
    public Cursor getOrders(String username) {
        db = this.getWritableDatabase();
        Cursor cursor = db.query("Orders", null, "username = '"+username+"'", null, null, null, "orderDate DESC, orderId DESC");
        return cursor;
    }
    public Cursor getOrderDetails(String orderId) {
        db = this.getWritableDatabase();
        Cursor cursor = db.query("Orders", null, "orderId = '"+orderId+"'", null, null, null, null);
        return cursor;
    }
    public Cursor getOrderItems(String orderId) {
        db = this.getWritableDatabase();
        Cursor cursor = db.query("OrderItem", null, "orderId = '"+orderId+"'", null, null, null, null);
        return cursor;
    }
    public void changeOrderStatus(String orderId, String status) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("orderStatus", status);
        db.update("Orders", cv, "orderId = '"+orderId+"'", null);
    }

    public Cursor getCurrentOrderForVehicle(String vehicleId) {
        db = this.getWritableDatabase();
        SimpleDateFormat formatForDate = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatForDate.format(new Date());
        Cursor cursor = db.query("Orders", null, "vehicleId = '"+vehicleId+"' and orderDate = '"+date+"'", null, null, null, "orderDate DESC, orderId DESC");
        return cursor;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
