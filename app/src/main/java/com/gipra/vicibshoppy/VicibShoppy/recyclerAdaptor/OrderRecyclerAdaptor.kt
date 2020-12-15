package com.gipra.vicibshoppy.VicibShoppy.recyclerAdaptor


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gipra.vicibshoppy.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.shoppy_order_item.view.*

import org.json.JSONArray
import org.json.JSONObject


class OrderRecyclerAdaptor(
    private val context: Context?,
    private val array: JSONArray,
    private val clickListiner: OrderItemClickListiner
) :
    RecyclerView.Adapter<OrderRecyclerAdaptor.MyViewHolder>() {

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
                    .into(itemView.image)


            } else {
                picasso.load(dataObject.getString("image"))
                    .into(itemView.image)
            }

            itemView.orderDate.text = dataObject.getString("d_date")
            itemView.orderStatus.text = dataObject.getString("c_order_status")
            itemView.totalCount.text ="Total item : " +dataObject.getString("total_count")
            itemView.totalSum.text = "Total : ₹ " + dataObject.getString("sub_total")

            if (dataObject.getString("total_count").toInt()-1 ==0){
                itemView.orderHeader.text = dataObject.getString("c_product")
            }else if(dataObject.getString("total_count").toInt()-1 ==1){
                itemView.orderHeader.text = dataObject.getString("c_product") +
                        "\t and ${dataObject.getString("total_count").toInt() - 1} " +
                        " more Item"
            }
            else{
                itemView.orderHeader.text = dataObject.getString("c_product") +
                        "\t and ${dataObject.getString("total_count").toInt() - 1} " +
                        " more Items"
            }
        }


        //************************************************************************************************

        //************************************************************************************************
        //OnClick events
        fun click(

            context: Context?,
            position: Int,
            array: JSONArray,
            clickListiner: OrderItemClickListiner
        ) {

            var orderID = array.getJSONObject(position).getString("order_id")

            itemView.moreDetails.setOnClickListener {
                clickListiner.onMoreClick(orderID)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shoppy_order_item, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return array.length()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val jsonObject = array.getJSONObject(position)
        val picasso = Picasso.get()
        holder.setData(jsonObject, picasso, position)


        holder.click(context, position, array, clickListiner)



    }


    //************************************************************************************************



    //************************************************************************************************
    //interFace
    interface OrderItemClickListiner {

        fun onMoreClick(orderID :String)

    }
    //************************************************************************************************

}


