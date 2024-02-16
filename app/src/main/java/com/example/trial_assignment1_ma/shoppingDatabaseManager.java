package com.example.trial_assignment1_ma;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;

import com.example.trial_assignment1_ma.shoppingAddPage;


public class shoppingDatabaseManager implements Serializable {
    public static final String DB_NAME = "shoppingList";
    public static final String DB_TABLE = "shoppingList";
    public static final int DB_VERSION = 2; //added a new column items
    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " (id INTEGER PRIMARY KEY, shop_name TEXT, location TEXT, date TEXT, items TEXT);";
    public static final String[]   TABLE_COLUMNS   = { "id", "shop_name", "location", "date", "items" };
    private SQLHelper helper;
    private SQLiteDatabase db;
    private Context context;
    String whereClauseMul;
    String whereClause="";





    public shoppingDatabaseManager(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();


    }

    public shoppingDatabaseManager openReadable() throws android.database.SQLException {
        helper = new SQLHelper(context);
        db = helper.getReadableDatabase();
        return this;
    }

    public void close() {
        helper.close();
    }

    public boolean addRow(Integer id, String shopName, String location, String date, String items) {
        // If problem.equal(id) display error message, or problem.equal(name) display dialog interface for confirmation before adding. If cancel button, delete new row with unique id but same name
        synchronized(this.db) {
            boolean checkId=false;
            boolean checkName=false;


            ContentValues newProduct = new ContentValues();
            newProduct.put("id", id);
            newProduct.put("shop_name", shopName); //have a function that creates a dialog interface if name is the same and confirm if they want to add a new item with the same name
            newProduct.put("location", location);
            newProduct.put("date", date);
            newProduct.put("items",items);

            checkId = checkForId(id);
            if (checkId == true) {
                shoppingAddPage.problemString = "The Id inserted already exists in the database. Please enter a new one";
                return false;
            } else {
                try {
                    db.insertOrThrow(DB_TABLE, null, newProduct);
                } catch (Exception e) {
                    shoppingAddPage.problemString = "Sorry, there was an unexpected error that occurred";

                    return false;
                }
            }

//            db.close();

            return true;
        }
    }



