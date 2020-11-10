package com.gipra.vicibshoppy.utlis

import android.R
import android.content.Context
import android.view.View
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class MySingleton {

    companion object {

        val webService: String = "https://www.vicibhomelyindia.com/api_demo/api_demo/Webservices/"
        val imageBaseUrl: String =
            "https://www.vicibhomelyindia.com/superadmin/productimages/300X300/"

    }


}