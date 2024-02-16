package com.example.trial_assignment1_ma;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.trial_assignment1_ma.databinding.ShoppingpageActivityBinding;
import com.example.trial_assignment1_ma.shoppingAddPage;

import com.example.trial_assignment1_ma.databinding.ShoppingListDisplayBinding;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;


public class ShoppingListDisplay extends AppCompatActivity {
    ListView list;
    private shoppingDatabaseManager mydManager;
    public Integer positionItem;
    ShoppingpageActivityBinding binding;
    Button backBtn,deleteMultipleBtn;
    Integer colourId=Color.WHITE;
    ArrayList<String> changedColorValues;
    Integer positionOfItem;
    Drawable background;
    String formattedDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_display);

        backBtn=(Button) findViewById(R.id.back);
        deleteMultipleBtn=(Button) findViewById(R.id.deleteMultipleRows);

        list= (ListView) findViewById(R.id.shoppinglistView);
        showRows();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShopPage();

            }
        });
        deleteMultipleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMultipleRows();

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
                goToShopPage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public boolean showRows(){
        mydManager= new shoppingDatabaseManager(this);
        mydManager.openReadable();
        Integer size=mydManager.getSize();
        String[] values = new String[size];
        changedColorValues=new ArrayList<String>();
        Integer[] colourChanged=new Integer[mydManager.getSize()];
        Arrays.fill(colourChanged,Color.WHITE);
        int Gray=Color.GRAY;

//Retrieve Current Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date dateNotFormatted = Calendar.getInstance().getTime();
        formattedDate=sdf.format(dateNotFormatted);
//

        values= mydManager.retrieveRows(formattedDate);

        CustomAdapter adapter = new CustomAdapter(this, values);//pass values here
        list= (ListView) findViewById(R.id.shoppinglistView);
        list.setBackgroundColor(Color.WHITE);
        list.setAdapter(adapter);




        list.setOnItemLongClickListener((parent,v,position,id) -> {
            colourId=colourChanged[position];
            if(colourId==Gray){
                positionOfItem = position;
                v.setBackgroundColor(Color.WHITE);
                colourChanged[position]=Color.WHITE;
                String tempValue=list.getAdapter().getItem(position).toString();
                if(changedColorValues.contains(tempValue)) {
                    changedColorValues.remove(tempValue);
                }
            }
            else {
                positionOfItem=position;
                colourChanged[position]=Color.GRAY;
                String tempValue=list.getAdapter().getItem(position).toString();
                changedColorValues.add(tempValue);
                v.setBackgroundColor(Color.GRAY);


            }
            return true;

        });

        list.setOnItemClickListener((parent, v, position, id) -> {
            positionItem=position;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Confirm Action");
            builder.setMessage("Would you like to edit item? " + list.getAdapter().getItem(position) );
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String[] item=new String[4];
                            String compare=list.getAdapter().getItem(positionItem).toString();
                            item= mydManager.retrieveItem(compare);
                            goToEditPage(item);

                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });


        return true;
    }


    public boolean removeRecs() {
        mydManager.clearRecords();
//        displayMessage.setText("All Records are removed");
        return true;
    }

    public void deleteMultipleRows() {
        String[] values=new String[changedColorValues.size()];
        Integer i=0;
        ListIterator<String> iterate = changedColorValues.listIterator();
        while(iterate.hasNext()) {
           values[i]=iterate.next();
           i++;
        }

        mydManager.deleteMultipleRowsDB(values);
        showRows();
        Toast.makeText(getApplicationContext(),
                "Selected Items Deleted!", Toast.LENGTH_SHORT).show();
    }



    public void goToAddPage(){
        Intent intent = new Intent (ShoppingListDisplay.this, shoppingAddPage.class);
        startActivity(intent);
    }
    public void goToShopPage(){
        Intent intent = new Intent (ShoppingListDisplay.this, ShoppingPage.class);
        startActivity(intent);
    }
    public void goToEditPage(String[] item){
        Intent intent = new Intent (ShoppingListDisplay.this, shoppingEdit.class); //make edit page
        intent.putExtra("ShoppingqueryValues",item);
        startActivity(intent);
    }

}