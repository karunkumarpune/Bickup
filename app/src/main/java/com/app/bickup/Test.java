package com.app.bickup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.app.bickup.model.User;
import com.app.bickup.utility.ConstantValues;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import static com.app.bickup.controller.WebAPIManager.get_url_Ride;

/**
 * Created by fluper-pc on 25/11/17.
 */

public class Test extends AppCompatActivity {

    String message="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareBooking("21.323","23.4342","KK");
    }


    public void  prepareBooking(final String lattitude, final String longitude,final String buildinName) {
        final JsonObject requestBody=new JsonObject();

        requestBody.addProperty(ConstantValues.LATITUDE, lattitude);
        requestBody.addProperty(ConstantValues.LONGITUDE, longitude);
        requestBody.addProperty(ConstantValues.BUILDING_NAME, buildinName);

        callAPI(requestBody);
        Log.d("TAGS",requestBody.toString());
    }


    private void callAPI(JsonObject requestBody){
         Ion.with(this)
                .load("POST",get_url_Ride)
                .setHeader(ConstantValues.USER_ACCESS_TOKEN, User.getInstance().getAccesstoken())
                .setJsonObjectBody(requestBody)
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if(e!=null){
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_Netork_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int status = result.getHeaders().code();
                        JsonObject resultObject = result.getResult();
                        String value=String.valueOf(resultObject);
                        try {
                            JSONObject jsonObject=new JSONObject(value);
                            message = jsonObject.getString("message");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        switch (status){
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
                              //  loginActivity.onSuccess(resultObject,requestCode,status);
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
