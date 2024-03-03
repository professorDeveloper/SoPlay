package com.azamovhudstc.soplay.data

import java.io.Serializable

data class Movie(val href: String, val title: String, val image: String, val rating: Int) :
    Serializable
