package com.azamovhudstc.soplay.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.app.App
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.ui.activity.MainActivity
import com.azamovhudstc.soplay.utils.Download.adm
import com.azamovhudstc.soplay.utils.Download.defaultDownload
import com.azamovhudstc.soplay.utils.Download.oneDM
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.lagradost.nicehttp.addGenericDns
import okhttp3.OkHttpClient
import java.io.*
import kotlin.reflect.KFunction

fun initActivity(a: Activity) {
    val window = a.window
    WindowCompat.setDecorFitsSystemWindows(window, true)
    AppCompatDelegate.setDefaultNightMode(
        AppCompatDelegate.MODE_NIGHT_NO
    )

}

fun download(activity: Activity, episode: MovieInfo, link: String, animeTitle: String) {
    Toast.makeText(activity, "Downloading...", Toast.LENGTH_SHORT).show()
    when (loadData<Int>("settings_download_manager", activity, false) ?: 0) {
        1 -> oneDM(activity, episode, link, animeTitle)
        2 -> adm(activity, episode, link, animeTitle)
        else -> defaultDownload(activity, episode, link, animeTitle)
    }
}


fun startMainActivity(activity: Activity, bundle: Bundle? = null) {
    activity.finishAffinity()
    activity.startActivity(
        Intent(
            activity,
            MainActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            if (bundle != null) putExtras(bundle)
        }
    )
}


@Suppress("UNCHECKED_CAST")
fun <T> loadData(fileName: String, context: Context? = null, toast: Boolean = true): T? {
    val a = context ?: App.instance
    try {
        if (a?.fileList() != null)
            if (fileName in a.fileList()) {
                val fileIS: FileInputStream = a.openFileInput(fileName)
                val objIS = ObjectInputStream(fileIS)
                val data = objIS.readObject() as T
                objIS.close()
                fileIS.close()
                return data
            }
    } catch (e: Exception) {
        if (toast) snackString("Error loading data $fileName")
        e.printStackTrace()
    }
    return null
}

@Suppress("DEPRECATION")
fun Activity.hideSystemBars() {
    window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
}

@Suppress("DEPRECATION")
fun Activity.hideStatusBar() {
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun ImageView.loadImage(url: String?, size: Int = 0) {
    if (!url.isNullOrEmpty()) {
        loadImage(FileUrl(url), size)
    }
}

fun ImageView.loadImage(file: FileUrl?, size: Int = 0) {
    if (file?.url?.isNotEmpty() == true) {
        tryWith {
            val glideUrl = GlideUrl(file.url) { file.headers }
            Glide.with(this.context).load(glideUrl)
                .transition(DrawableTransitionOptions.withCrossFade()).override(size).into(this)
        }
    }
}

data class FileUrl(
    val url: String,
    val headers: Map<String, String> = mapOf()
) : Serializable {
    companion object {
        operator fun get(url: String?, headers: Map<String, String> = mapOf()): FileUrl? {
            return FileUrl(url ?: return null, headers)
        }
    }
}

//Credits to leg
data class Lazier<T>(
    val lClass: KFunction<T>,
    val name: String
) {
    val get = lazy { lClass.call() }
}

fun <T> lazyList(vararg objects: Pair<String, KFunction<T>>): List<Lazier<T>> {
    return objects.map {
        Lazier(it.second, it.first)
    }
}

fun <T> T.printIt(pre: String = ""): T {
    println("$pre$this")
    return this
}


fun OkHttpClient.Builder.addGoogleDns() = (
        addGenericDns(
            "https://dns.google/dns-query",
            listOf(
                "8.8.4.4",
                "8.8.8.8"
            )
        ))

fun OkHttpClient.Builder.addCloudFlareDns() = (
        addGenericDns(
            "https://cloudflare-dns.com/dns-query",
            listOf(
                "1.1.1.1",
                "1.0.0.1",
                "2606:4700:4700::1111",
                "2606:4700:4700::1001"
            )
        ))

fun OkHttpClient.Builder.addAdGuardDns() = (
        addGenericDns(
            "https://dns.adguard.com/dns-query",
            listOf(
                // "Non-filtering"
                "94.140.14.140",
                "94.140.14.141",
            )
        ))

fun <T> tryWith(post: Boolean = false, snackbar: Boolean = true, call: () -> T): T? {
    return try {
        call.invoke()
    } catch (e: Throwable) {
        null
    }
}

fun saveData(fileName: String, data: Any?, context: Context? = null) {
    tryWith {
        val a = context ?: App.instance
        val fos: FileOutputStream = a.openFileOutput(fileName, Context.MODE_PRIVATE)
        val os = ObjectOutputStream(fos)
        os.writeObject(data)
        os.close()
        fos.close()
    }
}


fun setAnimation(
    context: Context,
    viewToAnimate: View,
    duration: Long = 150,
    list: FloatArray = floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f),
    pivot: Pair<Float, Float> = 0.5f to 0.5f
) {
    val anim = ScaleAnimation(
        list[0],
        list[1],
        list[2],
        list[3],
        Animation.RELATIVE_TO_SELF,
        pivot.first,
        Animation.RELATIVE_TO_SELF,
        pivot.second
    )
    anim.duration = (duration * 1f).toLong()
    anim.setInterpolator(context, R.anim.over_shoot)
    viewToAnimate.startAnimation(anim)
}


fun setSlideIn() = AnimationSet(false).apply {
    var animation: Animation = AlphaAnimation(0.0f, 1.0f)
    animation.duration = (500 * 1f).toLong()
    animation.interpolator = AccelerateDecelerateInterpolator()
    addAnimation(animation)

    animation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF, 1.0f,
        Animation.RELATIVE_TO_SELF, 0f,
        Animation.RELATIVE_TO_SELF, 0.0f,
        Animation.RELATIVE_TO_SELF, 0f
    )

    animation.duration = (750 * 1f).toLong()
    animation.interpolator = OvershootInterpolator(1.1f)
    addAnimation(animation)
}

fun setSlideUp() = AnimationSet(false).apply {
    var animation: Animation = AlphaAnimation(0.0f, 1.0f)
    animation.duration = (500 * 1f).toLong()
    animation.interpolator = AccelerateDecelerateInterpolator()
    addAnimation(animation)

    animation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF, 0.0f,
        Animation.RELATIVE_TO_SELF, 0f,
        Animation.RELATIVE_TO_SELF, 1.0f,
        Animation.RELATIVE_TO_SELF, 0f
    )

    animation.duration = (750 * 1f).toLong()
    animation.interpolator = OvershootInterpolator(1.1f)
    addAnimation(animation)
}
