package app.equipment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import app.agrishare.BaseActivity;
import app.agrishare.R;
import app.c2.android.Utils;
import app.dao.Service;
import butterknife.BindView;
import butterknife.ButterKnife;

import static app.agrishare.Constants.KEY_ENABLE_TEXT;
import static app.agrishare.Constants.KEY_SERVICE;

public class AddEquipmentActivity extends BaseActivity {

    File file;
    private static int RESULT_LOAD_IMG = 250;
    String imgDecodableString;
    Bitmap photo;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_PERMISSIONS_REQUESTS = 300;
    String mCurrentPhotoPath;
    Uri my_file_uri = null;
    Boolean filehasBeenSelected = false;

    String photo1_base64 = "";
    String photo2_base64 = "";
    String photo3_base64 = "";

    Service ploughing;
    Service discing;
    Service planting;

    int PLOUGHING_REQUEST_CODE = 1000;
    int DISCING_REQUEST_CODE = 1001;
    int PLANTING_REQUEST_CODE = 1002;


    @BindView(R.id.photo1)
    public ImageView photo1_imageview;

    @BindView(R.id.photo2)
    public ImageView photo2_imageview;

    @BindView(R.id.photo3)
    public ImageView photo3_imageview;

    @BindView(R.id.ploughing_switch)
    public Switch ploughing_switch;

    @BindView(R.id.discing_switch)
    public Switch discing_switch;

    @BindView(R.id.planting_switch)
    public Switch planting_switch;

