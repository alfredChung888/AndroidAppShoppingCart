package com.example.trial_assignment1_ma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.ListIterator;

public class addImage extends AppCompatActivity implements RecyclerView_AddImage.ItemClickListener {
    DatabaseManager dbm;
    HashMap<Integer,Bitmap> imageInfo;
    private Integer selectedId,selectedQuantity;
    private String selectedName,comeFrom;
    Button save,cancel,btnTakePhoto,gallery,delete;
    public static final int RequestPermissionCode=2;
    public static final int REQUEST_IMAGE_CAPTURE=3;
    Context context;
    RecyclerView_AddImage adapter;
    private static HashMap<Integer,Bitmap> dataToPush= new HashMap<Integer,Bitmap>();
    Integer counter;
    private static HashMap<Integer,Bitmap> extraImages=new HashMap<Integer,Bitmap>();
    Integer colourId=Color.WHITE;
    ArrayList<Integer> changedColorPosition;
    ArrayList<Integer> colourChanged;
    int Gray=Color.GRAY;
    private static boolean deletedFlag=false;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        context=this;
        dbm=new DatabaseManager(this);
        //initialise hashmap
        imageInfo=new HashMap<Integer, Bitmap>();



        //initialise intent with getIntent()
        Intent intent = getIntent();
        //store extra with key values
        selectedId=intent.getExtras().getInt("selectedId");
        selectedName=intent.getExtras().getString("selectedName");
        selectedQuantity=intent.getExtras().getInt("selectedQuantity");
        //get where the activity came from. This could be from EditGrocery or AddGrocery
        comeFrom=intent.getExtras().getString("comeFrom");


        //find save Image button in xml file
        save=(Button)findViewById(R.id.saveImage);
        //set on click listener event handler for when it is clicked.
        save.setOnClickListener(v->{
            //check where activity came from to return to the correct page with correct values to return.
            if(comeFrom.equals("AddGrocery")) {
                returnToAddPage();
            }
            else{
                returnToEditPage();
            }
        });

        //find cancel Image button in xml file
        cancel=(Button)findViewById(R.id.cancelImage);
        //set on click listener event handler for when it is clicked.
        cancel.setOnClickListener(v->{
            cancelImage();
        });


        EnableRuntimePermission();
        btnTakePhoto=(Button)findViewById(R.id.btnTakePhoto) ;
        btnTakePhoto.setOnClickListener(v -> {
//            Intent photoIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE);
            dispatchTakePictureIntent();
        });

        gallery= (Button) findViewById(R.id.btnGallery);
        gallery.setOnClickListener(v->{
            //Select an image from the Gallery by starting an intent
            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivityForResult(Intent.createChooser(galleryIntent, "Select picture"),3);

        });

        delete= (Button) findViewById(R.id.deleteImage);
        delete.setOnClickListener(v->{
            deleteMultipleImages();

        });



        if(deletedFlag==false) {
            insertImages();
        }

        colourChanged=new ArrayList<Integer>(Collections.nCopies(dataToPush.size(),Color.WHITE));
        changedColorPosition=new ArrayList<Integer>();

