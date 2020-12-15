package com.gipra.vicibshoppy.VicibShoppy.server

import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class ServiceVolley : ServiceInterface {
    val TAG = ServiceVolley::class.java.simpleName
    val basePath = "https://www.vicibhomelyindia.com/api_demo/api_demo/Webservices/"

    override fun post(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit) {

        val jsonObjReq = object : JsonObjectRequest(Method.POST, basePath + path, params,
            Response.Listener<JSONObject> { response ->
                Log.d(TAG, "$params $path /post request OK! Response: $response")
                completionHandler(response)
            },
            Response.ErrorListener { error ->
                VolleyLog.e(TAG, "/post request fail! Error: ${error.message}")
                completionHandler(null)
            })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/json")
                return headers
            }
        }

        BackendVolley.instance?.addToRequestQueue(jsonObjReq, TAG)
    }


}

