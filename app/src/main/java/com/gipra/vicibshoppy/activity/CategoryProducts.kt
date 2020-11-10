package com.gipra.vicibshoppy.activity

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.application.ConnectivityReceiver
import com.gipra.vicibshoppy.recyclerAdaptor.CategoryRecycler
import com.gipra.vicibshoppy.utlis.MySingleton
import com.gipra.vicibshoppy.utlis.changeStatusBarColor
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.shoppy_activity_category_products.*
import org.json.JSONException
import org.json.JSONObject

class CategoryProducts : AppCompatActivity(), CategoryRecycler.CategoryClickListiner,
    ConnectivityReceiver.ConnectivityReceiverListener{
    private  val TAG = "CategoryProducts"

    private var catID: String? = null
    private var catName: String? = null
    private var snackBar: Snackbar? = null
    private var flag: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shoppy_activity_category_products)


        // setStatusBarGradient(this);
        val window = window

        changeStatusBarColor(window,112, 189, 231)

        val bundle: Bundle? = intent.extras
        catID = bundle!!.getString("cat_id")
        catName = bundle!!.getString("cat_name")

        categoryToolbar.title = catName
        categoryToolbar.setNavigationIcon(R.drawable.ic_arrow)
        categoryToolbar.setNavigationOnClickListener { onBackPressed() }

        shimmerCatActivity.startShimmerAnimation()
        Handler().postDelayed({
            getSampleProducts(catID)
        }, 2000)


    }

//    override fun onResume() {
//        super.onResume()
//        shimmerCatActivity.startShimmerAnimation()
//    }
//
//    override fun onPause() {
//        shimmerCatActivity.stopShimmerAnimation()
//        super.onPause()
//    }

    private fun showList() {
        shimmerCatActivity.stopShimmerAnimation()
        shimmerCatActivity.visibility = View.GONE
        //***************************************************************************************************************
        //Category recyclerView
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        categoryItems.layoutManager = layoutManager
      //  val Adaptor = CategoryRecycler(this, CatImageModel.suppler.imagesCategory, this)
        categoryItems.visibility = View.VISIBLE
       // categoryItems.adapter = Adaptor
        //***************************************************************************************************************
    }

    override fun onClick() {

    }


    private fun getSampleProducts(catID: String?) {
        val stringRequest = object : StringRequest(
            Method.POST, MySingleton.webService+"Product_list",
            Response.Listener<String> { response ->
                try {
                    val objects = JSONObject(response)
                    Log.e(TAG,response)

                    if (objects.getString("status")=="1"){
                        val array = objects.getJSONArray("data")

                        shimmerCatActivity.stopShimmerAnimation()
                        shimmerCatActivity.visibility = View.GONE
                        val layoutManager = GridLayoutManager(applicationContext, 2)
                        layoutManager.orientation = GridLayoutManager.VERTICAL
                        categoryItems?.layoutManager = layoutManager
                        val Adaptor = CategoryRecycler(applicationContext,array,this)

                        categoryItems?.visibility =View.VISIBLE

                        categoryItems?.adapter = Adaptor

                    }else{


                    }

                } catch (e: JSONException) {
                    Log.e(TAG,e.toString())
                }
            },
            Response.ErrorListener {
                    error ->  Log.e(TAG, error.toString())
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                catID?.let { params.put("cid", it) }
                return params
            }
        }
        applicationContext?.let { VolleySingleton.getInstance(it).addToRequestQueue(stringRequest) }

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