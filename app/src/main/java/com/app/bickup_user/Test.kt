package com.app.bickup_user

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.app.bickup_user.controller.WebAPIManager.get_url_Ride
import com.app.bickup_user.model.User
import com.app.bickup_user.utility.ConstantValues
import com.google.gson.JsonObject
import com.koushikdutta.async.future.FutureCallback
import com.koushikdutta.ion.Ion
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by fluper-pc on 25/11/17.
 */

class Test : AppCompatActivity() {

    internal var message = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prepareBooking("21.323", "23.4342", "KK")
    }


    fun prepareBooking(lattitude: String, longitude: String, buildinName: String) {
        val requestBody = JsonObject()

        requestBody.addProperty(ConstantValues.LATITUDE, lattitude)
        requestBody.addProperty(ConstantValues.LONGITUDE, longitude)
        requestBody.addProperty(ConstantValues.BUILDING_NAME, buildinName)

        callAPI(requestBody)
        Log.d("TAGS", requestBody.toString())
    }


    private fun callAPI(requestBody: JsonObject) {
        Ion.with(this)
                .load("POST", get_url_Ride)
                .setHeader(ConstantValues.USER_ACCESS_TOKEN, User.getInstance().accesstoken)
                .setJsonObjectBody(requestBody)
                .asJsonObject()
                .withResponse()
                .setCallback(FutureCallback { e, result ->
                    if (e != null) {
                        Toast.makeText(applicationContext, resources.getString(R.string.txt_Netork_error), Toast.LENGTH_SHORT).show()
                        return@FutureCallback
                    }
                    val status = result.headers.code()
                    val resultObject = result.result
                    val value = resultObject.toString()
                    try {
                        val jsonObject = JSONObject(value)
                        message = jsonObject.getString("message")
                    } catch (e1: JSONException) {
                        e1.printStackTrace()
                    }

                    when (status) {
                        422 -> Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                        400 -> Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                        500 -> Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                        200, 202 -> {
                        }
                        else -> Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    }//  loginActivity.onSuccess(resultObject,requestCode,status);
                })
    }

}
