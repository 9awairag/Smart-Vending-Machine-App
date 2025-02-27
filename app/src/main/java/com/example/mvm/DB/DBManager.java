package com.example.mvm.DB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DBManager extends SQLiteOpenHelper
{

    private static final String dbname = "VendingVehicleMachine.db";
    public DBManager( Context context) {
        super(context,dbname , null, 1);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(SQLiteDatabase db) {
        String qry = "create table tbl_registerUser(firstname text, " +
                "lastname text, username text primary key,password text,usertype text,email text,phone text,address text," +
                "city text,state text,zipcode text,secques text, secans text)";
        db.execSQL(qry);

        qry = "create table Location (locationId text primary key, description text)";
        ContentValues cv = new ContentValues();
        db.execSQL(qry);

        cv.put("locationId","COOPER_UTA_BLUD");
        cv.put("description","Cooper & UTA Blud");
        db.insert("Location", null,cv );

        cv.put("locationId","W_NEDDERMEN_GREEK_ROW");
        cv.put("description","W Nedderman & Greek Row");
        db.insert("Location", null,cv );

        cv.put("locationId","S_DAVIS_W_MITCHELL");
        cv.put("description","S Davis & W Mitchell");
        db.insert("Location", null,cv );

        cv.put("locationId","COOPER_W_MITCHELL");
        cv.put("description","Cooper & W Mitchell");
        db.insert("Location", null,cv );

        cv.put("locationId","S_OAK_UTA_BLUD");
        cv.put("description","S Oak & UTA Blud");
        db.insert("Location", null,cv );

        cv.put("locationId","SPANIOLO_W_1ST");
        cv.put("description","Spaniolo & W 1st");
        db.insert("Location", null,cv );

        cv.put("locationId","SPANIOLO_W_MITCHELL");
        cv.put("description","Spaniolo & W Mitchell");
        db.insert("Location", null,cv );

        cv.put("locationId","S_CENTER_W_MITCHELL");
        cv.put("description","S Center & W Mitchell");
        db.insert("Location", null,cv );

        qry = "create table LocationDuration (locationId text, startTime integer, endTime integer, foreign key(locationId) references Location(locationId))";
        db.execSQL(qry);

        cv = new ContentValues();
        cv.put("locationId","COOPER_UTA_BLUD");
        cv.put("startTime",8);
        cv.put("endTime",10);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","COOPER_UTA_BLUD");
        cv.put("startTime",10);
        cv.put("endTime",12);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","COOPER_UTA_BLUD");
        cv.put("startTime",13);
        cv.put("endTime",15);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","COOPER_UTA_BLUD");
        cv.put("startTime",15);
        cv.put("endTime",17);
        db.insert("LocationDuration", null,cv );


        cv.put("locationId","W_NEDDERMEN_GREEK_ROW");
        cv.put("startTime",8);
        cv.put("endTime",9);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","W_NEDDERMEN_GREEK_ROW");
        cv.put("startTime",9);
        cv.put("endTime",10);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","W_NEDDERMEN_GREEK_ROW");
        cv.put("startTime",10);
        cv.put("endTime",11);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","W_NEDDERMEN_GREEK_ROW");
        cv.put("startTime",11);
        cv.put("endTime",12);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","W_NEDDERMEN_GREEK_ROW");
        cv.put("startTime",12);
        cv.put("endTime",13);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","W_NEDDERMEN_GREEK_ROW");
        cv.put("startTime",13);
        cv.put("endTime",14);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","W_NEDDERMEN_GREEK_ROW");
        cv.put("startTime",14);
        cv.put("endTime",15);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","W_NEDDERMEN_GREEK_ROW");
        cv.put("startTime",15);
        cv.put("endTime",16);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","W_NEDDERMEN_GREEK_ROW");
        cv.put("startTime",16);
        cv.put("endTime",17);
        db.insert("LocationDuration", null,cv );

        cv.put("locationId","S_DAVIS_W_MITCHELL");
        cv.put("startTime",8);
        cv.put("endTime",10);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_DAVIS_W_MITCHELL");
        cv.put("startTime",10);
        cv.put("endTime",12);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_DAVIS_W_MITCHELL");
        cv.put("startTime",13);
        cv.put("endTime",15);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_DAVIS_W_MITCHELL");
        cv.put("startTime",15);
        cv.put("endTime",17);
        db.insert("LocationDuration", null,cv );

        cv.put("locationId","COOPER_W_MITCHELL");
        cv.put("startTime",8);
        cv.put("endTime",11);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","COOPER_W_MITCHELL");
        cv.put("startTime",11);
        cv.put("endTime",14);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","COOPER_W_MITCHELL");
        cv.put("startTime",14);
        cv.put("endTime",17);
        db.insert("LocationDuration", null,cv );

        cv.put("locationId","S_OAK_UTA_BLUD");
        cv.put("startTime",8);
        cv.put("endTime",10);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_OAK_UTA_BLUD");
        cv.put("startTime",10);
        cv.put("endTime",12);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_OAK_UTA_BLUD");
        cv.put("startTime",13);
        cv.put("endTime",15);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_OAK_UTA_BLUD");
        cv.put("startTime",15);
        cv.put("endTime",17);
        db.insert("LocationDuration", null,cv );


        cv.put("locationId","SPANIOLO_W_1ST");
        cv.put("startTime",8);
        cv.put("endTime",12);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","SPANIOLO_W_1ST");
        cv.put("startTime",13);
        cv.put("endTime",17);
        db.insert("LocationDuration", null,cv );


        cv.put("locationId","SPANIOLO_W_MITCHELL");
        cv.put("startTime",8);
        cv.put("endTime",10);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","SPANIOLO_W_MITCHELL");
        cv.put("startTime",10);
        cv.put("endTime",12);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","SPANIOLO_W_MITCHELL");
        cv.put("startTime",13);
        cv.put("endTime",15);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","SPANIOLO_W_MITCHELL");
        cv.put("startTime",15);
        cv.put("endTime",17);
        db.insert("LocationDuration", null,cv );


        cv.put("locationId","S_CENTER_W_MITCHELL");
        cv.put("startTime",8);
        cv.put("endTime",9);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_CENTER_W_MITCHELL");
        cv.put("startTime",8);
        cv.put("endTime",9);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_CENTER_W_MITCHELL");
        cv.put("startTime",9);
        cv.put("endTime",10);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_CENTER_W_MITCHELL;");
        cv.put("startTime",10);
        cv.put("endTime", 11);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_CENTER_W_MITCHELL");
        cv.put("startTime",11);
        cv.put("endTime",12);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_CENTER_W_MITCHELL");
        cv.put("startTime",12);
        cv.put("endTime",13);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_CENTER_W_MITCHELL");
        cv.put("startTime",13);
        cv.put("endTime",14);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_CENTER_W_MITCHELL");
        cv.put("startTime",14);
        cv.put("endTime",15);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_CENTER_W_MITCHELL");
        cv.put("startTime",15);
        cv.put("endTime",16);
        db.insert("LocationDuration", null,cv );
        cv.put("locationId","S_CENTER_W_MITCHELL");
        cv.put("startTime",16);
        cv.put("endTime",17);
        db.insert("LocationDuration", null,cv );

        qry = "create table Vehicle (vehicleId text primary key, description text, vehicleType text)";
        db.execSQL(qry);
        cv = new ContentValues();
        cv.put("vehicleId","TRUCK_1");
        cv.put("description","Truck 1");
        cv.put("vehicleType","Truck");
        db.insert("Vehicle", null,cv );

        cv.put("vehicleId","TRUCK_2");
        cv.put("description","Truck 2");
        cv.put("vehicleType","Truck");
        db.insert("Vehicle", null,cv );

        cv.put("vehicleId","CART_1");
        cv.put("description","Cart 1");
        cv.put("vehicleType","Cart");
        db.insert("Vehicle", null,cv );

        cv.put("vehicleId","CART_2");
        cv.put("description","Cart 2");
        cv.put("vehicleType","Cart");
        db.insert("Vehicle", null,cv );

        cv.put("vehicleId","CART_3");
        cv.put("description","Cart 3");
        cv.put("vehicleType","Cart");
        db.insert("Vehicle", null,cv );

        cv.put("vehicleId","CART_4");
        cv.put("description","Cart 4");
        cv.put("vehicleType","Cart");
        db.insert("Vehicle", null,cv );

        cv.put("vehicleId","CART_5");
        cv.put("description","Cart 5");
        cv.put("vehicleType","Cart");
        db.insert("Vehicle", null,cv );

        qry = "create table VehicleOperatorAndLocation (vehicleId text, locationId text, username text default null, startTime integer, endTime integer, date text default null, foreign key (locationId) references Location(locationId), foreign key (vehicleId) references Vehicle(vehicleId), foreign key (username) references tbl_registerUser(username))";
        db.execSQL(qry);

        qry = "create table Item (itemId text primary key, description text, unitCost real)";
        db.execSQL(qry);

        cv = new ContentValues();
        cv.put("itemId","SANDWITCHES");
        cv.put("description","Sandwitches");
        cv.put("unitCost", 5.75);
        db.insert("Item", null,cv );

        cv.put("itemId","DRINKS");
        cv.put("description","Drinks");
        cv.put("unitCost", 1.50);
        db.insert("Item", null,cv );

        cv.put("itemId","SNACKS");
        cv.put("description","Snacks");
        cv.put("unitCost",1.25);
        db.insert("Item", null,cv );

        String datePattern = "yyyy-MM-dd";
        qry = "create table Inventory (itemId text, vehicleId text, vehicleType text, quantity integer, placeQuantity integer default 0, thruDate text default '"+ (new SimpleDateFormat(datePattern)).format(new Date()) +"', foreign key (itemId) references Items(itemId), foreign key (vehicleId) references Vehicle(vehicleId))";
        db.execSQL(qry);

        qry = "insert into Inventory (vehicleId, vehicleType, itemId) select vehicleId, vehicleType, itemId from Vehicle INNER JOIN Item";
        db.execSQL(qry);

        cv = new ContentValues();
        cv.put("quantity",50);
        db.update("Inventory", cv, "vehicleType = 'Truck' and itemId = 'DRINKS'", null );

        cv.put("quantity",35);
        db.update("Inventory", cv, "vehicleType = 'Truck' and itemId = 'SANDWITCHES'", null );

        cv.put("quantity",40);
        db.update("Inventory", cv, "vehicleType = 'Truck' and itemId = 'SNACKS'", null );

        cv.put("quantity",30);
        db.update("Inventory", cv, "vehicleType = 'Cart' and itemId = 'DRINKS'", null );

        cv.put("quantity",5);
        db.update("Inventory", cv, "vehicleType = 'Cart' and itemId = 'SANDWITCHES'", null );

        cv.put("quantity",30);
        db.update("Inventory", cv, "vehicleType = 'Cart' and itemId = 'SNACKS'", null );

        qry = "create table Orders (orderId text primary key, username text, vehicleId text, locationId text, pickupTime integer, orderDate text, orderStatus text, grandTotal real, foreign key (username) references tbl_registerUser(username), foreign key (locationId) references Location(locationId), foreign key (vehicleId) references Vehicle(vehicleId))";
        db.execSQL(qry);

        qry = "create table OrderItem (orderId text, itemId text, quantity integer, totalCost real, foreign key (orderId) references Orders(orderId), foreign key (itemId) references Items(itemId))";
        db.execSQL(qry);


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tbl_registerUser");
        onCreate(db);
    }

    public String addRecord(String p1, String p2, String p3,String p4,String p5,String p6,
                            String p7,String p8,String p9, String p10, String p11,String p12, String p13)

    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("firstname",p1);
        cv.put("lastname",p2);
        cv.put("username",p3);
        cv.put("password",p4);
        cv.put("usertype",p5);
        cv.put("email",p6);
        cv.put("phone",p7);
        cv.put("address",p8);
        cv.put("city",p9);
        cv.put("state",p10);
        cv.put("zipcode",p11);
        cv.put("secques",p12);
        cv.put("secans",p13);

        long res = db.insert("tbl_registerUser", null,cv );
        if(res== -1)
            return "failed";
        else
            return "Account Created Successfully";

    }
}
