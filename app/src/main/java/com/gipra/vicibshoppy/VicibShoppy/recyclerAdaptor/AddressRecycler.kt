package com.gipra.vicibshoppy.VicibShoppy.recyclerAdaptor


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.VicibShoppy.activity.AddressActivity
import com.gipra.vicibshoppy.VicibShoppy.activity.CheckoutActivity
import com.gipra.vicibshoppy.VicibShoppy.utlis.MySingleton
import com.gipra.vicibshoppy.VicibShoppy.utlis.showToast
import kotlinx.android.synthetic.main.shoppy_address_item.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class AddressRecycler(
    private val context: Context?,
    private val array: JSONArray,
    private val clickListiner: AddressClickListiner,
    private var login_id : String
) :
    RecyclerView.Adapter<AddressRecycler.MyViewHolder>() {
    
    var lastSelectedPosition = -1

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun setData(
            dataObject: JSONObject,
            position: Int
        ) {


            itemView.name.text = dataObject.getString("Name")
            itemView.address.text = dataObject.getString("address")
            itemView.place.text = dataObject.getString("place")
            itemView.other.text =
                dataObject.getString("district") + "," + dataObject.getString("state") + "\n" +
                        dataObject.getString("pin")
        }

        fun onClick(
            jsonArray: JSONArray,
            position: Int,
            context: Context?,
            login_id: String

        ) {

          var addressId = jsonArray.getJSONObject(position).getString("address_id")

            Log.e("ADDRESS" ,addressId)

            itemView.selectButton.setOnClickListener {

                setToServer(addressId,login_id,context)

            }
            itemView.editButton.setOnClickListener {

                val intent = Intent(context, AddressActivity::class.java)
                intent.putExtra("address_id", addressId)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)

            }


        }

        private fun setToServer(addressId: String, login_id: String, context: Context?) {

            val stringRequest =
                object : StringRequest(
                    Method.POST, MySingleton.webService + "Update_default_address",
                    Response.Listener<String> { response ->
                        Log.e("AddressRecycler",response)
                        try {
                            val jsonObject = JSONObject(response)


                            if (jsonObject.getString("status") == "1") {

                                context?.showToast(jsonObject.getString("message"))

                                val sp = context?.getSharedPreferences("User", Context.MODE_PRIVATE)!!.edit()
                                sp.putString("primaryAddress", addressId)
                                sp.commit()



                                val intent = Intent(context, CheckoutActivity::class.java)
//                                intent.putExtra("address_id", addressId)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context?.startActivity(intent)
                            }

                        } catch (e: JSONException) {
                            Log.e("AddressRecycler", e.toString())
                        }
                    },
                    Response.ErrorListener { error ->
                        Log.e("AddressRecycler", error.toString())
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params.put("login_id", login_id)
                        params.put("address_id", addressId)
                        return params
                    }
                }
            context?.let { VolleySingleton.getInstance(it).addToRequestQueue(stringRequest) }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shoppy_address_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return array.length()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val jsonObject = array.getJSONObject(position)

        holder.setData(jsonObject, position)
        holder.onClick(array, position,context,login_id)


        if ((lastSelectedPosition == -1 && position == 0)) {
            holder.itemView.radioButton.setChecked(true)
            holder.itemView.options.visibility = View.VISIBLE
            holder.itemView.editButton.visibility = View.VISIBLE
        } else
            if (lastSelectedPosition == position) {
                holder.itemView.radioButton.setChecked(true)
                holder.itemView.options.visibility = View.VISIBLE
                holder.itemView.editButton.visibility = View.VISIBLE
            } else {
                holder.itemView.radioButton.setChecked(false)
                holder.itemView.options.visibility = View.GONE
                holder.itemView.editButton.visibility = View.GONE
            }


        holder.itemView.setOnClickListener {
            lastSelectedPosition = position
            notifyDataSetChanged()
            holder.itemView.options.visibility = View.VISIBLE
            holder.itemView.editButton.visibility = View.VISIBLE
        }


        holder.itemView.radioButton.setOnClickListener {
            lastSelectedPosition = position
            notifyDataSetChanged()
            holder.itemView.options.visibility = View.VISIBLE
            holder.itemView.editButton.visibility = View.VISIBLE
        }

        holder.itemView.removeButton.setOnClickListener {
            var addressId = array.getJSONObject(position).getString("address_id")
            deleteFromList(addressId,login_id,position)

        }
    }

    private fun deleteFromList(addressId: String, loginId: String, position: Int) {

        val stringRequest =
            object : StringRequest(Method.POST, MySingleton.webService + "Delete_address",
                Response.Listener { response ->
                    var jsonObject = JSONObject(response)

                    Log.e("Response", response)

                    if (jsonObject.getString("status") == "1") {
                        context?.showToast(jsonObject.getString("message"))
                        array.remove(position)
                        notifyItemRemoved(position)



                    } else {

                    }
                }, Response.ErrorListener { error ->
                    context?.showToast("Error : $error")
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put("login_id", login_id)
                    params.put("address_id", addressId)
                    return params
                }
            }
        context?.let { VolleySingleton.getInstance(it).addToRequestQueue(stringRequest) }
    }

    interface AddressClickListiner {


    }
}






