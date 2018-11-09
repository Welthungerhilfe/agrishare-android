package app.equipment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

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
import app.agrishare.BaseFragment;
import app.agrishare.MainActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.c2.android.Utils;
import app.calendar.CalendarActivity;
import app.category.CategoryActivity;
import app.category.CategoryAdapter;
import app.dao.Category;
import app.dao.EquipmentService;
import app.dao.Listing;
import app.dao.ListingDetailService;
import app.dao.Location;
import app.dao.Service;
import app.dao.User;
import app.database.Categories;
import app.map.MapActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import okhttp3.Response;

import static app.agrishare.Constants.KEY_CATEGORY;
import static app.agrishare.Constants.KEY_CATEGORY_ID;
import static app.agrishare.Constants.KEY_EDIT;
import static app.agrishare.Constants.KEY_ENABLE_TEXT;
import static app.agrishare.Constants.KEY_EQUIPMENT_SERVICE;
import static app.agrishare.Constants.KEY_IS_LOOKING;
import static app.agrishare.Constants.KEY_LISTING;
import static app.agrishare.Constants.KEY_LOCATION;

public class AddEquipmentActivity extends BaseActivity {

    Location selectedLocation;
    int mobile = 0;
    EquipmentService selectedSpinnerTypeService;

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
    String encodedImage_prefix = "data:image/jpg;base64,";

    //for edit
    String photo1_file_name = "";
    String photo2_file_name = "";
    String photo3_file_name = "";

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
    final int MY_LOCATION_PERMISSIONS_REQUEST = 2001;
    final int CHOOSE_LOCATION_FROM_MAP_REQUEST_CODE = 2002;

    ArrayList<EquipmentService> servicesList;
    EquipmentServiceAdapter adapter;

    Listing listing;
    Place place;
    PlaceLikelihoodBufferResponse likelyPlaces;
    Category category;
    int condition_id = 0;

    boolean editMode = false;
    ArrayList<EquipmentService> myservicesList;

    //for add screen. doesn't apply to edit...
    Boolean userHasSelectedServiceTypeSpinnerAtLeastOnce = false;

    long service_id_for_selected_processing_service_in_editmode = 0;


    @BindView(R.id.service_type)
    public MaterialSpinner service_type_spinner;

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

    @BindView(R.id.submit)
    public Button submit_button;

    @BindView(R.id.fuel_unit_textview)
    public TextView fuel_unit_textview;

    @BindView(R.id.time_unit_textview)
    public TextView time_unit_textview;

    @BindView(R.id.dollar_per_unit)
    public TextView dollar_per_unit_textview;

    @BindView(R.id.unit)
    public TextView unit_textview;

    @BindView(R.id.mobile_container)
    public RelativeLayout mobile_container;

    @BindView(R.id.total_volume)
    public EditText total_volume_edittext;

    @BindView(R.id.hours_required_per_hectare)
    public EditText hours_required_per_hectare_edittext;

    @BindView(R.id.hire_cost)
    public EditText hire_cost_edittext;

    @BindView(R.id.fuel_cost)
    public EditText fuel_cost_edittext;

    @BindView(R.id.minimum_quantity)
    public EditText minimum_quantity_edittext;

    @BindView(R.id.distance_charge)
    public EditText distance_charge_edittext;

    @BindView(R.id.distance_charge2)
    public EditText distance_charge_edittext2;

    @BindView(R.id.maximum_distance)
    public EditText maximum_distance_edittext;


    //form label containers
    @BindView(R.id.type_of_equipment_label_container)
    public RelativeLayout type_of_equipment_label_container;

    @BindView(R.id.service_label_container)
    public RelativeLayout service_label_container;

    @BindView(R.id.title_label_container)
    public RelativeLayout title_label_container;

    @BindView(R.id.additional_information_label_container)
    public RelativeLayout additional_information_label_container;

    @BindView(R.id.location_label_container)
    public RelativeLayout location_label_container;

    @BindView(R.id.allow_group_hire_label_container)
    public RelativeLayout allow_group_hire_label_container;

    @BindView(R.id.brand_label_container)
    public RelativeLayout brand_label_container;

    @BindView(R.id.horse_power_label_container)
    public RelativeLayout horse_power_label_container;

    @BindView(R.id.year_label_container)
    public RelativeLayout year_label_container;

    @BindView(R.id.condition_label_container)
    public RelativeLayout condition_label_container;

    @BindView(R.id.available_without_fuel_label_container)
    public RelativeLayout available_without_fuel_label_container;


    //service Labels
    @BindView(R.id.hours_required_per_hectare_label)
    public TextView hours_required_per_hectare_label;

    @BindView(R.id.minimum_quantity_label)
    public TextView minimum_quantity_label;

    //service Label containers
    @BindView(R.id.is_service_mobile_label_container)
    public RelativeLayout is_service_mobile_label_container;

    @BindView(R.id.total_volume_label_container)
    public RelativeLayout total_volume_label_container;

    @BindView(R.id.hours_required_per_hectare_label_container)
    public RelativeLayout hours_required_per_hectare_label_container;

    @BindView(R.id.hire_cost_label_container)
    public RelativeLayout hire_cost_label_container;

    @BindView(R.id.fuel_cost_label_container)
    public RelativeLayout fuel_cost_label_container;

    @BindView(R.id.minimum_quantity_label_container)
    public RelativeLayout minimum_quantity_label_container;

    @BindView(R.id.distance_charge_label_container)
    public RelativeLayout distance_charge_label_container;

    @BindView(R.id.maximum_distance_label_container)
    public RelativeLayout maximum_distance_label_container;

    @BindView(R.id.distance_charge_label_container2)
    public RelativeLayout distance_charge_label_container2;


    //Tool tips

    @BindView(R.id.title_tool_tip)
    public ImageView title_tool_tip;

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
        ((ScrollView) findViewById(R.id.scrollView)).post(new Runnable() {
            @Override
            public void run() {
                View view = getCurrentFocus();
                if (view != null)
                    ((ScrollView) findViewById(R.id.scrollView)).scrollTo(0, view.getBottom());
            }
        });
        listview = findViewById(R.id.list);
        servicesList = new ArrayList<>();
        (findViewById(R.id.type_spinner_container)).setVisibility(View.GONE);
        service_label_container.setVisibility(View.GONE);

