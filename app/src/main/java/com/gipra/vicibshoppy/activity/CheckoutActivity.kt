package com.gipra.vicibshoppy.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.application.ConnectivityReceiver
import com.gipra.vicibshoppy.recyclerAdaptor.CartItemRecycler

import com.gipra.vicibshoppy.server.APIController
import com.gipra.vicibshoppy.server.ServiceVolley
import com.gipra.vicibshoppy.utlis.MySingleton
import com.gipra.vicibshoppy.utlis.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.shimmer_chechout_layout.*

import kotlinx.android.synthetic.main.shoppy_activity_checkout.*
import kotlinx.android.synthetic.main.shoppy_show_checkout.*

import org.json.JSONArray
import org.json.JSONObject

class CheckoutActivity : AppCompatActivity(), CartItemRecycler.CartItemClickListiner,
    View.OnClickListener,  ConnectivityReceiver.ConnectivityReceiverListener {

    private val TAG = "CheckoutActivity"
    private var address_id: String? = null
    private var login_id: String? = null
    private var order_id: String? = null
    var jsonArray = JSONArray()
    var Adaptor: CartItemRecycler? = null
    private var snackBar: Snackbar? = null
    private var flag: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shoppy_activity_checkout)


//        val window = window
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.statusBarColor = Color.rgb(112, 189, 231)
//        }

//        val bundle: Bundle? = intent.extras
//        address_id = bundle!!.getString("address_id")
//        Log.e(TAG, address_id)


        get_SharedPreferences()
        getCartItems(login_id)

        if (address_id != "0") {
            getSelectedAddress(address_id.toString())
            changeButton.text = "Change Delivery address"

        } else {
            changeButton.text = "Select Delivery address"
            addressLayout.visibility = View.GONE
        }

        toolBarCheckOut.setNavigationOnClickListener { onBackPressed() }

        //total price view
        val standardBottomSheetBehavior = BottomSheetBehavior.from(checkoutView)
        standardBottomSheetBehavior.setPeekHeight(300);
        standardBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //************************************************************************************************



        changeButton.setOnClickListener(this)
        changeCart.setOnClickListener(this)
        fabCheckout.setOnClickListener(this)
    }


    private fun get_SharedPreferences() {
        var user = applicationContext.getSharedPreferences("User", MODE_PRIVATE)
        login_id = user.getString("user_id", "4")
        address_id = user.getString("primaryAddress", "0")

        Log.e(TAG, address_id)

    }

    //************************************************************************************************
    //get selected Address
    private fun getSelectedAddress(addressId: String) {

        val stringRequest =
            object : StringRequest(Method.POST, MySingleton.webService + "Get_id_address",
                Response.Listener { response ->
                    var jsonObject = JSONObject(response)
                    Log.e(TAG, "Get_id_address : $response")
                    if (jsonObject.getString("status") == "1") {

                        addressLayout.visibility = View.VISIBLE
                        setValues(jsonObject)


                    } else {


                    }
                }, Response.ErrorListener { error ->
                    showToast("Error : $error")
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    login_id?.let { params.put("login_id", it) }
                    params.put("address_id", addressId)
                    return params
                }
            }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)

    }
    //************************************************************************************************

    //************************************************************************************************
    //set address Values
    private fun setValues(jsonObject: JSONObject) {
        name.text = jsonObject.getString("Name")
        address.text = jsonObject.getString("address")
        place.text = jsonObject.getString("place")
        other.text =
            jsonObject.getString("district") + "," + jsonObject.getString("state") + "\n" +
                    jsonObject.getString("pin")

    }

    //************************************************************************************************
    //getCartItems
    private fun getCartItems(loginId: String?) {
        shimmerCheckoutItem.startShimmerAnimation()

        val service = ServiceVolley()
        val apiController = APIController(service)
        val path = "View_Cart_List"
        val params = JSONObject()
        params.put("login_id", "4")
        apiController.post(path, params) { jsonResponse ->
            Log.e(TAG, "Response  $jsonResponse")
            if (jsonResponse != null) {

                if (jsonResponse.getString("status").equals("1")) {

                    jsonArray = jsonResponse.getJSONArray("data")
                    generateCartList(jsonArray)
                    var total = jsonResponse.getString("total_sum")
                    order_id = jsonResponse.getString("order_id")
                    setTotals(total)

                } else {
//                    shimmerCartActivity.stopShimmerAnimation()
//                    shimmerCartActivity.visibility = View.GONE
//                    checkoutView.visibility = View.VISIBLE
//                    emptyCart.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun setTotals(total: String) {


        totalLayout.visibility - View.VISIBLE
        totalPrice.text = "₹ " + total + ".00"
        // jsonResponse.getString("total_count").toString()
    }

    private fun generateCartList(jsonArray: JSONArray) {
        shimmerCheckoutItem.stopShimmerAnimation()
        shimmerCheckoutItem.visibility = View.GONE
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        cartItemRecycler.layoutManager = layoutManager
        var Adaptor = CartItemRecycler(applicationContext, jsonArray, this)

        cartLayout.visibility = View.VISIBLE
        cartItemRecycler.visibility = View.VISIBLE
        // checkoutView.visibility = View.VISIBLE

        cartItemRecycler.adapter = Adaptor
    }

    override fun onDelete(jsonObject: JSONObject) {
        TODO("Not yet implemented")
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.changeButton -> {
                val intent = Intent(applicationContext, SelectAddress::class.java)
                startActivity(intent)
            }
            R.id.changeCart -> {
                val intent = Intent(applicationContext, ViewcartActivity::class.java)
                startActivity(intent)
            }
            R.id.fabCheckout -> checkoutOrder(login_id,address_id,order_id)
        }

    }
    //************************************************************************************************

    private fun checkoutOrder(loginId: String?, addressId: String?, orderId: String?) {
        val stringRequest =
            object : StringRequest(Method.POST, MySingleton.webService + "Place_order",
                Response.Listener { response ->
                    Log.e(TAG, "Place_order: $response")
                    try{
                        var jsonObject = JSONObject(response)
                        if (jsonObject.getString("status") == "1") {

                            val intent = Intent(applicationContext, FinishActivity::class.java)
                            startActivity(intent)
                            finish()
                            //showToast(jsonObject.getString("message"))


                        } else {


                        }
                    }catch(e : Exception){
                        Log.e(TAG,"Exception : $e")
                    }

                }, Response.ErrorListener { error ->
                    showToast("Error : $error")
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    loginId?.let { params.put("login_id", it) }
                    addressId?.let { params.put("address_id", it) }
                    orderId?.let { params.put("order_id", it) }
                    return params
                }
            }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)

    }
    //************************************************************************************************

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