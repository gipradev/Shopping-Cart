package com.gipra.vicibshoppy.VicibShoppy.recyclerAdaptor


import VolleySingleton
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.VicibShoppy.activity.ProductActivity
import com.gipra.vicibshoppy.VicibShoppy.utlis.MySingleton
import com.gipra.vicibshoppy.VicibShoppy.utlis.showToast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.shoppy_cart_checkout_item.view.*

import org.json.JSONArray
import org.json.JSONObject


class CartItemRecycler(
    private val context: Context?,
    private val array: JSONArray,
    private val clickListiner: CartItemClickListiner
) :
    RecyclerView.Adapter<CartItemRecycler.MyViewHolder>() {

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

            itemView.productName.text = dataObject.getString("item_name")
            itemView.actualPrice.text = "₹ " + dataObject.getString("price")
            itemView.itemCount.text = dataObject.getString("product_count")
            //  itemView.discountPrice.setPaintFlags(itemView.discountPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        }
        //************************************************************************************************

        //************************************************************************************************
        //OnClick events
        fun click(

            context: Context?,
            position: Int,
            array: JSONArray,
            clickListiner: CartItemClickListiner
        ) {

            var count = array.getJSONObject(position).getString("product_count").toString().toInt()
            var price = array.getJSONObject(position).getString("price").toString().toFloat()
            var productID = array.getJSONObject(position).getString("product_id")



            itemView.productImage.setOnClickListener {


                val intent = Intent(context, ProductActivity::class.java)
                intent.putExtra("p_id", productID)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)


            }



        //************************************************************************************************



        //************************************************************************************************
        //Change cart count api

        //************************************************************************************************

    }}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shoppy_cart_checkout_item, parent, false)

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

        holder.itemView.deleteButton.setOnClickListener {

            var productID = array.getJSONObject(position).getString("product_id")

            deleteFromCart(clickListiner, productID, "4", position)

        }

    }

    //************************************************************************************************
    //deleteFrom Cart function
    private fun deleteFromCart(
        clickListiner: CartItemClickListiner,
        productID: String, login_id: String, position: Int
    ) {

        val stringRequest =
            object : StringRequest(Method.POST, MySingleton.webService + "Add_Remove_Cart",
                Response.Listener { response ->
                    var jsonObject = JSONObject(response)

                    Log.e("Response", response)

                    if (jsonObject.getString("status") == "1") {
                        context?.showToast(jsonObject.getString("message"))
                        array.remove(position)
                        notifyItemRemoved(position)

                        clickListiner.onDelete(jsonObject)

                        // clickListiner.onDelete(productID, "4", position)

                    } else {

                    }
                }, Response.ErrorListener { error ->
                    context?.showToast("Error : $error")
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put("product_id", productID)
                    params.put("action", "0")
                    params.put("login_id", "4")
                    params.put("p_count", "0")
                    return params
                }
            }
        context?.let { VolleySingleton.getInstance(it).addToRequestQueue(stringRequest) }
    }

    //************************************************************************************************



    //************************************************************************************************
    //interFace
    interface CartItemClickListiner {

        fun onDelete(jsonObject: JSONObject)

    }
    //************************************************************************************************

}


