package com.waffle22.wafflytime.util

import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.dto.ErrorDTO
import com.waffle22.wafflytime.network.dto.TokenContainer
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import java.nio.charset.Charset


fun HttpException.parseError(moshi: Moshi): ErrorDTO? {
    val rawStr = this.response()?.errorBody()?.string()
    try {
        return moshi.adapter(ErrorDTO::class.java).fromJson(rawStr)
    } catch (e: Exception) {
        return null
    }
}

fun Response.parseError(moshi: Moshi): ErrorDTO? {
    val rawStr = getStringFromOkHttp(this)
    try {
        return moshi.adapter(ErrorDTO::class.java).fromJson(rawStr)
    } catch (e: Exception) {
        return null
    }
}

fun Response.parseRefresh(moshi: Moshi): TokenContainer? {
    val rawStr = getStringFromOkHttp(this)
    try {
        return moshi.adapter(TokenContainer::class.java).fromJson(rawStr)
    } catch (e: Exception) {
        return null
    }
}

fun getStringFromOkHttp(response: Response): String {
    val responseBody = response.body!!
    val source = responseBody.source()
    source.request(Long.MAX_VALUE)
    val buffer = source.buffer
    return buffer.clone().readString(Charset.forName("UTF-8"))
}