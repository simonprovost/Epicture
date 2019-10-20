package com.epicture.Utils

import android.util.Log

/**
 * AuthentificationImgur is the class which is needed to authenticate the user
 * on the platform with the imgur API
 */
class AuthentificationImgur {

    // Property
    var access_token: String? = ""
        // getter
        get() = field

        // setter
        set(value) {
            field = value
        }
    var expires_in: String? = ""
        // getter
        get() = field

        // setter
        set(value) {
            field = value
        }
    var token_type: String? = ""
        // getter
        get() = field

        // setter
        set(value) {
            field = value
        }
    var refresh_token: String? = ""
        // getter
        get() = field

        // setter
        set(value) {
            field = value
        }
    var account_id: String? = ""
        // getter
        get() = field

        // setter
        set(value) {
            field = value
        }
    var account_username: String? = ""
        // getter
        get() = field

        // setter
        set(value) {
            field = value
        }

    // Member Function

    /**
     * ParseDataAuthentification is the fun which will parse the url from the webView connection from Imgur after logged successfully.
     *
     * @param url -> URL with the following information like access_token, account username and so son...
     */
    fun ParseDataAuthentification (url: String) {
        var pattern_string_login: List<String>?

        pattern_string_login = url.split("#", "&", "=")

        this.access_token = pattern_string_login.get(pattern_string_login.indexOf("access_token") + 1)
        this.expires_in = pattern_string_login.get(pattern_string_login.indexOf("expires_in" ) + 1)
        this.token_type = pattern_string_login.get(pattern_string_login.indexOf("token_type" ) + 1)
        this.refresh_token = pattern_string_login.get(pattern_string_login.indexOf("refresh_token" ) + 1)
        this.account_id = pattern_string_login.get(pattern_string_login.indexOf("account_id" ) + 1)
        this.account_username = pattern_string_login.get(pattern_string_login.indexOf("account_username" ) + 1)
    }

    /**
     * Show data of the Auth.
     *
     */
    fun ShowDataAuthentification () {
        Log.d("Epicture", "################ INFO IMGUR ################")
        Log.d("Epicture", "access_token : ${this.access_token}")
        Log.d("Epicture", "expires_in : ${this.expires_in}")
        Log.d("Epicture", "token_type : ${this.token_type}")
        Log.d("Epicture", "refresh_token : ${this.refresh_token}")
        Log.d("Epicture", "account_id : ${this.account_id}")
        Log.d("Epicture", "account_username : ${this.account_username}")
    }
}