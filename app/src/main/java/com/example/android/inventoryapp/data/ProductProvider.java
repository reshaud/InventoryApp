package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Reshaud Ally on 8/8/2017.
 */

public class ProductProvider extends ContentProvider {
    //Tag for log messages
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer. This is run the first time anything is called from this class
    static {
        //The calls to addURI() go here, for all of the content URI patterns that the provider
        //should recognize. All paths added to the UriMatcher have a corresponding code to return
        //when a match is found.

        //This URI is used to provide access to multiple rows in the product table
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCT);

        //This URI is used to provide access to a single row in the product table
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    //To access our database, we instantiate our subclass of SQLiteOpenHelper
    //and pass the context, which is the current activity
    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        //Initialize the SQLiteOpenHelper
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    //Perform the query for the given URI. Use the given projection, selection, selection arguments
    //,and sort order
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //Get readable version of database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        //Determine the type of query and get data
        switch (match) {
            case PRODUCT:
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, null, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //set notification URI on the Cursor
        //so we know what content URI the Cursor was created for
        //If the data at this URI changes, then we know we need to update the Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        //Return the Cursor
        return cursor;
    }

    //Returns the MIME type of data for the content URI
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI : " + uri + " with match " + match);
        }
    }


    //Insert new data into the provider with the given ContentValues
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        String productName = contentValues.getAsString(ProductEntry.COLUMN_NAME);
        String productImage = contentValues.getAsString(ProductEntry.COLUMN_IMAGE);
        double productPrice = contentValues.getAsDouble(ProductEntry.COLUMN_PRICE);
        String supplierName = contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER);
        String supplierEmail = contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER_EMAIL);

        //Check that a product name has been entered
        if (productName == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        //Check that a product image has been selected
        if (productImage == null) {
            throw new IllegalArgumentException("Product requires a image");
        }

        //Check that a product price has been entered
        if (productPrice <= 0.00) {
            throw new IllegalArgumentException("Product requires a price");
        }

        //Check that a supplier name has been entered
        if (supplierName == null) {
            throw new IllegalArgumentException("Supplier name required");
        }

        //Check that a supplier email has been entered
        if (supplierEmail == null) {
            throw new IllegalArgumentException("Supplier email required");
        }

        //Get writable version of database to insert new row
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, contentValues);

        //If the ID is -1 then the insertion failed. Log error and return null.
        if (newRowId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners that the data has changed for the product content URI
        getContext().getContentResolver().notifyChange(uri, null);

        //Once we know the  ID of the new row in the table
        //return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return insertBulkProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private int insertBulkProduct(Uri uri, ContentValues[] values) {
        int numInserted = 0;

        //Keep inserting until there are no more ContentValues in the array
        //Used for inserting the dummy data
        for (int i = 0; i < values.length; i++) {
            String productName = values[i].getAsString(ProductEntry.COLUMN_NAME);
            String productImage = values[i].getAsString(ProductEntry.COLUMN_IMAGE);
            double productPrice = values[i].getAsDouble(ProductEntry.COLUMN_PRICE);
            String supplierName = values[i].getAsString(ProductEntry.COLUMN_SUPPLIER);
            String supplierEmail = values[i].getAsString(ProductEntry.COLUMN_SUPPLIER_EMAIL);

            //Check that a product name has been entered
            if (productName == null) {
                throw new IllegalArgumentException("Product requires a name");
            }

            //Check that a product image has been selected
            if (productImage == null) {
                throw new IllegalArgumentException("Product requires a image");
            }

            //Check that a product price has been entered
            if (productPrice <= 0.00) {
                throw new IllegalArgumentException("Product requires a price");
            }

            //Check that a supplier name has been entered
            if (supplierName == null) {
                throw new IllegalArgumentException("Supplier name required");
            }

            //Check that a supplier email has been entered
            if (supplierEmail == null) {
                throw new IllegalArgumentException("Supplier email required");
            }

            //Get writable version of database to insert new row
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values[i]);

            if (newRowId < 0) {
                Log.e(LOG_TAG, "Failed to insert row for " + uri);
            } else {
                //Row inserted successfully
                numInserted++;
            }
        }

        //Notify all listeners that the data has changed for the product content URI
        getContext().getContentResolver().notifyChange(uri, null);

        return numInserted;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                //Delete all rows that match selection and selection Args
                return deleteProduct(uri, selection, selectionArgs);
            case PRODUCT_ID:
                //Delect a single Product
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteProduct(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    private int deleteProduct(Uri uri, String selection, String[] selectionArgs) {
        //TODO work on this
        return 1;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
