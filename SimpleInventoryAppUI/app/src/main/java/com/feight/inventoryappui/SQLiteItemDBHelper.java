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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// SQL helper class for ItemEntry objects
public class SQLiteItemDBHelper extends SQLiteOpenHelper {


    private static final int DB_version = 1;
    private static final String DB_name = "ItemsData.DB";
    private static final String ITEM_TABLE_NAME = "ItemsTable";

    private static final String COLUMN_0_ID = "id";
    private static final String COLUMN_1_USER_EMAIL = "email";
    private static final String COLUMN_2_DESCRIPTION = "description";
    private static final String COLUMN_3_QUANTITY = "quantity";
    private static final String COLUMN_4_UNIT = "unit";

    private static final String CREATE_ITEMS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            ITEM_TABLE_NAME + " (" +
            COLUMN_0_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COLUMN_1_USER_EMAIL + " VARCHAR, " +
            COLUMN_2_DESCRIPTION + " VARCHAR, " +
            COLUMN_3_QUANTITY + " VARCHAR, " +
            COLUMN_4_UNIT + " VARCHAR" + ");";

    public SQLiteItemDBHelper(Context context) {
        super(context, DB_name, null, DB_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE_NAME);
        onCreate(db);
    }


    // Iterate through readable DB and return count of items
    public int getItemsCount() {
        String countQuery = "SELECT * FROM " + ITEM_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int totalItems = cursor.getCount();
        cursor.close();

        return totalItems;
    }


    // ADD ItemEntry
    public void createItem(ItemEntry item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_1_USER_EMAIL, item.getUserEmail());
        values.put(COLUMN_2_DESCRIPTION, item.getDescription());
        values.put(COLUMN_3_QUANTITY, item.getQuantity());

        db.insert(ITEM_TABLE_NAME, null, values);
        db.close();
    }

    // READ ItemEntry
    public ItemEntry readItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ITEM_TABLE_NAME,
                new String[] { COLUMN_0_ID, COLUMN_1_USER_EMAIL, COLUMN_2_DESCRIPTION, COLUMN_3_QUANTITY, COLUMN_4_UNIT }, COLUMN_0_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        ItemEntry item = new ItemEntry(Integer.parseInt(Objects.requireNonNull(cursor).getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));

        cursor.close();
        return item;
    }

    // UPDATE ItemEntry
    public int updateItem(ItemEntry item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_1_USER_EMAIL, item.getUserEmail());
        values.put(COLUMN_2_DESCRIPTION, item.getDescription());
        values.put(COLUMN_3_QUANTITY, item.getQuantity());

        return db.update(ITEM_TABLE_NAME, values, COLUMN_0_ID + " = ?", new String[] { String.valueOf(item.getId()) });
    }

    // DELETE ItemEntry
    public void deleteItem(ItemEntry item) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(ITEM_TABLE_NAME, COLUMN_0_ID + " = ?", new String[] { String.valueOf(item.getId()) });
        db.close();
    }


    // Return a List<ItemEntry> of all items for displaying
    public List<ItemEntry> getAllItems() {
        List<ItemEntry> itemList = new ArrayList<>();

        // Select everything in SQLite db
        String selectQuery = "SELECT * FROM " + ITEM_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // iterate through every row, adding items
        if (cursor.moveToFirst()) {
            do {
                ItemEntry item = new ItemEntry();
                item.setId(Integer.parseInt(cursor.getString(0)));
                item.setUserEmail(cursor.getString(1));
                item.setDescription(cursor.getString(2));
                item.setQuantity(cursor.getString(3));

                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return itemList;
    }


}
