package com.gipra.vicibshoppy.VicibShoppy.recyclerAdaptor

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.VicibShoppy.activity.ProductActivity
import com.gipra.vicibshoppy.VicibShoppy.models.CatImageModel

import kotlinx.android.synthetic.main.shoppy_category_main_item.view.*

class CategoryTestRecycler(private val activity: Activity?, private var imagesCategory: List<CatImageModel>) :
    RecyclerView.Adapter<CategoryTestRecycler.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(imageData: CatImageModel, position: Int) {
            itemView.itemImage.setImageResource(imageData.imagesCategory) }

        fun click(

            activity: Activity?,
            position: Int

        ) {

            itemView.itemImage.setOnClickListener {


                val intent = Intent(activity, ProductActivity::class.java)
                val pairs: Array<Pair<View, String>?> = arrayOfNulls(1)
                pairs[0] = Pair<View, String>(itemView.itemImage, "productImage")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val options =
                        ActivityOptions.makeSceneTransitionAnimation(activity, *pairs)
                    activity?.startActivity(intent, options.toBundle())
                }


            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.shoppy_category_main_item, parent, false)
        Log.e("CategoryRecycler","${imagesCategory.size}")
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  imagesCategory.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val imageData = imagesCategory[position]

        holder.setData(imageData,position)
        holder.click(activity,position)

    }

    interface CategoryClickListiner{

        fun onClick();
    }
}