package com.gipra.vicibshoppy.activity

import VolleySingleton
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.application.ConnectivityReceiver
import com.gipra.vicibshoppy.utlis.MySingleton
import com.gipra.vicibshoppy.utlis.showToast
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.shoppy_activity_address.*
import kotlinx.android.synthetic.main.shoppy_fragment_first.*
import kotlinx.android.synthetic.main.shoppy_fragment_second.*
import org.json.JSONObject

class AddressActivity : AppCompatActivity(), View.OnClickListener ,
    ConnectivityReceiver.ConnectivityReceiverListener{
    private val TAG = "AddressActivity"
    private var addressId: String? = null
    private var login_id: String? = null

    private var snackBar: Snackbar? = null
    private var flag: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shoppy_activity_address)

        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.rgb(112, 189, 231)
        }

        val bundle: Bundle? = intent.extras
        addressId = bundle!!.getString("address_id")

        Log.e(TAG,addressId)

        if (addressId != "0") {
            getSelectedAddress(addressId.toString())
            fabSubmit.visibility = View.GONE
            fabUpdate.visibility = View.VISIBLE
        }
        else{
            fabSubmit.visibility = View.VISIBLE
            fabUpdate.visibility = View.GONE
        }

        get_SharedPreferences()



        toolBarAddress.setNavigationOnClickListener { onBackPressed() }
        fabSubmit.setOnClickListener(this)
        fabUpdate.setOnClickListener(this)

    }

    private fun get_SharedPreferences() {
        var user = applicationContext.getSharedPreferences("Login", MODE_PRIVATE)
        login_id = user.getString("user_id", "4")

    }

    private fun getSelectedAddress(addressId: String) {



        val stringRequest =
            object : StringRequest(Method.POST, MySingleton.webService + "Get_id_address",
                Response.Listener { response ->
                    var jsonObject = JSONObject(response)
                    Log.e(TAG, "Get_id_address : $response")
                    if (jsonObject.getString("status") == "1") {


                        setValues(jsonObject)


                    } else {


                    }
                }, Response.ErrorListener { error ->
                    showToast("Error : $error")
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    login_id?.let { params.put("login_id", it) }
                    params.put("address_id", addressId)
                    return params
                }
            }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)

    }

    private fun setValues(jsonObject: JSONObject) {

        firstName.setText(jsonObject.getString("Name"))
        email.setText(jsonObject.getString("email"))
        mobile.setText(jsonObject.getString("mobile"))
        house.setText(jsonObject.getString("address"))
        //landmark.setText(jsonObject.getString("Name"))
        pinCode.setText(jsonObject.getString("pin"))
        place.setText(jsonObject.getString("place"))
    }

    override fun onClick(v: View?) {

        when(v?.id){
            R.id.fabSubmit -> validateForm()
            R.id.fabUpdate -> validateUpdateForm()
        }

    }

    private fun validateUpdateForm() {
        onUpdateSnack()
    }

    private fun onUpdateSnack() {
        val snackbar = Snackbar
            .make(coordinateLayout, "confirmation ", Snackbar.LENGTH_LONG)
            .setAction("Yes", {

                var nameText = firstName.text.toString()
                var emailText = email.text.toString()
                var mobileText = mobile.text.toString()
                var houseText = house.text.toString()
                var landMarkText = landmark.text.toString()
                var pincodeText = pinCode.text.toString()
                var placeText = place.text.toString()
//                var stateText = stateSpinner.selectedItem.toString()
//                var districtText = districtSpinner.selectedItem.toString()

               updateForm(
                    nameText, emailText, mobileText, houseText, landMarkText, pincodeText,
                    placeText
                )


            })
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun updateForm(
        nameText: String?, emailText: String?,
        mobileText: String?, houseText: String?,
        landMarkText: String?, pincodeText: String?,
        placeText: String?
    ) {

        Log.e(TAG,"$nameText \n $emailText \n $mobileText \n $houseText \n  $landMarkText " +
                "\n $pincodeText \n $placeText \n $addressId \n $login_id")

        val stringRequest =
            object : StringRequest(Method.POST, MySingleton.webService + "Edit_Shipping_Address",
                Response.Listener { response ->
                    var jsonObject = JSONObject(response)

                    if (jsonObject.getString("status") == "1") {


                        showToast(jsonObject.getString("message"))


                    } else {


                    }
                }, Response.ErrorListener { error ->
                    showToast("Error : $error")
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    nameText?.let { params.put("c_name", it) }
                    emailText?.let { params.put("C_EMAIL", it) }
                    mobileText?.let { params.put("C_MOBILE", it) }
                    houseText?.let { params.put("c_address", it) }
                    pincodeText?.let { params.put("n_pincode", it) }
                    params.put("C_STATE", "KERALA")
                    params.put("C_DISTRICT", "ALAPPUZHA")
                    placeText?.let { params.put("C_PLACE", it) }
                    login_id?.let { params.put("login_id", it) }
                    addressId?.let { params.put("address_id", it) }
                    return params
                }
            }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
    }



    private fun validateForm() {
        var stat = false
//        if (firstName.text.toString().isEmpty()) {
//            firstName.setError("Required")
//            firstName.requestFocus()
//            stat = true
//
//        } else if (email.text.toString().isEmpty()) {
//            email.setError("Required")
//            email.requestFocus()
//            stat = true
//        } else if (mobile.text.toString().isEmpty()) {
//            mobile.setError("Required")
//            mobile.requestFocus()
//            stat = true
//        } else if (house.text.toString().isEmpty()) {
//            house.setError("Required")
//            house.requestFocus()
//            stat = true
//        } else if (landmark.text.toString().isEmpty()) {
//            landmark.setError("Required")
//            landmark.requestFocus()
//            stat = true
//        } else if (pinCode.text.toString().isEmpty()) {
//            pinCode.setError("Required")
//            pinCode.requestFocus()
//            stat = true
//        } else if (city.text.toString().isEmpty()) {
//            city.setError("Required")
//            city.requestFocus()
//            stat = true
//        } else if (place.text.toString().isEmpty()) {
//            place.setError("Required")
//            place.requestFocus()
//            stat = true
        //   }
        if (!stat) {
            onSnack()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun onSnack() {
        val snackbar = Snackbar
            .make(coordinateLayout, "confirmation", Snackbar.LENGTH_LONG)
            .setAction("Yes", {

                var nameText = firstName.text.toString()
                var emailText = email.text.toString()
                var mobileText = mobile.text.toString()
                var houseText = house.text.toString()
                var landMarkText = landmark.text.toString()
                var pincodeText = pinCode.text.toString()
                var placeText = place.text.toString()
//                var stateText = stateSpinner.selectedItem.toString()
//                var districtText = districtSpinner.selectedItem.toString()

                submitForm(
                    nameText, emailText, mobileText, houseText, landMarkText, pincodeText,
                    placeText
                )


            })
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun submitForm(
        nameText: String?, emailText: String?,
        mobileText: String?, houseText: String?,
        landMarkText: String?, pincodeText: String?,
        placeText: String?
    ) {

        val stringRequest =
            object : StringRequest(Method.POST, MySingleton.webService + "Add_Shipping_Address",
                Response.Listener { response ->
                    var jsonObject = JSONObject(response)

                    if (jsonObject.getString("status") == "1") {


                        showToast(jsonObject.getString("message"))


                    } else {


                    }
                }, Response.ErrorListener { error ->
                    showToast("Error : $error")
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    nameText?.let { params.put("c_name", it) }
                    emailText?.let { params.put("C_EMAIL", it) }
                    mobileText?.let { params.put("C_MOBILE", it) }
                    houseText?.let { params.put("c_address", it) }
                    pincodeText?.let { params.put("n_pincode", it) }
                    params.put("C_STATE", "KERALA")
                    params.put("C_DISTRICT", "ALAPPUZHA")
                    placeText?.let { params.put("C_PLACE", it) }
                    login_id?.let { params.put("login_id", it) }
                    addressId?.let { params.put("address_id", it) }
                    return params
                }
            }
        VolleySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
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

