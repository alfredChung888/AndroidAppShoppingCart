package com.example.trial_assignment1_ma;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;


public class CustomAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public CustomAdapter(Context context, String[] v) {
        super(context, R.layout.rowlayout, v);
        this.context = context;
        this.values = v;
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        DatabaseManager dbm=new DatabaseManager(context); //context initiated on create


        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

        //find textview to display the information for 1 row
        TextView textView = rowView.findViewById(R.id.label);
        //find imageview to display image for the key
        ImageView imageView = rowView.findViewById(R.id.imageView);

        //set text to given string for current position
        //Note:Values[] is a string array given by DisplayGroceryList
        textView.setText(values[position]);

        //initialise a Bitmap
        Bitmap image;
        //retrieve image by calling a database function that looks through all values
        //for a similar string value when combined.
        //When found, return the image as a bitmap stored for that specific position.
        if(dbm.retrieveImage(values[position])!=null) {
            //if there is a image stored. Get image.
            image = dbm.retrieveImage(values[position]);
            //set image
            imageView.setImageBitmap(image);
        }

        return rowView;
    }
}
