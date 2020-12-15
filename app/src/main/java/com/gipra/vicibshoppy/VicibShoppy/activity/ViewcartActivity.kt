package com.gipra.vicibshoppy.VicibShoppy.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.VicibShoppy.application.ConnectivityReceiver
import com.gipra.vicibshoppy.VicibShoppy.recyclerAdaptor.CartRecycler
import com.gipra.vicibshoppy.VicibShoppy.server.APIController
import com.gipra.vicibshoppy.VicibShoppy.server.ServiceVolley
import com.gipra.vicibshoppy.VicibShoppy.utlis.changeStatusBarColor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.shoppy_activity_viewcart.*

import kotlinx.android.synthetic.main.shoppy_show_placeorder.*
import org.json.JSONArray
import org.json.JSONObject


class ViewcartActivity : AppCompatActivity(), CartRecycler.CartClickListiner, View.OnClickListener ,
    ConnectivityReceiver.ConnectivityReceiverListener{
    private val TAG = "ViewcartActivity"

    var jsonArray = JSONArray()
    var Adaptor: CartRecycler? = null
    private var snackBar: Snackbar? = null
    private var flag: Int = 0

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shoppy_activity_viewcart)

        cartToolBar.setTitleTextColor(R.color.colorBlack)
        cartToolBar.setNavigationIcon(R.drawable.ic_arrow_accent)
        cartToolBar.setNavigationOnClickListener { onBackPressed() }
        //************************************************************************************************
        val window = window
        changeStatusBarColor(window,255, 255, 255)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //************************************************************************************************


        //************************************************************************************************
        //api calls
        showCartList()
        //************************************************************************************************

        //total price view
        val standardBottomSheetBehavior = BottomSheetBehavior.from(placeOrderView)
        standardBottomSheetBehavior.setPeekHeight(300);
        standardBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //************************************************************************************************


        //************************************************************************************************
        //caet scrollling
        cartRecycler.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // Scrolling up
                    placeOrderView.visibility = View.GONE
                    //changeStatusColor(255, 255 , 255);

                } else {
                    // Scrolling down
                    placeOrderView.visibility = View.VISIBLE
                    //changeStatusColor(112, 189 , 231);
                }
            }
        })
        //************************************************************************************************

        fabCheckout.setOnClickListener(this)
        keepShopping.setOnClickListener(this)

    }

    //************************************************************************************************
    //Change Status color
    private fun changeStatusColor(red: Int, green: Int, blue: Int) {
        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.rgb(red, green, blue)
        }
    }
    //************************************************************************************************


    //********************************************************************************************************
    // netWork call fot cart list
    private fun showCartList() {
        shimmerCartActivity.startShimmerAnimation()

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
                    setTotals(total)

                } else {
                    shimmerCartActivity.stopShimmerAnimation()
                    shimmerCartActivity.visibility = View.GONE
                    placeOrderView.visibility = View.VISIBLE
                    emptyCart.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun setTotals(total: String) {

        totalPrice.text = "₹ " + total + ".00"
        // jsonResponse.getString("total_count").toString()
    }
    //***************************************************************************************************************

    //***************************************************************************************************************
    //Category recyclerView
    private fun generateCartList(array: JSONArray) {

        shimmerCartActivity.stopShimmerAnimation()
        shimmerCartActivity.visibility = View.GONE

        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        cartRecycler.layoutManager = layoutManager
        Adaptor = CartRecycler(applicationContext, array, this)

        cartRecycler.visibility = View.VISIBLE
        placeOrderView.visibility = View.VISIBLE


        cartRecycler.addItemDecoration(
            DividerItemDecoration(
                cartRecycler.getContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        cartRecycler.adapter = Adaptor
    }

    override fun onDelete(jsonObject: JSONObject) {

        setTotals(jsonObject.getString("total_price"))
        if (jsonObject.getString("total_price").equals("0")) {

            cartRecycler.visibility = View.GONE
            placeOrderView.visibility = View.GONE
            emptyCart.visibility = View.VISIBLE

        }

    }

    //************************************************************************************************
    //on delt from cart
    override fun onChangeCount(totalSum: String) {
        setTotals(totalSum)
    }
    //************************************************************************************************

    //************************************************************************************************
    //select address
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabCheckout -> {

                showAlert()


            }
            R.id.keepShopping -> {
                val intent = Intent(applicationContext, ShoppyHome::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(R.string.dialogTitle)
        //set message for alert dialog
        builder.setMessage(R.string.dialogMessage)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            val intent = Intent(applicationContext, SelectAddress::class.java)
            startActivity(intent)
        }
        //performing cancel action
        builder.setNeutralButton("Cancel"){dialogInterface , which ->

           //close alert

        }
        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->
            val intent = Intent(applicationContext, CheckoutActivity::class.java)
            startActivity(intent)
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
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