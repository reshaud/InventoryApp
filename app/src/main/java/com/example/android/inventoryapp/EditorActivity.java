package com.example.android.inventoryapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditorActivity extends AppCompatActivity {

    private EditText mProductName;
    private EditText mDescription;
    private EditText mOrderQuantity;
    private ImageView mProductImage;
    private TextView mQuantityAvailable;
    private AutoCompleteTextView mSupplier;
    private EditText mPrice;
    private static final int PICK_PHOTO_FOR_PRODUCT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Find all user input views
        mProductImage = (ImageView) findViewById(R.id.product_image);

        mProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
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
                //TODO add code
                return true;
            case R.id.action_delete:
                //TODO add code
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void pickImage(){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i,PICK_PHOTO_FOR_PRODUCT);
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

                //Get bitmap from inputstream
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                //Set bitmap on imageview to the bitmap selected by user
                mProductImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
