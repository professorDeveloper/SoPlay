package com.azamovhudstc.soplay.utils

import android.app.Activity
import android.app.DownloadManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils.replace
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.azamovhudstc.soplay.data.response.MovieInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object Download {
    @Suppress("DEPRECATION")
    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun getDownloadDir(activity: Activity): File {
        val direct: File
        if (loadData<Boolean>("sd_dl") == true) {
            val arrayOfFiles = ContextCompat.getExternalFilesDirs(activity, null)
            val parentDirectory = arrayOfFiles[1].toString()
            direct = File(parentDirectory)
            if (!direct.exists()) direct.mkdirs()
        } else {
            direct = File("storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/Saikou/")
            if (!direct.exists()) direct.mkdirs()
        }
        return direct
    }

    fun defaultDownload(activity: Activity, episode: MovieInfo, link: String, epTitle: String) {
        val manager =
            activity.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(link))
        val regex = "[\\\\/:*?\"<>|]".toRegex()
        val aTitle = episode.title.replace(regex, "")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                val arrayOfFiles = ContextCompat.getExternalFilesDirs(activity, null)
                if (loadData<Boolean>("sd_dl") == true && arrayOfFiles.size > 1 && arrayOfFiles[0] != null && arrayOfFiles[1] != null) {
                    val parentDirectory =
                        arrayOfFiles[1].toString() + "/Movie/${aTitle}&&${epTitle}/"
                    val direct = File(parentDirectory)
                    if (!direct.exists()) direct.mkdirs()
                    request.setDestinationUri(Uri.fromFile(File("$parentDirectory${aTitle}")))
                } else {
                    val direct =
                        File(Environment.DIRECTORY_DOWNLOADS + "/SoPlay/Movie/${aTitle}&&${epTitle}/")
                    if (!direct.exists()) direct.mkdirs()
                    request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "/Saikou/Movie/${aTitle}/$epTitle"
                    )
                }
                request.setTitle("$epTitle:$aTitle")
                manager.enqueue(request)
                Toast.makeText(activity, "Started Downloading\n$epTitle : $aTitle", Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                logError(e)
            } catch (e: Exception) {
                logError(e)
            }
        }
    }

    fun oneDM(activity: Activity, episode: MovieInfo, link: String, epTitle: String) {
        val appName =
            if (isPackageInstalled("idm.internet.download.manager.plus", activity.packageManager)) {
                "idm.internet.download.manager.plus"
            } else if (isPackageInstalled(
                    "idm.internet.download.manager",
                    activity.packageManager
                )
            ) {
                "idm.internet.download.manager"
            } else if (isPackageInstalled(
                    "idm.internet.download.manager.adm.lite",
                    activity.packageManager
                )
            ) {
                "idm.internet.download.manager.adm.lite"
            } else {
                ""
            }
        if (appName.isNotEmpty()) {
            val regex = "[\\\\/:*?\"<>|]".toRegex()
            val aTitle = episode.title.replace(regex, "")
            val bundle = Bundle()
            defaultHeaders.forEach { a -> bundle.putString(a.key, a.value) }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                component = ComponentName(appName, "idm.internet.download.manager.Downloader")
                data = Uri.parse(link)
                putExtra("extra_headers", bundle)
                putExtra("extra_filename", "$aTitle - $epTitle")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(activity.baseContext, intent, null)
        } else {
            ContextCompat.startActivity(
                activity.baseContext,
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=idm.internet.download.manager")
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                null
            )
            snackString("Please install 1DM")
        }
    }

    fun adm(activity: Activity, episode: MovieInfo, link: String, epTitle: String) {
        if (isPackageInstalled("com.dv.adm", activity.packageManager)) {

            val regex = "[\\\\/:*?\"<>|]".toRegex()
            val aTitle = episode.title.replace(regex, "")
            val bundle = Bundle()
            defaultHeaders.forEach { a -> bundle.putString(a.key, a.value) }
            // unofficial documentation: https://pastebin.com/ScDNr2if (there is no official documentation)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                component = ComponentName("com.dv.adm", "com.dv.adm.AEditor")
                putExtra("com.dv.get.ACTION_LIST_ADD", "${link}<info>$epTitle.mp4")
                putExtra(
                    "com.dv.get.ACTION_LIST_PATH",
                    "${getDownloadDir(activity)}/Movie/${aTitle}/"
                )
                putExtra("android.media.intent.extra.HTTP_HEADERS", bundle)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            ContextCompat.startActivity(activity.baseContext, intent, null)
        } else {
            ContextCompat.startActivity(
                activity.baseContext,
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.dv.adm")).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                ),
                null
            )
            snackString("Please install ADM")
        }
    }
}