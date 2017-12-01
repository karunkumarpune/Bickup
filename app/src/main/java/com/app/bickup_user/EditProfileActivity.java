package com.app.bickup_user;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bickup_user.controller.NetworkCallBack;
import com.app.bickup_user.controller.WebAPIManager;
import com.app.bickup_user.model.User;
import com.app.bickup_user.utility.CommonMethods;
import com.app.bickup_user.utility.ConstantValues;
import com.app.bickup_user.utility.Image;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, NetworkCallBack {


    private Activity mActivityReference;
    private EditText edtFullName;
    private EditText edtMobileNumber;
    private EditText edtEmailID;
    private Button btnSave;
    private Typeface mTypefaceRegular;
    private Typeface mTypefaceBold;
    private TextView tv_header;
    private ImageView imgBack;
    private ImageView userImage;
    private CircularProgressView circularProgressView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private File imgFile;
    private SharedPreferences sharedPreferences;
    private Dialog selectImageDailog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        InitializeViews();
    }


    private void InitializeViews() {
        mActivityReference = this;
        circularProgressView = (CircularProgressView) findViewById(R.id.progress_view);
        tv_header = (TextView) findViewById(R.id.txt_activty_header);
        tv_header.setText(getResources().getString(R.string.txt_edt_profile));

        userImage = (ImageView) findViewById(R.id.userImage);
        imgBack = (ImageView) findViewById(R.id.backImage_header);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(this);

        mTypefaceRegular = Typeface.createFromAsset(mActivityReference.getAssets(), ConstantValues.TYPEFACE_REGULAR);
        mTypefaceBold = Typeface.createFromAsset(mActivityReference.getAssets(), ConstantValues.TYPEFACE_BOLD);
        edtFullName = (EditText) findViewById(R.id.edt_full_name_profile_page);

        edtMobileNumber = (EditText) findViewById(R.id.edt_mobile_number_profile);
        edtEmailID = (EditText) findViewById(R.id.edt_email_profile);
        btnSave = (Button) findViewById(R.id.btn_save_profile);
        btnSave.setOnClickListener(this);

        edtFullName.setTypeface(mTypefaceRegular);

        edtMobileNumber.setTypeface(mTypefaceRegular);
        userImage.setOnClickListener(this);

        edtEmailID.setTypeface(mTypefaceRegular);
        btnSave.setTypeface(mTypefaceRegular);

        edtFullName.setText(User.getInstance().getFirstName() + " " + User.getInstance().getLastName());
        edtMobileNumber.setText(User.getInstance().getMobileNumber());
        edtEmailID.setText(User.getInstance().getEmail());
        if (User.getInstance().getUserImage() != null) {
            Ion.with(userImage)
                    .placeholder(R.drawable.driver)
                    .error(R.drawable.driver)
                    .load(User.getInstance().getUserImage());
        }
    }


    private boolean validateFields() {
        if (!CommonMethods.getInstance().validateEditFeild(edtFullName.getText().toString())) {
            Toast.makeText(mActivityReference, mActivityReference.getResources().getString(R.string.txt_vaidate_name), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!CommonMethods.getInstance().validateMobileNumber(edtMobileNumber.getText().toString(), 6)) {
            Toast.makeText(mActivityReference, mActivityReference.getResources().getString(R.string.txt_vaidate_mobile_number), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!CommonMethods.getInstance().validateEmailAddress(edtEmailID.getText().toString())) {
            Toast.makeText(mActivityReference, mActivityReference.getResources().getString(R.string.txt_vaidate_emailID), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void prepareuserLogin(final String firstname, final String lastname, final String email) {
        String createUserUrl = WebAPIManager.getInstance().getimageUrl();
        final JsonObject requestBody = new JsonObject();
        editProfile(requestBody, createUserUrl, this, 60 * 1000, 100, firstname, lastname, email);
    }

    private void editProfile(JsonObject requestBody, String createUserUrl, final NetworkCallBack loginActivity, int timeOut, final int requestCode, String firstname, String lastname, String email) {
        final String[] message = new String[1];
        circularProgressView.setVisibility(View.VISIBLE);
        if (imgFile != null) {
            Ion.with(this)
                    .load("PUT", createUserUrl)
                    .setHeader(ConstantValues.USER_ACCESS_TOKEN, User.getInstance().getAccesstoken())
                    .setMultipartFile("file", imgFile)
                    .setMultipartParameter(ConstantValues.USER_FIRSTNAME, firstname)
                    .setMultipartParameter(ConstantValues.USER_LASTNAME, lastname)
                    .setMultipartParameter(ConstantValues.USER_EMAILADDRESS, email)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            circularProgressView.setVisibility(View.GONE);
                            if (e != null) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_Netork_error), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            int status = result.getHeaders().code();
                            JsonObject resultObject = result.getResult();
                            String value = String.valueOf(resultObject);
                            try {
                                JSONObject jsonObject = new JSONObject(value);
                                message[0] = jsonObject.getString("message");
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            switch (status) {
                                case 422:
                                    Toast.makeText(getApplicationContext(), message[0], Toast.LENGTH_SHORT).show();
                                    break;
                                case 400:
                                    Toast.makeText(getApplicationContext(), message[0], Toast.LENGTH_SHORT).show();
                                    break;
                                case 500:
                                    Toast.makeText(getApplicationContext(), message[0], Toast.LENGTH_SHORT).show();
                                    break;
                                case 200:

                                case 202:
                                    loginActivity.onSuccess(resultObject, requestCode, status);
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(), message[0], Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } else {
            Ion.with(this)
                    .load("PUT", createUserUrl)
                    .setHeader(ConstantValues.USER_ACCESS_TOKEN, User.getInstance().getAccesstoken())
                    .setMultipartParameter(ConstantValues.USER_FIRSTNAME, firstname)
                    .setMultipartParameter(ConstantValues.USER_LASTNAME, lastname)
                    .setMultipartParameter(ConstantValues.USER_EMAILADDRESS, email)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            circularProgressView.setVisibility(View.GONE);
                            if (e != null) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_Netork_error), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            int status = result.getHeaders().code();
                            JsonObject resultObject = result.getResult();
                            switch (status) {
                                case 422:
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_user_exist), Toast.LENGTH_SHORT).show();
                                    break;
                                case 400:
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_Netork_error), Toast.LENGTH_SHORT).show();
                                    break;
                                case 500:
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_Netork_error), Toast.LENGTH_SHORT).show();
                                    break;
                                case 200:
                                case 202:
                                    loginActivity.onSuccess(resultObject, requestCode, status);
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_Netork_error), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_save_profile:
                if (validateFields()) {
                    String firstname = "", lastname = "";
                    String fullname = edtFullName.getText().toString();
                    if (fullname.contains(" ")) {
                        String[] array = fullname.split(" ");
                        firstname = array[0];
                        lastname = array[1];
                    }
                    prepareuserLogin(firstname, lastname, edtEmailID.getText().toString());
                }
                break;
            case R.id.backImage_header:
                finish();
                break;
            case R.id.userImage:
                checkCameraPermission();
                break;
        }
    }

    @Override
    public void onSuccess(JsonObject data, int requestCode, int statusCode) {
        switch (requestCode) {
            case 100:
                ParseuserLoginResponse parseuserLoginResponse = new ParseuserLoginResponse();
                parseuserLoginResponse.execute(String.valueOf(data));
                break;

        }
    }

    class ParseuserLoginResponse extends AsyncTask<String, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... strings) {
            String email, accessToken, phoneNumber, userId, message, flag = "0";
            HashMap<String, String> map = new HashMap<>();
            String response = strings[0];
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject data = jsonObject.getJSONObject("response");
                message = jsonObject.getString("message");
                flag = jsonObject.getString("flag");
                map.put("flag", flag);
                map.put("message", message);
                JSONObject jsonObject1 = data.getJSONObject("profile_image");
                String imageUrl = jsonObject1.getString("image_url");
                map.put(ConstantValues.USER_EMAILADDRESS, data.getString("email"));
                map.put(ConstantValues.USER_MOBILENUMBER, data.getString("phone_number"));
                map.put(ConstantValues.USER_ID, data.getString("user_id"));
                map.put(ConstantValues.USER_ACCESS_TOKEN, data.getString("access_token"));
                map.put(ConstantValues.USER_FIRSTNAME, data.getString("first_name"));
                map.put(ConstantValues.USER_LASTNAME, data.getString("last_name"));
                map.put(ConstantValues.COUNTRY_CODE, data.getString("country_code"));
                map.put(ConstantValues.USER_IMAGE, imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> hashMap) {
            String flag = hashMap.get("flag");
            String message = hashMap.get("message");
            if (!flag.equalsIgnoreCase("2")) {
                if (hashMap.size() > 2) {
                    User.getInstance().updateProfile(EditProfileActivity.this, hashMap.get(ConstantValues.USER_EMAILADDRESS), hashMap.get(ConstantValues.USER_FIRSTNAME), hashMap.get(ConstantValues.USER_LASTNAME), ConstantValues.BASE_URL + "/" + hashMap.get(ConstantValues.USER_IMAGE));
                } else {
                    String firstName = "", lastName = "";
                    String fullName = edtFullName.getText().toString();
                    if (fullName.contains(" ")) {
                        String[] array = fullName.split(" ");
                        firstName = array[0];
                        lastName = array[1];
                    }
                    User.getInstance().updateProfile(EditProfileActivity.this, edtEmailID.getText().toString().trim(), firstName, lastName, User.getInstance().getUserImage());
                }
            }


            Intent intent=new Intent(EditProfileActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

            Toast.makeText(mActivityReference, message, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onError(String msg) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 111: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                } else {
                }
                return;
            }
        }
    }


    public void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(mActivityReference, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mActivityReference, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mActivityReference, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            if (ContextCompat.checkSelfPermission(mActivityReference, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivityReference,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        111);
            }
            if (ContextCompat.checkSelfPermission(mActivityReference, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivityReference,
                        new String[]{Manifest.permission.CAMERA},
                        113);
            }

            if (ContextCompat.checkSelfPermission(mActivityReference, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivityReference,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        113);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_OK) {
            if (requestCode == 2) {
                Uri picUri = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                try {
                    Cursor c = mActivityReference.getContentResolver().query(picUri, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    if (picturePath == null) {
                        picturePath = Image.getPath(mActivityReference, picUri);

                        imgFile = new File(picturePath);
                        final int THUMBSIZE = 200;
                        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(picturePath),
                                THUMBSIZE, THUMBSIZE);
                        bitmap = MediaStore.Images.Media.getBitmap(mActivityReference.getContentResolver(), picUri);
                        userImage.setImageBitmap(bitmap);
                    } else {
//                        picturePath = Image.getPath(activity, picUri);
                        imgFile = new File(picturePath);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivityReference.getContentResolver(), picUri);
                        final int THUMBSIZE = 200;
                        bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(picturePath),
                                THUMBSIZE, THUMBSIZE);
                        userImage.setImageBitmap(bitmap);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 0) {
                onCaptureImageResult(data);
            }
        }
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        userImage.setImageBitmap(bitmap);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        imgFile = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            imgFile.createNewFile();
            fo = new FileOutputStream(imgFile);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void selectImage() {
        selectImageDailog = new Dialog(this);
        selectImageDailog.setContentView(R.layout.edit_image_dialog);
        selectImageDailog.setCancelable(true);
        selectImageDailog.setCanceledOnTouchOutside(false);
        LinearLayout camera = (LinearLayout) selectImageDailog.findViewById(R.id.camera);
        final LinearLayout gallery = (LinearLayout) selectImageDailog.findViewById(R.id.gallery);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(captureIntent, 0);
                selectImageDailog.dismiss();


            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = (new Intent("android.intent.action.GET_CONTENT")).setType("image/*");
                startActivityForResult(intent, 2);
                selectImageDailog.dismiss();
            }
        });

        selectImageDailog.show();
    }
}
