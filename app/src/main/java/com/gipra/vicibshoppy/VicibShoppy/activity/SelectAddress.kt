package com.gipra.vicibshoppy.VicibShoppy.activity

import VolleySingleton
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.VicibShoppy.application.ConnectivityReceiver
import com.gipra.vicibshoppy.VicibShoppy.recyclerAdaptor.AddressRecycler
import com.gipra.vicibshoppy.VicibShoppy.utlis.MySingleton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.shoppy_activity_select_address.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SelectAddress : AppCompatActivity() , AddressRecycler.AddressClickListiner ,
    ConnectivityReceiver.ConnectivityReceiverListener{
    private  val TAG = "SelectAddress"
    var Adaptor: AddressRecycler? = null
    private var login_id: String? = null
    private var snackBar: Snackbar? = null
    private var flag: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shoppy_activity_select_address)

        selectToolbar.setNavigationOnClickListener { onBackPressed() }

        getAddressList("4")
        get_SharedPreferences()

    }
    private fun get_SharedPreferences() {
        var user = applicationContext.getSharedPreferences("Login", MODE_PRIVATE)
        login_id = user.getString("user_id", "4")

    }

    private fun getAddressList(login_id: String) {
        listAddressShimmer.startShimmerAnimation()

        val stringRequest =
            object : StringRequest(
                Method.POST, MySingleton.webService + "Shipping_Address_Listing",
                Response.Listener<String> { response ->
                    Log.e(TAG,response)
                    try {
                        val objects = JSONObject(response)


                        if (objects.getString("status") == "1") {
                            val array = objects.getJSONArray("data")

                            setListView(array)


                        }

                    } catch (e: JSONException) {
                        Log.e(TAG, e.toString())
                    }
                },
                Response.ErrorListener { error ->
                    Log.e(TAG, error.toString())
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put("login_id", login_id)
                    return params
                }
            }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
    }

    private fun setListView(array: JSONArray) {
        listAddressShimmer.stopShimmerAnimation()
        listAddressShimmer.visibility =View.GONE

        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        addressList.layoutManager = layoutManager

        addressList.visibility =View.VISIBLE

        Adaptor = login_id?.let { AddressRecycler(applicationContext, array, this, it) }

        addressList.visibility = View.VISIBLE
        addNew.visibility = View.VISIBLE

        addressList.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                DividerItemDecoration.VERTICAL
            )
        )

        addressList.adapter = Adaptor
    }

    fun addNewAddress(view: View) {
        val intent = Intent(applicationContext, AddressActivity::class.java)
        intent.putExtra("address_id", "0")
        startActivity(intent)
    }

    override fun onRestart() {
        super.onRestart()
        getAddressList("4")
    }


    //*********************************************************************************************
    //Network Handler
    override fun onStart() {
        super.onStart()
        ConnectivityReceiver.connectivityReceiverListener = this
    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {

        if (!isConnected) {

            snackBar = Snackbar.make(
                findViewById(android.R.id.content),
                "You are offline",
                Snackbar.LENGTH_LONG
            )
            //Assume "rootLayout" as the root layout of every activity.
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()

            flag = 1


        } else {

            if (flag == 1) {

                snackBar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "You are online",
                    Snackbar.LENGTH_LONG
                )
                //Assume "rootLayout" as the root layout of every activity.
                snackBar?.setBackgroundTint(Color.rgb(33, 150, 243))
                snackBar?.duration = BaseTransientBottomBar.LENGTH_SHORT
                snackBar?.show()




            } else {
                Log.e(TAG, "is First Connected")
                snackBar?.dismiss()
            }
            // showToast("Online")
            //showSnack(true)
        }
    }
    //*********************************************************************************************
}