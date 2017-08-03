package com.example.android.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Reshaud Ally on 8/2/2017.
 * Contract class containing tables used in Inventory Database
 */

public final class ProductContract {

    //The "content authority" is a name for the entire content provider
    private static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    //Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    //the content provider
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_PRODUCTS = "products";

    //Prevent someone from accidentally instantiating the contract class
    private ProductContract(){};

    //Inner class that defines constant values for the products table
    public static abstract class ProductEntry implements BaseColumns{
        //The content URI to access the products data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PRODUCTS);

        //Constants
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_SUPPLIER = "supplier";
        public static final String COLUMN_IMAGE = "image";
    }
}
