package com.waffle22.wafflytime.util

import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.dto.ErrorDTO
import com.waffle22.wafflytime.network.dto.TimeDTO
import com.waffle22.wafflytime.network.dto.TokenContainer
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import java.nio.charset.Charset
import java.time.LocalDate


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

//연도(필요시)와 날짜까지 표시
fun TimeDTO.timeToString(): String{
    var timeText = this.hour.toString() + ':' + this.minute.toString()
    if (LocalDate.now().monthValue != this.month || LocalDate.now().dayOfMonth != this.day)
        timeText = this.month.toString() + '/' + this.day.toString() + ' ' + timeText
    if (LocalDate.now().year != this.year)
        timeText = this.year.toString() + '/' + timeText
    return timeText
}

// lines 수 혹은 30*lines 글자만큼 잘라서 미리보기용 문자열을 만듦
// lines가 0이면 원 문자열 그대로 반환
fun String.previewText(lines: Int): String{
    if (lines == 0) return this
    var previewText = this.substring(0 until kotlin.math.min(lines*30, this.length))
    var cnt = 0
    for (i in previewText.indices){
        if (previewText[i] == '\n') cnt += 1
        if (cnt == lines){
            previewText = previewText.substring(0 until i)
            break
        }
    }
    return if (previewText != this) "$previewText..." else previewText
}