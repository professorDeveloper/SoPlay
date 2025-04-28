package com.azamovme.soplay.tv.stv.response

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val category_id: Int,
    val category_name: String,
    val channel_description: String,
    val channel_id: Int,
    val channel_image: String,
    val channel_name: String,
    val channel_type: String,
    val channel_url: String,
    val user_agent: String,
    val video_id: String
)