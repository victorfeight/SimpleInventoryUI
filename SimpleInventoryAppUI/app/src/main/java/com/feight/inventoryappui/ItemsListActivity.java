package com.feight.inventoryappui;

/*
 *    Victor Feight (victor.feight@snhu.edu)
 *    SNHU Project 3
 *    CS-360 - Mobile Architect & Programming
 *    12/10/21
 *
 */


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;


public class ItemsListActivity extends AppCompatActivity {

    // Declare buttons and ListView to display items
    TextView user_name, item_total_count;
    ListView ItemsListView;
    ImageButton add_button, sms_button;

    SQLiteItemDBHelper db;
    static String NameHolder, EmailHolder, PhoneNumHolder;
    AlertDialog AlertDialog = null;
    ArrayList<ItemEntry> all_items;

    // We will be displaying a custom itemRowList
    ItemRowList itemRowList;
    int itemsCount;

    public static final String UserEmail = "";
    private static final int USER_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static boolean smsAuthorized = false;
    private static boolean deleteItems = false;

    // Sign out button on top
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu_top, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.exitApp) {
            // Close AddItemActivity when clicking exit button
            db.close();
            super.finish();
            Toast.makeText(this,"Logged Out: Success", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                itemsCount = db.getItemsCount();
                item_total_count.setText(String.valueOf(itemsCount));

                if(itemRowList == null)	{
                    itemRowList = new ItemRowList(this, all_items, db);
                    ItemsListView.setAdapter(itemRowList);
                }

                // get all items and notify view to refresh itself
                itemRowList.items = (ArrayList<ItemEntry>) db.getAllItems();
                ((BaseAdapter)ItemsListView.getAdapter()).notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        // initialize name, count, buttons, View variables
        add_button = findViewById(R.id.addItemButton);
        sms_button = findViewById(R.id.smsNotification);
        user_name = findViewById(R.id.textViewUserNameLabel);
        item_total_count = findViewById(R.id.textViewTotalItemsCount);
        ItemsListView = findViewById(R.id.bodyListView);
        db = new SQLiteItemDBHelper(this);

        // LoginActivity sent this bundle, we receive it here
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            NameHolder = bundle.getString("user_name");
            EmailHolder = bundle.getString("user_email");
            PhoneNumHolder = bundle.getString("user_phone");
            // Setting welcome message
            user_name.setText( getString(R.string.greeting, NameHolder.toUpperCase()));
        }


        all_items = (ArrayList<ItemEntry>) db.getAllItems();
        itemsCount = db.getItemsCount();

        // if item count > 0, display vertical collection of views containing all_item and db
        // otherwise toast empty DB
        if (itemsCount > 0) {
            itemRowList = new ItemRowList(this, all_items, db);
            ItemsListView.setAdapter(itemRowList);
        } else {
            Toast.makeText(this, "Database is Empty!", Toast.LENGTH_LONG).show();
        }

        item_total_count.setText(String.valueOf(itemsCount));

        // add_button click listener
        add_button.setOnClickListener(view -> {
            // Opening new AddItemActivity using intent on forgotPasswordButton click.
            Intent add = new Intent(this, AddItemActivity.class);
            add.putExtra(UserEmail, EmailHolder);
            startActivityForResult(add, 1);
        });

        // sms button click listener
        sms_button.setOnClickListener(view -> {
            // Request sms permission for the device
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS)) {
                    Toast.makeText(this,"Device SMS Permission is Needed", Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[] {Manifest.permission.SEND_SMS},
                            USER_PERMISSIONS_REQUEST_SEND_SMS);
                }
            } else {
                Toast.makeText(this,"Device SMS Permission is Allowed", Toast.LENGTH_LONG).show();
            }
            // Open SMS Alert Dialog
            AlertDialog = SMSNotifyDialog.buttonToggle(this);
            AlertDialog.show();
        });

    }


    // Receive and evaluate user response from AlertDialog to send SMS
    public static void AllowSendSMS() {
        smsAuthorized = true;
    }

    public static void DenySendSMS() {
        smsAuthorized = false;
    }

    public static void SendSMSMessage(Context context) {
        String phone_number = PhoneNumHolder;
        String sms_msg = "An item in the inventory is empty (0 remaining).";

        // AlertDialogPermission
        if (smsAuthorized) {
            try {
                SmsManager sms_manager = SmsManager.getDefault();
                sms_manager.sendTextMessage(phone_number, null, sms_msg, null, null);
                Toast.makeText(context, "SMS Sent", Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(context, "SMS Alert Disabled", Toast.LENGTH_LONG).show();
        }
    }
}
