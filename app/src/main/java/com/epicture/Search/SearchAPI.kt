package com.epicture.Search

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.android.volley.Request
import com.epicture.Utils.AuthentificationImgur
import com.epicture.Home.PictureInfoFeed
import com.epicture.Home.PictureListFeed
import com.epicture.Utils.Utils
import com.google.gson.Gson

/**
 * SearchAPI is the class which contains all the imgur informations that we
 * need to display in the search fragment
 */
class SearchAPI {

    //property
    var pictureList: MutableList<PictureInfoFeed> = ArrayList()

    //member function

    /**
     * Callback function which parse and add to the property $pictureList the appropriate data.
     *
     * @param data -> the response of the request in json form within a string.
     */
    fun searchPictures(data: String?) {
        if (!Utils().isJSONValid(data))
            return
        val dataJson = Gson().fromJson<PictureListFeed>(data, PictureListFeed::class.java)

        if (this.pictureList.isNotEmpty())
            this.pictureList.clear()
        dataJson.table.forEach {
            if (it.imageInfo.isNotEmpty()) {
                if (it.imageInfo[0].type == "image/jpeg" || it.imageInfo[0].type == "image/png") {
                    this.pictureList.add(it)
                }
            }
        }
    }

    /**
     * Show all picture data of main feed in console.
     *
     */
    fun showPictureInfo() {
        this.pictureList.forEach {
            Log.d("Epicture", "####################")
            Log.d("Epicture", "     title : ${it.title}")
            Log.d("Epicture", "     description : ${it.description}")
            Log.d("Epicture", "     datetime : ${it.datetime}")
            Log.d("Epicture", "     views : ${it.views}")
            Log.d("Epicture", "     ups : ${it.ups}")
            Log.d("Epicture", "     down : ${it.down}")
            Log.d("Epicture", "####IMAGES###")
            Log.d("Epicture", "     link : ${it.imageInfo[0].link}")
            Log.d("Epicture", "     animated : ${it.imageInfo[0].animated}")
        }
    }

    /**
     * initUserSearch is the function which init the search feature for user with key gven in parameters..
     *
     * @param PersonnalApplication -> application from getApplication()
     * @param PersonnalPackageManagerkey -> PackageManager from getPackageManager()
     * @param actualContext -> context of the app.
     * @param authentificationData -> authentification data to access to some variable like access_token.
     * @param searchKey -> the key that you want to search.
     * @param sortKey -> sort the search by time | viral | top
     */
    @Suppress("UNUSED_PARAMETER")
    fun initUserSearch(PersonnalApplication: Application,
                       PersonnalPackageManagerkey: PackageManager,
                       actualContext: Context,
                       authentificationData: AuthentificationImgur,
                       sortKey: String?, searchKey: String?,
                       callback : () -> Unit) {
        val paramsHeader = HashMap<String, String>()
        var keyRequest: String? = ""

        paramsHeader["Authorization"] = "Client-ID ${Utils().getMetaDataByKey(PersonnalApplication, PersonnalPackageManagerkey,"clientId")}"
        paramsHeader["content-type"] = "application/json"

        when (sortKey) {
            "time" -> keyRequest = "/time/?q=${searchKey}"
            "viral" -> keyRequest = "/viral/?q=${searchKey}"
            "top" -> keyRequest = "/top/?q=${searchKey}"
        }
        Utils().getRequest(actualContext, paramsHeader, "https://api.imgur.com/3/gallery/search/" + keyRequest, {
            this.searchPictures(it)
            callback()
        }, Request.Method.GET, null, null, null)
    }


}