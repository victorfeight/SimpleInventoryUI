package com.feight.inventoryappui;

/*
 *    Victor Feight (victor.feight@snhu.edu)
 *    SNHU Project 3
 *    CS-360 - Mobile Architect & Programming
 *    12/10/21
 *
 */


public class ItemEntry {

    int id;
	String user_email;
    String description;
    String quantity;

    public ItemEntry() {
        super();
    }

    public ItemEntry(int i, String email, String description, String quantity) {
        super();
        this.id = i;
		this.user_email = email;
        this.description = description;
        this.quantity = quantity;
    }

    // constructor
    public ItemEntry(String email, String description, String quantity) {
		this.user_email = email;
        this.description = description;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	public String getUserEmail() {
        return user_email;
    }

    public void setUserEmail(String user_email) {
        this.user_email = user_email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String qty) {
        this.quantity = qty;
    }
}
