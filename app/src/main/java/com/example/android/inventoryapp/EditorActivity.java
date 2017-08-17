package com.example.android.inventoryapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PICK_PHOTO_FOR_PRODUCT = 0;
    private static final int CURSOR_LOADER_ID = 0;
    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();
    private EditText mProductName;
    private EditText mDescription;
    private EditText mOrderQuantity;
    private ImageView mProductImage;
    private TextView mQuantityAvailable;
    private ImageButton mAddQuantity;
    private ImageButton mRemoveQuantity;
    private EditText mSupplier;
    private EditText mSupplierEmail;
    private EditText mPrice;
    //editUri determines if EditorActivity will be add mode or edit mode
    private Uri editUri = null;

    //Contains Uri for image selected by user
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //get associated URI
        editUri = getIntent().getData();

        //Set title of EditorActivity on which situation we have
        //If the EditoryActivity was opened using the ListView, then we will
        //have a uri for a product. Therefore change title to "Edit Product"
        //Otherwise change title to say "Add Product"
        if (editUri == null) {
            setTitle(R.string.add_mode);

            //Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_mode);

            //Initialize Loader
            getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        }

        //Find all user input views
        mProductImage = (ImageView) findViewById(R.id.product_image);
        mProductName = (EditText) findViewById(R.id.product_name_edit_text);
        mQuantityAvailable = (TextView) findViewById(R.id.available);
        mRemoveQuantity = (ImageButton) findViewById(R.id.remove);
        mAddQuantity = (ImageButton) findViewById(R.id.add);
        mSupplier = (EditText) findViewById(R.id.supplier);
        mPrice = (EditText) findViewById(R.id.price_edit_text);
        mProductImage = (ImageView) findViewById(R.id.product_image);
        mOrderQuantity = (EditText) findViewById(R.id.quantity_edit_text);
        mDescription = (EditText) findViewById(R.id.description_edit_text);
        mSupplierEmail = (EditText) findViewById(R.id.supplier_email);

        //On click listener for mProductImage ImageView
        mProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //On user click select image from gallery
                pickImage();
            }
        });

        //On click listener for mRemoveQuantity Button
        mRemoveQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get integer from mOrderQuantity EditText
                int quantityOrdered = Integer.parseInt(mOrderQuantity.getText().toString());

                //Check if value is 0
                if (quantityOrdered == 0) {
                    //Do not subtract.Display error message to user
                    Toast.makeText(EditorActivity.this, "Order Quantity cannot be less than 0", Toast.LENGTH_SHORT).show();
                } else {
                    //Subtract 1
                    quantityOrdered = quantityOrdered - 1;
                    //set new value
                    mOrderQuantity.setText(Integer.toString(quantityOrdered));
                }
            }
        });

        //On click listener for mAddQuantity Button
        mAddQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get integer from mOrderQuantity EditText
                int quantityOrdered = Integer.parseInt(mOrderQuantity.getText().toString());

                //Add 1 to the quantity ordered
                quantityOrdered = quantityOrdered + 1;

                //Set new value
                mOrderQuantity.setText(Integer.toString(quantityOrdered));
            }
        });

        mPrice.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals(current)) {
                    mPrice.removeTextChangedListener(this);

                    String replaceable = String.format("[%s,.\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                    String cleanString = editable.toString().replaceAll(replaceable, "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }
                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    formatter.setMaximumFractionDigits(2);
                    String formatted = formatter.format(parsed / 100);

                    current = formatted;
                    mPrice.setText(formatted);
                    mPrice.setSelection(formatted.length());
                    mPrice.addTextChangedListener(this);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu options from the res/menu/menu_editor.xml file
        //This adds menu items to the app bar
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //User clicked on a  menu option in the app bar overflow menu
        switch (item.getItemId()){
            //Respond to various menu options
            case R.id.action_save:
                saveProduct();
                return true;
            case R.id.action_delete:
                deleteProduct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteProduct() {
        int mRowsDeleted;

        //Delete single row
        mRowsDeleted = getContentResolver().delete(editUri, null, null);

        //Remove bitmap from imageview
        mProductImage.setImageBitmap(null);

        if (mRowsDeleted > 0) {
            Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error deleting Product", Toast.LENGTH_SHORT).show();
        }

        //Reset the Loader to stop cursor from trying to refresh list
        //Since only one item will be in the cursor
        getLoaderManager().destroyLoader(CURSOR_LOADER_ID);

        //Exit Activity
        finish();
    }

    //Get user input from editor and save product into the database
    private void saveProduct() {
        Uri mNewUri;
        String productImage;
        String productName;
        String supplierName;
        String supplierEmail;
        double productPrice;
        int orderQuantity = 0;
        Locale mLocale = Locale.getDefault();
        Currency mCurrency = Currency.getInstance(mLocale);

        //If no image was selected prompt user to select one
        if (imageUri != null) {
            productImage = imageUri.toString();
        } else {
            Toast.makeText(this, "Product Image required", Toast.LENGTH_SHORT).show();
            return;
        }

        //If no product name was entered prompt user to enter one
        if (TextUtils.isEmpty(mProductName.getText())) {
            Toast.makeText(this, "Product Name required", Toast.LENGTH_SHORT).show();
            return;
        } else {
            productName = mProductName.getText().toString().trim();
        }

        //If no order quantity was entered prompt user to enter one
        if (TextUtils.isEmpty(mOrderQuantity.getText())) {
            Toast.makeText(this, "Ordered Quantity required", Toast.LENGTH_SHORT).show();
        } else {
            orderQuantity = Integer.parseInt(mOrderQuantity.getText().toString().trim());
        }

        //If no product price was entered prompt user to enter one
        if (TextUtils.isEmpty(mPrice.getText())) {
            Toast.makeText(this, "Product Price required", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String price = mPrice.getText().toString().trim();
            //Remove currency symbol and comma before saving to database
            price = price.replace(mCurrency.getSymbol(), "");
            price = price.replace(",", "");
            productPrice = Double.parseDouble(price);
        }

        //If no supplier was entered prompt user to enter one
        if (TextUtils.isEmpty(mSupplier.getText())) {
            Toast.makeText(this, "Supplier Name required", Toast.LENGTH_SHORT).show();
            return;
        } else {
            supplierName = mSupplier.getText().toString().trim();
        }

        //If not supplier email was entered prompt user to enter one
        if (TextUtils.isEmpty(mSupplierEmail.getText())) {
            Toast.makeText(this, "Supplier Email required", Toast.LENGTH_SHORT).show();
            return;
        } else {
            supplierEmail = mSupplierEmail.getText().toString().trim();
        }

        String productDescription = mDescription.getText().toString().trim();

        //Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_NAME, productName);
        values.put(ProductEntry.COLUMN_IMAGE, productImage);
        values.put(ProductEntry.COLUMN_QUANTITY, orderQuantity);
        values.put(ProductEntry.COLUMN_SUPPLIER, supplierName);
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);
        values.put(ProductEntry.COLUMN_PRICE, productPrice);
        values.put(ProductEntry.COLUMN_DESCRIPTION, productDescription);

        if (editUri == null) {
            //add mode
            mNewUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (mNewUri == null) {
                //Error inserting data
                Toast.makeText(this, R.string.insert_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.insert_successful, Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            //edit mode
        }
    }

    public void pickImage(){

        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(i, PICK_PHOTO_FOR_PRODUCT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_PHOTO_FOR_PRODUCT && resultCode == Activity.RESULT_OK){
            if(data == null){
                //Error picking image
                Toast.makeText(this,"Error picking product image",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                imageUri = data.getData();

                //Get bitmap from inputstream
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                //Set bitmap on imageview to the bitmap selected by user
                mProductImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (editUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Define a projection that specifies which columns from the database
        //will be used for this query
        String[] projection = {
                ProductEntry.COLUMN_ID,
                ProductEntry.COLUMN_IMAGE,
                ProductEntry.COLUMN_NAME,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER,
                ProductEntry.COLUMN_DESCRIPTION,
                ProductEntry.COLUMN_SUPPLIER_EMAIL,
                ProductEntry.COLUMN_PRICE
        };

        //Now create and return a CursorLoader that will takecare of
        //Creating a Cursor for the data being displayed
        return new CursorLoader(this, editUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Move to the first position in the cursor
        cursor.moveToFirst();

        //Update fields with current data
        mProductName.setText(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_NAME)));
        mDescription.setText(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_DESCRIPTION)));
        mQuantityAvailable.setText(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY)));
        mPrice.setText(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRICE)));
        mSupplier.setText(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER)));
        mSupplierEmail.setText(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_EMAIL)));

        //Get Image Uri
        Uri imageUri = Uri.parse(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_IMAGE)));
        Log.v(LOG_TAG, imageUri.toString());

        try {
            InputStream inputStream = EditorActivity.this.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            mProductImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need clear all fields
        mProductName.setText("");
        mDescription.setText("");
        mQuantityAvailable.setText("");
        mPrice.setText("");
        mSupplier.setText("");
        mSupplierEmail.setText("");
    }
}
