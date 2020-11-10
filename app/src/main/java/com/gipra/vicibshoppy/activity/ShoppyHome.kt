package com.gipra.vicibshoppy.activity

import VolleySingleton
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.core.widget.NestedScrollView.OnScrollChangeListener
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.application.ConnectivityReceiver
import com.gipra.vicibshoppy.bottomSheets.PinCodeSheet
import com.gipra.vicibshoppy.fragment.PageFragment
import com.gipra.vicibshoppy.models.CatImageModel
import com.gipra.vicibshoppy.models.suppler
import com.gipra.vicibshoppy.pageAdaptor.BannerSlider
import com.gipra.vicibshoppy.pageAdaptor.CategoryPageAdaptor
import com.gipra.vicibshoppy.recyclerAdaptor.CategoryTestRecycler
import com.gipra.vicibshoppy.recyclerAdaptor.TopRateRecycler
import com.gipra.vicibshoppy.server.ServiceVolley
import com.gipra.vicibshoppy.utlis.MySingleton
import com.gipra.vicibshoppy.utlis.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.shoppy_appbar_home.*
import kotlinx.android.synthetic.main.shoppy_content_main.*
import kotlinx.android.synthetic.main.shoppy_show_cart.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.time.ExperimentalTime 
import org.json.JSONException as JSONException1


class ShoppyHome : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener,
    BannerSlider.OnBannerClicked, TopRateRecycler.TopRateClickListiner {
    private val TAG = "ShoppyHome"
    val proPic: String =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQm5RpcDNqOmEk6wNUhtoo293xmfT6WpPuCqQ&usqp=CAU"
    val picasso = Picasso.get()
    var cartStatus: String? = null
    private var user: SharedPreferences? = null
    private var pincode: String? = null
    private var snackBar: Snackbar? = null
    private var flag: Int = 0
    private var login_id : String = ""

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shoppy_activity_home)



        login_id = "4"
        init(login_id)


        //cart view
        val standardBottomSheetBehavior = BottomSheetBehavior.from(standardBottomSheet)
        standardBottomSheetBehavior.setPeekHeight(300);
        standardBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        //*********************************************************************


        homeScroll.setOnScrollChangeListener(object : OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                if (cartStatus == "1") {
                    if (scrollY > 0) {
                        standardBottomSheet.visibility = View.GONE
                    } else {
                        standardBottomSheet.visibility = View.VISIBLE
                    }
                }

            }

        })


        //autocomplete Clicks
        autoCompleteText.setOnItemClickListener { parent, view, position, id ->
            var pName = parent.getItemAtPosition(position).toString()

            val intent = Intent(applicationContext, SearchActivity::class.java)
            intent.putExtra("productName", pName)
            startActivity(intent)

            Log.e(TAG, " value : $pName ")
        }

