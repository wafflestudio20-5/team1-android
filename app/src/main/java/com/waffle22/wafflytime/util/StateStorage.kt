package com.waffle22.wafflytime.util

data class StateStorage (
    val status: String,
    val errorCode: String?,
    val errorMessage: String?
)

data class StateValueStorage (
    val status: String,
    val value: String?,
    val errorCode: String?,
    val errorMessage: String?
)