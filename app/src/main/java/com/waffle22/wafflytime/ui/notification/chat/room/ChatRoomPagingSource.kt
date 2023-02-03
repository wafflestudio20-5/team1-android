package com.waffle22.wafflytime.ui.notification.chat.room

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.MessageInfo
import retrofit2.HttpException
import java.io.IOException

class ChatRoomPagingSource(
    private val chatId: Long,
    private val wafflyApiService: WafflyApiService
    ) : PagingSource<Long, MessageInfo>() {

    override fun getRefreshKey(state: PagingState<Long, MessageInfo>): Long? {
        return null
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, MessageInfo> {
        try {
            val response = wafflyApiService.getMessagesPaged(
                chatId = chatId,
                cursor = params.key,
                size = params.loadSize.toLong()
            )
            val prevItemId = ((params.key ?: 0) - params.loadSize).let {
                if (it > 0) it
                else null
            }
            return LoadResult.Page(
                data = response.body()!!.contents,
                nextKey = response.body()!!.cursor,
                prevKey = prevItemId,
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}