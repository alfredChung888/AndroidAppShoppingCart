package com.example.trial_assignment1_ma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trial_assignment1_ma.databinding.ActivityAddgrocerypageBinding;
import com.example.trial_assignment1_ma.databinding.ActivityEditgrocerylistBinding;
import com.example.trial_assignment1_ma.databinding.ContentDisplayBinding;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class EditGroceryList extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityEditgrocerylistBinding binding;
    private DatabaseManager mydManger;
    private TextView displayMessage, productRecord;
    CustomAdapter listAdapter;
    public List<String> listDataHeader;
    private String[] item = new String[3];
    private DatabaseManager.SQLHelper helper;


    Button editSave_button, editCancel_button,editDelete_button,editImage_button;

    Bitmap bmp;
    byte[] byteArray;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editgrocerylist);


        mydManger=new DatabaseManager(this);
        Intent intent = getIntent();

        //check if there is an image passed associated to the item

        if (intent.getExtras().getByteArray("image") != null || String.valueOf(intent.getExtras().getByteArray("image")).matches("0")) {
            //if there is, grab the image and convert it into bitmap
            byteArray = getIntent().getByteArrayExtra("image");
            bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            compressBitmap();
        }

        //retrieve the Extra string array to set values
        item=intent.getExtras().getStringArray("queryValues");


        //find the EditText fields to manipulate
        EditText editId= (EditText) findViewById(R.id.edit_id);
        EditText editName= (EditText) findViewById(R.id.edit_name);
        EditText editQuantity= (EditText) findViewById(R.id.edit_quantity);

        //set the field to values that were passed through intent
        editId.setText(item[0]);
        editName.setText(item[1]);
        editQuantity.setText(item[2]);

        //find the buttons to set onclick listeners
        editSave_button=(Button) findViewById(R.id.editSave_button);
        editCancel_button=(Button) findViewById(R.id.cancelEditButton);
        editDelete_button=(Button) findViewById(R.id.deleteButton);
        editImage_button=(Button) findViewById(R.id.EditImage);



        //Set onclickListener for editSave button
        editSave_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String errorFields, emptyFields;

                //get NEW values of the
                EditText newEditId = (EditText) findViewById(R.id.edit_id);
                EditText newEditName = (EditText) findViewById(R.id.edit_name);
                EditText newEditQuantity = (EditText) findViewById(R.id.edit_quantity);

                //pass the values changed or not to string array to update and return back to DisplayPage
                String[] newValues = new String[3];
                newValues[0] = newEditId.getText().toString();
                newValues[1] = newEditName.getText().toString();
                newValues[2] = newEditQuantity.getText().toString();

                emptyFields = checkEmptyStrings(newEditId, newEditName, newEditQuantity);
                if (emptyFields.matches("")) {

                    errorFields = validateInputs(newValues[0], newValues[2]);
                    if (errorFields.matches("")) {
                        runUpdate(newValues);
                        goToDisplayPage();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Following fields have an error:\n" + errorFields, Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "Following fields are empty:\n" + emptyFields, Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Set onclickListener for editDelete button
        editDelete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete the item
                mydManger.deleteRow(item);
                //go back to display page
                goToDisplayPage();
                //toast to confirm to user of action
                Toast.makeText(getApplicationContext(),
                        "Item Deleted", Toast.LENGTH_SHORT).show();

            }
        });

        //Set onclickListener for editCancel button
        editCancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            //return back to GroceryPage
            public void onClick(View view) {
                returnToGroceryPage();
            }
        });

        //Set onclickListener for editImage button
        editImage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //run function to pass values to prepare on return as well and move to next activity
                //to change the image
                goToAddImagePage();

            }
        });

    }

    /*
    checkEmptyStrings(EditText id, EditText name, EditText quantity)

    -Check if any fields are empty, if they are: add them to the string
    -returns String of missing fields to inform user
*/
    public String checkEmptyStrings(EditText id, EditText name, EditText quantity){
        String emptyFields="";
        if(id.getText().toString().matches("") ){
            emptyFields=emptyFields+" id ";
        }

        if(name.getText().toString().matches("")){
            emptyFields=emptyFields+" name ";
        }

        if(quantity.getText().toString().matches("")){
            emptyFields=emptyFields+" quantity ";
        }
        return emptyFields;

    }


    /*
         compressBitmap()
         - creates a scaled bitmap
         - compresses the selected bitmap to a size that will fit the layout for an item
         - return the compressed bitmap as a byte array to variable to be returned.

    */
    private void compressBitmap() {
        Bitmap mutableBitmapImage = Bitmap.createScaledBitmap(bmp,120, 120, false);
        mutableBitmapImage.compress(Bitmap.CompressFormat.PNG, 0 , outputStream);
        byteArray=outputStream.toByteArray();
    }

    /*
            onCreateOptionsMenu(Menu menu)
            -inflates the menu
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
           runUpdate(String[] newValues)
           -runs database function updateRow()
           -confirms action with a toast
   */
    private void runUpdate(String[] newValues){
        mydManger=new DatabaseManager(this);
        mydManger.updateRow(item,newValues,byteArray);
        Toast.makeText(getApplicationContext(),
                "Item updated", Toast.LENGTH_SHORT).show();

    }

    /*
         returnToGroceryPage()
         -creates an intent to return to GroceryPage
    */
    private void returnToGroceryPage() {
        Intent intent = new Intent(EditGroceryList.this, GroceryPage.class);
        startActivity(intent);
    }

     /*
         goToAddImagePage()
         -gets values from EditText fields, to be saved and re-entered later on
    */

    private void goToAddImagePage(){
        EditText newEditId= (EditText) findViewById(R.id.edit_id);
        EditText newEditName= (EditText) findViewById(R.id.edit_name);
        EditText newEditQuantity= (EditText) findViewById(R.id.edit_quantity);


        //put the values of the EditText fields on Extra using intent in order to be re-entered again on return
        Intent intent = new Intent (EditGroceryList.this, addImage.class);
        String invalid=validateInputs(newEditId.getText().toString(),newEditQuantity.getText().toString());
        if(invalid.matches("")) {
            intent.putExtra("selectedId", Integer.parseInt(newEditId.getText().toString()));
            intent.putExtra("selectedName", newEditName.getText().toString());
            intent.putExtra("selectedQuantity", Integer.parseInt(newEditQuantity.getText().toString()));
            //let addImage know where activity is coming from so it knows where to return to
            intent.putExtra("comeFrom", "EditGrocery");
            startActivity(intent);

        }
        else{
            Toast.makeText(getApplicationContext(),
                    "The following fields were invalid: "+ invalid, Toast.LENGTH_SHORT).show();
        }

    }
    /*
       validateInputs(String id,String date)
       -calls other functions to validate inputs. This is what program calls to validate both fields
       -if invalid, return the name of the field to display

*/
    public String validateInputs(String id,String quantity){
        String invalidInputs="";
        if(!validateInt(id)){
            invalidInputs=invalidInputs+" id";
        }
        if(!validateInt(quantity)){
            invalidInputs=invalidInputs+" quantity";

        }
        return invalidInputs;
    }

    /*
        validateInt(String integerGiven)
        - validates to see if input is an integer value
    */
    public boolean validateInt(String integerGiven){
        Integer checkNegative;
        try
        {
            checkNegative=Integer.parseInt(integerGiven);
            if(checkNegative<0){
                return false;
            }
            else {
                return true;
            }
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    /*
       removeRecs()
         -removes records in the database and confirms with a toast
    */
    public void removeRecs() {
        mydManger.clearRecords();
        Toast.makeText(getApplicationContext(),
                "All records cleared", Toast.LENGTH_SHORT).show();
    }

    /*
       goToDisplayPage()
         -creates an intent to move to DisplayGrocery page
    */
    public void goToDisplayPage(){
        Intent intent = new Intent (EditGroceryList.this, DisplayGroceryList.class);
        startActivity(intent);
    }



}