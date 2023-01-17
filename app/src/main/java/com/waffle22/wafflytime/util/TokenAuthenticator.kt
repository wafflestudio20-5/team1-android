package com.waffle22.wafflytime.util

import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit

class TokenAuthenticator(
    private val retrofit: Retrofit
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {

        return response.request.newBuilder()
            .header("Authorization","Bearer ")
            .build()
    }
}