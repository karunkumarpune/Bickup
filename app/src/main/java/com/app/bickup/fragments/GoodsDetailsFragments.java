package com.app.bickup.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bickup.GlobleVariable.GloableVariable;
import com.app.bickup.GoodsActivity;
import com.app.bickup.R;
import com.app.bickup.adapter.GoodAddAdapter;
import com.app.bickup.adapter.GoodsImagesAdapter;
import com.app.bickup.controller.NetworkCallBack;
import com.app.bickup.controller.WebAPIManager;
import com.app.bickup.interfaces.HandlerGoodsNavigations;
import com.app.bickup.model.GoodsAddModel;
import com.app.bickup.model.GoodsAndHelper;
import com.app.bickup.model.User;
import com.app.bickup.utility.select.MainActivity;
import com.app.bickup.utility.ConstantValues;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.stacktips.view.CalendarListener;
import com.stacktips.view.CustomCalendarView;
import com.xw.repo.BubbleSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


public class GoodsDetailsFragments extends Fragment implements View.OnClickListener, NetworkCallBack {

    public static String TAG = GoodsDetailsFragments.class.getSimpleName();
    private GoodsActivity mGoodsActivity;
    private Activity mActivity;
    private Button btnSaveBooking;
    private Typeface mTypefaceRegular;
    private Typeface mTypefaceBold;
    private GoodsActivity mActivityReference;
    private ImageView imgOneHelper;
    private ImageView imgTwoHelper;
    private ImageView imgTickOneHelper;
    private ImageView imgTickTwoHelper;
    private EditText edtDescription;
    private TextView btnComeNow;
    private TextView btnComeLater;
    private ImageView imgUploadImage;
    private ImageView imghelperCheckBox;
    private ImageView imgTypesGoods;
    private RecyclerView recyclerView;
    private int REQUEST_GOODS = 100;
    private boolean isFirstTime = true;
    private ArrayList<Bitmap> listImagesGoods;
    private int imagecount = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private CircularProgressView circularProgressBar;
    private String message = "";
    private GoodsAndHelper goodsAndHelper;
    private TextView txtdateTime;

    private RecyclerView types_good_recyclerView;
    private ArrayList<GoodsAddModel> lists;
    private GoodAddAdapter goodAddAdapter;
    private String select_Data;
    private String add_date_time;
    private String add_mit="";
    private String add_hours="";
    private TextView txtdateTime_;
    private String current_Date;
    public GoodsDetailsFragments() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap bitmap1 = null, bitmap2 = null, bitmap3 = null, bitmap4 = null;
        goodsAndHelper = new GoodsAndHelper();
        lists = new ArrayList<>();
        listImagesGoods = new ArrayList<>();
        listImagesGoods.add(bitmap1);
        GloableVariable.Tag_helper="1";
        GloableVariable.Tag_Good_Details_Comming_time_type="1";

    }

    private void showPopUp(int choosetraveller) {
        final Dialog openDialog = new Dialog(mActivityReference);
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.calender_view_dialog);
        openDialog.setTitle("Custom Dialog Box");
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
       /* TextView travellerName = (TextView)openDialog.findViewById(R.id.txt_traveller_name_dialog);

        TextView travellerCost = (TextView)openDialog.findViewById(R.id.txt_traveller_cost);
        ImageView travellerImage = (ImageView)openDialog.findViewById(R.id.img_traveller);*/
        Button btnCancel = (Button) openDialog.findViewById(R.id.btn_cancel);
        final CustomCalendarView calendarView = (CustomCalendarView) openDialog.findViewById(R.id.calendar_view);

        Button btnok = (Button) openDialog.findViewById(R.id.btn_ok);



        //Initialize calendar with date
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());

        add_date_time= new SimpleDateFormat("dd/MM/yyyy").format(new Date());



//Show Monday as first date of week
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

//Show/hide overflow days of a month
        calendarView.setShowOverflowDate(false);

//call refreshCalendar to update calendar the view
        calendarView.refreshCalendar(currentCalendar);

