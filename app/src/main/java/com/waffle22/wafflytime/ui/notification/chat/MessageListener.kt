package com.waffle22.wafflytime.ui.notification.chat

interface MessageListener {
    fun onConnectSuccess () // successfully connected
    fun onConnectFailed () // connection failed
    fun onClose () // close
    fun onMessage(text: String)
}