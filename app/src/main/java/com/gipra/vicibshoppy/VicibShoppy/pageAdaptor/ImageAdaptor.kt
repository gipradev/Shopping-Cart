package com.gipra.vicibshoppy.VicibShoppy.pageAdaptor

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.VicibShoppy.utlis.MySingleton
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.slide_container.view.*
import org.json.JSONArray

class ImageAdaptor(val array : JSONArray) : RecyclerView.Adapter<ImageAdaptor.SlideViewHolder>() {
    inner class SlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(
            array: JSONArray,
            position: Int,
            picasso: Picasso
        ) {

            picasso.load(MySingleton.imageBaseUrl + array[position])
                .resize(300, 300)
                .into(itemView.sideImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        return SlideViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.slide_container,parent,false))
    }

    override fun getItemCount(): Int {
        Log.e("SliderAdaptor","${array.length()}")
        return array.length()
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {

        val picasso = Picasso.get()
        holder.bind(array,position,picasso)
    }
}