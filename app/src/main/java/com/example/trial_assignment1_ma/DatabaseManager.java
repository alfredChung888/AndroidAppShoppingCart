package com.example.trial_assignment1_ma;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.trial_assignment1_ma.DisplayGroceryList;

public class DatabaseManager implements Serializable {
    public static final String DB_NAME = "products";
    public static final String DB_TABLE = "products";
    public static final int DB_VERSION = 2;
    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " (id INTEGER PRIMARY KEY, product_name TEXT, quantity INTEGER, image BLOB);";
    public static final String[]   TABLE_COLUMNS   = { "id", "product_name", "quantity", "image" };
    private SQLHelper helper;
    private SQLiteDatabase db;
    private Context context;
    public List<String> listDataHeader;
    public  HashMap<String, List<String>> listDataChild;
    private AddGroceryPage addPage;
    String whereClauseMul;
    String whereClause="";
    private byte[] publicbyte;


    public DatabaseManager(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();
    }

    public DatabaseManager openReadable() throws android.database.SQLException {
        helper = new SQLHelper(context);
        db = helper.getReadableDatabase();
        return this;
    }

    public void close() {
        helper.close();
    }



    /*
       addRow(Integer id, String name, Integer quantity, byte[] image)

       -Gets value from activity and checks id if there is duplicate
       -adds it to database if it is valid, returns error message otherwise

   */
    public boolean addRow(Integer id, String name, Integer quantity, byte[] image) {
        synchronized(this.db) {
            boolean checkId = false;
            boolean checkName = false;
            AddGroceryPage.problemString = null;


            //store values given as a new instance of ContentValues and store in respective columns.
            ContentValues newProduct = new ContentValues();
            newProduct.put("id", id);
            newProduct.put("product_name", name);
            newProduct.put("quantity", quantity);
            newProduct.put("image", image);

            // Check if id is in the database, if there is: display error message.
                checkId = checkForId(id);
                if (checkId == true) {
                    AddGroceryPage.problemString = "The Id inserted already exists in the database. Please enter a new one";
                    return false;
                } else {
                    try {
                        db.insertOrThrow(DB_TABLE, null, newProduct);
                    } catch (Exception e) {
                        AddGroceryPage.problemString = "Sorry, there was an unexpected error that occurred";

                        return false;
                    }
                }

            return true;
        }
    }

    /*
       retrieveImage(String compare)

       - gets value of on clicked item for editing
       - searches/retrieves the values to string array from database by comparing the strings
       - Stores image in a public variable called publicbyte

       -if it has an image, return the image, otherwise return null

   */
    public Bitmap retrieveImage(String compare){
        Bitmap bmp;
        String[] values=new String[3];
        //retrieve the values
        values=retrieveItem(compare);

        //check image in public byte
        //Note:publicbyte set to null in retrieveItem() function before searching
        if(publicbyte!=null) {
            //store the byteArray if there is an image
            byte[] byteArray = publicbyte;
            //convert to bitmap variable
            bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            return bmp;
        }

        //no image, return null
        else return null;
    }


