package com.epicture.Search

import android.animation.ObjectAnimator
import android.app.Application
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.epicture.Utils.AuthentificationImgur
import com.example.epicture.R
import java.util.*

/**
 * SearchFragment is the fragment which is rendered in the main activity
 * when clicking on the search button in the bottom bar
 * @param PersonnalApplication Our Application
 * @param PersonnalPackageManagerkey Our PackageManager
 * @param authentificationData class which contains the clientId and clientSecret
 * @param UserSearch class which contains all imgur information for the profile fragment
 */
class SearchFragment(private val PersonnalApplication: Application,
                     private val PersonnalPackageManagerkey: PackageManager,
                     private val authentificationData: AuthentificationImgur,
                     private val UserSearch: SearchAPI
) : Fragment() {

    lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var recyclerView : RecyclerView
    private lateinit var list: LinearLayout
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var radioGroup : RadioGroup
    private lateinit var backgroundFilter : ImageView
    private lateinit var filterPanel : ConstraintLayout
    private lateinit var buttonBackground : ImageView
    private lateinit var hint : TextView
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var mRandom: Random
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private var keySearch : String = ""
    private var sortKey : String = "top"

    private fun randomInRange(min : Int, max : Int) : Int {
        val r = Random()

        return r.nextInt((max - min) + 1) + min;
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchBar = view.findViewById<EditText>(R.id.searchBar)

        mRandom = Random()

        mHandler = Handler()

        swipeContainer = view.findViewById(R.id.simpleSwipeRefreshLayout)

        swipeContainer.setOnRefreshListener {
            mRunnable = Runnable {
                UserSearch.initUserSearch(PersonnalApplication, PersonnalPackageManagerkey, view.context, authentificationData, sortKey, keySearch) {
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

        list = view.findViewById(R.id.list)
        viewManager = LinearLayoutManager(view.context)
        viewAdapter = SearchAdapter(UserSearch.pictureList, authentificationData)
        hint = view.findViewById(R.id.hint)
        radioGroup = view.findViewById(R.id.radioGroup)
        backgroundFilter = view.findViewById(R.id.backgroundFilter)
        filterPanel = view.findViewById(R.id.filterPanel)
        buttonBackground = view.findViewById(R.id.buttonBackground)

        val r = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
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
                sortKey = "top"
            else if (selected != null && selected == "Most Viral")
                sortKey = "viral"
            else if (selected != null && selected == "Newest First")
                sortKey = "time"

            UserSearch.initUserSearch(PersonnalApplication, PersonnalPackageManagerkey, it.context, authentificationData, sortKey, keySearch) {
                viewAdapter.notifyDataSetChanged()
            }

            backgroundFilter.visibility = View.INVISIBLE

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

                backgroundFilter.visibility = View.VISIBLE

                ObjectAnimator.ofFloat(filterPanel, "translationY", -px).apply {
                    duration = 300
                    start()
                }
            }
        }

        backgroundFilter.setOnClickListener {
            backgroundFilter.visibility = View.INVISIBLE
            ObjectAnimator.ofFloat(filterPanel, "translationY", px).apply {
                duration = 300
                start()
            }
        }

        filterPanel.setOnClickListener {}

        searchBar.addTextChangedListener( object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.toString() == "") {
                    UserSearch.pictureList.clear()
                    viewAdapter.notifyDataSetChanged()
                    hint.visibility = View.VISIBLE
                    list.visibility = View.INVISIBLE
                } else {
                    hint.visibility = View.INVISIBLE
                    list.visibility = View.VISIBLE
                    keySearch = s.toString()
                    UserSearch.initUserSearch(PersonnalApplication, PersonnalPackageManagerkey, view.context, authentificationData, sortKey, keySearch) {
                        viewAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }
        })

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
                        UserSearch : SearchAPI
        ): SearchFragment = SearchFragment(PersonnalApplication, PersonnalPackageManagerkey, authentificationData, UserSearch)
    }
}