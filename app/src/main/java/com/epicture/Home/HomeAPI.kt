package com.epicture.Home

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.android.volley.Request
import com.epicture.Utils.AuthentificationImgur
import com.epicture.Utils.Utils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class ImageInfo (
    @SerializedName("animated") val animated: Boolean = false,
    @SerializedName("link") val link: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("id") val id: String?
)

data class PictureInfoFeed (
    @SerializedName("id") val id: String? = "",
    @SerializedName("title") val title: String? = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("datetime") val datetime: Int = 0,
    @SerializedName("views") val views: Int = 0,
    @SerializedName("favorite") val favorite: Boolean = false,
    @SerializedName("ups") val ups: Int = 0,
    @SerializedName("down") val down: Int = 0,
    @SerializedName("vote") val vote: String? = "",
    @SerializedName("images") val imageInfo: ArrayList<ImageInfo> = ArrayList()
)

data class PictureListFeed (
    @SerializedName("data") val table: MutableList<PictureInfoFeed>
)

/**
 * HomeAPI is the class which contains all the imgur informations that we
 * need to display in the home fragment
 */
class HomeAPI {

    //property
    var pictureList: MutableList<PictureInfoFeed> = ArrayList()


    //member function

    /**
     * Callback function which parse and add to the property $pictureList the appropriate data.
     *
     * @param data -> the response of the request in json form within a string.
     */
    fun feedPictures(data: String?) {
        if (!Utils().isJSONValid(data))
            return

        val dataJson = Gson().fromJson<PictureListFeed>(data, PictureListFeed::class.java)

        if (this.pictureList.isNotEmpty())
            this.pictureList.clear()
        dataJson.table.forEach {
            if (it.imageInfo.isNotEmpty())
                if (!it.imageInfo[0].animated)
                    this.pictureList.add(it)
        }
    }

    /**
     * Show all picture data of main feed in console.
     *
     */
    fun showPictureInfo() {
        this.pictureList.forEach {
            Log.d("Epicture", "######################")
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
     * initHomeFeed is the function which init the feed of pictures.
     *
     * @param PersonnalApplication -> application from getApplication()
     * @param PersonnalPackageManagerkey -> PackageManager from getPackageManager()
     * @param actualContext -> context of the app.
     * @param authentificationData -> authentification data to access to some variable like access_token.
     * @param keySearch -> the key that you want to search (most_viral/highest_scoring/newest).
     */
    @Suppress("UNUSED_PARAMETER")
    fun initHomeFeed(PersonnalApplication: Application,
                     PersonnalPackageManagerkey: PackageManager,
                     actualContext: Context,
                     authentificationData: AuthentificationImgur,
                     keySearch: String?,
                     callback: () -> Unit) {
        val paramsHeader = HashMap<String, String>()
        var keyRequest: String? = ""

        paramsHeader["Authorization"] = "Client-ID ${Utils().getMetaDataByKey(PersonnalApplication, PersonnalPackageManagerkey,"clientId")}"
        paramsHeader["content-type"] = "application/json"

        when (keySearch) {
            "most_viral" -> keyRequest = "/hot/viral/"
            "highest_scoring" -> keyRequest = "/top/viral/"
            "newest" -> keyRequest = "/user/time/"
        }
        Utils().getRequest(actualContext, paramsHeader,
            "https://api.imgur.com/3/gallery$keyRequest", {
            feedPictures(it)
            callback()
        }, Request.Method.GET, null, null, null)
    }

}