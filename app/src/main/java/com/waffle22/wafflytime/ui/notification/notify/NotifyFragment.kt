package com.waffle22.wafflytime.ui.notification.notify

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.FragmentNotificationBinding
import com.waffle22.wafflytime.network.dto.NotificationData
import com.waffle22.wafflytime.ui.notification.BaseNotificationViewModel
import com.waffle22.wafflytime.util.SlackState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NotifyFragment : Fragment() {
    private lateinit var binding : FragmentNotificationBinding
    private val viewModel: NotifyViewModel by sharedViewModel()
    private val baseNotificationViewModel: BaseNotificationViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.refreshNotifications()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.commentRecyclerView
        val adapter = NotifyAdapter { moveToPost(it) }
        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0){
                    recyclerView.scrollToPosition(0)
                }
            }
        })

        lifecycleScope.launch {
            viewModel.notifyState.collect {
                notifyLogic(it, adapter)
            }
        }

        binding.apply {
            commentRecyclerView.adapter = adapter
            commentRecyclerView.layoutManager = LinearLayoutManager(context)

            commentRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!commentRecyclerView.canScrollVertically(1)) {
                        viewModel.getNotifications()
                    }
                }
            })

            swipeRefreshLayout.setOnRefreshListener {
                viewModel.refreshNotifications()
                binding.swipeRefreshLayout.isRefreshing=false
            }
        }
    }

    private fun notifyLogic(state: SlackState<MutableList<NotificationData>>, adapter: NotifyAdapter) {
        when (state.status){
            "0" -> {
                null
            }
            else -> {
                //alertDialog.dismiss()
                when(state.status) {
                    "200" -> {
                        adapter.submitList(state.dataHolder!!.toList())
                    }
                    else -> {
                        Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetNotifyState()
            }
        }
    }

    private fun moveToPost(notificationData: NotificationData) {
        baseNotificationViewModel.setStatePost(notificationData)
    }
}