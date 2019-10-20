package com.epicture.Utils

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Utils is the class which contains several "utils" functions
 */
class Utils {

    //property

    //member function


    /**
     * Function for network call (get request).
     *
     * @param theContext -> context of app.
     * @param headerParams -> header param that you want to add in the request (hashmap type).
     * @param urlRequested -> url of the request.
     * @param paramUrl -> param that you want to add in the url.
     * @param callback -> callback function.
     */
    fun getRequest(theContext: Context, headerParams: HashMap<String, String>, urlRequested: String?, callback: (data: String?) -> Unit, method: Int, bodyParams: String?, messageToastSuccess: String?, messageToastFaillure: String?) {
        val bodyContent = bodyParams ?: ""

        if (method == Request.Method.GET) {
            urlRequested!!.httpGet()
                .header(headerParams)
                .body(bodyContent)
                .responseString { _, _, result ->
                    when (result) {
                        is Result.Failure -> {
                            val ex = result.getException()
                            Log.d("Epicture", ex.toString())
                            Log.d("Epicture", "FAILED")
                            //  Toast.makeText(this, "Failed to post image!", Toast.LENGTH_SHORT).show()
                        }
                        is Result.Success -> {
                            val data = result.get()
                            Log.d("Epicture", data)
                            Log.d("Epicture", "SUCCESS")
                            callback(data)

                            // Toast.makeText(this@Upload, "Post Send!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        } else {
            urlRequested!!.httpPost()
                .header(headerParams)
                .body(bodyContent)
                .responseString { _, _, result ->
                    when (result) {
                        is Result.Failure -> {
                            val ex = result.getException()
                            Log.d("Epicture", ex.toString())
                            Log.d("Epicture", "FAILED")
                            if (messageToastFaillure != null && messageToastFaillure != "") {
                                Toast.makeText(theContext, messageToastFaillure, Toast.LENGTH_SHORT).show()
                            }
                        }
                        is Result.Success -> {
                            val data = result.get()
                            Log.d("Epicture", data)
                            Log.d("Epicture", "SUCCESS")
                            callback(data)
                            if (messageToastSuccess != null && messageToastSuccess != "") {
                                Toast.makeText(theContext, messageToastSuccess, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
        }
    }


    /**
     * getMetaDataByKey is th efunction which get the meta data by key from manifest.xml.
     *
     * @param PersonnalApplication
     * @param PersonnalPackageManagerkey
     * @param key --> key that you wanted.
     * @return
     */
    fun getMetaDataByKey(PersonnalApplication: Application, PersonnalPackageManagerkey: PackageManager, key: String?): String? {
        val ai = PersonnalPackageManagerkey.getApplicationInfo(
            PersonnalApplication.packageName,
            PackageManager.GET_META_DATA
        )
        val bundle = ai.metaData
        return bundle.getString(key)
    }

    /**
     * Check if a string given in argument are valid or not for Json Format.
     *
     * @param data -> data json string.
     * @return true if the string are valid, false otherwise.
     */
    fun isJSONValid(data: String?): Boolean {
        try {
            JSONObject(data)
        } catch (ex: JSONException) {
            try {
                JSONArray(data)
            } catch (e: JSONException) {
                return false
            }
        }
        return true
    }
}