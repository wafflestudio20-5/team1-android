package com.waffle22.wafflytime.util

import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.dto.ErrorDTO
import com.waffle22.wafflytime.network.dto.TokenContainer
import okhttp3.Response
import retrofit2.HttpException


fun HttpException.parseError(moshi: Moshi): ErrorDTO? {
    val rawStr = this.response()?.errorBody()?.string()
    try {
        return moshi.adapter(ErrorDTO::class.java).fromJson(rawStr)
    } catch (e: Exception) {
        return null
    }
}

fun Response.parseError(moshi: Moshi): ErrorDTO? {
    val rawStr = this.body?.string()
    try {
        return moshi.adapter(ErrorDTO::class.java).fromJson(rawStr)
    } catch (e: Exception) {
        return null
    }
}

fun Response.parseRefresh(moshi: Moshi): TokenContainer? {
    val rawStr = this.body?.string()
    try {
        return moshi.adapter(TokenContainer::class.java).fromJson(rawStr)
    } catch (e: Exception) {
        return null
    }
}