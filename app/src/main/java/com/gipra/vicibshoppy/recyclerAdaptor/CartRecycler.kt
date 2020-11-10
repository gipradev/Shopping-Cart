package com.gipra.vicibshoppy.recyclerAdaptor


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
import com.gipra.vicibshoppy.activity.ProductActivity
import com.gipra.vicibshoppy.utlis.MySingleton
import com.gipra.vicibshoppy.utlis.showToast
import com.gipra.vicibshoppy.utlis.showToasty
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.shoppy_cart_item.view.*
import org.json.JSONArray
import org.json.JSONObject


class CartRecycler(
    private val context: Context?,
    private val array: JSONArray,
    private val clickListiner: CartClickListiner
) :
    RecyclerView.Adapter<CartRecycler.MyViewHolder>() {

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
            itemView.category.text =   dataObject.getString("category_name")
            itemView.pCount.text = "₹ " + dataObject.getString("product_count")
            //  itemView.discountPrice.setPaintFlags(itemView.discountPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        }
        //************************************************************************************************

        //************************************************************************************************
        //OnClick events
        fun click(

            context: Context?,
            position: Int,
            array: JSONArray,
            clickListiner: CartClickListiner
        ) {

            var count = array.getJSONObject(position).getString("product_count").toString().toInt()
            var price = array.getJSONObject(position).getString("price").toString().toFloat()
            var productID = array.getJSONObject(position).getString("product_id")



            itemView.productImage.setOnClickListener {


                val intent = Intent(context, ProductActivity::class.java)
                intent.putExtra("p_id", productID)
//                val pairs: Array<Pair<View, String>?> = arrayOfNulls(1)
//                pairs[0] = Pair<View, String>(itemView.productImage, "productImage")
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    val options =
//                        ActivityOptions.makeSceneTransitionAnimation(context as Activity?, *pairs)
//                    context?.startActivity(intent, options.toBundle())
//                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)


            }

            itemView.plusButton.setOnClickListener {

                count += 1
                changeCartCount(
                    context,
                    count,
                    productID,
                    price,
                    itemView.plusButton,
                    clickListiner
                )
            }
            itemView.minusButton.setOnClickListener {

                if (count != 1) {
                    count -= 1

                    changeCartCount(
                        context,
                        count,
                        productID,
                        price,
                        itemView.minusButton,
                        clickListiner
                    )
                }

            }

        }

        //************************************************************************************************



        //************************************************************************************************
        //Change cart count api
        private fun changeCartCount(
            context: Context?,
            count: Int,
            productID: String,
            price: Float,
            button: View,
            clickListiner: CartClickListiner
        ) {

            button.isClickable = false
            button.isEnabled = false

            Log.e("API ", "$count and $productID")

            val stringRequest =
                object : StringRequest(Method.POST, MySingleton.webService + "Cart_List_Change",
                    Response.Listener { response ->
                        Log.e("Response ", "$response")

                        var jsonObject = JSONObject(response)


                        if (jsonObject.getString("status") == "1") {

                            button.isClickable = true
                            button.isEnabled = true


                            itemView.pCount.text = "₹ " + count.toString()
                            var priceChange = count * price
                            itemView.actualPrice.text = "₹ " + priceChange.toString() +"0"

                            var totalSum = jsonObject.getString("total_price")

                            clickListiner.onChangeCount(totalSum)


                        } else {

                            context?.showToast("Failed")
                        }
                    }, Response.ErrorListener { error ->
                        context?.showToast("Error : $error")
                    }) {
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params.put("product_id", productID)
                        params.put("login_id", "4")
                        params.put("product_count", count.toString())
                        return params
                    }
                }
            context?.let { VolleySingleton.getInstance(it).addToRequestQueue(stringRequest) }
        }
        //************************************************************************************************

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shoppy_cart_item, parent, false)

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
        clickListiner: CartClickListiner,
        productID: String, login_id: String, position: Int
    ) {

        val stringRequest =
            object : StringRequest(Method.POST, MySingleton.webService + "Add_Remove_Cart",
                Response.Listener { response ->
                    var jsonObject = JSONObject(response)

                    Log.e("Response", response)

                    if (jsonObject.getString("status") == "1") {
                        context?.showToasty(jsonObject.getString("message"))
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
    interface CartClickListiner {

        fun onDelete(jsonObject: JSONObject)
        fun onChangeCount(totalSum: String)
    }
    //************************************************************************************************

}


