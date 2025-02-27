package com.example.mvm.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OperatorDAO extends SQLiteOpenHelper {
    private static final String dbname = "VendingVehicleMachine.db";
    private SQLiteDatabase db;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public OperatorDAO( Context context) {
        super(context,dbname , null, 1);
    }

    public Cursor getAllVehiclesWithAssignedOperatorAndLocation(String vehicleId, String locationId, String date, String orderByField) {
        db = this.getWritableDatabase();

        String[] columns = new String[]{"vehicleId", "username", "locationId", "startTime", "endTime"};
        String condition = "";
        if (vehicleId != null)
            condition+= "vehicleId = '"+vehicleId+"'";
        if (locationId != null) {
            if (condition.length() > 0)
                condition+=" or ";
            condition+= "locationId = '"+locationId+"'";
        }

        Cursor cursor = db.query("VehicleOperatorAndLocation", columns, "date = '"+date+"'"+(condition.length() > 0 ? " and ("+condition+")" : "") , null, null, null, orderByField+" ASC, startTime ASC, endTime ASC");
        return cursor;
    }
    public String getDescription(String table, String id) {
        db = this.getWritableDatabase();
        String[] columns = new String[]{"description"};
        Cursor cursor = db.query(table, columns, table+"Id = '"+id+"'", null, null, null, null);
        while (cursor.moveToNext()) {
            return cursor.getString(cursor.getColumnIndex("description"));
        }
        return new String();
    }
    public Cursor getOperators() {
        db = this.getWritableDatabase();
        String[] columns = new String[]{"username"};
        Cursor cursor = db.query("tbl_registerUser", columns, "userType = 'Operator'", null, null, null, null);
        return cursor;
    }
    public Cursor getVehicles() {
        db = this.getWritableDatabase();
        String[] columns = new String[]{"vehicleId", "description"};
        Cursor cursor = db.query("Vehicle", columns, null, null, null, null, "description ASC");
        return cursor;
    }
    public Cursor getLocations() {
        db = this.getWritableDatabase();
        String[] columns = new String[]{"locationId", "description"};
        Cursor cursor = db.query("Location", columns, null, null, null, null, null);
        return cursor;
    }
    public boolean canAssignOperator(String username, String vehicleId, String date) {
        db = this.getWritableDatabase();
        Cursor cursor = db.query("VehicleOperatorAndLocation", null, " username = '"+username+"' and date = '"+date+"' and vehicleId != '"+vehicleId+"'" , null, null, null, null);
        if (cursor != null && cursor.getCount() > 0)
            return false;
        return true;
    }
    public void assignOperator(String username, String vehicleId, String date) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.query("VehicleOperatorAndLocation", null, " vehicleId = '"+vehicleId+"' and date = '"+date+"'" , null, null, null, null);
        cv.put("username", username);
        cv.put("date", date);
        cv.put("vehicleId", vehicleId);
        if (cursor.getCount() > 0)
            db.update("VehicleOperatorAndLocation", cv, "vehicleId = '"+vehicleId+"' and date = '"+date+"'" , null );
        else
            db.insert("VehicleOperatorAndLocation", null, cv);
    }
    public String getCurrentVehicle(String username) {
        db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(new Date());
        String vehicleId = "";
        Cursor cursor = db.query("VehicleOperatorAndLocation", null, " username = '"+username+"' and date = '"+date+"'", null, null, null, null);
        while (cursor.moveToNext()) {
            vehicleId = cursor.getString(cursor.getColumnIndex("vehicleId"));
            return vehicleId;
        }
        return vehicleId;
    }
    public boolean canAssignLocation(String vehicleId, String date) {
        db = this.getWritableDatabase();
        Cursor cursor = db.query("VehicleOperatorAndLocation", null, " vehicleId = '"+vehicleId+"' and date = '"+date+"'" , null, null, null, null);
        if (cursor != null && cursor.getCount() > 0)
            return true;
        return false;
    }
    public void assignLocation(String locationId, String vehicleId, String startTime, String endTime, String date) {
        db = this.getWritableDatabase();
        String operator = "";
        Cursor cursor = db.query("VehicleOperatorAndLocation", null, "vehicleId = '"+vehicleId+"' and date = '"+date+"'", null, null, null, null);
        boolean insert = false;
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("startTime")) != null && Integer.parseInt(cursor.getString(cursor.getColumnIndex("startTime"))) > 0) {
                operator = cursor.getString(cursor.getColumnIndex("username"));
                insert = true;
                break;
            }
        }
        ContentValues cv = new ContentValues();
        cv.put("locationId", locationId);
        cv.put("startTime", startTime);
        cv.put("endTime", endTime);
        cv.put("vehicleId", vehicleId);
        cv.put("date", date);
        if (!insert)
            db.update("VehicleOperatorAndLocation", cv, "vehicleId = '"+vehicleId+"' and date = '"+date+"'" , null );
        else {
            if (operator.length() > 0)
                cv.put("username", operator);
            db.insert("VehicleOperatorAndLocation", null, cv);
        }
    }
    public Cursor getTimeSlot(String locationId) {
        db = this.getWritableDatabase();
        String[] columns = new String[]{"startTime", "endTime"};
        Cursor cursor = db.query("LocationDuration", columns, "locationId = '"+locationId+"'", null, null, null, null);
        return cursor;
    }
    public Cursor getVehicleInventory(String vehicleId) {
        db = this.getWritableDatabase();
        String condition = "vehicleId = '"+vehicleId+"'";
        Cursor cursor = db.query("Inventory", null, condition, null, null, null, null);
        return cursor;
    }
    public String getVehicleType(String vehicleId) {
        db = this.getWritableDatabase();
        String condition = "vehicleId = '"+vehicleId+"'";
        Cursor cursor = db.query("Vehicle", null, condition, null, null, null, null);
        while (cursor.moveToNext())
            return cursor.getString(cursor.getColumnIndex("vehicleType"));
        return "Truck";
    }
    public String getUnitCost(String itemId) {
        db = this.getWritableDatabase();
        String condition = "itemId = '"+itemId+"'";
        Cursor cursor = db.query("Item", null, condition, null, null, null, null);
        while (cursor.moveToNext())
            return cursor.getString(cursor.getColumnIndex("unitCost"));
        return "";
    }
    public void refillOrderItems(String vehicleId, String itemId, int quantity) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor inventory = getVehicleInventory(vehicleId);
        int totalQuantity = 0;
        int placedQuantity = 0;
        while(inventory.moveToNext()) {
            String currentItem = inventory.getString(inventory.getColumnIndex("itemId"));
            if (currentItem.equals(itemId)) {
                totalQuantity = Integer.parseInt(inventory.getString(inventory.getColumnIndex("quantity")));
                placedQuantity = Integer.parseInt(inventory.getString(inventory.getColumnIndex("placeQuantity")));
                break;
            }
        }
        cv.put("quantity", totalQuantity + quantity);
        cv.put("placeQuantity", placedQuantity - quantity);
        String condition = "vehicleId = '"+vehicleId+"' and itemId = '"+itemId+"'";
        db.update("Inventory", cv, condition, null);
    }
    public boolean updateInventory(String date, String itemId, String vehicleId, int placedQuantity) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor inventory = getVehicleInventory(vehicleId);
        int totalQuantity = 0;
        int alreadyPlaced = 0;
        while(inventory.moveToNext()) {
            String currentItem = inventory.getString(inventory.getColumnIndex("itemId"));
            if (currentItem.equals(itemId)) {
                totalQuantity = Integer.parseInt(inventory.getString(inventory.getColumnIndex("quantity")));
                alreadyPlaced = Integer.parseInt(inventory.getString(inventory.getColumnIndex("placeQuantity")));
                break;
            }
        }
        if (placedQuantity > totalQuantity)
            return false;
        cv.put("quantity", totalQuantity - placedQuantity);
        cv.put("placeQuantity", placedQuantity + alreadyPlaced);
        String condition = "vehicleId = '"+vehicleId+"' and itemId = '"+itemId+"' and thruDate = '"+date+"'";
        long result = db.update("Inventory", cv, condition, null);
        return result < 0 ? false : true;
    }
    public Cursor getSchedule(String username) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 0);
        Date tomorrow = calendar.getTime();
        db = this.getWritableDatabase();
        Cursor cursor = db.query("VehicleOperatorAndLocation", null, " username = '"+username+"' and date = '"+simpleDateFormat.format(tomorrow)+"'" , null, null, null, "startTime ASC, endTime DESC");
        return cursor;
    }
    public void fullfilInventory() {
        db = this.getWritableDatabase();
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='Inventory'";
        Cursor mCursor = db.rawQuery(sql, null);
        if (mCursor.getCount() <= 0) {
            return;
        }
        String currentDate = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        Cursor cursor = db.query("Inventory", null, "thruDate < '"+currentDate+"'", null, null, null, null);

        while (cursor.moveToNext()) {
            ContentValues cv = new ContentValues();
            String vehicleType = cursor.getString(cursor.getColumnIndex("vehicleType"));
            String itemId = cursor.getString(cursor.getColumnIndex("itemId"));
            String vehicleId = cursor.getString(cursor.getColumnIndex("vehicleId"));
            if (vehicleType.equals("Truck")) {
                if (itemId.equals("SNACKS")) {
                    cv.put("quantity", 40);
                } else if (itemId.equals("DRINKS")) {
                    cv.put("quantity", 50);
                } else
                    cv.put("quantity", 35);
            } else {
                if (itemId.equals("SNACKS")) {
                    cv.put("quantity", 30);
                } else if (itemId.equals("DRINKS")) {
                    cv.put("quantity", 30);
                } else
                    cv.put("quantity", 5);
            }
            cv.put("itemId", itemId);
            cv.put("vehicleType", vehicleType);
            cv.put("vehicleId", vehicleId);
            cv.put("placeQuantity",0);
            cv.put("thruDate", currentDate);
            String whereClause = "itemId = '"+itemId+"' and vehicleType = '"+vehicleType+"' and vehicleId = '"+vehicleId+"'";
            db.update("Inventory", cv, whereClause , null );
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
