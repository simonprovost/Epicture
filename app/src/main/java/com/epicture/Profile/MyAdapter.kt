package com.epicture.Profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.epicture.Utils.AuthentificationImgur
import com.epicture.Utils.Utils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso
import com.example.epicture.R
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size


/**
 * MyAdapter is the class needed to adapt the image list to the recycler view
 * @param myDataset the image list
 * @param authentification_data the class which contains the clientId and clientSecret
 */
class MyAdapter(private val myDataset: MutableList<PictureProfileValueClass>, private val authentification_data: AuthentificationImgur) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var imageView : ImageView = v.findViewById(R.id.imageView)
        var star : ImageView = v.findViewById(R.id.star)
        var viewNb : TextView = v.findViewById(R.id.ViewNb)
        var viewKonfetti : KonfettiView = v.findViewById(R.id.viewKonfetti)

    }

    data class DataFavorite (
        @SerializedName("data") val data: String? = "",
        @SerializedName("success") val success: Boolean? = true
    )


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_profile, parent, false)
        // set the view's size, margins, paddings and layout parameters
        // val res = MyViewHolder(view)

        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if (myDataset[position].favorite)
            holder.star.setImageResource(R.drawable.fav)

        holder.star.setOnClickListener {
            val animFadein =
                AnimationUtils.loadAnimation(it.context, R.anim.fade_in)

            it.startAnimation(animFadein)

            holder.viewKonfetti.build()
                .addColors(Integer.parseInt("#20232A".replaceFirst("#", ""), 16),
                    Integer.parseInt("#61DAFB".replaceFirst("#", ""), 16),
                    Integer.parseInt("#61DAFB".replaceFirst("#", ""), 16))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 6f)
                .setFadeOutEnabled(true)
                .setTimeToLive(200L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(Size(10))
                .setPosition(-50f, holder.viewKonfetti.width + 50f, -50f, -50f)
                .stream(200, 500L)

            Utils().getRequest(it.context, hashMapOf("Authorization" to "Bearer ${authentification_data.access_token}", "content-type" to "application/json"),
                "https://api.imgur.com/3/image/${myDataset[position].id}/favorite", {
                    val dataJson = Gson().fromJson<MyAdapter.DataFavorite>(it, MyAdapter.DataFavorite::class.java)

                    if (dataJson.success == true) {
                        if (dataJson.data == "favorited") {
                            holder.star.setImageResource(R.drawable.fav)
                        } else {
                            holder.star.setImageResource(R.drawable.non_fav)
                        }
                    }
                }, Request.Method.POST, null, null, null)
        }
        holder.imageView.setOnClickListener(object: DoubleClickListener() {

            override fun onDoubleClick(v: View) {

                val animFadein =
                    AnimationUtils.loadAnimation(v.context, R.anim.fade_in_favorites)

                v.startAnimation(animFadein)


                holder.viewKonfetti.build()
                    .addColors(Integer.parseInt("#20232A".replaceFirst("#", ""), 16),
                        Integer.parseInt("#61DAFB".replaceFirst("#", ""), 16),
                        Integer.parseInt("#61DAFB".replaceFirst("#", ""), 16))
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 6f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(200L)
                    .addShapes(Shape.RECT, Shape.CIRCLE)
                    .addSizes(Size(10))
                    .setPosition(-50f, holder.viewKonfetti.width + 50f, -50f, -50f)
                    .stream(200, 500L)

                Utils().getRequest(v.context, hashMapOf("Authorization" to "Bearer ${authentification_data.access_token}", "content-type" to "application/json"),
                    "https://api.imgur.com/3/image/${myDataset[position].id}/favorite", {
                        val dataJson = Gson().fromJson<DataFavorite>(it, DataFavorite::class.java)

                        if (dataJson.success == true) {
                            if (dataJson.data == "favorited") {
                                holder.star.setImageResource(R.drawable.fav)
                            } else {
                                holder.star.setImageResource(R.drawable.non_fav)
                            }
                        }
                    }, Request.Method.POST, null, null, null)
            }

            override fun onSingleClick(v: View) {
            }
        })

        holder.viewNb.text = myDataset[position].views.toString()
        holder.imageView.clipToOutline = true
        Picasso.get().load(myDataset[position].link).fit().centerCrop().into(holder.imageView)
    }


    abstract inner class DoubleClickListener : View.OnClickListener {

        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < 300) {
                onDoubleClick(v)
            } else {
                onSingleClick(v)
            }
            lastClickTime = clickTime
        }

        abstract fun onSingleClick(v: View)
        abstract fun onDoubleClick(v: View)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}