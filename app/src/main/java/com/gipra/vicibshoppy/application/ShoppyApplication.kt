package com.gipra.vicibshoppy.application

import android.app.Application
import android.util.Log


class ShoppyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.e(TAG, "muApplication")
    }

    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener?) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    companion object {
        private const val TAG = "MyApplication"
        var instance: ShoppyApplication? = null
    }
}
