package com.azamovme.soplay.tv.stv.response

import kotlinx.serialization.Serializable

@Serializable
data class RandomTvResponse(
    val count: Int=0,
    val count_total: Int=0,
    val pages: Int=0,
    val posts: List<Post> = arrayListOf(),
    val status: String= ""
){

}