        (findViewById(R.id.location)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showLocationsPopupMenu();
                }
            }
        });

        (findViewById(R.id.location_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showLocationsPopupMenu();
                }
            }
        });

        //HIDE FOR NOW.. I THINK..
        condition_label_container.setVisibility(View.GONE);
        (findViewById(R.id.condition_container)).setVisibility(View.GONE);


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
                                case R.id.very_good:
                                    condition_id = 1;
                                    ((TextView) findViewById(R.id.condition)).setText(getResources().getString(R.string.very_good));
                                    ((TextView) findViewById(R.id.condition)).setTextColor(getResources().getColor(android.R.color.black));
                                    break;
                                case R.id.good:
                                    condition_id = 2;
                                    ((TextView) findViewById(R.id.condition)).setText(getResources().getString(R.string.good));
                                    ((TextView) findViewById(R.id.condition)).setTextColor(getResources().getColor(android.R.color.black));
                                    break;
                                case R.id.fair:
                                    condition_id = 3;
                                    ((TextView) findViewById(R.id.condition)).setText(getResources().getString(R.string.fair));
                                    ((TextView) findViewById(R.id.condition)).setTextColor(getResources().getColor(android.R.color.black));
                                    break;
                                case R.id.poor:
                                    condition_id = 4;
                                    ((TextView) findViewById(R.id.condition)).setText(getResources().getString(R.string.poor));
                                    ((TextView) findViewById(R.id.condition)).setTextColor(getResources().getColor(android.R.color.black));
                                    break;
                                case R.id.very_poor:
                                    condition_id = 5;
                                    ((TextView) findViewById(R.id.condition)).setText(getResources().getString(R.string.very_poor));
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

        (findViewById(R.id.mobile_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    closeKeypad();
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(AddEquipmentActivity.this, findViewById(R.id.mobile_container));
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_mobile_options);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.yes:
                                    mobile = 1;
                                    ((TextView) findViewById(R.id.mobile)).setText(getResources().getString(R.string.yes_is_mobile));
                                    if (category != null && category.Id !=3) {
                                        (findViewById(R.id.fuel_container)).setVisibility(View.VISIBLE);
                                        fuel_cost_label_container.setVisibility(View.VISIBLE);
                                    }

                                    if (category != null && category.Id == 3){
                                        distance_charge_label_container2.setVisibility(View.VISIBLE);
                                        (findViewById(R.id.distance_charge_container2)).setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        distance_charge_label_container2.setVisibility(View.GONE);
                                        (findViewById(R.id.distance_charge_container2)).setVisibility(View.GONE);
                                    }
                                    break;
                                case R.id.no:
                                    mobile = 2;
                                    ((TextView) findViewById(R.id.mobile)).setText(getResources().getString(R.string.no_it_is_not_mobile));
                                    (findViewById(R.id.fuel_container)).setVisibility(View.GONE);
                                    fuel_cost_label_container.setVisibility(View.GONE);


                                    distance_charge_label_container2.setVisibility(View.GONE);
                                    (findViewById(R.id.distance_charge_container2)).setVisibility(View.GONE);
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
                    photo1_file_name = "";
                    photo1_base64 = "";
                    photo1_imageview.setImageDrawable(getResources().getDrawable(R.drawable.add_box_400));
                    cancel1_imageview.setVisibility(View.GONE);
                }
            }
        });

        (findViewById(R.id.cancel2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    photo2_file_name = "";
                    photo2_base64 = "";
                    photo2_imageview.setImageDrawable(getResources().getDrawable(R.drawable.add_box_400));
                    cancel2_imageview.setVisibility(View.GONE);
                }
            }
        });


        (findViewById(R.id.cancel3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    photo3_file_name = "";
                    photo3_base64 = "";
                    photo3_imageview.setImageDrawable(getResources().getDrawable(R.drawable.add_box_400));
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

        if (getIntent().hasExtra(KEY_EDIT)){
            if (getIntent().getBooleanExtra(KEY_EDIT, false)){
                editMode = true;
                setNavBar("Edit Details", R.drawable.white_close);

                (findViewById(R.id.type_container)).setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
                (findViewById(R.id.type_spinner_container)).setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
                service_type_spinner.setEnabled(false);

                listing = getIntent().getParcelableExtra(KEY_LISTING);

                category = listing.Category;

                if (category.Services.isEmpty()){
                    RealmResults<Categories> results = MyApplication.realm.where(Categories.class).equalTo("Id", category.Id).findAll();
                    int size = results.size();
                    if (size > 0) {
                        category = new Category(results.get(0));
                    }
                }

                setupSelectedCategory();

                submit_button.setText(getResources().getString(R.string.update));
                title_edittext.setText(listing.Title);
                title_edittext.setTextColor(getResources().getColor(android.R.color.black));
                additional_info_edittext.setText(listing.Description);
                ((Switch) findViewById(R.id.allow_group_hire_switch)).setChecked(listing.GroupServices);
                ((Switch) findViewById(R.id.available_without_fuel_switch)).setChecked(listing.AvailableWithoutFuel);


                ((TextView) findViewById(R.id.location)).setText(listing.Location);
                ((TextView) findViewById(R.id.location)).setTextColor(getResources().getColor(android.R.color.black));

                brand_edittext.setText(listing.Brand);
                horse_power_edittext.setText(String.valueOf(listing.HorsePower));
                year_edittext.setText(String.valueOf(listing.Year));

              /*  condition_id = listing.ConditionId;
                ((TextView) findViewById(R.id.condition)).setText(listing.Condition);
                ((TextView) findViewById(R.id.condition)).setTextColor(getResources().getColor(android.R.color.black));
*/
                try {
                    JSONArray jsonArray = new JSONArray(listing.Photos);
                    if (jsonArray.length() > 0) {
                        photo1_file_name = jsonArray.optJSONObject(0).optString("Filename");
                        Picasso.get()
                                .load(jsonArray.optJSONObject(0).optString("Thumb"))
                                .placeholder(R.drawable.default_image)
                                .into(photo1_imageview);
                        cancel1_imageview.setVisibility(View.VISIBLE);

                        if (jsonArray.length() > 1) {
                            photo2_file_name = jsonArray.optJSONObject(1).optString("Filename");
                            Picasso.get()
                                    .load(jsonArray.optJSONObject(1).optString("Thumb"))
                                    .placeholder(R.drawable.default_image)
                                    .into(photo2_imageview);
                            cancel2_imageview.setVisibility(View.VISIBLE);


                            if (jsonArray.length() > 2) {
                                photo3_file_name = jsonArray.optJSONObject(2).optString("Filename");
                                Picasso.get()
                                        .load(jsonArray.optJSONObject(2).optString("Thumb"))
                                        .placeholder(R.drawable.default_image)
                                        .into(photo3_imageview);
                                cancel3_imageview.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                } catch (JSONException ex){
                    Log("JSONException: " + ex.getMessage());
                }


            }
        }
        else {
            //hide labels if adding equipment
           // hideAllFormLabels();
          //  hideAllServiceFormLabels();
            simulateCategorySelection();


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

            (findViewById(R.id.type)).setOnClickListener(new View.OnClickListener() {
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
        }

        setToolTipListeners();
    }

    private void simulateCategorySelection(){
        if (getIntent().hasExtra(KEY_CATEGORY_ID)){
            long category_id = getIntent().getLongExtra(KEY_CATEGORY_ID, 0);

            if (category_id != 0) {
                RealmResults<Categories> results = MyApplication.realm.where(Categories.class)
                        .findAll();

                int size = results.size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        Category category = new Category(results.get(i));
                        if (category.Id == category_id){
                            this.category = category;
                            setupSelectedCategory();
                            break;
                        }
                    }
                }
            }
        }
    }

    private void setToolTipListeners(){
        title_label_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (category != null) {
                        if (category.Id == 1)
                            showToolTip(getResources().getString(R.string.title), getResources().getString(R.string.tractor_title_tooltip), AddEquipmentActivity.this);
                        else if (category.Id == 2)
                            showToolTip(getResources().getString(R.string.title), getResources().getString(R.string.lorry_title_tooltip), AddEquipmentActivity.this);
                        else if (category.Id == 3)
                            showToolTip(getResources().getString(R.string.title), getResources().getString(R.string.processing_title_tooltip), AddEquipmentActivity.this);
                    }
                    else {
                        popToast(AddEquipmentActivity.this, getString(R.string.select_category_first_to_view_tooltip));
                    }
                }
            }
        });

        additional_information_label_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (category != null) {
                        if (category.Id == 1)
                            showToolTip(getResources().getString(R.string.additional_information), getResources().getString(R.string.tractor_additional_info_tooltip), AddEquipmentActivity.this);
                        else if (category.Id == 2)
                            showToolTip(getResources().getString(R.string.additional_information), getResources().getString(R.string.lorry_additional_info_tooltip), AddEquipmentActivity.this);
                        else if (category.Id == 3)
                            showToolTip(getResources().getString(R.string.additional_information), getResources().getString(R.string.processor_additional_info_tooltip), AddEquipmentActivity.this);
                    }
                    else {
                        popToast(AddEquipmentActivity.this, getString(R.string.select_category_first_to_view_tooltip));
                    }
                }
            }
        });

        distance_charge_label_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showToolTip(getResources().getString(R.string.distance_charge), getResources().getString(R.string.distance_charge_tooltip), AddEquipmentActivity.this);
                }
            }
        });

        distance_charge_label_container2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showToolTip(getResources().getString(R.string.distance_charge), getResources().getString(R.string.distance_charge_tooltip), AddEquipmentActivity.this);
                }
            }
        });

        fuel_cost_label_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (category != null) {
                        if (category.Id == 1)
                            showToolTip(getResources().getString(R.string.fuel_cost), getResources().getString(R.string.tractor_fuel_cost_tooltip), AddEquipmentActivity.this);
                        else if (category.Id == 2)
                            showToolTip(getResources().getString(R.string.fuel_cost), getResources().getString(R.string.fuel_cost_tooltip), AddEquipmentActivity.this);
                    }
                    else {
                        popToast(AddEquipmentActivity.this, getString(R.string.select_category_first_to_view_tooltip));
                    }
                }
            }
        });

        (findViewById(R.id.location_label_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showToolTip(getResources().getString(R.string.location), getResources().getString(R.string.location_tooltip), AddEquipmentActivity.this);
                }
            }
        });

        (findViewById(R.id.allow_group_hire_label_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (category != null) {
                        if (category.Id == 1)
                            showToolTip(getResources().getString(R.string.allow_group_hire), getResources().getString(R.string.tractor_group_hire_tooltip), AddEquipmentActivity.this);
                        else if (category.Id == 2)
                            showToolTip(getResources().getString(R.string.allow_group_hire), getResources().getString(R.string.lorry_group_hire_tooltip), AddEquipmentActivity.this);
                        else if (category.Id == 3)
                            showToolTip(getResources().getString(R.string.allow_group_hire), getResources().getString(R.string.processor_group_hire_tooltip), AddEquipmentActivity.this);
                    }
                    else {
                        popToast(AddEquipmentActivity.this, getString(R.string.select_category_first_to_view_tooltip));
                    }
                }
            }
        });

        (findViewById(R.id.maximum_distance_label_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showToolTip(getResources().getString(R.string.maximum_distance), getResources().getString(R.string.max_distance_tooltip), AddEquipmentActivity.this);
                }
            }
        });



        (findViewById(R.id.brand_label_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showToolTip(getResources().getString(R.string.brand), getResources().getString(R.string.brand_tooltip), AddEquipmentActivity.this);
                }
            }
        });

        (findViewById(R.id.horse_power_label_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showToolTip(getResources().getString(R.string.horse_power), getResources().getString(R.string.horse_power_tooltip), AddEquipmentActivity.this);
                }
            }
        });

        (findViewById(R.id.year_label_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showToolTip(getResources().getString(R.string.year), getResources().getString(R.string.year_manufactured_tooltip), AddEquipmentActivity.this);
                }
            }
        });





        (findViewById(R.id.is_service_mobile_label_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showToolTip(getResources().getString(R.string.mobile), getResources().getString(R.string.mobile_tooltip), AddEquipmentActivity.this);
                }
            }
        });

        (findViewById(R.id.total_volume_label_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showToolTip(getResources().getString(R.string.total_volume), getResources().getString(R.string.how_many_tons_can_lorry_carry_tooltip), AddEquipmentActivity.this);
                }
            }
        });

        (findViewById(R.id.hours_required_per_hectare_label_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (category != null) {
                        if (category.Id == 2)
                            showToolTip(getResources().getString(R.string.time), getResources().getString(R.string.how_long_does_your_lorry_take_for_100k_tooltip), AddEquipmentActivity.this);
                        else
                            showToolTip(getResources().getString(R.string.time), getResources().getString(R.string.processor_bags_per_hour_tooltip), AddEquipmentActivity.this);
                    }
                    else {
                        popToast(AddEquipmentActivity.this, getString(R.string.select_category_first_to_view_tooltip));
                    }

                }
            }
        });

        (findViewById(R.id.hire_cost_label_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (category != null) {
                        if (category.Id == 2)
                            showToolTip(getResources().getString(R.string.hire_cost), getResources().getString(R.string.this_is_how_much_you_charge_for_the_load_on_your_lorry_tooltip), AddEquipmentActivity.this);
                        else
                            showToolTip(getResources().getString(R.string.hire_cost), getResources().getString(R.string.processor_how_much_do_you_charge_per_bag_tooltip), AddEquipmentActivity.this);
                    }
                    else {
                        popToast(AddEquipmentActivity.this, getString(R.string.select_category_first_to_view_tooltip));
                    }
                }
            }
        });


        fuel_cost_label_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (category != null) {
                        if (category.Id == 2)
                            showToolTip(getResources().getString(R.string.fuel_cost), getResources().getString(R.string.how_much_do_you_charge_per_k_tooltip), AddEquipmentActivity.this);
                        else
                            showToolTip(getResources().getString(R.string.fuel_cost), getResources().getString(R.string.processor_fuel_cost_tooltip), AddEquipmentActivity.this);
                    }
                    else {
                        popToast(AddEquipmentActivity.this, getString(R.string.select_category_first_to_view_tooltip));
                    }

                }
            }
        });

        minimum_quantity_label_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    showToolTip(getResources().getString(R.string.minimum_field_size), getResources().getString(R.string.tractor_minimum_field_size_tooltip), AddEquipmentActivity.this);
                }
            }
        });
    }

    private void hideAllFormLabels(){
        //hides all the common form labels
        type_of_equipment_label_container.setVisibility(View.GONE);
        service_label_container.setVisibility(View.GONE);
        title_label_container.setVisibility(View.GONE);
        additional_information_label_container.setVisibility(View.GONE);
        location_label_container.setVisibility(View.GONE);
        allow_group_hire_label_container.setVisibility(View.GONE);
        brand_label_container.setVisibility(View.GONE);
        horse_power_label_container.setVisibility(View.GONE);
        year_label_container.setVisibility(View.GONE);
        condition_label_container.setVisibility(View.GONE);
        distance_charge_label_container.setVisibility(View.GONE);
        maximum_distance_label_container.setVisibility(View.GONE);
        available_without_fuel_label_container.setVisibility(View.GONE);
    }

    private void hideAllServiceFormLabels(){
        //hides all the service form labels for lorries and processing
        is_service_mobile_label_container.setVisibility(View.GONE);
        total_volume_label_container.setVisibility(View.GONE);
        hours_required_per_hectare_label_container.setVisibility(View.GONE);
        hire_cost_label_container.setVisibility(View.GONE);
        fuel_cost_label_container.setVisibility(View.GONE);
        minimum_quantity_label_container.setVisibility(View.GONE);
    }


    public int convertDPtoPx(int dp_value){
        final float scale = getResources().getDisplayMetrics().density;
        int value_in_px = (int) (dp_value * scale + 0.5f);
        return value_in_px;
    }

    private void setTypeSpinner(EquipmentService equipmentServiceToSetInSpinnerDuringEditMode){

        if (category.Id == 3 && servicesList != null && servicesList.size() > 0){

            service_label_container.setVisibility(View.VISIBLE); //
            (findViewById(R.id.type_spinner_container)).setVisibility(View.VISIBLE);
            service_type_spinner.setHintTextColor(getResources().getColor(R.color.black_grey));
            service_type_spinner.setTextColor(getResources().getColor(android.R.color.black));
            if (editMode)
                service_type_spinner.setBackground(getResources().getDrawable(R.drawable.round_corner_light_grey_bg));
            else
                service_type_spinner.setBackground(getResources().getDrawable(R.drawable.round_corner_white_bg));
            service_type_spinner.setPadding(convertDPtoPx(36), 0, MyApplication.custom_spinner_right_padding, 0);
            service_type_spinner.setTextSize(16);

            myservicesList = servicesList;
            if(!getIntent().getBooleanExtra(KEY_EDIT, false)){
                EquipmentService _species = new EquipmentService(true);
                myservicesList.add(0, _species);
            }

            service_type_spinner.setItems(myservicesList);
            if (editMode){
                int position_to_set = 0;
                int size = myservicesList.size();
                for (int i = 0; i < size; i++){
                    if (myservicesList.get(i).service_id == equipmentServiceToSetInSpinnerDuringEditMode.service_id){
                        position_to_set = i;
                        selectedSpinnerTypeService = myservicesList.get(i);
                        break;
                    }
                }
                service_type_spinner.setSelectedIndex(position_to_set);
            }
            if(!getIntent().getBooleanExtra(KEY_EDIT, false)) {
                userHasSelectedServiceTypeSpinnerAtLeastOnce = false;
                service_type_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<EquipmentService>() {

                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, EquipmentService item) {
                        if (!userHasSelectedServiceTypeSpinnerAtLeastOnce) {
                            userHasSelectedServiceTypeSpinnerAtLeastOnce = true;
                            myservicesList.remove(0);
                            service_type_spinner.setItems(myservicesList);
                            if (position > 0)
                                service_type_spinner.setSelectedIndex(position - 1);
                            selectedSpinnerTypeService = item;

                        }
                       // checkIfFormIsReady();
                    }
                });
            }
            else {
                //in edit mode
                service_type_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<EquipmentService>() {

                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, EquipmentService item) {
                        selectedSpinnerTypeService = item;
                    }
                });
            }


        } else {
            (findViewById(R.id.type_spinner_container)).setVisibility(View.GONE);
            service_label_container.setVisibility(View.GONE);
        }
    }

    private void checkIfLocationServicesIsEnabled(){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
        else {
            attemptFetchCurrentLocation();
        }
    }

    private void showLocationsPopupMenu(){
        closeKeypad();
        //creating a popup menu
        PopupMenu popup = new PopupMenu(AddEquipmentActivity.this, findViewById(R.id.location_arrow));
        popup.inflate(R.menu.menu_location_options);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.use_current_location:
                        checkIfLocationServicesIsEnabled();
                        break;
                    case R.id.find_location:
                      //  findPlace();

                        Intent intent = new Intent(AddEquipmentActivity.this, MapActivity.class);
                        startActivityForResult(intent, CHOOSE_LOCATION_FROM_MAP_REQUEST_CODE);
                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
                        break;
                }
                return false;
            }
        });
        //displaying the popup
        popup.show();
    }

    private void attemptFetchCurrentLocation(){
        if (ContextCompat.checkSelfPermission(AddEquipmentActivity.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(AddEquipmentActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            askForMyLocationPermissions();
        }
    }

    public void askForMyLocationPermissions(){
        if (ContextCompat.checkSelfPermission(AddEquipmentActivity.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(AddEquipmentActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)  {

            ActivityCompat.requestPermissions(AddEquipmentActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_PERMISSIONS_REQUEST);
        }
    }


    public void findPlace() {
        try {
        //    AutocompleteFilter countryFilter = new AutocompleteFilter.Builder().setCountry("ZW").build();  //limit locations to Zim only
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                           // .setFilter(countryFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    public void getCurrentLocation(){
        showFetchingLocationTextView();
        PlaceDetectionClient placeDetectionClient = Places.getPlaceDetectionClient(AddEquipmentActivity.this, null);
        Task<PlaceLikelihoodBufferResponse> placeResult = placeDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                Log("AGRISHARE PLACE GET CURRENT: COMPLETE" + task.isSuccessful() + " - " +  task.isComplete() + " - " +  task.isCanceled());
                if (task.isSuccessful()) {
                    likelyPlaces = task.getResult();
                    float highest_likelihood = 0;
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        Log.d("AGRISHARE PLACES", String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                        if (placeLikelihood.getLikelihood() > highest_likelihood) {
                            place = placeLikelihood.getPlace();
                            highest_likelihood = placeLikelihood.getLikelihood();
                        }
                    }
                    updateSelectedLocationTextView();
                    //   likelyPlaces.release();
                }
                else {
                    Toast.makeText(AddEquipmentActivity.this, getResources().getString(R.string.failed_to_fetch_current_location), Toast.LENGTH_LONG).show();
                    place = null;
                    resetLocationTextView();
                }

            }

        });

    }

    private void resetLocationTextView(){
        ((TextView) findViewById(R.id.location)).setText(getResources().getString(R.string.location));
        ((TextView) findViewById(R.id.location)).setTextColor(getResources().getColor(R.color.grey_for_text));
    }

    private void showFetchingLocationTextView(){
        ((TextView) findViewById(R.id.location)).setText(getResources().getString(R.string.fetching_current_location));
        ((TextView) findViewById(R.id.location)).setTextColor(getResources().getColor(R.color.grey_for_text));
    }

    private void updateSelectedLocationTextView(){
        ((TextView) findViewById(R.id.location)).setText(place.getName());
        ((TextView) findViewById(R.id.location)).setTextColor(getResources().getColor(android.R.color.black));
        selectedLocation = null;
    }

    private void showFetchingLocationFromMapTextView(){
        ((TextView) findViewById(R.id.location)).setText(getResources().getString(R.string.fetching_location_details));
        ((TextView) findViewById(R.id.location)).setTextColor(getResources().getColor(R.color.grey_for_text));
    }

    private void showFetchingLocationFromMapFailedTextView(){
        ((TextView) findViewById(R.id.location)).setText(getResources().getString(R.string.failed_to_fetch_location_details));
        ((TextView) findViewById(R.id.location)).setTextColor(getResources().getColor(R.color.grey_for_text));
    }

    private void showFetchingLocationTitleFromGoogleApiFailedTextView(){
        ((TextView) findViewById(R.id.location)).setText(getResources().getString(R.string.location_successfully_marked));
        ((TextView) findViewById(R.id.location)).setTextColor(getResources().getColor(android.R.color.black));
    }

    private void updateSelectedLocationTextViewFromMapData(){
        ((TextView) findViewById(R.id.location)).setText(selectedLocation.Title);
        ((TextView) findViewById(R.id.location)).setTextColor(getResources().getColor(android.R.color.black));
        place = null;
    }

    private void clearErrors(){
        title_edittext.setError(null);
        additional_info_edittext.setError(null);
        brand_edittext.setError(null);
        horse_power_edittext.setError(null);
        year_edittext.setError(null);

        total_volume_edittext.setError(null);
        hours_required_per_hectare_edittext.setError(null);
        hire_cost_edittext.setError(null);
        fuel_cost_edittext.setError(null);
        minimum_quantity_edittext.setError(null);
        distance_charge_edittext.setError(null);
        maximum_distance_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String title = title_edittext.getText().toString();
        String additional_info = additional_info_edittext.getText().toString();
        String brand = brand_edittext.getText().toString();
        String horse_power = horse_power_edittext.getText().toString();
        String year = year_edittext.getText().toString();

        String distance_charge = distance_charge_edittext.getText().toString();
        String distance_charge2 = distance_charge_edittext2.getText().toString();
        String maximum_distance = maximum_distance_edittext.getText().toString();

        String hours_required_per_hectare = hours_required_per_hectare_edittext.getText().toString();
        String hire_cost = hire_cost_edittext.getText().toString();
        String fuel_cost = fuel_cost_edittext.getText().toString();
        String minimum_field_size = minimum_quantity_edittext.getText().toString();
        String total_volume = total_volume_edittext.getText().toString();

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

       /* if (TextUtils.isEmpty(additional_info)) {
            additional_info_edittext.setError(getString(R.string.error_field_required));
            focusView = additional_info_edittext;
            cancel = true;
        }
*/
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

        if (category.Id == 3) {
            if (mobile == 1) {
                if (TextUtils.isEmpty(distance_charge2)) {
                    distance_charge_edittext2.setError(getString(R.string.error_field_required));
                    focusView = distance_charge_edittext2;
                    cancel = true;
                } else {
                    double distanceCharge = Double.valueOf(distance_charge2);
                    if (distanceCharge > 5) {
                        distance_charge_edittext2.setError(getString(R.string.error_distance_charge_too_high));
                        focusView = distance_charge_edittext2;
                        cancel = true;
                    }
                }

                distance_charge = distance_charge2;
            }
        }
        else {
            if (TextUtils.isEmpty(distance_charge)) {
                distance_charge_edittext.setError(getString(R.string.error_field_required));
                focusView = distance_charge_edittext;
                cancel = true;
            }
            else {
                double distanceCharge = Double.valueOf(distance_charge);
                if (distanceCharge > 5){
                    distance_charge_edittext.setError(getString(R.string.error_distance_charge_too_high));
                    focusView = distance_charge_edittext;
                    cancel = true;
                }
            }
        }


        if (TextUtils.isEmpty(maximum_distance)) {
            maximum_distance_edittext.setError(getString(R.string.error_field_required));
            focusView = maximum_distance_edittext;
            cancel = true;
        }
        else {
            double max_distance = Double.valueOf(maximum_distance);
            if (max_distance < 10){
                maximum_distance_edittext.setError(getString(R.string.minimum_distance_required_is_10));
                focusView = maximum_distance_edittext;
                cancel = true;
            }
        }

       /* if (condition_id == 0){
            popToast(AddEquipmentActivity.this, getResources().getString(R.string.please_specify_condition));
            cancel = true;
        }*/

        if (category != null && category.Id == 1) {
            if (servicesList != null && servicesList.size() > 0) {
                int size = servicesList.size();
                boolean hasEnabledAtLeastOneService = false;
                for (int i = 0; i < size; i++) {
                    if (servicesList.get(i).enabled) {
                        hasEnabledAtLeastOneService = true;
                        break;
                    }
                }

                if (!hasEnabledAtLeastOneService) {
                    popToast(AddEquipmentActivity.this, getResources().getString(R.string.please_select_at_least_one_service));
                    cancel = true;
                }
            }
        }

        if (editMode){
            if (photo1_base64.isEmpty() && photo2_base64.isEmpty() && photo3_base64.isEmpty()
                    && photo1_file_name.isEmpty() && photo2_file_name.isEmpty() && photo3_file_name.isEmpty()) {
                popToast(AddEquipmentActivity.this, getResources().getString(R.string.please_add_at_least_one_photoi));
                cancel = true;
            }
        }
        else {
            if (category != null && category.Id == 3 && !userHasSelectedServiceTypeSpinnerAtLeastOnce){
                popToast(AddEquipmentActivity.this, getResources().getString(R.string.please_select_a_service_type));
                cancel = true;
            }
            if (photo1_base64.isEmpty() && photo2_base64.isEmpty() && photo3_base64.isEmpty()) {
                popToast(AddEquipmentActivity.this, getResources().getString(R.string.please_add_at_least_one_photoi));
                cancel = true;
            }
        }

        if ((findViewById(R.id.service_form)).getVisibility() == View.VISIBLE) {
            if (category != null) {
                if (category.Id == 2) {
                    if (TextUtils.isEmpty(total_volume)) {
                        total_volume_edittext.setError(getString(R.string.error_field_required));
                        focusView = total_volume_edittext;
                        cancel = true;
                    }

                    if (TextUtils.isEmpty(fuel_cost)) {
                        fuel_cost_edittext.setError(getString(R.string.error_field_required));
                        focusView = fuel_cost_edittext;
                        cancel = true;
                    }
                } else if (category.Id == 3) {
                    if (mobile == 0) {
                        popToast(AddEquipmentActivity.this, getResources().getString(R.string.please_specify_if_the_service_is_mobile));
                    }
                    else if (mobile == 1) {
                        /*if (TextUtils.isEmpty(fuel_cost)) {
                            fuel_cost_edittext.setError(getString(R.string.error_field_required));
                            focusView = fuel_cost_edittext;
                            cancel = true;
                        }*/
                    }
                }
            }

            if (TextUtils.isEmpty(hours_required_per_hectare)) {
                hours_required_per_hectare_edittext.setError(getString(R.string.error_field_required));
                focusView = hours_required_per_hectare_edittext;
                cancel = true;
            }

            if (TextUtils.isEmpty(hire_cost)) {
                hire_cost_edittext.setError(getString(R.string.error_field_required));
                focusView = hire_cost_edittext;
                cancel = true;
            }

            if (category != null && category.Id != 2) {
                //exclude minimum quantity check for lorries
                if (TextUtils.isEmpty(minimum_field_size)) {
                    minimum_quantity_edittext.setError(getString(R.string.error_field_required));
                    focusView = minimum_quantity_edittext;
                    cancel = true;
                }
            }



        }

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            submit_button.setVisibility(View.GONE);
            showLoader(editMode ? "Updating Equipment" : "Adding Equipment", "Please wait...");
            HashMap<String, Object> query = new HashMap<String, Object>();
            query.put("Brand", brand);
            query.put("CategoryId",category.Id);
        //    query.put("ConditionId",condition_id);
            if (editMode){
                if (place != null) {
                    query.put("Latitude", place.getLatLng().latitude);
                    query.put("Longitude", place.getLatLng().longitude);
                    query.put("Location", place.getName().toString());
                }
                else if (selectedLocation != null) {
                    query.put("Latitude", selectedLocation.Latitude);
                    query.put("Longitude", selectedLocation.Longitude);
                    query.put("Location", selectedLocation.Title);
                }
                else {
                    query.put("Latitude", listing.Latitude);
                    query.put("Longitude", listing.Longitude);
                    query.put("Location", listing.Location);
                }
            }
            else {
                if (place != null) {
                    query.put("Latitude", place.getLatLng().latitude);
                    query.put("Longitude", place.getLatLng().longitude);
                    query.put("Location", place.getName().toString());
                }
                else if (selectedLocation != null) {
                    query.put("Latitude", selectedLocation.Latitude);
                    query.put("Longitude", selectedLocation.Longitude);
                    query.put("Location", selectedLocation.Title);
                }
            }
            query.put("Description", additional_info);
            query.put("GroupServices", ((Switch) findViewById(R.id.allow_group_hire_switch)).isChecked() + "");


            if (category.Id == 1) {          //Tractors
                query.put("AvailableWithoutFuel", ((Switch) findViewById(R.id.available_without_fuel_switch)).isChecked() + "");
            }

            if (category != null && category.Id != 3)
                query.put("HorsePower", horse_power);
            query.put("Title", title);
            query.put("Year", year);

            if (editMode)
                query.put("Id", listing.Id);

            if(servicesList != null){
                try {
                    JSONArray servicesArray = new JSONArray();
                    if (category.Id == 1) {  // for tractors and lorries
                        int size = servicesList.size();
                        if (size > 0) {
                            for (int i = 0; i < size; i++) {
                                if (servicesList.get(i).enabled) {
                                    JSONObject jsonObject = new JSONObject();
                                    if (editMode)
                                        jsonObject.accumulate("Id", servicesList.get(i).listingDetailService.Id);
                                    jsonObject.accumulate("CategoryId", servicesList.get(i).service_id);
                                    jsonObject.accumulate("DistanceUnitId", 1);
                                    jsonObject.accumulate("PricePerQuantityUnit", servicesList.get(i).hire_cost);
                                    jsonObject.accumulate("TimePerQuantityUnit", servicesList.get(i).hours_required_per_hectare);
                                    jsonObject.accumulate("MinimumQuantity", servicesList.get(i).minimum_field_size);
                                 //   jsonObject.accumulate("PricePerDistanceUnit", servicesList.get(i).distance_charge);
                                  //  jsonObject.accumulate("MaximumDistance", servicesList.get(i).maximum_distance);
                                    jsonObject.accumulate("PricePerDistanceUnit", distance_charge);
                                    jsonObject.accumulate("MaximumDistance", maximum_distance);

                                    if (category.Id == 1) {          //Tractors
                                        jsonObject.accumulate("FuelPerQuantityUnit", servicesList.get(i).fuel_cost);
                                        jsonObject.accumulate("QuantityUnitId", 1);
                                        jsonObject.accumulate("TimeUnitId", 1);
                                        jsonObject.accumulate("Mobile", 1);
                                    } else if (category.Id == 2) {         //Lorries
                                        Log("SERVICE TOTAL VOLUME TO SEND" + servicesList.get(i).total_volume_in_tonne);
                                        jsonObject.accumulate("FuelPerQuantityUnit", servicesList.get(i).fuel_cost);
                                        jsonObject.accumulate("TotalVolume", servicesList.get(i).total_volume_in_tonne);
                                        jsonObject.accumulate("QuantityUnitId", 2);
                                        jsonObject.accumulate("TimeUnitId", 2);
                                        jsonObject.accumulate("Mobile", 1);
                                    } /*else if (category.Id == 3) {         //Processing
                                        jsonObject.accumulate("Mobile", servicesList.get(i).mobile);
                                        jsonObject.accumulate("QuantityUnitId", 2);
                                        jsonObject.accumulate("TimeUnitId", 3);
                                    }*/
                                    servicesArray.put(jsonObject);
                                }
                            }
                            query.put("Services", servicesArray);
                        }
                    }
                    else {
                        JSONObject jsonObject = new JSONObject();
                        if (editMode) {
                            if (category.Id == 2) {          //Lorries
                                try {
                                    JSONArray lorriesservicesArray = new JSONArray(listing.Services);
                                    if (lorriesservicesArray.length() > 0){
                                        jsonObject.accumulate("Id", lorriesservicesArray.optJSONObject(0).optLong("Id"));
                                    }
                                } catch (JSONException ex){
                                    Log("JSONException" + ex.getMessage());
                                }
                            }
                            else if (category.Id == 3) {
                                jsonObject.accumulate("Id", service_id_for_selected_processing_service_in_editmode);
                            }
                        }

                        if (category.Id == 2) {          //Lorries
                            jsonObject.accumulate("CategoryId", 16);
                        }
                        else
                            jsonObject.accumulate("CategoryId", selectedSpinnerTypeService.service_id);

                        jsonObject.accumulate("DistanceUnitId", 1);
                        jsonObject.accumulate("MaximumDistance", maximum_distance);
                        jsonObject.accumulate("PricePerQuantityUnit", hire_cost);
                        jsonObject.accumulate("TimePerQuantityUnit", hours_required_per_hectare);
                        jsonObject.accumulate("PricePerDistanceUnit", distance_charge);
                      //  jsonObject.accumulate("AvailableWithoutFuel", ((Switch) findViewById(R.id.available_without_fuel_switch)).isChecked() + "");


                        if (category.Id == 2) {         //Lorries
                            jsonObject.accumulate("MinimumQuantity", "0");
                            jsonObject.accumulate("TotalVolumeUnit", total_volume);
                            jsonObject.accumulate("QuantityUnitId", 2);
                            jsonObject.accumulate("TimeUnitId", 2);
                        } else if (category.Id == 3) {         //Processing
                            jsonObject.accumulate("MinimumQuantity", minimum_field_size);
                            jsonObject.accumulate("Mobile", mobile == 1);
                            jsonObject.accumulate("QuantityUnitId", 2);
                            jsonObject.accumulate("TimeUnitId", 3);
                        }
                        servicesArray.put(jsonObject);
                        query.put("Services", servicesArray);
                    }

                    JSONArray photosArray = new JSONArray();
                    if (!photo1_base64.isEmpty()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("base64", encodedImage_prefix + photo1_base64);
                        photosArray.put(jsonObject);
                    }
                    else if (!photo1_file_name.isEmpty() && editMode) {
                        //for edit mode
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("Filename", photo1_file_name);
                        photosArray.put(jsonObject);
                    }

                    if (!photo2_base64.isEmpty()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("base64", encodedImage_prefix + photo2_base64);
                        photosArray.put(jsonObject);
                    }
                    else if (!photo2_file_name.isEmpty() && editMode) {
                        //for edit mode
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("Filename", photo2_file_name);
                        photosArray.put(jsonObject);
                    }

                    if (!photo3_base64.isEmpty()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("base64", encodedImage_prefix + photo3_base64);
                        photosArray.put(jsonObject);
                    }
                    else if (!photo3_file_name.isEmpty() && editMode) {
                        //for edit mode
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("Filename", photo3_file_name);
                        photosArray.put(jsonObject);
                    }

                    query.put("Photos", photosArray);
                } catch (JSONException ex){
                    Log("JSONEXception: " + ex.getMessage());
                }
            }
            if (editMode)
                postAPI("listings/edit", query, fetchResponse);
            else
                postAPI("listings/add", query, fetchResponse);
        }
    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("ADD LISTING SUCCESS: "+ result.toString());
            MyApplication.refreshEquipmentTab = true;
            MyApplication.closeEquipmentDetailActivity = true;
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
            case MY_LOCATION_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    Log.d("PERMISSION GRANTED", "");
                    getCurrentLocation();
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

    private void setupSelectedCategory(){
        if (category != null){
            servicesList.clear();
            ((TextView) findViewById(R.id.type)).setText(category.Title);
            ((TextView) findViewById(R.id.type)).setTextColor(getResources().getColor(android.R.color.black));

            //reset
            (findViewById(R.id.fuel_container)).setVisibility(View.VISIBLE);
            (findViewById(R.id.total_volume_container)).setVisibility(View.VISIBLE);
            if (editMode) {
                fuel_cost_label_container.setVisibility(View.VISIBLE);
                total_volume_label_container.setVisibility(View.VISIBLE);
            }

            //limit available without fuel switch to tractors only
            if (category.Id == 1) {
                (findViewById(R.id.available_without_fuel_container)).setVisibility(View.VISIBLE);
                available_without_fuel_label_container.setVisibility(View.VISIBLE);
              /*  if (editMode) {
                    available_without_fuel_label.setVisibility(View.VISIBLE);
                }*/
            }
            else {
                (findViewById(R.id.available_without_fuel_container)).setVisibility(View.GONE);
                available_without_fuel_label_container.setVisibility(View.GONE);
            }

            if (category.Id == 1) {
                (findViewById(R.id.service_form)).setVisibility(View.GONE);
                (findViewById(R.id.services_label)).setVisibility(View.VISIBLE);
                listview.setVisibility(View.VISIBLE);

                distance_charge_label_container.setVisibility(View.VISIBLE);
                (findViewById(R.id.distance_charge_container)).setVisibility(View.VISIBLE);
                distance_charge_label_container2.setVisibility(View.GONE);
                (findViewById(R.id.distance_charge_container2)).setVisibility(View.GONE);
            }
            else {
                (findViewById(R.id.service_form)).setVisibility(View.VISIBLE);
                (findViewById(R.id.services_label)).setVisibility(View.GONE);
                listview.setVisibility(View.GONE);

                if (category.Id == 2){
                    distance_charge_label_container.setVisibility(View.VISIBLE);
                    (findViewById(R.id.distance_charge_container)).setVisibility(View.VISIBLE);
                    distance_charge_label_container2.setVisibility(View.GONE);
                    (findViewById(R.id.distance_charge_container2)).setVisibility(View.GONE);

                    hours_required_per_hectare_label.setText(getResources().getString(R.string.hours_required_per_100km));
                    hours_required_per_hectare_edittext.setHint(getResources().getString(R.string.hours_required_per_100km));

                    minimum_quantity_label.setText(getResources().getString(R.string.minimum_bags));
                    minimum_quantity_edittext.setHint(getResources().getString(R.string.minimum_bags));
                    minimum_quantity_label_container.setVisibility(View.GONE);
                    (findViewById(R.id.minimum_quantity_container)).setVisibility(View.GONE);

                    /*time_unit_textview.setText(getResources().getString(R.string.hrs));
                    unit_textview.setText(getResources().getString(R.string.bags));
                    dollar_per_unit_textview.setText(getResources().getString(R.string.dollar_per_bag));*/

                    time_unit_textview.setText(getResources().getString(R.string.hrs));
                    dollar_per_unit_textview.setText(getResources().getString(R.string.dollar_per_load));
                    fuel_unit_textview.setText(getResources().getString(R.string.dollar_per_km));

                    is_service_mobile_label_container.setVisibility(View.GONE);
                    mobile_container.setVisibility(View.GONE);

                    fuel_cost_label_container.setVisibility(View.VISIBLE);
                    (findViewById(R.id.fuel_container)).setVisibility(View.VISIBLE);

                    total_volume_label_container.setVisibility(View.VISIBLE);
                    (findViewById(R.id.total_volume_container)).setVisibility(View.VISIBLE);

                }
                else if (category.Id == 3){
                    distance_charge_label_container.setVisibility(View.GONE);
                    (findViewById(R.id.distance_charge_container)).setVisibility(View.GONE);
                    if (mobile == 1) {
                        distance_charge_label_container2.setVisibility(View.VISIBLE);
                        (findViewById(R.id.distance_charge_container2)).setVisibility(View.VISIBLE);
                    }
                    else {
                        distance_charge_label_container2.setVisibility(View.GONE);
                        (findViewById(R.id.distance_charge_container2)).setVisibility(View.GONE);
                    }

                    hours_required_per_hectare_label.setText(getResources().getString(R.string.bags_per_hour));
                    hours_required_per_hectare_edittext.setHint(getResources().getString(R.string.bags_per_hour));

                    minimum_quantity_label.setText(getResources().getString(R.string.minimum_bags));
                    minimum_quantity_edittext.setHint(getResources().getString(R.string.minimum_bags));
                    minimum_quantity_label_container.setVisibility(View.VISIBLE);
                    (findViewById(R.id.minimum_quantity_container)).setVisibility(View.VISIBLE);

                    fuel_unit_textview.setText(getResources().getString(R.string.dollar_per_bag));
                    time_unit_textview.setText(getResources().getString(R.string.bags));
                    unit_textview.setText(getResources().getString(R.string.bags));
                    dollar_per_unit_textview.setText(getResources().getString(R.string.dollar_per_bag));

                    if (editMode)
                        is_service_mobile_label_container.setVisibility(View.VISIBLE);
                    mobile_container.setVisibility(View.VISIBLE);

                    fuel_cost_label_container.setVisibility(View.GONE);
                    (findViewById(R.id.fuel_container)).setVisibility(View.GONE);

                    total_volume_label_container.setVisibility(View.GONE);
                    (findViewById(R.id.total_volume_container)).setVisibility(View.GONE);

                }

            }


            try {
                EquipmentService equipmentServiceToSetInSpinnerDuringEditMode = null;
                JSONArray servicesArray = new JSONArray(category.Services);
                int size = servicesArray.length();
                if (size > 0) {
                    if (editMode) {
                        JSONArray currentServicesArray = new JSONArray(listing.Services);
                        for (int i = 0; i < size; i++) {
                            boolean service_already_added_to_list = false;
                            for (int z = 0; z < currentServicesArray.length(); z++){
                                if (servicesArray.optJSONObject(i).optLong("Id") == currentServicesArray.optJSONObject(z).optJSONObject("Category").optLong("Id")) {
                                    ListingDetailService listingDetailService = new ListingDetailService(currentServicesArray.optJSONObject(z));
                                    Service service = new Service(servicesArray.optJSONObject(i));
                                    EquipmentService equipmentService = new EquipmentService(service, category.Id, listingDetailService);
                                    servicesList.add(equipmentService);
                                    service_already_added_to_list = true;
                                    equipmentServiceToSetInSpinnerDuringEditMode = equipmentService;
                                    service_id_for_selected_processing_service_in_editmode = listingDetailService.Id;
                                    break;
                                }
                            }

                            if (!service_already_added_to_list){
                                Service service = new Service(servicesArray.optJSONObject(i));
                                EquipmentService equipmentService = new EquipmentService(service, category.Id);
                                servicesList.add(equipmentService);
                            }
                        }
                    }
                    else {
                        for (int i = 0; i < size; i++) {
                            Service service = new Service(servicesArray.optJSONObject(i));
                            EquipmentService equipmentService = new EquipmentService(service, category.Id);
                            servicesList.add(equipmentService);
                        }
                    }
                } else {
                    (findViewById(R.id.services_label)).setVisibility(View.GONE);
                }

                Log("SERVICES LIST SIZE : " + servicesList.size());
                setTypeSpinner(equipmentServiceToSetInSpinnerDuringEditMode);

                adapter = new EquipmentServiceAdapter(AddEquipmentActivity.this, servicesList, AddEquipmentActivity.this);
                listview.setAdapter(adapter);
                Utils.setListViewHeightBasedOnChildren(listview);

            } catch (JSONException ex) {
                Log("JSONException : " + ex.getMessage());
            }

            if (editMode) {
                try {
                    JSONArray currentServicesArray = new JSONArray(listing.Services);
                    if (currentServicesArray.length() > 0){
                        JSONObject serviceObject = currentServicesArray.optJSONObject(0);  //...coz there should only ever be 1 service for Lorries and Processing categories

                        total_volume_edittext.setText(String.valueOf(serviceObject.optDouble("TotalVolume")));
                        hours_required_per_hectare_edittext.setText(String.valueOf(serviceObject.optDouble("TimePerQuantityUnit")));
                        hire_cost_edittext.setText(String.valueOf(serviceObject.optDouble("PricePerQuantityUnit")));
                        fuel_cost_edittext.setText(String.valueOf(serviceObject.optDouble("FuelPerQuantityUnit")));
                        minimum_quantity_edittext.setText(String.valueOf(serviceObject.optDouble("MinimumQuantity")));
                        distance_charge_edittext.setText(String.valueOf(serviceObject.optDouble("PricePerDistanceUnit")));
                        maximum_distance_edittext.setText(String.valueOf(serviceObject.optDouble("MaximumDistance")));

                        if (category.Id == 3) {
                            if (serviceObject.has("Mobile")) {
                                if (serviceObject.optBoolean("Mobile")) {
                                    mobile = 1;
                                    ((TextView) findViewById(R.id.mobile)).setText(getResources().getString(R.string.yes_is_mobile));
                                    (findViewById(R.id.fuel_container)).setVisibility(View.VISIBLE);
                                    if (editMode)
                                        fuel_cost_label_container.setVisibility(View.VISIBLE);
                                } else {
                                    mobile = 2;
                                    ((TextView) findViewById(R.id.mobile)).setText(getResources().getString(R.string.no_it_is_not_mobile));
                                    (findViewById(R.id.fuel_container)).setVisibility(View.GONE);
                                    fuel_cost_label_container.setVisibility(View.GONE);
                                }
                            }
                        }

                    }
                } catch (JSONException ex) {
                    Log("JSONException" + ex.getMessage());
                }
            }

            //hide horse-power field for Processing category.
            if (category.Id == 3) {
                (findViewById(R.id.horse_power_container)).setVisibility(View.GONE);
                horse_power_label_container.setVisibility(View.GONE);
            } else {
                (findViewById(R.id.horse_power_container)).setVisibility(View.VISIBLE);
                horse_power_label_container.setVisibility(View.VISIBLE);
              /*  if (editMode)
                    horse_power_label.setVisibility(View.VISIBLE);*/
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log("ON ACTIVITY RESULT: " + requestCode);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this, data);
                updateSelectedLocationTextView();
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log("PLACES RESPONSE ERROR"+ status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if (requestCode == CHOOSE_LOCATION_FROM_MAP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                selectedLocation = data.getParcelableExtra(KEY_LOCATION);
                showFetchingLocationFromMapTextView();
                getLocationData(selectedLocation.Latitude + "," + selectedLocation.Longitude);
            }else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if (requestCode == TYPE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
               category = data.getParcelableExtra(KEY_CATEGORY);
               setupSelectedCategory();
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
        else if ((requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) || (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)) {
            // Get the Image from data

            Log("IMAGE DATA: "+ data);
            if (requestCode == CAMERA_REQUEST) {
                imgDecodableString = mCurrentPhotoPath;
            } else {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                if (cursor != null) {
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                }
                else {
                    imgDecodableString = selectedImage.getPath();
                }

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

    private void getLocationData(String coordinates){
        Log("COORDINATES TO FIND LOCATION DETAILS: "+ coordinates);
        GetMapLocationDetailsRequest task = new GetMapLocationDetailsRequest(fetchLocationDataResponse);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, coordinates);
    }

    AsyncResponse fetchLocationDataResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("MAP LOCATION DETAIL SUCCESS"+ result.toString() + "");
            if (selectedLocation != null) {
                if (result.optJSONArray("results").length() > 0) {
                    selectedLocation.Title = result.optJSONArray("results").optJSONObject(0).optString("name");
                    updateSelectedLocationTextViewFromMapData();
                } else {
                   // selectedLocation.Title = "Could not fetch location title";
                    selectedLocation.Title = "";
                    showFetchingLocationTitleFromGoogleApiFailedTextView();
                }
            }
            else {

            }
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("MAP LOCATION DETAIL ERROR:  " + errorMessage);
            showFetchingLocationFromMapFailedTextView();
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    private void showPhotoInUI(){
        if (editMode) {
            if (photo1_base64.isEmpty() && photo1_file_name.isEmpty()) {
                convertFileToBase64(0);
                photo1_imageview.setImageBitmap(photo);
                cancel1_imageview.setVisibility(View.VISIBLE);
            } else if (photo2_base64.isEmpty() && photo1_file_name.isEmpty()) {
                convertFileToBase64(1);
                photo2_imageview.setImageBitmap(photo);
                cancel2_imageview.setVisibility(View.VISIBLE);
            } else if (photo3_base64.isEmpty() && photo1_file_name.isEmpty()) {
                convertFileToBase64(2);
                photo3_imageview.setImageBitmap(photo);
                cancel3_imageview.setVisibility(View.VISIBLE);
            } else {
                if (selected_upload_button == 1) {
                    convertFileToBase64(0);
                    photo1_imageview.setImageBitmap(photo);
                    cancel1_imageview.setVisibility(View.VISIBLE);
                } else if (selected_upload_button == 2) {
                    convertFileToBase64(1);
                    photo2_imageview.setImageBitmap(photo);
                    cancel2_imageview.setVisibility(View.VISIBLE);
                } else if (selected_upload_button == 3) {
                    convertFileToBase64(2);
                    photo3_imageview.setImageBitmap(photo);
                    cancel3_imageview.setVisibility(View.VISIBLE);
                }
                selected_upload_button = 0;
            }
        }
        else {
            if (photo1_base64.isEmpty()) {
                convertFileToBase64(0);
                photo1_imageview.setImageBitmap(photo);
                cancel1_imageview.setVisibility(View.VISIBLE);
            } else if (photo2_base64.isEmpty()) {
                convertFileToBase64(1);
                photo2_imageview.setImageBitmap(photo);
                cancel2_imageview.setVisibility(View.VISIBLE);
            } else if (photo3_base64.isEmpty()) {
                convertFileToBase64(2);
                photo3_imageview.setImageBitmap(photo);
                cancel3_imageview.setVisibility(View.VISIBLE);
            } else {
                if (selected_upload_button == 1) {
                    convertFileToBase64(0);
                    photo1_imageview.setImageBitmap(photo);
                    cancel1_imageview.setVisibility(View.VISIBLE);
                } else if (selected_upload_button == 2) {
                    convertFileToBase64(1);
                    photo2_imageview.setImageBitmap(photo);
                    cancel2_imageview.setVisibility(View.VISIBLE);
                } else if (selected_upload_button == 3) {
                    convertFileToBase64(2);
                    photo3_imageview.setImageBitmap(photo);
                    cancel3_imageview.setVisibility(View.VISIBLE);
                }
                selected_upload_button = 0;
            }
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

        }
        else {
            popToast(AddEquipmentActivity.this, "Failed to convert image to base64.");
        }
    }

    private Service getLorryGeneralService(){
        RealmResults<Categories> results = MyApplication.realm.where(Categories.class)
                .equalTo("Id", 2)
                .findAll();

        int size = results.size();
        if (size > 0) {
            try {
                Category category = new Category(results.get(0));
                JSONArray jsonArray = new JSONArray(category.Services);
                int services_count = jsonArray.length();

                for (int i = 0; i < services_count; i++) {
                    Service service = new Service(jsonArray.getJSONObject(i));
                    if (service.Id == 16){
                        return service; // general service
                    }
                }

            } catch (JSONException ex){
                Log("JSONEXception Services: " + ex.getMessage());

            }

        }
        return null;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (likelyPlaces != null){
            likelyPlaces.release();
        }
    }

}
