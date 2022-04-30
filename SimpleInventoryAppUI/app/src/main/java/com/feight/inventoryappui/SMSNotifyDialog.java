package com.feight.inventoryappui;

/*
 *    Victor Feight (victor.feight@snhu.edu)
 *    SNHU Project 3
 *    CS-360 - Mobile Architect & Programming
 *    12/10/21
 *
 */

import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

// Request User permission to send SMS notification on empty inventory
public class SMSNotifyDialog {

    public static AlertDialog buttonToggle(final ItemsListActivity context){
        // Builder helps with building dialogs
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set title, icon, message, positive button, negative button
        builder.setTitle(R.string.sms_permission_text)
                .setCancelable(false)
                .setIcon(R.drawable.sms_notification)
                .setMessage(R.string.sms_enable_msg)
                .setNegativeButton(R.string.disable_sms_button, (dialog, arg1) -> {
                    Toast.makeText(context, "SMS Alerts: Disabled", Toast.LENGTH_LONG).show();
                    ItemsListActivity.DenySendSMS();
                    dialog.cancel();
                })
                .setPositiveButton(R.string.enable_sms_button, (dialog, arg1) -> {
                    Toast.makeText(context, "SMS Alerts: Enabled", Toast.LENGTH_LONG).show();
                    ItemsListActivity.AllowSendSMS();
                    dialog.cancel();
                });

        // Return created dialog
        return builder.create();
    }
}