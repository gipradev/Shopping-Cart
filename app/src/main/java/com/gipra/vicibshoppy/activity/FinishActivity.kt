package com.gipra.vicibshoppy.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.utlis.changeStatusBarColor
import kotlinx.android.synthetic.main.shoppy_activity_finish.*


class FinishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shoppy_activity_finish)



        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        val window = window
        changeStatusBarColor(window, 255, 255, 255)

        fabClose.setOnClickListener {
            val intent = Intent(applicationContext, ShoppyHome::class.java)
            startActivity(intent)
            finish()
        }

        val handler = Handler()
        handler.postDelayed(Runnable { // Do something after 5s = 5000ms
            mCheckView.check()
            textMessage.visibility =View.VISIBLE
        }, 1000)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(applicationContext, ShoppyHome::class.java)
        startActivity(intent)
        finish()
    }


}