package com.app.bickup;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bickup.GlobleVariable.GloableVariable;
import com.app.bickup.broadcastreciever.InternetConnectionBroadcast;
import com.app.bickup.controller.AppController;
import com.app.bickup.controller.NetworkCallBack;
import com.app.bickup.controller.WebAPIManager;
import com.app.bickup.model.User;
import com.app.bickup.utility.CommonMethods;
import com.app.bickup.utility.ConstantValues;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

import static com.app.bickup.R.id.map;
import static com.app.bickup.utility.ConstantValues.KEY_CAMERA_POSITION;
import static com.app.bickup.utility.ConstantValues.KEY_LOCATION;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, InternetConnectionBroadcast.ConnectivityRecieverListener, OnMapReadyCallback, View.OnClickListener, NetworkCallBack {

    private Snackbar snackbar;
    private CoordinatorLayout mCoordinatorLayout;
    public static String TAG = MainActivity.class.getSimpleName();
    private boolean mIsConnected;
    private Activity mActivityreference;
    private Location mCurrentLocation;
    private boolean mLocationPermission = false;
    private LocationManager mLocationmanager;
    private LocationListener mLocationListener;
    private GoogleMap googleMap;
    private CameraPosition mCameraPosition;
    private TextView txtSmalltravellerName, txtLargeTravellername, txtsmallCost, txtLargeCost;
    private Button btnSmallSubmit, btnLargeSubmit;
    private TextView edtPickUpLocation, edtDropLocation;
    private Typeface mTypefaceRegular;
    private Typeface mTypefaceBold;
    private DuoDrawerLayout drawerLayout;
    private ImageView navigationDrawer;
    private DrawerLayout CheckdrawerLayout;
    private RelativeLayout imgPickupSearch;
    private RelativeLayout imgDropSearch;
    private ImageView imgUserImage;
    private TextView userName;
    private TextView useremail;
    private RoundedImageView userImage;
    private SharedPreferences sharedPreferences;
    public String lattitude;
    public String longitude;
    public String droplattitude;
    public String droplongitude;
    public static int RESULT_PICKUP = 200;
    public static int RESULT_DROP = 201;
    private ArrayList<Marker> markerList;
    private CircularProgressView circularProgressBar;
    private String message;
    private String smallPickupCost = "";
    private String largePickupCost = "";
    private String smallPickupDistance = "";
    private String largePickupDistance = "";


    private final int REQUEST_FARE_DETAILS = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim._slide_out);
        setContentView(R.layout.activity_main);
        mActivityreference = MainActivity.this;
        intializeViews();
        initializeForLocation(savedInstanceState);
        setGoogleMap();
        GloableVariable.Tag_pickup_home_type = "2";
        GloableVariable.Tag_drop_home_type = "2";
        GloableVariable.Tag_drop_home_type = "1";

    }

    private void initializeForLocation(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        mLocationmanager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mCurrentLocation = location;
                //updateLocationUI();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    private void getUserCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermission = true;
            mCurrentLocation = mLocationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mLocationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            updateLocationUI();
            return;
        }

    }

    private void setGoogleMap() {
        SupportMapFragment mAupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mAupportMapFragment.getMapAsync(this);

    }

    private void intializeViews() {
        navigationDrawer = (ImageView) findViewById(R.id.navigation_menu);
        navigationDrawer.setOnClickListener(this);
        circularProgressBar = (CircularProgressView) findViewById(R.id.progress_view);
        findViewById(R.id.menu_delivery).setOnClickListener(this);
        findViewById(R.id.menu_scheduled).setOnClickListener(this);
        findViewById(R.id.menu_setting).setOnClickListener(this);
        findViewById(R.id.img_drawer_image).setOnClickListener(this);
        userName = (TextView) findViewById(R.id.username);
        useremail = (TextView) findViewById(R.id.userEmail);
        userImage = (RoundedImageView) findViewById(R.id.img_drawer_image);
        userName.setText(User.getInstance().getFirstName() + " " + User.getInstance().getLastName());
        useremail.setText(User.getInstance().getEmail());
        drawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer_layout);


        mTypefaceRegular = Typeface.createFromAsset(getAssets(), ConstantValues.TYPEFACE_REGULAR);
        mTypefaceBold = Typeface.createFromAsset(getAssets(), ConstantValues.TYPEFACE_BOLD);

        RelativeLayout cardLargeTravellerContainer = (RelativeLayout) findViewById(R.id.rl_large_traveller);
        RelativeLayout cardSmalltravellerContainer = (RelativeLayout) findViewById(R.id.rl_small_traveller);
        cardSmalltravellerContainer.setOnClickListener(this);
        cardLargeTravellerContainer.setOnClickListener(this);

        txtSmalltravellerName = (TextView) findViewById(R.id.txt_traveller_name_small);
        txtsmallCost = (TextView) findViewById(R.id.txt_cost_small);
        txtLargeTravellername = (TextView) findViewById(R.id.txt_traveller_name_large);
        txtLargeCost = (TextView) findViewById(R.id.txt_cost_large);

        imgPickupSearch = (RelativeLayout) findViewById(R.id.label_pickup_location_dialog);
        imgDropSearch = (RelativeLayout) findViewById(R.id.label_drop_location);

        imgDropSearch.setOnClickListener(this);
        imgPickupSearch.setOnClickListener(this);

        btnLargeSubmit = (Button) findViewById(R.id.btn_submit_large);
        btnSmallSubmit = (Button) findViewById(R.id.btn_submit_small);

        btnLargeSubmit.setOnClickListener(this);
        btnSmallSubmit.setOnClickListener(this);

        //Set Font to Views
        txtLargeTravellername.setTypeface(mTypefaceBold);
        txtSmalltravellerName.setTypeface(mTypefaceBold);
        txtLargeCost.setTypeface(mTypefaceRegular);
        txtsmallCost.setTypeface(mTypefaceRegular);
        btnLargeSubmit.setTypeface(mTypefaceRegular);
        btnSmallSubmit.setTypeface(mTypefaceRegular);
        edtPickUpLocation = (TextView) findViewById(R.id.edt_pickup_location);
        edtDropLocation = (TextView) findViewById(R.id.edt_drop_location);


        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
        TextView tv_header = (TextView) toolbar.findViewById(R.id.txt_activty_header);
        toolbar.hideOverflowMenu();
        toolbar.showContextMenu();
        tv_header.setText(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        findViewById(R.id.contactus_container).setOnClickListener(this);
        findViewById(R.id.aboutus_container).setOnClickListener(this);
        findViewById(R.id.privacy_container).setOnClickListener(this);
        findViewById(R.id.faq_container).setOnClickListener(this);
        findViewById(R.id.invite_and_earn_container).setOnClickListener(this);
        findViewById(R.id.change_password_container).setOnClickListener(this);
        findViewById(R.id.help_container).setOnClickListener(this);

        // toolbar.getBackground().setAlpha(8);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        checkInternetconnection();
        if (AppController.getInstance() != null) {
            AppController.getInstance().setConnectivityListener(this);
        }
        setUserData();
    }


    private void setUserData() {
        String firstName = User.getInstance().getFirstName();
        String lastName = User.getInstance().getLastName();
        String email = User.getInstance().getEmail();
        String userImageString = User.getInstance().getUserImage();

        if (userName != null && useremail != null) {
            userName.setText(firstName + " " + lastName);
            useremail.setText(email);
            if (userImage != null) {
                if (userImageString != null) {
                    Ion.with(userImage)
                            .placeholder(R.drawable.driver)
                            .error(R.drawable.driver)
                            .load(userImageString);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("")
                .setMessage(getResources().getString(R.string.txt_close_app))
                .setPositiveButton(getResources().getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(getResources().getString(R.string.txt_No), null)
                .show();

    }


    private void checkInternetconnection() {
        mIsConnected = CommonMethods.getInstance().checkInterNetConnection(mActivityreference);
        if (mIsConnected) {
            if (snackbar != null) {
                snackbar.dismiss();

            }
        } else {
            showSnackBar(getResources().getString(R.string.iconnection_availability));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermission = false;
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getUserCurrentLocation();
                    }
                    // mLocationPermission = true;

                }
                break;
          /*  case 222:
                if(grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        getUserCurrentLocation();
                    }
                }*/

        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Set UI to hide keyoard onTouch outside the edittext
    public void setUIToHideKeyBoard(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    CommonMethods.getInstance().hideSoftKeyBoard(MainActivity.this);
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setUIToHideKeyBoard(innerView);
            }
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            if (snackbar != null) {
                snackbar.dismiss();
            }
        } else {
            showSnackBar(getResources().getString(R.string.iconnection_availability));
        }

    }

    public void showSnackBar(String mString) {
      /*  snackbar = Snackbar
                .make(mCoordinatorLayout, mString, Snackbar.LENGTH_INDEFINITE);
        snackbar.setText(mString);
        snackbar.show();*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            this.googleMap = googleMap;
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style);
            this.googleMap.setMapStyle(style);
            googleMap.clear();
            showLocationOnmap();
           /* if (mLocationPermission) {
               // getUserCurrentLocation();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        ConstantValues.PERMISSION_CURRENTLOCATION);
            }*/
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Location location = null;
        if (googleMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                location = mLocationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                outState.putParcelable(KEY_CAMERA_POSITION, googleMap.getCameraPosition());
                outState.putParcelable(ConstantValues.KEY_LOCATION, location);
                super.onSaveInstanceState(outState);
                return;
            }

        }
    }

    private void updateLocationUI() {
        if (googleMap != null) {
            if (mCurrentLocation != null) {
                LatLngBounds AUSTRALIA = new LatLngBounds(
                        new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                LatLng sydney = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(AUSTRALIA.getCenter(), 15);
                googleMap.animateCamera(myLocation);
                UiSettings mUiSetting = googleMap.getUiSettings();
                mUiSetting.setTiltGesturesEnabled(true);
                mUiSetting.setRotateGesturesEnabled(true);
            }
        }
    }

    public void addMarkere(Double lattitude, Double longitude, String title, int marker) {
        LatLng sydney = new LatLng(lattitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(sydney)
                .icon(BitmapDescriptorFactory.fromResource(marker))
                .anchor(0.5f, 0.5f)
                .title(title);
        Marker marker1 = googleMap.addMarker(markerOptions);
        markerList.add(marker1);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rl_small_traveller:
                setLayoutForSmallTraveller();
                break;
            case R.id.rl_large_traveller:
                setLayoutForLargetraveller();
                break;
            case R.id.btn_submit_small:
                showPopUp(1);
                break;
            case R.id.btn_submit_large:
                showPopUp(0);
                break;
            case R.id.navigation_menu:
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;
            case R.id.menu_delivery:
                Intent intent = new Intent(this, DeliveryActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_scheduled:
                Intent scheDuled = new Intent(this, ScheduledActivity.class);
                startActivity(scheDuled);
                break;
            case R.id.menu_setting:
                Intent setting = new Intent(this, SettingActivity.class);
                startActivity(setting);

                break;
            case R.id.label_pickup_location_dialog:
                Intent pickupLocation = new Intent(this, PickupLocationActivity.class);
                startActivityForResult(pickupLocation, RESULT_PICKUP);
                break;
            case R.id.label_drop_location:
                Intent dropLocation = new Intent(this, DropLocationActivity.class);
                startActivityForResult(dropLocation, RESULT_DROP);
                break;
            case R.id.img_drawer_image:
                Intent edit = new Intent(this, EditProfileActivity.class);
                startActivity(edit);
                break;
            case R.id.contactus_container:
                Intent cms = new Intent(this, CMSActivity.class);
                cms.putExtra(ConstantValues.CHOOSE_PAGE, 1);
                startActivity(cms);
                break;
            case R.id.aboutus_container:
                Intent cms1 = new Intent(this, CMSActivity.class);
                cms1.putExtra(ConstantValues.CHOOSE_PAGE, 3);
                startActivity(cms1);
                break;
            case R.id.privacy_container:
                Intent cms3 = new Intent(this, CMSActivity.class);
                cms3.putExtra(ConstantValues.CHOOSE_PAGE, 2);
                startActivity(cms3);
                break;
            case R.id.faq_container:
                Intent cms4 = new Intent(this, CMSActivity.class);
                cms4.putExtra(ConstantValues.CHOOSE_PAGE, 4);
                startActivity(cms4);
                break;
            case R.id.invite_and_earn_container:
                Intent cms5 = new Intent(this, CMSActivity.class);
                cms5.putExtra(ConstantValues.CHOOSE_PAGE, 5);
                startActivity(cms5);
                break;
            case R.id.change_password_container:
                Intent cms6 = new Intent(this, CMSActivity.class);
                cms6.putExtra(ConstantValues.CHOOSE_PAGE, 6);
                startActivity(cms6);
                break;
            case R.id.help_container:
                Intent cms7 = new Intent(this, CMSActivity.class);
                cms7.putExtra(ConstantValues.CHOOSE_PAGE, 7);
                startActivity(cms7);
                break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_PICKUP && resultCode == Activity.RESULT_OK) {
            if (resultCode == Activity.RESULT_OK) {
                String pickupAddress = data.getStringExtra(ConstantValues.ADDRESS);
                edtPickUpLocation.setText(pickupAddress);
                showLocationOnmap();
            }
        } else {
            if (resultCode == Activity.RESULT_OK && requestCode == RESULT_DROP) {
                String pickupAddress = data.getStringExtra(ConstantValues.DROP_ADDRESS);
                edtDropLocation.setText(pickupAddress);
                showLocationOnmap();

            }
        }

    }

    public void showLocationOnmap() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(ConstantValues.USER_PREFERENCES, Context.MODE_PRIVATE);
        String lattitude = sharedPreferences.getString(ConstantValues.LATITUDE, "");
        String longitude = sharedPreferences.getString(ConstantValues.LONGITUDE, "");
        String pickupAddress = sharedPreferences.getString(ConstantValues.ADDRESS, "");
        String droplattitude = sharedPreferences.getString(ConstantValues.DROP_LATITUDE, "");
        String dropLongitude = sharedPreferences.getString(ConstantValues.DROP_LONGITUDE, "");
        String dropAddress = sharedPreferences.getString(ConstantValues.DROP_ADDRESS, "");


        GloableVariable.Tag_pickup_contact_name = User.getInstance().getFirstName() + " " + User.getInstance().getLastName();
        GloableVariable.Tag_pickup_contact_number = User.getInstance().getMobileNumber();


        GloableVariable.Tag_drop_contact_name = User.getInstance().getFirstName() + " " + User.getInstance().getLastName();
        GloableVariable.Tag_drop_contact_number = User.getInstance().getMobileNumber();


        boolean isRideActive = sharedPreferences.getBoolean(ConstantValues.IS_RIDE_ACTIVE, false);
        if (isRideActive) {
            try {
                clearMap();
                markerList = new ArrayList<>();
                addMarkere(Double.parseDouble(lattitude), Double.parseDouble(longitude), "Pick", R.drawable.pin_location_pin);
                addMarkere(Double.parseDouble(droplattitude), Double.parseDouble(dropLongitude), "Drop", R.drawable.drop_location_pin);
                prepareRouteUrl(Double.parseDouble(lattitude), Double.parseDouble(longitude), Double.parseDouble(droplattitude), Double.parseDouble(dropLongitude));
                edtPickUpLocation.setText(pickupAddress);
                edtDropLocation.setText(dropAddress);

                GloableVariable.Tag_pickup_location_address = pickupAddress;
                GloableVariable.Tag_drop_location_address = dropAddress;

                GloableVariable.Tag_pickup_latitude = Double.parseDouble(lattitude);
                GloableVariable.Tag_pickup_longitude = Double.parseDouble(longitude);

                GloableVariable.Tag_drop_latitude = Double.parseDouble(droplattitude);
                GloableVariable.Tag_drop_longitude = Double.parseDouble(dropLongitude);


                prepareGetFareDetails(lattitude, longitude, droplattitude, dropLongitude);
            } catch (Exception e) {

            }
        } else {
            if (CommonMethods.getInstance().validateEditFeild(lattitude) && CommonMethods.getInstance().validateEditFeild(longitude) && CommonMethods.getInstance().validateEditFeild(droplattitude) && CommonMethods.getInstance().validateEditFeild(dropLongitude)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(ConstantValues.IS_RIDE_ACTIVE, true);
                editor.commit();
                showLocationOnmap();

            } else {
                getUserCurrentLocation();
            }
        }
    }

    private void clearMap() {
        if (googleMap != null) {
            googleMap.clear();
        }
    }


    private void showAllMarkers(Marker v, Marker parseDouble) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(v.getPosition().latitude, v.getPosition().longitude));
        builder.include(new LatLng(parseDouble.getPosition().latitude, parseDouble.getPosition().longitude));
        LatLngBounds bounds = builder.build();
        googleMap.setPadding(30, 20, 10, 10);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(v.getPosition().latitude, v.getPosition().longitude)).zoom(15).tilt(30).bearing(90).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }


    public void prepareRouteUrl(double lattitude, double longitude, double droplattitude, double dropLongitude) {
        String url = WebAPIManager.getInstance().getMapDirectionUrl();
        url = url + "origin=" + lattitude + "," + longitude + "&destination=" + droplattitude + "," + dropLongitude + "&mode=driving&key=AIzaSyC-xQ2NJX_QoyLjZQJw8DWnJQwqnJvmTI4";
        callAPIForDrawRoute(url);
    }


    private void callAPIForDrawRoute(String url) {
        Ion.with(this)
                .load(url)
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_Netork_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int status = result.getHeaders().code();
                        JsonObject resultObject = result.getResult();
                        switch (status) {
                            case 200:
                                ParserTask parserTask = new ParserTask();
                                parserTask.execute(String.valueOf(resultObject));
                                break;
                        }
                    }
                });
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.color(MainActivity.this.getResources().getColor(R.color.appcolor));

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                Polyline polyline = googleMap.addPolyline(lineOptions);
                polyline.setWidth(20);
                showAllMarkers(markerList.get(0), markerList.get(1));
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }


    private void showPopUp(int choosetraveller) {
        final Dialog openDialog = new Dialog(mActivityreference);
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.pick_up_dialog);
        openDialog.setTitle("Custom Dialog Box");
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView travellerName = (TextView) openDialog.findViewById(R.id.txt_traveller_name_dialog);

        TextView travellerCost = (TextView) openDialog.findViewById(R.id.txt_traveller_cost);
        TextView text_descrption = openDialog.findViewById(R.id.text_descrption);
        ImageView travellerImage = (ImageView) openDialog.findViewById(R.id.img_traveller);
        Button btnDisagree = (Button) openDialog.findViewById(R.id.btn_disagree);

        Button btnAgree = (Button) openDialog.findViewById(R.id.btn_agree);
        travellerName.setTypeface(mTypefaceBold);
        travellerCost.setTypeface(mTypefaceRegular);
        btnDisagree.setTypeface(mTypefaceRegular);
        btnAgree.setTypeface(mTypefaceRegular);
        if (choosetraveller == 1) {
            travellerName.setText(getResources().getString(R.string.txt_small_pickUP));
            travellerCost.setText("Fare Cost: $" + smallPickupCost);
            GloableVariable.Tag_total_price = smallPickupCost;
            GloableVariable.Tag_distance = smallPickupDistance;
        } else {
            travellerName.setText(getResources().getString(R.string.txt_medium_pickUP));
            travellerCost.setText("Fare Cost: $" + largePickupCost);
            GloableVariable.Tag_total_price = smallPickupCost;
            GloableVariable.Tag_distance = largePickupDistance;

        }
        btnDisagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDialog.dismiss();
            }
        });
        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDialog.dismiss();
                Intent intent = new Intent(MainActivity.this, GoodsActivity.class);
                startActivity(intent);
            }
        });
        openDialog.show();

    }

    private void setLayoutForLargetraveller() {
        btnSmallSubmit.setVisibility(View.INVISIBLE);
        btnLargeSubmit.setVisibility(View.VISIBLE);

        txtLargeTravellername.setTextColor(getResources().getColor(R.color.grey_text_color));
        txtSmalltravellerName.setTextColor(getResources().getColor(R.color.greyColor));
        ImageView smallTraveller = (ImageView) findViewById(R.id.img_traveller_small);
        ImageView largeTraveller = (ImageView) findViewById(R.id.img_traveller_large);
        largeTraveller.setImageResource(R.drawable.ac_lg_vehicle_car);
        smallTraveller.setImageResource(R.drawable.de_sm_vehicle_car);

        RelativeLayout smallTravellers = (RelativeLayout) findViewById(R.id.rl_small_traveller);
        RelativeLayout largeTravellers = (RelativeLayout) findViewById(R.id.rl_large_traveller);


        LinearLayout.LayoutParams cardSmallTravelerLayoutParams = (LinearLayout.LayoutParams) smallTravellers.getLayoutParams();
        LinearLayout.LayoutParams cardLargeTravellerLayoutParams = (LinearLayout.LayoutParams) largeTravellers.getLayoutParams();

        cardLargeTravellerLayoutParams.setMargins(2, 0, 8, 0);
        cardSmallTravelerLayoutParams.setMargins(8, 20, 2, 0);
        smallTravellers.setLayoutParams(cardSmallTravelerLayoutParams);
        largeTravellers.setLayoutParams(cardLargeTravellerLayoutParams);


    }

    private void setLayoutForSmallTraveller() {
        btnSmallSubmit.setVisibility(View.VISIBLE);
        btnLargeSubmit.setVisibility(View.INVISIBLE);

        txtSmalltravellerName.setTextColor(getResources().getColor(R.color.grey_text_color));
        txtLargeTravellername.setTextColor(getResources().getColor(R.color.greyColor));
        ImageView smallTravellers = (ImageView) findViewById(R.id.img_traveller_small);
        ImageView largeTravellers = (ImageView) findViewById(R.id.img_traveller_large);
        largeTravellers.setImageResource(R.drawable.de_lg_vehicle_car);
        smallTravellers.setImageResource(R.drawable.ac_sm_vehicle_car);
        RelativeLayout smallTraveller = (RelativeLayout) findViewById(R.id.rl_small_traveller);
        RelativeLayout largeTraveller = (RelativeLayout) findViewById(R.id.rl_large_traveller);
        LinearLayout.LayoutParams cardLayoutParams = (LinearLayout.LayoutParams) smallTraveller.getLayoutParams();
        LinearLayout.LayoutParams cardLargeLayoutParams = (LinearLayout.LayoutParams) largeTraveller.getLayoutParams();

        cardLayoutParams.setMargins(2, 0, 8, 0);
        cardLargeLayoutParams.setMargins(8, 20, 2, 0);
        smallTraveller.setLayoutParams(cardLayoutParams);
        largeTraveller.setLayoutParams(cardLargeLayoutParams);
    }


    public class DataParser {

        /**
         * Receives a JSONObject and returns a list of lists containing latitude and longitude
         */
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<>();
            JSONArray jRoutes;
            JSONArray jLegs;
            JSONArray jSteps;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<>();

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat", Double.toString((list.get(l)).latitude));
                                hm.put("lng", Double.toString((list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }


            return routes;
        }


        /**
         * Method to decode polyline points
         * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         */
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }

    public void prepareGetFareDetails(String lattitude, String longitude, final String droplattitude, final String droplongitude) {
        String createUserUrl = WebAPIManager.getInstance().getFareDetailsUrl();
        final JsonObject requestBody = new JsonObject();
        requestBody.addProperty(ConstantValues.PICKUP_LATTITUDE, lattitude);
        requestBody.addProperty(ConstantValues.PICKUP_LONGITUDE, longitude);
        requestBody.addProperty(ConstantValues.DROP_LATITUDE, droplattitude);
        requestBody.addProperty(ConstantValues.DROP_LONGITUDE, droplongitude);
        getApproxfareDetails(requestBody, createUserUrl, this, 60 * 1000, REQUEST_FARE_DETAILS);
    }

    private void getApproxfareDetails(JsonObject requestBody, String createUserUrl, final NetworkCallBack loginActivity, int timeOut, final int requestCode) {
        circularProgressBar.setVisibility(View.VISIBLE);
        Ion.with(this)
                .load("POST", createUserUrl)
                .setHeader(ConstantValues.USER_ACCESS_TOKEN, User.getInstance().getAccesstoken())
                .setJsonObjectBody(requestBody)
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        circularProgressBar.setVisibility(View.GONE);
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_Netork_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int status = result.getHeaders().code();
                        JsonObject resultObject = result.getResult();
                        String value = String.valueOf(resultObject);
                        try {
                            JSONObject jsonObject = new JSONObject(value);
                            message = jsonObject.getString("message");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        switch (status) {
                            case 422:
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                break;
                            case 400:
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                break;
                            case 500:
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                break;
                            case 200:
                            case 202:
                                loginActivity.onSuccess(resultObject, requestCode, status);
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    @Override
    public void onSuccess(JsonObject data, int requestCode, int statusCode) {
        switch (requestCode) {
            case REQUEST_FARE_DETAILS:
                ParseFareDetailsResult parseFareDetailsResult = new ParseFareDetailsResult();
                parseFareDetailsResult.execute(data.toString());
                break;
        }
    }

    @Override
    public void onError(String msg) {
    }


    class ParseFareDetailsResult extends AsyncTask<String, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... strings) {
            String email, accessToken, phoneNumber, userId, message, flag = "0";
            HashMap<String, String> map = new HashMap<>();
            String response = strings[0];
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray data = jsonObject.getJSONArray("response");
                message = jsonObject.getString("message");
                flag = jsonObject.getString("flag");
                map.put("flag", flag);
                map.put("message", message);
                JSONObject smallTraveller = data.getJSONObject(0);
                JSONObject largeTraveller = data.getJSONObject(1);
                map.put(ConstantValues.CAR_NAME, smallTraveller.getString("car_name"));
                map.put(ConstantValues.CAR_TYPE, smallTraveller.getString("car_type"));
                map.put(ConstantValues.TOTAL_FARE, smallTraveller.getString("total_fare"));
                map.put(ConstantValues.TOTAL_Distance, smallTraveller.getString("distance"));

                map.put(ConstantValues.LARGE_CAR_NAME, largeTraveller.getString("car_name"));
                map.put(ConstantValues.LARGE_CAR_TYPE, largeTraveller.getString("car_type"));
                map.put(ConstantValues.LARGE_TOTAL_FARE, largeTraveller.getString("total_fare"));
                map.put(ConstantValues.LARGE_TOTAL_Distance, largeTraveller.getString("distance"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> hashMap) {
            circularProgressBar.setVisibility(View.GONE);
            //     String flag=hashMap.get("flag");
            //   String message=hashMap.get("message");
            setDataToViews(hashMap);
        }
    }

    private void setDataToViews(HashMap<String, String> hashMap) {
        //   smallPickupCost=hashMap.get(ConstantValues.TOTAL_FARE);
        // largePickupCost=hashMap.get(ConstantValues.LARGE_TOTAL_FARE);

        smallPickupDistance = hashMap.get(ConstantValues.TOTAL_Distance);
        largePickupDistance = hashMap.get(ConstantValues.LARGE_TOTAL_Distance);

        double value = 0.0;
        double values22 = 0.0;
        double value2 = 0.0;
        double values = 0.0;

        try {
            value = Double.parseDouble(hashMap.get(ConstantValues.TOTAL_FARE));
            values = Double.parseDouble(new DecimalFormat("#.##").format(value));
            //  Log.d("TAGS", String.valueOf(values));
        } catch (Exception e) {
        }

        try {

            value2 = Double.parseDouble(hashMap.get(ConstantValues.LARGE_TOTAL_FARE));
            values22 = Double.parseDouble(new DecimalFormat("#.##").format(value2));
            // Log.d("TAGS", String.valueOf(values22));}
        } catch (Exception e) {
        }

        smallPickupCost = "" + values;
        largePickupCost = "" + values22;
        txtsmallCost.setText("Fare Cost: $" + values);
        txtLargeCost.setText("Fare Cost: $" + values22);

    }

}
