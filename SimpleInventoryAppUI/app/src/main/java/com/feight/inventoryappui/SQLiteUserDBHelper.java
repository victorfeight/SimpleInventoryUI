package com.feight.inventoryappui;

/*
 *    Victor Feight (victor.feight@snhu.edu)
 *    SNHU Project 3
 *    CS-360 - Mobile Architect & Programming
 *    12/10/21
 *
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteUserDBHelper extends SQLiteOpenHelper {

	// DB name, table name, DB version
	private static final String DB_name = "UsersData.DB";
    public static final String USERS_TABLE_NAME = "UsersTable";
	private static final int DB_version = 1;

    // Declare column names as static variables
    public static final String COL0_id = "id";
    public static final String COL1_name = "name";
    public static final String COL2_number = "phone_number";
    public static final String COL3_email = "email";
    public static final String COL4_pass = "password";

    // SQL query to create Users table with autoincrementing id
	private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " +
			USERS_TABLE_NAME + " (" +
			COL0_id + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
			COL1_name + " VARCHAR, " +
			COL2_number + " VARCHAR, " +
			COL3_email + " VARCHAR, " +
			COL4_pass + " VARCHAR" + ");";

	// pass DB name and version to super class constructor
	public SQLiteUserDBHelper(Context context) {
        super(context, DB_name, null, DB_version);
    }

    // Execute SQL command for creating a table
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_USERS_TABLE);
    }

    // Update table (Note: Not suitable for production as this will wipe the table every time!)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
        onCreate(db);
    }

	// Add user to db
	public void createUser(UserEntry user) {
		SQLiteDatabase db = this.getWritableDatabase();

		// place appropriate user info into columns
		ContentValues values = new ContentValues();
		values.put(COL1_name, user.getUserName());
		values.put(COL2_number, user.getUserPhone());
		values.put(COL3_email, user.getUserEmail());
		values.put(COL4_pass, user.getUserPass());

		db.insert(USERS_TABLE_NAME, null, values);
		db.close();
	}


}
