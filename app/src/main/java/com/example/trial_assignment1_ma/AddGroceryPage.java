package com.example.trial_assignment1_ma;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.trial_assignment1_ma.databinding.ActivityAddgrocerypageBinding;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class AddGroceryPage extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityAddgrocerypageBinding binding;

    private DatabaseManager mydManager;

    private EditText pcode, pname, pquantity;
    private Button addButton, cancelButton, gallery,addImage;
    private boolean recInserted;
    private Intent intent;
    byte[] byteArray;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ListView list;
    Bitmap bmp;
    public static String problemString;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddgrocerypageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        intent=new Intent();
        setSupportActionBar(binding.toolbar);

        insertRec(); //call insertRec() function to insert a record to database
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.insert_rows:
                break;
            case R.id.list_rows:
                goToDisplayPage(); //display data page
                break;

            case R.id.home:
                returnToGroceryPage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public boolean insertRec() {

        //declare database and form variable
        mydManager = new DatabaseManager(this);

        //find EditText in xml and store in variable with type EditText to obtain entered value later
        pcode = findViewById(R.id.prod_code);
        pname = findViewById(R.id.prod_name);
        pquantity = findViewById(R.id.prod_qty);

        //initialise the Button from xml file
        addButton = findViewById(R.id.add_button);


        //create an intent to pass the values if any when moving to addImage page so it can be
        //initalised again on return.
        intent= getIntent();
        //if there are values in Intent that means we just returned from addImage page
        //note:addImage before coming back to this page, returns the values given at the start of class.

        if(getIntent().getExtras() != null){

            //get value of Integer value with key "selectedId"
            Integer parsedId=intent.getExtras().getInt("selectedId");
            //set the EditText box with the value obtained with intent
            pcode.setText(String.valueOf(parsedId));


            //check if data has been entered yet into name field
            if(intent.getExtras().getString("selectedName")!=null){
                //get value of String value with key "selectedName"
                String parsedName=intent.getExtras().getString("selectedName");
                //set field of name with value parsed
                pname.setText(parsedName);
            }

            //check if data has been entered yet into Quantity field
            if(!String.valueOf(intent.getExtras().getInt("selectedQuantity")).matches("0")){
                //get value of Int value with key "selectedQuantity"
                Integer parsedQuantity=intent.getExtras().getInt("selectedQuantity");
                //set field of quantity with value parsed
                pquantity.setText(String.valueOf(parsedQuantity));
            }

            //check if an image has been entered yet into Image field
            if(intent.getExtras().getByteArray("image") != null || String.valueOf(intent.getExtras().getByteArray("image")).matches("0")){
                //get the image in the form of byteArray
                byteArray = getIntent().getByteArrayExtra("image");
                //decode it into a bitmap
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                //rescale the image to fit the layout
                Bitmap mutableBitmapImage = Bitmap.createScaledBitmap(bmp,120, 120, false);
                //compress down to byteArray
                mutableBitmapImage.compress(Bitmap.CompressFormat.PNG, 0 , outputStream);
                //assign compressed image in the form of byteArray back to variable.
                byteArray=outputStream.toByteArray();
            }
        }


        //get Button from xml file and store in object of type Button
        addImage=findViewById(R.id.addImage);

        //set on clickListener for an event of user clicking
        addImage.setOnClickListener(v->{
            //get value of pcode and store in Id to be passed to next page through intent
            String invalidInput=validateInputAddImage(pcode.getText().toString(),pquantity.getText().toString());
            if(invalidInput.matches("")) {

                Integer Id = Integer.parseInt(pcode.getText().toString());
                String name = null;
                Integer quantity = null;
                if (!pname.getText().toString().matches("")) {
                    //if name field is not null, store in name variable to be passed
                    name = pname.getText().toString();
                }
                if (!pquantity.getText().toString().matches("")) {
                    //if quantity field is not null, store in quantity variable to be passed
                    quantity = Integer.parseInt(pquantity.getText().toString());
                }

                //go to function to pass intents and start activity to move to next page
                goToAddImage(Id, name, quantity);
            }
            else{
                Toast.makeText(getApplicationContext(),
                        "The following fields were invalid: " + invalidInput, Toast.LENGTH_SHORT).show();
            }
        });




        //On click listener for when addButton is clicked
        addButton.setOnClickListener(v -> {
            Integer id;
            String name;
            Integer quantity;
            String problem=null;

            //check if any fields are empty
            problem=checkEmptyStrings(pcode,pname,pquantity);
            if(problem!=""){
                //display missing fields to user
                Toast.makeText(getApplicationContext(),
                        "The following fields were missing:\n " + problem, Toast.LENGTH_SHORT).show();
            }
            else {
                String invalidInput=validateInputs(pcode.getText().toString(),pquantity.getText().toString());

                //check if fields are valid
                if (invalidInput.matches("")) {
                    //take input from the EditText fields to be stored on the database
                    id = Integer.parseInt(pcode.getText().toString());
                    name = pname.getText().toString();
                    quantity = Integer.parseInt(pquantity.getText().toString());


                    //check if name is already in the database to confirm if user wants to add a new instance
                    if (mydManager.checkForName(name)) {
                        String passId = id.toString();
                        String passQuantity = quantity.toString();
                        String[] passValues = new String[]{passId, name, passQuantity};
                        displayConfirmation(passValues);
                    }

                    //run database function add row and pass values that are to be added with
                    // the image in the form of byteArray
                    recInserted = mydManager.addRow(id, name, quantity, byteArray);


                    //Toasts, used as confirmation to the user to confirm actions for error responses
                    if (recInserted) {
                        Toast.makeText(getApplicationContext(),
                                "The row in the products table is inserted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                problemString, Toast.LENGTH_SHORT).show();
                    }

                    //clear the form
                    pcode.setText("");
                    pname.setText("");
                    pquantity.setText("");
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "The following fields were invalid: " + invalidInput, Toast.LENGTH_SHORT).show();
                }
            }
        });



        //when cancel button is clicked, clear the form.
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v->{
            pcode.setText("");
            pname.setText("");
            pquantity.setText("");
            returnToGroceryPage();
        });
        return true;
    }
    public String validateInputAddImage(String id,String quantity) {
        String invalidInputs="";
        if(!validateInt(id)){
            invalidInputs=invalidInputs+" id";
        }
        if(quantity.matches("")){

        }
        else{
            if(!validateInt(quantity)){
                invalidInputs=invalidInputs+" quantity";
            }
        }
        return invalidInputs;
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
    displayConfirmation(String[] id)
    Creates a DialogInterface to confirm if you want to add nothing row/instance of the name
    as it already exists in the database.

*/
    private void displayConfirmation(String[] id){
        //initialises Alert Dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //allow for cancel
        builder.setCancelable(true);
        //set title of alert dialog
        builder.setTitle("Confirm Action");
        //set message
        builder.setMessage("Item name is already in your list, would you like to add it again?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if positive button is clicked, do nothing
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if negative button is clicked, delete the newly inserted value from database
                mydManager.deleteRow(id);
                //go back to return page. i.e. clear values.
                returnToAddRowPage();
            }
        });

        AlertDialog dialog = builder.create();
        //show alert dialog
        dialog.show();
    }


    /*
    goToDisplayPage()

    Creates a new intent and starts activity to move to: Grocery class i.e. home
     */

    private void returnToGroceryPage() {
        Intent intent=new Intent(AddGroceryPage.this,GroceryPage.class);
        startActivity(intent);
    }


    /*
    goToDisplayPage()
    Does nothing
       */
    private void returnToAddRowPage() {
        Intent intent=new Intent(AddGroceryPage.this,AddGroceryPage.class);
        startActivity(intent);
    }


    /*
    goToAddImage(Integer id, String name, Integer quantity)

    Called when event onClick occurs on Button addImage.
    - Pass values by putting values on intent so next activity can return them on return
     */
    private void goToAddImage(Integer id, String name, Integer quantity) {

        //create new intent from this class to addImage page
        Intent intent=new Intent(AddGroceryPage.this,addImage.class);

        //put value of id on intent with key "selectedId"
        intent.putExtra("selectedId",id);

        if(name!=null){
            //put value of name on intent with key "selectedName"
            intent.putExtra("selectedName",name);
        }

        if(quantity!=null){
            //put value of quantity on intent with key "selectedQuantity"
            intent.putExtra("selectedQuantity",quantity);
        }

        //pass where activity came from so addImage class knows which page to return to.
        intent.putExtra("comeFrom","AddGrocery");

        //start activity with passed values and move to addImage class.
        startActivity(intent);
    }

    /*
    goToDisplayPage()

    Creates a new intent and starts activity to move to: DisplayGroceryList class
     */
    public boolean goToDisplayPage() {
        Intent intent = new Intent (AddGroceryPage.this, DisplayGroceryList.class);
        startActivity(intent);
        return true;
    }

}
