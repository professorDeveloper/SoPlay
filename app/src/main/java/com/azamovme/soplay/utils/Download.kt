package com.azamovme.soplay.utils

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import com.azamovme.soplay.data.response.MovieInfo
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
            direct = File("storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/SoPlay/")
            if (!direct.exists()) direct.mkdirs()
        }
        return direct
    }

    fun defaultDownload(activity: Activity, episode: MovieInfo, link: String, epTitle: String) {
        val downloadManager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(parseUrl(link)))
        println("LINK :${parseUrl(link)}")
        // Remove special characters from the title
        val sanitizedTitle = episode!!.title.replace("[\\\\/:*?\"<>|]".toRegex(), "")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                val fileExtension = ".mp4" // MP4 formatni belgilash

                val externalDirs = ContextCompat.getExternalFilesDirs(activity, null)
                if (loadData<Boolean>("sd_dl") == true && externalDirs.size > 1 && externalDirs[1] != null) {
                    val parentDirectory =
                        externalDirs[1].toString() + "/SoPlay/${sanitizedTitle}&&${epTitle}/"
                    val directory = File(parentDirectory)
                    if (!directory.exists()) directory.mkdirs()
                    request.setDestinationUri(Uri.fromFile(File("$parentDirectory$sanitizedTitle")))
                } else {
                    val directory =
                        File(Environment.DIRECTORY_DOWNLOADS + "/SoPlay/${sanitizedTitle}&&${epTitle}/")
                    if (!directory.exists()) directory.mkdirs()
                    request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "/SoPlay/${sanitizedTitle}/$epTitle$fileExtension"
                    )
                }

                request.setTitle("$epTitle:$sanitizedTitle")
                downloadManager.enqueue(request)
                println("Downloading")
            } catch (e: SecurityException) {
                println("An error occurred: ${e.message}")
                logError(e)
            } catch (e: Exception) {
                println("An error occurred: ${e.message}")
                logError(e)
            }
        }
    }

    private fun parseUrl(url: String): String? {
        // Split the URL using "?" as the delimiter
        val parts = url.split("?")

        // Check if there are two parts (base URL and parameters)
        if (parts.size == 2) {
            // Split the parameters using "&" as the delimiter
            val parameters = parts[1].split("&")

            // Find the parameter with "file=" prefix
            val fileParameter = parameters.find { it.startsWith("file=") }

            // Extract the value after "file=" prefix
            return fileParameter?.substringAfter("file=")
        }


        return url
    }


}