package com.waffle22.wafflytime.ui.boards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.waffle22.wafflytime.databinding.FragmentNewThreadBinding

class NewPostFragment : Fragment() {
    private lateinit var binding: FragmentNewThreadBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewThreadBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitButton.setOnClickListener{
            findNavController().navigateUp()
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}