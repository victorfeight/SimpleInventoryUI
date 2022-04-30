package com.feight.inventoryappui;

/*
 *    Victor Feight (victor.feight@snhu.edu)
 *    SNHU Project 3
 *    CS-360 - Mobile Architect & Programming
 *    12/10/21
 *
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Extend AppCompatActivity is good practice for backward compatibility
// and library updates
public class UserLoginActivity extends AppCompatActivity {


    // edit text and popup functionality
    Boolean empty_place;
    PopupWindow forgotPopup;
    EditText email, password;

    // Password entered by user
    String temporary_password_entered = "EMPTY_PASS" ;

    String name_place, phone_number_place, email_place, pass_place;

    // Declare buttons and activity
    Button login_button, register_button, forgot_pass_button;
    Activity activity;


    // SQLiteOpenHelper is extended to manage database functionality
    SQLiteDatabase db;
    SQLiteUserDBHelper sqliteDBHelper;


    // Start Activity (activity_login) with onCreate
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Default activity to load upon starting the app
        setContentView(R.layout.activity_login);
        activity = this;

        // Declare button and field variables
        login_button = findViewById(R.id.signinButton);
        register_button = findViewById(R.id.registerButton);
        forgot_pass_button = findViewById(R.id.forgotPasswordButton);
        email = findViewById(R.id.editTextEmailAddress);
        password = findViewById(R.id.editTextPassword);

        // Create a handler reference to our SQLiteUserDBHelper
        sqliteDBHelper = new SQLiteUserDBHelper(this);


        // Adding click listener to register button
        register_button.setOnClickListener(view -> {
            // On button click, open new RegisterActivity using intent
            Intent intent = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
            startActivity(intent);
        });

        // Adding click listener to login button to call User Login functionality
        login_button.setOnClickListener(view -> {
            UserLogin();
        });


        //  Adding click listener to register forgot password button
        forgot_pass_button.setOnClickListener(view -> {

           // retrieve text from email field
            email_place = email.getText().toString().trim();

            // if email field filled, bring up forgottenPassword, else toast
            if (!email_place.isEmpty()) {
                forgottenPassword();
            } else {
                Toast.makeText(UserLoginActivity.this, "Email field empty. Please enter email.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // User login functionality
    public void UserLogin() {

        // message content returned if EditText field empty
        String message = checkEditTextFieldEmpty();

        // empty_place false if checkEditTextFieldEmpty finds text in field
        if(!empty_place) {
            // SQLite write to database requires permission
            db = sqliteDBHelper.getWritableDatabase();


            // SQLite query for user email address
            Cursor cursor = db.query(SQLiteUserDBHelper.USERS_TABLE_NAME, null, " " + SQLiteUserDBHelper.COL3_email + "=?", new String[]{email_place}, null, null, null);

            // iterate cursor to retrieve adjacent column values
            while (cursor.moveToNext()) {
                if (cursor.isFirst()) {
                    cursor.moveToFirst();

                    // Storing Password and Name associated with entered email
                    temporary_password_entered = cursor.getString(cursor.getColumnIndex(SQLiteUserDBHelper.COL4_pass));
                    name_place = cursor.getString(cursor.getColumnIndex(SQLiteUserDBHelper.COL1_name));
                    phone_number_place = cursor.getString(cursor.getColumnIndex(SQLiteUserDBHelper.COL2_number));

                    // close cursor
                    cursor.close();
                }
            }
            sqliteDBHelper.close();

            // Calling method to check final result
            checkPasswordMatches();
        } else {
            // Execute toast if EditText empty
            Toast.makeText(UserLoginActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    // Return message for when EditText fields empty
    public String checkEditTextFieldEmpty() {
        // Getting value from fields and storing into string variable
        String textFieldMessage = "";
        email_place = email.getText().toString().trim();
        pass_place = password.getText().toString().trim();

        if (email_place.isEmpty()){
            email.requestFocus();
            empty_place = true;
            textFieldMessage = "Please Enter Email";
        } else if (pass_place.isEmpty()){
            password.requestFocus();
            empty_place = true;
            textFieldMessage = "Please Enter Pass";
        } else {
            empty_place = false;
        }
        return textFieldMessage;
    }

    // Clear fields after successful login
    public void clearEditText() {
        email.getText().clear();
        password.getText().clear();
    }


    // Function to help user retrieve forgotten password with stored phone number
    public void forgottenPassword() {


        // Create a new view using forgot_pass_popup XML layout
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.forgot_pass_popup, activity.findViewById(R.id.popup_element));

        // store phone and password field values
        EditText phone_number = layout.findViewById(R.id.editTextItemDescriptionPopup);
        TextView password = layout.findViewById(R.id.textViewPassword);

        // Create popup in the center
        forgotPopup = new PopupWindow(layout, 800, 800, true);
        forgotPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

        // Opening SQLite database write permission
        db = sqliteDBHelper.getWritableDatabase();

        // Adding search email query to cursor
        Cursor cursor = db.query(SQLiteUserDBHelper.USERS_TABLE_NAME, null, " " + SQLiteUserDBHelper.COL3_email + "=?", new String[]{email_place}, null, null, null);

        while (cursor.moveToNext()) {
            if (cursor.isFirst()) {
                cursor.moveToFirst();

                // Storing Password and Name associated with entered email
                phone_number_place = cursor.getString(cursor.getColumnIndex(SQLiteUserDBHelper.COL2_number));
                temporary_password_entered = cursor.getString(cursor.getColumnIndex(SQLiteUserDBHelper.COL4_pass));

                // Closing cursor.
                cursor.close();
            }
        }
        sqliteDBHelper.close();

        // declare buttons
        Button cancel_button = layout.findViewById(R.id.forgotCancelButton);
        Button get_pass_button = layout.findViewById(R.id.forgotGetButton);

        // dismiss popup on cancel button press
        cancel_button.setOnClickListener(view -> {
            Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show();
            forgotPopup.dismiss();
        });

        // verify phone number on get_pass_button press
        get_pass_button.setOnClickListener(view -> {
            String verifyPhone = phone_number.getText().toString();

            if(verifyPhone.equals(phone_number_place)) {
                password.setText(temporary_password_entered);

                new android.os.Handler().postDelayed(() -> forgotPopup.dismiss(), 1500);
            } else {
                Toast.makeText(activity, "Wrong Phone Number", Toast.LENGTH_LONG).show();
            }
        });

    }

    // check password entered matches associated SQLite db email
    public void checkPasswordMatches(){

        // If password is match
        if(temporary_password_entered.equalsIgnoreCase(pass_place)) {
            // Send successful login toast
            Toast.makeText(UserLoginActivity.this,"Login Success",Toast.LENGTH_SHORT).show();

            // bundle login info for ItemsListActivity intent
            Bundle bundle = new Bundle();
            bundle.putString("user_name", name_place);
            bundle.putString("user_email", email_place);
            bundle.putString("user_phone", phone_number_place);

            // Switch to ItemsListActivity (successful login) and pass bundle to it
            Intent intent = new Intent(UserLoginActivity.this, ItemsListActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

            // Empty editText  after login successful and close database
            clearEditText();
        } else {
            // Incorrect credential message toast
            Toast.makeText(UserLoginActivity.this,"Login Failed!\nIncorrect credentials\nor unregistered user",Toast.LENGTH_LONG).show();
        }
        temporary_password_entered = "NOT_FOUND" ;
    }
}