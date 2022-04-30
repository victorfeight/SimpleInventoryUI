package com.feight.inventoryappui;

/*
 *    Victor Feight (victor.feight@snhu.edu)
 *    SNHU Project 3
 *    CS-360 - Mobile Architect & Programming
 *    12/10/21
 *
 */
import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

// itemsScrollView will show this custom layout of ItemRowList
public class ItemRowList extends BaseAdapter {

    private final Activity context;
    private PopupWindow popwindow;
    ArrayList<ItemEntry> items;
    SQLiteItemDBHelper db;

	public ItemRowList(Activity context, ArrayList<ItemEntry> items, SQLiteItemDBHelper db) {
        this.context = context;
        this.items = items;
        this.db = db;
    }

    public static class ViewHolder {
        TextView textViewItemId;
		TextView textViewUserEmail;
        TextView textViewItemDescription;
        TextView textViewItemQuantity;
        ImageButton edit_button;
        ImageButton delete_button;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        ViewHolder text_view_holder;

		if (convertView == null) {
			text_view_holder = new ViewHolder();
			row = inflater.inflate(R.layout.item_row_template, null, true);


			text_view_holder.textViewUserEmail = row.findViewById(R.id.textViewUserEmail);
			text_view_holder.textViewItemDescription = row.findViewById(R.id.textViewItemDesc);
			text_view_holder.textViewItemQuantity = row.findViewById(R.id.textViewItemQty);
			text_view_holder.edit_button = row.findViewById(R.id.editButton);
			text_view_holder.textViewItemId = row.findViewById(R.id.textViewItemId);
			text_view_holder.delete_button = row.findViewById(R.id.deleteButton);

			row.setTag(text_view_holder);
		} else {
			text_view_holder = (ViewHolder) convertView.getTag();
		}

		text_view_holder.textViewItemId.setText("" + items.get(position).getId());
		text_view_holder.textViewUserEmail.setText(items.get(position).getUserEmail());
		text_view_holder.textViewItemDescription.setText(items.get(position).getDescription());
		text_view_holder.textViewItemQuantity.setText(items.get(position).getQuantity());


		// Check is the cell value is zero to change color and send SMS
		String value = text_view_holder.textViewItemQuantity.getText().toString().trim();
		if (value.equals("0")) {
			// bg color and text of item quantity cell when 0
			text_view_holder.textViewItemQuantity.setBackgroundColor(Color.RED);
			text_view_holder.textViewItemQuantity.setTextColor(Color.WHITE);
			ItemsListActivity.SendSMSMessage(context.getApplicationContext());
		} else {
			// item quantity cell bg color and text color
			text_view_holder.textViewItemQuantity.setBackgroundColor(Color.parseColor("#E6E6E6"));
			text_view_holder.textViewItemQuantity.setTextColor(Color.BLACK);
		}

		final int positionPopup = position;

		text_view_holder.edit_button.setOnClickListener(view -> editPopup(positionPopup));

		text_view_holder.delete_button.setOnClickListener(view -> {
			Integer index = (Integer) view.getTag();
			db.deleteItem(items.get(positionPopup));

			items.remove(index.intValue());
			items = (ArrayList<ItemEntry>) db.getAllItems();
			notifyDataSetChanged();

			Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();

			int itemsCount = db.getItemsCount();
			TextView TotalItems = context.findViewById(R.id.textViewTotalItemsCount);
			TotalItems.setText(String.valueOf(itemsCount));
		});

        return  row;
    }

    public Object getItem(int position) {
        return position;
    }

	public long getItemId(int position) {
		return position;
	}

	public int getCount() {
        return items.size();
    }

	public void editPopup(final int positionPopup) {
		LayoutInflater inflater = context.getLayoutInflater();
		View layout = inflater.inflate(R.layout.edit_item_popup, context.findViewById(R.id.popup_element));

		popwindow = new PopupWindow(layout, 800, 1000, true);
		popwindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

		final EditText editItemDesc = layout.findViewById(R.id.editTextItemDescriptionPopup);
		final EditText editItemQty = layout.findViewById(R.id.editTextItemQtyPopup);

		editItemDesc.setText(items.get(positionPopup).getDescription());
		editItemQty.setText(items.get(positionPopup).getQuantity());

		Button save = layout.findViewById(R.id.editSaveButton);
		Button cancel = layout.findViewById(R.id.editCancelButton);

		cancel.setOnClickListener(view -> {
			Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
			popwindow.dismiss();
		});

		save.setOnClickListener(view -> {
			String item_description = editItemDesc.getText().toString();
			String item_quantity = editItemQty.getText().toString();


			ItemEntry item = items.get(positionPopup);
			item.setDescription(item_description);
			item.setQuantity(item_quantity);

			db.updateItem(item);
			items = (ArrayList<ItemEntry>) db.getAllItems();
			notifyDataSetChanged();

			Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();

			popwindow.dismiss();
		});

	}

}
