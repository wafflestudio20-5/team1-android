package com.waffle22.wafflytime.util

data class SlackState<T> (
    val status: String,
    val errorCode: String?,
    val errorMessage: String?,
    val dataHolder: T?
)

//data class StateStorage (
//    val status: String,
//    val errorCode: String?,
//    val errorMessage: String?
//)
//
//data class StateValueStorage (
//    val status: String,
//    val value: String?,
//    val errorCode: String?,
//    val errorMessage: String?
//)