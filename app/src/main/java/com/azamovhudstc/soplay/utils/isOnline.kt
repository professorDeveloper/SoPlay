package com.azamovhudstc.soplay.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build

fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return tryWith {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val cap = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            return@tryWith if (cap != null) {
                when {
                    cap.hasTransport(TRANSPORT_BLUETOOTH) ||
                            cap.hasTransport(TRANSPORT_CELLULAR) ||
                            cap.hasTransport(TRANSPORT_ETHERNET) ||
                            cap.hasTransport(TRANSPORT_LOWPAN) ||
                            cap.hasTransport(TRANSPORT_USB) ||
                            cap.hasTransport(TRANSPORT_VPN) ||
                            cap.hasTransport(TRANSPORT_WIFI) ||
                            cap.hasTransport(TRANSPORT_WIFI_AWARE) -> true
                    else                                           -> false
                }
            } else false
        } else true
    } ?: false
}
