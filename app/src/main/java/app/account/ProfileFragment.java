package app.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.util.HashMap;

import app.about.AboutActivity;
import app.agrishare.BaseFragment;
import app.agrishare.MainActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.AsyncResponse;
import app.contact.ContactUsActivity;
import app.faqs.FAQsActivity;
import io.realm.RealmResults;
import okhttp3.Response;

import static app.agrishare.Constants.DASHBOARD;
import static app.agrishare.Constants.PREFS_IS_DEVICE_REGISTERED_ON_OUR_SERVER;
import static app.agrishare.Constants.PREFS_TOKEN;
import static app.agrishare.Constants.PROFILE;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class ProfileFragment extends BaseFragment {

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_profile_parent, container, false);
        initViews();
        return rootView;
    }

    private void initViews(){
        setToolbar();
        int versionCode = 0;
        String versionName = "";
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionCode = pInfo.versionCode;
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException ex) {

        }
        ((TextView) rootView.findViewById(R.id.build)).setText("AgriShare v" + versionName + " Build #" + versionCode + " • Built by C2 Digital");

        (rootView.findViewById(R.id.edit_profile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    openScreen(EditProfileActivity.class);
                }
            }
        });

        (rootView.findViewById(R.id.notification_preferences)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    openScreen(NotificationPreferencesActivity.class);
                }
            }
        });


        (rootView.findViewById(R.id.reset_pin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    openScreen(ForgotPinActivity.class);
                }
            }
        });

        (rootView.findViewById(R.id.about_agrishare)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    openScreen(AboutActivity.class);
                }
            }
        });

        (rootView.findViewById(R.id.faqs)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    openScreen(FAQsActivity.class);
                }
            }
        });

        (rootView.findViewById(R.id.contact_us)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    openScreen(ContactUsActivity.class);
                }
            }
        });

        (rootView.findViewById(R.id.privacy_policy)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    openScreen(PrivacyPolicyActivity.class);
                }
            }
        });

        (rootView.findViewById(R.id.logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Logout");
                    alertDialogBuilder
                            .setMessage("Are you sure you want to logout?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Query and update the result asynchronously in another thread
                                   // logoutUser();
                                    removeDeviceFromOurServer();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    alertDialog.setCancelable(true);

                }
            }
        });
    }



    public void removeDeviceFromOurServer(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(getActivity(),  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String mToken = instanceIdResult.getToken();
                if (!mToken.isEmpty()) {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Please wait..."); // Setting Message
                    progressDialog.setTitle("Logging out"); // Setting Title
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                    progressDialog.show(); // Display Progress Dialog
                    progressDialog.setCancelable(false);

                    HashMap<String, String> query = new HashMap<String, String>();
                    query.put("Token", mToken);
                    getAPI("logout", query, fetchResponse);
                }
                else {
                    logoutUser();
                }
            }
        });

    }

    AsyncResponse fetchResponse = new AsyncResponse() {

        @Override
        public void taskSuccess(JSONObject result) {
            Log("SUCCESS DEVICE REMOVE: " + result.toString());
            progressDialog.dismiss();
            logoutUser();
        }

        @Override
        public void taskProgress(int progress) { }

        @Override
        public void taskError(String errorMessage) {
            Log("DEVICE REMOVE"+ "FROM OUR OWN SERVER FAILED: " + errorMessage);
            progressDialog.dismiss();
            logoutUser();
        }

        @Override
        public void taskCancelled(Response response) {

        }
    };

    private void openScreen(Class activity){
        Intent intent = new Intent(getActivity(), activity);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
    }

    public void logoutUser(){
        if (MyApplication.notificationManager != null)
            MyApplication.notificationManager.cancelAll();
        MyApplication.token = "";
        MyApplication.isDeviceRegisteredOnOurServer = false;
        SharedPreferences.Editor editor = MyApplication.prefs.edit();
        editor.putString(PREFS_TOKEN, MyApplication.token);
        editor.putBoolean(PREFS_IS_DEVICE_REGISTERED_ON_OUR_SERVER, MyApplication.isDeviceRegisteredOnOurServer);
        editor.commit();

        Intent intent = new Intent(getActivity(), SplashActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
        getActivity().finish();
    }

    private void setToolbar(){
        if (rootView != null){
            Toolbar toolbar = rootView.findViewById(R.id.toolbar);
            toolbar.setTitle("Profile");
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }
        setToolbar();
      //  ((MainActivity) getActivity()).setActionBarTitle("Profile");
        ((TextView) rootView.findViewById(R.id.name)).setText(MyApplication.currentUser.FirstName + " " + MyApplication.currentUser.LastName);
        ((TextView) rootView.findViewById(R.id.phone)).setText(MyApplication.currentUser.Telephone);

        if (MyApplication.tabsStackList.contains(PROFILE))
            MyApplication.tabsStackList.remove(PROFILE);
        MyApplication.tabsStackList.add(PROFILE);
    }

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }
}
