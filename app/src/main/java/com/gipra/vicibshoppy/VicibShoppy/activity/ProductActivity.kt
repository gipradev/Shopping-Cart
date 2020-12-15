package com.gipra.vicibshoppy.VicibShoppy.activity

import VolleySingleton
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Cache
import com.android.volley.Network
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.VicibShoppy.application.ConnectivityReceiver
import com.gipra.vicibshoppy.VicibShoppy.pageAdaptor.ProductImageSlider
import com.gipra.vicibshoppy.VicibShoppy.utlis.MySingleton
import com.gipra.vicibshoppy.VicibShoppy.utlis.changeStatusBarColor
import com.gipra.vicibshoppy.VicibShoppy.utlis.showToast
import com.gipra.vicibshoppy.VicibShoppy.utlis.showToasty
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.shimmer_product_details.*
import kotlinx.android.synthetic.main.shoppy_activity_product.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.HashMap


class ProductActivity : AppCompatActivity(), View.OnClickListener ,
    ConnectivityReceiver.ConnectivityReceiverListener{

    private val TAG = "ProductActivity"
    private var productId: String? = null
    var actual_price: Float? = null
    var d_price: Float? = null
    private var snackBar: Snackbar? = null
    private var flag: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shoppy_activity_product)

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        val bundle: Bundle? = intent.extras
        productId = bundle!!.getString("p_id")

        val window = window
        changeStatusBarColor(window,112, 189, 231)
        //*************************************************************************************************
        //set full screen mode
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            val w: Window = window
//            w.setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//            )
//        }
        //*************************************************************************************************

        //click events
        backArrow.setOnClickListener { onBackPressed() }
        //*************************************************************************************************

        init(productId)

        wishButton.setOnClickListener { switchIconView.switchState() }

        addToCart.setOnClickListener(this)
        viewCart.setOnClickListener(this)
        plusButton.setOnClickListener(this)
        minusButton.setOnClickListener(this)



    }

    //*********************************************************************************************************

    //init
    private fun init(productId: String?) {
        getProductDetails(productId)
    }


    //*********************************************************************************************************
    //getpProductDetails
    private fun getProductDetails(productId: String?) {

        shimmerDetails.startShimmerAnimation()

        // Setup 1 MB disk-based cache for Volley
        val cache: Cache = DiskBasedCache(cacheDir, 1024 * 1024)

        // Use HttpURLConnection as the HTTP client
        val network: Network = BasicNetwork(HurlStack())


        val stringRequest =
            object : StringRequest(Method.POST, MySingleton.webService + "Product_details",
                Response.Listener { response ->
                    Log.e(TAG, response)
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getString("status") == "1") {

                        shimmerDetails.stopShimmerAnimation()
                        shimmerDetails.visibility = View.GONE
                        topLayout.visibility = View.VISIBLE
                        details.visibility = View.VISIBLE

//                        //animate
//                        val bAnime = AnimationUtils.loadAnimation(this, R.anim.bottom_anim)
//                        details.animation = bAnime
//                        //************************************************************

                        val dataObject = jsonObject.getJSONObject("data")

                        Log.e(TAG, dataObject.toString())


                        setViewValues(dataObject)
                        setImageView(jsonObject)


                    }
                },
                Response.ErrorListener { error ->
                    Log.e(TAG, "$error")
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    productId?.let { params.put("mid", it) }
                    params.put("login_id", "4")
                    return params
                }
            }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)

    }

    private fun setImageView(jsonObject: JSONObject) {

        if (!jsonObject.getString("product_image").equals("0")) {
            val imageArray = jsonObject.getJSONArray("product_image")
            showViewPagerBanner(imageArray)
        } else {

            noImage.visibility = View.VISIBLE
        }
    }

    private fun setViewValues(dataObject: JSONObject) {

        pName.text = dataObject.getString("modelname")
        pDes.text = dataObject.getString("productdesc")
        actualPrice.text = "₹ " + dataObject.getString("pmrp")
        dPrice.text = "₹ " + dataObject.getString("dp")
        dPrice.setPaintFlags(dPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        actual_price = dataObject.getString("pmrp").toFloat()
        d_price = dataObject.getString("dp").toFloat()

        if (dataObject.getString("cart_status").equals("0")) {

            pCount.text = "1"

        } else {

          //  pCount.text = dataObject.getString("product_count")
            itemCount.text = dataObject.getString("product_count")
            var count = pCount.text.toString().toInt()
            actualPrice.text = "₹ " + (count * this!!.actual_price!!).toString()
            dPrice.text = "₹ " + (count * this!!.d_price!!).toString()
        }


        if (!dataObject.getString("cart_status").equals("0")) {
            addToCart.visibility = View.GONE
            viewCart.visibility = View.VISIBLE
            changeLayout.visibility = View.GONE
            cartCount.visibility = View.VISIBLE

        } else {
            addToCart.visibility = View.VISIBLE
            viewCart.visibility = View.GONE
        }


    }
    //*********************************************************************************************************


    //*********************************************************************************************************
    //onClickImplementation
    override fun onClick(v: View?) {

        var count = pCount.text.toString().toInt()
        Log.e(TAG, "$count")


        when (v?.id) {
            R.id.addToCart -> addToCart(productId, count.toString(), addToCart)
            R.id.viewCart -> {
                val intent = Intent(applicationContext, ViewcartActivity::class.java)
                startActivity(intent)
            }


            R.id.minusButton -> {


                if (count != 1) {
                    count -= 1
                    pCount.text = count.toString()

                    actualPrice.text = "₹ " + (count * this!!.actual_price!!).toString()+"0"
                    dPrice.text = "₹ " + (count * this!!.d_price!!).toString()+"0"

                }

            }
            R.id.plusButton -> {

                var count = pCount.text.toString().toInt()



                count += 1
                pCount.text = count.toString()

                actualPrice.text = "₹ " + (count * this!!.actual_price!!).toString()+"0"
                dPrice.text = "₹ " + (count * this!!.d_price!!).toString()+"0"

            }
        }
    }
    //*********************************************************************************************************


    //*********************************************************************************************************
    //addToCartFunction
    fun addToCart(
        productId: String?,
        productCount: String,
        addToCart: ExtendedFloatingActionButton
    ) {
        addToCart.isEnabled = false
        val pd = ProgressDialog(this)
        pd.setMessage("loading")
        pd.show()

        val stringRequest =
            object : StringRequest(Method.POST, MySingleton.webService + "Add_Remove_Cart",
                Response.Listener { response ->
                    var jsonObject = JSONObject(response)

                    if (jsonObject.getString("status") == "1") {
                        pd.dismiss()
                        setState(productCount)


                        showToasty(jsonObject.getString("message"))


                    } else {
                        pd.dismiss()
                        addToCart.isEnabled = true
                        showToast("Failed")
                    }
                }
                , Response.ErrorListener { error ->
                    showToast("Error : $error")
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    productId?.let { params.put("product_id", it) }
                    params.put("action", "1")
                    params.put("login_id", "4")
                    params.put("p_count", productCount)
                    return params
                }
            }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)


    }

    private fun setState(productCount: String) {

        addToCart.isEnabled = false
        addToCart.visibility = View.GONE
        viewCart.visibility = View.VISIBLE
        changeLayout.visibility = View.GONE
        cartCount.visibility = View.VISIBLE
        itemCount.text = productCount
    }
    //*********************************************************************************************************

    private fun showViewPagerBanner(imageArray: JSONArray) {

        productSlider.visibility = View.VISIBLE
        productSlider.adapter = ProductImageSlider(imageArray)

    }

    override fun onRestart() {
        super.onRestart()
        Log.e(TAG,"Called")
        getProductDetails(productId)
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