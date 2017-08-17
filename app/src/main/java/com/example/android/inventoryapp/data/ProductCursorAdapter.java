package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

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
        TextView productName = (TextView) view.findViewById(R.id.list_product);
        TextView currentQuantity = (TextView) view.findViewById(R.id.current_quantity);
        TextView price = (TextView) view.findViewById(R.id.price);

        //Extract data from cursor
        String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_NAME));
        double productPrice = (cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRICE))) / 100;
        String quantity = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY));

        String newPriceString = view.getResources().getString(R.string.product_price, productPrice);

        //If the default currency symbol is not $ replace it with the correct currency symbol
        Locale mLocale = Locale.getDefault();
        Currency mCurrency = Currency.getInstance(mLocale);
        newPriceString = newPriceString.replace("$", mCurrency.getSymbol());

        //Populate TextViews
        productName.setText(name);
        currentQuantity.setText(view.getResources().getString(R.string.current_quantity, quantity));
        price.setText(newPriceString);
    }
}
