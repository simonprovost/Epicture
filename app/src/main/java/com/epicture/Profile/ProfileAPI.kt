package com.epicture.Profile

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.android.volley.Request
import com.epicture.Utils.AuthentificationImgur
import com.epicture.Utils.Utils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class InfoProfileValueClass (

    @SerializedName("id") val id: Int = 0,
    @SerializedName("url") val url: String? = "",
    @SerializedName("bio") val bio: String? = "",
    @SerializedName("avatar") val avatar: String? = "",
    @SerializedName("avatar_name") val avatar_name: String? = "",
    @SerializedName("cover") val cover: String? = "",
    @SerializedName("cover_name") val cover_name: String? = "",
    @SerializedName("reputation") val reputation: Int = 0,
    @SerializedName("reputation_name") val reputation_name: String? = "",
    @SerializedName("created") val created: Int = 0,
    @SerializedName("pro_expiration") val pro_expiration: Boolean = false
)

data class InfoProfileDataClass (
    @SerializedName("data") val table: InfoProfileValueClass
)

data class PictureProfileValueClass (
    @SerializedName("id") val id: String? = "",
    @SerializedName("title") val title: String? = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("datetime") val datetime: Int = 0,
    @SerializedName("animated") val animated: Boolean = false,
    @SerializedName("views") val views: Int = 0,
    @SerializedName("favorite") val favorite: Boolean = false,
    @SerializedName("link") val link: String?
)

data class PicturesProfileDataClass (
    @SerializedName("data") val table: MutableList<PictureProfileValueClass>
)

/**
 * ProfileAPI is the class which contains all the imgur informations that we
 * need to display in the profile fragment
 */
class ProfileAPI {

    //property
    var id: Int = 0
    var account_username: String? = ""
    var bio: String? = ""
    var avatar_url: String? = ""
    var avatar_name: String? = ""
    var cover_url: String? = ""
    var cover_name: String? = ""
    var reputation: Int = 0
    var reputation_name: String? = ""
    var created: Int = 0
    var pictureList: MutableList<PictureProfileValueClass> = ArrayList()

    //member function

    /**
     * userInfo is the function which will parse and filled the attributes account_username and other of the class
     * She filled with the info data from the user logged in.
     * @param data -> data in Json from the following get request /account/{{accountUsername}}
     */
    fun userInfo (data: String?) {
        val dataJson = Gson().fromJson<InfoProfileDataClass>(data, InfoProfileDataClass::class.java)

        this.id = dataJson.table.id
        this.account_username = dataJson.table.url
        this.bio = dataJson.table.bio
        this.avatar_url = dataJson.table.avatar
        this.avatar_name = dataJson.table.avatar_name
        this.cover_url = dataJson.table.cover
        this.cover_name = dataJson.table.cover_name
        this.reputation = dataJson.table.reputation
        this.reputation_name = dataJson.table.reputation_name
        this.created = dataJson.table.created
    }

    /**
     * userPicutres is the function which will parse and filled the attribute $pictureList of the class
     * She filled with the pictures data of the user logged in.
     * @param data -> data in Json from the following get request /account/me/images
     */
    fun userPictures (data: String?) {
        if (!Utils().isJSONValid(data))
            return

        val dataJson = Gson().fromJson<PicturesProfileDataClass>(data, PicturesProfileDataClass::class.java)

        if (this.pictureList.isNotEmpty())
            this.pictureList.clear()
        dataJson.table.forEach {
            if (!it.animated)
                this.pictureList.add(it)
        }
    }

    /**
     * Show pictures of the user and all of their charac's like their links, their title...
     *
     */
    fun showPictureInfo() {
        this.pictureList.forEach {
            Log.d("Epicture", "####################")
            Log.d("Epicture", "animated : ${it.animated}")
            Log.d("Epicture", "datetime : ${it.datetime}")
            Log.d("Epicture", "descr : ${it.description}")
            Log.d("Epicture", "favorite : ${it.favorite}")
            Log.d("Epicture", "link : ${it.link}")
            Log.d("Epicture", "title : ${it.title}")
            Log.d("Epicture", "view : ${it.views}")
        }
    }

    /**
     * Show profile data (id, account username and so on...)
     *
     */
    fun showProfileData () {
        Log.d("Epicture", "id = ${this.id}")
        Log.d("Epicture", "url = ${this.account_username}")
        Log.d("Epicture", "bio = ${this.bio}")
        Log.d("Epicture", "avatar = ${this.avatar_url}")
        Log.d("Epicture", "avatar_name = ${this.avatar_name}")
        Log.d("Epicture", "cover = ${this.cover_url}")
        Log.d("Epicture", "cover_name = ${this.cover_name}")
        Log.d("Epicture", "reputation = ${this.reputation}")
        Log.d("Epicture", "reputation_name = ${this.reputation_name}")
        Log.d("Epicture", "created = ${this.created}")
    }

    /**
     * Init Profile User with his account information + his pictures uploaded.
     *
     * @param PersonnalApplication -> application via getApplication() from the MainActivity
     * @param PersonnalPackageManagerkey -> packageManager via getPackageManager() from the MainActivity
     * @param actualContext -> actual context
     * @param authentificationData -> Auth data (access token, expires in....)
     */
    fun initProfileUser(PersonnalApplication: Application,
                        PersonnalPackageManagerkey: PackageManager,
                        actualContext: Context,
                        authentificationData: AuthentificationImgur,
                        callback : () -> Unit) {
        val paramsInfoUser = HashMap<String, String>()
        paramsInfoUser["Authorization"] = "Client-ID ${Utils().getMetaDataByKey(PersonnalApplication, PersonnalPackageManagerkey,"clientId")}"
        paramsInfoUser["content-type"] = "application/json"

        val paramsPictures = HashMap<String, String>()
        paramsPictures["Authorization"] = "Bearer ${authentificationData.access_token}"
        paramsPictures["content-type"] = "application/json"

        Utils().getRequest(actualContext, paramsInfoUser, "https://api.imgur.com/3/account/" + authentificationData.account_username, this::userInfo, Request.Method.GET, null, null, null)
        Utils().getRequest(actualContext, paramsPictures, "https://api.imgur.com/3/account/me/images", {
            this.userPictures(it)
            callback()
        }, Request.Method.GET, null,null, null)

    }
}