package com.waffle22.wafflytime.ui.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.waffle22.wafflytime.databinding.FragmentChangeEmailBinding

class ChangeEmailFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentChangeEmailBinding.inflate(inflater, container, false).root
    }
}