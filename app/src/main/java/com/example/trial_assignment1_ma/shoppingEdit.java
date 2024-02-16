package com.example.trial_assignment1_ma;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Date;

public class shoppingEdit extends AppCompatActivity {
    Button editshoppingSave_button, editshoppingCancel_button,editshoppingDelete_button;
    shoppingDatabaseManager mydManger;
    private String[] item = new String[4];
    String checkboxValues="";
    shoppingCustomAdapter adapter;
    ListView list;
    DatabaseManager groceryDB;
    String[] values;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_edit);

        mydManger=new shoppingDatabaseManager(this);
        Intent intent = getIntent();

        //get values from Display Page in order to set the EditText fields
        item=intent.getExtras().getStringArray("ShoppingqueryValues");

        //find the EditText fields
        EditText editshoppingId= (EditText) findViewById(R.id.editshopId);
        EditText editshoppingName= (EditText) findViewById(R.id.editshopName);
        EditText editshoppingLocation= (EditText) findViewById(R.id.editlocation);
        EditText editshoppingDate= (EditText) findViewById(R.id.editdate);

        //set the fields of each description
        editshoppingId.setText(item[0]);
        editshoppingName.setText(item[1]);
        editshoppingLocation.setText(item[2]);
        editshoppingDate.setText(item[3]);

        //find the Buttons to set onclick listener
        editshoppingSave_button=(Button) findViewById(R.id.editshopping_save_button);
        editshoppingCancel_button=(Button) findViewById(R.id.editshopping_cancel);
        editshoppingDelete_button=(Button) findViewById(R.id.editshopping_delete);


        //initiate grocery database object to use it's functions
        groceryDB = new DatabaseManager(this);
        groceryDB.openReadable();

        //get the size
        Integer size=groceryDB.getSize();
        //create a string array of size database
        values = new String[size];
        //retrieve the rows using the grocery database
        values=groceryDB.retrieveRows();
        //create a new instance of shoppingCustomerAdapter with values from groceryList
        adapter = new shoppingCustomAdapter(this, values);

        //find listview for groceryList to be displayed
        list= (ListView) findViewById(R.id.edit_list_View);
        //set each row using adapter
        list.setAdapter(adapter);



        //set onclickListener for saveButton
        editshoppingSave_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //find the EditText fields
                EditText newEditshoppingId = (EditText) findViewById(R.id.editshopId);
                EditText newEditshoppingName = (EditText) findViewById(R.id.editshopName);
                EditText newEditshoppingLocation = (EditText) findViewById(R.id.editlocation);
                EditText newEditshoppingDate = (EditText) findViewById(R.id.editdate);

                //create a new string array
                String[] newValues = new String[5];
                //get values from EditText fields even if unchanged
                newValues[0] = newEditshoppingId.getText().toString();
                newValues[1] = newEditshoppingName.getText().toString();
                newValues[2] = newEditshoppingLocation.getText().toString();
                newValues[3] = newEditshoppingDate.getText().toString();

                //validate inputs gotten from the field after save button is pressed
                String invalidInputs=validateInputs(newValues[0],newValues[3]);
                if (invalidInputs.matches("")) {
                    //get values from Checkbox to be updated
                    boolean[] checkboxes = adapter.getCheckBoxState(); //showRows() already initialised the values

                    for (int i = 0; i < list.getCount(); i++) {
                        if (checkboxes[i] == true)
                            //if they are checked, add to string
                            checkboxValues = checkboxValues + adapter.getName(i) + "\n";// list.getAdapter().getItem(i).toString();
                    }
                    //insert it to last spot on string array
                    newValues[4] = checkboxValues;

                    //run update function
                    runUpdate(newValues);
                    //confirm to user using toast
                    toastPopUp("Shopping List Updated!");
                    returnToShoppingListPage();
                }
                else{
                    toastPopUp("Following fields is not valid:\n"+invalidInputs);
                }

            }
        });

        //set onclicklistener for delete button
        editshoppingDelete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete the item
                mydManger.deleteRow(item);
                //confirm using toast
                toastPopUp("Shopping List Row Deleted!");
                //return to List page
                returnToShoppingListPage();

            }
        });

        //set onclicklistener for cancel button
        editshoppingCancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            //just return to display page without doing anything
            public void onClick(View view) {
                returnToShoppingListPage();
            }
        });


    }

    /*
               validateInputs(String id,String date)
               -calls other functions to validate inputs. This is what program calls to validate both fields
               -if invalid, return the name of the field to display

    */
public String validateInputs(String id,String date){
        String invalidInputs="";
        if(!validateInt(id)){
            invalidInputs=invalidInputs+" id";
        }
        if(!validateDate(date)){
            invalidInputs=invalidInputs+" date";

        }
        return invalidInputs;
}
    /*
               validateInt(String integerGiven)
               -validates to see if input is an integer value

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
            validateDate(String dateGiven)
            -validates the string Date with a regular expression
            -in the case user edits with invalid date e.g. 2020-10-0a9

    */
    public boolean validateDate(String dateGiven){
        if (dateGiven.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) // for yyyy-MM-dd format
            return true;
        else
            return false;
    }


    /*
        runUpdate(String[] newValues)
        - runs database function to delete row
   */

    private void runUpdate(String[] newValues){
        mydManger=new shoppingDatabaseManager(this);
        mydManger.updateRow(item,newValues);
        toastPopUp("ShoppingList updated!");
    }

    /*
        toastPopUp(String text)
        - creates a toast using text given
   */

    private void toastPopUp(String text){
        Toast.makeText(getApplicationContext(),
                text, Toast.LENGTH_SHORT).show();

    }

    /*
       returnToShoppingListPage()
        - creates a new intent to ShoppingList page
   */

    private void returnToShoppingListPage() {
            Intent intent = new Intent (shoppingEdit.this, ShoppingListDisplay.class);
        startActivity(intent);
    }


}