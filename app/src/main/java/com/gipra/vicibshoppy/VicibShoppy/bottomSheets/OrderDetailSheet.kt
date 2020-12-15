package com.gipra.vicibshoppy.VicibShoppy.bottomSheets

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.VicibShoppy.recyclerAdaptor.OrderItemListAdaptor
import com.gipra.vicibshoppy.VicibShoppy.utlis.MySingleton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.shoppy_order_details_sheet.*

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class OrderDetailSheet(
    private val applicationContext: Context,
    private var orderID: String,
    private var login_id: String?
) : BottomSheetDialogFragment() {

    private val TAG = "OrderDetailSheet"
    var Adaptor: OrderItemListAdaptor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.shoppy_order_details_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(login_id, orderID)
    }

    private fun init(loginId: String?, orderID: String) {

        getOrderDetails(loginId, orderID)
    }

    private fun getOrderDetails(loginId: String?, orderID: String) {
        val stringRequest =
            object : StringRequest(
                Method.POST, MySingleton.webService + "Get_order_history",
                Response.Listener<String> { response ->
                    try {
                        val objects = JSONObject(response)
                        Log.e(TAG, "Get_order_history \n $response")

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
                    loginId?.let { params.put("login_id", it) }
                    params.put("order_id", orderID)
                    return params
                }
            }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
    }

    private fun generateRecyclerView(array: JSONArray) {
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        orderItemListRecycler.layoutManager = layoutManager
        Adaptor = OrderItemListAdaptor(applicationContext, array)

        orderItemListRecycler.visibility = View.VISIBLE


//        orderRecycler.addItemDecoration(
//            DividerItemDecoration(
//                orderRecycler.getContext(),
//                DividerItemDecoration.VERTICAL
//            )
//        )

        orderItemListRecycler.adapter = Adaptor
    }
}