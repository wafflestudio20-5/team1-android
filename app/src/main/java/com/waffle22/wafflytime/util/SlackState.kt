package com.waffle22.wafflytime.util

data class SlackState<T> (
    var status: String,
    var errorCode: String?,
    var errorMessage: String?,
    val dataHolder: T?
)