package com.waffle22.wafflytime.ui.notification.chat.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.ChatSimpleInfo
import retrofit2.HttpException
import java.io.IOException

class ChatListPagingSource(
    private val wafflyApiService: WafflyApiService
) : PagingSource<Long, ChatSimpleInfo>() {

    override fun getRefreshKey(state: PagingState<Long, ChatSimpleInfo>): Long? {
        return null
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, ChatSimpleInfo> {
        try {
            val response = wafflyApiService.getChatListPaged(
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