package com.epicture.Home

import android.animation.ObjectAnimator
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.epicture.Utils.AuthentificationImgur
import com.example.epicture.R
import java.util.*

/**
 * HomeFragment is the fragment which is rendered in the main activity
 * when clicking on the home button in the bottom bar
 * @param PersonnalApplication Our Application
 * @param PersonnalPackageManagerkey Our PackageManager
 * @param authentificationData class which contains the clientId and clientSecret
 * @param homeAPI class which contains all imgur information for the home fragment
 */
class HomeFragment(private val PersonnalApplication: Application,
                   private val PersonnalPackageManagerkey: PackageManager,
                   private val authentificationData: AuthentificationImgur,
                   private val homeAPI: HomeAPI
) : Fragment() {

    private lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var radioGroup : RadioGroup
    private lateinit var backgroundFilter : ImageView
    private lateinit var filterPanel : ConstraintLayout
    private lateinit var buttonBackground : ImageView
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var mRandom: Random
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private var keySearch : String = "most_viral"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun randomInRange(min : Int, max : Int) : Int {
        val r = Random()

        return r.nextInt((max - min) + 1) + min;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRandom = Random()

        mHandler = Handler()

        swipeContainer = view.findViewById(R.id.simpleSwipeRefreshLayout)

        swipeContainer.setOnRefreshListener {
            mRunnable = Runnable {
                homeAPI.initHomeFeed(PersonnalApplication, PersonnalPackageManagerkey, view.context, authentificationData, keySearch) {
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

        viewManager = LinearLayoutManager(view.context)
        viewAdapter = HomeAdapter(homeAPI.pictureList, authentificationData)
        radioGroup = view.findViewById(R.id.radioGroup)
        backgroundFilter = view.findViewById(R.id.backgroundFilter)
        filterPanel = view.findViewById(R.id.filterPanel)
        buttonBackground = view.findViewById(R.id.buttonBackground)

        val r = resources
        val px = applyDimension(
            COMPLEX_UNIT_DIP,
            375f,
            r.displayMetrics
        )

        filterPanel.clipToOutline = true
        buttonBackground.clipToOutline = true

        view.findViewById<LinearLayout>(R.id.Apply).setOnClickListener {
            val animFadein = AnimationUtils.loadAnimation(it.context, R.anim.fade_in)

            it.startAnimation(animFadein)

            val selected = view.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)?.text

            if (selected != null && selected == "Highest Scoring")
                keySearch = "highest_scoring"
            else if (selected != null && selected == "Most Viral")
                keySearch = "most_viral"
            else if (selected != null && selected == "Newest First")
                keySearch = "newest"

            homeAPI.initHomeFeed(PersonnalApplication, PersonnalPackageManagerkey, it.context, authentificationData, keySearch) {
                viewAdapter.notifyDataSetChanged()
            }

            backgroundFilter.visibility = INVISIBLE

            ObjectAnimator.ofFloat(filterPanel, "translationY", px).apply {
                duration = 300
                start()
            }
        }

        view.findViewById<LinearLayout>(R.id.filterButton).apply {
            this.clipToOutline = true
            setOnClickListener {
                val animFadein = AnimationUtils.loadAnimation(it.context, R.anim.fade_in)

                it.startAnimation(animFadein)

                backgroundFilter.visibility = VISIBLE

                ObjectAnimator.ofFloat(filterPanel, "translationY", -px).apply {
                    duration = 300
                    start()
                }
            }
        }

        backgroundFilter.setOnClickListener {
            backgroundFilter.visibility = INVISIBLE
            ObjectAnimator.ofFloat(filterPanel, "translationY", px).apply {
                duration = 300
                start()
            }
        }

        filterPanel.setOnClickListener {}

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {

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
                        authentificationData: AuthentificationImgur,
                        homeAPI: HomeAPI
        ): HomeFragment {
            return HomeFragment(PersonnalApplication, PersonnalPackageManagerkey, authentificationData, homeAPI)
        }
    }
}