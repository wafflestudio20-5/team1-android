package com.waffle22.wafflytime.util

data class SlackState<T> (
    val status: String,
    val errorCode: String?,
    val errorMessage: String?,
    val dataHolder: T?
)