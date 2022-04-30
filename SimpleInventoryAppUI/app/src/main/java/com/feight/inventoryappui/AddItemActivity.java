package com.feight.inventoryappui;

/*
 *    Victor Feight (victor.feight@snhu.edu)
 *    SNHU Project 3
 *    CS-360 - Mobile Architect & Programming
 *    12/10/21
 *
 */

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicReference;


public class AddItemActivity extends AppCompatActivity {

    ImageButton increment, decrement;
    EditText item_description, item_quantity;
    Button cancel_button, add_item_button;
    Boolean empty_place;
    String EmailHolder, description_holder, quantity_holder;
    TextView UserEmail;
    SQLiteItemDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newitem);

        // Initiate buttons, textViews, and editText variables
        UserEmail = findViewById(R.id.textViewLoggedUser);
        item_description = findViewById(R.id.editTextItemDescription);
        increment = findViewById(R.id.itemQtyIncrease);
        decrement = findViewById(R.id.itemQtyDecrease);
        item_quantity = findViewById(R.id.editTextItemQuantity);
        cancel_button = findViewById(R.id.addCancelButton);
        add_item_button = findViewById(R.id.addItemButton);
        db = new SQLiteItemDBHelper(this);

        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());

        // ItemsListActivity given email
        EmailHolder = intent.get().getStringExtra(ItemsListActivity.UserEmail);

        // Set email
        UserEmail.setText(getString(R.string.logged_in_user, EmailHolder));

        // increment button click listener
        increment.setOnClickListener(view -> {
            int input = 0, total;

            String value = item_quantity.getText().toString().trim();

            if (!value.isEmpty()) {
                input = Integer.parseInt(value);
            }

            total = input + 1;
            item_quantity.setText(String.valueOf(total));
        });

        // add_item_button click listener, pass to ItemsListActivity finally
        add_item_button.setOnClickListener(view -> insertItemEntryToDB());

        // Adding click listener to addCancelButton
        cancel_button.setOnClickListener(view -> {
            // Return to ItemsListActivity after cancel adding item
            Intent add = new Intent();
            setResult(0, add);
            this.finish();
        });

        // decrement button click listener
        decrement.setOnClickListener(view -> {
            int input, total;

            String quantity = item_quantity.getText().toString().trim();

            if (quantity.equals("0")) {
                Toast.makeText(this, "Zero Quantity!", Toast.LENGTH_LONG).show();
            } else {
                input = Integer.parseInt(quantity);
                total = input - 1;
                item_quantity.setText(String.valueOf(total));
            }
        });

    }

    // After inserting ItemEntry to db, send data to ItemsListActivity
    public void insertItemEntryToDB() {
        String message = CheckEditTextNotEmpty();

        if (!empty_place) {
            String email = EmailHolder;
            String description = description_holder;
            String quantity = quantity_holder;

            ItemEntry item = new ItemEntry(email, description, quantity);
            db.createItem(item);

            // Display toast message after insert in table
            Toast.makeText(this,"Item Added Successfully", Toast.LENGTH_LONG).show();

            // close AddItemActivity
            Intent add = new Intent();
            setResult(RESULT_OK, add);
            this.finish();
        } else {
            // Display toast message if item description is empty and focus the field
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    // Checking item description is not empty
    public String CheckEditTextNotEmpty() {
        // Getting value from fields and storing into string variable
        String message = "";
        quantity_holder = item_quantity.getText().toString().trim();
        description_holder = item_description.getText().toString().trim();

        // warn user if no description entered, else return message
        if (description_holder.isEmpty()) {
            item_description.requestFocus();
            empty_place = true;
            message = "Empty Item Description!";
        } else {
            empty_place = false;
        }
        return message;
    }

}