package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Reshaud Ally on 8/2/2017.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";
    private static final String COMMA_SEP = ",";
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_REAL = " REAL";
    private static final String TYPE_INT = " INTEGER";

    //SQL_CREATE_ENTRIES contains SQLite statement to create "products" table
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ProductEntry.TABLE_NAME +
            "(" + ProductEntry.COLUMN_ID + TYPE_INT + " PRIMARY KEY AUTOINCREMENT " + COMMA_SEP +
            ProductEntry.COLUMN_IMAGE + TYPE_TEXT + " NOT NULL" + COMMA_SEP +
            ProductEntry.COLUMN_NAME + TYPE_TEXT + " NOT NULL" + COMMA_SEP +
            ProductEntry.COLUMN_DESCRIPTION + TYPE_TEXT + COMMA_SEP +
            ProductEntry.COLUMN_PRICE + TYPE_REAL + " NOT NULL" + COMMA_SEP +
            ProductEntry.COLUMN_SUPPLIER + TYPE_TEXT + " NOT NULL" + COMMA_SEP +
            ProductEntry.COLUMN_SUPPLIER_EMAIL + TYPE_TEXT + " NOT NULL" + COMMA_SEP +
            ProductEntry.COLUMN_QUANTITY + TYPE_INT + " NOT NULL DEFAULT 0" + ");";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create pets table using sql statement
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Upgrade policy on this database is to discard existing data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
