package com.epicture.Favorites

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.android.volley.Request
import com.epicture.Utils.AuthentificationImgur
import com.epicture.Utils.Utils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class FavoriteInfoFeed (
    @SerializedName("id") var id: String? = "",
    @SerializedName("title") var title: String? = "",
    @SerializedName("description") var description: String? = "",
    @SerializedName("type") var type: String? = "",
    @SerializedName("cover") var cover: String? = "",
    @SerializedName("datetime") var datetime: Int = 0,
    @SerializedName("views") var views: Int = 0,
    @SerializedName("favorite") var favorite: Boolean = false,
    @SerializedName("animated") var animated: Boolean = false,
    @SerializedName("link") var link: String = ""

)

data class FavoriteListFeed (
    @SerializedName("data") var table: MutableList<FavoriteInfoFeed>
)

/**
 * FavoritesAPI is the class which contains all the imgur informations that we
 * need to display in the favorite fragment
 */
class FavoritesAPI {

    //property
    var pictureList: MutableList<FavoriteInfoFeed> = ArrayList()

    //member function

    /**
     * Callback function which parse and add to the property $pictureList the appropriate data.
     *
     * @param data -> the response of the request in json form within a string.
     */
    fun favoritesPictures(data: String?) {
        if (!Utils().isJSONValid(data))
            return
        val dataJson = Gson().fromJson<FavoriteListFeed>(data, FavoriteListFeed::class.java)

        if (this.pictureList.isNotEmpty())
            this.pictureList.clear()
        dataJson.table.forEach {
            if (!it.animated) {
                var extension = ""

               // Log.d("Epicture", "type = ${it.type} cover = ${it.cover}")
                if (it.type == "image/png")
                    extension = "png"
                else if (it.type == "image/jpeg")
                    extension = "jpg"
                it.link = "https://i.imgur.com/${it.cover}.${extension}"
                if (it.type == "image/jpeg" || it.type == "image/png")
                    this.pictureList.add(it)
            }
        }
       // showPictureInfo()
    }

    /**
     * Show all picture data of main feed in console.
     *
     */
    fun showPictureInfo() {
        Log.d("Epicture", "Size of list : ${this.pictureList.size}")
        this.pictureList.forEach {
            Log.d("Epicture", "####################")
            Log.d("Epicture", "     title : ${it.title}")
            Log.d("Epicture", "     description : ${it.description}")
            Log.d("Epicture", "     datetime : ${it.datetime}")
            Log.d("Epicture", "     views : ${it.views}")
            Log.d("Epicture", "####IMAGES###")
            Log.d("Epicture", "     link : ${it.link}")
            Log.d("Epicture", "     animated : ${it.animated}")
        }
    }

    /**
     * initFavoritesPictures is the function which init all favorites picture of the user.
     *
     * @param PersonnalApplication -> application from getApplication()
     * @param PersonnalPackageManagerkey -> PackageManager from getPackageManager()
     * @param actualContext -> context of the app.
     * @param authentificationData -> authentification data to access to some variable like access_token.
     * @param favoriteSort -> the favoriteSort variable is "oldest" or "newest" it's a kind of sort..
     */
    @Suppress("UNUSED_PARAMETER")
    fun initFavoritesPictures(PersonnalApplication: Application,
                              PersonnalPackageManagerkey: PackageManager,
                              actualContext: Context,
                              authentificationData: AuthentificationImgur,
                              favoriteSort: String?,
                              callback: () -> Unit) {
        val paramsHeader = HashMap<String, String>()
        var keyRequest: String? = ""

        paramsHeader["Authorization"] = "Bearer ${authentificationData.access_token}"
        paramsHeader["content-type"] = "application/json"

        when (favoriteSort) {
            "newest" -> keyRequest = "/${authentificationData.account_username}/favorites/0/newest"
            "oldest" -> keyRequest = "/${authentificationData.account_username}/favorites/0/oldest"
        }
        Utils().getRequest(actualContext, paramsHeader, "https://api.imgur.com/3/account/" + keyRequest, {
            this.favoritesPictures(it)
            callback()
        }, Request.Method.GET, null, null, null)
    }
}