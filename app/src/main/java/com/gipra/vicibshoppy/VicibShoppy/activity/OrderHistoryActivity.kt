package com.gipra.vicibshoppy.VicibShoppy.activity

import VolleySingleton
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.VicibShoppy.bottomSheets.OrderDetailSheet
import com.gipra.vicibshoppy.VicibShoppy.recyclerAdaptor.OrderRecyclerAdaptor
import com.gipra.vicibshoppy.VicibShoppy.utlis.MySingleton
import com.gipra.vicibshoppy.VicibShoppy.utlis.changeStatusBarColor
import kotlinx.android.synthetic.main.shoppy_activity_order_history.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class OrderHistoryActivity : AppCompatActivity() ,OrderRecyclerAdaptor.OrderItemClickListiner{

    private val TAG = "OrderHistoryActivity"
    private var login_id: String? = null
    var Adaptor: OrderRecyclerAdaptor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shoppy_activity_order_history)

        changeStatusBarColor(window, 112, 189, 231)

        val toolbar: Toolbar = findViewById(R.id.historyToolBar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow)

        toolbar.setNavigationOnClickListener { onBackPressed() }


        get_SharedPreferences()
        getOrderHistory()

    }


    private fun get_SharedPreferences() {
        var user = applicationContext.getSharedPreferences("Login", MODE_PRIVATE)
        login_id = user.getString("user_id", "4")

    }
    

    private fun getOrderHistory() {
        shimmerOrderHistory.startShimmerAnimation()
        val stringRequest =
            object : StringRequest(
                Method.POST, MySingleton.webService + "View_order_history",
                Response.Listener<String> { response ->
                    try {
                        val objects = JSONObject(response)
                        Log.e(TAG, "View_order_history \n $response")

                        if (objects.getString("status") == "1") {
                            val array = objects.getJSONArray("data")

                            generateRecyclerView(array)

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
                    login_id?.let { params.put("login_id", it) }
                    return params
                }
            }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
    }

    private fun generateRecyclerView(array: JSONArray) {
        shimmerOrderHistory.stopShimmerAnimation()
        shimmerOrderHistory.visibility = View.GONE
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        orderRecycler.layoutManager = layoutManager

        orderRecycler.visibility = View.VISIBLE
        Adaptor = OrderRecyclerAdaptor(applicationContext, array, this)

        orderRecycler.visibility = View.VISIBLE


//        orderRecycler.addItemDecoration(
//            DividerItemDecoration(
//                orderRecycler.getContext(),
//                DividerItemDecoration.VERTICAL
//            )
//        )

        orderRecycler.adapter = Adaptor
    }

    override fun onMoreClick(orderID: String) {
        val moreOrderDetails = OrderDetailSheet(applicationContext, orderID,login_id)
        moreOrderDetails.show(getSupportFragmentManager(), moreOrderDetails.getTag())
    }
}