package com.feight.inventoryappui;

/*
 *    Victor Feight (victor.feight@snhu.edu)
 *    SNHU Project 3
 *    CS-360 - Mobile Architect & Programming
 *    12/10/21
 *
 */

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class UserRegisterActivity extends AppCompatActivity {

    Button registerUserButton, cancelButton;
    EditText person_name, phone_number, email, password;
    Boolean fieldEmpty;
    SQLiteDatabase db;
    SQLiteUserDBHelper sqlite_helper;
    String query_found = "not_found";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        // store field variables and declare buttons
        email = findViewById(R.id.editTextEmailAddress);
        password = findViewById(R.id.editTextPassword);
        person_name = findViewById(R.id.editTextPersonName);
        phone_number = findViewById(R.id.editTextPhoneNumber);
        cancelButton = findViewById(R.id.registerCancelButton);
        registerUserButton = findViewById(R.id.registerSignupButton);

        // handler for SQL functionality
        sqlite_helper = new SQLiteUserDBHelper(this);


        // cancelButton click listener
        cancelButton.setOnClickListener(view -> {
            // return to activity_login
            startActivity(new Intent(UserRegisterActivity.this, UserLoginActivity.class));
            this.finish();
        });

        // registerUserButton click listener
        registerUserButton.setOnClickListener(view -> {

            String message = determineEmptyFields();

            if (!fieldEmpty) {
                // Check if email already exists in database
                checkEmailInDB();
                // Empty editText fields after done inserting in database
                person_name.getText().clear();
                phone_number.getText().clear();
                email.getText().clear();
                password.getText().clear();
            } else {
                // Display toast on any empty field
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });


    }

    // Register user into SQLite db
    public void insertUserToDB(){

        // Get user info from fields
        String phone = phone_number.getText().toString().trim();
        String email = this.email.getText().toString().trim();
        String name = person_name.getText().toString().trim();
        String pass = password.getText().toString().trim();

        UserEntry user = new UserEntry(name, phone, email, pass);
        sqlite_helper.createUser(user);

        // toast message on success
        Toast.makeText(UserRegisterActivity.this,"Successful Registration!", Toast.LENGTH_LONG).show();

        // Return to activity_login
        startActivity(new Intent(UserRegisterActivity.this, UserLoginActivity.class));
        this.finish();
    }

    // Checking fields are not empty
    public String determineEmptyFields() {
        // Getting value from fields and storing into string variables
        String message = "";
        String name = person_name.getText().toString().trim();
        String phone = phone_number.getText().toString().trim();
        String email = this.email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        // check if current field is empty
        if (name.isEmpty()) {
            person_name.requestFocus();
            fieldEmpty = true;
            message = "Empty username field";
        } else if (phone.isEmpty()){
            phone_number.requestFocus();
            fieldEmpty = true;
            message = "Empty phone number field";
        } else if (email.isEmpty()){
            this.email.requestFocus();
            fieldEmpty = true;
            message = "Empty email field";
        } else if (pass.isEmpty()){
            password.requestFocus();
            fieldEmpty = true;
            message = "Empty password field";
        } else {
            fieldEmpty = false;
        }
        return message;
    }

    // checks if email in SQLite db already
    public void checkEmailInDB(){

        // reference to db handler
        db = sqlite_helper.getWritableDatabase();

        // current user email
        String email = this.email.getText().toString().trim();

        // SQLite query to search for user email in email columns
        Cursor cursor = db.query(SQLiteUserDBHelper.USERS_TABLE_NAME, null, " " + SQLiteUserDBHelper.COL3_email + "=?", new String[]{email}, null, null, null);

        while (cursor.moveToNext()) {
            if (cursor.isFirst()) {
                cursor.moveToFirst();
                // set result if found and close cursor
                query_found = "Email Found";
                cursor.close();
            }
        }
        sqlite_helper.close();

        // If email in DB, display with toast
        if(query_found.equalsIgnoreCase("Email Found"))
        {
            Toast.makeText(UserRegisterActivity.this,"Email Already Exists",Toast.LENGTH_LONG).show();
        }
        else {
            // If no email enter user to database
            insertUserToDB();
        }
        query_found = "not_found" ;
    }

}

