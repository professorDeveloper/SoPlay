package com.azamovhudstc.soplay.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import com.azamovhudstc.soplay.BuildConfig
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.ui.activity.MainActivity
import com.azamovhudstc.soplay.ui.screens.bottomsheet.CustomBottomDialog
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

object AppUpdater {
    suspend fun check(activity: MainActivity, post: Boolean = false) {
        if (post) snackString("Checking for Update")
        val repo = activity.getString(R.string.repo)
        tryWithSuspend {
            val md =
                client.get("https://raw.githubusercontent.com/$repo/master/stable.md").text

            val version = md.substringAfter("# ").substringBefore("\n")
            logMessage("""Version: $version""")
            val dontShow = loadData("dont_ask_for_update_$version") ?: true
            if (compareVersion(version) && !dontShow && !activity.isDestroyed) activity.runOnUiThread {
                CustomBottomDialog.newInstance().apply {
                    setTitleText("Update Available")
                    addView(
                        TextView(activity).apply {
                            val markWon = Markwon.builder(activity)
                                .usePlugin(SoftBreakAddsNewLinePlugin.create()).build()
                            markWon.setMarkdown(this, md)
                        }
                    )

                    setCheck("Boshqa Ko`rsatilmasin $version", false) { isChecked ->
                        if (isChecked) {
                            saveData("dont_ask_for_update_$version", isChecked)
                        }
                    }
                    setPositiveButton("O`rnatish") {
                        MainScope().launch(Dispatchers.IO) {
                            try {
                                client.get("https://api.github.com/repos/$repo/releases/tags/v$version")
                                    .parsed<GithubResponse>().assets?.find {
                                        it.browserDownloadURL.endsWith("apk")
                                    }?.browserDownloadURL.apply {
                                        if (this != null) activity.downloadUpdate(version, this)
                                        else openLinkInBrowser(
                                            "https://github.com/repos/$repo/releases/tag/v$version",
                                            activity
                                        )
                                    }
                            } catch (e: Exception) {
                                logError(e)
                            }
                        }
                        dismiss()
                    }
                    setNegativeButton("Bekor qilish") {
                        dismiss()
                    }
                    show(activity.supportFragmentManager, "dialog")
                }
            }
            else {
                if (post) snackString("No Update Found")
            }
        }
    }

    private fun compareVersion(version: String): Boolean {

        fun toDouble(list: List<String>): Double {
            return list.mapIndexed { i: Int, s: String ->
                when (i) {
                    0 -> s.toDouble() * 100
                    1 -> s.toDouble() * 10
                    2 -> s.toDouble()
                    3 -> "0.$s".toDouble()
                    else -> s.toDouble()
                }
            }.sum()
        }

        val new = toDouble(version.split("."))
        val curr = toDouble(BuildConfig.VERSION_NAME.split("."))
        return new > curr
    }


    //Blatantly kanged from https://github.com/LagradOst/CloudStream-3/blob/master/app/src/main/java/com/lagradost/cloudstream3/utils/InAppUpdater.kt
    private fun Activity.downloadUpdate(version: String, url: String): Boolean {

        snackString("Downloading Update $version")

        val downloadManager = this.getSystemService<DownloadManager>()!!

        val request = DownloadManager.Request(Uri.parse(url))
            .setMimeType("application/vnd.android.package-archive")
            .setTitle("Downloading Saikou $version")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "Saikou $version.apk"
            )
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(true)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val id = try {
            downloadManager.enqueue(request)
        } catch (e: Exception) {
            logError(e)
            -1
        }
        if (id == -1L) return true
        registerReceiver(
            object : BroadcastReceiver() {
                @SuppressLint("Range")
                override fun onReceive(context: Context?, intent: Intent?) {
                    try {
                        val downloadId = intent?.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, id
                        ) ?: id

                        val query = DownloadManager.Query()
                        query.setFilterById(downloadId)
                        val c = downloadManager.query(query)

                        if (c.moveToFirst()) {
                            val columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
                            if (DownloadManager.STATUS_SUCCESSFUL == c
                                    .getInt(columnIndex)
                            ) {
                                c.getColumnIndex(DownloadManager.COLUMN_MEDIAPROVIDER_URI)
                                val uri = Uri.parse(
                                    c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                                )
                                openApk(this@downloadUpdate, uri)
                            }
                        }
                    } catch (e: Exception) {
                        logError(e)
                    }
                }
            }, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        return true
    }

    fun openApk(context: Context, uri: Uri) {
        try {
            uri.path?.let {
                val contentUri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    File(it)
                )
                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                    data = contentUri
                }
                context.startActivity(installIntent)
            }
        } catch (e: Exception) {
            logError(e)
        }
    }

    @kotlinx.serialization.Serializable
    data class GithubResponse(
        val assets: List<Asset>? = null
    ) {
        @Serializable
        data class Asset(
            @SerialName("browser_download_url")
            val browserDownloadURL: String
        )
    }
}