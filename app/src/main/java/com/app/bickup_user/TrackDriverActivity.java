package com.app.bickup_user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bickup_user.broadcastreciever.InternetConnectionBroadcast;
import com.app.bickup_user.controller.AppController;
import com.app.bickup_user.utility.CommonMethods;
import com.app.bickup_user.utility.ConstantValues;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;

import static com.app.bickup_user.R.id.btn_asign;

public class TrackDriverActivity extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener, InternetConnectionBroadcast.ConnectivityRecieverListener {

    public static String OPENTYPESGOODS="opentypesgoods";
    private TextView txtTrackStatus;
    private TextView txtHeaderText;
    private TextView txtDriverName;
    private TextView txtDriverTexiAddress;
    private TextView txtDriverNameBottomSheet;
    private TextView txtDriverTexiAddressBottomSheet;
    private ImageView imgDriverImage;
    private ImageView imgDriverImageBottomSheet;
    private ImageView callDriver;
    private ImageView callDriverBottomSheet;
    private Button btnAssignAnother;
    private Button btnAssignAnotherBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private ImageView imgBackButton;
    private RelativeLayout rlBottomSheet;

    private GoogleMap map;
    private Typeface mTypefaceRegular;
    private Typeface mTypefaceBold;
    private Activity activity;
    private CoordinatorLayout mCoordinatorLayout;
    private boolean mIsConnected;
    private Context mActivityreference;
    private Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // overridePendingTransition(R.anim.slide_in, R.anim._slide_out);
        setContentView(R.layout.activity_track_driver);

