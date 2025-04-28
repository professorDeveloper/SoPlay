package com.azamovme.soplay.tv.stv.response.channels_ontv

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class File(
    val id: Int,
    val url: String,
    val size: Long,
    // now Jackson can pass `null` (or default) if the JSON omits origin_ext
    val origin_ext: String? = null
)
