package com.waffle22.wafflytime.network

import com.squareup.moshi.Json

data class WafflyLogin (
    val id: String, @Json(name = "img_src") val imgSrcUrl: String
)