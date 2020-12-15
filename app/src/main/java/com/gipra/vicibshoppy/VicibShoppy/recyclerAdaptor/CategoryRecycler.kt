package com.gipra.vicibshoppy.VicibShoppy.recyclerAdaptor

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.VicibShoppy.activity.ProductActivity
import com.gipra.vicibshoppy.VicibShoppy.utlis.MySingleton
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.shoppy_category_main_item.view.*
import kotlinx.android.synthetic.main.shoppy_category_main_item.view.itemName
import org.json.JSONArray
import org.json.JSONObject

class   CategoryRecycler(
    private val context: Context,
    private val  array: JSONArray,
    private val clickListiner: CategoryClickListiner
) :
    RecyclerView.Adapter<CategoryRecycler.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun setData(
            jsonObject: JSONObject,
            position: Int,
            picasso: Picasso,
            context: Context
        ) {

            var imageName = jsonObject.getString("pimage")
            val noImage = "https://babystyle.co.za/wp-content/uploads/2018/07/no-product-image.png"

            if (imageName == "No image") {

                picasso.load(noImage)
                    .into(itemView.itemImage)

                //  itemView.catImage.setImageResource(R.drawable.no_image)

            } else {
                picasso.load(MySingleton.imageBaseUrl + jsonObject.getString("pimage"))
                    .into(itemView.itemImage)
            }

            itemView.itemName.text = jsonObject.getString("modelname")
            itemView.actualPrice.text =  "₹ "+jsonObject.getString("pmrp")
            itemView.discountPrice.text =  "₹ "+jsonObject.getString("dp")
            itemView.discountPrice.setPaintFlags(itemView.discountPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        }

        fun click(
            context: Context?,
            position: Int,
            array: JSONArray
        ) {


            itemView.itemImage.setOnClickListener {

                var productID = array.getJSONObject(position).getString("mid")
                val intent = Intent(context, ProductActivity::class.java)
                intent.putExtra("p_id",productID)
//                val pairs: Array<Pair<View, String>?> = arrayOfNulls(1)
//                pairs[0] = Pair<View, String>(itemView.catImage, "productImage")
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    val options =
//                        ActivityOptions.makeSceneTransitionAnimation(activity, *pairs)
//                    activity?.startActivity(intent, options.toBundle())
//                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                context?.startActivity(intent)


            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shoppy_category_main_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return array.length()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val objects: JSONObject = array.getJSONObject(position)
        val picasso = Picasso.get()
        holder.setData(objects, position, picasso,context)
        holder.click(context, position,array)
    }

    interface CategoryClickListiner{

        fun onClick();
    }
}