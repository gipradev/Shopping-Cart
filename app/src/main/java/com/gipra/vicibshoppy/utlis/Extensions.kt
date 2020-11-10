package com.gipra.vicibshoppy.utlis

import VolleySingleton
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import es.dmoral.toasty.Toasty


fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {

    Toast.makeText(this, message, duration).show()

}

fun Context.showToasty(message: String, duration: Int = Toast.LENGTH_SHORT) {

    Toasty.success(this, message, Toast.LENGTH_SHORT, true).show();
}


fun Context.fetchFromServer(header: String, map: Map<String, String>? = null): String? {

    var responseData: String? = null
    val stringRequest = object : StringRequest(
        Method.POST, MySingleton.webService + header,
        Response.Listener<String> { response ->

            responseData = response.toString()
            Log.e("Extension", responseData)
        },
        Response.ErrorListener { error ->
            responseData = error.toString()
        }) {
        @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            var params = map as HashMap<String, String>
            return params
        }
    }

    VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
    return responseData
}

fun Context.changeStatusBarColor(window: Window, red: Int, green: Int, blue: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.statusBarColor = Color.rgb(red, green, blue)
    }
}
//
//fun showSnackBar(
//    viewSnackBar: Snackbar?,
//    view: View,
//    message: String,
//    duration: Int,
//    isConnected: Boolean
//)  {
//
//    var snackBar: Snackbar? = viewSnackBar
//    if (!isConnected) {
//        snackBar = Snackbar.make(
//            view,
//            message,
//            duration
//        )
//        //Assume "rootLayout" as the root layout of every activity.
//        snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
//        snackBar?.show()
//
//
//
//    }  else {
//        snackBar?.dismiss()
//    }
//
//}
