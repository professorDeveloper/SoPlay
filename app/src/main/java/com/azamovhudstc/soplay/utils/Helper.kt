package com.azamovhudstc.soplay.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.scheduler.Requirements
import com.azamovhudstc.soplay.R
import java.io.File
import java.util.concurrent.Executor

object Helper {


    private var download: DownloadManager? = null
    private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"

    @Synchronized
    @UnstableApi
    fun downloadManager(context: Context): DownloadManager {
        return download ?: let {
            val database = StandaloneDatabaseProvider(context)
            val downloadDirectory = File(getDownloadDirectory(context), DOWNLOAD_CONTENT_DIRECTORY)
            val dataSourceFactory = androidx.media3.datasource.DataSource.Factory {
                val dataSource: androidx.media3.datasource.HttpDataSource = OkHttpDataSource.Factory(Utils.httpClient).createDataSource()
                defaultHeaders.forEach {
                    dataSource.setRequestProperty(it.key, it.value)
                }
                dataSource
            }
            DownloadManager(
                context,
                database,
                SimpleCache(downloadDirectory, NoOpCacheEvictor(), database),
                dataSourceFactory,
                Executor(Runnable::run)
            ).apply {
                requirements = Requirements(Requirements.NETWORK or Requirements.DEVICE_STORAGE_NOT_LOW)
                maxParallelDownloads = 3
            }
        }
    }

    private var downloadDirectory: File? = null

    @Synchronized
    private fun getDownloadDirectory(context: Context): File {
        if (downloadDirectory == null) {
            downloadDirectory = context.getExternalFilesDir(null)
            if (downloadDirectory == null) {
                downloadDirectory = context.filesDir
            }
        }
        return downloadDirectory!!
    }
}