    /*
       retrieveRows()

       - Queries the Grocery database and retrieves items.
       - This is done one item at a time and stored in individual index's

       - returns string array with values of Grocery Database in it's own cell

   */
    public String[] retrieveRows() {

        String[] columns = new String[] {"id", "product_name", "quantity"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, "id"); //order by id
        Integer size=getSize();
        String[] tablerows = new String[size];
        Integer iterator=0;
        Integer itemNumber=1;

        //go to first item
        cursor.moveToFirst();
        //set condition where cursor is not past the last item
        while (cursor.isAfterLast() == false) {
            //for each index, store item and its values
            tablerows[iterator] =  "ITEM " + itemNumber + ":\n" + " Id: " +cursor.getInt(0) + " Name: " + cursor.getString(1) + " Quantity: " +
                    cursor.getString(2);
            cursor.moveToNext();
            //iterate by one so tablerows can store in next index
            iterator++;
            itemNumber++;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        //return String array with all values
        return tablerows;
    }


    /*
        retrieveItem(String compare)

        - Note: For EditGrocery Page

        * Users have an option to click on any item in order to edit it.
        * When onClick event is triggered, it returns the string value. But not the image

        - This function compares that string value to all values on the database.

        - returns string array and stores in public byte if comparison is found.

    */
    public String[] retrieveItem(String compare){
        //set public byte to null
        publicbyte=null;
        String[]item=new String[3];
        //query database for values including image
        String[] columns = new String[] {"id", "product_name", "quantity","image"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, "id"); //order by id to make it always in order
        String tempValue;
        byte[] holder;
        Integer tempInt;
        Integer itemNumber=1;

        //iterate through all items in the database
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            tempValue =  "ITEM " + itemNumber + ":\n" + " Id: " +cursor.getInt(0) + " Name: " + cursor.getString(1) + " Quantity: " +
                    cursor.getInt(2);//changed this cursor from getString to getInt;


            //if there is an equal comparison, store all values in string array
            if(tempValue.equals(compare)) {
                tempInt=cursor.getInt(0);
                item[0]=tempInt.toString();

                item[1]=cursor.getString(1);

                tempInt=cursor.getInt(2);
                item[2]=tempInt.toString();

                //store image in publicbyte
                publicbyte=cursor.getBlob(3);
                cursor.close();

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
         getSize()
        - Returns the size of the database by using getCount() function belonging to the cursor class.

   */
    public int getSize() {
        String countQuery = "SELECT  * FROM " + "products";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /*
        deleteRow(String[] item)
       - uses database query DELETE FROM along with the id of the string array located in index 0 to
         delete an instance with unique id.

  */
    public void deleteRow(String[] item){
        String whereClause="id=" + item[0];

        db.execSQL("DELETE FROM " + "products"
                + " WHERE " + whereClause);

    }


    /*
        checkForId(Integer id)
       - queries the database and iterates through them. If id of the same recently given id is found. Return true and print error message
       - Else return false.

  */
    public boolean checkForId(Integer id){
        //set exists to false in the beginning
        boolean exists=false;
        String[] columns = new String[] {"id", "product_name", "quantity"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
        cursor.moveToFirst();
        Integer tempId;
        while (cursor.isAfterLast() == false) {
            //get id of item cursor is currently on
            tempId=cursor.getInt(0);
            //compare id to recently inputted value
            if(tempId==id){
                //return true as it is already in database
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
      checkForName(String name)
     - similar to checkId function but checks for same name to confirm if user wants to add it again.

    */
    public boolean checkForName(String name){
        boolean exists=false;
        String[] columns = new String[] {"id", "product_name", "quantity"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
        cursor.moveToFirst();
        String tempName;
        while (cursor.isAfterLast() == false) {
            //compare name to recently inputted value
            tempName=cursor.getString(1);
            //return true as it is already in database
            if(tempName.equals(name)){
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
      updateRow(String[] item, String[] newValues, byte[] bmp)
     - deletes the id existing in the database already
     - inserts new values in it's place.

    */
    public void updateRow(String[] item, String[] newValues, byte[] bmp){
        Integer newId,newQuantity;
//        whereClause="id= " + item[0];
//        deleteRow(item);
        newId=Integer.parseInt(newValues[0]);
        newQuantity=Integer.parseInt(newValues[2]);
//        addRow(newId,newValues[1],newQuantity,bmp);

        ContentValues values=new ContentValues();
        values.put("id",newId);
        values.put("product_name",newValues[1]);
        values.put("quantity",newQuantity);
        values.put("image",bmp);

        int rowsUpdated=db.update(DB_TABLE,values,"id= '" + item[0] + "'",null);


    }

    /*
     deleteMultipleRowsDB(String[] values)
     - Gets values of onLongClick. i.e. string values or the items selected.
     - Combines them into a single whereclause
     - Calls database function DELETE FROM to delete them from the database

    */
    public void deleteMultipleRowsDB(String[] values){
        String[] deleteItem;
        String[] whereArgs=new String[values.length];
        for(int i=0; i<values.length; i++){

            deleteItem=new String[values.length];
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

        }
        db.execSQL("DELETE FROM " + "products" + " WHERE " + whereClause);

    }

    /*
      clearRecords
     - clears the database using database function

    */
    public void clearRecords()
    {
        db = helper.getWritableDatabase();
        db.delete(DB_TABLE, null, null);
    }

    public List<String> returnListDataHeader(){
        return listDataHeader;
    }
    public HashMap<String, List<String>> returnListDataChild(){
        return listDataChild;
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