        setGoogleMap();
        initiTializeViews();
    }

    private void setGoogleMap() {
        SupportMapFragment mAupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_track_driver);
        mAupportMapFragment.getMapAsync(this);

    }

    private void initiTializeViews() {
        mCoordinatorLayout=(CoordinatorLayout) findViewById(R.id.coordinator_track_driver);
        activity=this;
        mActivityreference=this;
        mTypefaceRegular= Typeface.createFromAsset(activity.getAssets(), ConstantValues.TYPEFACE_REGULAR);
        mTypefaceBold=Typeface.createFromAsset(activity.getAssets(), ConstantValues.TYPEFACE_BOLD);
        txtHeaderText=(TextView)findViewById(R.id.txt_activty_header);
        txtHeaderText.setText(getString(R.string.txt_tracking_driver));
        txtTrackStatus=(TextView)findViewById(R.id.txt_track_status);

        txtDriverName=(TextView)findViewById(R.id.txt_drver_name);

        txtDriverNameBottomSheet=(TextView)findViewById(R.id.txt_drver_name_bottomsheet);
        txtDriverTexiAddress=(TextView)findViewById(R.id.txt_drver_address);
        txtDriverTexiAddressBottomSheet=(TextView)findViewById(R.id.txt_drver_address_bottomsheet);
        btnAssignAnother=(Button)findViewById(btn_asign);
        btnAssignAnotherBottomSheet=(Button)findViewById(R.id.btn_asign_bottomsheet);
        imgDriverImageBottomSheet=(ImageView)findViewById(R.id.img_driver_bottomshet);
        imgDriverImage=(ImageView)findViewById(R.id.img_driver);
        callDriverBottomSheet=(ImageView)findViewById(R.id.img_call_bottomsheet);
        callDriver=(ImageView)findViewById(R.id.call_driver);
        ImageView imgBack=(ImageView)findViewById(R.id.backImage_header);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(this);
        btnAssignAnotherBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetBehavior.setPeekHeight(0);
            }
        });

        btnAssignAnother.setOnClickListener(this);

        rlBottomSheet=(RelativeLayout)findViewById(R.id.rl_bottomSheet);
        rlBottomSheet.setOnClickListener(this);


        txtHeaderText.setTypeface(mTypefaceRegular);
        txtTrackStatus.setTypeface(mTypefaceRegular);
        txtDriverName.setTypeface(mTypefaceRegular);
        txtDriverTexiAddress.setTypeface(mTypefaceRegular);
        txtDriverNameBottomSheet.setTypeface(mTypefaceRegular);
        txtDriverTexiAddressBottomSheet.setTypeface(mTypefaceRegular);
        btnAssignAnother.setTypeface(mTypefaceRegular);
        btnAssignAnotherBottomSheet.setTypeface(mTypefaceRegular);

        setTypeFaceToViews();

        View bottomSheet = findViewById(R.id.design_bottom_sheet);


        mBottomSheetBehavior= BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setPeekHeight(0);

 mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if(newState==BottomSheetBehavior.STATE_COLLAPSED){
            //toolbarLyout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

    }
});
        txtTrackStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   toolbarLyout.setVisibility(View.GONE);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);



            }
        });
    }

    private void hidebottom() {
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

    }

    private void showPopUp() {
        final Dialog openDialog = new Dialog(this);
       openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.assign_driver_dialog);
        openDialog.setTitle("Custom Dialog Box");
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btnAgree = (Button)openDialog.findViewById(R.id.btn_agree);
        Button btnDisAgree = (Button)openDialog.findViewById(R.id.btn_disagree);

        btnAgree.setTypeface(mTypefaceRegular);
        btnDisAgree.setTypeface(mTypefaceRegular);
        btnAgree.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openDialog.dismiss();
                Intent intent=new Intent(TrackDriverActivity.this,TypesGoods.class);
                intent.putExtra(OPENTYPESGOODS,1);
                startActivity(intent);
            }
        });
        btnDisAgree.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openDialog.dismiss();

            }
        });
        openDialog.show();

    }
    private void setTypeFaceToViews() {
        TextView txtTrackStatusBottomSheet=(TextView)findViewById(R.id.track_status_bottomSheet);
        txtTrackStatusBottomSheet.setTypeface(mTypefaceRegular);
        TextView txtBookingStatusTime=(TextView)findViewById(R.id.txt_booking_status_time);
        TextView txtBookingStatus=(TextView)findViewById(R.id.txt_booking_status);
        TextView txtOnTheWayTime=(TextView)findViewById(R.id.txt_on_the_way_time);
        TextView txtOnTheWay=(TextView)findViewById(R.id.txt_on_the_way);
        TextView txtArrivedTime=(TextView)findViewById(R.id.txt_arrived_time);
        TextView txtArrived=(TextView)findViewById(R.id.txt_arrived);
        TextView txtLoadingTime=(TextView)findViewById(R.id.txt_loading_time);
        TextView txtLoading=(TextView)findViewById(R.id.txt_loading_time);
        TextView txtEnrouteTime=(TextView)findViewById(R.id.txt_enroute_time);
        TextView txtEnroute=(TextView)findViewById(R.id.txt_enroute);
        TextView txtReachedDropOff=(TextView)findViewById(R.id.txt_reached_drop_off);

        txtBookingStatusTime.setTypeface(mTypefaceRegular);
        txtBookingStatus.setTypeface(mTypefaceRegular);
        txtOnTheWayTime.setTypeface(mTypefaceRegular);
        txtOnTheWay.setTypeface(mTypefaceRegular);
        txtArrivedTime.setTypeface(mTypefaceRegular);
        txtArrived.setTypeface(mTypefaceRegular);
        txtLoading.setTypeface(mTypefaceRegular);
        txtLoadingTime.setTypeface(mTypefaceRegular);
        txtEnrouteTime.setTypeface(mTypefaceRegular);
        txtEnroute.setTypeface(mTypefaceRegular);
        txtReachedDropOff.setTypeface(mTypefaceRegular);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null) {
            this.map = googleMap;
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style);
            this.map.setMapStyle(style);
         /*   if(mLocationPermission){
                getUserCurrentLocation();
            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        ConstantValues.PERMISSION_CURRENTLOCATION);
            }
*/
        }




    }


    @Override
    protected void onResume() {
        super.onResume();
        checkInternetconnection();
        if (AppController.getInstance() != null) {
            AppController.getInstance().setConnectivityListener(this);
        }
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

    public void showSnackBar(String mString) {
       /* snackbar = Snackbar
                .make(mCoordinatorLayout, mString, Snackbar.LENGTH_INDEFINITE);
        snackbar.setText(mString);
        snackbar.show();*/
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

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.rl_bottomSheet:
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetBehavior.setPeekHeight(0);
               // toolbarLyout.setVisibility(View.VISIBLE);
                break;
            case R.id.backImage_header:
               finish();
                break;
            case R.id.btn_asign:
               showPopUp();
                break;
        }

    }

   /* private void getUserCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermission = true;
            mCurrentLocation=mLocationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mLocationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            updateLocationUI();
            return;
        }

    }*/
}
