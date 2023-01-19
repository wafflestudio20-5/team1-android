package com.waffle22.wafflytime.ui.preferences

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.waffle22.wafflytime.databinding.ProfileCardBinding
import com.waffle22.wafflytime.util.AuthStorage

class ProfileCardPreference(context: Context, attrs: AttributeSet?): Preference(context, attrs) {

    private lateinit var binding: ProfileCardBinding
    private val sharedPref =
        context.getSharedPreferences(AuthStorage.SharedPreferenceName, Context.MODE_PRIVATE)

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        binding = ProfileCardBinding.bind(holder.itemView)
        var id = sharedPref.getString(AuthStorage.UserLoginIdKey, "")
        if(id!!.isEmpty()) id = sharedPref.getString(AuthStorage.UserSocialEmailKey, "")
        binding.useridText.text = id
        binding.usernameText.text = sharedPref.getString(AuthStorage.UserNickNameKey, "NULL")
    }
}