    /*
        retrieveRows(String currentDate)
        - Queries and iterates through the database
        - Compares the currentDate passed into the function to the date given in the database
            *This is to categories the dates as past/current for the user

        - returns String array: each row corresponds to one value in the database
   */
    public String[] retrieveRows(String currentDate) {
        String tempcheckId;
        Integer checkId;
        String passedStringValues="";

        Cursor cursor = db.query(DB_TABLE, TABLE_COLUMNS, null, null, null, null, "date DESC"); //order by id
        Integer size=getSize();
        String[] tablerows = new String[size];
        Integer iterator=0;
        Integer itemNumber=1;
        Date date1=new Date();
        Date date2=new Date();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            try {
                //current date to compare
                date1 = new SimpleDateFormat("yyyy-MM-dd").parse(currentDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                //date cursor is pointing at in the database
                date2 = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(3));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //check if database date is before current date
            Boolean current=date2.before(date1);

            if(current==false) {
                //if not: it has a current status
                tablerows[iterator] = "Status: CURRENT \nShopping List " + itemNumber + ":\n" + "Id: " + cursor.getInt(0) + " \nShop Name: " + cursor.getString(1) + " \nLocation: " +
                        cursor.getString(2) + " \nDate: " + cursor.getString(3) + "\n \nGroceryList\n" + cursor.getString(4);

                cursor.moveToNext();
                iterator++;
                itemNumber++;
            }
            else{
                //if it is: it has a past status
                tablerows[iterator] = "Status: PAST \nShopping List " + itemNumber + ":\n" + "Id: " + cursor.getInt(0) + " \nShop Name: " + cursor.getString(1) + " \nLocation: " +
                        cursor.getString(2) + " \nDate: " + cursor.getString(3) + "\n \nGroceryList\n" + cursor.getString(4);

                cursor.moveToNext();
                iterator++;
                itemNumber++;
            }
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return tablerows;
    }

    /*
        retrieveItem(String compare)
        - Queries and iterates through the database
        - Compares the currentDate passed into the function to the date given in the database
            *This is to categories the dates as past/current for the user

        - returns String array: each index corresponds to a detail of the item.
   */
    public String[] retrieveItem(String compare){
        String passedStringValues="";

        String[]item=new String[5];
        String[] columns = new String[] {"id", "shop_name", "location", "date", "items"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, "date DESC"); //order by id to make it always in order
        String tempValue;
        Integer tempInt;
        Date date1=new Date();
        Date date2=new Date();
        Integer itemNumber=1;
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            try {
                date1 = new SimpleDateFormat("yyyy-MM-dd").parse(getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                date2 = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(3));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //check if database date is before current date
            Boolean current=date2.before(date1);

            if(current==false) {
                //if not: it has a current status
                tempValue = "Status: CURRENT \nShopping List " + itemNumber + ":\n" + "Id: " + cursor.getInt(0) + " \nShop Name: " + cursor.getString(1) + " \nLocation: " +
                        cursor.getString(2) + " \nDate: " + cursor.getString(3) + "\n \nGroceryList\n" + cursor.getString(4);
            }
            else{
                //if it is: it has a past status
                tempValue = "Status: PAST \nShopping List " + itemNumber + ":\n" + "Id: " + cursor.getInt(0) + " \nShop Name: " + cursor.getString(1) + " \nLocation: " +
                        cursor.getString(2) + " \nDate: " + cursor.getString(3) + "\n \nGroceryList\n" + cursor.getString(4);
            }

            //compare string passed through with temp value just created above
            if(tempValue.equals(compare)) {
                //if it matches, pass each value in each index cell
                tempInt=cursor.getInt(0);
                item[0]=tempInt.toString();

                item[1]=cursor.getString(1);

                item[2]=cursor.getString(2);

                item[3]=cursor.getString(3);

                item[4]=cursor.getString(4);


                cursor.close();

                //return the string array
                return item;


            }
            else {
                cursor.moveToNext();
                itemNumber++;
            }

        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return item;
    }


    /*
        getDate()
        - function to get the current date and format it
        - returns date calculated
   */
    private String getDate(){
        String shopDate,formattedDate;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date dateNotFormatted = Calendar.getInstance().getTime();
        formattedDate=sdf.format(dateNotFormatted);


        shopDate=formattedDate;
        return shopDate;
    }

    /*
        getSize()
        - function to get the size of the database
            *Done by querying the database and using a cursor function "getcount()"
        - returns the count
   */
    public int getSize() {
        String countQuery = "SELECT  * FROM " + "shoppingList";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /*
        deleteRow(String[] item)
        - delete a row from the database
            *done by using the id as it is unique
   */
    public void deleteRow(String[] item){
        String whereClause="id=" + item[0];


        db.execSQL("DELETE FROM " + "shoppingList"
                + " WHERE " + whereClause);

    }

    /*
        deleteMultipleRowsDB(String[] values)
        - delete multiple rows from the database
            Done by:
            *getting the string values selected from activity
            *comparing them to values in the database
            *getting the individual values when comparison is equal.
            *add the id to the whereClause to be deleted in 1 query
   */
    public void deleteMultipleRowsDB(String[] values){
        String[] deleteItem;
        String[] whereArgs=new String[values.length];
        int iterator=1;
        for(int i=0; i<values.length; i++){

            //string array to get individual values
            deleteItem=new String[values.length];
            //compare string value
            //Note:values[index] where each index is a full string of all the values
            //deleteItem[index] where each index is the string broken down into it's individual values
            deleteItem=retrieveItem(values[i]);

            if(i==0){
                whereClause="id= " + deleteItem[0];
            }
            else if(i==values.length-1){
                whereClause = whereClause + " OR id = " + deleteItem[0];
            }
            else {
                whereClause = whereClause + " OR id = " + deleteItem[0];
            }
            iterator++;
        }
        //delete all id's at once
        db.execSQL("DELETE FROM " + "shoppingList" + " WHERE " + whereClause);

    }


    /*
        checkForId(Integer id)
        - look for the given id in the database
            Done by:
            *passing in the id
            *comparing them to id's in the database
            *if it is found, set exists to true and return
            *else exists is normally false
   */
    public boolean checkForId(Integer id){
        boolean exists=false;
        String[] columns = new String[] {"id", "shop_name", "location", "date", "items"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
        cursor.moveToFirst();
        Integer tempId;
        while (cursor.isAfterLast() == false) {
            //get the id cursor is currently on
            tempId=cursor.getInt(0);
            //check if current cursor id is equal to given id
            if(tempId==id){
                //if it exists already, set exists to true and return
                exists=true;
                return exists;
            }
            else {
                cursor.moveToNext();
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return exists;
    }


    /*
        updateRow(String[] item, String[] newValues)
        - deletes row with id just inserted
        - add a new instance of the row with the new values
   */

    public void updateRow(String[] item, String[] newValues){

        Integer newId,newQuantity;
//        deleteRow(item);
        newId=Integer.parseInt(newValues[0]);
//        addRow(newId,newValues[1],newValues[2],newValues[3],newValues[4]);


        ContentValues values=new ContentValues();
        values.put("id",newId);
        values.put("shop_name",newValues[1]);
        values.put("location",newValues[2]);
        values.put("date",newValues[3]);
        values.put("items",newValues[4]);


        int rowsUpdated=db.update(DB_TABLE,values,"id= '" + item[0] + "'",null);
    }


    /*
        clearRecords()
        - clear records using database function
   */

    public void clearRecords()
    {
        db = helper.getWritableDatabase();
        db.delete(DB_TABLE, null, null);

    }





    public class SQLHelper extends SQLiteOpenHelper {
        public SQLHelper (Context c) {
            super(c, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Products table", "Upgrading database i.e. dropping table and re-creating it");
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    }
}

