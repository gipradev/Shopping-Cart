package com.gipra.vicibshoppy.recyclerAdaptor



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.gipra.vicibshoppy.R

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.shoppy_cart_item.view.*
import org.json.JSONArray
import org.json.JSONObject


class OrderItemListAdaptor(
    private val context: Context?,
    private val array: JSONArray

) :
    RecyclerView.Adapter<OrderItemListAdaptor.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //************************************************************************************************
        //Set Recycler Values
        fun setData(
            dataObject: JSONObject,
            picasso: Picasso,
            position: Int
        ) {
            var imageName = dataObject.getString("image")
            val noImage = "https://babystyle.co.za/wp-content/uploads/2018/07/no-product-image.png"

            if (imageName == "No image") {

                picasso.load(noImage)
                    .into(itemView.productImage)


            } else {
                picasso.load(dataObject.getString("image"))
                    .into(itemView.productImage)
            }

            itemView.productName.text = dataObject.getString("c_product")
            itemView.actualPrice.text = "₹ " + dataObject.getString("n_amount")
            itemView.category.text =   dataObject.getString("category_name")

            //  itemView.discountPrice.setPaintFlags(itemView.discountPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        }
        //************************************************************************************************


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shoppy_order_list_item, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return array.length()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val jsonObject = array.getJSONObject(position)
        val picasso = Picasso.get()
        holder.setData(jsonObject, picasso, position)


    }

}


