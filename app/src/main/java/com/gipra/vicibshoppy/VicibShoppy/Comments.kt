package com.gipra.vicibshoppy.VicibShoppy

import androidx.viewpager2.widget.ViewPager2

//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.LinearLayout
//import androidx.core.content.ContextCompat
//import androidx.core.view.get
//import kotlinx.android.synthetic.main.shoppy_activity_product.*
//import org.json.JSONArray
//
////*********************************************************************************************************
////setUpPageIndicator
//private fun setUpIndicator(imageArray: JSONArray) {
//    val indicators = arrayOfNulls<ImageView>(imageArray.length())
//    val layoutParms: LinearLayout.LayoutParams =
//        LinearLayout.LayoutParams(
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//    layoutParms.setMargins(8, 0, 8, 0)
//
//    for (i in indicators.indices) {
//        indicators[i] = ImageView(applicationContext)
//        indicators[i].apply {
//            this?.setImageDrawable(
//                ContextCompat.getDrawable(applicationContext, R.drawable.inactive_indicator)
//            )
//            this?.layoutParams = layoutParms
//        }
//        indicatorContainer.addView(indicators[i])
//    }
//}
//
////*********************************************************************************************************
//
//
////*********************************************************************************************************
////setCurrentIndicator
//private fun setCurrentInicator(index: Int) {
//
//    val childCount = indicatorContainer.childCount
//    for (i in 0 until childCount) {
//        val imageView = indicatorContainer[i] as ImageView
//        if (i == index) {
//
//            imageView.setImageDrawable(
//                ContextCompat.getDrawable(applicationContext, R.drawable.active_indicator)
//            )
//
//        } else {
//
//            imageView.setImageDrawable(
//                ContextCompat.getDrawable(applicationContext, R.drawable.inactive_indicator)
//            )
//
//        }
//    }
//
//}

//
////Slider call back method
//slideViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//    override fun onPageSelected(position: Int) {
//        super.onPageSelected(position)
//        setCurrentInicator(position)
//    }
//})

//************************************************************************************************
//
////*********************************************************************************************************