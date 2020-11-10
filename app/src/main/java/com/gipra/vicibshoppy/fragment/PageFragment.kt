package com.gipra.vicibshoppy.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.activity.CategoryProducts
import com.gipra.vicibshoppy.recyclerAdaptor.CategoryHomeRecycler
import com.gipra.vicibshoppy.utlis.MySingleton
import kotlinx.android.synthetic.main.shoppy_fragment_dynamic.*
import org.json.JSONException
import org.json.JSONObject


class PageFragment : Fragment() {
    private  val TAG = "PageFragment"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.shoppy_fragment_dynamic, container, false)
        return view
    }

    companion object {
        val PAGE_ID = "PAGE_NUM"
        val PAGE_NAME = "PAGE_NAME"
        fun newInstance(page: String,name : String): PageFragment {
            Log.e("page id",page)
            val fragment = PageFragment()
            val args = Bundle()
            args.putString(PAGE_ID, page)
            args.putString(PAGE_NAME, name)
            fragment.setArguments(args)
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val catID = getArguments()?.getString(PAGE_ID)
        val catName = getArguments()?.getString(PAGE_NAME)

        Log.e(TAG,catID)


        getSampleProducts(catID)


        viewMore.setOnClickListener {
            val intent = Intent(activity, CategoryProducts::class.java)
            intent.putExtra("cat_id",catID)
            intent.putExtra("cat_name",catName)
            activity?.startActivity(intent)
        }




           // showCatItems()



    }

    private fun showCatItems() {
        //***************************************************************************************************************
        //Category recyclerView

//        val layoutManager = GridLayoutManager(activity, 2)
//        layoutManager.orientation = GridLayoutManager.VERTICAL
//        categoryRecycler?.layoutManager = layoutManager
//        val Adaptor = CategoryTestRecycler(activity, CatImageModel.suppler.imagesCategory, this)
//
//        viewMore?.visibility= View.VISIBLE
//        categoryRecycler?.adapter = Adaptor
        //***************************************************************************************************************
    }

    private fun getSampleProducts(catID: String?) {
        shimmerHomeCat.startShimmerAnimation()
        val stringRequest = object : StringRequest(
            Method.POST, MySingleton.webService+"Product_list",
            Response.Listener<String> { response ->
                try {
                    val objects = JSONObject(response)
                     Log.e(TAG,response)

                    if (objects.getString("status")=="1"){
                        val array = objects.getJSONArray("data")

                        shimmerHomeCat?.stopShimmerAnimation()
                        shimmerHomeCat?.visibility =  View.GONE

                        val layoutManager = GridLayoutManager(activity, 2)
                        layoutManager.orientation = GridLayoutManager.VERTICAL
                        categoryRecycler?.layoutManager = layoutManager
                        val Adaptor = CategoryHomeRecycler(activity,array)

                        categoryRecycler?.visibility =View.VISIBLE
                        viewMore?.visibility =View.VISIBLE

                        categoryRecycler?.adapter = Adaptor
                    
                    }else{

                        viewMore?.visibility =View.GONE
                        shimmerHomeCat?.visibility =  View.GONE
                        noProduct?.visibility = View.VISIBLE
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
        activity?.let { VolleySingletonActivity.getInstance(it).addToRequestQueue(stringRequest) }

    }



}