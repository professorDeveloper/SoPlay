package com.azamovme.soplay.tv.stv.response.channels_ontv

data class ChannelList(
    val current_page: Int?=null,
    val `data`: List<Data>,
    val first_page_url: String?=null,
    val from: Int?=null,
    val last_page: Int?=null,
    val last_page_url: String?=null,
    val links: List<Link>?=null,
    val next_page_url: String?=null,
    val path: String?=null,
    val per_page: Int?=null,
    val prev_page_url: Any?=null,
    val to: Int?=null,
    val total: Int?=null

)