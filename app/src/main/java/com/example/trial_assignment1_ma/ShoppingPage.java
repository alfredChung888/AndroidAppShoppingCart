package com.example.trial_assignment1_ma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.trial_assignment1_ma.databinding.ShoppingpageActivityBinding;

public class ShoppingPage extends AppCompatActivity {
    private ShoppingpageActivityBinding binding;
    private Button homebutton;
    private Button addShopping;
    private Button displayShopping;
    private shoppingDatabaseManager mydmanager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mydmanager=new shoppingDatabaseManager(this);

        binding = ShoppingpageActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //find the Buttons
        homebutton=(Button) findViewById(R.id.homeBtn);
        addShopping=(Button) findViewById(R.id.addShoppingBtn);
        displayShopping=(Button) findViewById(R.id.viewShoppingBtn);


        //set on click listener to go to main activity
        homebutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(ShoppingPage.this,MainActivity.class);
                startActivity(intent);
            }
        });

        //set on click listener to go to add activity
        addShopping.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(ShoppingPage.this,shoppingAddPage.class);
                startActivity(intent);
            }
        });

        //set on click listener to go to list activity
        displayShopping.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(ShoppingPage.this,ShoppingListDisplay.class);
                startActivity(intent);
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
            case R.id.add_button:
                goToAddPage();
                break;
            case R.id.list_rows:
                goToDisplayShoppingPage(); //display data page
                break;

            case R.id.home:
                returnHome();
                break;

            case R.id.remove_rows:
                removeRecs(); //remove all records
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void goToAddPage() {
        Intent intent=new Intent(ShoppingPage.this,shoppingAddPage.class);
        startActivity(intent);
    }


    public boolean goToDisplayShoppingPage() {

        Intent intent = new Intent (ShoppingPage.this, ShoppingListDisplay.class);
        startActivity(intent);
        return true;
    }
    public boolean returnHome() {

        Intent intent = new Intent (ShoppingPage.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    public boolean removeRecs() {
        mydmanager.clearRecords();
        Toast.makeText(getApplicationContext(),
                "Shopping Database Cleared!", Toast.LENGTH_SHORT).show();
//        displayMessage.setText("All Records are removed");
        return true;
    }
}
