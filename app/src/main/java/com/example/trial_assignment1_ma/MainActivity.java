package com.example.trial_assignment1_ma;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.trial_assignment1_ma.databinding.ActivityMainBinding;

import android.widget.Button;


public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;


    private Button bGrocery,bShopping;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initiate button and set on click listener
        bGrocery=this.findViewById(R.id.groceryPageBtn);
        bGrocery.setOnClickListener(new View.OnClickListener(){
            @Override
            //go to Grocery Main Page
            public void onClick(View v) {
                goGroceryPage(v);
            }
        });

        //initiate button and set on click listener
        bShopping=this.findViewById(R.id.shoppingListPage);
        bShopping.setOnClickListener(new View.OnClickListener(){
            @Override
            //go to Shopping Main Page
            public void onClick(View v) {
                goToShoppingPage(v);
            }
        });
    }
    /*
        goToShoppingPage(View v)
        - Go to main shopping page
    */
    private void goToShoppingPage(View v) {
        Intent intent = new Intent (MainActivity.this, ShoppingPage.class);
        startActivity(intent);
    }


    /*
        goGroceryPage(View view)
        - Go to main grocery page
    */
    public void goGroceryPage(View view) {

        Intent intent = new Intent (MainActivity.this, GroceryPage.class);
        startActivity(intent);
    }

}