//Handling custom calendar events
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String dates=df.format(date);
                add_date_time=dates;


            }

            @Override
            public void onMonthChanged(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
              //  Toast.makeText(getApplicationContext(), df.format(date), Toast.LENGTH_SHORT).show();
            }
        });






        btnCancel.setTypeface(mTypefaceRegular);
        btnok.setTypeface(mTypefaceRegular);
       /* travellerName.setTypeface(mTypefaceBold);
        travellerCost.setTypeface(mTypefaceRegular);
        btnDisagree.setTypeface(mTypefaceBold);
        btnAgree.setTypeface(mTypefaceBold);*/
        if (choosetraveller == 1) {

        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDialog.dismiss();
            }
        });
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 openDialog.dismiss();
                 showPopUpForTime(0);

            }
        });
        openDialog.show();

    }


    private void showPopUpForTime(int choosetraveller) {
        final Dialog openDialog = new Dialog(mActivityReference);
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.time_dialog);
        openDialog.setTitle("Custom Dialog Box");
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final TextView fifteenMinuit = (TextView) openDialog.findViewById(R.id.fifteen_minuit);
        final TextView thirtyMinuit = (TextView) openDialog.findViewById(R.id.thirty_minuit);
        final TextView fourtyMinuit = (TextView) openDialog.findViewById(R.id.fourty_minuit);
        final TextView minuitText = (TextView) openDialog.findViewById(R.id.minuit_txt);
        final TextView hourtext = (TextView) openDialog.findViewById(R.id.hourtext);
        minuitText.setTypeface(mTypefaceRegular);
        fifteenMinuit.setTypeface(mTypefaceRegular);
        thirtyMinuit.setTypeface(mTypefaceRegular);
        fourtyMinuit.setTypeface(mTypefaceRegular);
        setColorWhite(fifteenMinuit);
        setColorWhite(thirtyMinuit);
        setColorWhite(fourtyMinuit);
        final com.xw.repo.BubbleSeekBar seekBar = (com.xw.repo.BubbleSeekBar) openDialog.findViewById(R.id.seekbar);
        add_hours="1";
        seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                hourtext.setText(String.valueOf(progress));
                add_hours=String.valueOf(progress);
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });

        add_mit="15";
        fifteenMinuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minuitText.setText("15");
                add_mit="15";
                fifteenMinuit.setTextColor(getActivity().getResources().getColor(R.color.grey_text_color));
                thirtyMinuit.setTextColor(getActivity().getResources().getColor(R.color.greyColor));
                fourtyMinuit.setTextColor(getActivity().getResources().getColor(R.color.greyColor));
                setColorYellow(view);
                setColorWhite(thirtyMinuit);
                setColorWhite(fourtyMinuit);

            }


        });

        thirtyMinuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minuitText.setText("30");
                add_mit="30";

                thirtyMinuit.setTextColor(getActivity().getResources().getColor(R.color.grey_text_color));
                fifteenMinuit.setTextColor(getActivity().getResources().getColor(R.color.greyColor));
                fourtyMinuit.setTextColor(getActivity().getResources().getColor(R.color.greyColor));
                setColorYellow(view);
                setColorWhite(fifteenMinuit);
                setColorWhite(fourtyMinuit);
            }
        });

        fourtyMinuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setColorYellow(view);
                minuitText.setText("45");
                add_mit="45";
                fourtyMinuit.setTextColor(getActivity().getResources().getColor(R.color.grey_text_color));
                fifteenMinuit.setTextColor(getActivity().getResources().getColor(R.color.greyColor));
                thirtyMinuit.setTextColor(getActivity().getResources().getColor(R.color.greyColor));
                setColorWhite(thirtyMinuit);
                setColorWhite(fifteenMinuit);
            }
        });



        TextView travellerCost = (TextView) openDialog.findViewById(R.id.txt_traveller_cost);
        ImageView travellerImage = (ImageView) openDialog.findViewById(R.id.img_traveller);
        Button btnCancel = (Button) openDialog.findViewById(R.id.btn_cancel);

        Button btnok = (Button) openDialog.findViewById(R.id.btn_ok);
        btnCancel.setTypeface(mTypefaceRegular);
        btnok.setTypeface(mTypefaceRegular);
       /* travellerName.setTypeface(mTypefaceBold);
        travellerCost.setTypeface(mTypefaceRegular);
        btnDisagree.setTypeface(mTypefaceBold);
        btnAgree.setTypeface(mTypefaceBold);*/
        if (choosetraveller == 1) {

        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDialog.dismiss();
            }
        });
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog.dismiss();

                Log.d("TAGS Time",add_date_time +"    "+add_hours+":"+ add_mit);
                    GloableVariable.Tag_Good_Details_Comming_Date_time=add_date_time +"  "+add_hours+":"+ add_mit;

                if (GloableVariable.Tag_Good_Details_Comming_time_type.equals("2")){
                    txtdateTime_.setText(GloableVariable.Tag_Good_Details_Comming_Date_time);
                }

            }
        });
        openDialog.show();


     //   txtdateTime.setText(""+GloableVariable.Tag_Good_Details_Comming_Date_time);

    }

    private void setColorYellow(View view) {
        TextView view1 = (TextView) view;
        StateListDrawable bgShape = (StateListDrawable) view1.getBackground();
        bgShape.setColorFilter(Color.parseColor("#e6ba13"), PorterDuff.Mode.SRC_ATOP);
    }

    private void setColorWhite(TextView view) {
        StateListDrawable bgShape = (StateListDrawable) view.getBackground().mutate();
        bgShape.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_goods_details_fragments, container, false);
        try {
            initializeViews(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GloableVariable.Tag_Good_Details_Description=edtDescription.getText().toString();

        return view;
    }

    private void initializeViews(View view) throws JSONException {

        types_good_recyclerView = view.findViewById(R.id.types_good_recyclerView);
        circularProgressBar = (CircularProgressView) view.findViewById(R.id.progress_view);
        mTypefaceRegular = Typeface.createFromAsset(mActivityReference.getAssets(), ConstantValues.TYPEFACE_REGULAR);
        mTypefaceBold = Typeface.createFromAsset(mActivityReference.getAssets(), ConstantValues.TYPEFACE_BOLD);
        setTypefaceToviews(view);

        imgOneHelper = (ImageView) view.findViewById(R.id.img_helper_single);
        imgTwoHelper = (ImageView) view.findViewById(R.id.img_double_helper);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerImages);
        setImagesList();
        AddGoodOption();

        imgTickOneHelper = (ImageView) view.findViewById(R.id.tick_single_helper);
        imgTickTwoHelper = (ImageView) view.findViewById(R.id.tick_double_helper);
        imghelperCheckBox = (ImageView) view.findViewById(R.id.check_box_img);
        imgTypesGoods = (ImageView) view.findViewById(R.id.img_types_goods);
        edtDescription = (EditText) view.findViewById(R.id.edt_description);
        btnComeNow = (TextView) view.findViewById(R.id.btn_come_now);
        btnComeLater = (TextView) view.findViewById(R.id.btn_come_later);
        imgUploadImage = (ImageView) view.findViewById(R.id.img_upload_image);

        imgOneHelper.setOnClickListener(this);
        imgUploadImage.setOnClickListener(this);
        imgOneHelper.setTag(R.drawable.ac_sing_helper);
        imgTwoHelper.setOnClickListener(this);
        imgTwoHelper.setTag(R.drawable.de_double_helper);
        btnComeNow.setOnClickListener(this);
        btnComeNow.setTag(true);
        btnComeLater.setOnClickListener(this);
        btnComeLater.setTag(false);



        imgTypesGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (goodsAndHelper.getGoods() != null) {
                    if (goodsAndHelper.getGoods().size() > 0) {
                      /*  Intent intent = new Intent(mActivityReference, TypesGoods.class);
                        intent.putExtra(TrackDriverActivity.OPENTYPESGOODS, 0);
                        intent.putExtra(ConstantValues.GOODSDETAILS, goodsAndHelper);
                        startActivity(intent);*/



                        StringBuilder  sb=new StringBuilder();
                        for(GoodsAddModel m : lists){
                            sb.append(m.getId()+"~");
                        }
                        try {
                            sb.deleteCharAt(sb.length()-1);
                            Log.d("TAGS ","Response StringBuilder: "+sb.toString());
                            startActivity(new Intent(mActivityReference, MainActivity.class).putExtra("key",sb.toString()).putExtra(ConstantValues.GOODSDETAILS, goodsAndHelper));
                        } catch (Exception e) {
                            startActivity(new Intent(mActivityReference, MainActivity.class).putExtra("key",sb.toString()).putExtra(ConstantValues.GOODSDETAILS, goodsAndHelper));
                        }





                    }
                }
            }

        });

        imghelperCheckBox.setOnClickListener(this);
        imghelperCheckBox.setTag(false);

        edtDescription.setTypeface(mTypefaceRegular);
        btnComeNow.setTypeface(mTypefaceRegular);
        btnComeLater.setTypeface(mTypefaceRegular);

        btnSaveBooking = (Button) view.findViewById(R.id.btn_save_booking);
        btnSaveBooking.setOnClickListener(this);
        btnSaveBooking.setTypeface(mTypefaceRegular);


        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        GloableVariable.Tag_Good_Details_Comming_Date_time=date;
        txtdateTime_.setText(date);
    }

    private void setImagesList() {
        GoodsImagesAdapter goodsImagesAdapter = new GoodsImagesAdapter(mActivityReference, listImagesGoods);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivityReference, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(goodsImagesAdapter);
    }

    private void setTypefaceToviews(View view) {
        TextView txtHelper = (TextView) view.findViewById(R.id.txt_helper);
        TextView txtChooseHelper = (TextView) view.findViewById(R.id.txt_choose_helper);
        TextView txtNohelperRequired = (TextView) view.findViewById(R.id.txt_no_helper_required);
        TextView txtDescription = (TextView) view.findViewById(R.id.txt_description);
        TextView txtComingTime = (TextView) view.findViewById(R.id.txt_coming_time);
        txtdateTime_ = (TextView) view.findViewById(R.id.txt_date_time);

        txtHelper.setTypeface(mTypefaceRegular);
        txtChooseHelper.setTypeface(mTypefaceRegular);
        txtComingTime.setTypeface(mTypefaceRegular);
        txtNohelperRequired.setTypeface(mTypefaceRegular);
        txtDescription.setTypeface(mTypefaceRegular);
        txtdateTime_.setTypeface(mTypefaceRegular);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getGoodsDetails();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityReference = (GoodsActivity) context;

    }

    @Override
    public void onResume() {
        super.onResume();
        GloableVariable.Tag_helper="1";
        SharedPreferences sp = getActivity().getSharedPreferences("LoginInfos", 0);
        select_Data= sp.getString("key", "");
        Log.d("TAGS Data:",select_Data);


        if(select_Data!=null){
            lists.clear();
            JSONArray array= null;
            try {
                array = new JSONArray(select_Data);

            for(int i=0;i<array.length();i++){
                JSONObject obj=array.getJSONObject(i);
                lists.add(new GoodsAddModel(obj.getString("id"),obj.getString("name")));
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public void onClick(View view) {
        HandlerGoodsNavigations handlerGoodsNavigations = mActivityReference;
        int id = view.getId();
        switch (id) {
            case R.id.btn_save_booking:
                if(listImagesGoods.size()==1) {
                    Toast.makeText(getActivity(), "Please upload Iamge", Toast.LENGTH_SHORT).show();
                }else {
                    // Toast.makeText(getActivity(), "Good", Toast.LENGTH_SHORT).show();
                    handlerGoodsNavigations.callBookingDetailsFragment(listImagesGoods);
                    GloableVariable.Tag_Good_Details_Description=edtDescription.getText().toString();

                }




             /*   Log.d("TAGS","Helper   "+GloableVariable.Tag_helper);
                Log.d("TAGS","Descrtion  "+GloableVariable.Tag_Good_Details_Description);
                Log.d("TAGS","Comming_Type   "+GloableVariable.Tag_Good_Details_Comming_time_type);
                Log.d("TAGS","Comming_Date Time  "+GloableVariable.Tag_Good_Details_Comming_Date_time);
*/

                break;
            case R.id.btn_come_now:
                btnComeNow.setBackground(mActivityReference.getResources().getDrawable(R.drawable.sm_btn));
                btnComeLater.setBackgroundColor(mActivityReference.getResources().getColor(R.color.white));
                btnComeNow.setTextColor(mActivityReference.getResources().getColor(R.color.white));
                btnComeLater.setTextColor(mActivityReference.getResources().getColor(R.color.grey_text_color));
                btnComeNow.setTag(true);
                btnComeLater.setTag(false);
                String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                GloableVariable.Tag_Good_Details_Comming_Date_time=date;
                GloableVariable.Tag_Good_Details_Comming_time_type="1";
                txtdateTime_.setText(date);
                break;
            case R.id.btn_come_later:
                btnComeNow.setBackgroundColor(mActivityReference.getResources().getColor(R.color.white));
                btnComeLater.setBackground(mActivityReference.getResources().getDrawable(R.drawable.sm_btn));
                btnComeNow.setTextColor(mActivityReference.getResources().getColor(R.color.grey_text_color));
                btnComeLater.setTextColor(mActivityReference.getResources().getColor(R.color.white));
                btnComeNow.setTag(false);
                btnComeLater.setTag(true);
                showPopUp(0);
                GloableVariable.Tag_Good_Details_Comming_time_type="2";
                break;
            case R.id.img_helper_single:
                if (!(boolean) imghelperCheckBox.getTag()) {
                    imgOneHelper.setImageResource(R.drawable.ac_sing_helper);
                    imgTwoHelper.setImageResource(R.drawable.de_double_helper);
                    imgTickTwoHelper.setVisibility(View.GONE);
                    imgTickOneHelper.setVisibility(View.VISIBLE);
                    imgOneHelper.setTag(true);
                    imgTwoHelper.setTag(false);
                    GloableVariable.Tag_helper="1";
                }
                break;
            case R.id.img_double_helper:
                if (!(boolean) imghelperCheckBox.getTag()) {
                    imgOneHelper.setImageResource(R.drawable.de_sing_helper);
                    imgTwoHelper.setImageResource(R.drawable.ac_double_helper);
                    imgTickTwoHelper.setVisibility(View.VISIBLE);
                    imgTickOneHelper.setVisibility(View.GONE);
                    imgOneHelper.setTag(false);
                    imgTwoHelper.setTag(true);
                    GloableVariable.Tag_helper="2";
                }
                break;
            case R.id.check_box_img:
                boolean ischecked = (boolean) imghelperCheckBox.getTag();
                if (!ischecked) {
                    imghelperCheckBox.setImageResource(R.drawable.ac_checkbox);
                    imgOneHelper.setImageResource(R.drawable.de_sing_helper);
                    imgTwoHelper.setImageResource(R.drawable.de_double_helper);
                    imgTickTwoHelper.setVisibility(View.GONE);
                    imgTickOneHelper.setVisibility(View.GONE);
                    imghelperCheckBox.setTag(true);
                    GloableVariable.Tag_helper="0";
                } else {
                    imghelperCheckBox.setImageResource(R.drawable.de_checkbox);
                    imghelperCheckBox.setTag(false);
                    imgOneHelper.setImageResource(R.drawable.ac_sing_helper);
                    imgTwoHelper.setImageResource(R.drawable.de_double_helper);
                    imgTickTwoHelper.setVisibility(View.GONE);
                    imgTickOneHelper.setVisibility(View.VISIBLE);
                    imgOneHelper.setTag(true);
                    imgTwoHelper.setTag(false);

                }
                break;
            case R.id.img_upload_image:
                checkCameraPermission();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 111: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                }
                return;
            }
        }
    }

    public void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(mActivityReference, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mActivityReference, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mActivityReference, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            if (ContextCompat.checkSelfPermission(mActivityReference,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivityReference,
                        new String[]{Manifest.permission.CAMERA},
                        111);
            }
            if (ContextCompat.checkSelfPermission(mActivityReference,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivityReference,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        111);
            }
            if (ContextCompat.checkSelfPermission(mActivityReference,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivityReference,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        111);
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mActivityReference.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

           if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
               if (data != null) {
                   Bundle extras = data.getExtras();
                   Bitmap imageBitmap = (Bitmap) extras.get("data");
                   GoodsImagesAdapter goodsImagesAdapter = (GoodsImagesAdapter) recyclerView.getAdapter();
                   goodsImagesAdapter.notiFydata(imageBitmap, imagecount);

                   imagecount++;

               }
           }
    }


    public void getGoodsDetails() {
        String createUserUrl = WebAPIManager.getInstance().getTypesGoods();
        final JsonObject requestBody = new JsonObject();
        callAPI(requestBody, createUserUrl, this, 60 * 1000, REQUEST_GOODS);
    }


    private void callAPI(JsonObject requestBody, String createUserUrl, final NetworkCallBack loginActivity, int timeOut, final int requestCode) {
        circularProgressBar.setVisibility(View.VISIBLE);
        Ion.with(this)
                .load("GET", createUserUrl)
                .setHeader(ConstantValues.USER_ACCESS_TOKEN, User.getInstance().getAccesstoken())
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
        ParseGoodsDetails parseSaveBooking = new ParseGoodsDetails();
        parseSaveBooking.execute(String.valueOf(data));
    }

    @Override
    public void onError(String msg) {

    }

    class ParseGoodsDetails extends AsyncTask<String, Object, GoodsAndHelper> {
        @Override
        protected GoodsAndHelper doInBackground(String... strings) {
            SharedPreferences sp;
            SharedPreferences.Editor editor;
            String message, flag = "0";
            GoodsAndHelper goodsAndHelper = new GoodsAndHelper();
            ArrayList<GoodsAndHelper.Goods> goodses = new ArrayList<>();
            ArrayList<GoodsAndHelper.Helper> helpers = new ArrayList<>();
            HashMap<String, String> map = new HashMap<>();
            String response = strings[0];
            try {
                JSONObject jsonObject = new JSONObject(response);
                message = jsonObject.getString("message");
                flag = jsonObject.getString("flag");
                map.put("flag", flag);
                map.put("message", message);
                JSONObject data = jsonObject.getJSONObject("response");
                JSONArray goodsArray = data.getJSONArray("types_of_goods");
                JSONArray helperArray = data.getJSONArray("helper");

                sp = getActivity().getSharedPreferences("helper", 0);
                editor = sp.edit();
                editor.clear();
                editor.commit();
                editor.putString("key_helper", helperArray.toString());
                editor.commit();


                for (int i = 0; i < goodsArray.length(); i++) {
                    GoodsAndHelper.Goods goods = goodsAndHelper.createGoodsObject();
                    JSONObject jsonObject1 = goodsArray.getJSONObject(i);
                    goods.setGoodsID(jsonObject1.getString("goods_id"));
                    goods.setGoodsName(jsonObject1.getString("name"));
                    goods.setSelected(false);
                    goodses.add(goods);
                }
                for (int i = 0; i < helperArray.length(); i++) {
                    GoodsAndHelper.Helper helper = goodsAndHelper.createHelperObject();
                    JSONObject jsonObject1 = helperArray.getJSONObject(i);
                    helper.setHelperID(jsonObject1.getString("helper_id"));
                    helper.setHelperCount(jsonObject1.getString("person_count"));
                    helper.setPrice(jsonObject1.getString("price"));
                    helper.setSelected(false);
                    helpers.add(helper);
                }
                goodsAndHelper.setGoods(goodses);
                goodsAndHelper.setHelper(helpers);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return goodsAndHelper;
        }

        @Override
        protected void onPostExecute(GoodsAndHelper goodsAndHelper) {
            super.onPostExecute(goodsAndHelper);
            GoodsDetailsFragments.this.goodsAndHelper = goodsAndHelper;
        }
    }

    private void AddGoodOption() {

        goodAddAdapter = new GoodAddAdapter(mActivityReference, lists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivityReference, LinearLayoutManager.HORIZONTAL, false);
        types_good_recyclerView.setLayoutManager(mLayoutManager);
        types_good_recyclerView.setItemAnimator(new DefaultItemAnimator());
        types_good_recyclerView.setAdapter(goodAddAdapter);


    }


}
