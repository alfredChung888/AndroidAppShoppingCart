package com.example.trial_assignment1_ma;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.trial_assignment1_ma.databinding.ContentDisplayBinding;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class DisplayGroceryList extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ContentDisplayBinding binding;
    private DatabaseManager mydManger;
    private TextView displayMessage, productRecord;
    CustomAdapter listAdapter;
    public List<String> listDataHeader;
    Integer colourId=Color.WHITE;
    ArrayList<String> changedColorValues;
    Integer positionOfItem;
    Button backBtn,deleteMultipleBtn;
    String editValues;



    public Integer positionItem;


    ListView list;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_display);

        //find listview in xml file
        list= (ListView) findViewById(R.id.listView);


        showRows();

        //find backButton in xml file
        backBtn=(Button) findViewById(R.id.back);
        //find delete button on xml file
        deleteMultipleBtn=(Button) findViewById(R.id.deleteMultipleRows);


        //set on click listener for delete button
        deleteMultipleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMultipleRows();
            }
        });

        //set on click listener for back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToGroceryPage();
            }
        });

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
                goToAddPage(); //go to the entry form
                break;
            case R.id.list_rows:
                // do nothing as we are currently on list_row
                showRows();
                break;
            case R.id.remove_rows:
                removeRecs(); //remove all records
                break;

            case R.id.home:
                returnToGroceryPage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean showRows(){
        mydManger= new DatabaseManager(this);
        mydManger.openReadable();

        //get size of database
        Integer size=mydManger.getSize();
        //initialise string array for custom adapter later
        String[] values = new String[size];

        //initialise ArrayList to store string value of whichever gets clicked
        changedColorValues=new ArrayList<String>();
        Integer[] colourChanged=new Integer[mydManger.getSize()];
        Arrays.fill(colourChanged,Color.WHITE);
        int Gray=Color.GRAY;

        //retrieve all rows in database and store in string array called "values"
        values= mydManger.retrieveRows();

        CustomAdapter adapter = new CustomAdapter(this, values);//pass values here
        //find listview to display all rowviews returned by CustomAdapter
        list= (ListView) findViewById(R.id.listView);
        //set adapter
        list.setAdapter(adapter);

        //set an onlongclick listener (this is for deleting multiple items at once)
        list.setOnItemLongClickListener((parent,v,position,id) -> {
            //get colour of item clicked.
            colourId=colourChanged[position];

            //if it is gray, it means it has already been clicked. Revert back to original colour and
            //remove from selected item list "changedColorValues".
            if(colourId==Gray){
                //get position ofo item clicked
                positionOfItem = position;
                v.setBackgroundColor(Color.WHITE);
                list.setCacheColorHint(Color.WHITE);

                //set colour to white in case user clicks it again
                colourChanged[position]=Color.WHITE;

                //String tempValue=list.getAdapter().getItem(positionItem).toString();
                String tempValue=list.getAdapter().getItem(position).toString();
                if(changedColorValues.contains(tempValue)) {
                    changedColorValues.remove(tempValue);
                }
            }

            //if it is white, it means it has not been selected.
            else {
                //get position of the item
                positionOfItem=position;
                //change the colour from white to gray
                colourChanged[position]=Color.GRAY;
                list.setCacheColorHint(Color.GRAY);
                //store the value of the item in tempvalue
                String tempValue=list.getAdapter().getItem(position).toString();
                //store that value in an array to be deleted later on
                changedColorValues.add(tempValue);
                //set background to gray to inform user of click
                v.setBackgroundColor(Color.GRAY);


            }
            return true;

        });


        //set on click listener for editing option of the app
        list.setOnItemClickListener((parent, v, position, id) -> {
            //get position of the item clicked
            positionItem=position;
            //set Alert Builder to confirm if user wants to edit item
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Confirm Action");
            builder.setMessage("Would you like to edit item " + list.getAdapter().getItem(position) + "?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String[] item=new String[3];
                            //get value of items in the form of string
                            String compare=list.getAdapter().getItem(positionItem).toString();
                            //this is for edit page. A public variable that will be used to retrieve image before calling startActivity
                            editValues=compare;
                            //if string passed matches an item in database, return those values back
                            //as a string array.
                            item= mydManger.retrieveItem(compare);
                            //go to function to pass current values and get images before going to next activity
                            goToEditPage(item);

                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if negative button is picked, do nothing
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });
        return true;
    }


    /*
              returnToGroceryPage()
             -goes back to GroceryPage

    */
    private void returnToGroceryPage() {
        Intent intent = new Intent (DisplayGroceryList.this, GroceryPage.class);
        startActivity(intent);
    }

    /*
         public void deleteMultipleRows()
         -Gets values of all the items that onLongClick listener picked up
         -Pass them to a String array with each value having it's own index
            *We do this by creating an iterator and iterating through them, passing the values each time.

         -Call the deleteMultipleRowsDB() function which takes the string array and compares them to values
         in the database and deletes items that match.

         -After items have been deleted. To show user updated list:
            *call the showRows() function again.
            *create toast to inform user of action.

    */
    public void deleteMultipleRows() {
        String[] values=new String[changedColorValues.size()];
        Integer i=0;
        ListIterator<String> iterate = changedColorValues.listIterator();
        while(iterate.hasNext()) {
            values[i]=iterate.next();
            i++;
        }

         mydManger.deleteMultipleRowsDB(values);
        showRows();
        Toast.makeText(getApplicationContext(),
                "Selected Items Deleted!", Toast.LENGTH_SHORT).show();
    }

    /*
         removeRecs()
         -removes the database and displays toast to confirm action

    */
    public boolean removeRecs() {
        mydManger.clearRecords();
        Toast.makeText(getApplicationContext(),
                "All records removed", Toast.LENGTH_SHORT).show();
        return true;
    }


    /*
     goToAddPage(String[] item)
     -creates activity to move to AddGroceryPage

    */

    public void goToAddPage(){
        Intent intent = new Intent (DisplayGroceryList.this, AddGroceryPage.class);
        startActivity(intent);
    }


    /*
     goToEditPage(String[] item)
     -Initialises intent to go to EditGroceryList
     -Put values in extra with keys to identify which value to get later
     -Retrieve the image of the clicked item
     -start the activity

    */
    public void goToEditPage(String[] item){
        Intent intent = new Intent (DisplayGroceryList.this, EditGroceryList.class);
        intent.putExtra("queryValues",item);
        if(mydManger.retrieveImage(editValues)!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mydManger.retrieveImage(editValues).compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] editByteArray = stream.toByteArray();
            intent.putExtra("image", editByteArray);
        }

        startActivity(intent);
    }





}