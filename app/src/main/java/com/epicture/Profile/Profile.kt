package com.epicture.Profile

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.epicture.Utils.AuthentificationImgur
import com.example.epicture.R

/**
 * ProfileFragment is the fragment which is rendered in the main activity
 * when clicking on the profile button in the bottom bar
 * @param PersonnalApplication Our Application
 * @param PersonnalPackageManagerkey Our PackageManager
 * @param profileAPI class which contains all imgur information for the profile fragment
 * @param authentificationData class which contains the clientId and clientSecret
 */
class ProfileFragment(private var PersonnalApplication: Application,
                      private var PersonnalPackageManagerkey: PackageManager,
                      private var profileAPI: ProfileAPI,
                      private var authentification_data: AuthentificationImgur
) : Fragment() {

    private lateinit var cover : ImageView
    private lateinit var avatar : ImageView
    private lateinit var name : TextView
    private lateinit var reputation : TextView
    private lateinit var points : TextView
    private lateinit var creationDate : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var mRandom:Random
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(
        R.layout.fragment_profile, container, false)

    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val netDate = Date(s.toLong() * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    private fun randomInRange(min:Int, max:Int):Int{
        val r = Random()

        return r.nextInt((max - min) + 1) + min;
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRandom = Random()

        mHandler = Handler()

        swipeContainer = view.findViewById(R.id.simpleSwipeRefreshLayout)

        swipeContainer.setOnRefreshListener {
            mRunnable = Runnable {
                profileAPI.initProfileUser(PersonnalApplication, PersonnalPackageManagerkey, view.context, authentification_data) {
                    viewAdapter.notifyDataSetChanged()
                }

                swipeContainer.isRefreshing = false
            }

            mHandler.postDelayed(
                mRunnable, (randomInRange(1,3)*1000).toLong() // Delay 1 to 3 seconds
            )
        }
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)

        cover = view.findViewById(R.id.cover)!!
        avatar = view.findViewById(R.id.avatar)!!

        name = view.findViewById(R.id.profileName)!!
        reputation = view.findViewById(R.id.profileReputation)!!
        points = view.findViewById(R.id.profilePoints)!!
        creationDate = view.findViewById(R.id.profileCreation)!!

        Picasso.get().load(profileAPI.cover_url!!).into(cover)
        Picasso.get().load(profileAPI.avatar_url!!).into(avatar)
        avatar.clipToOutline = true

        name.text = profileAPI.account_username
        reputation.text = profileAPI.reputation_name
        points.text = profileAPI.reputation.toString()
        creationDate.text = "Joined on ${getDateTime(profileAPI.created.toString())}"

        viewManager = LinearLayoutManager(view.context)
        viewAdapter = MyAdapter(profileAPI.pictureList, authentification_data)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

    }

    companion object {
        fun newInstance(PersonnalApplication: Application,
                        PersonnalPackageManagerkey: PackageManager,
                        profileAPI: ProfileAPI,
                        authentification_data: AuthentificationImgur
        ): ProfileFragment = ProfileFragment(PersonnalApplication, PersonnalPackageManagerkey, profileAPI, authentification_data)
    }
}