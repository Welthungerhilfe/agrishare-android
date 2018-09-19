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
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import app.account.RegisterActivity;
import app.agrishare.BaseActivity;
import app.agrishare.MainActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.c2.android.Utils;
import app.category.CategoryActivity;
import app.dao.Category;
import app.dao.EquipmentService;
import app.dao.Service;
import app.dao.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_CATEGORY;
import static app.agrishare.Constants.KEY_ENABLE_TEXT;
import static app.agrishare.Constants.KEY_EQUIPMENT_SERVICE;
import static app.agrishare.Constants.KEY_IS_LOOKING;

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

    int selected_upload_button = 0;

    EquipmentService ploughing;
    EquipmentService discing;
    EquipmentService planting;

    int PLOUGHING_REQUEST_CODE = 1000;
    int DISCING_REQUEST_CODE = 1001;
    int PLANTING_REQUEST_CODE = 1002;

    int TYPE_REQUEST_CODE = 1100;
    int SERVICE_DETAIL_REQUEST_CODE = 1101;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2000;

    ArrayList<EquipmentService> servicesList;
    EquipmentServiceAdapter adapter;

    Place place;
    Category category;
    int condition_id = 0;


    @BindView(R.id.title)
    public EditText title_edittext;

    @BindView(R.id.additional_information)
    public EditText additional_info_edittext;

    @BindView(R.id.brand)
    public EditText brand_edittext;

    @BindView(R.id.horse_power)
    public EditText horse_power_edittext;

    @BindView(R.id.year)
    public EditText year_edittext;

    @BindView(R.id.cancel1)
    public ImageView cancel1_imageview;

    @BindView(R.id.cancel2)
    public ImageView cancel2_imageview;

    @BindView(R.id.cancel3)
    public ImageView cancel3_imageview;

    @BindView(R.id.photo1)
    public ImageView photo1_imageview;

    @BindView(R.id.photo2)
    public ImageView photo2_imageview;

    @BindView(R.id.photo3)
    public ImageView photo3_imageview;

  /*  @BindView(R.id.ploughing_switch)
    public Switch ploughing_switch;

    @BindView(R.id.discing_switch)
    public Switch discing_switch;

    @BindView(R.id.planting_switch)
    public Switch planting_switch;
*/
    @BindView(R.id.submit)
    public Button submit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_equipment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavBar("Add Equipment", R.drawable.white_close);
        ButterKnife.bind(this);
        context = this;
        initViews();
    }

    private void initViews(){
        listview = findViewById(R.id.list);
        servicesList = new ArrayList<>();

        (findViewById(R.id.type_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    closeKeypad();
                    Intent intent = new Intent(AddEquipmentActivity.this, CategoryActivity.class);
                    startActivityForResult(intent, TYPE_REQUEST_CODE);
                    overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.hold);
                }
            }
        });

        (findViewById(R.id.location_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    closeKeypad();
                    findPlace();
                }
            }
        });

        (findViewById(R.id.condition_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    closeKeypad();

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(AddEquipmentActivity.this, findViewById(R.id.condition));
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_condition_options);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.brand_new:
                                    condition_id = 1;
                                    ((TextView) findViewById(R.id.condition)).setText(getResources().getString(R.string.new_));
                                    ((TextView) findViewById(R.id.condition)).setTextColor(getResources().getColor(android.R.color.black));
                                    break;
                                case R.id.used:
                                    condition_id = 2;
                                    ((TextView) findViewById(R.id.condition)).setText(getResources().getString(R.string.used));
                                    ((TextView) findViewById(R.id.condition)).setTextColor(getResources().getColor(android.R.color.black));
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            }
        });

      /*  (findViewById(R.id.ploughing_container)).setOnClickListener(new View.OnClickListener() {
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
        });*/

        (findViewById(R.id.photo1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    selected_upload_button = 1;
                    selectImage();
                }
            }
        });

        (findViewById(R.id.photo2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    selected_upload_button = 2;
                    selectImage();
                }
            }
        });

        (findViewById(R.id.photo3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    selected_upload_button = 3;
                    selectImage();
                }
            }
        });

        (findViewById(R.id.cancel1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    photo1_base64 = "";
                    photo1_imageview.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
                    cancel1_imageview.setVisibility(View.GONE);
                }
            }
        });

        (findViewById(R.id.cancel2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    photo2_base64 = "";
                    photo2_imageview.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
                    cancel2_imageview.setVisibility(View.GONE);
                }
            }
        });


        (findViewById(R.id.cancel3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    photo3_base64 = "";
                    photo3_imageview.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
                    cancel3_imageview.setVisibility(View.GONE);
                }
            }
        });


        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    checkFields();
                }
            }
        });
    }

    public void findPlace() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            //   .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    private void clearErrors(){
        title_edittext.setError(null);
        additional_info_edittext.setError(null);
        brand_edittext.setError(null);
        horse_power_edittext.setError(null);
        year_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String title = title_edittext.getText().toString();
        String additional_info = additional_info_edittext.getText().toString();
        String brand = brand_edittext.getText().toString();
        String horse_power = horse_power_edittext.getText().toString();
        String year = year_edittext.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (category == null){
            popToast(AddEquipmentActivity.this, "Please select a category.");
            cancel = true;
        }

        if (category != null && category.Id != 3){
            if (TextUtils.isEmpty(horse_power)) {
                horse_power_edittext.setError(getString(R.string.error_field_required));
                focusView = horse_power_edittext;
                cancel = true;
            }
        }

        if (TextUtils.isEmpty(title)) {
            title_edittext.setError(getString(R.string.error_field_required));
            focusView = title_edittext;
            cancel = true;
        }

        if (TextUtils.isEmpty(additional_info)) {
            additional_info_edittext.setError(getString(R.string.error_field_required));
            focusView = additional_info_edittext;
            cancel = true;
        }

        if (TextUtils.isEmpty(brand)) {
            brand_edittext.setError(getString(R.string.error_field_required));
            focusView = brand_edittext;
            cancel = true;
        }

        if (TextUtils.isEmpty(year)) {
            year_edittext.setError(getString(R.string.error_field_required));
            focusView = year_edittext;
            cancel = true;
        }

        if (condition_id == 0){
            popToast(AddEquipmentActivity.this, getResources().getString(R.string.please_specify_condition));
            cancel = true;
        }

        if(servicesList != null && servicesList.size() > 0){
            int size = servicesList.size();
            boolean hasEnabledAtLeastOneService = false;
            for (int i = 0; i < size; i++) {
                if (servicesList.get(i).enabled){
                    hasEnabledAtLeastOneService = true;
                    break;
                }
            }

            if (!hasEnabledAtLeastOneService){
                popToast(AddEquipmentActivity.this, getResources().getString(R.string.please_select_at_least_one_service));
                cancel = true;
            }
        }

        /*if (photo1_base64.isEmpty() && photo2_base64.isEmpty() && photo3_base64.isEmpty()){
            popToast(AddEquipmentActivity.this, getResources().getString(R.string.please_add_at_least_one_photoi));
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            submit_button.setVisibility(View.GONE);
            showLoader("Adding Equipment", "Please wait...");
            HashMap<String, Object> query = new HashMap<String, Object>();
            query.put("Brand", brand);
            query.put("CategoryId",category.Id);
            query.put("ConditionId",condition_id);
            query.put("Latitude", place.getLatLng().latitude);
            query.put("Longitude", place.getLatLng().longitude);
            query.put("Description", additional_info);
          //  query.put("GroupServices", true);
            if (category != null && category.Id != 3)
                query.put("HorsePower", horse_power);
            query.put("Location", place.getName().toString());
            query.put("Title", title);
            query.put("Year", year);

            if(servicesList != null){
                try {
                    JSONArray servicesArray = new JSONArray();
                    int size = servicesList.size();
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            if (servicesList.get(i).enabled) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.accumulate("SubcategoryId", servicesList.get(i).service_id);
                                jsonObject.accumulate("DistanceUnitId", 1);
                                jsonObject.accumulate("MaximumDistance", servicesList.get(i).maximum_distance);
                                jsonObject.accumulate("PricePerQuantityUnit", servicesList.get(i).hire_cost);
                                jsonObject.accumulate("TimePerQuantityUnit", servicesList.get(i).hours_required_per_hectare);
                                jsonObject.accumulate("PricePerDistanceUnit", servicesList.get(i).distance_charge);

                                if (category.Id == 1) {          //Tractors
                                    jsonObject.accumulate("FuelPerQuantityUnit", servicesList.get(i).fuel_cost);
                                    jsonObject.accumulate("QuantityUnitId", 1);
                                    jsonObject.accumulate("TimeUnitId", 1);
                                } else if (category.Id == 2) {         //Lorries
                                    jsonObject.accumulate("TotalVolumeUnit", servicesList.get(i).total_volume_in_tonne);
                                    jsonObject.accumulate("QuantityUnitId", 2);
                                    jsonObject.accumulate("TimeUnitId", 2);
                                } else if (category.Id == 3) {         //Processing
                                    jsonObject.accumulate("Mobile", servicesList.get(i).mobile);
                                    jsonObject.accumulate("QuantityUnitId", 2);
                                    jsonObject.accumulate("TimeUnitId", 3);
                                }
                                servicesArray.put(jsonObject);
                            }
                        }
                        query.put("Services", servicesArray);
                    }
                } catch (JSONException ex){
                    Log("JSONEXception: " + ex.getMessage());
                }
            }
            postAPI("listings/add", query, fetchResponse);
        }
    }


    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("ADD LISTING SUCCESS: "+ result.toString());
            MyApplication.refreshEquipmentTab = true;
            showFeedbackWithButton(R.drawable.feedbacksuccess, "Done", "Your listing has successfully been added.");
            setCloseButton();
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("ADD LISTING ERROR: " + errorMessage);
            hideLoader();
            submit_button.setVisibility(View.VISIBLE);
            popToast(AddEquipmentActivity.this, errorMessage);
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    public void setCloseButton(){
        ((Button) findViewById(R.id.feedback_retry)).setText(getResources().getString(R.string.close));
        findViewById(R.id.feedback_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    close();
                }
            }
        });
    }

    public void openServiceDetailForm(EquipmentService service){
        closeKeypad();

        Intent intent = new Intent(AddEquipmentActivity.this, ServiceFormActivity.class);
        intent.putExtra(KEY_EQUIPMENT_SERVICE, service);
        intent.putExtra(KEY_ENABLE_TEXT, "Enable " + service.title);
        startActivityForResult(intent, SERVICE_DETAIL_REQUEST_CODE);
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
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this, data);
                ((TextView) findViewById(R.id.location)).setText(place.getName());
                ((TextView) findViewById(R.id.location)).setTextColor(getResources().getColor(android.R.color.black));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.d("PLACES RESPONSE ERROR", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if (requestCode == TYPE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
               category = data.getParcelableExtra(KEY_CATEGORY);
               if (category != null){
                   ((TextView) findViewById(R.id.type)).setText(category.Title);
                   ((TextView) findViewById(R.id.type)).setTextColor(getResources().getColor(android.R.color.black));

                   //hide horse-power field for Processing category.
                   if (category.Id == 3){
                       (findViewById(R.id.horse_power_container)).setVisibility(View.GONE);
                   }
                   else {
                       (findViewById(R.id.horse_power_container)).setVisibility(View.VISIBLE);
                   }

                   try {
                       servicesList.clear();
                       JSONArray servicesArray = new JSONArray(category.Services);
                       int size = servicesArray.length();
                       if (size > 0) {
                           for (int i = 0; i < size; i++) {
                               Service service = new Service(servicesArray.optJSONObject(i));
                               EquipmentService equipmentService = new EquipmentService(service, category.Id);
                               servicesList.add(equipmentService);
                           }
                           (findViewById(R.id.services_label)).setVisibility(View.VISIBLE);
                       }
                       else {
                           (findViewById(R.id.services_label)).setVisibility(View.GONE);
                       }

                       adapter = new EquipmentServiceAdapter(AddEquipmentActivity.this, servicesList, AddEquipmentActivity.this);
                       listview.setAdapter(adapter);
                       Utils.setListViewHeightBasedOnChildren(listview);

                   } catch (JSONException ex){
                       Log("JSONException : " + ex.getMessage());
                   }
               }
            }else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if (requestCode == SERVICE_DETAIL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                EquipmentService equipmentService = data.getParcelableExtra(KEY_EQUIPMENT_SERVICE);
                if (equipmentService != null){
                    for (int i = 0; i < servicesList.size(); i++){
                        if (servicesList.get(i).service_id == equipmentService.service_id){
                            servicesList.get(i).update(equipmentService);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        /*else if (requestCode == PLOUGHING_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ploughing = data.getParcelableExtra(KEY_EQUIPMENT_SERVICE);
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
                discing = data.getParcelableExtra(KEY_EQUIPMENT_SERVICE);
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
                planting = data.getParcelableExtra(KEY_EQUIPMENT_SERVICE);
                if (planting != null){
                    if (planting.enabled)
                        planting_switch.setChecked(true);
                    else
                        planting_switch.setChecked(false);
                }
            }else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }*/
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
            photo1_imageview.setImageBitmap(photo);
            cancel1_imageview.setVisibility(View.VISIBLE);
        }
        else if (photo2_base64.isEmpty()){
            convertFileToBase64(1);
            photo2_imageview.setImageBitmap(photo);
            cancel2_imageview.setVisibility(View.VISIBLE);
        }
        else if (photo3_base64.isEmpty()){
            convertFileToBase64(2);
            photo3_imageview.setImageBitmap(photo);
            cancel3_imageview.setVisibility(View.VISIBLE);
        }
        else {
            if (selected_upload_button == 1){
                convertFileToBase64(0);
                photo1_imageview.setImageBitmap(photo);
                cancel1_imageview.setVisibility(View.VISIBLE);
            }
            else if (selected_upload_button == 2) {
                convertFileToBase64(1);
                photo2_imageview.setImageBitmap(photo);
                cancel2_imageview.setVisibility(View.VISIBLE);
            }
            else if (selected_upload_button == 3) {
                convertFileToBase64(2);
                photo3_imageview.setImageBitmap(photo);
                cancel3_imageview.setVisibility(View.VISIBLE);
            }
            selected_upload_button = 0;
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
                close();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        close();
    }

}
