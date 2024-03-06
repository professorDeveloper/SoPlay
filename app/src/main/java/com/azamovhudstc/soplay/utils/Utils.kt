package com.azamovhudstc.soplay.utils

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.azamovhudstc.soplay.R
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.concurrent.TimeUnit

object Utils {
    fun showNetworkDialog(context: FragmentActivity, container: View?) {
        val dialog = Dialog(context)
        container?.gone()
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.no_connection, null)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setCancelable(false)
        val tryAgainButton =
            dialogView.findViewById<com.google.android.material.card.MaterialCardView>(R.id.try_again)
        val tryAgainTxt = dialogView.findViewById<TextView>(R.id.try_againtxt)
        val tryAgainProgress = dialogView.findViewById<ProgressBar>(R.id.try_again_progress)
        tryAgainButton.setOnClickListener {
            tryAgainTxt.gone()
            tryAgainProgress.visible()
            context.lifecycleScope.launch {
                delay(600)
                if (hasConnection()) {
                    container?.visible()
                    dialog.dismiss()
                    tryAgainTxt.visible()
                    tryAgainProgress.gone()

                } else {
                    container?.gone()
                    tryAgainTxt.visible()
                    tryAgainProgress.gone()
                    dialog.setContentView(dialogView)
                    dialog.show()
                }
            }

        }
        dialog.setContentView(dialogView)

        dialog.show()


    }

    var httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(2, TimeUnit.MINUTES)
        .build()

    fun getAsilMedia(
        host: String? = null,
        pathSegment: ArrayList<String>? = null,
        mapOfHeaders: Map<String, String>? = null,
        params: Map<String, String>? = null,
    ): String {
        val urlBuilder = HttpUrl.Builder()
            .scheme("http") // Replace with your scheme (http or https)
            .host(host!!) // Replace with your actual host
        pathSegment?.forEach {
            urlBuilder.addPathSegment(it)
        }


        if (!params.isNullOrEmpty()) {
            params.forEach {
                urlBuilder.addQueryParameter(it.key, it.value)
            }
        }

        val requestBuilder = Request.Builder().url(urlBuilder.build())
        if (!mapOfHeaders.isNullOrEmpty()) {
            mapOfHeaders.forEach {
                requestBuilder.addHeader(it.key, it.value)
            }
        }

        return httpClient.newCall(requestBuilder.build())
            .execute().body.string()
    }

    fun get(
        url: String,
        mapOfHeaders: Map<String, String>? = null
    ): String {
        val requestBuilder = Request.Builder().url(url)
        if (!mapOfHeaders.isNullOrEmpty()) {
            mapOfHeaders.forEach {
                requestBuilder.addHeader(it.key, it.value)
            }
        }
        return httpClient.newCall(requestBuilder.build())
            .execute().body!!.string()
    }

    fun post(
        url: String,
        mapOfHeaders: Map<String, String>? = null,
        payload: Map<String, String>? = null
    ): String {
        val requestBuilder = Request.Builder().url(url)

        if (!mapOfHeaders.isNullOrEmpty()) {
            mapOfHeaders.forEach {
                requestBuilder.addHeader(it.key, it.value)
            }
        }

        val requestBody = payload?.let {
            FormBody.Builder().apply {
                it.forEach { (key, value) ->
                    add(key, value)
                }
            }.build()
        }

        if (requestBody != null) {
            requestBuilder.post(requestBody)
        }

        val response = httpClient.newCall(requestBuilder.build()).execute()
        return response.body?.string() ?: ""
    }

    fun getJsoup(
        url: String,
        mapOfHeaders: Map<String, String>? = null
    ): Document {
        return Jsoup.parse(get(url, mapOfHeaders))
    }

    fun getJsoupAsilMedia(
        host: String,
        pathSegment: ArrayList<String>? /* = java.util.ArrayList<kotlin.String>? */ = null,
        params: Map<String, String>? = null,
        mapOfHeaders: Map<String, String>? = null
    ): Document {
        return Jsoup.parse(
            getAsilMedia(
                host = host,
                pathSegment = pathSegment,
                params = params,
                mapOfHeaders = mapOfHeaders
            )
        )
    }

    fun getJson(
        url: String,
        mapOfHeaders: Map<String, String>? = null
    ): JsonElement? {
        return JsonParser.parseString(get(url, mapOfHeaders))
    }

    fun postJson(
        url: String,
        mapOfHeaders: Map<String, String>? = null,
        payload: Map<String, String>? = null
    ): JsonElement? {
        val res = post(url, mapOfHeaders, payload)
        return JsonParser.parseString(res)
    }
}