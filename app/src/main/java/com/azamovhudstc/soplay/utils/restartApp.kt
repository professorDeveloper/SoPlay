package com.azamovhudstc.soplay.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import com.azamovhudstc.soplay.R
import com.google.android.material.snackbar.Snackbar

fun Context.restartApp(view: View) {
    val mainIntent = Intent.makeRestartActivityTask(
        packageManager.getLaunchIntentForPackage(this.packageName)!!.component
    )
    val component = ComponentName(this@restartApp.packageName, this@restartApp::class.qualifiedName!!)
    Snackbar.make(view, R.string.restart_app, Snackbar.LENGTH_INDEFINITE).apply {
        setAction(R.string.do_it) {
            this.dismiss()
            try {
                startActivity(Intent().setComponent(component))
            } catch (anything: Exception) {
                startActivity(mainIntent)
            }
            Runtime.getRuntime().exit(0)
        }
        show()
    }
}
@SuppressLint("IntentWithNullActionLaunch")
fun Activity.reloadActivity() {
    Refresh.all()
    finish()
    startActivity(Intent(this, this::class.java))
    initActivity(this)
}
