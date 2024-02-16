package com.example.trial_assignment1_ma;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trial_assignment1_ma.databinding.ShoppingAddpageBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class shoppingAddPage extends AppCompatActivity {

    ListView list;
    shoppingDatabaseManager dbm; //for grocery database
    ShoppingAddpageBinding binding;
    boolean[] checkBoxes;
    DatabaseManager mydmanager;//need it to get values from the database
    shoppingCustomAdapter adapter;
    EditText shopId,shopName,location;
    CalendarView date;
    Button cancel,save;
    private boolean recInserted;
    private TextView response;
    String checkboxValues;
    String shopDate;
    String formattedDate;
    public static String problemString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.shopping_addpage_content); //will not inflate the menu bar
        binding = ShoppingAddpageBinding.inflate(getLayoutInflater());

        insertRows();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_rows:
                insertRows();
                break;
            case R.id.list_rows:
//                goToDisplayList;
                break;

            case R.id.remove_rows:
                removeRecs();
                break;

            case R.id.home:
                returnToShoppingPage();
        }
        return true;
    }



    public void insertRows(){
        //declare database and form variable
        dbm = new shoppingDatabaseManager(this);
        mydmanager = new DatabaseManager(this);
        mydmanager.openReadable();
        Integer size=mydmanager.getSize();
        String[] values = new String[size];

        //get values of grocery list
        values=mydmanager.retrieveRows();
        //create an adapter for the child listview
        adapter = new shoppingCustomAdapter(this, values);
        checkBoxes = new boolean[values.length]; //or dbs.getSize()


        //find the listview in the Add page for shopping list
        list= (ListView) findViewById(R.id.list_View);
        //set the adapter for the child listview
        list.setAdapter(adapter);
        //allows for both Nested scrolling and listview scrolling
        list.setFocusable(true);
        list.setNestedScrollingEnabled(true);

        //find fields that user needs to input
        response = findViewById(R.id.response);
        shopId = findViewById(R.id.shopId);
        shopName = findViewById(R.id.shopName);
        location = findViewById(R.id.location);
        date=(CalendarView) findViewById(R.id.date);


        //retrieveRows() already initialised the values  line:92
        //now we create a checkbox boolean for each value
        boolean[] checkboxes = adapter.getCheckBoxState();

        //
        checkboxValues="";


        //get format for todays date and store it to be checked
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date c = Calendar.getInstance().getTime();
        formattedDate=sdf.format(c);

        //set shopDate to have today's date if user does not change the date
        shopDate=formattedDate;


        //set an OnDateChangeListener to update the shopDate variable on change
        date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            String extraZero="";
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                //just adding a 0 in front of month to look better
                if(month+1<10){
                    extraZero="0";
                }
                else{
                    extraZero="";
                }

                //set shopdate formated correctly
                shopDate = String.valueOf(year) + "-" + extraZero + String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
            }
        });


        //when add button on the form is clicked
        save = findViewById(R.id.save_button);
        save.setOnClickListener(v -> {
            Integer shopid;
            String shopname, shoplocation;
            String problem=null;
            String check=String.valueOf(shopId.getText().toString());

            //check all checkbox's if any have been checked
            for (int i = 0; i < list.getCount(); i++) {
                if (checkboxes[i] == true)
                    //if there has:
                    //add value of any that have been checked.
                    checkboxValues =checkboxValues + adapter.getName(i) + "\n"; //Note: getName(i) is just a function to get values in shoppingCustomAdapter
            }

            //check if any fields are empty
            problem=checkEmptyStrings(shopId,shopName,location);
            if(problem!="\n"){
                //display missing fields to user
                Toast.makeText(getApplicationContext(),
                        "The following fields were missing: " + problem, Toast.LENGTH_SHORT).show();
            }

            //validate Integer values
            else if(!validateInt(check)){
                //display invalid after checking if fields are missing
                Toast.makeText(getApplicationContext(),
                        "The following fields are invalid: id", Toast.LENGTH_SHORT).show();
            }
            else {
                //insert input taken from the form the database
                shopid = Integer.parseInt(shopId.getText().toString());
                shopname = shopName.getText().toString();
                shoplocation = location.getText().toString();

                //add Row to database when everything is verified
                recInserted = dbm.addRow(shopid, shopname, shoplocation, shopDate, checkboxValues);

                //confirm to user if it has been inserted or if an error has occurred
                if (recInserted) {
                    Toast.makeText(getApplicationContext(),
                            "The row in the products table is inserted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            problemString, Toast.LENGTH_SHORT).show();
                }


                //clear the form
                shopId.setText("");
                shopName.setText("");
                location.setText("");

                goToAddPage();
            }
        });

        //when cancel button is clicked, clear the form.
        cancel = findViewById(R.id.shopcancelButton);
        cancel.setOnClickListener(v->{
            returnToShoppingPage();
        });
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
        checkEmptyStrings(EditText id, EditText name, EditText localtion)
        - Check if EditText fields are empty, if they are: add them to the string to be displayed to user
    */
    private String checkEmptyStrings(EditText id, EditText name, EditText localtion){
        String emptyFields="\n";
        if(id.getText().toString().matches("") ){
            emptyFields=emptyFields+" Id ";
        }

        if(name.getText().toString().matches("")){
            emptyFields=emptyFields+" Name ";
        }

        if(localtion.getText().toString().matches("")){
            emptyFields=emptyFields+" Location ";
        }

        return emptyFields;

    }


    /*
            returnToShoppingPage()
            - returns back to shopping page using intent
    */
    public void returnToShoppingPage(){
        Intent intent=new Intent(shoppingAddPage.this,ShoppingPage.class);
        startActivity(intent);
    }
    /*
            goToDisplayPage()
            - goes to DisplayPage using intent
    */
    public void goToDisplayPage(){
        Intent intent=new Intent(shoppingAddPage.this,ShoppingListDisplay.class);
        startActivity(intent);
    }
    /*
            goToAddPage()
            - goes to Add Page using intent
    */
    public void goToAddPage(){
        Intent intent=new Intent(shoppingAddPage.this,shoppingAddPage.class);
        startActivity(intent);
    }
    /*
            removeRecs()
            -Clears the database
    */
    public boolean removeRecs() {
        dbm.clearRecords();
        return true;
    }


}