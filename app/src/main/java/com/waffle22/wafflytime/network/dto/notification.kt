package com.waffle22.wafflytime.network.dto

data class Notification(
    val title: String,
    val content: String,
    val date: String
)

data class Chat(
    val Name: String,
    val content: String,
    val date: String
)
