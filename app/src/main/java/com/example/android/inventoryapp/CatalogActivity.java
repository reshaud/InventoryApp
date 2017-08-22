package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;
import com.example.android.inventoryapp.data.ProductCursorAdapter;
import com.example.android.inventoryapp.data.ProductDbHelper;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURSOR_LOADER_ID = 0;
    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();
    private ProductCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ProductDbHelper mDbHelper = new ProductDbHelper(this);

        //Find ListView to populate
        ListView mListView = (ListView) findViewById(R.id.list_view);

        //Create an empty adapter we will use to display the loaded data
        //We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new ProductCursorAdapter(this, null);

        //Find and set empty view on the ListView, so that it only shows when the list has 0 items
        View emptyView = findViewById(R.id.empty_view);
        mListView.setEmptyView(emptyView);

        //Attach cursor adapter to the ListView
        mListView.setAdapter(mAdapter);

        //Setup onItemClickListener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View itemView, int pos, long id) {
                Uri editUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                Log.v(LOG_TAG, Long.toString(id));

                Intent i = new Intent(CatalogActivity.this, EditorActivity.class);
                i.setData(editUri);
                startActivity(i);
            }
        });

        //Prepare the loader. Either re-connect with an existing one,
        //or start a new one
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        //Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CatalogActivity.this,EditorActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu options from the res/menu/menu_catalog.xml file
        //This adds menu items to the app bar
        getMenuInflater().inflate(R.menu.menu_catalog,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //User clicked ona  menu option in the app bar overflow menu
        switch (item.getItemId()){
            //Respond to various menu options
            case R.id.action_insert_dummy_data:
                insertProducts();
                return true;
            case R.id.action_delete_all_entries:
                deleteProduct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteProduct() {
        int mRowsDeleted;

        mRowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);

        if (mRowsDeleted > 0) {
            Toast.makeText(this, R.string.product_deleted_all, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.error_product_deleted_all, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Define a projection that specifies which columns from the database
        //will be used for this query
        String[] projection = {
                ProductEntry.COLUMN_ID,
                ProductEntry.COLUMN_NAME,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_PRICE
        };

        //Now create and return a CursorLoader that will takecare of
        //Creating a Cursor for the data being displayed
        return new CursorLoader(this, ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    // Called when a previously created loader has finished loading
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Swap the new cursor in.(The framework will take care of closing the
        //old cursor once we return)
        mAdapter.swapCursor(cursor);
    }

    // Called when a previously created loader is reset, making the data unavailable
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //This is called when the last Cursor provided to onLoadFinished()
        //above is about to be closed. We need to make sure we are no
        //longer using it
        mAdapter.swapCursor(null);
    }

    //Insert dummy data for products using content values
    private void insertProducts() {
        //Create a new map of values, where column names are the keys - Dummy Data 1
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_NAME, "Re Zero Shirt");
        contentValues.put(ProductEntry.COLUMN_DESCRIPTION, "Awesome Shirt");
        contentValues.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "test@email.com");
        contentValues.put(ProductEntry.COLUMN_PRICE, 39.99);
        contentValues.put(ProductEntry.COLUMN_SUPPLIER, "arthur shirts");
        contentValues.put(ProductEntry.COLUMN_QUANTITY, 5);
        contentValues.put(ProductEntry.COLUMN_IMAGE,
                "android.resource://com.example.android.inventoryapp/drawable/rezero_shirt");

        //Create a new map of values, where column names are the keys - Dummy Data 2
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(ProductEntry.COLUMN_NAME, "Rick & Morty Shirt");
        contentValues1.put(ProductEntry.COLUMN_DESCRIPTION, "Shirt from Rick and Morty Tv Show");
        contentValues1.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "test@email.com");
        contentValues1.put(ProductEntry.COLUMN_PRICE, 60.45);
        contentValues1.put(ProductEntry.COLUMN_SUPPLIER, "arthur shirts");
        contentValues1.put(ProductEntry.COLUMN_QUANTITY, 1);
        contentValues1.put(ProductEntry.COLUMN_IMAGE,
                "android.resource://com.example.android.inventoryapp/drawable/rick_shirt");

        //Create a new map of values, where column names are the keys - Dummy Data 3
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(ProductEntry.COLUMN_NAME, "Stein's Gate Shirt");
        contentValues2.put(ProductEntry.COLUMN_DESCRIPTION, "Shirt from the anime Stein's Gate");
        contentValues2.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "test@email.com");
        contentValues2.put(ProductEntry.COLUMN_PRICE, 45.01);
        contentValues2.put(ProductEntry.COLUMN_SUPPLIER, "arthur shirts");
        contentValues2.put(ProductEntry.COLUMN_QUANTITY, 0);
        contentValues2.put(ProductEntry.COLUMN_IMAGE,
                "android.resource://com.example.android.inventoryapp/drawable/stein_shirt");

        ContentValues[] values = new ContentValues[3];
        values[0] = contentValues;
        values[1] = contentValues1;
        values[2] = contentValues2;

        int mNewRows = getContentResolver().bulkInsert(ProductEntry.CONTENT_URI, values);

        Log.v(LOG_TAG, "Products inserted: " + mNewRows);
    }
}
