package com.waffle22.wafflytime.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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

        viewModel.getNotifications()

        val adapter = NotifyAdapter()
        lifecycleScope.launchWhenStarted {
            viewModel.notifyState.collect {
                notifyLogic(it, adapter)
            }
        }

        binding.apply {
            commentRecyclerView.adapter = adapter
        }
    }

    fun notifyLogic(state: NotifyState, adapter: NotifyAdapter) {
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
            }
        }
    }
}