package com.azamovhudstc.soplay.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import com.azamovhudstc.soplay.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject

class GithubAppUpdater(private val context: Context, private val owner: String, private val repo: String) {

    private val latestReleaseUrl = "https://api.github.com/repos/$owner/$repo/releases/latest"

    fun checkForUpdates() {
        FetchLatestReleaseTask().execute(latestReleaseUrl)
    }

    private inner class FetchLatestReleaseTask : AsyncTask<String, Void, String?>() {
        override fun doInBackground(vararg urls: String): String? {
            val url = urls[0]
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()
            return try {
                val response = client.newCall(request).execute()
                response.body?.string()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null) {
                try {
                    val json = JSONObject(result)
                    val tagName = json.getString("tag_name")
                    val version = tagName.removePrefix("v")
                    val assets = json.getJSONArray("assets")
                    if (assets.length() > 0) {
                        val browserDownloadUrl = assets.getJSONObject(0).getString("browser_download_url")
                        handleLatestRelease(version, browserDownloadUrl)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun handleLatestRelease(version: String, downloadUrl: String) {
        val currentVersion = BuildConfig.VERSION_NAME
        if (version != currentVersion) {
            showUpdateDialog(version, downloadUrl)
        }
    }

    private fun showUpdateDialog(version: String, downloadUrl: String) {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Update Available")
            .setMessage("A new version ($version) is available. Do you want to update?")
            .setPositiveButton("Update") { _, _ ->
                downloadAndInstallUpdate(downloadUrl)
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun downloadAndInstallUpdate(downloadUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
        context.startActivity(intent)
    }
}