    @BindView(R.id.submit)
    public Button submit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_equipment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Add Equipment", R.drawable.button_back);
        ButterKnife.bind(this);
        context = this;
        initViews();
    }

    private void initViews(){
        ploughing_switch.setEnabled(false);
        discing_switch.setEnabled(false);
        planting_switch.setEnabled(false);

        (findViewById(R.id.ploughing_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    openServiceDetailForm(ploughing, PLOUGHING_REQUEST_CODE, "Enable Ploughing");
                }
            }
        });

        (findViewById(R.id.discing_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    openServiceDetailForm(discing, DISCING_REQUEST_CODE, "Enable Discing");
                }
            }
        });

        (findViewById(R.id.planting_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    openServiceDetailForm(planting, PLANTING_REQUEST_CODE, "Enable Planting");
                }
            }
        });

        (findViewById(R.id.photo1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    selectImage();
                }
            }
        });

        (findViewById(R.id.photo2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    selectImage();
                }
            }
        });

        (findViewById(R.id.photo3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    selectImage();
                }
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                   // checkFields();
                }
            }
        });
    }

    private void openServiceDetailForm(Service service, int request_code, String enable_text){
        closeKeypad();
        Intent intent = new Intent(AddEquipmentActivity.this, ServiceFormActivity.class);
        intent.putExtra(KEY_SERVICE, service);
        intent.putExtra(KEY_ENABLE_TEXT, enable_text);
        startActivityForResult(intent, request_code);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
    }

    private void selectImage(){
        if (ContextCompat.checkSelfPermission(AddEquipmentActivity.this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(AddEquipmentActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log("SELECT IMAGE");
            popImageOptionsAlert();
        } else {
            Log("ASK PERMISSIONS");
            askForPermissions();
        }
    }

    public void askForPermissions(){
        if (ContextCompat.checkSelfPermission(AddEquipmentActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(AddEquipmentActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Log("GO ASK PERMS");
            ActivityCompat.requestPermissions(AddEquipmentActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUESTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    Log.d("PERMISSION GRANTED", "");
                    popImageOptionsAlert();
                } else {
                    Log.d("PERMISSION GRANTED", "NOT");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void popImageOptionsAlert(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddEquipmentActivity.this);
        alertDialogBuilder.setTitle("Add Photo");
        alertDialogBuilder
                .setMessage("Add photo from:")
                .setCancelable(false)
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dispatchTakePictureIntent();
                    }
                })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Create intent to Open Image applications like Gallery, Google Photos
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Start the Intent
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);

    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void recycleBitmap(){
        //fixes OOM on weaker devices
        if(photo!=null)
        {
            photo.recycle();
            photo=null;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("IOExceptions", ""+ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "app.agrishare.provider",
                        photoFile);
                my_file_uri = photoURI;
                giveUriPermission(takePictureIntent, photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    public void giveUriPermission(Intent intent, Uri uri){
        List<ResolveInfo> resolvedIntentActivities = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
            String packageName = resolvedIntentInfo.activityInfo.packageName;

            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = file.getAbsolutePath();
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log("ON ACTIVITY RESULT: " + requestCode);
        if (requestCode == PLOUGHING_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ploughing = data.getParcelableExtra(KEY_SERVICE);
                if (ploughing != null){
                    if (ploughing.enabled)
                        ploughing_switch.setChecked(true);
                    else
                        ploughing_switch.setChecked(false);
                }
            }else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if (requestCode == DISCING_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                discing = data.getParcelableExtra(KEY_SERVICE);
                if (discing != null){
                    if (discing.enabled)
                        discing_switch.setChecked(true);
                    else
                        discing_switch.setChecked(false);
                }
            }else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if (requestCode == PLANTING_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                planting = data.getParcelableExtra(KEY_SERVICE);
                if (planting != null){
                    if (planting.enabled)
                        planting_switch.setChecked(true);
                    else
                        planting_switch.setChecked(false);
                }
            }else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if ((requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) || (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)) {
            // Get the Image from data

            if (requestCode == CAMERA_REQUEST) {
                imgDecodableString = mCurrentPhotoPath;
            } else {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                file = new File(imgDecodableString);
                my_file_uri = Uri.fromFile(file);
            }

            photo = BitmapFactory.decodeFile(imgDecodableString);

            int height = photo.getHeight(), width = photo.getWidth();
            Log.d("PHOTO", "height: " + height + " width: " + width);
            int bigger_side = 0;
            if (height > width)
                bigger_side = height;
            else
                bigger_side = width;

            if (bigger_side > 1600){
                int compression_factor = (int) Math.ceil(bigger_side / 1400.0);  //using 1400 (instead of 1600) and rounding up  coz i'm forced to use compression factor as int instead of double
                Log.d("Compression factor", compression_factor + " bigger side: " + bigger_side);
                recycleBitmap();
                //resize image bitmap
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = compression_factor;
                photo = BitmapFactory.decodeFile(imgDecodableString, options);
                Log.d("PHOTO COMP", "height: " + photo.getHeight() + " width: " + photo.getWidth());
            }


            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imgDecodableString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            photo = Utils.rotateBitmap(photo, orientation);


            if (requestCode == CAMERA_REQUEST){


                try{
                    //=======LETS CREATE NEW FILE FOR THE ROTATED IMAGE====//
                    File f = createImageFile();

                    //Convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    //write the bytes in file
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    file = f;
                    submit_button.setVisibility(View.VISIBLE);
                    showPhotoInUI();
                    filehasBeenSelected = true;
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error bitmap", e);
                    popToast(AddEquipmentActivity.this, "Please retake the photo");
                    filehasBeenSelected = false;
                }
            }
            else {
                showPhotoInUI();
                filehasBeenSelected = true;
            }

        }
    }

    private void showPhotoInUI(){
        if (photo1_base64.isEmpty()){
            convertFileToBase64(0);
         //   photo1_imageview.setImageBitmap(photo);
        }
        else if (photo2_base64.isEmpty()){
            convertFileToBase64(1);
            photo2_imageview.setImageBitmap(photo);
        }
        else if (photo3_base64.isEmpty()){
            convertFileToBase64(2);
            photo3_imageview.setImageBitmap(photo);
        }
    }

    private void convertFileToBase64(int position){
        if (file != null && file.exists() && file.length() > 0) {
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bOut);

            if (position == 0)
                photo1_base64 = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT);
            else if (position == 1)
                photo2_base64 = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT);
            else if (position == 2)
                photo3_base64 = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT);

            //decode: just for testing
          /*  byte[] decodedString = Base64.decode(photo1_base64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            photo1_imageview.setImageBitmap(decodedByte);*/


            Log("BASE 64: " + photo1_base64 );
        }
        else {
            popToast(AddEquipmentActivity.this, "Failed to convert image to base64.");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

}
