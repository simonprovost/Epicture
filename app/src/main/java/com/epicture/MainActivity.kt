package com.epicture

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.epicture.Favorites.FavoritesFragment
import com.epicture.Home.HomeAPI
import com.epicture.Home.HomeFragment
import com.epicture.Profile.ProfileAPI
import com.epicture.Profile.ProfileFragment
import com.epicture.Search.SearchFragment
import com.epicture.Upload.UploadFragment
import com.epicture.Utils.Utils
import com.example.epicture.*
import com.epicture.Favorites.FavoritesAPI
import com.epicture.Search.SearchAPI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var AuthentificationImgur: WebView? = null
    private var authentification_data = com.epicture.Utils.AuthentificationImgur()
    private var main_feed = HomeAPI()
    private lateinit var actualContext: Context
    private var profile_user = ProfileAPI()
    private var user_Favorites = FavoritesAPI()
    private var search = SearchAPI()

    /**
     * openFragment is called when we want to display a specific fragment on the screen
     * when we click on a button in the bottom bar for example
     * 
     * @param fragment the fragment which we want to open
     */
    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_favorites -> {
                val favoritesFragment = FavoritesFragment.newInstance(
                    application,
                    packageManager,
                    authentification_data,
                    user_Favorites
                )
                openFragment(favoritesFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                val searchFragment = SearchFragment.newInstance(
                    application,
                    packageManager,
                    authentification_data,
                    search
                )
                openFragment(searchFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_home -> {
                val homeFragment = HomeFragment.newInstance(
                    application,
                    packageManager,
                    authentification_data,
                    main_feed
                )
                openFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_upload -> {
                val uploadFragment =
                    UploadFragment.newInstance(application, packageManager, authentification_data)
                openFragment(uploadFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                val profileFragment = ProfileFragment.newInstance(
                    application,
                    packageManager,
                    profile_user,
                    authentification_data
                )
                openFragment(profileFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    /**
     * function to override the back button action.
     */
    override fun onBackPressed() {}

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        actualContext = applicationContext


        this.AuthentificationImgur = findViewById(R.id.webview)
        this.AuthentificationImgur!!.settings.javaScriptEnabled = true
        this.AuthentificationImgur!!.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                view?.loadUrl(url)
                if (view!!.url.split("#", "&", "=").indexOf("access_token") != -1) {
                    authentification_data.ParseDataAuthentification(view.url)
                    authentification_data.ShowDataAuthentification()
                    profile_user.initProfileUser(application, packageManager, actualContext, authentification_data) {}
                    user_Favorites.initFavoritesPictures(application, packageManager, actualContext, authentification_data, "newest") {}

                    val homeFragment = HomeFragment.newInstance(
                        application,
                        packageManager,
                        authentification_data,
                        main_feed
                    )
                    openFragment(homeFragment)
                    main_feed.initHomeFeed(application, packageManager, actualContext, authentification_data, "most_viral") {
                        homeFragment.viewAdapter.notifyDataSetChanged()
                    }

                    val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
                    bottomNavigation.menu.findItem(R.id.navigation_home).isChecked = true

                    bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


                    bottomNavigation.visibility = View.VISIBLE

                }
                return true
            }
        }
        this.AuthentificationImgur!!.loadUrl(Utils().getMetaDataByKey(application, packageManager, "oauth2link"))

    }

}