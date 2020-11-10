package com.gipra.vicibshoppy.pageAdaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.models.ImagesData
import com.gipra.vicibshoppy.models.suppler
import com.github.islamkhsh.CardSliderAdapter
import kotlinx.android.synthetic.main.slide_container.view.*

class BannerSlider(var listSuppler: suppler, val clickListiner: OnBannerClicked) :
    CardSliderAdapter<BannerSlider.MovieViewHolder>() {

    override fun getItemCount() = listSuppler.images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.slide_banner_container, parent, false)
        return MovieViewHolder(view)
    }

    override fun bindVH(holder: MovieViewHolder, position: Int) {
        holder.bindView(listSuppler.images[position])
        holder.click(clickListiner)
    }

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(catImageModel: ImagesData) {
            itemView.sideImage.setImageResource(catImageModel.image)


        }

        fun click(clickListiner:OnBannerClicked) {
            itemView.sideImage.setOnClickListener {
                clickListiner.onItemClicked()
            }
        }
    }

    interface OnBannerClicked {

        fun onItemClicked()
    }


}