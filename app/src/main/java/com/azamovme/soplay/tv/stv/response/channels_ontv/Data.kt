package com.azamovme.soplay.tv.stv.response.channels_ontv

data class Data(
    val analytic: Int?=null,
    val company_id: Any?=null,
    val created_at: String?=null,
    val deleted_at: Any?=null,
    val description: String?=null,
    val `file`: File?=null,
    val file_id: Int?=null,
    val id: Int=-1,
    val kaz: Int?=null,
    val name: String?=null,
    val paid: Int?=null,
    val promotion: Boolean?=null,
    val sort: Int? =null,
    val status: Int?=null,
    val updated_at: String?=null,
    val url_1080: String?=null,
    val url_480: String?=null,
    val url_720: String?=null,
    val viewed: Int?=null
)