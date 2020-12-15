package com.gipra.vicibshoppy.VicibShoppy.pageAdaptor

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.VicibShoppy.utlis.MySingleton
import com.github.islamkhsh.CardSliderAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.slide_container.view.*
import org.json.JSONArray

class ProductImageSlider(var imageArray: JSONArray) :
    CardSliderAdapter<ProductImageSlider.MovieViewHolder>() {

    private  val TAG = "ProductImageSlider"

    override fun getItemCount() = imageArray.length()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.slide_image_container, parent, false)
        return MovieViewHolder(view)
    }

    override fun bindVH(holder: MovieViewHolder, position: Int) {
        val picasso = Picasso.get()
        holder.bindView(imageArray,position,picasso)

        Log.e(TAG,imageArray.toString())

    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(
            array: JSONArray,
            position: Int,
            picasso: Picasso
        ) {

            picasso.load(MySingleton.imageBaseUrl + array[position])
                .into(itemView.sideImage)

        }

    }



}