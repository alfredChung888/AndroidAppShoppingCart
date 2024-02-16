package com.example.trial_assignment1_ma;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class shoppingCustomAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    private boolean checked = false;

    // boolean array for storing
    //the state of each CheckBox
    boolean[] checkBoxState;
    ViewHolder viewHolder;


    public shoppingCustomAdapter(Context context, String[] v) {
        super(context, R.layout.rowlayout, v);
        this.context = context;
        this.values = v;
        checkBoxState = new boolean[values.length];
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.shoppinglist_layout, null);
            viewHolder=new ViewHolder();


            //cache the views
            viewHolder.name= convertView.findViewById(R.id.label);
            viewHolder.checkBox= convertView.findViewById(R.id.checkBox);

            //link the cached views to the convertview
            convertView.setTag(viewHolder);
        }
        else
            viewHolder=(ViewHolder) convertView.getTag();

        viewHolder.name.setTextColor(Color.BLACK);
        viewHolder.name.setText(values[position]);

        String s = values[position];


        //VITAL PART!!! Set the state of the
        //CheckBox using the boolean array
        viewHolder.checkBox.setChecked(checkBoxState[position]);


        //for managing the state of the boolean
        //array according to the state of the
        //CheckBox
        viewHolder.checkBox.setOnClickListener(v -> {
            if(((CheckBox)v).isChecked()) {
                checkBoxState[position] = true;
            }
            else {
                checkBoxState[position] = false;

            }

        });

        //return the view to be displayed
        return convertView;
    }




    public boolean[] getCheckBoxState(){
        return checkBoxState;
    }

    public String getName(int pos){
        String val = values[pos];
        return val;
    }


    private class ViewHolder
    {
        ImageView photo;
        TextView name;
        CheckBox checkBox;

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public TextView getName() {
            return name;
        }
    }

}