//        autoCompleteText.setOnFocusChangeListener { v, hasFocus ->
//            //autoCompleteText.showDropDown()
//            val intent = Intent(applicationContext, SearchActivity::class.java)
//            startActivity(intent)
//        }

        autoCompleteText.setOnClickListener {
            //autoCompleteText.showDropDown()
            val intent = Intent(applicationContext, SearchActivity::class.java)
            startActivity(intent)
        }

        autoCompleteText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // If the event is a key-down event on the "enter" button
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    showToast(autoCompleteText.text.toString())
                    return true
                }
                return false
            }
        })

        //*********************************************************************


        standardBottomSheet.setOnClickListener {
            val intent = Intent(applicationContext, ViewcartActivity::class.java)
            startActivity(intent)
        }


    }


    //**********************************************************************************************************
    //init
    fun init(loginId: String) {

        fetchSharedPreferences()
        shimmerBanner.startShimmerAnimation()
        shimmerCat.startShimmerAnimation()
        shimmerRec.startShimmerAnimation()
        Handler().postDelayed({
            // showBanner()

            showViewPagerBanner()
            showList()

            loadProPic()
        }, 3000)

        // getProductNames()//get List of Products
        showCategory(loginId) //get Sample categories
        checking(login_id)
        showRecommendedProducts(loginId)
        //

        if (pincode.equals(null) || pincode.equals("0")) {
            showBottomSheet()
        }
    }

    private fun checking(loginId: String): String? {
       // delay(3000L)
        val service = ServiceVolley()
        val path = "View_Cart_List"
        val params = JSONObject()
        params.put("login_id", "4")
        service.post(path, params) { response ->
            Log.e(TAG, "Response  $response")

            if (response?.getString("status").equals("1"))


                showCartStatus(response)
            else
                standardBottomSheet.visibility = View.GONE
        }

        return "Success"
    }

    private fun showCartStatus(response: JSONObject?) {
        standardBottomSheet.visibility = View.VISIBLE
        totalSum.text = "₹ "+response?.getString("total_sum") +".00"

        if (response?.getString("total_count")?.toInt()!! > 1)
            count.text = response?.getString("total_count") +" Items"
        else
            count.text = response?.getString("total_count") +" Item"
    }


    private fun showViewPagerBanner() {
        shimmerBanner.stopShimmerAnimation()
        shimmerBanner.visibility = View.GONE
        bannerLayout.visibility = View.VISIBLE
        cardSlider.visibility = View.VISIBLE
        cardSlider.adapter = BannerSlider(suppler, this)

    }

    private fun loadProPic() {


        picasso.load(proPic)
            .resize(300, 300)
            .into(profilePic)


    }

    //***************************************************************************************************
    private fun fetchSharedPreferences() {
        user = applicationContext.getSharedPreferences("User", Context.MODE_PRIVATE)
        pincode = user?.getString("pinCode", "0")

    }
    //***************************************************************************************************


    //***************************************************************************************************
    //get List of Products
    private fun getProductNames() {
        val stringRequest =
            object : StringRequest(Method.POST, MySingleton.webService + "Product_names",
                Response.Listener<String> { response ->
                    try {
                        val objects = JSONObject(response)
                        //  Log.e(TAG,response)

                        if (objects.getString("status") == "1") {
                            val array = objects.getJSONArray("data")

                            setAdaptor(array)

                        }

                    } catch (e: JSONException1) {
                        Log.e(TAG, e.toString())
                    }
                },
                Response.ErrorListener { error ->
                    Log.e(TAG, error.toString())
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put("pincode", "0")
                    return params
                }
            }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
    }

    private fun setAdaptor(array: JSONArray) {

        val nameList = ArrayList<String>()

        for (i in 0 until array.length()) {
            nameList.add(array.getJSONObject(i).getString("modelname"))
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_selectable_list_item, nameList)
        autoCompleteText.setAdapter(adapter)


    }
    //***************************************************************************************************

    //***************************************************************************************************
    private fun showBottomSheet() {
        val bottomSheet = PinCodeSheet()
        bottomSheet.show(supportFragmentManager, "PinCodeSheet")
    }
    //***************************************************************************************************

    //***************************************************************************************************************
    //category tab layout dynamic

    private fun showCategory(loginId: String) {
        shimmerHomeCat.startShimmerAnimation()


        val stringRequest =
            object : StringRequest(Method.POST, MySingleton.webService + "Category_list",
                Response.Listener<String> { response ->
                    try {
                        val objects = JSONObject(response)
                        //  Log.e(TAG,response)

                        if (objects.getString("status") == "1") {

                            val array = objects.getJSONArray("data")
                            shimmerHomeCat.stopShimmerAnimation()
                            shimmerHomeCat.visibility = View.GONE

                            val categoryPageAdaptor = CategoryPageAdaptor(supportFragmentManager)
                            for (i in 0 until array.length()) {
                                var catName = array.getJSONObject(i).getString("category")
                                var catId = array.getJSONObject(i).getString("cid")
                                categoryPageAdaptor.addFragment(
                                    PageFragment.newInstance(catId,catName),
                                    "${catName}"
                                )
                            }

                            viewPager.visibility = View.VISIBLE

                            viewPager.adapter = categoryPageAdaptor
                            tabLayout.setupWithViewPager(viewPager, true)
                        }

                    } catch (e: JSONException1) {
                        Log.e(TAG, e.toString())
                    }
                },
                Response.ErrorListener { error -> showToast("$error") }) {

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    return params
                }
            }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)


    }
    //***************************************************************************************************************

    //***************************************************************************************************************
    //category tab layout dynamic

    private fun showRecommendedProducts(loginId: String) {



        val stringRequest =
            object : StringRequest(Method.POST, MySingleton.webService + "Recommanded_products",
                Response.Listener<String> { response ->
                    try {
                        val objects = JSONObject(response)
                          Log.e(TAG,"Recomm"+response)

                        if (objects.getString("status") == "1") {

                            shimmerCat.stopShimmerAnimation()
                            shimmerCat.visibility = View.GONE

                            val array = objects.getJSONArray("data")


                            val layoutManager = GridLayoutManager(applicationContext, 2)
                            layoutManager.orientation = GridLayoutManager.HORIZONTAL
                            topRateRecycler.layoutManager = layoutManager
                            val Adaptor = TopRateRecycler(this, array, this)
                            topRateRecycler.visibility = View.VISIBLE
                            topRateRecycler.adapter = Adaptor
                        }

                    } catch (e: JSONException1) {
                        Log.e(TAG, e.toString())
                    }
                },
                Response.ErrorListener { error -> showToast("$error") }) {

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put("login_id",loginId)
                    return params
                }
            }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)


    }
    //***************************************************************************************************************


    //***************************************************************************************************************
    //Banner click listener
