package com.gipra.vicibshoppy.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.application.ConnectivityReceiver
import com.gipra.vicibshoppy.utlis.MySingleton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.shoppy_activity_search.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class SearchActivity : AppCompatActivity(),  ConnectivityReceiver.ConnectivityReceiverListener {
    private val TAG = "TestClass"
    private var snackBar: Snackbar? = null
    private var flag: Int = 0


    private lateinit var adaptor: ArrayAdapter<*>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shoppy_activity_search)

        val toolbar: Toolbar = findViewById(R.id.toolBar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_black)

//        val bundle: Bundle? = intent.extras
//        var productName = bundle!!.getString("productName")

        toolbar.setTitleTextColor(Color.GRAY)
        setSupportActionBar(toolbar)

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.rgb(255, 255, 255)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }

        getProductNames()

        listView.emptyView = emptyTextView
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.appbar_menu, menu)


        val search: MenuItem = menu?.findItem(R.id.search)
        val searchView: SearchView = search?.actionView as SearchView




        searchView.queryHint = "Search product"


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptor.filter.filter(newText)
                return true
            }

        })

        return true
    }



    //***************************************************************************************************
    //get List of Products
    private fun getProductNames() {
        searchShimmer.startShimmerAnimation()
        val stringRequest =
            object : StringRequest(
                Method.POST, MySingleton.webService + "Product_names",
                Response.Listener<String> { response ->
                    try {
                        val objects = JSONObject(response)
                        //  Log.e(TAG,response)

                        if (objects.getString("status") == "1") {
                            val array = objects.getJSONArray("data")

                            setAdaptor(array)

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

        searchShimmer.stopShimmerAnimation()
        searchShimmer.visibility = View.GONE

        listView.visibility = View.VISIBLE

        adaptor = ArrayAdapter(
            this, android.R.layout.simple_selectable_list_item, nameList
        )
        listView.adapter = adaptor

        listView.setOnItemClickListener { parent, view, position, id ->

            var id = array.getJSONObject(position).getString("mid")
            val intent = Intent(applicationContext, ProductActivity::class.java)
            intent.putExtra("p_id", id)
            startActivity(intent)
        }


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