package com.soplay.sozo

import java.net.URI
import java.net.URLEncoder

internal object MediaSegmentRelay {
    fun rewrite(raw: String, endpoint: String?): String? {
        if (endpoint.isNullOrBlank()) return null
        return try {
            val u = URI(raw)
            val relay = URI(endpoint)
            if (relay.scheme != "https" || relay.userInfo != null ||
                u.scheme != "https" || u.userInfo != null || (u.port != -1 && u.port != 443) ||
                !Regex("^p(?:16|19)-ad-site-sign-sg\\.tiktokcdn\\.com$").matches(u.host ?: "") ||
                !u.path.startsWith("/ad-site-i18n-sg/") || !u.path.endsWith(".image")) null
            else "$endpoint?url=${URLEncoder.encode(raw, "UTF-8")}"
        } catch (_: Exception) { null }
    }
}