        showImages();
    }







    private void showImages() {
        RecyclerView recyclerView = findViewById(R.id.RecyclerView);
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new RecyclerView_AddImage(this, dataToPush);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        recyclerView.setFocusable(true);
        recyclerView.setNestedScrollingEnabled(true);
    }

    @Override
    public void onItemClick(View view, int position) {
        Integer index=position + 1;
        Toast.makeText(addImage.this,
                "Image "+ index + " has been selected!",
                Toast.LENGTH_SHORT).show();
        insertInfo(dataToPush.get(position));
    }

    @Override
    public void OnItemLongClickListener(View view, int position) {
        colourId=colourChanged.get(position);
        if(colourId==Color.GRAY){
            view.setBackgroundColor(Color.WHITE);
            colourChanged.set(position,Color.WHITE);
            if(changedColorPosition.contains(position)) {
                changedColorPosition.remove(Integer.valueOf(position));
            }
        }
        else {
            colourChanged.set(position,Color.GRAY);
            changedColorPosition.add(position);
            view.setBackgroundColor(Color.GRAY);

        }

    }
    /*
    deleteMultipleImages()
    -deletes multiple images from hashmap using for loop.
    -Shift dataToPush
    -Show images

    */
    public void deleteMultipleImages() {
        for(int i=0;i<changedColorPosition.size();i++){
            int tempCHeck=changedColorPosition.get(i);
            Integer index=changedColorPosition.indexOf(changedColorPosition.get(i));
            dataToPush.remove(Integer.valueOf(changedColorPosition.get(i)));
            if(changedColorPosition.get(i)>14) {
                extraImages.remove(Integer.valueOf(changedColorPosition.get(i)));
            }

        }
        //this is so compiler does not input images more than once each time app runs
        deletedFlag=true;
        shift();


        Toast.makeText(getApplicationContext(),
                "Selected Items Deleted!", Toast.LENGTH_SHORT).show();

        colourChanged=new ArrayList<Integer>(Collections.nCopies(dataToPush.size(),Color.WHITE));
        changedColorPosition.clear();
        showImages();
    }


    /*
    shift()
    -copies data from dataToPush hashmap onto entry skipping null values
    -Updates key
    -Clears dataToPush
    -Return updated values to dataToPush.

    */
    public void shift() {
    HashMap<Integer, Bitmap> tempHolder = new HashMap<Integer, Bitmap>();
    int counter=0;

    for (HashMap.Entry<Integer, Bitmap> entry : dataToPush.entrySet()) {
        if(entry.getValue()==null){

        }
        else {
            tempHolder.put(counter,
                    entry.getValue());
            counter++;
        }

    }
    dataToPush.clear();
    for (HashMap.Entry<Integer, Bitmap> entry : tempHolder.entrySet()) {
        if (entry.getValue() != null) {
            // using put method to copy one Map to Other
            dataToPush.put(entry.getKey(),
                    entry.getValue());
        }
    }
}


    /*
    cancelImage()
    returns to correct activity page without returning any values to be stored.

    */
    private void cancelImage() {
        //remove value from hashmap
        imageInfo.remove(selectedId);
        if(comeFrom.equals("AddGrocery")) {
            returnToAddPage();
        }
        else{
            returnToEditPage();
        }
    }


    /*
       returnToEditPage()
       - Creates a new intent
       - adds values given to be returned and stored to their EditText field by other activity
       - adds image in the form of byteArray
       - goes to other page

    */
    private void returnToEditPage() {
        Intent intent=new Intent(addImage.this,EditGroceryList.class);

        //check if there were any images clicked
        if(imageInfo.containsKey(selectedId))
        {
            //initiate new byteArray stream
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //compress bitmap into byteArray format
            imageInfo.get(selectedId).compress(Bitmap.CompressFormat.PNG, 100, stream);
            //store it in a new instance of byte[]
            byte[] byteArray = stream.toByteArray();

            //add it into the intent with key "image"
            intent.putExtra("image",byteArray);

        }

        //create queryValues array to return as a string array with all the values given at the start
        //Note: Edit grocery page gets values back as a string array for other uses later on
        String[] queryValues= new String[3];
        queryValues[0]=selectedId.toString();
        queryValues[1]=selectedName;
        queryValues[2]=selectedQuantity.toString();
        intent.putExtra("queryValues",queryValues);

        //returns back to EditGroceryList page
        startActivity(intent);
    }


    /*
      returnToAddPage()
      - Creates a new intent
      - adds values given to be returned and stored to their EditText field by other activity
      - adds image in the form of byteArray
      - goes to other page

   */
    private void returnToAddPage() {
        Intent intent=new Intent(addImage.this,AddGroceryPage.class);
        //return id back to addPage
        intent.putExtra("selectedId",selectedId);

        if(selectedName!=null){
            //return name back to addPage
            intent.putExtra("selectedName",selectedName);
        }

        if(selectedQuantity!=0){
            //return quantity back to addPage
            intent.putExtra("selectedQuantity",selectedQuantity);
        }
        if(imageInfo.containsKey(selectedId))
        {
            //initiate new byteArray stream
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //compress bitmap into byteArray format
            imageInfo.get(selectedId).compress(Bitmap.CompressFormat.PNG, 100, stream);
            //store it in a new instance of byte[]
            byte[] byteArray = stream.toByteArray();

            //add it into the intent with key "image"
            intent.putExtra("image",byteArray);

        }
        startActivity(intent);
    }


    /*
         insertInfo(Bitmap icon)
         - check if there is an image selected yet
         - if there is: delete bitmap already in hashmap
         - add bitmap to return later

      */
    public void insertInfo(Bitmap icon){

        if(imageInfo.containsKey(selectedId))
        {
            imageInfo.remove(selectedId);
        }
        //if id is alreadt in database, delete the previous id and insert new one
        imageInfo.put(selectedId,icon);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
// Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
// Make sure the request was successful
            if (resultCode == RESULT_OK) {
// The user picked an image. The Intent's data Uri
                //identifies which image was selected.

                Uri imgUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                counter=dataToPush.size();
                dataToPush.put(counter,bitmap);
                extraImages.put(counter,bitmap);
                colourChanged.add(Color.WHITE);

                showImages();
            }
        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            galleryAddPic();


        }
    }




    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(addImage.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(addImage.this,"CAMERA permission allows us to Access CAMERA app",     Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(addImage.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        super.onRequestPermissionsResult(requestCode, permissions, result);
        switch (requestCode) {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(addImage.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(addImage.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



    //Gallery



    static final int REQUEST_TAKE_PHOTO = 1;
    private void dispatchTakePictureIntent() {   //returning result=-1 which may be the problem
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
// Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
// Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
// Error occurred while creating the File
                ex.printStackTrace();
            }
// Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI= FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
// Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );
// Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() { //not adding the image. i.e. not finding the image and importing null
        Intent mediaScanIntent = new
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private void insertImages() {
        //initialise each image and set an onClickListener to display a toast to confirm to user that they have selected it.
        //for each: call insertInfo( image ) to be stored in hashmap for returning.

        Bitmap icon1 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.apple); //look at drawable and name of image
        dataToPush.put(0,icon1);


        Bitmap icon2 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.banana); //look at drawable and name of image
        dataToPush.put(1,icon2);


        Bitmap icon3 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.pineapple); //look at drawable and name of image
        dataToPush.put(2,icon3);

        Bitmap icon5 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.clothes); //look at drawable and name of image

        dataToPush.put(3,icon5);


        Bitmap icon4 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.dragonfruit); //look at drawable and name of image
        dataToPush.put(4,icon4);



        Bitmap icon6 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.durian); //look at drawable and name of image
        dataToPush.put(5,icon6);


        Bitmap icon7 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.fruit); //look at drawable and name of image
        dataToPush.put(6,icon7);



        Bitmap icon8 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.icecream); //look at drawable and name of image
        dataToPush.put(7,icon8);



        Bitmap icon9 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.longan); //look at drawable and name of image
        dataToPush.put(8,icon9);



        Bitmap icon10 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.meat); //look at drawable and name of image
        dataToPush.put(9,icon10);


        Bitmap icon11 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.tools); //look at drawable and name of image
        dataToPush.put(10,icon11);



        Bitmap icon12 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.vegetables); //look at drawable and name of image
        dataToPush.put(11,icon12);

        Bitmap icon13 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.bags); //look at drawable and name of image
        dataToPush.put(12,icon13);

        Bitmap icon14 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.balls); //look at drawable and name of image
        dataToPush.put(13,icon14);

        Bitmap icon15 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.technology); //look at drawable and name of image
        dataToPush.put(14,icon15);


    }

}