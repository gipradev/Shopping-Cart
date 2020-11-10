package com.gipra.vicibshoppy.recyclerAdaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.models.ImagesData
import kotlinx.android.synthetic.main.shoppy_banner_item.view.*

class BannerRecycler(val context: Context, private var images: List<ImagesData>,val clickListiner: OnBannerClicked) :
    RecyclerView.Adapter<BannerRecycler.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(imageData: ImagesData, position: Int) {
            itemView.bannerImage.setImageResource(imageData.image)
        }

        fun click(clickListiner: OnBannerClicked) {
            itemView.bannerImage.setOnClickListener {
                clickListiner.onItemClicked()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shoppy_banner_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
      val imageData = images[position]
        holder.setData(imageData,position)
        holder.click(clickListiner)
    }

    interface OnBannerClicked{
        fun onItemClicked()
    }
}

