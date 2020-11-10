package com.gipra.vicibshoppy.pageAdaptor

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.models.CatImageModel
import kotlinx.android.synthetic.main.slide_container.view.*

class SliderAdaptor(var  listSuppler: CatImageModel.suppler) : RecyclerView.Adapter<SliderAdaptor.SlideViewHolder>() {
    inner class SlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(catImageModel: CatImageModel) {
            itemView.sideImage.setImageResource(catImageModel.imagesCategory)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        return SlideViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.slide_banner_container,parent,false))
    }

    override fun getItemCount(): Int {
        Log.e("SliderAdaptor","${listSuppler.imagesCategory.size}")
        return listSuppler.imagesCategory.size
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {

        holder.bind(listSuppler.imagesCategory[position])
    }
}