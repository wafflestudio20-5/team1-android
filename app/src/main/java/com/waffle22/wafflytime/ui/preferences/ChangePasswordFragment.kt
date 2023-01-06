package com.waffle22.wafflytime.ui.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.waffle22.wafflytime.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentChangePasswordBinding.inflate(inflater, container, false).root
    }
}