package com.example.trial_assignment1_ma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trial_assignment1_ma.databinding.ActivityGrocerypageBinding;
import com.example.trial_assignment1_ma.databinding.ContentDisplayBinding;

public class GroceryPage extends AppCompatActivity implements View.OnClickListener {
    private ActivityGrocerypageBinding binding;
    private Button addGroceryBtn,displayGroceryBtn,editGroceryBtn;

    private DatabaseManager mydManger;
    private TextView displayMessage, productRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocerypage);
        binding = ActivityGrocerypageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //find Buttons in xml
        addGroceryBtn=this.findViewById(R.id.addGroceryBtn);
        displayGroceryBtn=this.findViewById(R.id.viewGroceryBtn);

        //set on click listners
        addGroceryBtn.setOnClickListener(this);
        displayGroceryBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.addGroceryBtn:
                //creates intent to go to Add Grocery Page
                Intent addIntent = new Intent(GroceryPage.this, AddGroceryPage.class);
                startActivity(addIntent);
                break;
            case R.id.viewGroceryBtn:
                //creates intent to go to Display Grocery Page
                Intent viewIntent = new Intent(GroceryPage.this, DisplayGroceryList.class);
                startActivity(viewIntent);
                break;

        }
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
                goToAddPage();
                break;
            case R.id.list_rows:
                goToDisplayPage(); //display data page
                break;

            case R.id.home:
                returnToMainPage();
                break;

            case R.id.remove_rows:
                removeRecs(); //remove all records
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /*
        returnToMainPage()
        -goes back to Main Activity page

     */
    private void returnToMainPage() {
        Intent intent=new Intent(GroceryPage.this,MainActivity.class);
        startActivity(intent);
    }

    /*
      removeRecs()
      -deletes the database

   */
    public boolean removeRecs() {
        mydManger.clearRecords();
        Toast.makeText(getApplicationContext(),
                "All records removed " , Toast.LENGTH_SHORT).show();
        productRecord.setText("");
        return true;
    }

    /*
         goToAddPage()
          -goes to Add Page

    */
    private void goToAddPage() {
        Intent intent=new Intent(GroceryPage.this,AddGroceryPage.class);
        startActivity(intent);
    }

    /*
        goToDisplayPage()
        -Goes to Display Page

   */
    public boolean goToDisplayPage() {

        Intent intent = new Intent (GroceryPage.this, DisplayGroceryList.class);
        startActivity(intent);
        return true;
    }
}