package com.makazemi.bitcoinwidget.network

import android.content.Context
import android.net.ConnectivityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CheckConnection @Inject constructor(@ApplicationContext val application: Context) {

    fun isConnectedToTheInternet(): Boolean {
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            return cm.activeNetworkInfo?.isConnected!!
        } catch (e: Exception) {
        }
        return false
    }

}