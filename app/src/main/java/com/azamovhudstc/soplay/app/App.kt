package com.azamovhudstc.soplay.app

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.azamovhudstc.soplay.data.PhoneData
import com.azamovhudstc.soplay.utils.initializeNetwork
import dagger.hilt.android.HiltAndroidApp
import java.text.SimpleDateFormat
import java.util.*

@HiltAndroidApp
class App : Application() {
    companion object {
        lateinit var instance: App
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        instance = this

        initializeNetwork(this)
    }
}
fun getDeviceID(): String {
    return "35" + //we make this look like a valid IMEI
            Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDeviceData(context: Context): PhoneData {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    val imei = getDeviceID()!!

    val phoneModel = Build.MODEL ?: "Model not available"

    val networkOperatorName = telephonyManager.networkOperatorName ?: "Operator name not available"

    val currentDate = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val installedAppDate = dateFormat.format(currentDate)

    return PhoneData(imei, phoneModel, networkOperatorName, installedAppDate)
}
