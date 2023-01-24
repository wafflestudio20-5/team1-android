package com.waffle22.wafflytime.ui.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.FragmentNotificationBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NotifyFragment : Fragment() {
    private lateinit var binding : FragmentNotificationBinding
    private lateinit var alertDialog: AlertDialog
    private val viewModel: NotifyViewModel by sharedViewModel()

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

        viewModel.getNewNotifications()

        val recyclerView = binding.commentRecyclerView
        val adapter = NotifyAdapter()
        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0){
                    recyclerView.scrollToPosition(0)
                }
            }
        })

        lifecycleScope.launchWhenStarted {
            viewModel.notifyState.collect {
                notifyLogic(it, adapter)
            }
        }

        binding.apply {
            commentRecyclerView.adapter = adapter
            /*
            commentRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!commentRecyclerView.canScrollVertically(1)) {
                        viewModel.getPastNotifications()
                    }
                }
            })
             */
            swipeRefreshLayout.setOnRefreshListener { viewModel.getNewNotifications() }
        }
    }

    private fun notifyLogic(state: NotifyState, adapter: NotifyAdapter) {
        when (state.status){
            "0" -> {
                null
            }
            else -> {
                //alertDialog.dismiss()
                when(state.status) {
                    "200" -> {
                        adapter.submitList(state.notificationDataSet)
                    }
                    else -> {
                        Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetNotifyState()
                binding.swipeRefreshLayout.isRefreshing=false
            }
        }
    }
}