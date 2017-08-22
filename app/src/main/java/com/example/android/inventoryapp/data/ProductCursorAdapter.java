package com.example.android.inventoryapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by Reshaud Ally on 8/8/2017.
 */

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Infate item layout
        return LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //find fields to populate inflated layout
        final TextView productName = (TextView) view.findViewById(R.id.list_product);
        TextView currentQuantity = (TextView) view.findViewById(R.id.current_quantity);
        TextView price = (TextView) view.findViewById(R.id.price);
        Button saleButton = (Button) view.findViewById(R.id.saleButton);

        //Extract data from cursor
        final String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_NAME));
        double productPrice = (cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRICE)));
        final String quantity = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY));

        String newPriceString = view.getResources().getString(R.string.product_price, productPrice);

        //If the default currency symbol is not $ replace it with the correct currency symbol
        Locale mLocale = Locale.getDefault();
        Currency mCurrency = Currency.getInstance(mLocale);
        newPriceString = newPriceString.replace("$", mCurrency.getSymbol());

        //Populate TextViews
        productName.setText(name);
        currentQuantity.setText(view.getResources().getString(R.string.current_quantity, quantity));
        price.setText(newPriceString);


        //When sales button is clicked decrease current quantity of product by 1
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View parentRow = (View) view.getParent();
                ListView listView = (ListView) parentRow.getParent();

                //Get position in the listview
                final int pos = listView.getPositionForView(parentRow);

                //get id for row
                final long id = listView.getItemIdAtPosition(pos);

                //uri with the item to update
                Uri mUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                //Get current quantity
                int currentQuantity = Integer.parseInt(quantity);

                //Check if quantity is 0
                if (currentQuantity == 0) {
                    //Cannot sell any more of this product
                    Toast.makeText(view.getContext(), R.string.out_of_stock, Toast.LENGTH_SHORT).show();
                } else {
                    //sell product
                    //Decrease quantity by 1
                    currentQuantity = currentQuantity - 1;

                    //Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_QUANTITY, currentQuantity);

                    //Updating existing info in the database
                    int rowsUpdated = view.getContext().getContentResolver().update(mUri, values, null, null);

                    if (rowsUpdated != 0) {
                        Toast.makeText(view.getContext(), name + " sold", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(view.getContext(), view.getResources().getString(R.string.error_product_sale, name)
                                , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
