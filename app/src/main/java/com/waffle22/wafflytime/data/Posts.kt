package com.waffle22.wafflytime.data

import android.graphics.Picture
import android.graphics.drawable.Drawable

abstract class Post(
    open val postId: Long,
//    open val profilePic: Drawable,
    open val nickname: String,
    open val userId: String,
    open val time: String,
    open val text: String,
    open val likes: Int,
)

data class Reply(
    override val postId: Long,
//   override val profilePic: Drawable,
    override val nickname: String,
    override val userId: String,
    override val time: String,
    override val text: String,
    override val likes: Int,
//) : Post (profilePic, nickname, userId, time, text, likes)
): Post (postId, nickname, userId, time, text, likes)

data class Comment(
    override val postId: Long,
//    override val profilePic: Drawable,
    override val nickname: String,
    override val userId: String,
    override val time: String,
    override val text: String,
    override val likes: Int,
    val replies: List<Reply>
//) : Post (profilePic, nickname, userId, time, text, likes)
) : Post (postId, nickname, userId, time, text, likes)

data class ThreadPreview(
    override val postId: Long,
    override val nickname: String,
    override val userId: String,
    override val time: String,
    override val text: String,
    override val likes: Int,
    val comment_cnt: Int,
    val tag: String
) : Post (postId, nickname, userId, time, text, likes)

data class WaffThread(
    override val postId: Long,
//    override val profilePic: Drawable,
    override val nickname: String,
    override val userId: String,
    override val time: String,
    override val text: String,
    override val likes: Int,
    val comment_cnt: Int,
    val tag: String,
    val clipped: Int,
    val comments: List<Comment>
//) : Post (profilePic, nickname, userId, time, text, likes)
) : Post (postId, nickname, userId, time, text, likes)

data class Board(
    val id: Long,
    val title: String,
    val description: String,
    val tag: List<String>,
    val default: Boolean,
    val pinned: Boolean
)
