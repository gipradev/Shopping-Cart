package com.gipra.vicibshoppy.recyclerAdaptor

import android.content.Intent
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.activity.ProductActivity
import com.gipra.vicibshoppy.activity.ShoppyHome
import com.gipra.vicibshoppy.utlis.MySingleton.Companion.imageBaseUrl
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.shoppy_toprate_item.view.*
import kotlinx.android.synthetic.main.shoppy_toprate_item.view.actualPrice
import org.json.JSONArray
import org.json.JSONObject

class TopRateRecycler(
    private val activity: ShoppyHome?,
    private val array: JSONArray,
    private val clickListiner: TopRateClickListiner
) :
    RecyclerView.Adapter<TopRateRecycler.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(
            jsonObject: JSONObject,
            position: Int,
            picasso: Picasso
        ) {

            itemView.itemName.setText(jsonObject.getString("modelname"))
            itemView.actualPrice.setText(jsonObject.getString("pmrp"))
            itemView.discountPrice.setText(jsonObject.getString("dp"))


            var imageName = jsonObject.getString("pimage")
            val noImage = "https://babystyle.co.za/wp-content/uploads/2018/07/no-product-image.png"

            if (imageName == "No image") {

                picasso.load(noImage)
                    .resize(300, 300)
                    .into(itemView.itemImage)


            } else {
                picasso.load(imageBaseUrl+jsonObject.getString("pimage"))

                    .into(itemView.itemImage)
            }
        }

        fun click(

            activity: ShoppyHome?,
            position: Int,
            listiner: TopRateClickListiner,
            array: JSONArray
        ) {

            itemView.itemImage.setOnClickListener {
                var id = array.getJSONObject(position).getString("mid")

                val intent = Intent(activity, ProductActivity::class.java)
                val pairs: Array<Pair<View, String>?> = arrayOfNulls(1)
                intent.putExtra("p_id", id)
//                pairs[0] = Pair<View, String>(itemView.itemImage, "productImage")
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    val options =
//                        ActivityOptions.makeSceneTransitionAnimation(activity, *pairs)
//                    activity?.startActivity(intent, options.toBundle())
//                }
                activity?.startActivity(intent)


            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(activity).inflate(R.layout.shoppy_toprate_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return array.length()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var jsonObject = array.getJSONObject(position)
        val picasso = Picasso.get()


        holder.setData(jsonObject, position,picasso)
        holder.click(activity, position, clickListiner,array)

    }

    interface TopRateClickListiner {

        fun onClick();
    }
}