//    override fun onItemClicked() {
//
//        Log.e(TAG,"clicked")
//        cartStatus = "1"
//
//        if (standardBottomSheet.visibility == View.VISIBLE)
//            standardBottomSheet.visibility = View.GONE
//        else
//            standardBottomSheet.visibility = View.VISIBLE
//    }


    //***************************************************************************************************************


    private fun showList() {
        shimmerRec.stopShimmerAnimation()
        shimmerRec.visibility = View.GONE
        //***************************************************************************************************************
        //Category recyclerView
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recomRecycler.layoutManager = layoutManager
        val Adaptor = CategoryTestRecycler(this, CatImageModel.suppler.imagesCategory)
        recomRecycler.visibility = View.VISIBLE
        recomRecycler.adapter = Adaptor
        //***************************************************************************************************************
    }

//    private fun showTopRateList() {
//        shimmerCat.stopShimmerAnimation()
//        shimmerCat.visibility = View.GONE
//        //***************************************************************************************************************
//        //Category recyclerView
//        val layoutManager = GridLayoutManager(applicationContext, 2)
//        layoutManager.orientation = GridLayoutManager.HORIZONTAL
//        topRateRecycler.layoutManager = layoutManager
//        val Adaptor = TopRateRecycler(this, CatImageModel.suppler.imagesCategory, this)
//        topRateRecycler.visibility = View.VISIBLE
//        topRateRecycler.adapter = Adaptor
//        //***************************************************************************************************************
//    }


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




                init(login_id)


            } else {
                Log.e(TAG, "is First Connected")
                snackBar?.dismiss()
            }
            // showToast("Online")
            //showSnack(true)
        }
    }


    //***************************************************************************************************************
    //TopRate click listener
    override fun onClick() {
        TODO("Not yet implemented")
    }

    override fun onItemClicked() {
        if (standardBottomSheet.visibility == View.VISIBLE)
            standardBottomSheet.visibility = View.GONE
        else
            standardBottomSheet.visibility = View.VISIBLE
    }

    //***************************************************************************************************************





    override fun onResume() {
        super.onResume()
        Log.e(TAG,"onResume")
        checking(loginId = login_id)
        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    fun clickOnProfile(view: View) {
        val intent = Intent(applicationContext, OrderHistoryActivity::class.java)
        startActivity(intent)
    }

}

