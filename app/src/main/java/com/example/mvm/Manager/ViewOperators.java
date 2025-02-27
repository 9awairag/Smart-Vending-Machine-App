package com.example.mvm.Manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mvm.DB.OperatorDAO;
import com.example.mvm.DB.UserDAO;
import com.example.mvm.MainActivity;
import com.example.mvm.R;

import java.util.*;

public class ViewOperators extends AppCompatActivity {

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.parent_menu, menu);
        MenuItem cartItem = menu.getItem(4);
        cartItem.setIcon(0);
        cartItem.setTitle("");
        cartItem.setEnabled(false);
        return true;
    }
    AlertDialog.Builder alertBuilder;
    public void onLogoutClick(final Context context) {
        alertBuilder = new AlertDialog.Builder(ViewOperators.this);
        alertBuilder.setTitle("Confirm Logout");
        alertBuilder.setMessage("Are you sure you want to logout ?");
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(context, MainActivity.class));
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
    public void onBackPressed() {
        startActivity(new Intent(this,ManagerHomeScreen.class));
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                onLogoutClick(getApplicationContext());
                return true;
            case R.id.home:
                startActivity(new Intent(this,ManagerHomeScreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_operators);
        String[] operators = getResources().getStringArray(R.array.operator_name);
//        final List<String> listItem = new ArrayList<>();
        final List<String> listItemNames = new ArrayList<>();
        final Map<String, String> map = new HashMap<>();
        final ListView list = (ListView) findViewById(R.id.listView1);

        OperatorDAO db = new OperatorDAO(this);
        UserDAO userDAO = new UserDAO(this);
        Cursor cursor = db.getOperators();

        if(cursor.getCount() == 0){
            list.setVisibility(View.INVISIBLE);
            Toast.makeText(this,"No Data",Toast.LENGTH_SHORT).show();
        }
        else {
            while(cursor.moveToNext()) {
//                listItem.add(cursor.getString(cursor.getColumnIndex("username")));
                //TODO: Need to change hashmap implementation
                listItemNames.add(userDAO.getUserFullName(cursor.getString(cursor.getColumnIndex("username"))));
                map.put(userDAO.getUserFullName(cursor.getString(cursor.getColumnIndex("username"))), cursor.getString(cursor.getColumnIndex("username")));
            }
        }
        Collections.sort(listItemNames);
        final ListAdapter myadapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listItemNames);
        list.setAdapter(myadapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                String name = map.get((String) list.getAdapter().getItem(position));
                Intent intent = new Intent(ViewOperators.this, VehicleOperatorDetailsActivity.class);
                intent.putExtra("Name", name);
                startActivity(intent);
            }
        });
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (listItemNames.contains(query)){
                    ((ArrayAdapter) myadapter).getFilter().filter(query);
                } else {
                    Toast.makeText(ViewOperators.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((ArrayAdapter) myadapter).getFilter().filter(newText);
                return false;
            }
        });
    }


}