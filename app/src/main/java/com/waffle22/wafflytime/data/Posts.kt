package com.waffle22.wafflytime.data

import android.graphics.Picture

abstract class Post(
    open val profilePic: Picture,
    open val nickname: String,
    open val userId: String,
    open val time: String,
    open val text: String,
    open val likes: Int
)

data class Reply(
    override val profilePic: Picture,
    override val nickname: String,
    override val userId: String,
    override val time: String,
    override val text: String,
    override val likes: Int,
) : Post (profilePic, nickname, userId, time, text, likes)

data class Comment(
    override val profilePic: Picture,
    override val nickname: String,
    override val userId: String,
    override val time: String,
    override val text: String,
    override val likes: Int,
    val replies: List<Reply>
) : Post (profilePic, nickname, userId, time, text, likes)

data class Thread(
    override val profilePic: Picture,
    override val nickname: String,
    override val userId: String,
    override val time: String,
    override val text: String,
    override val likes: Int,
    val comment_cnt: Int,
    val clipped: Int
) : Post (profilePic, nickname, userId, time, text, likes)
